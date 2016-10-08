package me.hao0.diablo.server;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.junit.Test;
import java.util.concurrent.TimeUnit;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class CacheTests {

    @Test
    public void testExpiredAfterAccess() throws InterruptedException {
        Cache<String, Integer> cache =
                CacheBuilder.newBuilder()
                        .expireAfterAccess(10, TimeUnit.SECONDS).build();
        cache.put("test", 1);

        Thread.sleep(5000);
        System.out.println(cache.size() + ": " + cache.getIfPresent("test"));

        Thread.sleep(10000);
        System.out.println(cache.size() + ": " + cache.getIfPresent("test"));
    }
}
