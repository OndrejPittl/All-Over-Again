#ifndef GAME_STATUS_H
#define GAME_STATUS_H


enum class GameStatus {

    /**
     *  Players getting connected.
     */
     CONNECTING,

    /**
     * All players got connected.
     */
    READY,

    /**
     * A game is getting started.
     */
    STARTED,

    /**
     * A game has started and a game is in a progress;
     */
    PLAYING,

    /**
     * Waiting on a player who got lost.
     */
    WAITING,

    /**
     * Game is over, we've got a winner.
     */
    FINISHED,

    /**
     * Game is over, wanna replay.
     */
    FINISHED_REPLAY,

    /**
     * Game is over, wanna end.
     */
    FINISHED_END
};


#endif

