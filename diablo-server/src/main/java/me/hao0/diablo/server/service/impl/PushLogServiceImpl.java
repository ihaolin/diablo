package me.hao0.diablo.server.service.impl;

import com.google.common.base.Throwables;
import me.hao0.common.model.Page;
import me.hao0.diablo.server.dao.PushLogDao;
import me.hao0.diablo.server.dao.mgr.PushLogManager;
import me.hao0.diablo.server.model.PushLog;
import me.hao0.diablo.server.model.Response;
import me.hao0.diablo.server.service.PushLogService;
import me.hao0.diablo.server.support.Messages;
import me.hao0.diablo.server.support.RedisKeys;
import me.hao0.diablo.server.util.Logs;
import me.hao0.diablo.server.util.Paging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Service
public class PushLogServiceImpl implements PushLogService {

    @Autowired
    private PushLogDao configPushLogDao;

    @Autowired
    private PushLogManager pushLogManager;

    @Autowired
    private Messages messages;

    private static final ExecutorService LOG_RECORDER = Executors.newSingleThreadExecutor();

    @Override
    public Response<Boolean> add(final PushLog log) {
        try {
            LOG_RECORDER.submit(new Runnable() {
                @Override
                public void run() {
                    pushLogManager.add(log);
                }
            });
            return Response.ok(true);
        } catch (Exception e){
            e.printStackTrace();
            Logs.error("failed to add config push log({}), cause: {}",
                    log, Throwables.getStackTraceAsString(e));
            return Response.notOk(messages.get("config.log.push.add.failed"));
        }
    }

    @Override
    public Response<Page<PushLog>> pagingConfigPushLog(Long appId, Integer pageNo, Integer pageSize) {
        try {

            String appConfigPushLogsKey = RedisKeys.keyOfAppPushLogs(appId);

            Long totalCount = configPushLogDao.count(appConfigPushLogsKey);
            if (totalCount <= 0L){
                return Response.ok(Page.empty());
            }

            Paging paging = new Paging(pageNo, pageSize);
            List<PushLog> configs = configPushLogDao.list(appConfigPushLogsKey, paging.getOffset(), paging.getLimit());

            return Response.ok(new Page<>(totalCount, configs));
        } catch (Exception e){
            Logs.error("failed to paging config push log(appId={}, pageNo={}, pageSize={}), cause: {}",
                    appId, pageNo, pageSize, Throwables.getStackTraceAsString(e));
            return Response.notOk(messages.get("config.log.push.find.failed"));
        }
    }
}
