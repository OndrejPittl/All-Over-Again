#ifndef ROOM_H
#define ROOM_H

#include <string>
#include <vector>
#include <map>
#include <queue>

#include "GameType.h"
#include "GameDifficulty.h"
#include "BoardDimension.h"
#include "Player.h"
#include "../partial/StringBuilder.h"

class Room {

    private:
        int id;
        int activePlayerID;
        GameType type;
        BoardDimension boardDimension;
        GameDifficulty difficulty;
        std::map<int, Player> players;

    public:
        Room();

        Room(GameType type, GameDifficulty difficulty, BoardDimension dimension);

        void init();

        int getID();

        void setID(int id);

        GameType getGameType();

        void setGameType(GameType type);

        int getPlayerCount();

        BoardDimension getBoardDimension();

        void setBoardDimension(BoardDimension boardDimension);

        int getActivePlayerID();

        void setActivePlayerID(int activePlayerID);

        GameDifficulty getDifficulty();

        void setDifficulty(GameDifficulty difficulty);

        std::map<int, Player> getPlayers();

        void registerPlayer(Player *p);

        bool isRoomReady();

        bool isEmpty();

        bool isJoinable();

        std::queue<int> getPlayerSockets();



};


#endif