#ifndef BOARD_DIMENSION_H
#define BOARD_DIMENSION_H


enum class BoardDimension {
    BOARD_TINE = 1,
    BOARD_SMALL,
    BOARD_NORMAL,
    BOARD_LARGE,
    BOARD_HUGE
};

BoardDimension convertInternalBoardDimension(int index);

#endif
