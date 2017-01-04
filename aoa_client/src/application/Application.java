package application;

import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import communication.CommunicationManager;
import game.GameMove;
import game.GameTurn;
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



	private boolean gameFinished;

	private  Player player;

	private  Room[] rooms;

	private  Room selectedRoom;

	//private GameMove[] progress;
    private GameTurn turn;


    private boolean isJoinedRoom = false;
    private boolean isGameStarted = false;
    private boolean isSignedIn = false;


//	private static Screen gui;
//	private static Client cli;

	
	
	/**
	 * 
	 */
	private Application(){
		this.clientBarrier = new CyclicBarrier(2);
		this.guiBarrier = new CyclicBarrier(2);
		this.init();
	}

	private void init(){
		this.gameFinished = false;
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

        if(this.selectedRoom.hasID()) {

            //join game
            // this.selectRoom(this.rooms[index]);
            selected = this.comm.joinGame(this.selectedRoom);
            this.selectRoom(selected);

        } else {

            //new game
            // this.selectRoom(this.comm.newGame(this.selectedRoom));
            selected = this.comm.newGame(this.selectedRoom);
            this.selectRoom(selected);
        }

        this.isJoinedRoom = selected != null;
    }




    public void waitForGameInit(){
		this.isGameStarted = this.comm.waitGameInitComplete();
    }

    public void waitForTurnStart(){
        this.turn = this.comm.waitForTurn();
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

	//	public void setGUI(Screen gui){
//		this.gui = gui;
//	}
//	
//	public void setClient(Client cli){
//		this.cli = cli;
//	}
	

	
//	public Screen getScreen(){
//		return this.gui;
//	}
	
//	public static CyclicBarrier getBarrier(){
//		return Application.clientBarrier;
//	}
	


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
        this.comm.registerEndTurn(this.turn.getMoves());
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

    public synchronized void setGameFinished(){
		this.gameFinished = true;
	}




	public synchronized void registerPlayer(Player player){
		this.player = player;
	}

	public synchronized Player getPlayerInfo(){
		return this.player;
	}

	public synchronized boolean isPlayerRegistered(){
		return this.player.hasID();
	}

	public synchronized boolean isGameFinished() {
		return gameFinished;
	}

	public synchronized boolean isRoomJoined(){
    	return this.isJoinedRoom;
	}

	public synchronized boolean isGameStarted() {
		return isGameStarted;
	}

	public synchronized boolean isSignedIn() {
		return isSignedIn;
	}
}
