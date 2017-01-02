package model;

import javafx.scene.paint.Color;

/**
 * Created by OndrejPittl on 01.01.17.
 */
public enum GameColors {

    GOLD (Color.web("EDAC03")),
    GREEN (Color.web("276230")),
    BLUE (Color.web("005688")),
    RED (Color.web("C13524"));

    private Color color;


    GameColors(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
