package application;

import java.util.Observable;

import javafx.application.Platform;

public class GUIController extends Observable implements Runnable {

	private Application app;
	
	private Screen gui;
	
	
	
	public GUIController(Application app) {
		this.app = app;
	}

	@Override
	public void run() {
		
		//this.gui.runConnecting();

		//GUIController, WAIT: at "Connecting..." scene for a connection to a server.
		Application.awaitAtClientBarrier("GUIControl – waits for connection established (1GCWC)");

		//Application.awaitAtClientBarrier("GUIControl releases after init.");
        Application.awaitAtGuiBarrier("GUIControl waits for GUI init.");

		do {

			//Display "Enter username scene"
			this.gui.runLogin();

			Application.awaitAtGuiBarrier("GUIControl – waits for entering username. (4GCWG)");

			this.gui.runChecking();

			Application.awaitAtClientBarrier("GUIControl releases. Username entered. (6GCRC)");

			Application.awaitAtClientBarrier("GUIControl wsaits for checking username. (7GCWC)");

		} while(!this.app.isPlayerRegistered());
		
		
		Application.awaitAtClientBarrier("GUIControl waits for room list. (8_2GCWC)");
		
		
		this.gui.runGameCenter();
		
		
		Application.awaitAtGuiBarrier("GUIControl waits for user room selection/creation. (10GCWG)");

        this.gui.runConnecting();

		Application.awaitAtClientBarrier("GUIControl releases after room selection/creation. (12GCRC)");

        Application.awaitAtClientBarrier("GUIControl waits for room selection/creation response. (13GCWC)");

		this.gui.runWaiting();

        Application.awaitAtClientBarrier("GUIControl waits for game initialization. (15GCWC)");

        this.gui.runGamePlayground();

        Application.awaitAtClientBarrier("GUIControl waits for game start. (17GCWC)");

        // player plays a turn
        // tmp wait
        Application.awaitAtGuiBarrier("GUIControl TEMPORARILY waits.");
		
		
		
		
		_Developer.threadExecEnds("GUIController");
		
	}
	
	


	
	public void setScreen(Screen gui){
		this.gui = gui;
	}

}
