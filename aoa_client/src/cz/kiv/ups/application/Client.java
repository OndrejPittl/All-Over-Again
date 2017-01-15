package cz.kiv.ups.application;

import cz.kiv.ups.communication.CommunicationManager;
import cz.kiv.ups.communication.Message;
import cz.kiv.ups.communication.MessageType;
import cz.kiv.ups.config.ConnectionConfig;
import cz.kiv.ups.model.Error;
import cz.kiv.ups.model.GameStatus;

import java.util.logging.Logger;


public class Client implements Runnable {

    private String[] args;

    /**
     * Connection.
     */
    private Connection conn;

    private CommunicationManager comm;

    private Application app;

    private Logger logger;

    private Message msg;




    private int helloRequestCount = 0;




    public Client(String[] args, Application app) {
        this.args = args;
        this.app = app;
        this.init();
    }

    private void init(){
        //this.app.setClient(this);
        //this.barrier = Application.getBarrier();

        this.logger = Logger.getLogger(getClass().getName());

        this.comm = new CommunicationManager(this.app);

        this.conn = new Connection(
                this.args[0],
                Integer.parseInt(this.args[1])
        );

        this.app.setDependencies(this.conn, this.comm);
    }



    public void run() {

        boolean result;

        //connection try – max 10 tries
        if (!this.conn.connect()) {
            Application.disconnect(true);
        }
        this.comm.setConnection(this.conn);
        this.comm.startService();


        Application.awaitAtBarrier("CLI: waits for gui init.");

        do {

            this.performAction();


            if(Application.getStatus() == GameStatus.EXIT_GAME) {
                this.handleDisconect();
                break;
            }

            System.out.println("CLIIII: waits for a message at a queue.");

            this.msg = this.comm.receiveMessage();

            System.out.println("CLIIII: gets a message from a queue.");

            if (this.msg == null)
                continue;

            this.handleResponse();



        } while (true);
    }

    private void performAction() {
        switch (Application.getStatus()){
            case HELLO_AUTHORIZATION:   this.handleHelloAuthorizationRequest(); break;
            case SIGNING_IN:            this.handleSignInRequest(); break;
            case ROOM_SELECTING:        this.handleRoomListRequest(); break;
            case ROOM_CREATING:         this.handleRoomCreationRequest(); break;
            case ROOM_JOINING:          this.handleRoomJoinRequest(); break;
            case GAME_INITIALIZING:     break; // this.handleGameStart(); // this.handleGameInitRequest(); break;
            case GAME_RESTART:          this.handleGameRestartRequest(); break;
            case GAME_PLAYING_TURN_START:
            case GAME_PLAYING_TURN_END: this.handleTurnDataRequest(); break;
            case GAME_WAITING:          this.handleWaitingRequest(); break;
            case GAME_END:              this.handleGameEnd(); break;                    // not tested
            case GAME_RESULTS:          this.handleLeaveGame(); break;
            case EXIT_GAME:             this.handleExitGame(); break;
        }

    }


    /**
     * HELLO SERVER
     */
    private void handleHelloAuthorizationRequest() {
        // request: hello server!
        this.comm.sendHelloServer();
    }

    /**
     *  SIGN IN
     */
    private void handleSignInRequest() {
        //Application.awaitAtClientBarrier("CLI: Waits for username. (3CWC)");
        Application.awaitAtBarrier("CLI wait for username.");
        this.comm.sendUsernameRequest();
    }

    /**
     *  ROOM LIST
     */
    private void handleRoomListRequest() {
        this.app.requestRoomList();
    }

    /**
     *  ROOM CREATE
     */
    private void handleRoomCreationRequest() {
        this.app.requestCreateRoom();
        //Application.changeStatus(GameStatus.ROOM_CREATING);
    }

    /**
     *  ROOM JOIN
     */
    private void handleRoomJoinRequest() {
        this.app.requestJoinRoom();
        //Application.changeStatus(GameStatus.ROOM_JOINING);
    }

    private void handleGameStart() {

    }

    /**
     *  GAME INIT
     */
    private void handleGameInitRequest() {

    }

    /**
     * GAME RESTART
     */
    private void handleGameRestartRequest(){
        // RESTART
        // send game start
        this.comm.handleRestartGame();
        Application.changeStatus(GameStatus.GAME_INITIALIZING);
        Application.awaitAtBarrier("CLI releases GUIC with new progress: RESTART GAME.");
        Application.awaitAtBarrier("CLI waits for GUIC for waiting screen.");
        //Application.awaitAtClientBarrier("Client waits for waiting screen init.");
    }

