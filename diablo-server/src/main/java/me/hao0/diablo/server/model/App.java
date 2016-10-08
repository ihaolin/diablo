package me.hao0.diablo.server.model;

import java.util.Date;

/**
 * App Info
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class App implements Model<Long> {

    private static final long serialVersionUID = 2730662650309229394L;

    /**
     * primary key
     */
    private Long id;

    /**
     * app name
     */
    private String appName;

    /**
     * app key
     */
    private String appKey;

    /**
     * app desc
     */
    private String appDesc;

    private Date ctime;

    private Date utime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppDesc() {
        return appDesc;
    }

    public void setAppDesc(String appDesc) {
        this.appDesc = appDesc;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public Date getUtime() {
        return utime;
    }

    public void setUtime(Date utime) {
        this.utime = utime;
    }

    @Override
    public String toString() {
        return "App{" +
                "id=" + id +
                ", appName='" + appName + '\'' +
                ", appKey='" + appKey + '\'' +
                ", appDesc='" + appDesc + '\'' +
                ", ctime=" + ctime +
                ", utime=" + utime +
                '}';
    }
}
