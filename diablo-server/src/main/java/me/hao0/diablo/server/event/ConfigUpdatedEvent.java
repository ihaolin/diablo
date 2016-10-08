package me.hao0.diablo.server.event;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class ConfigUpdatedEvent {

    /**
     * The app id
     */
    private Long appId;

    /**
     * The config name
     */
    private String name;

    public static ConfigUpdatedEvent newEvent(Long appId, String name){
        ConfigUpdatedEvent e = new ConfigUpdatedEvent();
        e.appId = appId;
        e.name = name;
        return e;
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

    @Override
    public String toString() {
        return "ConfigUpdatedEvent{" +
                "appId=" + appId +
                ", name='" + name + '\'' +
                '}';
    }
}
