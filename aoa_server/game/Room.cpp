#include "Room.h"
#include "../communication/MessageSerializer.h"
#include "../core/Logger.h"
#include "GameDifficulty.h"
#include "GameStatus.h"
#include "Game.h"

Room::Room() {
    this->init();
}

Room::Room(GameType type, GameDifficulty difficulty, bd::BoardDimension dimension) {
    this->init();
    this->type = type;
    this->difficulty = difficulty;
    this->boardDimension = dimension;
}

void Room::init() {
    this->turn = 0;
    this->winnerID = -1;
    this->activePlayerIndex = 0;
    this->status = GameStatus::CONNECTING;
}

void Room::restart() {
    this->turn = 0;
    this->winnerID = -1;
    this->progress = std::queue<int>();
}

int Room::getID() {
    return this->id;
}

void Room::setID(int id) {
    this->id = id;
}

int Room::getPlayerCount() {
    //return this->playerCount;
    return (int) this->players.size();
}

bd::BoardDimension Room::getBoardDimension() {
    return this->boardDimension;
}

void Room::setBoardDimension(bd::BoardDimension boardDimension) {
    this->boardDimension = boardDimension;
}

int Room::getActivePlayerID() {
    return this->players[this->playerOrder[this->activePlayerIndex]].getID();
}

void Room::updateActivePlayer() {
//    std::string str = "";
//    str.append("==================================== apID: ");
//    str.append(std::to_string(this->activePlayerIndex));
//    str.append(" real ID: ");
//    str.append(std::to_string(this->getActivePlayerID()));
//    str.append("\n ");

    this->activePlayerIndex = this->getPlayerCount() > 0 ? this->turn % this->getPlayerCount() : 0;

//    str.append("==== apID: ");
//    str.append(std::to_string(this->activePlayerIndex));
//    str.append(" real ID: ");
//    str.append(std::to_string(this->getActivePlayerID()));
//    Logger::info(str);
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

    if(this->isRoomFull()) {
        //Logger::info("++++++++++++++ room FULL");
        if(this->isEverybodyOnline()) {
            //Logger::info("++++++++++++++ ONLINE");
            this->changeStatus(GameStatus::READY);
        } else {
            //Logger::info("++++++++++++++ NOT ONLINE");
            this->changeStatus(GameStatus::CONNECTING);
        }
    } else {
        //Logger::info("++++++++++++++ NOT FULL");
    }
}

GameType Room::getGameType() {
    return this->type;
}

void Room::setGameType(GameType type) {
    this->type = type;
}

/**
 * Are all players assigned?
 * @return
 */
bool Room::isRoomFull() {
    return this->getPlayerCount() == (int) this->getGameType();
}

/**
 * Is everybody in a room online?
 * @return
 */
bool Room::isEverybodyOnline() {
    return this->countOnlinePlayers() == this->getPlayerCount();
}

/**
 *
 * @return
 */
bool Room::isJoinable() {
    return !this->isRoomFull();
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
    this->updateActivePlayer();
    this->turn++;
}

int Room::getTurn() const {

    int t = this->getTime();

    return this->turn;
}

void Room::deregisterPlayer(Player &p) {
    this->players.erase(p.getID());
    this->changeStatus(GameStatus::CONNECTING);
}

int Room::countOnlinePlayers() const {
    int onlinePlayers = 0;

    for(auto it = this->players.cbegin(); it != this->players.cend(); ++it) {
        if(it->second.isOnline())
            onlinePlayers++;
    }

    return onlinePlayers;
}

bool Room::isAnyoneOnline() const {
    return this->countOnlinePlayers() > 0;
}

int Room::getWinnerID() {
    return this->winnerID;
}

void Room::finishGame() {
    int index = (this->turn + 1) % this->getPlayerCount();
    this->winnerID = this->players[this->playerOrder[index]].getID();
    this->changeStatus(GameStatus::FINISHED);
}

bool Room::hasGameFinished() {
    return this->winnerID != -1;
}

void Room::endGame() {
    //this->changeStatus(GameStatus::FINISHED);
}

void Room::changeStatus(GameStatus s) {
    this->status = s;

    std::string str = "";

    str.append("CHANGING STATUS OF ROOM ");
    str.append(std::to_string(this->getID()));
    str.append(" TO ");

    switch (s) {
        case GameStatus::CONNECTING: str.append("CONNECTING"); break;
        case GameStatus::READY: str.append("READY"); break;
        case GameStatus::STARTED: str.append("STARTED"); break;
        case GameStatus::PLAYING: str.append("PLAYING"); break;
        case GameStatus::WAITING: str.append("WAITING"); break;
        case GameStatus::FINISHED: str.append("FINISHED"); break;
        case GameStatus::FINISHED_REPLAY: str.append("FINISHED_REPLAY"); break;
        case GameStatus::FINISHED_END: str.append("FINISHED_END"); break;
    }

    Logger::info(str);
}

bool Room::isReady() {
    return this->status == GameStatus::READY;
}

bool Room::checkReadyToContinue(bool replay) {

    if(this->type == GameType::SINGLEPLAYER) {

        if(replay) {
            this->changeStatus(GameStatus::FINISHED_REPLAY);
        } else {
            this->changeStatus(GameStatus::FINISHED_END);
        }

        return replay;
    }

    if(this->status == GameStatus::FINISHED) {

        if(replay) {
            this->changeStatus(GameStatus::FINISHED_REPLAY);
        } else {
            this->changeStatus(GameStatus::FINISHED_END);
        }

        // only one checked
        return false;
    }

    // both checked
    return true;
}

bool Room::isReplayReady() {
    return this->status == GameStatus::FINISHED_REPLAY;
}

int Room::getTime() const {
    int t = this->turn * Game::MOVE_TIME;
    return this->turn == 1 ? t + Game::FIRST_TURN_RESERVE : t;
}

//bool Room::checkReadyToNextTurnStart() {
//    return ++this->endTurnCount >= this->getPlayerCount();
//}


//
// *
// * @return true: everybody wants replay
// */
//bool Room::isReplayReady() {
//           // everybody online            everybody sent a reply request
//    return this->isRoomFull() && this->replayReady == this->getPlayerCount();
//}
//
//
// *
// * @return true: everybody sent a response, false: still waiting for someone
// */
//bool Room::checkPlayerReplayReady() {
//    this->replayReady++;
//    this->replayResponse++;
//
//    std::cout << this->replayReady << " / " << this->countOnlinePlayers() << " ... total: " << this->replayResponse;
//
//         // everybody online            everybody sent a response
//    return this->isRoomFull() && this->replayResponse == this->countOnlinePlayers();
//}
//
//void Room::checkPlayerReplayRefuse() {
//    this->replayResponse++;
//}