    /**
     *  GAME PLAY
     */
    private void handleTurnDataRequest() {
        if(Application.getStatus() == GameStatus.GAME_PLAYING_TURN_START)
            return;

        // TURN_END: send progress
        //Application.awaitAtClientBarrier("CLI: waits for user interaction. (19CWC)");

        Application.awaitAtBarrier("CLI waits for GUIC for an end game.");
        Application.changeStatus(GameStatus.GAME_PLAYING_TURN_START);
        this.app.proceedEndTurn();
    }

//    private void readWhileTurnWaiting(){
//        do {
//           Message m = this.comm.receiveMessage();
//           if (m == null) continue;
//           this.handleResponse();
//        } while (true);
//    }


    /**
     *  GAME WAIT
     */
    private void handleWaitingRequest() {
        // waits for a player
    }

    /**
     * GAME END
     */
    private void handleGameEnd() {
        //this.comm.handleLeaveGame();
    }

    private void handleGAmeResult() {

    }

    /**
     *  LEAVE GAME
     */
    private void handleLeaveGame() {
        // a user has chosen "Leave game." option.
        this.comm.handleLeaveGame();
        Application.changeStatus(GameStatus.ROOM_SELECTING);
        Application.awaitAtBarrier("CLI releases GUIC with new progress: LEAVING GAME.");
        this.handleRoomListRequest();
    }

    /**
     *  SIGN OUT
     *  + flag EXIT GAME
     */
    private void handleExitGame() {
        // send sign out
        this.comm.handleSignOut();
        Application.awaitAtBarrier("CLI releases GUIC with new progress: EXIT GAME.");
    }





    private void handleResponse() {
        MessageType type = this.msg.getType();

        // skipping unexpected!
        if(!Application.getStatus().isAcceptable(type))
            return;

        switch (type) {
            case HELLO:     this.handleHelloAuthorization(); break;
            case SIGN_IN:   this.handleSignIn(); break;
            case GAME_LIST: this.handleRoomList(); break;
            case GAME_NEW:  this.handleNewGame(); break;
            case GAME_JOIN: this.handleJoinGame(); break;
            case GAME_START:this.handleStartGame(); break;
            case TURN_DATA: this.handleTurnData(); break;
            case GAME_RESULT:this.handleEndGame(); break;
            case GAME_LEAVE:break;
            case SIGN_OUT:  break;
            case PING:      break;
        }

    }

