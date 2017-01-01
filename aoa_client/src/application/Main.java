package application;
	
import javafx.stage.Stage;
import partial.Tools;


public class Main extends javafx.application.Application {
	
	private static Application app;
	
	private static Thread clientThread;
	
	private static Client clientRunnable;
	
	private static Thread guiControlThread;
	
	private static GUIController guiControlRunnable;
	
	private static Screen guiRunnable;

	
	
	public static void main(String[] args) {
		// Main.app = Application.getInstance();

        if(!Main.checkArgs(args))
            return;

        Main.app = Application.getInstance();

		/**
		 * GUI controlling thread.
		 */
		Main.guiControlRunnable = new GUIController(Main.app);
		Main.guiControlThread = new Thread(Main.guiControlRunnable);
        Main.guiControlThread.setDaemon(true);  // umře, když umře Main Thrd
        Main.guiControlThread.start();

		/**
		 * Client (non-GUI) thread.
		 */
		Main.clientRunnable = new Client(args, Main.app);
		Main.clientThread = new Thread(Main.clientRunnable);
        Main.clientThread.setDaemon(true);  // umře, když umře Main Thrd
		Main.clientThread.start();

		/**
		 * Continues GUI thread.
		 */
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		//init gui
		Main.guiRunnable = new Screen(primaryStage, Main.app);
	
		Main.guiControlRunnable.setScreen(Main.guiRunnable);

        Application.awaitAtGuiBarrier("GUI Thrd releases GUIControl after init.");

		//run gui
		Main.guiRunnable.run();
	}

	private static boolean checkArgs(String[] args){
        String ip, port;

	    if(args.length != 2) {
            System.out.println("INVALID NUMBER OF ARGUMENTS.");
            return false;
        }

        ip = args[0];
        port = args[1];

        if(!Tools.isValidPort(port)) {
            System.out.println("INVALID PORT.");
            return false;
        }

        if(!Tools.isValidIP(ip)){
            System.out.println("INVALID IP.");
            return false;
        }

	    return true;
    }
}
