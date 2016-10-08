package me.hao0.diablo.common.model;

import java.io.Serializable;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class ConfigItem implements Serializable {

    private static final long serialVersionUID = 7713908862952122332L;

    /**
     * config item name
     */
    private String name;

    /**
     * config item value
     */
    private String value;

    /**
     * config item md5
     */
    private String md5;

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

    @Override
    public String toString() {
        return "ConfigItem{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", md5='" + md5 + '\'' +
                '}';
    }
}
