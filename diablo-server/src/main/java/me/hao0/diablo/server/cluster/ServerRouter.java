package me.hao0.diablo.server.cluster;

import me.hao0.diablo.server.model.Response;
import me.hao0.diablo.server.service.ClusterService;
import me.hao0.diablo.server.util.Ketama;
import me.hao0.diablo.server.util.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Set;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Component
public class ServerRouter {

    @Autowired
    private ClusterService clusterService;

    private final Ketama<String> ketama = new Ketama<>();

    public void start(){
        Response<Set<String>> serversResp = clusterService.listOnlineServers();
        if (!serversResp.isSuccess()){
            Logs.error("failed to load all online servers, cause: {}", serversResp.getErr());
            return;
        }

        for (String server : serversResp.getData()){
            Logs.info("server router add ketama server({})", server);
            ketama.add(server);
        }
    }

    public void join(String server){
        ketama.add(server);
    }

    public void leave(String server){
        ketama.remove(server);
    }

    public String route(String clientId){
        return ketama.get(clientId);
    }
}
