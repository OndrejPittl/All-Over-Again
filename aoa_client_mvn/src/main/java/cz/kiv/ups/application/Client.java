package cz.kiv.ups.application;

import cz.kiv.ups.communication.CommunicationManager;
import cz.kiv.ups.communication.Message;
import cz.kiv.ups.communication.MessageType;
import cz.kiv.ups.config.ConnectionConfig;
import cz.kiv.ups.config.ErrorConfig;
import cz.kiv.ups.model.Error;
import cz.kiv.ups.model.GameStatus;


public class Client implements Runnable {

    private static Logger logger = Logger.getLogger();

    private String ip;

    private int port;


    /**
     * Connection.
     */
    private Connection conn;

    private CommunicationManager comm;

    private Application app;

    private Message msg;


    private int helloRequestCount = 0;




    public Client(Application app, String ip, int port) {
        this.app = app;
        this.ip = ip;
        this.port = port;

        this.init();
    }

    private void init(){
        this.comm = new CommunicationManager(this.app);
        this.conn = new Connection(this.ip, this.port);
        this.app.setDependencies(this.conn, this.comm);
    }



    public void run() {

        Application.awaitAtBarrier("CLI: waits for Running screen.");

        for(;;) {

            Application.awaitAtBarrier("GUIC: waits for Conn screen.");

            //connection try – max 10 tries with 2 sec delays
            if (!this.conn.connect()) {
                Application.disconnect(true, ErrorConfig.CONNECTION_SERVER_UNREACHABLE);
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

                logger.info("CLI: waits at QUEUE for a message.");

                if ((this.msg = this.comm.receiveMessage()) == null) {
                    return;
                }

                logger.info("+++ CLI: takes a message: " + this.msg.getMessage());

                this.handleResponse();
            } while (true);

        }
    }

    private void performAction() {
        GameStatus status = Application.getStatus();

        switch (status){
            case HELLO_AUTHORIZATION:   this.handleHelloAuthorizationRequest(); break;
            case SIGNING_IN:            this.handleSignInRequest(); break;
            case ROOM_SELECTING:        this.handleRoomListRequest(); break;
            case ROOM_CREATING:         this.handleRoomCreationRequest(); break;
            case ROOM_JOINING:          this.handleRoomJoinRequest(); break;
            case GAME_INITIALIZING:     break;
            case GAME_RESTART:          this.handleGameRestartRequest(); break;
            case GAME_PLAYING_TURN_START:
            case GAME_PLAYING_TURN_END: this.handleTurnDataRequest(); break;
            case GAME_WAITING:          this.handleWaitingRequest(); break;
            case GAME_END:              this.handleGameEnd(); break;
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
    }

    /**
     *  ROOM JOIN
     */
    private void handleRoomJoinRequest() {
        if(Application.getPrevStatus() == GameStatus.SIGNING_IN)
            return;

        this.app.requestJoinRoom();
    }

    private void handleGameStart() {}
    private void handleGameInitRequest() {}

    /**
     * GAME RESTART
     */
    private void handleGameRestartRequest(){
        // RESTART
        // send game start
        this.comm.handleRestartGame();
        Application.changeStatus(GameStatus.GAME_INITIALIZING);

        Application.awaitAtBarrier("CLI releases GUIC with new progress: RESTART GAME.");
    }

    /**
     *  GAME PLAY
     */
    private void handleTurnDataRequest() {
        if(Application.getStatus() == GameStatus.GAME_PLAYING_TURN_START)
            return;

        // TURN_END: send progress
        Application.awaitAtBarrier("CLI waits for GUIC for turn end.");
        Application.changeStatus(GameStatus.GAME_PLAYING_TURN_START);
        this.app.proceedEndTurn();
    }

    private void handleGameEnd() {}
    private void handleWaitingRequest() {}
    private void handleGAmeResult() {}

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
        //this.comm.handleSignOut();
        //Application.awaitAtBarrier("CLI releases GUIC with new progress: EXIT GAME.");
        Application.disconnect(true, null);
    }

