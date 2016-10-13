package config;

public class CommunicationConfig {
	
	/**
	 * Blocks delimiting character. 
	 */
	public static final String MSG_DELIMITER = ";";
	
	/**
	 * Request accepted/succeeded.
	 */
	public static final int REQ_ACK = 1;
	
	/**
	 * Request rejected/failed.
	 */
	public static final int REQ_NACK = 0;
	
	/**
	 * Flag of a request/message for a user availability.
	 */
	public static final int REQ_USERNAME_AVAILABILITY = 1;
	
	/**
	 * Flag of a request/message for a list of available rooms.
	 */
	public static final int REQ_ROOM_LIST = 2;
	
	/**
	 * Flag of a request/message for creating a new room. 
	 */
	public static final int REQ_ROOM_CREATION = 3;
	
	/**
	 * Flag of a request/message for joining a room.
	 */
	public static final int REQ_ROOM_JOIN = 4;
	
	/**
	 * Flag of a request/message indicating change
	 * of a status of a game initialization.
	 */
	public static final int REQ_GAME_INIT = 5;
	
	/**
	 * Flag of a request/message indicating incoming
	 * info of a last turn.
	 */
	public static final int REQ_TURN_INFO = 6;
	
	/**
	 * Flag of a request/message indicating outgoing
	 * data of a current turn.
	 */
	public static final int REQ_TURN_DATA = 7;
	
	/**
	 * Flag of a request/message indicating incoming
	 * data about game result.
	 */
	public static final int REQ_GAME_RESULT = 8;

	/**
	 * Hello packet sent by a client to a server.
	 */
	public static final String MSG_HELLO_SERVER = "Hey AOA! How are you?";
	
	/**
	 * Hello packet response from a server to a client.
	 */
	public static final String MSG_HELLO_SERVER_RESPONSE = "Hey Client! I am fine.";
	
}
