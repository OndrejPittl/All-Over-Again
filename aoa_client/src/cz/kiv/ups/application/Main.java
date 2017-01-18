package cz.kiv.ups.application;
	
import cz.kiv.ups.config.ConnectionConfig;
import cz.kiv.ups.validation.ClientOptions;
import javafx.stage.Stage;
import cz.kiv.ups.partial.Tools;


public class Main extends javafx.application.Application {

    private static Logger logger = Logger.getLogger();


	private static String[] args;

	private static ClientOptions opts;

    private static String ip;

    private static int port;




    private static Application app;
	
	private static Thread clientThread;
	
	private static Client clientRunnable;
	
	private static Thread guiControlThread;
	
	private static GUIController guiControlRunnable;
	
	private static Screen guiRunnable;

	
	
	public static void main(String[] args) {

	    logger.info("----------------------------------");
	    logger.info("----    running AOA client    ----");
        logger.info("----------------------------------");

        Main.opts = new ClientOptions(args);
        Main.handleInputArguments();

        Main.app = Application.getInstance();
		Main.args = args;
		Main.startApp();
	}

	private static void handleInputArguments(){
        // help?
	    if(Main.opts.has(ClientOptions.OPT_HELP)) {
            Main.opts.printHelp();
            Application.disconnect(true, null);
        }

        // ip?
        if(Main.opts.has(ClientOptions.OPT_IP)) {
            Main.ip = Main.opts.get(ClientOptions.OPT_IP);
            logger.info("ip:    " + Main.ip + " (accepted)");
        } else {
            Main.ip = ConnectionConfig.DEFAULT_SERVER_IP;
            logger.info("ip:    " + Main.ip + " (default)");
        }

        //port?
        if(Main.opts.has(ClientOptions.OPT_PORT)) {
            Main.port = Integer.parseInt(Main.opts.get(ClientOptions.OPT_PORT));
            logger.info("port:  " + Main.port+ " (accepted)");
        } else {
            Main.port= ConnectionConfig.DEFAULT_SERVER_PORT;
            logger.info("port:  " + Main.port+ " (default)");
        }

        // quiet?
        if(Main.opts.has(ClientOptions.OPT_QUIET)) {
            boolean quiet = Integer.parseInt(Main.opts.get(ClientOptions.OPT_QUIET)) == 1;
            logger.info("quiet: active (accepted)");
            logger.info("----------------------------------");
            if(quiet) Logger.disableLogging();
        } else {
            logger.info("quiet: not active (default)");
            logger.info("----------------------------------");
        }


        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


	@Override
	public void start(Stage primaryStage) throws Exception {
		//init gui
		Main.guiRunnable = new Screen(primaryStage, Main.app);

		Main.guiControlRunnable.setScreen(Main.guiRunnable);

		Application.awaitAtGuiBarrier("GUI releases GUIControl after init.");
	}

	public static void startApp(){

		/**
		 * GUI controlling thread.
		 */
		Main.guiControlRunnable = new GUIController(Main.app);
		Main.guiControlThread = new Thread(Main.guiControlRunnable);
		Main.guiControlThread.start();

		/**
		 * Client (non-GUI) thread.
		 */
		Main.clientRunnable = new Client(Main.app, Main.ip, Main.port);
		Main.clientThread = new Thread(Main.clientRunnable);
		Main.clientThread.start();

		/**
		 * Continues GUI thread.
		 */
		launch(args);

	}

//	public static void stopApp(){
//		if(Main.running) {
//            Main.clientThread.interrupt();
//            Main.guiControlThread.interrupt();
//			Main.running = false;
//		}
//	}

//    public static void restartApp(){
//	    Main.stopApp();
//        Main.startApp();
//	}
}
