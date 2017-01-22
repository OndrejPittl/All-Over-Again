package cz.kiv.ups.application;

import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import cz.kiv.ups.communication.CommunicationManager;
import cz.kiv.ups.config.LogConfig;
import cz.kiv.ups.config.ViewConfig;
import cz.kiv.ups.game.GameMove;
import cz.kiv.ups.game.GameTurn;
import cz.kiv.ups.game.GameType;
import javafx.application.Platform;
import cz.kiv.ups.model.Error;
import cz.kiv.ups.model.GameStatus;
import cz.kiv.ups.model.Player;
import cz.kiv.ups.model.Room;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class Application {

	private static Application instance = null;

    private static Logger logger = Logger.getLogger();

	/**
	 * Connection.
	 */
	private Connection conn;
	private CommunicationManager comm;


	private static GameStatus prevStatus;
	private static GameStatus status;

	private static CyclicBarrier guiBarrier;
	private static CyclicBarrier barrier;



    private ArrayList<Error> errors;

	private  Player currentPlayer;

	private  Room[] rooms;

	private  Room selectedRoom = null;

    private GameTurn turn;

    private int winnerID;

    private boolean waitingAskResult = false;

    private static boolean disconnecting = false;



	/**
	 * 
	 */
	private Application(){
        Application.guiBarrier = new CyclicBarrier(2);
        Application.barrier = new CyclicBarrier(2);
		this.init();
	}

    /**
     *
     * @return
     */
    public static Application getInstance(){
        if(Application.instance == null)
            Application.instance = new Application();
        return Application.instance;
    }

    /**
     *
     */
	private void init(){
	    Application.changeStatus(GameStatus.HELLO_AUTHORIZATION);
        this.errors = new ArrayList<>();
        this.winnerID = -1;
	}


	public synchronized boolean updateRoomList(String msg){
        Room[] rooms = this.comm.handleRoomList(msg);

        if(rooms == null)
            return false;

        this.setRooms(rooms);
        return true;
    }

    public synchronized void selectRoom(Room r){
        this.selectedRoom = r;
    }

    public synchronized void selectUpdateRoom(Room r){
        this.selectedRoom = r;
        this.selectedRoom.setCurrentPlayer(this.currentPlayer);
    }


	public void setDependencies(Connection conn, CommunicationManager comm){
		this.conn = conn;
		this.comm = comm;
	}


	/**
	 * @return the rooms
	 */
	public Room[] getRooms() {
		return rooms;
	}

	/**
	 * @param rooms the rooms to set
	 */
	public void setRooms(Room[] rooms) {
		this.rooms = rooms;
	}

    public static void awaitAtGuiBarrier(String str){
	    if(Application.guiBarrier == null){
            //Thread.currentThread().interrupt();
            return;
        }

        if(LogConfig.DEVELOPER_MODE) {
            String status = Application.guiBarrier.getNumberWaiting() == 0 ? "waiting" : "released";
            logger.debug("########## BARRIER_GUI (" + status + "): " + str);
        }

        try {
            Application.guiBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            Application.barrier = null;
            Thread.currentThread().interrupt();
        }
    }

    public static void awaitAtBarrier(String str){
        if(Application.barrier == null){
            //Thread.currentThread().interrupt();
            return;
        }

        if(LogConfig.DEVELOPER_MODE) {
            String status = Application.barrier.getNumberWaiting() == 0 ? "waiting" : "released";
            logger.debug("###### BARRIER (" + status + "): " + str);
        }

        try {
            Application.barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            Application.barrier = null;
            Thread.currentThread().interrupt();
        }
    }

    public synchronized Room getSelectedRoom(){
	    return this.selectedRoom;
    }

    public synchronized void proceedEndTurn() {
        if(this.amIActive())
            this.comm.registerEndTurn(this.turn.getMoves());
    }

    public synchronized void forceEndTurn() {
        this.comm.registerEndTurn(null);
    }

    public synchronized boolean amIActive(){
    	    return this.turn.getActivePlayerID() == this.currentPlayer.getID();
    }

	public synchronized void storeProgress(ArrayList<GameMove> gameProgress){
		this.turn.setMoves(gameProgress);
	}

    public synchronized int getTurnTime() {
        return this.turn.getTime();
    }

    public synchronized int getTurn(){
	    return this.getSelectedRoom().getTurn() - 1;
    }

	public synchronized void signIn(Player player){
		this.currentPlayer = player;
	}

	public synchronized Player getPlayerInfo(){
		return this.currentPlayer;
	}

	public synchronized void registerError(Error err){
	    this.errors.add(err);
    }

    public synchronized void clearErrors(){
        this.errors.clear();
    }

    public ArrayList<Error> getErrors() {
        ArrayList<Error> errs = new ArrayList<>();

        for (Error e : this.errors) {
            errs.add(e);
        }

        return errs;
    }

	private synchronized boolean amIWinner() {
		 return this.currentPlayer.getID() == this.winnerID;
	}

	public synchronized String getWinnerText() {

        if(this.selectedRoom.getType() == GameType.SINGLEPLAYER) {
            return ViewConfig.MSG_GAME_END;
        }

        if(this.amIWinner()) {
            return ViewConfig.MSG_GAME_WIN;
        }

        return ViewConfig.MSG_GAME_LOSE;
	}

    public synchronized GameTurn getGameTurn(){
        return this.turn;
    }

    public synchronized void setGameTurn(GameTurn turn){
        this.turn = turn;
    }

    public static synchronized void disconnect(boolean hard, String message){
        if(Application.disconnecting)
            return;

        Application.disconnecting = true;
        Connection.disconnect();

        if(message != null) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR, message , ButtonType.OK);
                alert.showAndWait();

                if(hard) {
                    Platform.exit();
                    System.exit(1);
                }
            });

            if(hard)
                try {
                    new CyclicBarrier(2).await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            return;
        }


        if(hard) {
            Platform.exit();
            System.exit(1);
        }
    }

    public static GameStatus getStatus(){
        return Application.status;
    }

    public static void changeStatus(GameStatus status){
        logger.debug("+++ APP STATUS: " + status);
        Application.updatePrevStatus();
        Application.status = status;
    }

    public static void updatePrevStatus(){
        logger.debug("(previous status: " + Application.status + ")");
        Application.prevStatus = Application.status;
    }

    public static GameStatus getPrevStatus(){
        return Application.prevStatus;
    }

    public void requestCreateRoom(){
        this.comm.requestNewGame(this.getSelectedRoom());
    }

    public void requestJoinRoom(){
        this.comm.requestJoinGame(this.getSelectedRoom());
    }

    public boolean handleRoomSelection(String msg){
        Room selected = this.comm.handleRoomSelection(msg);

        if(selected == null)
            return false;

        this.selectUpdateRoom(selected);
        return true;
    }

    public boolean handleUsernameSelection(String msg){
        Player player = this.getPlayerInfo();

        player = this.comm.checkUsernameAvailability(msg, player);

        if(player == null) {
            return false;
        }

        this.signIn(player);
        return true;
    }

    public synchronized boolean handleTurnStart(String msg){
        int diff, turn;
        GameTurn t;

        diff = this.getSelectedRoom().getDifficulty().getDifficulty();
        t = this.comm.handleTurnData(msg, diff);

        if(t == null)
            return false;

        this.setGameTurn(t);
        turn = this.turn.getTurn();
        this.getSelectedRoom().setTurn(turn);

        logger.info("Start of turn " + turn + " processed.");

        return true;
    }

    public synchronized void handleGameResults(String msg) {
        this.winnerID = this.comm.handleGameReqults(msg);
    }

    public synchronized void requestRoomList() {
        this.comm.requestRoomList();
    }

    public synchronized void requestRoomListAndWait(){
        Room[] rooms  = this.comm.requestRoomListAndWait();

        if(rooms != null)
            this.setRooms(rooms);
    }

    /**
     * true: everything ok
     * false: an opponent is offline / has left the room
     */
    public synchronized boolean handlePlayerList(String msg) {
        if(this.getSelectedRoom().getType() == GameType.SINGLEPLAYER)
            return true;

        Player[] players = this.comm.handlePlayerList(msg);
        Room room = this.getSelectedRoom();

        room.updatePlayers(players);
        this.selectRoom(room);

        if (!this.isOpponentInGame()) {

            // a player has left room
            return false;

        } else {

            // the opponent is in game
            Player opponent = null;
            for (Player p : players) {
                if(p.getID() != this.currentPlayer.getID())
                    opponent = p;
            }

            if(opponent == null) {
                return false;
            }

            // true: opponent online, false: offline
            return room.isOpponentOnline();
        }
    }

    public synchronized boolean isOpponentOnline() {
        return this.getSelectedRoom().isOpponentOnline();
    }

    public synchronized boolean isOpponentInGame() {
        int playerLimit = this.getSelectedRoom().getType().getPlayerCount(),
            playerCount = this.getSelectedRoom().getPlayerCount();
        return playerCount == playerLimit;
    }

    public synchronized boolean isWaitingAskResult() {
        return waitingAskResult;
    }

    public synchronized void setWaitingAskResult(boolean waitingAskResult) {
        this.waitingAskResult = waitingAskResult;
    }

    public synchronized Player getCurrentPlayer() {
        return currentPlayer;
    }
}