    private void handleResponse() {
        MessageType type = this.msg.getType();

        // skipping unexpected!
        if(!Application.getStatus().isAcceptable(type))
            return;

        switch (type) {
            case HELLO:         this.handleHelloAuthorization(); break;
            case SIGN_IN:       this.handleSignIn(); break;
            case GAME_LIST:     this.handleRoomList(); break;
            case GAME_NEW:      this.handleNewGame(); break;
            case GAME_JOIN:     this.handleJoinGame(); break;
            case GAME_START:    this.handleStartGame(); break;
            case TURN_DATA:     this.handleTurnData(); break;
            case PLAYER_LIST:   this.handlePlayerList(); break;
            case GAME_RESULT:   this.handleEndGame(); break;
            case GAME_LEAVE:
            case SIGN_OUT:
                break;
        }

    }

    private void handlePlayerList() {
        logger.debug("+++ CLI: got PLAYER LIST.");

        // detects a change -> wait / end
        // updates players

        boolean result = this.app.handlePlayerList(this.msg.getMessage());

        if(result) {
            // everything ok, just update player list
            logger.debug("Player list: ok");

        } else {

            // a player is offline / has left the room
            logger.debug("Player list: NOT ok");
        }

        Application.awaitAtBarrier("++++ CLI releases GUIC with player update processed.");
        Application.awaitAtBarrier("++++ CLI: waits for GUIC for player list update/asking for WAITING.");


        if(!result) {
            if(this.app.isWaitingAskResult()) {

                // WAITING RESULT: waiting for the opponent comes back
                Application.changeStatus(GameStatus.GAME_WAITING);

                // send server: waiting ready
                this.comm.sendWaitingReady();

                Application.awaitAtBarrier("++++ CLI: releases GUIC with WAITING result process.");

            } else if (!this.app.isWaitingAskResult()) {

                // WAITING RESULT: not waiting
                this.app.forceEndTurn();
                this.comm.sendWaitingRefuse();
                Application.changeStatus(GameStatus.GAME_END);
                Application.awaitAtBarrier("++++ CLI: releases GUIC with WAITING result process.");
                Application.awaitAtBarrier("++++ CLI waits for GUIC for END GAME registered.");
            }
        }
    }

