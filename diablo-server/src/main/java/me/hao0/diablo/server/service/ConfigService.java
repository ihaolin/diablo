package me.hao0.diablo.server.service;

import me.hao0.common.model.Page;
import me.hao0.diablo.server.model.Config;
import me.hao0.diablo.server.model.Response;
import java.util.List;

/**
 * Config Service
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface ConfigService {

    /**
     * Add a config item
     * @param appId the appId
     * @param configName the config name
     * @param configValue the config value
     * @return the config id
     */
    Response<Long> save(Long appId, String configName, String configValue);

    /**
     * Delete the config
     * @param appId the app id
     * @param configName the config name
     * @return return true if delete successfully, or false
     */
    Response<Boolean> delete(Long appId, String configName);

    /**
     * find the config item by id
     * @param id config item id
     * @return the config item
     */
    Response<Config> findById(Long id);

    /**
     * find the config item by name(Full Match)
     * @param appId the app id
     * @param name the config name
     * @return the config
     */
    Response<Config> findByName(Long appId, String name);

    /**
     * find the configs item by names(Full Match)
     * @param appId the app id
     * @param names the config names
     * @return the config list
     */
    Response<List<Config>> findByNames(Long appId, List<String> names);

    /**
     * Paging config items
     * @param appId app id
     * @param configName the config name(full match)
     * @param pageNo page no
     * @param pageSize page size
     * @return paging config items
     */
    Response<Page<Config>> pagingConfig(Long appId, String configName, Integer pageNo, Integer pageSize);
}
