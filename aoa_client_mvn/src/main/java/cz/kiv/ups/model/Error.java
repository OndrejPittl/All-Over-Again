package cz.kiv.ups.model;


import cz.kiv.ups.config.ErrorConfig;

public enum Error {

    USERNAME_TAKEN (ErrorConfig.USERNAME_TAKEN),
    ROOM_JOIN_REFUSED(ErrorConfig.ROOM_JOIN_REFUSED),
    GAME_REPLAY_REFUSED (ErrorConfig.GAME_REPLAY_REFUSED);


    private String err;

    Error(String err) {
        this.err = err;
    }

    public String getErr(){
        return this.err;
    }
}
