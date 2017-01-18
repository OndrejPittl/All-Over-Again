package cz.kiv.ups.communication;

import cz.kiv.ups.config.CommunicationConfig;

public enum MessageType {
    HELLO (CommunicationConfig.REQ_HELLO),
    SIGN_IN (CommunicationConfig.REQ_SIGN_IN),
    GAME_LIST (CommunicationConfig.REQ_GAME_LIST),
    GAME_NEW (CommunicationConfig.REQ_GAME_NEW),
    GAME_JOIN (CommunicationConfig.REQ_GAME_JOIN),
    GAME_START (CommunicationConfig.REQ_GAME_START),
    TURN_DATA (CommunicationConfig.REQ_TURN_DATA),
    GAME_RESULT(CommunicationConfig.REQ_GAME_END),
    GAME_LEAVE (CommunicationConfig.REQ_GAME_END),
    SIGN_OUT (CommunicationConfig.REQ_SIGN_OUT),
    PLAYER_LIST (CommunicationConfig.REQ_PLAYER_LIST);

    private int code;

    MessageType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static MessageType nth(int index){
        for (MessageType t : MessageType.values()) {
            if(index == t.getCode()) {
                return t;
            }
        }
        return null;
    }
}
