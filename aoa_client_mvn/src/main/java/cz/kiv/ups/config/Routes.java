package cz.kiv.ups.config;

public class Routes {
	
	/**
	 * 
	 */
	private static final String PROJECT_PREFIX = "/";
	
	private static final String LAYOUT_EXTENSION = ".fxml";
	
	private static final String STYLESHEET_EXTENSION = ".css";

	private static final String IMG_PNG_EXTENSION = ".png";

	private static String[] DIRECTORIES = {
			Routes.PROJECT_PREFIX + "view/assets/",
			Routes.PROJECT_PREFIX + "view/assets/style/",
			Routes.PROJECT_PREFIX + "view/assets/layout/",
			Routes.PROJECT_PREFIX + "view/assets/layout/partial/",
			Routes.PROJECT_PREFIX + "view/assets/images/",
			Routes.PROJECT_PREFIX + "view/assets/images/symbols/",
	};

	// layout
	public static final String LAYOUT_PARTIAL_PLAYER_RECORD = "player";

	public static final String LAYOUT_PARTIAL_BOARD_FIELD= "board_field";


	// images
	public static final String IMG_LOADER = "loading.gif";

	public static final String IMG_ACTIVE_PLAYER = "crown.png";

	public static final String IMG_ARROW = "arrow.png";

	public static final String IMG_LEGEND = "legend.png";



    public static final String[] IMG_COLOR_PREFIXES = {
            "gold", "green", "blue", "red"
    };

    public static final String[] IMG_SYMBOL_SUFIXES = {
            "symbol1", "symbol2", "symbol3", "symbol4", "symbol5"
    };


	public static String getStyleDir(){
		return Routes.DIRECTORIES[1];
	}

	public static String getLayoutDir(){
		return Routes.DIRECTORIES[2];
	}

	public static String getPartialLayoutDir(){
		return Routes.DIRECTORIES[3];
	}

    public static String getImagesDir(){
        return Routes.DIRECTORIES[4];
    }

    public static String getSymbolImagesDir(){
        return Routes.DIRECTORIES[5];
    }


	public static String getLayoutFile(String filename){
		return Routes.getLayoutDir() + filename + Routes.LAYOUT_EXTENSION;
	}

	public static String getPartialLayoutFile(String filename){
		return Routes.getPartialLayoutDir() + filename + Routes.LAYOUT_EXTENSION;
	}
	
	public static String getStyleFile(String filename){
		return Routes.getStyleDir() + filename + STYLESHEET_EXTENSION;
	}

    // images - symbols
    public static final String getSymbolPath(int c, int s) {
	    return Routes.getSymbolImagesDir() + IMG_COLOR_PREFIXES[c] + "_" + IMG_SYMBOL_SUFIXES[s] + IMG_PNG_EXTENSION;
    }
}
