package communication;

import config.CommunicationConfig;
import model.GameDifficulty;
import model.Player;
import model.Room;
import partial.Tools;

import java.util.Arrays;

public class CommunicationParser {
	
	private byte[] msgBuffer;
	
	
	


	private boolean checkACK(String response, int index){
        String[] parts = response.split(CommunicationConfig.MSG_DELIMITER);
        return parts[index].equals(CommunicationConfig.REQ_ACK);
    }

    private boolean checkACK(String response){
	    return this.checkACK(response, 1);
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
	public boolean parseUsernameAvailabilityResponse(String response, Player player){
		String[] parts = response.split(CommunicationConfig.MSG_DELIMITER);
		
		boolean ack = Integer.parseInt(parts[1]) == 1;
		
		if(!ack)
			return false;
		
		int id = Integer.parseInt(parts[2]);
		player.setID(id);
		
		return true;
	}

    public Room[] parseRoomList(String response){
	    return this.parseRoomList(response, 1);
    }

	public Room[] parseRoomList(String response, int offset) {

		// message split to blocks
		String[] parts = response.split(CommunicationConfig.MSG_DELIMITER);
		
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
				playerLimit = Integer.parseInt(parts[i+2]),
				difficulty = Integer.parseInt(parts[i+3]),
				dimension = Integer.parseInt(parts[i+4]);

			Player[] players = Player.parsePlayers(parts[i+5], CommunicationConfig.MSG_SUB_DELIMITER);
				
			r.setID(id);
			r.setPlayerCount(playerCount);
			r.setPlayerLimit(playerLimit);
			r.setDifficulty(GameDifficulty.getNth(difficulty));
			r.setBoardDimension(dimension);
			r.setPlayers(players);
			
			rooms[roomIndex++] = r;
		}
		
		return rooms;
	}

	public Room parseSelectedRoom(String response){

        if(!this.checkACK(response))
            return null;

        return this.parseRoomList(response, 2)[0];
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

    public int[][] parseTurnInfo(String response) {

	    // message split to blocks
        String[] parts = response.split(CommunicationConfig.MSG_DELIMITER);

        int offset = 2,
            turn = Integer.parseInt(parts[1]),
            diff = (parts.length - offset)/turn;

        int[][] moves = new int[turn][diff];

        for (int t = 0; t < turn; t++) {
            for (int d = 0; d < diff; d++) {
                int index = t * diff + d + offset;
                moves[t][d] = Integer.parseInt(parts[index]);
            }
        }

        System.out.println("Moves:");
        System.out.println(Arrays.toString(moves));

        return moves;
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
			int isActive = Integer.parseInt(parts[i * params + offset + 4]);
			players.add(new Player(ID, nick, IP, isOnline == 1, isActive == 1));
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
		//	int isActive = Integer.parseInt(playerParts[i * params + 4]);
		//	result.add(new PlayerClient(ID, nick, IP, isOnline == 1, isActive == 1));
		//}
		
	}*/

}
