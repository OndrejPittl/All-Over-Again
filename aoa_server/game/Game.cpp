#include <iostream>
#include "Game.h"


/**
 * 4 seconds for every move.
 * ->  turn * 4 s = total time for a turn (without intro)
 */
const int Game::MOVE_TIME = 4;
const int Game::FIRST_TURN_RESERVE = 3;

Game::Game() {

}

void Game::init() {

}

bool Game::validateTurn(const std::queue<int> &progress, Room &room) {
    int progCount, prevProgCount;
    bool correct;
    std::queue<int> previousProgress;

    correct = true;

    if(room.hasProgress()) {
        // NOT first turn

        previousProgress = room.getProgress();

        // new progress: progress count == prev progress count + difficult
        progCount = (int) progress.size();
        prevProgCount = (int) previousProgress.size();

        int expectedCount = prevProgCount + (int) room.getDifficulty() + 1;

        std::cout << "____ old: " << prevProgCount << ", new: " << progCount << ", expected: " << expectedCount << std::endl;

        if(progCount != expectedCount) {
            return false;
        }

        correct = this->compareProgress(previousProgress, progress);
    }

    return correct;
}



bool Game::compareProgress(std::queue<int> oldProg, std::queue<int> newProg) {
    while(!oldProg.empty()) {
        int prev, next;

        prev = oldProg.front();
        next = newProg.front();

        if(prev != next)
            return false;

        newProg.pop();
        oldProg.pop();
    }

    return true;
}