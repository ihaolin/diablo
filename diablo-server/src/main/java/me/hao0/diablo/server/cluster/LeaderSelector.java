package me.hao0.diablo.server.cluster;

import me.hao0.diablo.server.support.RedisKeys;
import me.hao0.diablo.server.util.Logs;
import me.hao0.diablo.server.util.SimpleRedisLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.PreDestroy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Component
public class LeaderSelector {

    @Value("${diablo.serverCheckInterval:10}")
    private Integer serverCheckInterval;

    @Autowired
    private StringRedisTemplate redis;

    @Autowired
    private ServerHost serverHost;

    @Autowired
    private ServerStatusChecker serverStatusChecker;

    private SimpleRedisLock leaderLock;

    private ScheduledExecutorService scheduler;

    private boolean isLeader = Boolean.FALSE;

    public boolean isLeader() {
        return isLeader;
    }

    /**
     * acquire to be leader
     */
    public void start(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // acquire master
                leaderLock = SimpleRedisLock.newBuilder(redis.opsForValue(),
                        RedisKeys.SERVER_LEADER, serverHost.get()).build();

                for (;;){

                    if (leaderLock.acquire()){
                        // start the scheduler
                        startScheduler();
                        isLeader = true;
                        break;
                    } else {
                        Logs.info("acquire leader lock timeout and will retry, don't care.");
                    }
                }
            }
        }).start();
    }

    private void startScheduler() {
        scheduler = Executors.newScheduledThreadPool(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("SERVER-STATUS-CHECKER");
                t.setDaemon(true);
                return t;
            }
        });

        scheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                serverStatusChecker.check();
            }
        }, 1L, serverCheckInterval, TimeUnit.SECONDS);
    }

    /**
     * Release the leader lock if necessary
     */
    @PreDestroy
    public void stop(){
        // release leader lock if necessary
        if (leaderLock != null){
            leaderLock.release();
        }

        // shutdown the scheduler if necessary
        if (scheduler != null){
            scheduler.shutdownNow();
        }

        isLeader = false;
    }
}
