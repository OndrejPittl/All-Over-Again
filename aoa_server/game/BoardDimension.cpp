#include "BoardDimension.h"

BoardDimension convertInternalBoardDimension(int index) {
    switch(index) {
        case 4: return BoardDimension::HUGE;            // 5x5
        case 3: return BoardDimension::LARGE;           // 4x4
        case 2: return BoardDimension::NORMAL;          // 3x3
        case 1: return BoardDimension::SMALL;           // 2x2
        default: case 0: return BoardDimension::TINY;   // 1x1? Rly?
    }
}
