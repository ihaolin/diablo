package me.hao0.diablo.client.listener;

/**
 * The listener for one config
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface ConfigListener<T> {

    /**
     * The config name
     * @return the config name
     */
    String name();

    /**
     * Invoke after the local configs are updated
     * @param value the config updated value
     */
    void onUpdate(T value);

}
