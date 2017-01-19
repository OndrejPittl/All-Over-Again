#ifndef GAME_DIFFICULTY_H
#define GAME_DIFFICULTY_H


enum class GameDifficulty {
    EASY,
    NORMAL,
    EXPERT
};

GameDifficulty convertInternalGameDifficulty(int index);

#endif
