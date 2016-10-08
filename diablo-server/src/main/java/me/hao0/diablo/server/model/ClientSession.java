package me.hao0.diablo.server.model;

import java.util.Date;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class ClientSession {

    private String id;

    private long appId;

    private String appKey;

    private int ip;

    private int pid;

    private Date uptime;

    public ClientSession(String id, long appId, String appKey, int ip, int pid) {
        this.id = id;
        this.appId = appId;
        this.appKey = appKey;
        this.ip = ip;
        this.pid = pid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public int getIp() {
        return ip;
    }

    public void setIp(int ip) {
        this.ip = ip;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public Date getUptime() {
        return uptime;
    }

    public void setUptime(Date uptime) {
        this.uptime = uptime;
    }

    @Override
    public String toString() {
        return "ClientSession{" +
                "id='" + id + '\'' +
                ", appId=" + appId +
                ", appKey='" + appKey + '\'' +
                ", ip=" + ip +
                ", pid=" + pid +
                ", uptime=" + uptime +
                '}';
    }
}
