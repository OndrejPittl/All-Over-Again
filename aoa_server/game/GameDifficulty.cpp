#include "GameDifficulty.h"

GameDifficulty convertInternalGameDifficulty(int index) {
    switch(index) {
        case 2: return GameDifficulty::EXPERT;
        case 1: return GameDifficulty::NORMAL;
        default: case 0: return GameDifficulty::EASY;
    }
}