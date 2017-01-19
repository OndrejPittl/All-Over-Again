#ifndef GAME_H
#define GAME_H


#include <queue>
#include "../partial/StringBuilder.h"
#include "Room.h"

class Game {
    private:
        StringBuilder *log;

        void init();

        bool compareProgress(std::queue<int> oldProg, std::queue<int> newProg);

    public:
        static const int MOVE_TIME;

        static const int FIRST_TURN_RESERVE;


        Game();

        bool validateTurn(const std::queue<int> &progress, Room *room);

};


#endif