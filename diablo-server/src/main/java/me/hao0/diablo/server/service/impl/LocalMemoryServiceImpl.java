package me.hao0.diablo.server.service.impl;

import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;
import me.hao0.diablo.server.dao.ConfigDao;
import me.hao0.diablo.server.model.Config;
import me.hao0.diablo.server.model.Response;
import me.hao0.diablo.server.service.LocalService;
import me.hao0.diablo.server.support.Messages;
import me.hao0.diablo.server.util.ConfigKeys;
import me.hao0.diablo.server.util.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Service
public class LocalMemoryServiceImpl implements LocalService {

    @Autowired
    private ConfigDao configDao;

    @Autowired
    private Messages messages;

    /**
     * Config Cache
     */
    private final LoadingCache<String, Config> configCache =
            CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build(new CacheLoader<String, Config>() {
                @Override
                public Config load(String cacheKey) throws Exception {
                    String[] cacheKeyParts = ConfigKeys.splitKey(cacheKey);
                    Long appId = Long.valueOf(cacheKeyParts[0]);
                    return configDao.findByName(appId, cacheKeyParts[1]);
                }
            });

    @Override
    public Response<Boolean> sync(Long appId, String configName) {
        try {
            String cacheKey = ConfigKeys.buildKey(appId, configName);
            configCache.refresh(cacheKey);
            return Response.ok(Boolean.TRUE);
        } catch (Exception e){
            Logs.error("failed to sync cache(appId={}, configName={}), cause: {}",
                    appId, configName, Throwables.getStackTraceAsString(e));
            return Response.notOk(messages.get("config.sync.failed"));
        }
    }

    @Override
    public Response<Boolean> syncAll() {
        try {
            // TODO
            return Response.ok(Boolean.TRUE);
        } catch (Exception e){
            Logs.error("failed to sync all cache, cause: {}", Throwables.getStackTraceAsString(e));
            return Response.notOk(messages.get("config.sync.all.failed"));
        }
    }

    @Override
    public Response<Set<String>> checkUpdatedConfigs(Long appId, Map<String, String> configItems) {
        try {
            Set<String> updatedConfigs = Sets.newHashSet();
            String configKey;
            Config latest;

            for (Map.Entry<String, String> configItem : configItems.entrySet()){

                configKey = ConfigKeys.buildKey(appId, configItem.getKey());
                latest = configCache.get(configKey);
                if (latest != null){
                    if (!Objects.equal(latest.getMd5(), configItem.getValue())){
                        // config has updated
                        updatedConfigs.add(configItem.getKey());
                    }
                }
            }
            return Response.ok(updatedConfigs);
        } catch (Exception e){
            Logs.error("failed check updated configs(appId={}, configItems={}), cause: {}",
                    appId, configItems, Throwables.getStackTraceAsString(e));
            return Response.notOk(messages.get("config.check.update.failed"));
        }
    }

    @Override
    public Response<Boolean> cleanCache() {
        try {
            configCache.cleanUp();
            return Response.ok(Boolean.TRUE);
        } catch (Exception e){
            Logs.error("failed to clear the cache, cause: {}",
                    Throwables.getStackTraceAsString(e));
            return Response.notOk(messages.get("config.cache.clear.failed"));
        }
    }
}
