package model;

public enum GameDifficulty {
	
	EASY (1, "easy"),

	NORMAL(2, "normal"),

	EXPERT(3, "expert");

	
	
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
		return GameDifficulty.values()[diff - 1];
	}
	

}
