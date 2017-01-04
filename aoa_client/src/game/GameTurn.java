package game;


import config.CommunicationConfig;

import java.util.ArrayList;

public class GameTurn {

    private int activePlayerID;

    private int time;

    private GameMove[] moves;


    public GameTurn(int activePlayerID, int time, GameMove[] moves) {
        this.activePlayerID = activePlayerID;
        this.time = time;
        this.moves = moves;
    }

    public int getActivePlayerID() {
        return activePlayerID;
    }

    public int getTime() {
        return time;
    }

    public GameMove[] getMoves() {
        return moves;
    }

    public void setMoves(ArrayList<GameMove> moves) {
        int moveCount = moves.size();
        GameMove[] mvs = new GameMove[moveCount];

        for (int i = 0; i < moveCount; i++) {
            mvs[i] = moves.get(i);
        }

        this.moves = mvs;
    }
}
