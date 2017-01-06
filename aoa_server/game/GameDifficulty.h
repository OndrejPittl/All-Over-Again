#ifndef GAME_DIFFICULTY_H
#define GAME_DIFFICULTY_H


//class GameDifficulty {};
enum GameDifficulty {
    EASY,
    NORMAL,
    EXPERT
};

GameDifficulty convertInternalGameDifficulty(int index);

#endif
