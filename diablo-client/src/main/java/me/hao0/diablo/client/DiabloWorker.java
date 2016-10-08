package me.hao0.diablo.client;

import com.google.common.base.Throwables;
import me.hao0.diablo.client.exception.Server503Exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.*;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
class DiabloWorker {

    private static final Logger log = LoggerFactory.getLogger(DiabloWorker.class);

    private ScheduledExecutorService executor;

    private AbstractDiabloClient client;

    DiabloWorker(AbstractDiabloClient client){
        this.client = client;

    }

    public void start(){

        executor = Executors.newScheduledThreadPool(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("DIABLO-CONFIG-CHECK-WORKER");
                t.setDaemon(true);
                return t;
            }
        });


        executor.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                try {
                    // long pulling updated config
                    try {
                        client.pullingConfigs();
                    } catch (Server503Exception e){
                        log.warn("all servers are unavailable, wait longer until servers are started");
                        Thread.sleep(5000);
                    }
                } catch (Exception e) {
                    log.error("failed to pulling updated config, cause: {}",
                            Throwables.getStackTraceAsString(e));
                }
            }
        }, 1L, 1L, TimeUnit.MILLISECONDS);
    }
}
