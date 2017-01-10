package application;

import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import communication.CommunicationManager;
import config.ViewConfig;
import game.GameMove;
import game.GameTurn;
import game.GameType;
import model.Error;
import model.Player;
import model.Room;

public class Application {

	private static Application instance = null;

	/**
	 * Connection.
	 */
	private Connection conn;

	private CommunicationManager comm;


	private static CyclicBarrier clientBarrier;

	private static CyclicBarrier guiBarrier;



    private ArrayList<Error> errors;

	private  Player player;

	private  Room[] rooms;

	private  Room selectedRoom = null;

	//private GameMove[] progress;
    private GameTurn turn;

    private int winnerID;

    //private boolean signedIn = false;
    private boolean gameStarted = false;
    private boolean gameFinished = false;
    private boolean exitingGame = false;
    //private boolean turnDataOK = true;

	
	
	/**
	 * 
	 */
	private Application(){
		Application.clientBarrier = new CyclicBarrier(2);
        Application.guiBarrier = new CyclicBarrier(2);
		this.init();
	}

	private void init(){
	    this.setGameFinished(false);
        this.errors = new ArrayList<>();
        //this.signedIn = false;
	}

    public void resetGame(){
	    this.setExitingGame(false);
        this.setGameStarted(false);
        this.setGameFinished(false);
        this.winnerID = -1;
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

	public synchronized void updateRoomList(){
        this.rooms = this.comm.requestRoomList();
    }

    public synchronized void selectRoom(Room r){
	    this.selectedRoom = r;
    }


    public void handleSignIn(){
		//check nickname availability
		this.comm.checkUsernameAvailability();
    }

    public void requestCreateJoinRoom(){
        Room selected;

        if(this.getSelectedRoom().hasID()) {

            //join game
            // this.selectRoom(this.rooms[index]);
            selected = this.comm.joinGame(this.getSelectedRoom());
            this.selectRoom(selected);

        } else {

            //new game
            selected = this.comm.newGame(this.getSelectedRoom());
            this.selectRoom(selected);
        }
    }


    public void waitForGameInit(){
        this.setGameStarted(this.comm.waitGameInitComplete());
    }

    public void waitForTurnStart(){
        this.turn = this.comm.waitForTurn();
        if(this.turn != null) this.getSelectedRoom().setTurn(this.turn.getTurn());
    }


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
		
		System.out.println("### BARRIER_GUI (" + status + "): " + str);
		
		try {
			Application.guiBarrier.await();
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
	    return this.turn.getActivePlayerID() == this.player.getID();
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

    public synchronized void setGameFinished(boolean finished){
		this.gameFinished = finished;
	}



	public synchronized void signIn(Player player){
		this.player = player;
	}

	public synchronized Player getPlayerInfo(){
		return this.player;
	}


    public synchronized boolean isSignedIn() {
        return this.player != null && this.player.hasID();
    }

	public synchronized void signOut(){
	    this.player = null;
    }

	public synchronized boolean isGameFinished() {
		return gameFinished;
	}

    public synchronized boolean isRoomJoined(){
        return this.getSelectedRoom() != null;
    }

	public synchronized boolean isGameStarted() {
		return gameStarted;
	}

	public synchronized void setGameStarted(boolean started){
        this.gameStarted = started;
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

        this.clearErrors();

        return errs;
    }

//	public synchronized boolean isTurnDataOK(){
//		return this.turnDataOK;
//	}
//
//	public synchronized void setTurnDataOK(boolean ok){
//		this.turnDataOK = ok;
//	}

	public synchronized void waitForGameResults() {
		this.winnerID = this.comm.waitForResults();
	}

	private synchronized boolean amIWinner() {
		 return this.player.getID() == this.winnerID;
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

    public synchronized boolean isExitingGame() {
        return exitingGame;
    }

    public synchronized void setExitingGame(boolean exitingGame) {
        this.exitingGame = exitingGame;
    }

    public synchronized void disconnectRoom() {
        this.selectRoom(null);
    }


    public void restartGame(){
        this.comm.restartGame();
    }


    public void leaveGame() {
        this.comm.leaveGame();
    }

    public synchronized GameTurn getGameTurn(){
        return this.turn;
    }

}
