package application;

import communication.CommunicationManager;


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


		this.app.handleSignIn();

		this.app.updateRoomList();
		
		
		Application.awaitAtClientBarrier("Client releases with room list. (8_3CRC)");
		
		Application.awaitAtClientBarrier("Client waits for user room selection/creation. (9CWC)");
		

		this.app.requestCreateJoinRoom();

        Application.awaitAtClientBarrier("Client releases with room selection. (14CRC)");


        //game init
        this.app.waitForGameInit();

        Application.awaitAtClientBarrier("Client releases after game initialization. (16CRC)");

        // turn info
        this.app.waitForTurnStart();

        Application.awaitAtClientBarrier("Client releases after game start. (18CRC)");

        Application.awaitAtClientBarrier("Client waits for user interaction. (19CWC)");








        _Developer.threadExecEnds("Client");
		
	}
	
	
	public boolean Connect(){
		
		return true;
	}

}
