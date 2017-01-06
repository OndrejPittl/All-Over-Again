package communication;
import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;

import application.Application;
import application.Connection;
import application.Logger;
import config.CommunicationConfig;
import config.ConnectionConfig;
import game.GameMove;
import game.GameTurn;
import model.Player;
import model.Room;


public class CommunicationManager {
	
	private Application app;
	
	private Connection conn;
	
	private Communicator comm;
	
	private CommunicationParser parser;
	
	private CyclicBarrier barrier;
	
	private StringBuilder sb;

    LinkedBlockingQueue<Message> incomingMessages;

    LinkedBlockingQueue<Message> outcomingMessages;


    private MessageReceiver receiver;
    private Thread receiverThrd;

    private MessageSender sender;
    private Thread senderThrd;


    public CommunicationManager(Application app) {
        this.app = app;
		this.init();
	}

	private void init(){
        this.comm = new Communicator();
        this.parser = new CommunicationParser();

        // this.incomingMessages = new ConcurrentLinkedQueue<>();
        this.incomingMessages = new LinkedBlockingQueue<>();
        this.outcomingMessages = new LinkedBlockingQueue<>();

        this.barrier = new CyclicBarrier(2);
        this.sb = new StringBuilder();

        this.receiver = new MessageReceiver(this.conn, this.incomingMessages);
        this.receiverThrd = new Thread(this.receiver);

        this.sender = new MessageSender(this.conn, this.outcomingMessages);
        this.senderThrd = new Thread(this.sender);
    }

    public void startService(){
        this.receiverThrd.start();
        this.senderThrd.start();
    }

    private void prepareMessage(){
        this.outcomingMessages.add(new Message(this.sb.toString()));
        this.clearStringBuilder();
    }

    /**
     *
     * @return
     */
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
        boolean result = false;
        Message m;

        this.sb.append(CommunicationConfig.MSG_HELLO_SERVER);
        this.prepareMessage();

