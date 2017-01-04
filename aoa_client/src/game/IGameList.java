package game;

/**
 * Created by OndrejPittl on 02.01.17.
 */
public interface IGameList {

    IGameList at(int index);
    IGameList first();
    IGameList last();
    IGameList next();
    IGameList previous();
    int getIndex();
}
