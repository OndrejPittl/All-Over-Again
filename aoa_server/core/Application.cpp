#include <string>
#include <iostream>

#include "Application.h"
#include "../partial/tools.h"
#include "../partial/Indexer.h"
#include "../partial/StringBuilder.h"
#include "../game/Player.h"
#include "../game/GameType.h"
#include "Logger.h"
#include "Developer.h"
#include "../connection/ConnectionManager.h"
#include "../communication/CommunicationManager.h"




Application::Application() {
    this->init();
}

Application::Application(ConnectionManager *conn, CommunicationManager *comm) {
    this->conn = conn;
    this->comm = comm;
    this->init();
}

void Application::init() {
    this->roomIndexer = new Indexer();
    this->game = new Game();
    this->log = new StringBuilder();
    //this->fillMockRooms();
}

void Application::fillMockRooms() {
    int rID1,
        rID2,
        uID1 = 9,
        uID2 = 10,
        uID3 = 11,
        uID4 = 12;

    Room *r1 = this->createNewRoom();
    r1->setGameType(GameType::MULTIPLAYER);
    r1->setDifficulty(GameDifficulty::EXPERT);
    r1->setBoardDimension(BoardDimension::BOARD_NORMAL);

    Room *r2 = this->createNewRoom();
    r2->setGameType(GameType::MULTIPLAYER);
    r2->setDifficulty(GameDifficulty::NORMAL);
    r2->setBoardDimension(BoardDimension::BOARD_HUGE);

    this->registerUser(uID1);
    this->registerUser(uID2);
    this->registerUser(uID3);
    this->registerUser(uID4);

    this->signInUser(uID1, "Marty");
    this->signInUser(uID2, "Dendasda");
    this->signInUser(uID3, "Gabin");
    this->signInUser(uID4, "Olin");

    rID1 = r1->getID();
    rID2 = r2->getID();

    this->assignPlayer(uID1, rID1);
    this->assignPlayer(uID2, rID2);
    this->assignPlayer(uID3, rID2);
    this->assignPlayer(uID4, rID2);

    printRooms(this->rooms);

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

/**
 * Stores a player just with an ID in a collection of online players.
 * @param uid   user id / sock conneciton index
 */
void Application::registerUser(int uid) {
    Player *p = new Player(uid);
    this->storePlayer(p);

    this->log->clear();
    this->log->append("A new player was created and stored at index: ");
    this->log->append(uid);
    this->log->append(".");
    Logger::info(this->log->getString());
}

/**
 * Assigns a username to an existing user at uid in onlineusers collection.
 *
 *
 * @param uid       user id
 * @param username  entered username
 * @return          true - signed in, false - username taken by ANOTHER player
 */
bool Application::signInUser(int uid, std::string username) {

    // condition of merging:
    // user with the username is already in evidence
    // (USERNAME EXISTS) and NOT playing in a room
    // 'cause they are offline (PLAYER OFFLINE)

    // username is already taken
    bool taken,

        // previously signed in player is-online flag
        offIsOnline,

        // are players mergable? == taken && !offIsOnline
        mergable;

    // index of a player in an offline player data structure
    int offIndex;

    // new player
    Player *player;


    Logger::error("signing in");
    printUsernames(this->usernames);
    printRooms(this->rooms);

    // charset check
    if(!validateUsername(username))
        return false;

    taken = !this->checkUsernameAvailability(username);
    offIndex = this->usernames[username];
    offIsOnline = keyExistsInPlayerMap(this->onlinePlayers, offIndex);
    mergable = taken && !offIsOnline;

    std::cout << "taken: " << (taken ? "y" : "n") << ", offIndex: " << offIndex << ", mergable: " << (mergable ? "y" : "n") << ", isOnline: " << (offIsOnline ? "y" : "n") << std::endl;

    // username is not available!
    if(taken && offIsOnline)
        return false;


    // currently signing in player
    player = this->getPlayer(uid);
    player->setUsername(username);

    if(mergable) {
        Logger::info("mergiiiiiiiiing");
        // merging
        this->reconnectUser(player);

        // a game must go on!
        this->reassignPlayer(player);
    }

    this->usernames[username] = uid;

    Logger::info("assigniiiiiiing new uid to username");
    printRooms(this->rooms);
    printUsernames(this->usernames);



    return true;
}



void Application::reassignPlayer(Player *player) {
    Player *prevPlayer = this->getOfflinePlayer(this->usernames[player->getUsername()]);
    Room *r = this->getRoom(player->getRoomID());

    r->reassignPlayer(player, prevPlayer);
}

void Application::reconnectUser(Player *player){
    int offUid;

    Player *offPlayer;

    std::string username = player->getUsername();


    Developer::printOnlineOfflineUsers(this->onlinePlayers, this->offlinePlayers);

    offUid = this->usernames[username];
    offPlayer = this->getOfflinePlayer(offUid);
    player->merge(offPlayer);

    this->offlinePlayers.erase(this->offlinePlayers.begin() + offUid);

    this->log->clear();
    this->log->append("A user ");
    this->log->append(username);
    this->log->append(" was reconnected!");
    Logger::info(this->log->getString());
    Developer::printOnlineOfflineUsers(this->onlinePlayers, this->offlinePlayers);
}

/**
 *  caller: main thread
 *
 *  online -> offline
 *              ->
 *         -> xxx
 *              ->
 *
 */
void Application::deregisterUser(int uid) {

    // online player
    Player *p = this->getPlayer(uid);

    if(p == nullptr)
        return;

    // -- LOG --
    this->log->clear(); this->log->append(" --- deregistering: ");
    this->log->append(uid); Logger::info(this->log->getString());


    // is in room? -> offline users, stays in room marked as offline
    //        not? -> remove from online/room - COMPLETELY


    // playing a game?
    if(p->hasRoom()) {
        Logger::info(" --- deregistering a user with room");

        printRooms(this->rooms);

        int rid = p->getRoomID();
        Room *r = this->getRoom(rid);

        // singleplayer
//        if(r->getGameType() == GameType::SINGLEPLAYER) {
//            //Player *p = this->getPlayer(uid);
//            this->leaveRoomCheckCancel(p);
//            return;
//        }

        // multiplayer
        // online
        this->signOutUser(uid);

        if(r->isAnyoneOnline()) {

            // notify other players
            this->comm->getMsgProcessor()->handleUserGoneOffline(r);

        } else {

            // @TODO: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
            // cancel room ALWAYS -> everybody offline
            this->checkRoomCancel(rid);
        }

        printRooms(this->rooms);

    } else {

        Logger::info(" --- deregistering a user without room");

        // online -> remove
        this->removeOnlineUser(uid);

    }
}

/**
 * online -> offline (has room)
 * @param uid
 */
void Application::signOutUser(int uid) {
    Player *p = this->getPlayer(uid);
    Room *r = this->getRoom(p->getRoomID());

    Developer::printOnlineOfflineUsers(this->onlinePlayers, this->offlinePlayers);

    // mark as offline
    p->setOffline();

    // add to offline
    this->storeOfflinePlayer(p);

    // remove from online
    this->onlinePlayers.erase(uid);

    //remove a user from FD_SET
    this->conn->deregisterClient(uid);

    // room waiting for the player comes back
    // p still in a room, marked as offline
    r->changeStatus(GameStatus::WAITING);

    Developer::printOnlineOfflineUsers(this->onlinePlayers, this->offlinePlayers);

    // -- LOG --
    this->log->clear(); this->log->append("A user "); this->log->append(p->getUsername());
    this->log->append(" was marked as offline."); Logger::info(this->log->getString());
}

/**
 *  online -> xxx
 */
void Application::removeOnlineUser(int uid) {
    Player *player = this->getPlayer(uid);

    if(player == nullptr)
        return;

    Developer::printOnlineOfflineUsers(this->onlinePlayers, this->offlinePlayers);

    this->onlinePlayers.erase(uid);

    //remove a user from FD_SET
    this->conn->deregisterClient(uid);

    // -- LOG --
    this->log->clear(); this->log->append("The online user "); this->log->append(player->getUsername());
    this->log->append(" was completely removed."); Logger::info(this->log->getString());

    Developer::printOnlineOfflineUsers(this->onlinePlayers, this->offlinePlayers);


    // free username if exists
    this->freeUsername(player);
}

/**
 *  offline -> xxx
 */
void Application::removeOfflineUser(int offUid) {
    Player *player = this->getOfflinePlayer(offUid);

    if(player == nullptr)
        return;

    Developer::printOnlineOfflineUsers(this->onlinePlayers, this->offlinePlayers);

    this->offlinePlayers.erase(this->offlinePlayers.begin() + offUid);

    // -- LOG --
    this->log->clear(); this->log->append("The offline user "); this->log->append(player->getUsername());
    this->log->append(" was completely removed."); Logger::info(this->log->getString());

    Developer::printOnlineOfflineUsers(this->onlinePlayers, this->offlinePlayers);


    // free username if exists
    this->freeUsername(player);
}

/**
 * Frees a username if exists.
 */
void Application::freeUsername(Player *player) {
    if(!player->hasUsername())
        return;

    printUsernames(this->usernames);

    // free username
    this->usernames.erase(player->getUsername());

    // -- LOG --
    this->log->clear(); this->log->append("A username "); this->log->append(player->getUsername());
    this->log->append(" was freed."); Logger::info(this->log->getString());

    printUsernames(this->usernames);
}

/**
 *  User leaves the room
 *      & stays online.
 */
void Application::leaveRoom(Player *player) {
    if(!player->hasRoom())
        return;

    printRooms(this->rooms);

    int rid = player->getRoomID();
    Room *room = this->getRoom(rid);

    // reset player's room id
    player->leaveRoom();

    // remove player from room
    room->deregisterPlayer(player);

    // -- LOG --
    this->log->clear(); this->log->append("Player: "); this->log->append(player->getUsername());
    this->log->append(" left room: "); this->log->append(rid); this->log->append(".");
    Logger::info(this->log->getString());

    printRooms(this->rooms);

}

/**
 * DESTROYS ROOM and REMOVES OFFLINE players.
 * usage: only offline players left in the room
 */
void Application::cancelRoom(Room *room){
    PlayerMap players = room->copyPlayers();

    printRooms(this->rooms);

    for(auto it = players.cbegin(); it != players.cend(); ++it) {

        // removes relations player - room
        this->leaveRoom(it->second);

        // removes offline player
        int offlineIndex = this->usernames[it->second->getUsername()];
        this->removeOfflineUser(offlineIndex);
    }

    // cancel a room
    this->rooms.erase(room->getID());

    printRooms(this->rooms);
}

/**
 * DESTROYS ROOM and KICKS OUT ONLINE players.
     * usage: a player WANTED to leave a room
 */
void Application::disbandRoom(Room *room){
    PlayerMap players = room->copyPlayers();

    printRooms(this->rooms);

    for(auto it = players.cbegin(); it != players.cend(); ++it) {

        // removes relations player - room
        this->leaveRoom(it->second);
    }

    // cancel a room
    this->rooms.erase(room->getID());

    printRooms(this->rooms);
}

/**
 * Player leaves room. If all other players are offline, cancel it.
 */
void Application::leaveRoomCheckCancel(Player *player) {
    if(!player->hasRoom())
        return;

    int rid = player->getRoomID();

    // leave room
    this->leaveRoom(player);
    this->checkRoomCancel(rid);
}

/**
 * If nobody online left, cancel the room.
 */
void Application::checkRoomCancel(int rid) {
    Room *r = this->getRoom(rid);

    // somebody alive, calm down!
    if(r->isAnyoneOnline())
        return;

    // nobody online left -> cancel room
    this->cancelRoom(r);

    // -- LOG --
    this->log->clear(); this->log->append("-- Room at: "); this->log->append(rid);
    this->log->append(" is empty or everyone is offline and was CANCELED.");
    Logger::info(this->log->getString());

}






//void Application::deregisterUser_old(int uid) {
//    Player *p = this->getPlayer(uid);
//    if(p == nullptr) p = this->getOfflinePlayer(uid);
//    if(p == nullptr) return;
//
//
//    this->log->clear();
//    this->log->append(" --- deregistering: ");
//    this->log->append(uid);
//    Logger::info(this->log->getString());
//
//    // is in room? -> offline users, stays in room marked as offline
//    //        not? -> remove from online/offline/room - COMPLETELY
//
//    // is online and currently playing a game
//    if(p->isOnline() && p->hasRoom()) {
//        Logger::info(" --- deregistering a user with room");
//
//        int rid = p->getRoomID();
//        Room *r = this->getRoom(rid);
//
//        // online -> offline
//        this->signOutUser(uid);
//
//        if(r->isAnyoneOnline()) {
//            // notify other players
//            this->comm->getMsgProcessor()->handleUserGoneOffline(r);
//
//        } else {
//            // cancel room
//            this->checkRoomCancel(rid);
//        }
//
//    } else {
//
//        Logger::info(" --- deregistering a user without room");
//
//        // online/offline -> remove
//        this->removeUser(p);
//
//    }
//}

//void Application::signOutUser(int uid) {
//    Player *p = this->getPlayer(uid);
//    Room *r = this->getRoom(p->getRoomID());
//
//    Developer::printOnlineOfflineUsers(this->onlinePlayers, this->offlinePlayers);
//
//    // mark as offline
//    p->setOffline();
//
//    // add to offline
//    this->storeOfflinePlayer(p);
//
//    // remove from online
//    this->onlinePlayers.erase(uid);
//
//    //remove a user from FD_SET
//    this->conn->deregisterClient(uid);
//
//    // room waiting for the player comes back
//    // p still in a room, marked as offline
//    r->changeStatus(GameStatus::WAITING);
//
//    Developer::printOnlineOfflineUsers(this->onlinePlayers, this->offlinePlayers);
//
//    this->log->clear();
//    this->log->append("A user ");
//    this->log->append(p->getUsername());
//    this->log->append(" was marked as offline.");
//    Logger::info(this->log->getString());
//
//}


//void Application::removeUser(Player *player) {
//    int uid;
//    std::string username;
//
//    uid = player->getID();
//    username = player->getUsername();
//
//    // remove a user from a room (if joined)
//    this->leaveRoomAndCheck(player);
//
//    this->onlinePlayers.erase(uid);
//
//    //remove a user from FD_SET
//    this->conn->deregisterClient(uid);
//
//    this->log->clear();
//    this->log->append("A user ");
//    this->log->append(username);
//    this->log->append(" was completely removed.");
//    Logger::info(this->log->getString());
//
//
//    if(!player->hasUsername())
//        return;
//
//    // free username
//    this->usernames.erase(username);
//
//    this->log->clear();
//    this->log->append("A username ");
//    this->log->append(username);
//    this->log->append(" was freed.");
//    Logger::info(this->log->getString());
//}


//void Application::leaveRoomAndCheck(Player *player) {
//    if(!player->hasRoom())
//        return;
//
//    int rid = player->getRoomID();
//    Room *room = this->getRoom(rid);
//
//    this->leaveRoom(player);
//    this->disbandRoom(room);
//
//
//}

//void Application::leaveRoom(Player *player) {
//    if(!player->hasRoom())
//        return;
//
//    int rid = player->getRoomID();
//    Room *room = this->getRoom(rid);
//
//    player->leaveRoom();
//    room->deregisterPlayer(player);
//
//    this->log->clear();
//    this->log->append("Player: ");
//    this->log->append(player->getUsername());
//    this->log->append(" left room:");
//    this->log->append(rid);
//    this->log->append(".");
//    Logger::info(this->log->getString());
//
//}

//void Application::disbandRoom(Room *room){
//    //PlayerMap &players = room->getPlayers();
//
//    PlayerMap players = room->copyPlayers();
//
//    for(auto it = players.cbegin(); it != players.cend(); ++it) {
//        Player *p = it->second;
//
//        // remove players from online/offline players & FD_SET
//        this->leaveRoomAndCheck(p);
//    }
//
//    // cancel a room
//    //this->rooms.erase(room->getID());
//}

//void Application::cancelRoom(Room *room){
//    PlayerMap players = room->copyPlayers();
//
//    for(auto it = players.cbegin(); it != players.cend(); ++it) {
//        Player *p = it->second;
//
//        // remove players from online/offline players & FD_SET
//        this->removeUser(p);
//    }
//
//    // cancel a room
//    this->rooms.erase(room->getID());
//}


//void Application::checkRoomCancel(int rid) {
//    Room *r = this->getRoom(rid);
//
//    if(r->isAnyoneOnline())
//        return;
//
//    this->cancelRoom(r);
//
//    this->log->clear();
//    this->log->append("-- Room at: ");
//    this->log->append(rid);
//    this->log->append(" is empty or everyone is offline and was CANCELED.");
//    Logger::info(this->log->getString());
//
//}






RoomMap &Application::getRooms() {
    return this->rooms;
}

Room *Application::createNewRoom() {
    int index = this->roomIndexer->take();
    this->rooms[index] = new Room(index);
    return this->rooms[index];
}

bool Application::joinRoom(int uid, int rid) {
    Player *p = this->getPlayer(uid);
    Room *r = this->getRoom(rid);

    if(r->isJoinable()) {
        this->log->clear();
        this->log->append("* User ");
        this->log->append(p->getUsername());
        this->log->append(" (");
        this->log->append(uid);
        this->log->append(") ");
        this->log->append("joining room: ");
        this->log->append(rid);
        Logger::info(this->log->getString());

        this->assignPlayer(p, r);

        return true;
    }

    return false;
}

void Application::assignPlayer(Player *player, Room *room) {
    player->setRoomID(room->getID());
    room->registerPlayer(player);

    this->log->clear();
    this->log->append("assigning: ");
    this->log->append(player->getUsername());
    this->log->append(" with ID: ");
    this->log->append(player->getID());
    this->log->append(" to room: ");
    this->log->append(room->getID());
    Logger::info(this->log->getString());
}

/**
 * Temp?
 * @param uid
 * @param roomID
 */
void Application::assignPlayer(int uid, int roomID) {
    Room *r = this->getRoom(roomID);
    Player *p = this->getPlayer(uid);
    this->assignPlayer(p, r);
}

Room *Application::getRoom(int rid) {
    return this->rooms[rid];
}

Player *Application::getPlayer(int uid) {
    return this->onlinePlayers.count(uid) ? this->onlinePlayers[uid] : nullptr;
//    return this->onlinePlayers[uid];
}

Player *Application::getOfflinePlayer(int uid) {
    return this->offlinePlayers[uid];
}

void Application::storePlayer(Player *p) {
    this->onlinePlayers[p->getID()] = p;
}

int Application::storeOfflinePlayer(Player *p) {
    int index;

    // push into offline collection
    this->offlinePlayers.push_back(p);

    // index of a user in offline collection
    index = (int) this->offlinePlayers.size() - 1;

    // store new index
    this->usernames[p->getUsername()] = index;

    return index;
}

bool Application::startGameIfReady(Room *room) {
    this->log->clear();

    if(room->isReady()) {
        this->log->append("A room (");
        this->log->append(room->getID());
        this->log->append(") is full. Starting.");
        Logger::info(this->log->getString());
        //room->startTurn();
        return true;
    } else {
        this->log->append("A room (");
        this->log->append(room->getID());
        this->log->append(") is NOT full. NOT starting.");
        Logger::info(this->log->getString());
        return false;
    }
}


bool Application::proceedTurn(int rid, const std::queue<int> &progress) {
    bool result;
    Room *r = this->getRoom(rid);

    result = this->game->validateTurn(progress, r);

    if(result) {
        r->setProgress(progress);
    } else {
        r->finishGame();
    }

    return result;
}


void Application::registerSuspiciousBehaviour(int uid){
    Player *p;

    p = this->getPlayer(uid);

    if(p == nullptr)
        return;

    p->registerIncorrectMsgCount();

    this->log->clear();
    this->log->append("Suspicious/invalid messages detected from client ");
    this->log->append(uid);
    this->log->append(" (");
    this->log->append(p->getIncorrectMsgCount());
    this->log->append("/");
    this->log->append(ConnectionManager::COMM_INVALID_MSG_LIMIT);
    this->log->append(").");
    Logger::warning(this->log->getString());

    if(p->getIncorrectMsgCount() == ConnectionManager::COMM_INVALID_MSG_LIMIT) {
        this->suspiciousClients.push_back(uid);
    }
}

void Application::handleSuspiciousClients(){
    while(this->suspiciousClients.size() > 0) {
        int c = this->suspiciousClients.back();

        //odstrihneme
        this->log->clear();
        this->log->append("Client ");
        this->log->append(c);
        this->log->append(" was marked as an attacker.");
        Logger::warning(this->log->getString());

        this->deregisterUser(c);
        this->suspiciousClients.pop_back();
    }
}



