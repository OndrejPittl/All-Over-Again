#include "BoardDimension.h"

bd::BoardDimension convertInternalBoardDimension(int index) {
    switch(index) {
        case 5: return bd::BoardDimension::HUGE;            // 5x5
        case 4: return bd::BoardDimension::LARGE;           // 4x4
        case 3: return bd::BoardDimension::NORMAL;          // 3x3
        case 2: return bd::BoardDimension::SMALL;           // 2x2
        default: case 1: return bd::BoardDimension::TINY;   // 1x1? Rly?
    }
}
