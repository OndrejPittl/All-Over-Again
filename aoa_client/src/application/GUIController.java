package application;

import java.util.Observable;

import javafx.application.Platform;

public class GUIController extends Observable implements Runnable {

	private Application app;
	
	private Screen gui;
	
	
	
	public GUIController() {
		this.app = Application.getInstance();
	}

	@Override
	public void run() {
		
		//this.gui.runConnecting();

		//GUIController, WAIT: at "Connecting..." scene for a connection to a server.
		Application.awaitAtClientBarrier("GUIControl – waits for connection established (1GCWC)");

		
//		do {
//			
//			//Display "Enter username scene"
//			this.gui.runLogin();
//	
//			Application.awaitAtGuiBarrier("GUIControl – waits for entering username. (4GCWG)");
//			
//			this.gui.runChecking();
//			
//			Application.awaitAtClientBarrier("GUIControl releases. Username entered. (6GCRC)");
//			
//			Application.awaitAtClientBarrier("GUIControl wsaits for checking username. (7GCWC)");
//		
//		} while(!this.app.isPlayerRegistered());
		
		
		Application.awaitAtClientBarrier("GUIControl waits for room list. (8_2GCWC)");
		
		
		this.gui.runGameCenter();
		
		
		Application.awaitAtGuiBarrier("GUIControl waits for user room selection/creation. (10GCWG)");
		
		
		
		
		
		// (GUI, RELEASE: –> Client checks username)
		 
		//wait
		//Application.awaitAtClientBarrier("CLI waits for gui thread (C2W)");

		
		
		
		
		_Developer.threadExecEnds("GUIController");
		
	}
	
	


	
	public void setScreen(Screen gui){
		this.gui = gui;
	}

}
