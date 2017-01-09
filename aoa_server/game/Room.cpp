#include "Room.h"
#include "../communication/MessageSerializer.h"
#include "../core/Logger.h"
#include "GameDifficulty.h"

Room::Room() {
    this->init();
}

void Room::init() {
    this->turn = 0;
    this->winnerID = -1;
}

int Room::getID() {
    return this->id;
}

void Room::setID(int id) {
    this->id = id;
}

//int Room::getPlayerLimit() {
//    return this->playerLimit;
//}
//
//void Room::setPlayerLimit(int playerLimit) {
//    Room::playerLimit = playerLimit;
//}

int Room::getPlayerCount() {
    //return this->playerCount;
    return (int) this->players.size();
}

//void Room::setPlayerCount(int playerCount) {
//    this->playerCount = playerCount;
//}

BoardDimension Room::getBoardDimension() {
    return this->boardDimension;
}

void Room::setBoardDimension(BoardDimension boardDimension) {
    this->boardDimension = boardDimension;
}

int Room::getActivePlayerID() {
    return this->activePlayerID;
}

void Room::updateActivePlayerID() {
    this->activePlayerID = this->turn % this->getPlayerCount();
}

GameDifficulty Room::getDifficulty() {
    return this->difficulty;
}

void Room::setDifficulty(GameDifficulty difficulty) {
    this->difficulty = difficulty;
}

std::map<int, Player> &Room::getPlayers() {
    return this->players;
}

void Room::registerPlayer(Player *p) {
    this->players[p->getID()] = *p;
    this->playerOrder.push_back(p->getID());
}

GameType Room::getGameType() {
    return this->type;
}

void Room::setGameType(GameType type) {
    this->type = type;
}

Room::Room(GameType type, GameDifficulty difficulty, BoardDimension dimension) {
    this->init();
    this->type = type;
    this->difficulty = difficulty;
    this->boardDimension = dimension;
}

bool Room::isRoomReady() {
    return this->getPlayerCount() == (int) this->getGameType();
}

bool Room::isJoinable() {
    return !this->isRoomReady();
}

std::queue<int> Room::getPlayerSockets() const {
    std::queue<int> q;

    for(auto it = this->players.cbegin(); it != this->players.cend(); ++it) {
        q.push(it->second.getID());
    }

    return q;
}

bool Room::isEmpty() {
    return this->players.empty();
}

const std::queue<int> &Room::getProgress() const {
    return this->progress;
}

void Room::setProgress(std::queue<int> progress) {
    this->progress = progress;
}

bool Room::hasProgress() {
    return this->progress.size() > 0;
}

void Room::startTurn() {
    this->updateActivePlayerID();
    this->turn++;
}

int Room::getTurn() const {
    return this->turn;
}

void Room::deregisterPlayer(Player &p) {
    this->players.erase(p.getID());
}

bool Room::isAnyoneOnline() const {
    int onlinePlayers = 0;

//    if(this->isEmpty())
//        return false;

    for(auto it = this->players.cbegin(); it != this->players.cend(); ++it) {
        if(it->second.isOnline())
            onlinePlayers++;
    }

    return onlinePlayers > 0;
}

int Room::getWinnerID() {
    return this->winnerID;
}

void Room::finishGame() {
    this->winnerID = (this->turn + 1) % this->getPlayerCount();
}

bool Room::hasGameFinished() {
    return this->winnerID != -1;
}
