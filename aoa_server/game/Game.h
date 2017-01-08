#ifndef GAME_H
#define GAME_H


#include <queue>
#include "../partial/StringBuilder.h"
#include "Room.h"

class Game {
    private:
        StringBuilder sb;
        void init();

    public:
        Game();
        bool validateTurn(std::queue<int> &progress, Room &room);
};


#endif