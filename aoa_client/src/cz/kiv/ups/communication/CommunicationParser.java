package cz.kiv.ups.communication;

import cz.kiv.ups.application.Logger;
import cz.kiv.ups.config.CommunicationConfig;
import cz.kiv.ups.game.*;
import cz.kiv.ups.model.Player;
import cz.kiv.ups.model.Room;

import java.util.Arrays;

public class CommunicationParser {

    private static Logger logger = Logger.getLogger();

    private boolean checkACK(String[] parts, int index) {
        return parts[index].equals(CommunicationConfig.REQ_ACK);
    }

    public boolean checkACK(String[] parts) {
        return this.checkACK(parts, 1);
    }

    private boolean checkMessageType(String[] parts, MessageType[] types, int index) {
        for (MessageType t : types) {
            if(parts[index].equals(String.valueOf(t.getCode()))) {
                return true;
            }
        }
        return false;
    }

    private boolean checkMessageType(String[] parts, MessageType[] types) {
        return this.checkMessageType(parts, types, 0);
    }

	/**
	 * Parses a response of username availability request
	 * from a server in a form:
	 * 
	 * 1;[1 == ACK / 0 == NACK];[user ID]
	 * example: "1;1;7"
	 * 
	 * @param response	
	 * @param player	
	 * @return
	 */
	public Player parseUsernameAvailabilityResponse(String response, Player player){
		String[] parts = response.split(CommunicationConfig.MSG_DELIMITER);

        if(!this.checkMessageType(parts, new MessageType[]{MessageType.SIGN_IN}))
            return null;

        if(!this.checkACK(parts))
            return null;

        int id = Integer.parseInt(parts[2]);
        player.setID(id);
        player.setOnline(true);

        boolean reJoined = Integer.parseInt(parts[3]) == 1;
		if(reJoined) player.setRoomID(0);

		return player;
	}

    public Room[] parseRoomList(String response){

	    // message split to blocks
        String[] parts = response.split(CommunicationConfig.MSG_DELIMITER);

        if(!this.checkMessageType(parts, new MessageType[]{MessageType.GAME_LIST, MessageType.GAME_START}))
            return null;

	    return this.parseRoomList(parts, 1);
    }

	public Room[] parseRoomList(String[] parts, int offset) {
		
		int attribCount = 6,
			
			roomIndex = 0;
		
		// number of available rooms
		int count = (parts.length - offset) / attribCount;
		
		// collection of rooms
		Room[] rooms = new Room[count];
		
		
		for (int i = offset; i < parts.length - offset; i+=attribCount) {
			Room r = new Room();
			
			int id = Integer.parseInt(parts[i]),
				playerCount = Integer.parseInt(parts[i+1]),
				playerLimit = Integer.parseInt(parts[i+2]) - 1,
				difficulty = Integer.parseInt(parts[i+3]),
				dimension = Integer.parseInt(parts[i+4]) - 1;

			Player[] players = Player.parsePlayers(parts[i+5], CommunicationConfig.MSG_SUB_DELIMITER);

			r.setID(id);
			r.setPlayerCount(playerCount);
			r.setType(GameType.getNth(playerLimit));	// player limit
			r.setDifficulty(GameDifficulty.getNth(difficulty));
			r.setBoardDimension(BoardDimension.getNth(dimension));
			r.setPlayers(players);
			
			rooms[roomIndex++] = r;
		}
		
		return rooms;
	}

	public Room parseSelectedRoom(String response){

        // the only acceptable message is NACK/ACK + room info,
        // all other messages are being ignored

        // message split to blocks
        String[] parts = response.split(CommunicationConfig.MSG_DELIMITER);

        if(!this.checkACK(parts))
            return null;

        if(!this.checkMessageType(parts, new MessageType[]{MessageType.GAME_JOIN, MessageType.GAME_NEW}))
        	return null;

        return this.parseRoomList(parts, 2)[0];
	}


    public boolean parseGameInitResult(String response) {

	    // message split to blocks
        String[] parts = response.split(CommunicationConfig.MSG_DELIMITER);

        return parts[1].equals(CommunicationConfig.REQ_ACK);

	}

    public GameTurn parseTurnInfo(String response, int diff) {

	    // message split to blocks
        String[] parts = response.split(CommunicationConfig.MSG_DELIMITER);

        if(!this.checkMessageType(parts, new MessageType[]{MessageType.TURN_DATA})) {
            return null;
        }

        if(!this.checkACK(parts))
            return null;

        GameTurn turn;
        GameMove[] moves;

        int offset = 5,
            activePlayerID = Integer.parseInt(parts[2]),
            turnNum = Integer.parseInt(parts[3]),
            prevTurnNum = turnNum - 1,
            time = Integer.parseInt(parts[4]),
            ddif = diff + 1;
            //diff = prevTurnNum == 0 ? 0 : (parts.length - offset)/prevTurnNum;


        if(parts.length > offset) {

            // NOT first turn
            moves = new GameMove[prevTurnNum];

            for (int t = 0; t < prevTurnNum; t++) {

                int[] attrs = {-1, -1, -1};
                for (int a = 0; a < ddif; a++) {
                    int index = t * ddif + a + offset;
                    attrs[a] = Integer.parseInt(parts[index]);
                }
                moves[t] = new GameMove(attrs);
            }

        } else {

            // first turn
            moves = null;

        }

        turn = new GameTurn(activePlayerID, time, moves, turnNum);

        logger.debug("Moves:");
        logger.debug(Arrays.toString(moves));

        return turn;
    }


    public boolean checkIfTurnData(String response){
	    return this.checkMessageType(
	            response.split(CommunicationConfig.MSG_DELIMITER),
                new MessageType[]{MessageType.TURN_DATA}
        );
    }

    public int parseResults(String response) {

        // message split to blocks
        String[] parts = response.split(CommunicationConfig.MSG_DELIMITER);

        if(!this.checkMessageType(parts, new MessageType[]{MessageType.GAME_RESULT})) {
            return -1;
        }

        return Integer.parseInt(parts[1]);
    }

    public Player[] parsePlayerList(String response) {
        Player[] players;
        int attrCount, playerCount, index, offset = 1;

        index = 0;
        attrCount = 4;
        String[] parts = response.split(CommunicationConfig.MSG_DELIMITER);
        playerCount = (parts.length - offset)/attrCount;
        players = new Player[playerCount];

        for (int i = offset; i < parts.length; i++) {
            int uid = Integer.parseInt(parts[i]);
            String username = parts[++i];
            boolean online = Integer.parseInt(parts[++i]) == 1;
            boolean active = Integer.parseInt(parts[++i]) == 1;
            players[index++] = new Player(uid, username, online, active);
        }


        return players;
    }

}
