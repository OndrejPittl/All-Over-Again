package model;

import game.GameDifficulty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ViewRoom {

	private Room room;

	private final StringProperty viewNicknames;
    
	private final StringProperty viewPlayers;
    
	private final StringProperty viewDifficulty;

	
	public ViewRoom(Room room){
		this(room.getPlayers(), room.getPlayerCount(), room.getPlayerLimit(), room.getDifficulty());
		this.room = room;
	}
	
	public ViewRoom(Player[] players, int playerCount, int playerLimit, GameDifficulty difficulty) {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < players.length; i++) {
			sb.append(players[i].getName());
			if(i < players.length - 1) sb.append(", ");
		}
		
		this.viewNicknames = new SimpleStringProperty(sb.toString());
		this.viewPlayers = new SimpleStringProperty(playerCount + "/" + playerLimit);
		this.viewDifficulty = new SimpleStringProperty(difficulty.getTitle());
	}

	public int getID() {
		return this.room.getID();
	}

	public Room getRoom() {
		return room;
	}

	/**
	 * @return the viewNicknames
	 */
	public StringProperty getViewNicknames() {
		return viewNicknames;
	}



	/**
	 * @return the viewPlayers
	 */
	public StringProperty getViewPlayers() {
		return viewPlayers;
	}



	/**
	 * @return the viewDifficulty
	 */
	public StringProperty getViewDifficulty() {
		return viewDifficulty;
	}
	
	
}
