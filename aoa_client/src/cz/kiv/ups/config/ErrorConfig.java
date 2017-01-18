package cz.kiv.ups.config;


public class ErrorConfig {


    public static String USERNAME_REGEX = "^[a-zA-Z0-9-_<>]{3,15}$";

    public static final String CONNECTION_SERVER_UNREACHABLE = "Server is offline.";

    public static final String CONNECTION_SERVER_UNAUTHORIZED = "The server's identity cannot be verified.";

    public static final String CONNECTION_SERVER_OFFLINE_READ = "Connection lost. Server has gone offline. (receiver)";

    public static final String CONNECTION_SERVER_OFFLINE_WRITE = "Connection lost. Server has gone offline. (sender)";

    public static final String USERNAME_INVALID = "Invalid username. Username must be at least 3 and max 15 chars\n" +
                                                  "long. It can contain a-z, A-Z, 0-9 and some special characters: -_<>.";

    public static final String USERNAME_TAKEN = "Sorry, username is already taken.";

    /**
     * Player is no longer waiting for an opponent  -> left game,
     *                                              -> someone joined game.
     */
    public static final String ROOM_JOIN_REFUSED = "Sorry, the room is no longer available.";

    public static final String GAME_REPLAY_REFUSED = "Your opponent has left.";

}
