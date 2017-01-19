package cz.kiv.ups.game;

public interface IGameList {

    IGameList at(int index);
    IGameList first();
    IGameList last();
    IGameList next();
    IGameList previous();
    int getIndex();
}
