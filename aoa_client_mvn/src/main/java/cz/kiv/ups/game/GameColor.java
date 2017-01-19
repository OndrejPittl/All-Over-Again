package cz.kiv.ups.game;

import cz.kiv.ups.config.GameConfig;
import javafx.scene.paint.Color;


public enum GameColor implements IGameList {

    GOLD (0, GameConfig.GOLD),
    GREEN (1, GameConfig.GREEN),
    BLUE (2, GameConfig.BLUE),
    RED (3, GameConfig.RED);


    private static final int COUNT = GameColor.values().length;

    private Color color;

    private int index;


    GameColor(int index, Color color) {
        this.index = index;
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public IGameList at(int index) {
        return GameColor.values()[index];
    }

    @Override
    public IGameList first() {
        return this.at(0);
    }

    @Override
    public IGameList last() {
        return this.at(COUNT - 1);
    }

    @Override
    public IGameList next() {
        int index = (this.getIndex() + 1)  % COUNT;
        return this.at(index);
    }

    @Override
    public IGameList previous() {
        int index = this.getIndex() - 1;
        return index < 0 ? this.last() : this.at(index);
    }

    public static int count() {
        return GameColor.COUNT;
    }

    public static GameColor nth(int index) {
        return GameColor.values()[index];
    }
}
