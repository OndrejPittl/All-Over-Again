package application;

import communication.CommunicationManager;
import model.Room;





public class Client implements Runnable {

	private String[] args;

	/**
	 * Connection.
	 */
	private Connection conn;
	
	private CommunicationManager comm;
	
	//private CyclicBarrier barrier;
	
	private Application app;
	
	
	
	
	public Client(String[] args) {
		this.args = args;
		this.init();
	}

	private void init(){
		this.app = Application.getInstance();
		//this.app.setClient(this);
		//this.barrier = Application.getBarrier();

		this.comm = new CommunicationManager();

		this.conn = new Connection(
				this.args[0],
				Integer.parseInt(this.args[1])
		);

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

		do {
			//wait for entering username
			Application.awaitAtClientBarrier("CLI waits for username. (3CWC)");

			//check nickname availability
			this.comm.checkUsernameAvailability();

			Application.awaitAtClientBarrier("CLI releases. Username checked. (8CRC)");

		} while(!this.app.isPlayerRegistered());
		
		
		
		Room[] rooms = this.comm.requestRoomList();
		this.app.setRooms(rooms);
		
		
		Application.awaitAtClientBarrier("Client releases with room list. (8_3CRC)");
		
		Application.awaitAtClientBarrier("Client waits for user room selection/creation. (9CWC)");
		
		
		
		
		
		_Developer.threadExecEnds("Client");
		
	}
	
	
	public boolean Connect(){
		
		return true;
	}

}
