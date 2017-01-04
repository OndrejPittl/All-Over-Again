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

            do {

                //Display "Enter username scene"
                this.gui.runLogin();

                Application.awaitAtGuiBarrier("GUIControl – waits for entering username. (4GCWG)");

                this.gui.runChecking();

                Application.awaitAtClientBarrier("GUIControl releases. Username entered. (6GCRC)");

                Application.awaitAtClientBarrier("GUIControl waits for checking username. (7GCWC)");

            } while (!this.app.isPlayerRegistered());



            do {

                do {

                    Application.awaitAtClientBarrier("GUIControl waits for room list. (8_2GCWC)");

                    this.gui.runGameCenter();

                    Application.awaitAtGuiBarrier("GUIControl waits for user room selection/creation. (10GCWG)");

                    this.gui.runConnecting();

                    Application.awaitAtClientBarrier("GUIControl releases after room selection/creation. (12GCRC)");

                    Application.awaitAtClientBarrier("GUIControl waits for room selection/creation response. (13GCWC)");

                } while (!this.app.isRoomJoined());


                this.gui.runWaiting();

                Application.awaitAtClientBarrier("GUIControl waits for game initialization. (15GCWC)");


                // TODO: zkontrolovat!!!
                if (!this.app.isGameStarted()) {
                    continue;
                }


                this.gui.runGamePlayground();

                Application.awaitAtGuiBarrier("GUIControl waits for board initialization.");



                //---- cycle:
                do {

                    Application.awaitAtClientBarrier("GUIControl waits for turn start. (17GCWC)");

                    // new turn has begun
                    this.gui.beginTurn();
                    // player plays a turn

                    Application.awaitAtGuiBarrier("GUIControl waits for a player interaction / turn ends.");

                    Application.awaitAtClientBarrier("GUIControl releases after turn ends.");

                } while(!this.app.isGameFinished());


                // game results


            } while(this.app.isSignedIn());




        } while(!this.app.isSignedIn());

		
		
		_Developer.threadExecEnds("GUIController");
		
	}
	
	


	
	public void setScreen(Screen gui){
		this.gui = gui;
	}

}
