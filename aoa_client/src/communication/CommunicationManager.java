package communication;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CyclicBarrier;

import application.Connection;
import application.Logger;
import application.Room;
import config.CommunicationConfig;
import config.ConnectionConfig;
import model.Player;


public class CommunicationManager {
	
	private Connection conn;
	
	private Communicator comm;
	
	private CommunicationParser commParser;
	
	
	private CyclicBarrier barrier;
	
	
	
	public CommunicationManager() {
		this.comm = new Communicator();
		this.commParser = new CommunicationParser();
		this.barrier = new CyclicBarrier(2);
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
	
	
	
	
	
	public void setConnection(Connection conn){
		this.conn = conn;
		this.comm.setConnection(this.conn);
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



