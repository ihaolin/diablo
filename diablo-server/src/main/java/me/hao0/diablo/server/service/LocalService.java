package me.hao0.diablo.server.service;

import me.hao0.diablo.server.model.Response;
import java.util.Map;
import java.util.Set;

/**
 * Config Cache Service
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface LocalService {

    /**
     * Sync a config item to local
     * @param appId app id
     * @param configName config name
     * @return return true if sync successfully, or false
     */
    Response<Boolean> sync(Long appId, String configName);

    /**
     * Sync all config items to local
     * @return return true if sync successfully, or false
     */
    Response<Boolean> syncAll();

    /**
     * Check the updated config names by md5
     * @param appId app id
     * @param configItems pulling config items
     * @return updated config names
     */
    Response<Set<String>> checkUpdatedConfigs(Long appId, Map<String, String> configItems);

    /**
     * Clear server's cache
     */
    Response<Boolean> cleanCache();
}
