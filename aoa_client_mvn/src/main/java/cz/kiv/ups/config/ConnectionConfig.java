package cz.kiv.ups.config;

public class ConnectionConfig {
	
//	public static final String DEFAULT_SERVER_IP = "127.0.0.1";
	public static final String DEFAULT_SERVER_IP = "localhost";

	public static final int DEFAULT_SERVER_PORT = 23456;

	public static final int SERVER_PORT_MIN = 1;

	public static final int SERVER_PORT_MAX = 65535;

	public static final int MAX_CONNECTION_TRY_COUNT = 10;

	public static final int CONNECTION_TRY_PERIOD_MS = 2500;

	public static final int MAX_HELLO_TRY_COUNT = 3;

	public static final int MAX_INCORRECT_MESSAGES = 5;

}
