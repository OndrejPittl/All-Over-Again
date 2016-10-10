package application;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import config.ConnectionConfig;


public class Connection {
	
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
	private Socket clientSocket;
	


	public Connection() {
		this.serverIP = ConnectionConfig.SERVER_IP;
		this.serverPort = ConnectionConfig.SERVER_PORT;
	}
	
	/**
	 * Method handling connection to server.
	 */
	public boolean connect() {
		int count = 0;
		
		while(!this.createSocket()) {
			count++;
			
			Logger.logConnectionFailed(count);
			
			if(count >= ConnectionConfig.MAX_CONNECTION_TRY_COUNT) {
				return false;
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
		
		Logger.logConnectionSucceeded(this.getHostAddress(), this.getHostName());
		return true;
	}
	
	/**
	 * Method handling disconnection from server.
	 */
	public void disconnect(){
		this.closeSocket();
	}
	
	private void closeSocket() {
		if(this.clientSocket != null) {
			try {
				this.clientSocket.close();
			} catch (IOException e) {
				System.err.print("Error: closing socket.\n");
				e.printStackTrace();
			}
		}
	}
	
	public boolean isConnected(){
		return this.clientSocket != null;
	}
	
	public int getServerPort() {
		return serverPort;
	}

	public String getServerIP() {
		return serverIP;
	}

	public InetAddress getAddress() {
		return address;
	}

	public Socket getClientSocket() {
		return clientSocket;
	}
	
	public boolean createSocket() {
		try {
			this.clientSocket = new Socket(this.serverIP, this.serverPort);
			this.address = this.clientSocket.getInetAddress();
		} catch (UnknownHostException e) {
			System.err.print("Error: creating connection.\n");
			//e.printStackTrace();
		} catch (IOException e) {
			System.err.print("Error: creating connection.\n");
			//e.printStackTrace();
		}

		return this.isConnected();
	}
	
	public String getHostAddress(){
		return this.address.getHostAddress();
	}
	
	public String getHostName(){
		return this.address.getHostName();
	}
	
	
	
}
