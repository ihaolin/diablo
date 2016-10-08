package me.hao0.diablo.server.support;

import me.hao0.common.security.MD5;
import org.springframework.stereotype.Component;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Component
public class ClientIds {

    /**
     * Generate the client id
     * @param appName the app name
     * @param clientIp the client ip
     * @param clientPid client pid
     * @return the client id
     */
    public String generate(String appName, String clientIp, Integer clientPid){
        return MD5.generate(appName + clientIp + clientPid, false);
    }
}
