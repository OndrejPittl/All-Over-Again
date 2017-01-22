#include <string>
#include <iostream>

#include "Application.h"
#include "../partial/tools.h"
#include "Logger.h"
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
    this->offlinePlayerIndexer = new Indexer();
    this->game = new Game();
    this->log = new StringBuilder();
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

    Tools::printRooms(this->rooms);

}

bool Application::checkUsernameAvailability(std::string username) {

    //iterator
    typedef std::map<std::string, int>::iterator it_type;

    // iteration through a map of usernames
    for(it_type iterator = this->usernames.begin(); iterator != this->usernames.end(); iterator++) {
        std::string uName = iterator->first;

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

    Tools::printOnlineOfflineUsers(this->onlinePlayers, this->offlinePlayers);

    Player *p = new Player(uid);
    this->storePlayer(p);

    Tools::printOnlineOfflineUsers(this->onlinePlayers, this->offlinePlayers);

    // -- log --
    this->log->clear(); this->log->append("A new player was created and stored at index: ");
    this->log->append(uid); this->log->append("."); Logger::info(this->log->getString());
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


    // charset check
    if(!Tools::validateUsername(username))
        return false;

    taken = !this->checkUsernameAvailability(username);
    offIndex = this->usernames[username];
    offIsOnline = Tools::keyExistsInPlayerMap(this->onlinePlayers, offIndex);
    mergable = taken && !offIsOnline;


    // -- log --
    this->log->clear(); this->log->append("taken: "); this->log->append(taken ? "y" : "n");
    this->log->append(", offIndex: "); this->log->append(offIndex); this->log->append(", mergable: ");
    this->log->append(mergable ? "y" : "n"); this->log->append(", isOnline: ");
    this->log->append(offIsOnline ? "y" : "n"); Logger::debug(this->log->getString());


    // username is not available!
    if(taken && offIsOnline)
        return false;


    // currently signing in player
    player = this->getPlayer(uid);
    player->setUsername(username);

    if(mergable) {
        this->log->clear(); this->log->append("Merging a player: ");
        this->log->append(username); Logger::info(this->log->getString());


        // a game must go on!
        this->reassignPlayer(player);
    }

    this->usernames[username] = uid;

    Tools::printUsernames(this->usernames);
    Logger::debug("Assigning new uid to username.");


    Tools::printOnlineOfflineUsers(this->onlinePlayers, this->offlinePlayers);

    // -- log --
    this->log->clear(); this->log->append("Username assigned to a user: ");
    this->log->append(uid); this->log->append("."); Logger::info(this->log->getString());
    return true;
}



void Application::reassignPlayer(Player *player) {
    int prevPlayerUid;

    std::string username;

    Player *prevPlayer;

    Room *room;


    username = player->getUsername();
    prevPlayerUid = this->usernames[username];
    prevPlayer = this->getOfflinePlayer(prevPlayerUid);
    room = this->getRoom(prevPlayer->getRoomID());

    player->merge(prevPlayer);
    this->removeOfflineUser(prevPlayerUid);

    if(room != nullptr) {
        room->reassignPlayer(player, prevPlayer);
    } else {
        // room no longer exists
        player->leaveRoom();
    }

    // -- log --
    this->log->clear(); this->log->append("A user "); this->log->append(username);
    this->log->append(" was reconnected!"); Logger::debug(this->log->getString());
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
    Logger::error("deregistering user");


    // online player
        Player *p = this->getPlayer(uid);

    if(p == nullptr)
        return;

    // -- LOG --
    this->log->clear(); this->log->append(" --- deregistering: ");
    this->log->append(uid); Logger::debug(this->log->getString());


    // is in room? -> offline users, stays in room marked as offline
    //        not? -> remove from online/room - COMPLETELY


    // playing a game?
    if(p->hasRoom()) {
        Logger::info(" --- deregistering a user with room");

        int rid = p->getRoomID();
        Room *r = this->getRoom(rid);

        // multiplayer
        // online
        this->signOutUser(uid);

        if(r->isAnyoneOnline()) {

            // notify other players
            this->comm->getMsgProcessor()->handleUserGoneOffline(r);

        } else {

            // cancel room ALWAYS -> everybody offline
            this->checkRoomCancel(rid);
        }

        Tools::printRooms(this->rooms);

    } else {

        Logger::debug(" --- deregistering a user without room");

        // online -> remove
        this->removeOnlineUser(uid);

    }
}

/**
 * online -> offline (has room)
 * @param uid
 */
void Application::signOutUser(int uid) {
    Logger::error("signing out user");

    Player *p = this->getPlayer(uid);
    Room *r = this->getRoom(p->getRoomID());

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


    Tools::printUsernames(this->usernames);
    Tools::printOnlineOfflineUsers(this->onlinePlayers, this->offlinePlayers);
    Tools::printRooms(this->rooms);


    // -- LOG --
    this->log->clear(); this->log->append("A user "); this->log->append(p->getUsername());
    this->log->append(" was marked as offline."); Logger::info(this->log->getString());
}

/**
 *  online -> xxx
 */
void Application::removeOnlineUser(int uid) {
    Logger::error("removing online user");

    Player *player = this->getPlayer(uid);

    if(player == nullptr)
        return;

    this->onlinePlayers.erase(uid);

    //remove a user from FD_SET
    this->conn->deregisterClient(uid);

    Tools::printOnlineOfflineUsers(this->onlinePlayers, this->offlinePlayers);

    // -- LOG --
    this->log->clear(); this->log->append("The online user "); this->log->append(player->getUsername());
    this->log->append(" was completely removed."); Logger::info(this->log->getString());

    // free username if exists
    this->freeUsername(player);
}

/**
 *  offline -> xxx
 */
void Application::removeOfflineUser(int offUid) {
    Logger::error("removing offline user");

    Player *player = this->getOfflinePlayer(offUid);

    if(player == nullptr)
        return;

    this->offlinePlayers.erase(offUid);
    this->offlinePlayerIndexer->free(offUid);

    // -- LOG --
    this->log->clear(); this->log->append("The offline user "); this->log->append(player->getUsername());
    this->log->append(" was completely removed."); Logger::info(this->log->getString());

    // free username if exists
    this->freeUsername(player);
}

/**
 * Frees a username if exists.
 */
void Application::freeUsername(Player *player) {
    Logger::error("freeing username");

    if(!player->hasUsername())
        return;

    // free username
    this->usernames.erase(player->getUsername());

    // -- LOG --
    this->log->clear(); this->log->append("A username "); this->log->append(player->getUsername());
    this->log->append(" was freed."); Logger::info(this->log->getString());

    Tools::printUsernames(this->usernames);
}

/**
 *  User leaves the room
 *      & stays online.
 */
void Application::leaveRoom(Player *player) {
    Logger::error("user leaving room");

    if(!player->hasRoom())
        return;

    int rid = player->getRoomID();
    Room *room = this->getRoom(rid);

    // reset player's room id
    player->leaveRoom();

    // remove player from room
    room->deregisterPlayer(player);

    Tools::printOnlineOfflineUsers(this->onlinePlayers, this->offlinePlayers);
    Tools::printRooms(this->rooms);

    // -- LOG --
    this->log->clear(); this->log->append("A player: "); this->log->append(player->getUsername());
    this->log->append(" ("); this->log->append(player->getID()); this->log->append(") ");
    this->log->append("has left the room ("); this->log->append(rid); this->log->append(").");
    Logger::info(this->log->getString());
}

/**
 * DESTROYS ROOM and REMOVES OFFLINE players.
 * usage: only offline players left in the room
 */
void Application::cancelRoom(Room *room){
    Logger::error("cancelling room");

    PlayerMap players = room->copyPlayers();

    for(auto it = players.cbegin(); it != players.cend(); ++it) {

        // removes relations player - room
        this->leaveRoom(it->second);

        // removes offline player
        int offlineIndex = this->usernames[it->second->getUsername()];
        this->removeOfflineUser(offlineIndex);
    }

    // destroy the room
    this->removeRoom(room);
}

/**
 * DESTROYS ROOM and KICKS OUT ONLINE players.
     * usage: a player WANTED to leave a room
 */
void Application::disbandRoom(Room *room){
    Logger::error("disbanding room");

    PlayerMap players = room->copyPlayers();

    for(auto it = players.cbegin(); it != players.cend(); ++it) {

        // removes relations player - room
        //this->leaveRoom(this->getPlayer(it->second->getID()));
        this->leaveRoom(it->second);

        if(!it->second->isOnline()) {
            this->removeOfflineUser(this->usernames[it->second->getUsername()]);
        }

    }

    // destroy the room
    this->removeRoom(room);
}

/**
 * Player leaves room. If all other players are offline, cancel it.
 */
void Application::leaveRoomCheckCancel(Player *player) {
    Logger::error("leave&checking room");

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
    Logger::error("cancel-checking room");

    Room *r = this->getRoom(rid);

    // somebody alive, calm down!
    if(r->isAnyoneOnline())
        return;

    // nobody online left -> cancel room
    this->cancelRoom(r);

    Tools::printOnlineOfflineUsers(this->onlinePlayers, this->offlinePlayers);
    Tools::printRooms(this->rooms);

    // -- LOG --
    this->log->clear(); this->log->append("-- Room ("); this->log->append(rid);
    this->log->append(") is empty or everybody is offline. The room was CANCELED.");
    Logger::info(this->log->getString());
}

void Application::removeRoom(Room *room) {
    this->rooms.erase(room->getID());
    this->roomIndexer->free(room->getID());

    Tools::printRooms(this->rooms);

    this->log->clear();
    this->log->append("The room (");
    this->log->append(room->getID());
    this->log->append(") was destroyed.");
}

RoomMap &Application::getRooms() {
    return this->rooms;
}

Room *Application::createNewRoom() {
    int index = this->roomIndexer->take();
    this->rooms[index] = new Room(index);
    Tools::printRooms(this->rooms);
    return this->rooms[index];
}

bool Application::joinRoom(int uid, int rid) {
    Player *p = this->getPlayer(uid);
    Room *r = this->getRoom(rid);

    if(r == nullptr || !r->isJoinable()) {
        return false;
    }

    // -- log --
    this->log->clear(); this->log->append("User "); this->log->append(p->getUsername()); this->log->append(" (");
    this->log->append(uid); this->log->append(") "); this->log->append("joining room ("); this->log->append(rid);
    this->log->append(")."); Logger::info(this->log->getString());

    this->assignPlayer(p, r);
    return true;
}

void Application::assignPlayer(Player *player, Room *room) {
    player->setRoomID(room->getID());
    room->registerPlayer(player); Tools::printRooms(this->rooms);

    // -- log --
    this->log->clear(); this->log->append("Player ");
    this->log->append(player->getUsername()); this->log->append(" (");
    this->log->append(player->getID()); this->log->append(") was assigned to room (");
    this->log->append(room->getID()); this->log->append(")."); Logger::debug(this->log->getString());
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
    return this->rooms.count(rid) ? this->rooms[rid] : nullptr;
}

Player *Application::getPlayer(int uid) {
    return this->onlinePlayers.count(uid) ? this->onlinePlayers[uid] : nullptr;
}

Player *Application::getOfflinePlayer(int uid) {
    return this->offlinePlayers.count(uid) ? this->offlinePlayers[uid] : nullptr;
}

void Application::storePlayer(Player *p) {
    this->onlinePlayers[p->getID()] = p;
}

int Application::storeOfflinePlayer(Player *p) {
    int index = this->offlinePlayerIndexer->take();

    // push into offline collection
    this->offlinePlayers[index] = p;

    // store new index
    this->usernames[p->getUsername()] = index;

    return index;
}

bool Application::startGameIfReady(Room *room) {
    this->log->clear();

    if(room->isReady()) {
        this->log->append("A room ("); this->log->append(room->getID());
        this->log->append(") is full. Starting."); Logger::info(this->log->getString());
        return true;
    } else {
        this->log->append("A room ("); this->log->append(room->getID());
        this->log->append(") is NOT full. NOT starting."); Logger::info(this->log->getString());
        return false;
    }
}

bool Application::proceedTurn(int rid, const std::queue<int> &progress) {
    Room *r = this->getRoom(rid);
    bool result = this->game->validateTurn(progress, r);

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

    this->log->clear(); this->log->append("Suspicious/invalid messages detected from client ");
    this->log->append(uid); this->log->append(" ("); this->log->append(p->getIncorrectMsgCount());
    this->log->append("/"); this->log->append(ConnectionManager::COMM_INVALID_MSG_LIMIT);
    this->log->append(")."); Logger::warning(this->log->getString());

    if(p->getIncorrectMsgCount() == ConnectionManager::COMM_INVALID_MSG_LIMIT) {
        this->suspiciousClients.push_back(uid);
    }
}

void Application::handleSuspiciousClients(){
    while(this->suspiciousClients.size() > 0) {
        int c = this->suspiciousClients.back();

        // cut off!
        this->log->clear(); this->log->append("Client "); this->log->append(c);
        this->log->append(" was marked as an attacker and will be cut off.");
        Logger::warning(this->log->getString());

        this->deregisterUser(c);
        this->suspiciousClients.pop_back();
    }
}


