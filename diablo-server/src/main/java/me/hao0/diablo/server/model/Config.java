package me.hao0.diablo.server.model;

import java.util.Date;

/**
 * Config Item
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class Config implements Model<Long> {

    private static final long serialVersionUID = -5129757981000580407L;

    /**
     * primary key
     */
    private Long id;

    /**
     * app id
     */
    private Long appId;

    /**
     * config item name
     */
    private String name;

    /**
     * config item value
     */
    private String value;

    /**
     * config item md5,
     */
    private String md5;

    /**
     * create time
     */
    private Date ctime;

    /**
     * update time
     */
    private Date utime;

    public Config() {
    }

    public Config(Long appId, String name, String value) {
        this.appId = appId;
        this.name = name;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
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
        return "Config{" +
                "id=" + id +
                ", appId=" + appId +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", md5='" + md5 + '\'' +
                ", ctime=" + ctime +
                ", utime=" + utime +
                '}';
    }
}
