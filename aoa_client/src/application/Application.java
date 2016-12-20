package application;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import model.Player;
import model.Room;

public class Application {

	private static Application instance = null;
	
	private  Player player;
	
	private  Room[] rooms;
	
//	private static Screen gui;

//	private static Client cli;
	
	private static CyclicBarrier clientBarrier;
	
	private static CyclicBarrier guiBarrier;
	
	
	
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


}
