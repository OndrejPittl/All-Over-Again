#ifndef BOARD_DIMENSION_H
#define BOARD_DIMENSION_H


enum class BoardDimension {
    TINY = 1,
    SMALL,
    NORMAL,
    LARGE,
    HUGE = 5
};

BoardDimension convertInternalBoardDimension(int index);

#endif
