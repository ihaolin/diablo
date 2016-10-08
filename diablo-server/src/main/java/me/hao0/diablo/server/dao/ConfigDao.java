package me.hao0.diablo.server.dao;

import me.hao0.diablo.server.model.Config;
import java.util.List;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface ConfigDao extends BaseDao<Config> {

    /**
     * Index the config item's name for findByName
     * @param config the config
     * @return return true if index successfully, or false
     */
    Boolean index(Config config);

    /**
     * Unindex the config item's name
     * @param config the config
     * @return return true if unindex successfully, or false
     */
    Boolean unIndex(Config config);

    /**
     * Unindex all the config item's names of the app
     * @param appId the app id
     * @return return true if unindex successfully, or false
     */
    Boolean unIndexByAppId(Long appId);

    /**
     * Find config item by name
     * @param appId the app id
     * @param name the config item's name
     * @return the object
     */
    Config findByName(Long appId, String name);

    /**
     * Find config items by names
     * @param appId the app id
     * @param names the config item's names
     * @return the object
     */
    List<Config> findByNames(Long appId, List<String> names);

    /**
     * Bind config to app
     * @param config the config
     * @return return true if bind successfully, or false
     */
    Boolean bindApp(Config config);

    /**
     * Unbind the config from app
     * @param config the config
     * @return return true if unbind successfully, or false
     */
    Boolean unBindApp(Config config);

    /**
     * Unbind the app's all config ids
     * @param appId the app id
     * @return return true if unbind successfully, or false
     */
    Boolean deleteBindOfApp(Long appId);

    /**
     * Count the config item of the app
     * @param appId app id
     * @return the total countByAppId config item of the app
     */
    Long countByAppId(Long appId);

    /**
     * List the config items
     * @param appId app id
     * @param offset start offset
     * @param limit limit
     * @return the config items
     */
    List<Config> listByAppId(Long appId, Integer offset, Integer limit);
}
