package me.hao0.diablo.server.dao;

import me.hao0.diablo.server.model.PushLog;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface PushLogDao extends BaseDao<PushLog> {

    /**
     * Bind the log to the app
     * @param log the log
     * @return return true if bind successfully, or false
     */
    boolean bindApp(PushLog log);

    /**
     * Unbind the log from the app
     * @param log the log
     * @return return true if bind successfully, or false
     */
    boolean unBindApp(PushLog log);
}
