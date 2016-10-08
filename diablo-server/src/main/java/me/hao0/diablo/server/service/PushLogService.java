package me.hao0.diablo.server.service;

import me.hao0.common.model.Page;
import me.hao0.diablo.server.model.PushLog;
import me.hao0.diablo.server.model.Response;

/**
 * Config Service
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface PushLogService {

    /**
     * Add a config push log
     * @param log the log
     * @return the log id
     */
    Response<Boolean> add(PushLog log);

    /**
     * Paging config push logs
     * @param appId app id
     * @param pageNo page no
     * @param pageSize page size
     * @return paging config items
     */
    Response<Page<PushLog>> pagingConfigPushLog(Long appId, Integer pageNo, Integer pageSize);

}
