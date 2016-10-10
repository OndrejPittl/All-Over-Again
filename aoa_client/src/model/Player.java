package model;

public class Player {
	
	private int ID;
	
	private String name;
	
	//private String IP;
	
	private boolean isActive;
	
	
	public Player(String name) {
		this.name = name;
		this.ID = -1;
	}
	
	public Player(int id, String name) {
		this.ID = id;
		this.name = name;
	}
	
	
	
	
	
	
	/**
	 * @return the iD
	 */
	public int getID() {
		return ID;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the isActive
	 */
	public boolean isActive() {
		return isActive;
	}






	public static Player[] parsePlayers(String sequence){
		return null;
	}

	public String toString(){
		return "user(" + this.ID + " | " + this.name + ")";
	}
}
