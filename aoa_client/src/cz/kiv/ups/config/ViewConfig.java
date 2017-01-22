package cz.kiv.ups.config;

import javafx.geometry.Dimension2D;
import cz.kiv.ups.model.ScreenSettings;
import cz.kiv.ups.model.ScreenType;
import javafx.scene.paint.Color;

public class ViewConfig {

	public static final String MSG_STARTING = "Starting application.";

	public static final String MSG_CONNECTION = "Connecting...";

	public static final String MSG_CHECKING = "Checking...";

    public static final String MSG_GAMECENTER_NO_ROOM = "Sorry bro, nobody wants to play with you.";

	public static final String MSG_WAITING_GAME_INIT = "Please wait, your game is being prepared...";

	public static final String MSG_STATUS_ONLINE = "(online)";

	public static final String MSG_STATUS_OFFLINE = "(offline)";

	public static final String MSG_GAME_WIN = "You won!";

	public static final String MSG_GAME_LOSE = "You lost.";

	public static final String MSG_GAME_END = "Game ends.";

	public static final String MSG_ASK_OPPONENT_LEFT_HEADER = "Oops!";

   // retain honor == zachovat čest
   public static final String MSG_ASK_OPPONENT_LEFT_CONTENT = "Your opponent has gone offline. There's a chance they return in a while and retain their honor. Would you like to wait?\n\n";

   public static final String MSG_ASK_OPPONENT_LEFT_TITLE = "Your opponent left.";
    public static final String MSG_ASK_YES = "Yes, I wanna win!";

   public static final String MSG_ASK_NO = "Nope. I'm looser.";



	public static final String MSG_SERVER_SUSPICIOUS = "Server cannot be authorized and was marked as suspicious (after 5 incorrect messages). I am disconnecting.";




	public static final int TIMER_TURN_INTRO_MOVE_DURATION = 1000;

	public static final Color CORRECT = Color.web("18b639");



	public static final Color INCORRECT = Color.web("a61212");
    /**
	 *
	 */
	private static final ScreenSettings[] SCREENS = {
		new ScreenSettings(
			ScreenType.Login,
			new Dimension2D(450, 300),
			new Dimension2D(350, 330)
		), new ScreenSettings(
			ScreenType.GameCenter,
			new Dimension2D(700, 450),
			new Dimension2D(700, 450)
		), new ScreenSettings(
			ScreenType.Initializing,
			new Dimension2D(450, 300),
			new Dimension2D(350, 330)
		), new ScreenSettings(
			ScreenType.Playground,
			new Dimension2D(850, 650),
			new Dimension2D(850, 650)
		), new ScreenSettings(
			ScreenType.GameResult,
			new Dimension2D(850, 650),
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
