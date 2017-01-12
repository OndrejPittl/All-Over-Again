#include <string>
#include <iostream>

#include "Application.h"
#include "../partial/tools.h"
#include "../partial/StringBuilder.h"
#include "../game/Player.h"
#include "../game/GameType.h"
#include "Logger.h"
#include "Developer.h"
#include "../connection/ConnectionManager.h"
#include "../communication/CommunicationManager.h"


const std::string Application::USERNAME_VALIDATION_REGEX = "^[a-zA-Z0-9-_<>]{3,15}$";

Application::Application() {
    this->init();
}

Application::Application(ConnectionManager *conn, CommunicationManager *comm) {
    this->conn = conn;
    this->comm = comm;
    this->init();
}

void Application::init() {
    this->roomIndex = 0;
    this->game = new Game();

    this->log = new StringBuilder();

    // type ; r-id ; p-count ; p-limit ; diff ; dim ; nicks
    //sbMsg->append("2;1;1;2;1;3;marty;2;2;3;2;5;dendasda:gabin");

    int rID1,
        rID2,
        uID1 = 9,
        uID2 = 10,
        uID3 = 11;

    Room *r1 = new Room();
    r1->setGameType(GameType::MULTIPLAYER);
    r1->setDifficulty(GameDifficulty::EXPERT);
    r1->setBoardDimension(BoardDimension::NORMAL);
    this->createNewRoom(r1);

    Room *r2 = new Room();
    r2->setGameType(GameType::MULTIPLAYER);
    r2->setDifficulty(GameDifficulty::EXPERT);
    r2->setBoardDimension(BoardDimension::HUGE);
    this->createNewRoom(r2);

    this->registerUser(uID1, "Franta");
    this->registerUser(uID2, "Marie");
    this->registerUser(uID3, "Jana");

    rID1 = r1->getID();
    rID2 = r2->getID();
    this->assignPlayer(uID1, rID1);
    this->assignPlayer(uID2, rID2);
    this->assignPlayer(uID3, rID2);

    printRooms(this->rooms);

}

bool Application::validateUsername(std::string username) {
    return validate(username, Application::USERNAME_VALIDATION_REGEX);
}

bool Application::checkUsernameAvailability(std::string username) {
    //iterator
    typedef std::map<std::string, int>::iterator it_type;

    // iteration through a map of usernames
    for(it_type iterator = this->usernames.begin(); iterator != this->usernames.end(); iterator++) {
        std::string uName = iterator->first;
        int id = iterator->second;

        // found -> not available
        if(!uName.compare(username))
            return false;
    }

    // available
    return true;
}

bool Application::registerUser(int uid, std::string username) {

    // charset check
    if(!this->validateUsername(username))
        return false;


    // is already assigned in a room?
    // ...


    // availability check
    if(!this->checkUsernameAvailability(username))
        return false;

    Player *p = new Player(uid, username);
    this->usernames[username] = uid;

    this->setOnlinePlayer(*p);
    //this->onlineUsers[uid] = *p;
    return true;
}


void Application::deregisterUser(int uid) {
    Player &p = this->getOnlinePlayer(uid);

    // is in room? -> offline users, stays in room marked as offline
    //        not? -> remove from online/offline/room - COMPLETELY

    if(p.hasRoom()) {
        Logger::info(" --- deregistering a user with room");

        int rid = p.getRoomID();

        // online -> offline
        this->deregisterOnlineUser(uid);

        // check room cancellation
        this->checkRoomCancel(rid);

    } else {

        Logger::info(" --- deregistering a user without room");

        // online/offline -> remove
        this->deregisterUserCompletely(p);

    }

    this->conn->deregisterClient(uid);
}

/**
 * Deregister user & check any user left.
 * @param uid
 */
void Application::deregisterOnlineUser(int uid) {
    Player &p = this->getOnlinePlayer(uid);
    Room &r = this->getRoom(p.getRoomID());

    Developer::printOnlineOfflineUser(this->onlineUsers, this->offlineUsers);

    // mark as offline
    p.setOffline();

    // add to offline
    this->setOfflinePlayer(p);

    // remove from online
    this->deregisterUserFrom(p, this->onlineUsers);

    // room waiting for the player comes back
    r.changeStatus(GameStatus::WAITING);

    // p still in a room, marked as offline

    Developer::printOnlineOfflineUser(this->onlineUsers, this->offlineUsers);

    this->log->clear();
    this->log->append("A user ");
    this->log->append(p.getUsername());
    this->log->append(" was marked as offline.");
    Logger::info(this->log->getString());

}

void Application::deregisterUserCompletely(Player &player) {
    int uid = player.getID();

    // remove a user from a room (if joined)
    // this->deregisterUserFromRoom(player);

    // remove a user from OFFLINE, ONLINE
    this->deregisterUserFrom(player, this->offlineUsers);
    this->deregisterUserFrom(player, this->onlineUsers);

    //remove a user from FD_SET
    this->conn->deregisterClient(uid);

    this->log->clear();
    this->log->append("A user ");
    this->log->append(player.getUsername());
    this->log->append(" was completely removed.");
    Logger::info(this->log->getString());
}

