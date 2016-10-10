package application;

import model.Player;

public class Room {
	
	private int ID;

	private int playerLimit;
	
	private int difficulty;

	private int boardDimension;
	
	private Player[] players;
	
	private int activePlayerID;
	
	//private int[] gameStatus;
	
	
	public Room(int ID, int playerLimit, Player[] players) {
		this.ID = ID;
		this.playerLimit = playerLimit;
		this.players = players;
	}


	
	
	
	
	
	

}
