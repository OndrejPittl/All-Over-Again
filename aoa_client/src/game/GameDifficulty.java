package game;

public enum GameDifficulty {
	
	EASY (0, "easy"),

	NORMAL(1, "normal"),

	EXPERT(2, "expert");

	
	
	private int difficulty;
	
	private String title;
	
	private GameDifficulty(int diff, String title) {
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
	 * @param difficulty the difficulty to set
	 */
	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
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
