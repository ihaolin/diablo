package me.hao0.diablo.server.dao.mgr;

import me.hao0.diablo.server.dao.AppDao;
import me.hao0.diablo.server.dao.ConfigDao;
import me.hao0.diablo.server.model.App;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Repository
public class AppManager {

    @Autowired
    private ConfigDao configDao;

    @Autowired
    private AppDao appDao;

    @Autowired
    private ConfigManager configManager;

    /**
     * Delete the app
     * @param app the app
     */
    public void delete(App app){

        // delete the app index
        appDao.unIndex(app);

        // delete the app
        appDao.delete(app.getId());

        // delete all configs of the app
        configManager.deleteByAppId(app.getId());

        // delete the app's all config names index
        configDao.unIndexByAppId(app.getId());

        // unbind app's all config ids
        configDao.deleteBindOfApp(app.getId());
    }

    public void save(App app) {
        // save the app
        appDao.save(app);

        // index app if necessary
        appDao.index(app);
    }
}
