package me.hao0.diablo.server.service;

import me.hao0.diablo.server.dto.ClientDto;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface ServerService {

    /**
     * Notify all online servers that the config is updated
     * @param name config name
     */
    void updatedConfig(Long appId, String name);

    /**
     * Notify all online servers a server is joined
     * @param server the server joined
     */
    void joinedServer(String server);

    /**
     * Notify all online servers, except this server, a server left
     * @param server the server left
     */
    void leftServer(String server);

    /**
     * Notify the server to clear its' local cache
     * @param server the server
     */
    void cleanCache(String server);

    /**
     * Nofity the server to shutdown
     * @param server the server
     */
    void shutdownServer(String server);

    /**
     * Get the servers' current client count
     * @param servers the online servers
     * @return the servers' current client count
     */
    Map<String, Integer> clientCount(Set<String> servers);

    /**
     * Get the servers' client list
     * @param appId the app id
     * @return the client list
     */
    List<ClientDto> clients(Long appId);
}
