package application;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import communication.CommunicationManager;
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




	private  Player player;

	private  Room[] rooms;

	private  Room selectedRoom;

	private int[][] progress;

//	private static Screen gui;
//	private static Client cli;

	
	
	/**
	 * 
	 */
	private Application(){
		this.clientBarrier = new CyclicBarrier(2);
		this.guiBarrier = new CyclicBarrier(2);
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

	public void updateRoomList(){
        this.rooms = this.comm.requestRoomList();
    }

    public void selectRoom(Room r){
	    this.selectedRoom = r;
    }

    public void handleSignIn(){
        do {
            //wait for entering username
            Application.awaitAtClientBarrier("CLI waits for username. (3CWC)");

            //check nickname availability
            this.comm.checkUsernameAvailability();

            Application.awaitAtClientBarrier("CLI releases. Username checked. (8CRC)");

        } while(!this.isPlayerRegistered());
    }

    public boolean requestCreateJoinRoom(){
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

        return selected != null;
    }

    public boolean waitForGameInit(){
        return this.comm.waitGameInitComplete();
    }

    public void waitForTurnStart(){
        this.progress = this.comm.waitForTurn();
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
	
	public void registerPlayer(Player player){
		this.player = player;
	}
	
	public Player getPlayerInfo(){
		return this.player;
	}
	
	public boolean isPlayerRegistered(){
		return this.player.hasID();
	}
	
	
	
	
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



	public Room getSelectedRoom(){
	    return this.selectedRoom;
    }

}
