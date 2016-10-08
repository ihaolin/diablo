package me.hao0.diablo.server.dao;

import me.hao0.diablo.server.model.ServerStatus;
import java.util.Set;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface ServerDao {

    /**
     * Online the server
     * @param server the server
     * @return return true if online successfully, or false
     */
    boolean serverOnline(String server);

    /**
     * Offline the server
     * @param server the server
     * @return return true if offline successfully, or false
     */
    boolean serverOffline(String server);

    /**
     * Delete the server from the servers set
     * @param serversKey the servers key
     * @param server the server
     * @return return true if delete successfully, or false
     */
    boolean deleteServer(String serversKey, String server);

    /**
     * Get servers of status
     * @param status 1: ONLINE, 0: OFFLINE
     * @return the onlineServers of the status
     */
    Set<String> servers(ServerStatus status);

    /**
     * Get current leader
     * @return the leader
     */
    String getLeader();
}
