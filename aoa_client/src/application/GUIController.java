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

		//GUIController, WAIT: at "Connecting..." scene for a connection to a server.
		Application.awaitAtClientBarrier("GUI-control – waits for connection established (G1W)");
		
		//Display "Enter username scene"
		this.gui.runLogin();

		
		Application.awaitAtGuiBarrier("GUI-control – waits for user interaction.");
		
		
		// (GUI, RELEASE: –> Client checks username)
		 
		//wait
		//Application.awaitAtClientBarrier("CLI waits for gui thread (C2W)");

		
		
		
		
		_Developer.threadExecEnds("GUIController");
		
	}
	
	


	
	public void setScreen(Screen gui){
		this.gui = gui;
	}

}