void Application::deregisterUserFrom(Player& p, std::map<int, Player>& users) {
    int uid = p.getID();
    users.erase(uid);
}

void Application::deregisterUserFromRoom(Player &player) {
    if(!player.hasRoom())
        return;

    int rid = player.getRoomID();
    this->log->clear();
    this->log->append("DEREGISTERED PLAYER WAS IN ROOM: ");
    this->log->append(rid);
    Logger::info(this->log->getString());

    Room &r = this->getRoom(rid);
    r.deregisterPlayer(player);
    this->checkRoomCancel(rid);
}


std::map<int, Room> Application::getRooms() {
    return this->rooms;
}

int Application::createNewRoom(Room *room) {
    int index = this->getFreeRoomIndex();

    this->log->clear();
    this->log->append("* Creating a new room at: ");
    this->log->append(index);
    this->log->append(".");;
    Logger::info(this->log->getString());


    room->setID(index);
    room->changeStatus(GameStatus::CONNECTING);

    this->setRoom(*room);
    return index;
}

bool Application::joinRoom(int uid, int rid) {
    Player &p = this->getOnlinePlayer(uid);
    Room &r = this->getRoom(rid);

    if(r.isJoinable()) {
        this->log->clear();
        this->log->append("* User ");
        this->log->append(p.getUsername());
        this->log->append(" (");
        this->log->append(uid);
        this->log->append(") ");
        this->log->append("joining room: ");
        this->log->append(rid);
        Logger::info(this->log->getString());

        this->assignPlayer(uid, rid);

        return true;
    }

    return false;
}

int Application::getFreeRoomIndex() {
    int index;

    if(!this->freedRoomIndexeQueue.empty()){
        index = this->freedRoomIndexeQueue.front();
        this->freedRoomIndexeQueue.pop();
    } else {
        index = this->roomIndex++;
    }

    return index;
}

void Application::assignPlayer(int uid, int roomID) {
    Room &r = this->getRoom(roomID);
    Player &p = this->getOnlinePlayer(uid);

    this->log->clear();
    this->log->append("assigning: ");
    this->log->append(p.getUsername());
    this->log->append(" with id: ");
    this->log->append(p.getID());
    this->log->append(" to room: ");
    this->log->append(r.getID());
    Logger::info(this->log->getString());

    p.setRoomID(roomID);
    r.registerPlayer(&p);
}

Room& Application::getRoom(int rid) {
    return this->rooms[rid];
}

void Application::setRoom(Room& r) {
    this->rooms[r.getID()] = r;
}

void Application::cancelRoom(int rid) {
    this->rooms.erase(rid);
}


Player& Application::getOnlinePlayer(int uid) {
    return this->onlineUsers[uid];
}

Player& Application::getOfflinePlayer(int uid) {
    return this->offlineUsers[uid];
}

void Application::setOnlinePlayer(Player &p) {
    this->onlineUsers[p.getID()] = p;
}

void Application::setOfflinePlayer(Player &p) {
    this->offlineUsers[p.getID()] = p;
}


bool Application::startGameIfReady(int rid) {
    Room &r = this->getRoom(rid);

    this->log->clear();

    if(r.isReady()) {
        this->log->append("A room (");
        this->log->append(rid);
        this->log->append(") is full. Starting.");
        Logger::info(this->log->getString());
        r.startTurn();
        return true;
    } else {
        this->log->append("A room (");
        this->log->append(rid);
        this->log->append(") is NOT full. NOT starting.");
        Logger::info(this->log->getString());
        return false;
    }
}

Player Application::getPlayer(int uid) {
    return this->onlineUsers[uid];
}

void Application::removePlayer(int uid) {
    this->onlineUsers.erase(uid);
}

void Application::checkRoomCancel(int rid) {
    Room &r = this->getRoom(rid);

    if(r.isAnyoneOnline())
        return;

    std::map<int, Player> &m = r.getPlayers();

    for(auto it = m.cbegin(); it != m.cend(); ++it) {
        Player p = it->second;

        // remove players from online/offline players & FD_SET
        this->deregisterUserCompletely(p);
    }

    this->log->clear();
    this->log->append("-- Room at: ");
    this->log->append(rid);
    this->log->append(" is empty or everyone is offline and was CANCELED.");;
    Logger::info(this->log->getString());

    // cancel a room
    this->cancelRoom(rid);

}

bool Application::proceedTurn(int rid, const std::queue<int> &progress) {
    bool result;
    Room &r = this->getRoom(rid);

    result = this->game->validateTurn(progress, r);

    if(result) {
        r.setProgress(progress);
    } else {
        r.finishGame();
    }

    return result;
}











