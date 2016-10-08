package me.hao0.diablo.server.cluster;

import me.hao0.diablo.server.service.ClusterService;
import me.hao0.diablo.server.util.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PreDestroy;

/**
 * Author: haolin
 * Date:   8/24/16
 * Email:  haolin.h0@gmail.com
 */
@Component
public class ServerRegister {

    @Autowired
    private ServerHost serverHost;

    @Autowired
    private ClusterService clusterService;

    public void start() {
        String server = serverHost.get();
        clusterService.joinServer(server);
        Logs.info("registering the server: {}", server);
    }

    @PreDestroy
    public void stop() throws Exception {
        String server = serverHost.get();
        clusterService.leaveServer(server);
        Logs.info("un registering the server: {}", server);
    }
}
