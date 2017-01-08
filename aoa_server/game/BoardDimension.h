#ifndef BOARD_DIMENSION_H
#define BOARD_DIMENSION_H


enum class BoardDimension {
    TINY = 1,
    SMALL,
    NORMAL,
    LARGE,
    HUGE
};

BoardDimension convertInternalBoardDimension(int index);

#endif
