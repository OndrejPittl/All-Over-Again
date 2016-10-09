package config;

import javafx.geometry.Dimension2D;

public class AppConfig {
	
	/**
	 * 
	 */
	public static final String APP_NAME = "All Over Again";
	
	/**
	 * 
	 */
	public static final String APP_NAME_SHORT = "AOA";
	
	/**
	 * 
	 */
	private static final ScreenConfig[] SCREENS = {
		new ScreenConfig(
			ScreenEnum.Login,
			new Dimension2D(400, 300),
			new Dimension2D(350, 330)
		), new ScreenConfig(
			ScreenEnum.GameCenter,
			new Dimension2D(400, 300),
			new Dimension2D(350, 330)
		), new ScreenConfig(
			ScreenEnum.Initializing,
			new Dimension2D(400, 300),
			new Dimension2D(350, 330)
		), new ScreenConfig(
			ScreenEnum.Game,
			new Dimension2D(400, 300),
			new Dimension2D(350, 330)
		), new ScreenConfig(
			ScreenEnum.GameResult,
			new Dimension2D(400, 300),
			new Dimension2D(350, 330)
		),
	};
	
	
	
	
	
	
	
	
	public static ScreenConfig getScreen(ScreenEnum screen){
		return AppConfig.SCREENS[screen.getID()];
	}
	

}
