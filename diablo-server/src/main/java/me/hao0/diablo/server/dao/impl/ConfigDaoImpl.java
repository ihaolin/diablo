package me.hao0.diablo.server.dao.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import me.hao0.diablo.server.dao.ConfigDao;
import me.hao0.diablo.server.model.Config;
import me.hao0.diablo.server.support.RedisKeys;
import org.springframework.stereotype.Repository;
import java.util.Collections;
import java.util.List;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Repository
public class ConfigDaoImpl extends RedisDao<Config> implements ConfigDao {

    @Override
    public Boolean index(Config config) {
        String indexNameKey = RedisKeys.keyOfIndexName(config.getAppId());
        redis.opsForHash().put(indexNameKey, config.getName(), config.getId());
        return Boolean.TRUE;
    }

    @Override
    public Boolean unIndex(Config config) {
        String indexNameKey = RedisKeys.keyOfIndexName(config.getAppId());
        redis.opsForHash().delete(indexNameKey, config.getName());
        return Boolean.TRUE;
    }

    @Override
    public Boolean unIndexByAppId(Long appId) {
        String indexNameKey = RedisKeys.keyOfIndexName(appId);
        redis.delete(indexNameKey);
        return Boolean.TRUE;
    }

    @Override
    public Config findByName(Long appId, String name) {
        String indexNameKey = RedisKeys.keyOfIndexName(appId);
        Object idObj = redis.opsForHash().get(indexNameKey, name);
        return idObj == null ? null : findById(Long.valueOf(idObj.toString()));
    }

    @Override
    public List<Config> findByNames(Long appId, List<String> names) {
        String indexNameKey = RedisKeys.keyOfIndexName(appId);

        List<Object> namesObj = Lists.transform(names, new Function<String, Object>() {
            @Override
            public Object apply(String name) {
                return name;
            }
        });

        List<Object> idsObj = redis.opsForHash().multiGet(indexNameKey, namesObj);
        if (idsObj == null){
            return Collections.emptyList();
        }
        idsObj.removeAll(Collections.singleton(null));

        List<String> idsStr = Lists.newArrayListWithExpectedSize(idsObj.size());
        for (Object idObj : idsObj){
            idsStr.add(String.valueOf(idObj));
        }

        return findByIds(idsStr);
    }

    @Override
    public Boolean bindApp(Config config) {
        String appConfigsKey = RedisKeys.keyOfAppConfigs(config.getAppId());
        return redis.opsForList()
                .leftPush(appConfigsKey, config.getId().toString()) > 0L;
    }

    @Override
    public Boolean unBindApp(Config config) {
        String appConfigsKey = RedisKeys.keyOfAppConfigs(config.getAppId());
        redis.opsForList().remove(appConfigsKey, 1, String.valueOf(config.getId()));
        return Boolean.TRUE;
    }

    @Override
    public Long countByAppId(Long appId) {
        String appConfigsKey = RedisKeys.keyOfAppConfigs(appId);
        return redis.opsForList().size(appConfigsKey);
    }

    @Override
    public List<Config> listByAppId(Long appId, Integer offset, Integer limit) {
        return list(RedisKeys.keyOfAppConfigs(appId), offset, limit);
    }

    @Override
    public Boolean deleteBindOfApp(Long appId) {
        String appConfigsKey = RedisKeys.keyOfAppConfigs(appId);
        redis.delete(appConfigsKey);
        return Boolean.TRUE;
    }
}
