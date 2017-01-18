package cz.kiv.ups.model;


import cz.kiv.ups.communication.MessageType;

import java.util.Arrays;

public enum GameStatus {


    /**
     * Hello packet authorization.
     */
    HELLO_AUTHORIZATION (new MessageType[]{ MessageType.HELLO }),

    /**
     *  Players getting connected.
     */
    SIGNING_IN(new MessageType[]{ MessageType.SIGN_IN, MessageType.GAME_JOIN }),

    /**
     * Game center, room selection (create/join).
     */
    ROOM_SELECTING (new MessageType[]{ MessageType.GAME_LIST }),

    /**
     * Waiting for a game creation.
     */
    ROOM_CREATING (new MessageType[]{ MessageType.GAME_NEW }),

    /**
     * Waiting for a game join.
     */
    ROOM_JOINING (new MessageType[]{ MessageType.GAME_JOIN }),

    /**
     * Waiting for a game initialization.
     */
    GAME_INITIALIZING (new MessageType[]{ MessageType.GAME_START }),    // ACK vs. NACK

    /**
     * Restart game requested.
     */
    GAME_RESTART (new MessageType[]{ MessageType.GAME_START, MessageType.GAME_RESULT}),

    /**
     * Playing a game, turn starts.
     */
    GAME_PLAYING_TURN_START (new MessageType[]{ MessageType.TURN_DATA, MessageType.PLAYER_LIST }),

    /**
     * Playing a game, turn ends.
     */
    GAME_PLAYING_TURN_END (new MessageType[]{ MessageType.TURN_DATA, MessageType.PLAYER_LIST }),

    /**
     * Game paused, waiting for a player.
     */
//    GAME_WAITING (new MessageType[]{ MessageType.GAME_START, MessageType.GAME_RESULT}),
    GAME_WAITING (new MessageType[]{ MessageType.GAME_START }),

    /**
     * Game ends.
     */
    GAME_END (new MessageType[]{ MessageType.GAME_RESULT}),

    /**
     * Game has finished.
     */
//    GAME_RESULTS(new MessageType[]{ MessageType.GAME_START, MessageType.GAME_RESULT}),
    GAME_RESULTS(new MessageType[]{ MessageType.GAME_START}),

    /**
     * Exitting a game.
     */
    EXIT_GAME (new MessageType[]{});


    private final MessageType[] acceptableMessageTypes;


    GameStatus(MessageType[] acceptableMessageTypes) {
        this.acceptableMessageTypes = acceptableMessageTypes;
    }

    public boolean isAcceptable(MessageType type){
        return Arrays.asList(this.acceptableMessageTypes).contains(type);
    }
}
