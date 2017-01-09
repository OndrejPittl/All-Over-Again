package application;

import communication.CommunicationManager;
import model.Error;


public class Client implements Runnable {

	private String[] args;

	/**
	 * Connection.
	 */
	private Connection conn;
	
	private CommunicationManager comm;
	
	//private CyclicBarrier barrier;
	
	private Application app;
	
	
	
	
	public Client(String[] args, Application app) {
		this.args = args;
		this.app = app;
		this.init();
	}

	private void init(){
		//this.app.setClient(this);
		//this.barrier = Application.getBarrier();

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
        if(!this.conn.connect()) {
            System.exit(0);
        }

		this.comm.setConnection(this.conn);
		this.comm.startService();

//        Application.awaitAtClientBarrier("CLI awaits for GUI init.");


		//hello packet handshake – max 3 tries
		if(!this.comm.helloPacketHandShake()) {
			System.exit(0);
		}

		//release
		Application.awaitAtClientBarrier("CLI releases after connection & hello packet. (2CRC)");

        do {


            // sign-in loop until a username is accepted and a player signed in

            do {

                Application.awaitAtClientBarrier("CLI waits for username. (3CWC)");

                this.app.handleSignIn();

                Application.awaitAtClientBarrier("CLI releases. Username checked. (8CRC)");

                if(!(result = this.app.isPlayerRegistered())){
                    System.out.println("--- REGISTERING ERROR: USERNAME TAKEN");
                    this.app.registerError(Error.USERNAME_TAKEN);
                }


            } while(!result);



            do {

                // room selection loop until a room is joined

                // the only acceptable message is NACK/ACK + room info,
                // all other messages are being ignored

                do {

                    this.app.updateRoomList();

                    Application.awaitAtClientBarrier("Client releases with room list. (8_3CRC)");

                    Application.awaitAtClientBarrier("Client waits for user room selection/creation. (9CWC)");

                    this.app.requestCreateJoinRoom();

                    Application.awaitAtClientBarrier("Client releases with room selection. (14CRC)");

                } while (!this.app.isRoomJoined());



                //game init
                this.app.waitForGameInit();

                Application.awaitAtClientBarrier("Client releases after game initialization. (16CRC)");


                // TODO: zkontrolovat!!!
                if (!this.app.isGameStarted()) {
                    continue;
                }


                //---- cycle:
                do {

                    // turn info
                    this.app.waitForTurnStart();

                    Application.awaitAtClientBarrier("Client releases after turn start. (18CRC)");

                    // NOT correct solution
                    if(!this.app.isTurnDataOK()) {
                        break;
                    }


                    Application.awaitAtClientBarrier("Client waits for user interaction. (19CWC)");

                    this.app.proceedEndTurn();

                } while (!this.app.isGameFinished());


                // game results
                this.app.waitForGameResults();

                Application.awaitAtClientBarrier("Client releases with game results.");
                Application.awaitAtClientBarrier("Client TEMPORARILY WAITS.");

            } while (this.app.isSignedIn());



        } while(!this.app.isSignedIn());




        _Developer.threadExecEnds("Client");
		
	}
	
	
	public boolean Connect(){
		
		return true;
	}

}
