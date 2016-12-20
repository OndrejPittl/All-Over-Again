package communication;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CyclicBarrier;

import application.Application;
import application.Connection;
import application.Logger;
import config.CommunicationConfig;
import config.ConnectionConfig;
import model.Player;
import model.Room;


public class CommunicationManager {
	
	private Application app;
	
	private Connection conn;
	
	private Communicator comm;
	
	private CommunicationParser parser;
	
	private CyclicBarrier barrier;
	
	private StringBuilder sb;
	
	
	
	public CommunicationManager() {
		this.app = Application.getInstance();
		this.comm = new Communicator();
		this.parser = new CommunicationParser();
		this.barrier = new CyclicBarrier(2);
		this.sb = new StringBuilder();
	}
	
	public boolean helloPacketHandShake(){
		int count = 0;
		
		while(!this.helloServer()) {
			count++;
			
			Logger.logHelloFailed(count);
			
			if(count >= ConnectionConfig.MAX_HELLO_TRY_COUNT) {
				return false;
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
		
		Logger.logHelloSucceeded();
		return true;
		
	}
	
	private boolean helloServer(){
		this.comm.writeMsg(CommunicationConfig.MSG_HELLO_SERVER);
		
		String msg = this.comm.recvMsg();
		return msg.equals(CommunicationConfig.MSG_HELLO_SERVER_RESPONSE);
	}
	
	/**
	 * Checks whether or not is the chosen username 
	 * available (== not taken by other user).  
	 * 
	 * Sends a message to a server in a form:
	 * 1;[username]
	 * example: "1;ondra"
	 * 
	 * Waits for a message in a form:
	 * 1;[1 == ACK / 0 == NACK];[user ID]
	 * example: "1;1;7"
	 * (username is available and a user got an ID 7) 
	 * 
	 * @return
	 */
	public boolean checkUsernamAvailability(){
		this.clearStringBuilder();
		
		Player p = this.app.getPlayerInfo();
		String username = p.getName();
		
		this.sb.append(CommunicationConfig.REQ_USERNAME_AVAILABILITY);
		this.sb.append(CommunicationConfig.MSG_DELIMITER);
		this.sb.append(username);
		
		this.comm.writeMsg(this.sb.toString());
		
		
		String response = this.comm.recvMsg();
		this.parser.parseUsernameAvailabilityResponse(response, p);
		
		return true;
	}
	
	
	
	public Room[] requestRoomList(){
		this.clearStringBuilder();

		Room[] rooms;
		
		this.sb.append(CommunicationConfig.REQ_ROOM_LIST);
		this.comm.writeMsg(this.sb.toString());
	
		String response = this.comm.recvMsg();
		rooms = this.parser.parseRoomList(response);
		
		return rooms;
	}
	
	
	
	
	
	
	
	
	public void setConnection(Connection conn){
		this.conn = conn;
		this.comm.setConnection(this.conn);
	}
	
	private void clearStringBuilder(){
		this.sb.setLength(0);
	}
	
	
	
	
	
	
	public void updateServerStatus(){
		//comm.setSessions(getSessionInfo());
	}
	
	/**
	 * Server info – session rooms
	 */
	private Room[] getSessionInfo(){
		//return this.commParser.parseServerSessionInfo(comm.recvMsg());
		return null;
	}

	public void registerRoomSelection(int roomID, String nick){
		//int clientID = Client.getClientID();
		//this.comm.writeMsg(clientID + CommunicationConfig.MSG_DELIMITER + roomID + CommunicationConfig.MSG_DELIMITER + nick);
	}
	
	public Room waitForSessionInfo(){
		String sessionInfo = comm.recvMsg();
		//return this.commParser.parseSessionInfo(sessionInfo);
		return null;
	}
	
	public void confirmOnlinePingRq(){
		String answer = "yesido",
			   supposedQuestion = "doyoulive?",
			   question = comm.recvMsg();
		if(question.contains(supposedQuestion)) {
			this.comm.writeMsg(answer);
		} else {
			System.out.println("ERROR! Another message incoming: " + question);
		}
	}
	
	public ArrayList<Player> waitForSessionPlayerUpdate(){
		ArrayList<Player> result;
		
		//n x (cliID;cliNick;cliAdr;online?;acitve?;), kde n je pocet hracu v mistnosti
		String progress = comm.recvMsg();
		System.out.println(progress);
		
		//result = commParser.parseSessionPlayerUpdate(progress);
		result = null;
		
		System.out.println("---AKTUALIZACE HRÁČŮ---");
		for (Player p : result) System.out.println(p);
		System.out.println("-----------------------");
		
		return result;
	}
	
	public int[] waitForGameStatus(){
		String status = comm.recvMsg();
		System.out.println("game status: " + status);
		
		String[] parts = status.split(";");
		int[] result = new int[parts.length - 1];
		for (int i = 1; i < parts.length; i++) {
			result[i-1] = Integer.parseInt(parts[i]);
		}
		
		System.out.println("converted game status:");
		System.out.println(Arrays.toString(result));
		
		return result;
	}
	
	public CyclicBarrier getBarrier(){
		return this.barrier;
	}
	
	public void registerEndOfTurn(int clientID, String moves){
		//cli-id;n*(id-pole;symbol-pole;barva-pole)
		//cli-id;n*(id-pole;symbol-pole)
		//cli-id;n*(id-pole)
		
		this.comm.writeMsg(
			//roomID + CommunicationConfig.MSG_DELIMITER
			+ clientID + CommunicationConfig.MSG_DELIMITER
			+ moves
		);
	}

}



