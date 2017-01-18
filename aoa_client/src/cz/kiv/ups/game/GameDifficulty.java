package cz.kiv.ups.game;

public enum GameDifficulty {
	
	EASY (0, "easy"),

	NORMAL(1, "normal"),

	EXPERT(2, "expert");
	
	private int difficulty;
	
	private String title;
	
	GameDifficulty(int diff, String title) {
		this.difficulty = diff;
		this.title = title;
	}

	/**
	 * @return the difficulty
	 */
	public int getDifficulty() {
		return difficulty;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	public static GameDifficulty getNth(int diff){
		return GameDifficulty.values()[diff];
	}

	public boolean isGreaterEqualThan(GameDifficulty diff) {
		return this.getDifficulty() >= diff.getDifficulty();
	}

	public String toString(){
		return this.title;
	}
}
