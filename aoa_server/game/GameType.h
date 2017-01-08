#ifndef GAME_TYPE_H
#define GAME_TYPE_H


enum class GameType {
    SINGLEPLAYER = 1,
    MULTIPLAYER
};


GameType convertInternalGameType(int index);



#endif