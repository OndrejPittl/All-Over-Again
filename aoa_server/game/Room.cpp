#include "Room.h"
#include "../communication/MessageSerializer.h"
#include "../core/Logger.h"
#include "GameDifficulty.h"
#include "GameStatus.h"
#include "Game.h"

Room::Room() {
    this->init();
}

Room::Room(int id) {
    this->init();
    this->id = id;
}

Room::Room(GameType type, GameDifficulty difficulty, BoardDimension dimension) {
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
    this->updateActivePlayer();
}

int Room::getID() {
    return this->id;
}

void Room::setID(int id) {
    this->id = id;
}

int Room::getPlayerCount() {
    return (int) this->players.size();
}

BoardDimension Room::getBoardDimension() const {
    return this->boardDimension;
}

void Room::setBoardDimension(BoardDimension boardDimension) {
    this->boardDimension = boardDimension;
}

int Room::getActivePlayerID() {
    return this->playerOrder[this->activePlayerIndex];
}

void Room::updateActivePlayer() {
    this->activePlayerIndex = this->getPlayerCount() > 0 ? this->turn % this->getPlayerCount() : 0;
}

GameDifficulty Room::getDifficulty() const {
    return this->difficulty;
}

void Room::setDifficulty(GameDifficulty difficulty) {
    this->difficulty = difficulty;
}

PlayerMap &Room::getPlayers() {
    return this->players;
}

PlayerMap Room::copyPlayers() {
    return this->players;
}

void Room::registerPlayer(Player *p) {
    this->players[p->getID()] = p;
    this->playerOrder.push_back(p->getID());

    if(this->isRoomFull()) {
        if(this->isEverybodyOnline()) {
            this->changeStatus(GameStatus::READY);
        } else {
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
    return this->getPlayerCount() >= (int) this->getGameType();
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
    return !this->isRoomFull() && this->status == GameStatus::CONNECTING;
}


std::queue<int> Room::getPlayerSockets() const {
    std::queue<int> q;

    for(auto it = this->players.cbegin(); it != this->players.cend(); ++it) {
        if(it->second->isOnline())
            q.push(it->second->getID());
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
    this->turn++;
    this->updateActivePlayer();
}

int Room::getTurn() const {
    return this->turn;
}

void Room::deregisterPlayer(Player *p) {
    int uid = p->getID();

    int i = 0;
    for (auto const& o : this->playerOrder) {
        int id = this->playerOrder[i];
        if(id == uid) break;
        i++;
    }

    // i contains index in playerOrder
    this->playerOrder.erase(this->playerOrder.begin() + i);
    this->players.erase(p->getID());

}

int Room::countOnlinePlayers() const {
    int onlinePlayers = 0;

    for(auto it = this->players.cbegin(); it != this->players.cend(); ++it) {
        if(it->second->isOnline())
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
    this->winnerID = this->players[this->playerOrder[index]]->getID();
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
        case GameStatus::ENDED: str.append("ENDED"); break;
    }

    Logger::info(str);
}

bool Room::isReady() {
    return this->status == GameStatus::READY;
}

bool Room::checkReadyToContinue(bool replay) {
    if(this->status == GameStatus::ENDED)
        return false;

    if(this->type == GameType::SINGLEPLAYER) {

        if(replay) {
            this->changeStatus(GameStatus::FINISHED_REPLAY);
        } else {
            this->changeStatus(GameStatus::FINISHED_END);
        }

        return replay;
    }

    // multiplayer:
    if(this->status == GameStatus::FINISHED) {

        if(replay) {
            this->changeStatus(GameStatus::FINISHED_REPLAY);
        } else {
            this->changeStatus(GameStatus::FINISHED_END);
        }

        // only one checked
        return false;
    }

    if(this->status == GameStatus::FINISHED_REPLAY) {
        return true;
    }

    // both checked
    this->changeStatus(GameStatus::ENDED);
    return true;
}

bool Room::isReplayReady() {
    return this->status == GameStatus::FINISHED_REPLAY;
}

bool Room::hasGameEnded() {
    return this->status == GameStatus::ENDED;
}

int Room::getTime() const {
    /*
     *  difficulty (0 - 2): 1 - 2
     *  dimension  (1 - 5): 1 - 2
     */

    double  turn     = this->getTurn(),
            turnCoef = (turn > 5) ? turn * 0.8 : (turn > 10) ? turn * 0.5 : (turn > 15) ? turn * 0.6 : turn,
            diff     = (double) this->getDifficulty(),
            diffCoef = (diff == 2) ? 2 : 1,
            dim      = (double) this->getBoardDimension(),
            dimCoef  = (dim > 3) ? 2 : 1;
    int     time     = (int) (turnCoef * (diffCoef + dimCoef));

    //time += 500;

    time += 3;

    return this->turn == 1 ? time + Game::FIRST_TURN_RESERVE : time;
}

void Room::reassignPlayer(Player *player, Player *prevPlayer) {
    // room status?
    // players
    // player order

    this->changeStatus(GameStatus::READY);

    // this->players: PlayerMap[uid] = player
    this->deregisterPlayer(prevPlayer);
    this->registerPlayer(player);
}
