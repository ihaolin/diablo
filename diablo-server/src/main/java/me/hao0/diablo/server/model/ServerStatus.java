package me.hao0.diablo.server.model;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public enum ServerStatus {

    OFFLINE(0, "server.offline"),

    ONLINE(1, "server.online");

    private int status;

    private String code;

    ServerStatus(int status, String code){
        this.status = status;
        this.code = code;
    }

    public int status(){
        return status;
    }

    public String code(){
        return code;
    }

    @Override
    public String toString() {
        return "ServerStatus{" +
                "status=" + status +
                ", code='" + code + '\'' +
                '}';
    }
}
