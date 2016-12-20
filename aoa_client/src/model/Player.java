package model;

public class Player {
	
	private int ID = -1;
	
	private String name;
	
	//private String IP;
	
	private boolean isActive;
	
	
	public Player(String name) {
		this.name = name;
	}
	
	public Player(int id, String name) {
		this.ID = id;
		this.name = name;
	}
	
	
	
	
	




	/**
	 * @return the ID
	 */
	public int getID() {
		return ID;
	}

	/**
	 * @param id the ID to set
	 */
	public void setID(int id) {
		ID = id;
	}
	
	public boolean hasID(){
		return this.ID != -1;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the isActive
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public static Player[] parsePlayers(String sequence, String delimiter){
		String[] parts = sequence.split(delimiter);
		Player[] players = new Player[parts.length];
		
		for (int i = 0; i < parts.length; i++) {
			players[i] = new Player(parts[i]);
		}
		
		return players;
	}

	public String toString(){
		return "user(" + this.ID + " | " + this.name + ")";
	}
}
