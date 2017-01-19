#ifndef MESSAGE_TYPE_H
#define MESSAGE_TYPE_H

static const int MESSAGE_TYPE_COUNT = 10;

enum MessageType {
    HELLO,
    SIGN_IN,
    GAME_LIST,
    GAME_NEW,
    GAME_JOIN,
    GAME_START,
    TURN_DATA,
    GAME_END,
    GAME_LEAVE,
    SIGN_OUT,
    PLAYER_INFO,
};

MessageType convertInternalMessageType(int index);

#endif