        do {

            try {

                // Expected message: answer to handshake, all other messages are being ignored.
                m = this.incomingMessages.take();
                result = this.receiver.checkHelloPacket(m.getMessage());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(result) break;

        } while (!this.incomingMessages.isEmpty());

        return result;
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
	public boolean checkUsernameAvailability() {
	    Player pResult = null;
        Message m;

		Player p = this.app.getPlayerInfo();
		String username = p.getName();

		this.sb.append(CommunicationConfig.REQ_SIGN_IN);
		this.sb.append(CommunicationConfig.MSG_DELIMITER);
		this.sb.append(username);
		this.prepareMessage();

		do {

            try {
                m = this.incomingMessages.take();
                pResult = this.parser.parseUsernameAvailabilityResponse(m.getMessage(), p);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(pResult != null) {
                this.app.registerPlayer(pResult);
                break;
            }

        } while (!this.incomingMessages.isEmpty());

		return pResult != null;
	}

	public Room[] requestRoomList(){
	    Room[] r = null;
	    Message m;

		this.sb.append(CommunicationConfig.REQ_GAME_LIST);
	    this.prepareMessage();

        do {

            try {
                m = this.incomingMessages.take();
                r = this.parser.parseRoomList(m.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(r != null) break;

        } while (!this.incomingMessages.isEmpty());

        return r;
	}

    public Room joinGame(Room selection){
        this.sb.append(CommunicationConfig.REQ_GAME_JOIN);
        this.sb.append(CommunicationConfig.MSG_DELIMITER);
        this.sb.append(selection.getID());

        System.out.println("********* SENDING: " + this.sb.toString());


        this.prepareMessage();

        return this.waitForRoomInfo();
    }

    public Room newGame(Room selection){
        this.sb.append(CommunicationConfig.REQ_GAME_NEW);
        this.sb.append(CommunicationConfig.MSG_DELIMITER);
        this.sb.append(selection.getPlayerLimit());
        this.sb.append(CommunicationConfig.MSG_DELIMITER);
        this.sb.append(selection.getDifficulty());
        this.prepareMessage();

        return this.waitForRoomInfo();
    }

    private Room waitForRoomInfo(){
        Room r;
        Message m;

        do {

            try {
                m = this.incomingMessages.take();

                System.out.println("********* RECEIVED: " + m.getMessage());

                r = this.parser.parseSelectedRoom(m.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }

            if(r != null) break;

        } while (!this.incomingMessages.isEmpty());

        return r;
    }

	public boolean waitGameInitComplete(){
        boolean result = false;

        do {

            try {

                Message m = this.incomingMessages.take();
                result = this.parser.parseGameInitResult(m.getMessage());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // || (msgFound && !result)
            if(result) break;

        } while (!this.incomingMessages.isEmpty());

        return result;
    }

    public GameTurn waitForTurn(){
        GameTurn turn = null;

        do {
            try {
                Message m = this.incomingMessages.take();
                turn = this.parser.parseTurnInfo(m.getMessage());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(turn != null) break;

        } while (!this.incomingMessages.isEmpty());


        return turn;
    }


    public void registerEndTurn(GameMove[] gameProgress) {
        this.sb.append(CommunicationConfig.REQ_TURN_DATA);

        for (int i = 0; i < gameProgress.length; i++) {
            GameMove m = gameProgress[i];
            this.sb.append(CommunicationConfig.MSG_DELIMITER);
            this.sb.append(m.serialize());
        }

        // System.out.println("GAME PROGRESS: " + this.sb.toString());
        this.prepareMessage();
    }

	
	public void setConnection(Connection conn){
		this.conn = conn;
		this.comm.setConnection(this.conn);
		this.receiver.setConnection(this.conn);
		this.sender.setConnection(this.conn);
	}
	
//	private void clearStringBuilder(){
//		this.sb.setLength(0);
//	}
	
	
	
	
	
	
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
//		String sessionInfo = comm.recvMsg();
		//return this.commParser.parseSessionInfo(sessionInfo);
		return null;
	}
	
	public void confirmOnlinePingRq(){
//		String answer = "yesido",
//			   supposedQuestion = "doyoulive?",
//			   question = comm.recvMsg();
//		if(question.contains(supposedQuestion)) {
//			this.comm.writeMsg(answer);
//		} else {
//			System.out.println("ERROR! Another message incoming: " + question);
//		}
	}
	
	public ArrayList<Player> waitForSessionPlayerUpdate(){
		ArrayList<Player> result;
		
		//n x (cliID;cliNick;cliAdr;online?;acitve?;), kde n je pocet hracu v mistnosti
//		String progress = comm.recvMsg();
//		System.out.println(progress);
//
//		//result = commParser.parseSessionPlayerUpdate(progress);
//		result = null;
//
//		System.out.println("---AKTUALIZACE HRÁČŮ---");
//		for (Player p : result) System.out.println(p);
//		System.out.println("-----------------------");
		
//		return result;
        return null;
	}

	
	public int[] waitForGameStatus(){
//		String status = comm.recvMsg();
//		System.out.println("game status: " + status);
//
//		String[] parts = status.split(";");
//		int[] result = new int[parts.length - 1];
//		for (int i = 1; i < parts.length; i++) {
//			result[i-1] = Integer.parseInt(parts[i]);
//		}
//
//		System.out.println("converted game status:");
//		System.out.println(Arrays.toString(result));
//
//		return result;

        return null;
	}
	
	public CyclicBarrier getBarrier(){
		return this.barrier;
	}
	
	public void registerEndOfTurn(int clientID, String moves){
		//cli-id;n*(id-pole;symbol-pole;barva-pole)
		//cli-id;n*(id-pole;symbol-pole)
		//cli-id;n*(id-pole)
		
//		this.comm.writeMsg(
//			//roomID + CommunicationConfig.MSG_DELIMITER
//			+ clientID + CommunicationConfig.MSG_DELIMITER
//			+ moves
//		);
	}






    private void clearStringBuilder() {
        this.sb.setLength(0);
    }


}



