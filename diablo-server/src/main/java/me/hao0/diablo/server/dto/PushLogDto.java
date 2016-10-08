package me.hao0.diablo.server.dto;

import java.io.Serializable;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class PushLogDto implements Serializable {

    private static final long serialVersionUID = 5724396193945809793L;

    private String config;

    private String client;

    private String server;

    private String time;

    public PushLogDto() {
    }

    public PushLogDto(String config, String client, String server, String time) {
        this.config = config;
        this.client = client;
        this.server = server;
        this.time = time;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

