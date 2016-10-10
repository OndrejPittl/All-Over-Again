package model;

import config.AppConfig;

public enum ScreenType {
	
	/**
	 * Scene where a new player logs in.
	 */
	Login(0, "login", "Login"),
	
	/**
	 * Scene where a player creates a new game or selects an existing one.
	 */
	GameCenter(1, "game-center", "Game Center"),
	
	/**
	 * Scene indicating user that game is being prepared == waiting for players.
	 */
	Initializing (2, "initializing", "Initializing a game..."),
	
	/**
	 * Scene where players play a game.
	 */
	Game (3, "playground"),
	
	/**
	 * Scene with result of a game.
	 */
	GameResult (4, "results", "Game Results"),

	/**
	 * Scene with a message.
	 */
	Message (5, "message");
	
	/**
	 * ID of a scene.
	 */
	private int id;
	
	/**
	 * Name of a scene – internal string representation.
	 */
	private String name;
	
	/**
	 * Title of a scene – public.
	 */
	private String title;
	
	
	private ScreenType(int id, String name) {
		this.id = id;
		this.name = name;
		this.title = AppConfig.APP_NAME;
	}
	
	private ScreenType(int id, String name, String title) {
		this.id = id;
		this.name = name;
		this.title = title + " | " + AppConfig.APP_NAME;
	}
	
	


	/**
	 * @return the ID
	 */
	public int getID() {
		return id;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	
	public String getTitle(){
		return this.title;
	}

}
