package me.hao0.diablo.server.cluster;

import com.google.common.base.Objects;
import com.google.common.base.Stopwatch;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import me.hao0.common.http.Http;
import me.hao0.diablo.common.util.Constants;
import me.hao0.diablo.server.model.Response;
import me.hao0.diablo.server.service.ClusterService;
import me.hao0.diablo.server.util.Logs;
import me.hao0.diablo.server.util.ServerUris;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Server Status Check Component
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Component
public class ServerStatusChecker {

    @Autowired
    private ServerHost serverHost;

    @Autowired
    private ClusterService clusterService;

    private static final ExecutorService executor =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);

    /**
     * Check all online servers are health & remove dead online servers
     */
    public void check() {

        // load all online servers
        Response<Set<String>> serversResp = clusterService.listOnlineServers();
        if (!serversResp.isSuccess()){
            Logs.error("failed to get all online servers, cause: {}", serversResp.getErr());
            return;
        }

        try {
            Set<String> allServers = serversResp.getData();
            final Set<String> failedServers = Sets.newHashSet();

            // check server status
            doCheck(allServers, failedServers);

            // remove failed onlineServers
            removeFailedServers(failedServers);

        } catch (InterruptedException e) {
            Logs.error("failed to check online servers: {}", Throwables.getStackTraceAsString(e));
        }
    }

    private void doCheck(Set<String> allServers, final Set<String> failedServers) throws InterruptedException {

        Logs.info("server status check start.");

        Stopwatch watch = Stopwatch.createStarted();

        // don't need checking myself
        allServers.remove(serverHost.get());

        final CountDownLatch latch = new CountDownLatch(allServers.size());
        for (final String server : allServers){
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    String healthUrl = Constants.HTTP_PREFIX + server + ServerUris.SERVER_STATUS;
                    boolean isDown = false;
                    try {
                        String healthResp = Http.get(healthUrl).connTimeout(2).readTimeout(2).request();
                        if (!Objects.equal(Constants.SERVER_OK, healthResp)){
                            isDown = true;
                            Logs.info("checked server({}) is down, health resp is empty", server);
                        }
                    } catch (Exception e){
                        Logs.error("failed to check server({})'s health, cause: {}", server, Throwables.getStackTraceAsString(e));
                        isDown = true;
                    } finally {
                        if (isDown){
                            failedServers.add(server);
                        }
                        latch.countDown();
                    }
                }
            });
        }
        latch.await();

        Logs.info("server status check end, cost: {} ms.", watch.stop().elapsed(TimeUnit.MILLISECONDS));
    }

    /**
     * Remove all failed online servers
     * @param failedServers failed online servers
     */
    private void removeFailedServers(Set<String> failedServers) {
        if (!failedServers.isEmpty()){
            for (String server : failedServers){
                Response<Boolean> leaveResp = clusterService.leaveServer(server);
                if (leaveResp.isSuccess() && leaveResp.getData()){
                    Logs.info("remove failed server({}) successfully", server);
                }
            }
        }
    }
}
