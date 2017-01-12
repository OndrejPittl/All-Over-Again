package cz.kiv.ups.model;


import cz.kiv.ups.config.ErrorConfig;

public enum Error {

    USERNAME_TAKEN (ErrorConfig.USERNAME_TAKEN),
    ROOM_JOIN (ErrorConfig.ROOM_JOIN);


    private String err;

    Error(String err) {
        this.err = err;
    }

    public String getErr(){
        return this.err;
    }
}
