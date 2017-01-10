package application;

import java.util.Observable;

public class GUIController extends Observable implements Runnable {

	private Application app;
	
	private Screen gui;
	
	
	
	public GUIController(Application app) {
		this.app = app;
	}

	@Override
	public void run() {

        //Application.awaitAtClientBarrier("GUIControl releases after init.");
        Application.awaitAtGuiBarrier("GUIControl waits for GUI init.");

        this.gui.run();

        this.gui.runConnecting();

        this.waitAtScreen(2500);

		//GUIController, WAIT: at "Connecting..." scene for a connection to a server.
		Application.awaitAtClientBarrier("GUIControl – waits for connection established (1GCWC)");


		loopMain:
        do {

            System.out.println("....... GUIC ENTERING: MAIN LOOP!!!");

            loopLogin:
            do {

                System.out.println("....... GUIC ENTERING: LOGIN LOOP!!!");

                //Display "Enter username scene"
                this.gui.runLogin();

                Application.awaitAtGuiBarrier("GUIControl – waits for entering username. (4GCWG)");

                this.gui.runChecking();
                this.waitAtScreen(1000);

                Application.awaitAtClientBarrier("GUIControl releases. Username entered. (6GCRC)");

                Application.awaitAtClientBarrier("GUIControl waits for checking username. (7GCWC)");

            } while (!this.app.isSignedIn());


            do {

                System.out.println("....... GUIC ENTERING: noname LOOP!!!");

                loopRoomJoin:
                do {

                    System.out.println("....... GUIC ENTERING: ROOM LOOP!!!");

                    Application.awaitAtClientBarrier("GUIControl waits for room list. (8_2GCWC)");

                    this.gui.runGameCenter();

                    Application.awaitAtGuiBarrier("GUIControl waits for user room selection/creation. (10GCWG)");

                    this.gui.runConnecting();

                    Application.awaitAtClientBarrier("GUIControl releases after room selection/creation. (12GCRC)");

                    Application.awaitAtClientBarrier("GUIControl waits for room selection/creation response. (13GCWC)");

                } while (!this.app.isRoomJoined());



                loopGame:
                do {

                    System.out.println("....... GUIC ENTERING: GAME LOOP!!!");

                    this.gui.runWaiting();

                    this.waitAtScreen(1000);

                    Application.awaitAtClientBarrier("GUIControls releases after waiting screen init.");

                    Application.awaitAtClientBarrier("GUIControl waits for game initialization. (15GCWC)");

                    // TODO: zkontrolovat!!!
                    if (!this.app.isGameStarted()) {

                        continue;
                    }


                    this.gui.runGamePlayground();

                    Application.awaitAtGuiBarrier("GUIControl waits for board initialization.");


                    loopGameProgress:
                    do {

                        System.out.println("....... GUIC ENTERING: GAME-PROGRESS LOOP!!!");

                        Application.awaitAtClientBarrier("GUIControl releases after board init.");

                        Application.awaitAtClientBarrier("GUIControl waits for turn start. (17GCWC)");

                        // game over
                        //if (!this.app.isGameFinished()) {

                        System.out.println("__________ GUIC check GAME FINISHED");
                        if (this.app.isGameFinished()) {
                            System.out.println("?????? GUIC REGISTERED: GAME FINISHED!!! LEAVING GAME-PROGRESS");
                            break;
                        }

                        // new turn has begun
                        this.gui.beginTurn();
                        // player plays a turn

                        Application.awaitAtGuiBarrier("GUIControl waits for a player interaction / turn ends.");

                        Application.awaitAtClientBarrier("GUIControl releases after turn ends.");

                    } while (!this.app.isGameFinished());


                    // game results
                    Application.awaitAtClientBarrier("GUIControl waits for game results.");

                    // run results
                    this.gui.runGameResults();


                    Application.awaitAtGuiBarrier("GUIControl waits for user interaction.");

                    Application.awaitAtClientBarrier("GUIControl releases after user interaction.");


                    // game exit
                    if (this.app.isExitingGame()) {
                        System.out.println("?????? GUIC REGISTERED: GAME EXIT!!! LEAVING MAIN LOOP");
                        break loopMain;
                    }

                    // room leave
                    //if (this.app.isRoomJoined()) {}

                    //Application.awaitAtGuiBarrier("GUIControl TEMPORARILY WAITS.");

                } while (this.app.isRoomJoined());


            } while(this.app.isSignedIn());


        } while(!this.app.isSignedIn());


		_Developer.threadExecEnds("GUIController");
		
	}

	private void waitAtScreen(int milis){
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
	


	
	public void setScreen(Screen gui){
		this.gui = gui;
	}

}
