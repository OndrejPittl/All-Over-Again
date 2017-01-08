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

//    StringBuilder *sss = new StringBuilder();
//
//    sss->clear();
//    sss->append("rid1: ");
//    sss->append(rID1);
//    sss->append("\n");
//    sss->append("rid2: ");
//    sss->append(rID2);
//    sss->append("\n");
//
//    Logger::info(sss->getString());

    MessageSerializer *ser = new MessageSerializer();

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

    this->registerUser(uID1, "Marty");
    this->registerUser(uID2, "dendasda");
    this->registerUser(uID3, "gabin");

    rID1 = r1->getID();
    rID2 = r2->getID();
    this->assignPlayer(uID1, rID1);
    this->assignPlayer(uID2, rID2);
    this->assignPlayer(uID3, rID2);

    printRooms(this->rooms);

    std::string roomStr = ser->serializeRooms(this->rooms);
    Logger::info(roomStr);

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
    this->onlineUsers[uid] = *p;
    return true;
}

/**
 * Deregister user & check any user left.
 * @param uid
 */
void Application::deregisterOnlineUser(int uid) {
    Player p;

    p = this->onlineUsers[uid];

    this->deregisterUserFrom(p, this->onlineUsers);
    this->offlineUsers[uid] = p;


    this->log->clear();
    this->log->append("A user ");
    this->log->append(p.getUsername());
    this->log->append(" was marked as offline.");
    Logger::info(this->log->getString());
}

void Application::deregisterOfflineUser(int uid) {
    Player p;

    p = this->offlineUsers[uid];

    // remove a user from OFFLINE
    this->deregisterUserFrom(p, this->offlineUsers);

    //remove a user from FD_SET
    this->conn->deregisterClient(uid);


    this->log->clear();
    this->log->append("A user ");
    this->log->append(p.getUsername());
    this->log->append(" was completely removed.");
    Logger::info(this->log->getString());
}

void Application::deregisterUserFrom(Player& p, std::map<int, Player>& users) {
    Room r;
    int uid,
        rid;

    uid = p.getRoomID();

    Developer::printOnlineOfflineUser(this->onlineUsers, this->offlineUsers);
    users.erase(uid);
    Developer::printOnlineOfflineUser(this->onlineUsers, this->offlineUsers);


    if(!p.hasRoom())
        return;

    rid = p.getRoomID();

    // end room
    this->cancelRoomIfEmpty(rid);
}


std::map<int, Room> Application::getRooms() {
    return this->rooms;
}

int Application::createNewRoom(Room *room) {
    int index = this->getFreeRoomIndex();
    room->setID(index);
    this->setRoom(*room);
    return index;
}

bool Application::joinRoom(int uid, int rid) {
    Room r;
    Player p;

    p = this->getPlayer(uid);
    r = this->getRoom(rid);

    this->log->clear();
    this->log->append("*** user ");
    this->log->append(p.getUsername());
    this->log->append(" (");
    this->log->append(uid);
    this->log->append("|");
    this->log->append(p.getID());
    this->log->append(") ");
    this->log->append("joining room: ");
    this->log->append(rid);
    this->log->append("|");
    this->log->append(r.getID());
    this->log->append("\n");
    Logger::info(this->log->getString());

    if(r.isJoinable()) {
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
    Room r;
    Player p;

    r = this->getRoom(roomID);
    p = this->onlineUsers[uid];

    std::cout << "assigning: " << p.getUsername() << " with id: " << p.getID() << " to room: " << r.getID() << std::endl;

    p.setRoomID(roomID);
    r.registerPlayer(&p);
    this->setRoom(r);
}

Room& Application::getRoom(int rid) {
    return this->rooms[rid];
}

void Application::setRoom(Room& r) {
    this->rooms[r.getID()] = r;
}

void Application::startGameIfReady(int rid) {
    Room r;

    r = this->getRoom(rid);

    if(!r.isRoomReady())
        return;


}

bool Application::isGameReady(int rid) {
    return this->getRoom(rid).isRoomReady();
}

Player Application::getPlayer(int uid) {
    return this->onlineUsers[uid];
}

void Application::cancelRoomIfEmpty(int rid) {
    Room r;


    r = this->getRoom(rid);

    if(!r.isEmpty())
        return;

    // odstranit místnost z kolekce
    // odstranit hráče z offline kolekce
    //                 FD_SETu
}

bool Application::proceedTurn(int rid, std::queue<int> &progress) {
    Room r;
    bool result;

    r = this->getRoom(rid);
    result = this->game->validateTurn(progress, r);


    return false;
}







