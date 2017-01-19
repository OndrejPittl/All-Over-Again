#include <iostream>
#include "Game.h"

#include "../core/Logger.h"


/**
 * 4 seconds for every move.
 * ->  turn * 4 s = total time for a turn (without intro)
 */
const int Game::MOVE_TIME = 10;

const int Game::FIRST_TURN_RESERVE = 3;


Game::Game() {
    this->init();
}

void Game::init() {
    this->log = new StringBuilder();
}

bool Game::validateTurn(const std::queue<int> &progress, Room *room) {
    int progCount, prevProgCount, expectedCount;
    bool correct;
    std::queue<int> previousProgress;

    correct = true;

    previousProgress = room->getProgress();

    // new progress: progress count == prev progress count + difficult
    progCount = (int) progress.size();
    prevProgCount = (int) previousProgress.size();
    expectedCount = prevProgCount + (int) room->getDifficulty() + 1;


    // -- log --
    this->log->clear();
    this->log->append("____ PROGRESS: old: "); this->log->append(prevProgCount);
    this->log->append(", new: "); this->log->append(progCount);
    this->log->append(", expected: "); this->log->append(expectedCount);
    Logger::debug(this->log->getString());


    if(progCount != expectedCount) {
        return false;
    }

    if(room->hasProgress()) {
        // NOT first turn
        correct = this->compareProgress(previousProgress, progress);
    }

    return correct;
}



/**
 * COPIES of progress are being modified.
 * @param oldProg
 * @param newProg
 * @return
 */
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