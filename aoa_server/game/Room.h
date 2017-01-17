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
        GameStatus status;
        GameType type;
        BoardDimension boardDimension;
        GameDifficulty difficulty;
        PlayerMap players;
        std::queue<int> progress;
        std::vector<int> playerOrder;

        void init();

    public:
        Room();

        Room(int id);

        Room(GameType type, GameDifficulty difficulty, BoardDimension dimension);



        void restart();

        int getID();

        void setID(int id);

        GameType getGameType();

        void setGameType(GameType type);

        int getPlayerCount();

        BoardDimension getBoardDimension() const;

        void setBoardDimension(BoardDimension boardDimension);

        int getActivePlayerID();

        void updateActivePlayer();

        GameDifficulty getDifficulty() const;

        void setDifficulty(GameDifficulty difficulty);

        PlayerMap &getPlayers();

        void registerPlayer(Player *p);

        void deregisterPlayer(Player *p);

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

    PlayerMap copyPlayers();

    bool hasGameEnded();

    void reassignPlayer(Player *player, Player *prevPlayer);
};


typedef std::map<int, Room*> RoomMap;



#endif