    /**
     * hello packet handshake – max 3 tries
     */
    private void handleHelloAuthorization() {
        //this.logger.fine("CLI: Got hello response.");
        System.out.println("CLI: Got hello response.");


        if(this.comm.checkHelloPacket(this.msg.getMessage())) {
            //this.logger.fine("CLI: Hello authorization successful.");
            System.out.println("CLI: Hello authorization successful.");
            Application.changeStatus(GameStatus.SIGNING_IN);
            Application.changeStatus(GameStatus.SIGNING_IN);
            Application.awaitAtBarrier("CLI: hello authorization succeeded.");
            return;
        }

        if(this.helloRequestCount++ < ConnectionConfig.MAX_HELLO_TRY_COUNT) {
            //this.logger.fine("CLI: Hello authorization failed.");
            System.out.println("CLI: Hello authorization failed.");

        } else {
            //this.logger.fine("CLI: Hello authorization failed " + this.helloRequestCount + " times. Shutting down.");
            System.out.println("CLI: Hello authorization failed " + this.helloRequestCount + " times. Shutting down.");

            Application.disconnect(true);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {}
    }

    /**
     *  SIGN IN
     */
    private void handleSignIn() {
        if(this.app.handleUsernameSelection(this.msg.getMessage())) {
            //this.logger.fine("CLI: Sign in successful.");
            System.out.println("CLI: Sign in successful.");

            Application.changeStatus(GameStatus.ROOM_SELECTING);
        } else {
            this.logger.fine("CLI: Sign in failed.");
            System.out.println("CLI: Sign in failed.");

            this.app.registerError(Error.USERNAME_TAKEN);
        }

        //Application.awaitAtClientBarrier("CLI: releases. Username checked. (8CRC)");
        Application.awaitAtBarrier("CLI: releases GUIC. Username checked.");

    }

    /**
     *  ROOM LIST
     */
    private void handleRoomList() {
        if(this.app.updateRoomList(this.msg.getMessage())) {
            this.logger.fine("CLI: Room list in successfully updated.");
            System.out.println("CLI: Room list in successfully updated.");

        } else {
            this.logger.fine("CLI: Room list update failed.");
            System.out.println("CLI: Room list update failed.");
        }

//        Application.awaitAtClientBarrier("CLI releases GUIC.");
//        Application.awaitAtClientBarrier("CLI: releases with room list. (8_3CRC)");
//        Application.awaitAtClientBarrier("Client waits for user room selection/creation. (9CWC)");

        Application.awaitAtBarrier("CLI releases GUIC with room list.");
        Application.awaitAtBarrier("CLI waits for GUIC for room select.");

    }

    /**
     *  NEW GAME
     */
    private void handleNewGame() {
        if(this.app.handleRoomSelection(this.msg.getMessage())) {
            //this.logger.fine("CLI: New game successfully created and joined.");
            System.out.println("CLI: New game successfully created and joined.");

            Application.changeStatus(GameStatus.GAME_INITIALIZING);

            Application.awaitAtBarrier("CLI releases GUIC with room selection approved.");
            Application.awaitAtBarrier("CLI waits for GUIC for waiting screen.");

        } else {
            //this.logger.fine("CLI: New game creation failed.");
            System.out.println("CLI: New game creation failed.");

            Application.changeStatus(GameStatus.ROOM_SELECTING);

            Application.awaitAtBarrier("CLI releases GUIC with room selection failed.");
        }

        //Application.awaitAtClientBarrier("CLI: releases. Room selection handled. (14CRC)");

    }

    /**
     *  JOIN GAME
     */
    private void handleJoinGame() {
        if(this.app.handleRoomSelection(this.msg.getMessage())) {
//            this.logger.fine("CLI: Join game succeeded.");
            System.out.println("CLI: Join game succeeded.");

            Application.changeStatus(GameStatus.GAME_INITIALIZING);

            Application.awaitAtBarrier("CLI: releases GUIC. Room join approved.");
            Application.awaitAtBarrier("GUIC releases CLI with waiting screen.");

        } else {
            this.logger.fine("CLI: Join game failed.");
            System.out.println("CLI: Join game failed.");

            Application.changeStatus(GameStatus.ROOM_SELECTING);
            this.app.registerError(Error.ROOM_JOIN_REFUSED);

            Application.awaitAtBarrier("CLI: releases GUIC. Room join failed.");
        }

        //Application.awaitAtClientBarrier("CLI: releases GUIC. Room selection handled. (14CRC)");
        //Application.awaitAtClientBarrier("Client waits for waiting screen init.");


    }

    /**
     *  START GAME
     */
    private void handleStartGame() {
        boolean result = this.comm.handleGameInit(this.msg.getMessage());

        //Application.awaitAtClientBarrier("CLI: waits for waiting screen init.");
//        Application.awaitAtBarrier("CLI releases GUIC with game initialized.");

        if(result) {
            //this.logger.fine("CLI: Game init succeeded.");
            System.out.println("CLI: Game init succeeded.");
            Application.changeStatus(GameStatus.GAME_PLAYING_TURN_START);

        } else {
            //this.logger.fine("CLI: Game init failed.");
            System.out.println("CLI: Game init failed.");

            if(Application.getPrevStatus() == GameStatus.GAME_RESTART) {
                // end -> start == play again
                this.app.registerError(Error.GAME_REPLAY_REFUSED);
            }

            Application.changeStatus(GameStatus.ROOM_SELECTING);
        }

        //Application.awaitAtClientBarrier("CLI: releases after game initialization. (16CRC)");
        //if(result) Application.awaitAtClientBarrier("CLI: waits for board init.");

        Application.awaitAtBarrier("CLI releases GUIC with game initialization done.");

        if(result) {
            Application.awaitAtBarrier("CLI: wait for GUIC for board initialized.");
        } else {
            //Application.awaitAtBarrier("CLI: waits for GUIC for gui comes back at room select.");
        }
    }

    /**
     *  TURN DATA
     */
    private void handleTurnData() {
        if(this.app.handleTurnStart(this.msg.getMessage())) {
            //this.logger.fine("CLI: Turn start ok, game continues.");
            System.out.println("CLI: Turn start ok, game continues.");
            Application.changeStatus(GameStatus.GAME_PLAYING_TURN_END);
            //Application.awaitAtClientBarrier("AFTER BEGENNING TURN");

        } else {
            //this.logger.fine("CLI: Turn start ok, game finishes!.");
            System.out.println("CLI: Turn start NOT ok, game finishes!.");
            Application.changeStatus(GameStatus.GAME_END);
        }

        //Application.awaitAtClientBarrier("CLI: releases after turn start. (18CRC)");
        Application.awaitAtBarrier("CLI releases GUIC with turn data.");
    }

    /**
     *  END GAME
     */
    private void handleEndGame() {
        this.app.handleGameResults(this.msg.getMessage());
        //this.logger.fine("CLI: Game ends.");
        System.out.println("CLI: Game ends.");


//        if(Application.getStatus() == GameStatus.GAME_INITIALIZING) {
//            Application.changeStatus(GameStatus.ROOM_SELECTING);
//            return;
//        }

        Application.changeStatus(GameStatus.GAME_RESULTS);

        //Application.awaitAtClientBarrier("CLI: releases with game results.");
        Application.awaitAtBarrier("CLI: releases GUIC with game results.");


        //Application.awaitAtClientBarrier("CLI: waits for user interaction.");
        Application.awaitAtBarrier("CLI waits for GUIC for user interaction.");

    }






    private void handleDisconect(){
        System.out.println("!!!!!!!!!!!!!!!!!CLIENT ENDS!!!!!!!!!!!!!!!!!");
        Application.disconnect(false);
    }

}
