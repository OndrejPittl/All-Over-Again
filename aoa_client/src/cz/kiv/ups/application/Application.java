package cz.kiv.ups.application;

import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import cz.kiv.ups.communication.CommunicationManager;
import cz.kiv.ups.config.ViewConfig;
import cz.kiv.ups.game.GameMove;
import cz.kiv.ups.game.GameTurn;
import cz.kiv.ups.game.GameType;
import javafx.application.Platform;
import cz.kiv.ups.model.Error;
import cz.kiv.ups.model.GameStatus;
import cz.kiv.ups.model.Player;
import cz.kiv.ups.model.Room;

public class Application {

	private static Application instance = null;

	/**
	 * Connection.
	 */
	private Connection conn;

	private CommunicationManager comm;



	private static GameStatus prevStatus;
	private static GameStatus status;


	private static CyclicBarrier clientBarrier;

	private static CyclicBarrier guiBarrier;

	private static CyclicBarrier barrier;



    private ArrayList<Error> errors;

	private  Player currentPlayer;

	private  Room[] rooms;

	private  Room selectedRoom = null;

    private GameTurn turn;

    private int winnerID;


    private static java.util.logging.Logger logger;




    //private boolean signedIn = false;
//    private boolean gameStarted = false;
//    private boolean gameFinished = false;
//    private boolean exitingGame = false;
    //private boolean turnDataOK = true;




	
	
	/**
	 * 
	 */
	private Application(){
		Application.clientBarrier = new CyclicBarrier(2);
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
//	    this.setGameFinished(false);
        this.errors = new ArrayList<>();
        this.winnerID = -1;
        this.logger = java.util.logging.Logger.getLogger(getClass().getName());
        //this.signedIn = false;
	}

    /**
     *
     */
//    public void resetGame(){
//	    this.setExitingGame(false);
//        this.setGameStarted(false);
//        this.setGameFinished(false);
//        this.winnerID = -1;
//    }


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


//    public void handleSignIn(){
//		//check nickname availability
//		this.comm.checkUsernameAvailability();
//    }

//    public void requestCreateJoinRoom(){
//        Room selected;
//
//        if(this.getSelectedRoom().hasID()) {
//
//            //join game
//            // this.selectRoom(this.rooms[index]);
//            selected = this.comm.joinGame(this.getSelectedRoom());
//            this.selectRoom(selected);
//
//        } else {
//
//            //new game
//            selected = this.comm.requestNewGame(this.getSelectedRoom());
//            this.selectRoom(selected);
//        }
//    }







	public void setDependencies(Connection conn, CommunicationManager comm){
		this.conn = conn;
		this.comm = comm;
	}

	public Connection getConnection() {
		return conn;
	}

	public CommunicationManager getCommunicationManager() {
		return comm;
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

	public static void awaitAtClientBarrier(String str){
		String status = Application.clientBarrier.getNumberWaiting() == 0 ? "waiting" : "released";

//        Application.logger.info("### BARRIER_CLI (" + status + "): " + str);
        System.out.println("### BARRIER_CLI (" + status + "): " + str);

		
		try {
			Application.clientBarrier.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
	}

    public static void awaitAtGuiBarrier(String str){
	    String status = Application.guiBarrier.getNumberWaiting() == 0 ? "waiting" : "released";

//        Application.logger.info("### BARRIER_GUI (" + status + "): " + str);
        System.out.println("################# BARRIER_GUI (" + status + "): " + str);

        try {

            Application.guiBarrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    public static void awaitAtBarrier(String str){
        String status = Application.barrier.getNumberWaiting() == 0 ? "waiting" : "released";

        System.out.println("################## BARRIER (" + status + "): " + str);
//        Application.logger.info("### BARRIER_GUI (" + status + "): " + str);

        try {
            Application.barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    public synchronized Room getSelectedRoom(){
	    return this.selectedRoom;
    }

    public synchronized void proceedEndTurn() {
	    if(this.amIActive())
            this.comm.registerEndTurn(this.turn.getMoves());
    }

    public synchronized boolean amIActive(){
	    return this.turn.getActivePlayerID() == this.currentPlayer.getID();
    }

    public synchronized GameMove[] getProgress() {
        return this.turn.getMoves();
    }

	public synchronized void storeProgress(ArrayList<GameMove> gameProgress){
		this.turn.setMoves(gameProgress);
	}

    public synchronized int getTurnTime() {
        return this.turn.getTime();
    }

    public synchronized int getActivePlayerID() {
        return this.turn.getActivePlayerID();
    }

    public synchronized int getTurn(){
	    return this.getSelectedRoom().getTurn() - 1;
    }

//    public synchronized void setGameFinished(boolean finished){
//		this.gameFinished = finished;
//	}



	public synchronized void signIn(Player player){
		this.currentPlayer = player;
	}

	public synchronized Player getPlayerInfo(){
		return this.currentPlayer;
	}


    public synchronized boolean isSignedIn() {
        return this.currentPlayer != null && this.currentPlayer.hasID();
    }

	public synchronized void signOut(){
	    this.currentPlayer = null;
    }

//	public synchronized boolean isGameFinished() {
//		return gameFinished;
//	}

    public synchronized boolean isRoomJoined(){
        return this.getSelectedRoom() != null;
    }

//	public synchronized boolean isGameStarted() {
//		return gameStarted;
//	}
//
//	public synchronized void setGameStarted(boolean started){
//        this.gameStarted = started;
//    }

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

//	public synchronized boolean isTurnDataOK(){
//		return this.turnDataOK;
//	}
//
//	public synchronized void setTurnDataOK(boolean ok){
//		this.turnDataOK = ok;
//	}



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

//    public synchronized boolean isExitingGame() {
//        return exitingGame;
//    }
//
//    public synchronized void setExitingGame(boolean exitingGame) {
//        this.exitingGame = exitingGame;
//    }

    public synchronized void disconnectRoom() {
        this.selectRoom(null);
    }


    public void restartGame(){
        this.comm.handleRestartGame();
    }


    public void leaveGame() {
        this.comm.handleLeaveGame();
    }

    public synchronized GameTurn getGameTurn(){
        return this.turn;
    }

    public synchronized void setGameTurn(GameTurn turn){
        this.turn = turn;
    }

    public static void disconnect(boolean hard){
        Connection.disconnect();

        if(hard) {
            Platform.exit();
            System.exit(1);
        }

    }

    public static GameStatus getStatus(){
        return Application.status;
    }

    public static void changeStatus(GameStatus status){
        System.out.println("+++ APP STATUS: " + status);
        Application.updatePrevStatus();
        Application.status = status;
    }

    public static void updatePrevStatus(){
        System.out.println("(previous status: " + Application.status + ")");
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

        this.selectRoom(selected);
        return true;
    }

    public void requestNewGame(){
        this.comm.requestNewGame(this.getSelectedRoom());
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


        System.out.println("HANDLIIIIIIIIING TURN START: " + turn);

        return true;
    }

    public synchronized void handleGameResults(String msg) {
        this.winnerID = this.comm.handleGameReqults(msg);
    }

    public void requestRoomList() {
        this.comm.requestRoomList();
    }

    public void requestRoomListAndWait(){
        Room[] rooms  = this.comm.requestRoomListAndWait();

        if(rooms != null)
            this.setRooms(rooms);
    }
}
