package cz.kiv.ups.application;

public class _Developer {

	private static Logger logger = Logger.getLogger();

	public static void threadExecEnds(String thrd){
		for (int i = 0; i < 10; i++) {
			try {
				logger.debug(thrd + " ends execution.");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
