package cz.kiv.ups.application;

import cz.kiv.ups.config.LogConfig;
import cz.kiv.ups.partial.LogFormatter;
import cz.kiv.ups.partial.LogLevel;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;

public class Logger {

    private static Logger INSTANCE;

    private static boolean isLogging;

    private static java.util.logging.Logger consoleLogger;

    private static java.util.logging.Logger fileLogger;

	private static ConsoleHandler consoleHandler;

	private static FileHandler fileHandler;




	private Logger() {
        this.enableLogging();
        this.initConsoleLogger();
        this.initFileLogger();
    }

    private void initConsoleLogger(){
        Logger.consoleLogger = java.util.logging.Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName() + "(console)");
        Logger.consoleLogger.setUseParentHandlers(false);
        Logger.consoleHandler = new ConsoleHandler();
        Logger.consoleHandler.setFormatter(new LogFormatter());
        Logger.consoleLogger.addHandler(Logger.consoleHandler);

    }

    private void initFileLogger(){
        Logger.fileLogger = java.util.logging.Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName() + "(file)");
        Logger.fileLogger.setUseParentHandlers(false);

        try {
            Logger.fileHandler = new FileHandler(LogConfig.LOG_FILENAME, true);
            Logger.fileHandler.setFormatter(new LogFormatter());
            Logger.fileLogger.addHandler(Logger.fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public static synchronized Logger getLogger() {
		if(Logger.INSTANCE == null)
			Logger.INSTANCE = new Logger();
		return Logger.INSTANCE;
	}

	private synchronized void log(String msg, LogLevel lvl) {
        switch (lvl) {
            case INFO:
                if(Logger.isLogging) Logger.consoleLogger.info(msg);
                Logger.fileLogger.info(msg);
                break;
            case DEBUG:
                if(LogConfig.DEVELOPER_MODE) Logger.consoleLogger.info(msg);
                Logger.fileLogger.info(msg);
                break;
            case ERROR:
                if(Logger.isLogging)Logger.consoleLogger.severe(msg);
                Logger.fileLogger.severe(msg);
                break;
        }
	}

    public synchronized void error(String msg) {
        this.log(msg, LogLevel.ERROR);
    }

    public synchronized void info(String msg) {
        this.log(msg, LogLevel.INFO);
    }

    public synchronized void debug(String msg) {
        this.log(msg, LogLevel.DEBUG);
    }

	public static void disableLogging(){
		Logger.isLogging = false;
	}

	public static void enableLogging(){
		Logger.isLogging = true;
	}
	
}
