#include "GameType.h"


GameType convertInternalGameType(int index) {
    switch(index) {
        case 2: return GameType::MULTIPLAYER;
        default: case 1: return GameType::SINGLEPLAYER;
    }
}
