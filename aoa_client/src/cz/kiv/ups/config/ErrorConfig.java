package cz.kiv.ups.config;


public class ErrorConfig {

    public static final String USERNAME_TAKEN = "Sorry, username is already taken.";

    /**
     * Player is no longer waiting for an opponent  -> left game,
     *                                              -> someone joined game.
     */
    public static final String ROOM_JOIN_REFUSED = "Sorry, the room is no longer available.";

    public static final String GAME_REPLAY_REFUSED = "Your opponent has left.";

}
