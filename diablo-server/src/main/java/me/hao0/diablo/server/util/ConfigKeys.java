package me.hao0.diablo.server.util;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public final class ConfigKeys {

    public static final String CONFIG_KEY_DELIMITER = System.getProperty("diablo.cache.config.key", ":");

    private ConfigKeys(){}

    public static String buildKey(Long appId, String configName){
        return appId + CONFIG_KEY_DELIMITER + configName;
    }

    public static String[] splitKey(String cacheKey) {
        return cacheKey.split(CONFIG_KEY_DELIMITER);
    }
}
