package me.hao0.diablo.server.dao.mgr;

import me.hao0.diablo.server.dao.PushLogDao;
import me.hao0.diablo.server.model.PushLog;
import me.hao0.diablo.server.util.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Repository
public class PushLogManager {

    @Autowired
    private PushLogDao configPushLogDao;

    /**
     * Delete the config
     * @param log the log
     */
    public void delete(PushLog log){

        // unbind app's config id
        configPushLogDao.unBindApp(log);

        // delete the config
        configPushLogDao.delete(log.getId());
    }

    /**
     * Save the config
     */
    public void add(PushLog log) {
        // do save
        configPushLogDao.save(log);
        if (!configPushLogDao.bindApp(log)){
            Logs.error("failed to bind app of log({})", log);
        }
    }
}
