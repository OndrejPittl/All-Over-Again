package config;

import javafx.geometry.Dimension2D;
import model.ScreenType;
import model.ScreenSettings;

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
	private static final ScreenSettings[] SCREENS = {
		new ScreenSettings(
			ScreenType.Login,
			new Dimension2D(400, 300),
			new Dimension2D(350, 330)
		), new ScreenSettings(
			ScreenType.GameCenter,
			new Dimension2D(400, 300),
			new Dimension2D(350, 330)
		), new ScreenSettings(
			ScreenType.Initializing,
			new Dimension2D(400, 300),
			new Dimension2D(350, 330)
		), new ScreenSettings(
			ScreenType.Game,
			new Dimension2D(400, 300),
			new Dimension2D(350, 330)
		), new ScreenSettings(
			ScreenType.GameResult,
			new Dimension2D(400, 300),
			new Dimension2D(350, 330)
		), new ScreenSettings(
			ScreenType.Message,
			new Dimension2D(400, 300),
			new Dimension2D(350, 330)
		),
	};
	
	
	
	
	
	
	
	
	public static ScreenSettings getScreen(ScreenType screen){
		return AppConfig.SCREENS[screen.getID()];
	}
	

}
