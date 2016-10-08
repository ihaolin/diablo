package me.hao0.diablo.server.support;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import me.hao0.diablo.server.exception.KeyException;
import java.util.concurrent.ExecutionException;

/**
 * Author: haolin
 * Date:   8/24/16
 * Email:  haolin.h0@gmail.com
 */
public final class RedisKeys {

    public static final String DIABLO_NAMESPACE_PROP = "diablo.namespace";

    public static final String DIABLO_NAMESPACE = System.getProperty(DIABLO_NAMESPACE_PROP, "db");

    public static final String KEY_DELIMITER = ":";

    /**
     * The cluster's online servers key
     */
    public static final String CLUSTER_SERVERS_ONLINE = format("servers", 1);

    /**
     * The cluster's offline servers key
     */
    public static final String CLUSTER_SERVERS_OFFLINE = format("servers", 0);

    /**
     * Class id listByAppId key
     */
    public static final String IDS = "ids";

    /**
     * Class id generator prefix
     */
    public static final String ID_GENERATOR = "idg";

    /**
     * App name mapping
     */
    public static final String APP_INDEX_NAMES = format("apps", "names");

    /**
     * Server Leader
     */
    public static final String SERVER_LEADER = format("leader");

    /**
     * Push logs
     */
    public static final String PUSH_LOGS = format("logs", "push");

    /**
     * Class id generator key cache
     */
    private static final LoadingCache<Class, String> IDS_KEY_CACHE =
            CacheBuilder.newBuilder().build(new CacheLoader<Class, String>() {
                @Override
                public String load(Class clazz) throws Exception {
                    return format(IDS, clazz.getSimpleName().toLowerCase() + "s");
                }
            });

    /**
     * Class id listByAppId key cache
     */
    private static final LoadingCache<Class, String> ID_GENERATOR_KEY_CACHE =
            CacheBuilder.newBuilder().build(new CacheLoader<Class, String>() {
                @Override
                public String load(Class clazz) throws Exception {
                    return format(ID_GENERATOR, clazz.getSimpleName().toLowerCase() + "s");
                }
            });

    /**
     * The key of id generator
     * @param clazz the class
     * @return the key of id generator
     */
    public static String keyOfIdGenerator(Class<?> clazz) {
        try {
            return ID_GENERATOR_KEY_CACHE.get(clazz);
        } catch (ExecutionException e) {
            throw new KeyException(e);
        }
    }

    /**
     * The key of id generator
     * @param clazz the class
     * @return the key of id generator
     */
    public static String keyOfIds(Class<?> clazz) {
        try {
            return IDS_KEY_CACHE.get(clazz);
        } catch (ExecutionException e) {
            throw new KeyException(e);
        }
    }

    /**
     * Format impl key
     * @param parts string parts
     * @return db:part1:part2
     */
    public static String format(Object... parts){
        StringBuilder key = new StringBuilder(DIABLO_NAMESPACE);
        for (Object part : parts){
            key.append(KEY_DELIMITER).append(part);
        }
        return key.toString();
    }

    public static String keyOfAppConfigs(Long appId) {
        return format("apps", appId, "configs");
    }

    public static String keyOfIndexName(Long appId) {
        return format("apps", appId, "cfg_names");
    }

    public static String keyOfAppPushLogs(Long appId) {
        return format("apps", appId, "pushlogs");
    }
}
