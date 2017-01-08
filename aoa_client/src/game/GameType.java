package game;

public enum GameType {
    SINGLEPLAYER    (1, "singleplayer"),
    MULTIPLAYER     (2, "multiplayer");

    private int playerCount;

    private String title;

    GameType(int playerCount, String title) {
        this.playerCount = playerCount;
        this.title = title;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static GameType getNth(int count){
        return GameType.values()[count];
    }

    public String toString(){
        return this.title;
    }
}
