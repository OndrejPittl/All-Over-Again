package cz.kiv.ups.application;

public class Logger {
	
	
	public static void logConnectionFailed(int count){
		System.out.println("CLI: " + count + ". connection try failed.");
	}
	
	public static void logConnectionSucceeded(String addr, String name){
		System.out.println("CLI: Connected to : " + addr + " named: " + name);
	}
	
	public static void logHelloFailed(int count){
		System.out.println("CLI: " + count + ". hello handshake failed.");
	}
	
	public static void logHelloSucceeded(){
		System.out.println("CLI: Handshake succeeded.");
	}
	
	
	
}
