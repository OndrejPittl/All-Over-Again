#ifndef ROOM_H
#define ROOM_H

#include <string>
#include <vector>
#include <map>
#include <queue>

#include "GameType.h"
#include "GameDifficulty.h"
#include "GameStatus.h"
#include "BoardDimension.h"
#include "Player.h"
#include "../partial/StringBuilder.h"

class Room {

    private:
        int id;
        int activePlayerIndex;
        int turn;
        int winnerID;
        bool ended;
        GameStatus status;
        GameType type;
        bd::BoardDimension boardDimension;
        GameDifficulty difficulty;
        std::map<int, Player> players;
        std::queue<int> progress;
        std::vector<int> playerOrder;

    public:
        Room();

        Room(GameType type, GameDifficulty difficulty, bd::BoardDimension dimension);

        void init();

        void restart();

        int getID();

        void setID(int id);

        GameType getGameType();

        void setGameType(GameType type);

        int getPlayerCount();

        bd::BoardDimension getBoardDimension();

        void setBoardDimension(bd::BoardDimension boardDimension);

        int getActivePlayerID();

        void updateActivePlayer();

        GameDifficulty getDifficulty();

        void setDifficulty(GameDifficulty difficulty);

        std::map<int, Player> &getPlayers();

        void registerPlayer(Player *p);

        void deregisterPlayer(Player &p);

        bool isRoomFull();

        bool isEmpty();

        bool isJoinable();

        std::queue<int> getPlayerSockets() const;

        const std::queue<int> &getProgress() const;

        void setProgress(std::queue<int> progress);

        bool hasProgress();

        // turn++
        void startTurn();

        int getTurn() const;

        int countOnlinePlayers() const;

        bool isAnyoneOnline() const;

        int getWinnerID();

        void finishGame();

        bool hasGameFinished();

//        bool checkPlayerReplayReady();
//
//        void checkPlayerReplayRefuse();
//
        bool isReplayReady();

        void endGame();

        bool checkReadyToContinue(bool replay);

        void changeStatus(GameStatus s);

        bool isReady();




    bool isEverybodyOnline();

    int getTime() const;

};


#endif