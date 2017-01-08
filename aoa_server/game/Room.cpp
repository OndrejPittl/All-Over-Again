#include "Room.h"
#include "../communication/MessageSerializer.h"
#include "../core/Logger.h"
#include "GameDifficulty.h"

Room::Room() {
    this->init();
}

void Room::init() {

}

int Room::getID() {
    return this->id;
}

void Room::setID(int id) {
    std::cout << "setting id: " << id << std::endl;
    this->id = id;
    std::cout << "setted id: " << this->id << std::endl;
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

void Room::setActivePlayerID(int activePlayerID) {
    this->activePlayerID = activePlayerID;
}

GameDifficulty Room::getDifficulty() {
    return this->difficulty;
}

void Room::setDifficulty(GameDifficulty difficulty) {
    this->difficulty = difficulty;
}

std::map<int, Player> Room::getPlayers() {
    return this->players;
}

void Room::registerPlayer(Player *p) {
    this->players[p->getID()] = *p;
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

std::queue<int> Room::getPlayerSockets() {
    std::queue<int> q;

    for(auto it = this->players.cbegin(); it != this->players.cend(); ++it) {
        q.push(it->second.getID());
    }

    return q;
}

bool Room::isEmpty() {
    return this->players.empty();
}
