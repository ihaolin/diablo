package me.hao0.diablo.server.dao.mgr;

import me.hao0.diablo.server.dao.ConfigDao;
import me.hao0.diablo.server.model.Config;
import me.hao0.diablo.server.util.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Repository
public class ConfigManager {

    @Autowired
    private ConfigDao configDao;

    /**
     * Delete the config
     * @param config the config
     */
    public void delete(Config config){

        // unbind app's config id
        configDao.unBindApp(config);

        // delete the config index
        configDao.unIndex(config);

        // delete the config
        configDao.delete(config.getId());
    }

    /**
     * Delete the config of the app
     * @param appId the app id
     */
    public void deleteByAppId(Long appId) {
        List<Config> configs = configDao.listByAppId(appId, 0, 0);
        if (configs != null && !configs.isEmpty()){
            for (Config config : configs){
                delete(config);
            }
        }
    }

    /**
     * Save the config
     */
    public void save(Boolean isCreate, Config config) {
        // do save
        configDao.save(config);

        if(isCreate){

            // index config if necessary
            configDao.index(config);

            // bind app and config
            if (!configDao.bindApp(config)){
                Logs.error("failed to bind app of config({})", config);
            }
        }
    }
}
