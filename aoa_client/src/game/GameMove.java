package game;

import config.CommunicationConfig;
import config.GameConfig;


public class GameMove {

    private static int NONE = -1;

    private int index = NONE;

    private GameColor color = null;

    private GameSymbol symbol = null;


    public GameMove(int index) {
        this.index = index;
    }

    public GameMove(int index, GameColor color) {
        this.index = index;
        this.color = color;
    }

    public GameMove(int index, GameColor color, GameSymbol symbol) {
        this.index = index;
        this.color = color;
        this.symbol = symbol;
    }

    public GameMove(int[] attrs) {
        this.init();
        for (int a = 0; a <attrs.length; a++) {
            int val = attrs[a];
            if(val >= 0) {
                if(a == 0){
                    this.index = val;
                } else if(a == 1){
                    this.color = GameColor.nth(val);
                } else {
                    this.symbol = GameSymbol.nth(val);
                }
            }
        }
    }

    private void init(){
        this.color = GameColor.GOLD;
        this.symbol = GameSymbol.Symbol1;
    }

    public int getIndex() {
        return index;
    }

    public GameColor getColor() {
        return color;
    }

    public GameSymbol getSymbol() {
        return symbol;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    private boolean hasColor(){
        return this.color != null;
    }

    private boolean hasSymbol(){
        return this.symbol != null;
    }

    public String serialize(){
        String out = String.valueOf(this.index);

        if(this.hasColor()){
            out += CommunicationConfig.MSG_DELIMITER + this.color.getIndex();
        }

        if(this.hasSymbol()){
            out += CommunicationConfig.MSG_DELIMITER + this.symbol.getIndex();
        }

        return out;
    }

    public String toString(){
        return this.serialize();
    }
}
