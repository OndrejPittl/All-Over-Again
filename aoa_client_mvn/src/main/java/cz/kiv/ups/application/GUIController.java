package cz.kiv.ups.application;

import cz.kiv.ups.model.GameStatus;


public class GUIController implements Runnable {

    private static Logger logger = Logger.getLogger();

	private Application app;
	
	private Screen gui;

	private boolean started = false;
	
	
	
	public GUIController(Application app) {
		this.app = app;
	}

	@Override
	public void run() {

        Application.awaitAtGuiBarrier("GUIControl waits for GUI init.");

        this.gui.run();


        for(;;) {

            this.gui.runConnecting(this.started);

            if(!this.started) {
                this.waitAtScreen(2200);

                Application.awaitAtBarrier("GUIC: releases with Running screen.");

                this.gui.runConnectingMsg();

                this.started = true;
            }

            Application.awaitAtBarrier("GUI: releases CLI after gui init.");

            this.waitAtScreen(800);

            Application.awaitAtBarrier("GUIC: releases after conn screen.");

            // hello packet
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

                        Application.awaitAtBarrier("GUIC: releases CLI, username entered.");
                        Application.awaitAtBarrier("GUIC: waits for CLI for username check.");

                        if(Application.getStatus() == GameStatus.ROOM_JOINING)
                            Application.awaitAtBarrier("GUIC: waits for CLI room joined approved.");


                        break;
                    case ROOM_SELECTING:

                        if(Application.getPrevStatus() == GameStatus.GAME_INITIALIZING)
                            Application.awaitAtBarrier("GUIC: releases CLI after coming back at room select.");

                        Application.awaitAtBarrier("GUIC waits for CLI for room list.");

                        this.gui.runGameCenter();

                        Application.awaitAtGuiBarrier("GUIC: waits for GUI for room select.");

                        this.gui.runConnecting(this.started);

                        Application.awaitAtBarrier("GUIC releases CLI with room seleciton.");
                        Application.awaitAtBarrier("GUIC waits for CLI for room selection approved.");
                        break;

                    case GAME_WAITING:
                    case ROOM_CREATING:
                    case ROOM_JOINING:
                    case GAME_INITIALIZING:
                        this.gui.runWaiting();
                        this.waitAtScreen(500);
                        Application.awaitAtBarrier("GUIC releases CLI after running waiting........");
                        Application.awaitAtBarrier("GUIC: waits for CLI for game initialization.");

                        if (Application.getStatus() == GameStatus.ROOM_SELECTING) {
                            continue;
                        }

                        this.gui.runGamePlayground();

                        Application.awaitAtGuiBarrier("GUIC: waits for GUI for board initialization.");
                        Application.awaitAtBarrier("GUIC releases CLI with board initialized.");
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

                        } else {
                            Application.awaitAtBarrier("++++ GUIC: releases CLI with player update completed.");
                            Application.awaitAtBarrier("GUIC waits for CLI for turn data.");
                        }

                          if (Application.getStatus() == GameStatus.GAME_END || Application.getStatus() == GameStatus.GAME_RESULTS) {
                            logger.info("=== Game is over.");
                            break;
                        }

                        // new turn has begun
                        logger.info("=== GUIC: begins a turn.");
                        this.gui.beginTurn();

                        // player plays a turn
                        Application.awaitAtGuiBarrier("GUIC waits for GUI for a player interaction / turn ends.");
                        Application.awaitAtBarrier("GUIC releases CLI after turn ends.");
                        break;

                    case GAME_END:
                    case GAME_RESULTS:

                        Application.awaitAtBarrier("GUIC releases CLI after END GAME registered.");

                        // game results
                        Application.awaitAtBarrier("GUIC waits for CLI for game results.");

                        // run results
                        this.gui.runGameResults();

                        Application.awaitAtGuiBarrier("GUIC waits for GUI for user interaction.");
                        Application.awaitAtBarrier("GUIC releases CLI with user interaction.");
                        Application.awaitAtBarrier("GUIC waits for CLI for determine progress.");
                        break;

                    case EXIT_GAME:
                        break;
                }

            } while(true);
        }
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
