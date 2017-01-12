#ifndef BOARD_DIMENSION_H
#define BOARD_DIMENSION_H


namespace bd {
    enum BoardDimension {
        TINY = 1,
        SMALL,
        NORMAL,
        LARGE,
        HUGEUE = 5
    };
}

bd::BoardDimension convertInternalBoardDimension(int index);

#endif
