package cz.kiv.ups.model;

public class Player {
	
	private int ID = -1;
	
	private String name;

	private boolean isActive;

	private boolean isOnline;

	private int roomID = -1;

	// playerlist
	public Player(int id, String username, boolean online, boolean active) {
		this.ID = id;
		this.name = username;
		this.isOnline = online;
		this.isActive = active;
	}

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
	 * @return the isOnline
	 */
	public boolean isOnline() {
		return isOnline;
	}

	/**
	 * @param online the isOnline to set
	 */
	public void setOnline(boolean online) {
		this.isOnline= online;
	}

    /**
     *
     */
    public boolean isActive() {
        return this.isActive;
    }

    /**
     *
     */
    public void setActive(boolean active) {
        this.isActive = active;
    }

	public static Player[] parsePlayers(String sequence, String delimiter){
		String[] parts = sequence.split(delimiter);
		Player[] players = new Player[parts.length];
		
		for (int i = 0; i < parts.length; i++) {
			players[i] = new Player(parts[i]);
		}
		
		return players;
	}

    public boolean hasRoom() {
        return this.roomID != -1;
    }

    public int getRoomID() {
        return this.roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public String toString(){
		return "user(" + this.ID + " | " + this.name + ")";
	}
}
