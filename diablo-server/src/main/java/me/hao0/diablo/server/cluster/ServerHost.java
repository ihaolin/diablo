package me.hao0.diablo.server.cluster;

import me.hao0.common.util.Networks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Component
public class ServerHost {

    private final String server;

    @Autowired
    public ServerHost(
            @Value("${server.port:7259}") Integer serverPort,
            @Value("${server.address:-1}" ) String serverHost){
        server = (serverHost.equals("-1") ? Networks.getSiteIp() : serverHost)
                    + ":" + serverPort;
    }

    public String get(){
        return server;
    }
}
