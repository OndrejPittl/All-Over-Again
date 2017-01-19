package cz.kiv.ups.application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

import cz.kiv.ups.config.ConnectionConfig;


public class Connection {

    private static Logger logger = Logger.getLogger();

	
	/**
	 * Server port.
	 */
	private int serverPort;
	
	/**
	 * Server IP.
	 */
	private String serverIP;
	
	/**
	 * Server dot-notation represented address.
	 */
	private InetAddress address;
	
	/**
	 * Client socket.
	 */
	private static Socket clientSocket;



	private OutputStream outStream;

	private InputStream inStream;




	public Connection(String ip, int port) {
		this.serverIP = ip;
		this.serverPort = port;
	}
	
	/**
	 * Method handling connection to server.
	 */
	public boolean connect() {
		int count = 0;
		
		while(!this.createSocket()) {

			count++;
			logger.error("CLI: " + count + ". connection try failed.");
			
			if(count >= ConnectionConfig.MAX_CONNECTION_TRY_COUNT) {
				return false;
			}
			
			try {
				Thread.sleep(ConnectionConfig.CONNECTION_TRY_PERIOD_MS);
			} catch (InterruptedException e) {}
		}

		logger.info("CLI: Connected to : " + this.getHostAddress() + " named: " + this.getHostName());
		return true;
	}
	
	/**
	 * Method handling disconnection from server.
	 */
	public static void disconnect(){
		Connection.closeSocket();
	}
	
	private static void closeSocket() {
		if(Connection.clientSocket != null) {
			try {
				Connection.clientSocket.close();
			} catch (IOException e) {
				logger.error("Closing socket.");
				//e.printStackTrace();
			}


		}
	}
	
	public boolean isConnected(){
		return Connection.clientSocket != null;
	}
	
	public boolean createSocket() {
		try {
			//Connection.clientSocket = new Socket(this.serverIP, this.serverPort);
            Connection.clientSocket = new Socket();
            Connection.clientSocket.connect(new InetSocketAddress(this.serverIP, this.serverPort), 3000);

            this.address = Connection.clientSocket.getInetAddress();
			this.outStream = Connection.clientSocket.getOutputStream();
			this.inStream = Connection.clientSocket.getInputStream();
		} catch (SocketTimeoutException e) {
            logger.error("Error: Server unreachable! (timeout)");
            Application.disconnect(true, null);
		} catch (UnknownHostException e) {
			logger.error("Error: while creating connection.");
			Application.disconnect(true, null);
		} catch (IOException e) {
			logger.error("Error: while creating connection.");
            Application.disconnect(true, null);
		}

		return this.isConnected();
	}
	
	public String getHostAddress(){
		return this.address.getHostAddress();
	}
	
	public String getHostName(){
		return this.address.getHostName();
	}

	public OutputStream getOutStream() {
		return outStream;
	}

	public InputStream getInStream() {
		return inStream;
	}


}
