package communication;

///**
// * Flag of a request/message for a user availability.
// */
//public static final int REQ_SIGN_IN = 1;
//
///**
// * Flag of a request/message for a list of available rooms.
// */
//public static final int REQ_GAME_LIST = 2;
//
///**
// * Flag of a request/message for creating a new room.
// */
//public static final int REQ_GAME_NEW = 3;
//
///**
// * Flag of a request/message for joining a room.
// */
//public static final int REQ_GAME_JOIN = 4;
//
///**
// * Flag of a request/message indicating change
// * of a status of a game initialization.
// */
//public static final int REQ_GAME_START = 5;
//
///**
// * Flag of a request/message indicating incoming
// * info of a last turn.
// */
//public static final int REQ_TURN_INFO = 6;
//
///**
// * Flag of a request/message indicating outgoing
// * data of a current turn.
// */
//public static final int REQ_TURN_DATA = 7;
//
///**
// * Flag of a request/message indicating incoming
// * data about game result.
// */
//public static final int REQ_GAME_END = 8;


import config.CommunicationConfig;

public enum MessageType {
    HELLO (CommunicationConfig.REQ_HELLO),
    SIGN_IN (CommunicationConfig.REQ_SIGN_IN),
    GAME_LIST (CommunicationConfig.REQ_GAME_LIST),
    GAME_NEW (CommunicationConfig.REQ_GAME_NEW),
    GAME_JOIN (CommunicationConfig.REQ_GAME_JOIN),
    GAME_START (CommunicationConfig.REQ_GAME_START),
    TURN_DATA (CommunicationConfig.REQ_TURN_DATA),
    GAME_END (CommunicationConfig.REQ_GAME_END),
    GAME_LEAVE (CommunicationConfig.REQ_GAME_END),
    SIGN_OUT (CommunicationConfig.REQ_SIGN_OUT);

    private int code;

    MessageType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
