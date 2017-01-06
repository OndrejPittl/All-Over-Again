#ifndef ROOM_H
#define ROOM_H

#include <string>
#include <vector>

#include "GameDifficulty.h"
#include "Player.h"

class Room {

    private:
        int id;
        int playerLimit;
        int playerCount;
        int boardDimension;
        int activePlayerID;
        GameDifficulty difficulty;
        std::vector<Player*> players;

    public:



};


#endif