package config;

import javafx.geometry.Dimension2D;
import model.ScreenSettings;
import model.ScreenType;

public class ViewConfig {

	public static final String MSG_CONNECTION = "Connecting...";
	
	public static final String MSG_CHECKING = "Checking...";

	public static final String MSG_WAITING_GAME_INIT = "Please wait, your game is being prepared...";

	public static final String MSG_STATUS_ONLINE = "online";





	public static final String LAYOUT_PARTIAL_PLAYER_RECORD = "player";

	public static final String LAYOUT_PARTIAL_BOARD_FIELD= "board_field";



	public static final String LAYOUT_IMAGE_LOADER = "loading.gif";

	public static final String LAYOUT_IMAGE_ACTIVE_PLAYER = "crown.png";

	public static final String LAYOUT_IMAGE_ARROW = "arrow.png";



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
			ScreenType.Playground,
			new Dimension2D(750, 550),
			new Dimension2D(750, 550)
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
