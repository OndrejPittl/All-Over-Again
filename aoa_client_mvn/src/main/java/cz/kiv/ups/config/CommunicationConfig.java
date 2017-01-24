package cz.kiv.ups.config;

public class CommunicationConfig {
	
	/**
	 * Blocks delimiting character. 
	 */
	public static final String MSG_DELIMITER = ";";
	
	/**
	 * Sub-blocks delimiting character. 
	 */
	public static final String MSG_SUB_DELIMITER = ":";


	public static final int MSG_CHECKSUM_MODULO = 235;

	/**
	 *
	 */
	public static final char MSG_STX = '*';

	/**
	 *
	 */
	public static final char MSG_ETX = '#';
	
	/**
	 * Request accepted/succeeded.
	 */
	public static final String REQ_ACK = "1";
	
	/**
	 * Request rejected/failed.
	 */
	public static final String REQ_NACK = "0";


	public static final int ASCII_LOWER =  32;

	public static final int ASCII_UPPER =  126;



    public static final int REQ_HELLO = 0;

	/**
	 * Flag of a request/message for a user availability.
	 */
	public static final int REQ_SIGN_IN = 1;

	/**
	 * Flag of a request/message for a list of available rooms.
	 */
	public static final int REQ_GAME_LIST = 2;

	/**
	 * Flag of a request/message for creating a new room.
	 */
	public static final int REQ_GAME_NEW = 3;

	/**
	 * Flag of a request/message for joining a room.
	 */
	public static final int REQ_GAME_JOIN = 4;

	/**
	 * Flag of a request/message indicating change
	 * of a status of a game initialization.
	 */
	public static final int REQ_GAME_START = 5;

	/**
	 * Flag of a request/message indicating incoming
	 * info of a last turn.
	 */
//	public static final int REQ_TURN_INFO = 6;

	/**
	 * Flag of a request/message indicating outgoing
	 * data of a current turn.
	 */
	public static final int REQ_TURN_DATA = 6;

	/**
	 * Flag of a request/message indicating incoming
	 * data about game result.
	 */
    public static final int REQ_GAME_END = 7;

    public static final int REQ_GAME_LEAVE = 8;

    public static final int REQ_SIGN_OUT = 9;

    public static final int REQ_PLAYER_LIST = 10;

    public static final int REQ_WAITING_READY = 11;




	/**
	 * Hello packet sent by a client to a server.
	 */
	public static final String MSG_HELLO_SERVER = "Hey AOA! How are you?";
	
	/**
	 * Hello packet response from a server to a client.
	 */
	public static final String MSG_HELLO_SERVER_RESPONSE = "Hey Client! I am fine.";
	
}
