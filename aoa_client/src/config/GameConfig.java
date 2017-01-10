package config;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import game.GameColor;
import game.GameSymbol;


public class GameConfig {

    public static final GameColor DEFAULT_COLOR = GameColor.GOLD;
    public static final GameSymbol DEFAULT_SYMBOL = GameSymbol.Symbol1;
    public static final int DEFAULT_BOARD_DIMENSION = 3;

    public static final Color GOLD = Color.web("EDAC03");
    public static final Color GREEN = Color.web("276230");
    public static final Color BLUE = Color.web("005688");
    public static final Color RED = Color.web("C13524");




    public static final Image[][] SYMBOLS = {
        {
            // GOLD (0, Color.web("EDAC03")),
            new Image(Routes.getSymbolPath(0,0)),
            new Image(Routes.getSymbolPath(0,1)),
            new Image(Routes.getSymbolPath(0,2)),
            new Image(Routes.getSymbolPath(0,3)),
            new Image(Routes.getSymbolPath(0,4)),

        }, {
            // GREEN (1, Color.web("276230")),
            new Image(Routes.getSymbolPath(1,0)),
            new Image(Routes.getSymbolPath(1,1)),
            new Image(Routes.getSymbolPath(1,2)),
            new Image(Routes.getSymbolPath(1,3)),
            new Image(Routes.getSymbolPath(1,4)),
        }, {
            // BLUE (2, Color.web("005688")),
            new Image(Routes.getSymbolPath(2,0)),
            new Image(Routes.getSymbolPath(2,1)),
            new Image(Routes.getSymbolPath(2,2)),
            new Image(Routes.getSymbolPath(2,3)),
            new Image(Routes.getSymbolPath(2,4)),
        }, {
            // RED (3, Color.web("C13524"));
            new Image(Routes.getSymbolPath(3,0)),
            new Image(Routes.getSymbolPath(3,1)),
            new Image(Routes.getSymbolPath(3,2)),
            new Image(Routes.getSymbolPath(3,3)),
            new Image(Routes.getSymbolPath(3,4)),
        }
    };

    public static Image getSymbolImage(int c, int s){
        return GameConfig.SYMBOLS[c][s];
    }
}
