package config;

public class Routes {
	
	/**
	 * 
	 */
	private static final String PROJECT_PREFIX = "/";
	
	private static final String LAYOUT_EXPANSION = ".fxml";
	
	private static final String STYLESHEET_EXPANSION = ".css";
	
	private static String[] DIRECTORIES = {
			Routes.PROJECT_PREFIX + "view/assets/",
			Routes.PROJECT_PREFIX + "view/assets/style/",
			Routes.PROJECT_PREFIX + "view/assets/layout/",
			Routes.PROJECT_PREFIX + "view/assets/images/",
	};
	
	
	public static String getAssetsDir(){
		return Routes.DIRECTORIES[0];
	}

	public static String getStyleDir(){
		return Routes.DIRECTORIES[1];
	}
	
	public static String getLayoutDir(){
		return Routes.DIRECTORIES[2];
	}
	
	public static String getImagesDir(){
		return Routes.DIRECTORIES[3];
	}
	
	public static String getLayoutFile(String filename){
		return Routes.getLayoutDir() + filename + Routes.LAYOUT_EXPANSION;
	}
	
	public static String getStyleFile(String filename){
		return Routes.getStyleDir() + filename + STYLESHEET_EXPANSION;
	}
	
//	public static String getImageFile(String filename){
//		return Routes.getImagesDir() + filename + STYLESHEET_EXPANSION;
//	}
}
