package communication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import application.Connection;
import application.Room;
import config.ConnectionConfig;

public class Communicator {
	
	/**
	 * Connection reference.
	 */
	private Connection conn;
	
	/**
	 * Collection of available rooms.
	 */
	private Room rooms[];
	
	/**
	 *	Buffer for incoming message.
	 */
	private byte[] msgBuffer;
	
	/**
	 * Input byte stream.
	 */
	private BufferedInputStream bis;
	
	/**
	 * Output byte stream.
	 */
	private BufferedOutputStream bos;
	
	/**
	 * Last received message.
	 */
	private String lastReceivedMsg;
	
	/**
	 * Constructor.
	 */
	public Communicator(){
		this.msgBuffer = new byte[1024];
	}
	
	public void setConnection(Connection conn){
		this.conn = conn;
	}
	
	
	private void initBufferedOutputStream() throws IOException{
		this.bos = new BufferedOutputStream(this.conn.getClientSocket().getOutputStream());
	}
	
	private void initBufferedInputStream() throws IOException{
		this.bis = new BufferedInputStream(this.conn.getClientSocket().getInputStream());
	}
	
	/**
	 * Universal method sending server a message.
	 * @param msg	message to send
	 */
	public void writeMsg(String msg){
		try {
			System.out.print("sending...\n");
			initBufferedOutputStream();
			this.bos.write(msg.getBytes(), 0, msg.length());
			this.bos.flush();
		} catch (IOException e) {
			System.err.print("Error: BufferedOutputStream initialization.\n");
			e.printStackTrace();
		}
	}
	
	public String recvMsg(){		
		
		int msgLen;
		String msg = "";
		
		try {
			initBufferedInputStream();
			
			while((msgLen = this.bis.read(this.msgBuffer)) > 0) {	
				for (int i = 0; i < msgLen; i++) {
					if(msgBuffer[i] == 0) {
						break;
					} else if (msgBuffer[i] > 31) {
						msg = msg + (char) msgBuffer[i];
					}
				}
				System.out.print(">>> received: \"" + msg + "\" (" + msgLen + " bytes)\n");
				/*this.lastReceivedMsg = msg;
				msg = "";*/
				return msg.toString();
			}
			
		} catch (IOException e) {
			System.err.print("Error: BufferedInputStream initialization.\n");
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	public String getLastReceivedMsg(){
		return this.lastReceivedMsg;
	}
	
	public void setRooms(Room rooms[]){
		this.rooms = rooms;
	}
	
	public Room[] getRooms(){
		return this.rooms;
	}
}
