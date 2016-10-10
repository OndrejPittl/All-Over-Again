package application;

import java.util.concurrent.CyclicBarrier;

import communication.CommunicationManager;





public class Client implements Runnable {
	
	/**
	 * Connection.
	 */
	private Connection conn;
	
	private CommunicationManager comm;
	
	//private CyclicBarrier barrier;
	
	private Application app;
	
	
	
	
	public Client() {
		this.app = Application.getInstance();
		//this.app.setClient(this);
		this.conn = new Connection();
		this.comm = new CommunicationManager();
		//this.barrier = Application.getBarrier();
	}
	
	
	

	public void run() {
		
		//connection try – max 10 tries
		if(!this.conn.connect()) {
			System.exit(0);
		}
		
		this.comm.setConnection(this.conn);
		
		//hello packet handshake – max 3 tries
		if(!this.comm.helloPacketHandShake()) {
			System.exit(0);
		}
		
		//release
		Application.awaitAtClientBarrier("CLI releases after connection & hello packet (C1R)");
				
		//wait
		Application.awaitAtClientBarrier("CLI waits for gui thread (C2W)");
				
		
		
		
		
		
		
		_Developer.threadExecEnds("Client");
		
	}
	
	
	public boolean Connect(){
		
		return true;
	}

}
