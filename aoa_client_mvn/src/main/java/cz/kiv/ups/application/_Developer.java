package cz.kiv.ups.application;

public class _Developer {

	public static final boolean PRINT_BARRIERS = false;

	public static void threadExecEnds(String thrd){
		for (int i = 0; i < 10; i++) {
			try {
				System.out.println(thrd + " ends execution.");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}