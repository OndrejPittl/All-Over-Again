package cz.kiv.ups.model;

import cz.kiv.ups.game.BoardDimension;
import cz.kiv.ups.game.GameDifficulty;
import cz.kiv.ups.game.GameType;

public class Room {
	
	private int ID;

	private int playerCount;

	private int turn;

	private GameType type;

	private GameDifficulty difficulty;

//	private int boardDimension;
	private BoardDimension boardDimension;

	private Player[] players;

	private  Player currentPlayer;

	private  Player opponent;

	private int activePlayerID;
	


	public Room() {
	    this(-1);
	}

	public Room(int ID) {
		this.ID = ID;
	}


	public Room(GameType type, GameDifficulty diff, BoardDimension dim) {
		this();
		this.type = type;
		this.difficulty = diff;
		this.boardDimension = dim;
	}

    /**
	 * @return the id
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
	 * @return the playerCount
	 */
	public int getPlayerCount() {
		return playerCount;
	}

	/**
	 * @param playerCount the playerCount to set
	 */
	public void setPlayerCount(int playerCount) {
		this.playerCount = playerCount;
	}

	/**
	 * @return the difficulty
	 */
	public GameDifficulty getDifficulty() {
		return difficulty;
	}

	/**
	 * @param difficulty the difficulty to set
	 */
	public void setDifficulty(GameDifficulty difficulty) {
		this.difficulty = difficulty;
	}

	/**
	 * @return the boardDimension
	 */
	public BoardDimension getBoardDimension() {
		return boardDimension;
	}

	/**
	 * @param boardDimension the boardDimension to set
	 */
	public void setBoardDimension(BoardDimension boardDimension) {
		this.boardDimension = boardDimension;
	}

	/**
	 * @return the players
	 */
	public Player[] getPlayers() {
		return players;
	}

	/**
	 * @param players the players to set
	 */
	public void setPlayers(Player[] players) {
		this.players = players;
	}

	/**
	 * @return the activePlayerID
	 */
	public int getActivePlayerID() {
		return activePlayerID;
	}

	/**
	 * @param activePlayerID the activePlayerID to set
	 */
	public void setActivePlayerID(int activePlayerID) {
		this.activePlayerID = activePlayerID;
	}


	public GameType getType() {
		return type;
	}

	public void setType(GameType type) {
		this.type = type;
	}

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

//	public Player getOpponent(){
//		Player opponent = null;
//
//		for (Player p : this.players) {
//			if(p.getID() != this.currentPlayer.getID())
//				opponent = p;
//		}
//
//		return opponent;
//	}

//	public void updateOpponent(Player opponent){
//		for (int i = 0; i < this.players.length; i++) {
//			if(this.players[i].getID() == opponent.getID()){
//				this.players[i].setOnline(opponent.isOnline());
//				break;
//			}
//		}
//	}

	public void updatePlayers(Player[] players) {
        Player opponent = null;

		for (Player p : players) {
			if(p.getID() != this.currentPlayer.getID())
				opponent = p;
		}

		if(opponent == null)
		    return;

		this.players = players;

		// determine: player has gone offline
        this.opponent.setOnline(opponent.isOnline());

        // determine: player has left a room
        this.setPlayerCount(players.length);

	}

	public boolean isOpponentOnline(){
	    return this.getType() == GameType.SINGLEPLAYER || this.opponent.isOnline();
	    //return this.opponent.isOnline();
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;

        for (Player p : this.players) {
            if(p.getID() != this.currentPlayer.getID())
                this.opponent = p;
        }
    }

    public int getCurrentPlayerID() {
        return this.currentPlayer.getID();
    }
}
