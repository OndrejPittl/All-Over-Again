package config;

import javafx.geometry.Dimension2D;
import model.ScreenSettings;
import model.ScreenType;

public class ViewConfig {

	public static final String MSG_CONNECTION = "Connecting...";
	
	public static final String MSG_CHECKING = "Checking...";
	
	
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
			new Dimension2D(700, 400),
			new Dimension2D(700, 400)
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
			new Dimension2D(450, 300),
			new Dimension2D(450, 330)
		),
	};
	
	public static ScreenSettings getScreen(ScreenType screen){
		return ViewConfig.SCREENS[screen.getID()];
	}
	
}
