#include <iostream>
#include "MessageType.h"

MessageType convertInternalMessageType(int index) {
    switch(index % MESSAGE_TYPE_COUNT) {
        case 0: return MessageType::HELLO;
        case 1: return MessageType::SIGN_IN;
        case 2: return MessageType::GAME_LIST;
        case 3: return MessageType::GAME_NEW;
        case 4: return MessageType::GAME_JOIN;
        case 5: return MessageType::GAME_START;
        case 6: return MessageType::TURN_DATA;
        case 7: return MessageType::GAME_END;
        case 8: return MessageType::GAME_LEAVE;
        default: case 9: return MessageType::SIGN_OUT;
    }

//    int mod = (index - 1) % MESSAGE_TYPE_COUNT;
//    switch(mod) {
//        case 0: return MessageType::HELLO;
//        case 1: return MessageType::SIGN_IN;
//        case 2: return MessageType::GAME_LIST;
//        case 3: return MessageType::GAME_NEW;
//        case 4: return MessageType::GAME_JOIN;
//        case 5: return MessageType::GAME_START;
//        case 6: return MessageType::TURN_DATA;
//        case 7: return MessageType::GAME_END;
//        case 8: return MessageType::GAME_LEAVE;
//        default: case 9: return MessageType::SIGN_OUT;
//    }
};

// not being used already
//int convertExternalMessageType(int type) {
//    return type;
//};

