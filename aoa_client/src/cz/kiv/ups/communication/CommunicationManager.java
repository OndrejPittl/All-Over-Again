package cz.kiv.ups.communication;

import cz.kiv.ups.application.Application;
import cz.kiv.ups.application.Connection;
import cz.kiv.ups.application.Logger;
import cz.kiv.ups.config.CommunicationConfig;
import cz.kiv.ups.game.GameMove;
import cz.kiv.ups.game.GameTurn;
import cz.kiv.ups.model.Player;
import cz.kiv.ups.model.Room;

import java.util.concurrent.LinkedBlockingQueue;


public class CommunicationManager {

    private static Logger logger = Logger.getLogger();
	
	private Application app;
	
	private Connection conn;

	private CommunicationParser parser;

	private StringBuilder sb;

    private LinkedBlockingQueue<Message> incomingMessages;

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
        this.parser = new CommunicationParser();

        this.incomingMessages = new LinkedBlockingQueue<>();
        this.outcomingMessages = new LinkedBlockingQueue<>();

        this.sb = new StringBuilder();

        this.receiver = new MessageReceiver(this.incomingMessages);
        this.sender = new MessageSender(this.outcomingMessages);
    }

    public void startService(){
        this.receiverThrd = new Thread(this.receiver);
        this.senderThrd = new Thread(this.sender);

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

    public void registerEndTurn(GameMove[] gameProgress) {
        this.sb.append(CommunicationConfig.REQ_TURN_DATA);

        if(gameProgress != null)
            for (int i = 0; i < gameProgress.length; i++) {
                GameMove m = gameProgress[i];
                this.sb.append(CommunicationConfig.MSG_DELIMITER);
                this.sb.append(m.serialize());
            }

        logger.debug("GAME PROGRESS: " + this.sb.toString());
        this.prepareMessage();
    }
	
	public void setConnection(Connection conn){
		this.conn = conn;
		this.receiver.setConnection(this.conn);
		this.sender.setConnection(this.conn);
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

    public int handleGameReqults(String msg) {
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

    /**
     * incoming:     [uid] ; [username] ; [0: offline / 1: online] ; [0: not active / 1: active] ;  ...
     *                 5   ;   ondra    ;             1            ;                1            ;  ...
     */
    public Player[] handlePlayerList(String msg) {
        return this.parser.parsePlayerList(msg);
    }
}



