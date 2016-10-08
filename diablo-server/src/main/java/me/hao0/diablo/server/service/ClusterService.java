package me.hao0.diablo.server.service;

import me.hao0.diablo.common.model.ServerRouteResp;
import me.hao0.diablo.server.dto.ClientDto;
import me.hao0.diablo.server.model.Response;
import me.hao0.diablo.server.model.ServerInfo;
import java.util.List;
import java.util.Set;

/**
 * The Server Cluster Service
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface ClusterService {

    /**
     * Route a server to client
     * @param appName the app name
     * @param clientIp the client ip
     * @param clientPid the client pid
     * @return the server route info
     */
    Response<ServerRouteResp> routeServer(String appName, String clientIp, Integer clientPid);

    /**
     * A server is joined
     * @param server the server host, [ip]:[port]
     * @return return true if the server is online successfully, or false
     */
    Response<Boolean> joinServer(String server);

    /**
     * A server is leaved
     * @param server the server host, [ip]:[port]
     * @return return true if the server is offline successfully, or false
     */
    Response<Boolean> leaveServer(String server);

    /**
     * Return all online servers of the cluster
     * @return all online servers of the cluster
     */
    Response<Set<String>> listOnlineServers();

    /**
     * Return all servers of the cluster
     * @return all servers of the cluster
     */
    Response<List<ServerInfo>> listServers();

    /**
     * Return the clients of the app
     * @param appId the app id
     * @return the client lsit
     */
    Response<List<ClientDto>> listClients(Long appId);
}
