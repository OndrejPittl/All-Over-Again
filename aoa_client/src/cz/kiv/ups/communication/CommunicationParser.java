package cz.kiv.ups.communication;

import cz.kiv.ups.config.CommunicationConfig;
import cz.kiv.ups.game.*;
import cz.kiv.ups.model.Player;
import cz.kiv.ups.model.Room;

import java.util.Arrays;

public class CommunicationParser {
	
	private byte[] msgBuffer;
	
	
	


//	private boolean checkACK(String response, int index){
//        String[] parts = response.split(CommunicationConfig.MSG_DELIMITER);
//        return parts[index].equals(CommunicationConfig.REQ_ACK);
//    }
//
//    private boolean checkACK(String response){
//	    return this.checkACK(response, 1);
//    }

    private boolean checkACK(String[] parts, int index) {
        return parts[index].equals(CommunicationConfig.REQ_ACK);
    }

    public boolean checkACK(String[] parts) {
        return this.checkACK(parts, 1);
    }

    public boolean checkACK(String response) {
        return this.checkACK(response.split(CommunicationConfig.MSG_DELIMITER));
    }

//    private boolean checkMessageType(String response, MessageType[] types, int index) {
//        String[] parts = response.split(CommunicationConfig.MSG_DELIMITER);
//
//        for (MessageType t : types) {
//            if(parts[index].equals(String.valueOf(t.getCode()))) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    private boolean checkMessageType(String response, MessageType[] types) {
//        return this.checkMessageType(response, types, 0);
//    }


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

        boolean reJoined = Integer.parseInt(parts[3]) == 1;
		if(reJoined) player.setRoomID(0);

		return player;
	}

    public Room[] parseRoomList(String response){

	    // message split to blocks
        String[] parts = response.split(CommunicationConfig.MSG_DELIMITER);

//        if(!this.checkACK(parts))
//            return null;

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



//	public Room parseNewRoom(String response){
//
//		return this.parseRoomList(response, 2)[0];
//
//	}

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

        System.out.println("Moves:");
        System.out.println(Arrays.toString(moves));

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


//	private static int parseToInt(String str){
//		return Integer.parseInt(str);
//	}
//	
//	private static float parseToFloat(String str){
//		return Float.parseFloat(str);
//	}
	
	
	/* public CommunicationParser(){

	}
	
	public void setReceivedMsg(byte msgBuffer[]){
		this.msgBuffer = msgBuffer;
	}
	
	public Room[] parseServerSessionInfo(String msgBuffer){		
		Room sessions[];
		String parts[] = msgBuffer.split(";");
		
		int clientID = Integer.parseInt(parts[0]);
		Client.setClientID(clientID);
		
		int sessionCount = Integer.parseInt(parts[1]); 
		sessions = new Room[sessionCount];
		
		int paramCount = 3;
		for (int i=0; i<(parts.length-1)/paramCount; i++) {
			int id = Integer.parseInt(parts[i*paramCount + 2]);
			int players = Integer.parseInt(parts[i*paramCount + 3]);
			int diff = Integer.parseInt(parts[i*paramCount + 4]);
			sessions[i] = new Room(id, players, diff);
		}
		
		return sessions;
	}
	
	private ArrayList<Player> parsePlayerInfo(String parts[], int offset, int params){
		int playerCount = (parts.length - offset)/params;
		ArrayList<Player> players = new ArrayList<>();
		
		for(int i = 0; i < playerCount; i++) {
			int ID =  Integer.parseInt(parts[i * params + offset]);
			String nick = parts[i * params + offset + 1];
			String IP =  parts[i * params + offset + 2];
			int isOnline = Integer.parseInt(parts[i * params + offset + 3]);
			int isOnline = Integer.parseInt(parts[i * params + offset + 4]);
			players.add(new Player(ID, nick, IP, isOnline == 1, isOnline == 1));
		}
		
		return players;
	}
	
	public Room parseSessionInfo(String str){
		//ID klienta ; sessionID ; cas serveru ; sessionStartTime ; difficulty ; board dim ; playerCount ; N * (clientID ; cliNick ; cli addr)
		String parts[] = str.split(";");
		
		int idClient = Integer.parseInt(parts[0]);
		int sessionID = Integer.parseInt(parts[1]);
		String serverTime = parts[2];
		String sessionStartTime = parts[3];
		int difficulty = Integer.parseInt(parts[4]);
		int boardDim = Integer.parseInt(parts[5]);
		int playerCount = Integer.parseInt(parts[6]);
		ArrayList<Player> players = parsePlayerInfo(parts, 7, 5);
		
		return new Room(idClient, sessionID, playerCount, difficulty, boardDim, serverTime, sessionStartTime, players);
	} 
	
	public ArrayList<Player> parseSessionPlayerUpdate(String msg){
		String[] playerParts = msg.split(";");
		return parsePlayerInfo(playerParts, 0, 5);

		//int params = 5;
		//int playerCount = playerParts.length / params;
		//for(int i = 0; i < playerCount; i++) {
		//	int ID =  Integer.parseInt(playerParts[i * params]);
		//	String nick = playerParts[i * params + 1];
		//	String IP =  playerParts[i * params + 2];
		//	int isOnline = Integer.parseInt(playerParts[i * params + 3]);
		//	int isOnline = Integer.parseInt(playerParts[i * params + 4]);
		//	result.add(new PlayerClient(ID, nick, IP, isOnline == 1, isOnline == 1));
		//}
		
	}*/

}
