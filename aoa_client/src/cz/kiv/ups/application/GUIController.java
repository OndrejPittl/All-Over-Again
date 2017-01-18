package cz.kiv.ups.application;

import cz.kiv.ups.model.GameStatus;

import java.util.Observable;

public class GUIController extends Observable implements Runnable {

	private Application app;
	
	private Screen gui;
	
	
	
	public GUIController(Application app) {
		this.app = app;
	}

	@Override
	public void run() {

        Application.awaitAtGuiBarrier("GUIControl waits for GUI init.");

        Application.awaitAtBarrier("GUI: releases CLI after gui init.");

        this.gui.run();

        this.gui.runConnecting();

        //this.waitAtScreen(2500);

		//GUIController, WAIT: at "Connecting..." scene for a connection to a server.
		//Application.awaitAtClientBarrier("GUIControl – waits for connection established (1GCWC)");
        Application.awaitAtBarrier("GUI: wait for hello authorization.");



		do {


		    switch (Application.getStatus()) {

                case HELLO_AUTHORIZATION: break;
                case SIGNING_IN:

                    //Display "Enter username scene"
                    this.gui.runLogin();

                    Application.awaitAtGuiBarrier("GUIC: waits for username.");

                    this.gui.runChecking();
                    this.waitAtScreen(1000);

                    //Application.awaitAtClientBarrier("GUIControl releases. Username entered. (6GCRC)");
                    Application.awaitAtBarrier("GUIC: releases CLI, username entered.");



                    //Application.awaitAtClientBarrier("GUIControl waits for checking username. (7GCWC)");
                    Application.awaitAtBarrier("GUIC: waits for CLI for username check.");


                    if(Application.getStatus() == GameStatus.ROOM_JOINING)
                        Application.awaitAtBarrier("GUIC: waits for CLI room joined approved.");


                    break;
                case ROOM_SELECTING:

                    if(Application.getPrevStatus() == GameStatus.GAME_INITIALIZING)
                        Application.awaitAtBarrier("GUIC: releases CLI after coming back at room select.");

                    //Application.awaitAtClientBarrier("GUIControl waits for room list. (8_2GCWC)");
                    Application.awaitAtBarrier("GUIC waits for CLI for room list.");

                    this.gui.runGameCenter();

                    Application.awaitAtGuiBarrier("GUIC: waits for GUI for room select.");

                    this.gui.runConnecting();

                    //Application.awaitAtClientBarrier("GUIControl releases after room selection/creation. (12GCRC)");
                    Application.awaitAtBarrier("GUIC releases CLI with room seleciton.");

                    //Application.awaitAtClientBarrier("GUIControl waits for room selection/creation response. (13GCWC)");
                    Application.awaitAtBarrier("GUIC waits for CLI for room selection approved.");
                    break;

                case GAME_WAITING:
                case ROOM_CREATING:
                case ROOM_JOINING:
                case GAME_INITIALIZING:

                    //Application.awaitAtBarrier("GUIC releases CLI with waiting screen.");

                    this.gui.runWaiting();
                    this.waitAtScreen(500);
                    Application.awaitAtBarrier("GUIC releases CLI after running waiting........");

                    //singleplayer start game
                    //Application.awaitAtBarrier("GUIC releases CLI with waiting screen init.");

//                    Application.awaitAtClientBarrier("GUIControls releases after waiting screen init.");
//                    Application.awaitAtClientBarrier("GUIControl waits for game initialization. (15GCWC)");

                    Application.awaitAtBarrier("GUIC: waits for CLI for game initialization.");


                    if (Application.getStatus() == GameStatus.ROOM_SELECTING) {
                        continue;
                    }

                    this.gui.runGamePlayground();


                    Application.awaitAtGuiBarrier("GUIC: waits for GUI for board initialization.");
                    Application.awaitAtBarrier("GUIC releases CLI with board initialized.");


                    //Application.awaitAtClientBarrier("GUIControl releases after board init.");

                    break;
                case GAME_RESTART:
                    break;
                case GAME_PLAYING_TURN_START:
                case GAME_PLAYING_TURN_END:
                    Application.awaitAtBarrier("++++ GUIC: waits for CLI for player update.");

                    this.gui.updatePlayerList();

                    if(!this.app.isOpponentOnline()) {

                        // opponent has gone offline
                        this.gui.askPlayerWait();

                        Application.awaitAtBarrier("++++ GUIC: releases CLI with WAITING result.");
                        Application.awaitAtBarrier("++++ GUIC: waits for CLI for WAITING result process.");

                        if(this.app.isWaitingAskResult())
                            continue;

//                        if(this.app.isWaitingAskResult()) {
//                            // if we DO wait
//                            Application.awaitAtBarrier("++++ GUIC: waits for CLI for WAITING result process.");
//                            continue;
//                        }

                    } else {
                        Application.awaitAtBarrier("++++ GUIC: releases CLI with player update completed.");
                        Application.awaitAtBarrier("GUIC waits for CLI for turn data.");
                    }



                    //Application.awaitAtClientBarrier("GUIControl waits for turn start. (17GCWC)");



                    // game over
                    //if (!this.app.isGameFinished()) {

                    //System.out.println("__________ GUIC check GAME FINISHED");
//                        if (this.app.isGameFinished()) {

                    if (Application.getStatus() == GameStatus.GAME_END || Application.getStatus() == GameStatus.GAME_RESULTS) {
                        //System.out.println("?????? GUIC REGISTERED: GAME FINISHED!!! LEAVING GAME-PROGRESS");
                        System.out.println("====== Game is over.");
                        break;
                    }

                    //Application.awaitAtClientBarrier("WAITING BEFORE BEGENNING TURN");
                    // new turn has begun
                    System.out.println("===== GUIC: begins a turn.");
                    this.gui.beginTurn();

                    // player plays a turn

                    Application.awaitAtGuiBarrier("GUIC waits for GUI for a player interaction / turn ends.");

                    //Application.awaitAtClientBarrier("GUIControl releases after turn ends.");
                    Application.awaitAtBarrier("GUIC releases CLI after turn ends.");

                    //Application.awaitAtBarrier("GUIC waits for CLI for turn data send.");

                    break;

                case GAME_END:
                case GAME_RESULTS:

                    Application.awaitAtBarrier("GUIC releases CLI after END GAME registered.");

                    // game results
                    //Application.awaitAtClientBarrier("GUIControl waits for game results.");
                    Application.awaitAtBarrier("GUIC waits for CLI for game results.");

                    // run results
                    this.gui.runGameResults();


                    Application.awaitAtGuiBarrier("GUIC waits for GUI for user interaction.");

//                    Application.awaitAtClientBarrier("GUIControl releases after user interaction.");
                    Application.awaitAtBarrier("GUIC releases CLI with user interaction.");


                    Application.awaitAtBarrier("GUIC waits for CLI for determine progress.");


                    break;
                case EXIT_GAME:
                    break;
            }


        } while(true);

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
