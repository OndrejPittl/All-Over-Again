package application;
	
import javafx.stage.Stage;


public class Main extends javafx.application.Application {
	
//	private static Application app;
	
	private static Thread clientThread;
	
	private static Client clientRunnable;
	
	private static Thread guiControlThread;
	
	private static GUIController guiControlRunnable;
	
	private static Screen guiRunnable;

	
	
	public static void main(String[] args) {
		// Main.app = Application.getInstance();
		
		/**
		 * GUI controlling thread.
		 */
		Main.guiControlRunnable = new GUIController();
		Main.guiControlThread = new Thread(Main.guiControlRunnable);
		Main.guiControlThread.start();
		
		/**
		 * Client (non-GUI) thread.
		 */
		Main.clientRunnable = new Client();
		Main.clientThread = new Thread(Main.clientRunnable);
		Main.clientThread.start();
		
		/**
		 * Continues GUI thread.
		 */
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		//init gui
		Main.guiRunnable = new Screen(primaryStage);
	
		Main.guiControlRunnable.setScreen(Main.guiRunnable);
		
		//run gui
		Main.guiRunnable.run();
	}
}
