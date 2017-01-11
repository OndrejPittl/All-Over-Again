package communication;
import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;

import application.Application;
import application.Connection;
import config.CommunicationConfig;
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

    private LinkedBlockingQueue<Message> incomingMessages;

    private LinkedBlockingQueue<Message> incomingMessagesAsync;

    private LinkedBlockingQueue<Message> outcomingMessages;


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
        this.incomingMessagesAsync = new LinkedBlockingQueue<>();
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


    public Message receiveMessage(){
        try {
            return this.incomingMessages.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }




    public void handleRestartGame(){
        this.sb.append(CommunicationConfig.REQ_GAME_START);
        this.prepareMessage();
    }

    public void handleLeaveGame(){
        this.sb.append(CommunicationConfig.REQ_GAME_LEAVE);
        this.prepareMessage();
    }

//    private Room waitForRoomInfo(){
//        Room r;
//        Message m;
//
//        do {
//
//            try {
//                m = this.incomingMessages.take();
//
//                System.out.println("********* RECEIVED: " + m.getMessage());
//
//                r = this.parser.parseSelectedRoom(m.getMessage());
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                return null;
//            }
//
//            if(r != null) break;
//
//        } while (!this.incomingMessages.isEmpty());
//
//        return r;
//    }






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












    public void sendHelloServer(){
        this.sb.append(CommunicationConfig.MSG_HELLO_SERVER);
        this.prepareMessage();
    }

    public boolean checkHelloPacket(String msg) {
        return msg.contains(CommunicationConfig.MSG_HELLO_SERVER_RESPONSE) && msg.length() == CommunicationConfig.MSG_HELLO_SERVER_RESPONSE.length();
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
    public void sendUsernameRequest() {
        Player p = this.app.getPlayerInfo();
        String username = p.getName();

        this.sb.append(CommunicationConfig.REQ_SIGN_IN);
        this.sb.append(CommunicationConfig.MSG_DELIMITER);
        this.sb.append(username);
        this.prepareMessage();
    }

    public Player checkUsernameAvailability(String msg, Player player){
        return this.parser.parseUsernameAvailabilityResponse(msg, player);
    }

    public void requestRoomList(){
        this.sb.append(CommunicationConfig.REQ_GAME_LIST);
        this.prepareMessage();
    }

    public Room[] handleRoomList(String msg){
        return this.parser.parseRoomList(msg);
    }

    public void requestJoinGame(Room selection){
        this.sb.append(CommunicationConfig.REQ_GAME_JOIN);
        this.sb.append(CommunicationConfig.MSG_DELIMITER);
        this.sb.append(selection.getID());
        this.prepareMessage();
    }

    public void requestNewGame(Room selection){
        this.sb.append(CommunicationConfig.REQ_GAME_NEW);
        this.sb.append(CommunicationConfig.MSG_DELIMITER);
        this.sb.append(selection.getType().getPlayerCount());
        this.sb.append(CommunicationConfig.MSG_DELIMITER);
        this.sb.append(selection.getDifficulty().getDifficulty());
        this.sb.append(CommunicationConfig.MSG_DELIMITER);
        this.sb.append(selection.getBoardDimension().getDimension());
        this.prepareMessage();
    }

    public Room handleRoomSelection(String msg){
        return this.parser.parseSelectedRoom(msg);
    }

    public boolean handleGameInit(String msg){
        return this.parser.parseGameInitResult(msg);
    }

    public GameTurn handleTurnData(String msg, int diff){
        return this.parser.parseTurnInfo(msg, diff);
    }

    public int waitForResults(String msg) {
        return this.parser.parseResults(msg);
    }

    public void handleSignOut(){
        this.sb.append(CommunicationConfig.REQ_SIGN_OUT);
        this.prepareMessage();
    }


    public Room[] requestRoomListAndWait(){
        this.sb.append(CommunicationConfig.REQ_GAME_LIST);
        this.prepareMessage();

        try {
            Message m = this.incomingMessages.take();
            return this.parser.parseRoomList(m.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}