    /**
     * hello packet handshake – max 3 tries
     */
    private void handleHelloAuthorization() {
        logger.debug("CLI: Got HELLO response.");

        if(this.comm.checkHelloPacket(this.msg.getMessage())) {
            logger.info("CLI: Hello authorization successful.");
            Application.changeStatus(GameStatus.SIGNING_IN);
            Application.awaitAtBarrier("CLI: hello authorization succeeded.");
            return;
        }

        if(this.helloRequestCount++ < ConnectionConfig.MAX_HELLO_TRY_COUNT) {
            logger.error("CLI: Hello authorization failed.");

        } else {
            logger.error("CLI: Hello authorization failed " + this.helloRequestCount + " times. Shutting down.");
            Application.disconnect(true, ErrorConfig.CONNECTION_SERVER_UNAUTHORIZED);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {}
    }

    /**
     *  SIGN IN
     */
    private void handleSignIn() {
        boolean reJoined;

        if(this.app.handleUsernameSelection(this.msg.getMessage())) {
            logger.info("CLI: Sign in successful.");

            reJoined = this.app.getCurrentPlayer().hasRoom();
            if(reJoined) {
                // re-joined
                Application.changeStatus(GameStatus.ROOM_JOINING);

            } else {
                // new
                Application.changeStatus(GameStatus.ROOM_SELECTING);
            }

        } else {
            logger.error("CLI: Sign in failed.");
            this.app.registerError(Error.USERNAME_TAKEN);
        }

        Application.awaitAtBarrier("CLI: releases GUIC. Username checked.");
    }

    /**
     *  ROOM LIST
     */
    private void handleRoomList() {
        if(this.app.updateRoomList(this.msg.getMessage())) {
            logger.info("CLI: Room list in successfully updated.");

        } else {
            logger.error("CLI: Room list update failed.");
        }

        Application.awaitAtBarrier("CLI releases GUIC with room list.");
        Application.awaitAtBarrier("CLI waits for GUIC for room select.");
    }

    /**
     *  NEW GAME
     */
    private void handleNewGame() {
        if(this.app.handleRoomSelection(this.msg.getMessage())) {
            logger.info("CLI: New game successfully created and joined.");

            Application.changeStatus(GameStatus.GAME_INITIALIZING);
            Application.awaitAtBarrier("CLI releases GUIC with room selection approved.");

        } else {
            logger.error("CLI: New game creation failed.");
            Application.changeStatus(GameStatus.ROOM_SELECTING);
            Application.awaitAtBarrier("CLI releases GUIC with room selection failed.");
        }
    }

    /**
     *  JOIN GAME
     */
    private void handleJoinGame() {
        if(this.app.handleRoomSelection(this.msg.getMessage())) {
            logger.info("CLI: Join game succeeded.");

            Application.changeStatus(GameStatus.GAME_INITIALIZING);
            Application.awaitAtBarrier("CLI: releases GUIC. Room join approved.");

        } else {
            logger.error("CLI: Join game failed.");

            Application.changeStatus(GameStatus.ROOM_SELECTING);
            this.app.registerError(Error.ROOM_JOIN_REFUSED);

            Application.awaitAtBarrier("CLI: releases GUIC. Room join failed.");
        }
    }

    /**
     *  START GAME
     */
    private void handleStartGame() {
        Application.awaitAtBarrier("CLI waits for GUIC for running waiting........");

        boolean result = this.comm.handleGameInit(this.msg.getMessage());

        if(result) {
            logger.info("CLI: Game init succeeded.");
            Application.changeStatus(GameStatus.GAME_PLAYING_TURN_START);

        } else {
            logger.error("CLI: Game init failed.");

            if(Application.getPrevStatus() == GameStatus.GAME_RESTART) {
                // end -> start == play again
                this.app.registerError(Error.GAME_REPLAY_REFUSED);
            }

            Application.changeStatus(GameStatus.ROOM_SELECTING);
        }

        Application.awaitAtBarrier("CLI releases GUIC with game initialization done.");

        if(result) {
            Application.awaitAtBarrier("CLI: wait for GUIC for board initialized.");
        } else {
            Application.awaitAtBarrier("CLI: waits for GUIC for gui comes back at room select.");
        }
    }

    /**
     *  TURN DATA
     */
    private void handleTurnData() {
        boolean result = this.app.handleTurnStart(this.msg.getMessage());

        if(result) {
            logger.info("CLI: Turn was played very well. Game continues.");
            Application.changeStatus(GameStatus.GAME_PLAYING_TURN_END);

        } else {
            logger.error("CLI: Turn was played badly. Game finishes!");
            Application.changeStatus(GameStatus.GAME_END);
        }

        Application.awaitAtBarrier("CLI releases GUIC with turn data.");
        if(!result) Application.awaitAtBarrier("CLI waits for GUIC after turn data processed.");
    }

    /**
     *  END GAME
     */
    private void handleEndGame() {
        this.app.handleGameResults(this.msg.getMessage());
        logger.info("CLI: Game ends.");

        Application.changeStatus(GameStatus.GAME_RESULTS);

        //Application.awaitAtClientBarrier("CLI: releases with game results.");
        Application.awaitAtBarrier("CLI: releases GUIC with game results.");


        //Application.awaitAtClientBarrier("CLI: waits for user interaction.");
        Application.awaitAtBarrier("CLI waits for GUIC for user interaction.");

    }

    private void handleDisconect(){
        Application.disconnect(false, null);
    }
}
