package cz.kiv.ups.config;

public class ConnectionConfig {
	
	public static final String SERVER_IP = "127.0.0.1";
	
	public static final int SERVER_PORT = 23456;

	public static final int SERVER_PORT_MIN = 1024;

	public static final int SERVER_PORT_MAX = 65535;

	public static final int MAX_CONNECTION_TRY_COUNT = 10;

	public static final int CONNECTION_TRY_PERIOD_MS = 2500;

	public static final int MAX_HELLO_TRY_COUNT = 3;
}
