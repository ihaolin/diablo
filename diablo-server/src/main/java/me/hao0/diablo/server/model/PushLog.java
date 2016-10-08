package me.hao0.diablo.server.model;

import java.util.Date;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class PushLog implements Model<Long> {

    private static final long serialVersionUID = 424245674354391177L;

    private Long id;

    /**
     * The app id
     */
    private Long appId;

    /**
     * The config name
     */
    private String config;

    /**
     * The server pushing
     */
    private String server;

    /**
     * The client pushed
     */
    private String client;

    private Date ctime;

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Date getCtime() {
        return ctime;
    }

    @Override
    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    @Override
    public void setUtime(Date utime) {}

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    @Override
    public String toString() {
        return "PushLog{" +
                "id=" + id +
                ", appId=" + appId +
                ", config='" + config + '\'' +
                ", server='" + server + '\'' +
                ", client='" + client + '\'' +
                ", ctime=" + ctime +
                '}';
    }
}
