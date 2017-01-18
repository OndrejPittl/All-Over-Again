package cz.kiv.ups.game;

public enum GameSymbol implements IGameList {

    Symbol1(0),
    Symbol2(1),
    Symbol3(2),
    Symbol4(3),
    Symbol5(4);

    private static final int COUNT = GameSymbol.values().length;

    private int index;


    GameSymbol(int index) {
        this.index = index;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public IGameList at(int index) {
        return GameSymbol.values()[index];
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

    public static int count(){
        return GameSymbol.COUNT;
    }

    public static GameSymbol nth(int s) {
        return GameSymbol.values()[s];
    }
}
