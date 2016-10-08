package me.hao0.diablo.common.model;

import java.io.Serializable;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class ServerRouteResp implements Serializable {

    private static final long serialVersionUID = -6796772429161382925L;

    /**
     * The server routed
     */
    private String server;

    /**
     * The client id
     */
    private String clientId;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public String toString() {
        return "ServerRouteResp{" +
                "server='" + server + '\'' +
                ", clientId='" + clientId + '\'' +
                '}';
    }
}
