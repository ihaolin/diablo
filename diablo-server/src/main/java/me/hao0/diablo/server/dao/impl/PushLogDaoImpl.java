package me.hao0.diablo.server.dao.impl;

import me.hao0.diablo.server.dao.PushLogDao;
import me.hao0.diablo.server.model.PushLog;
import me.hao0.diablo.server.support.RedisKeys;
import org.springframework.stereotype.Repository;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Repository
public class PushLogDaoImpl extends RedisDao<PushLog> implements PushLogDao {

    @Override
    public boolean bindApp(PushLog log) {
        String appConfigsKey = RedisKeys.keyOfAppPushLogs(log.getAppId());
        return redis.opsForList()
                .leftPush(appConfigsKey, log.getId().toString()) > 0L;
    }

    @Override
    public boolean unBindApp(PushLog log) {
        String appConfigsKey = RedisKeys.keyOfAppPushLogs(log.getAppId());
        redis.opsForList().remove(appConfigsKey, 1, String.valueOf(log.getId()));
        return Boolean.TRUE;
    }
}
