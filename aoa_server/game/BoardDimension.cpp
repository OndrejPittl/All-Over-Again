#include "BoardDimension.h"

BoardDimension convertInternalBoardDimension(int index) {
    switch(index) {
        case 5: return BoardDimension::BOARD_HUGE;            // 5x5
        case 4: return BoardDimension::BOARD_LARGE;           // 4x4
        case 3: return BoardDimension::BOARD_NORMAL;          // 3x3
        case 2: return BoardDimension::BOARD_SMALL;           // 2x2
        default: case 1: return BoardDimension::BOARD_TINY;   // 1x1? Rly?
    }
}
