package me.hao0.diablo.server.util;

/**
 * Some uris for server notifying
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface ServerUris {

    String SERVERS = "/servers";

    String SERVER_JOINED = "/server_joined";

    String SERVER_LEFT = "/server_left";

    String CONFIG_UPDATED = "/config_updated";

    String CACHE_CLEAN = "/cache_clean";

    String SHUTDOWN = "/shutdown";

    String STATUS = "/status";

    String CLIENT_COUNT = "/client_count";

    String CLIENTS = "/clients";

    /**
     * Notify one server that a server joined
     */
    String NOTIFY_SERVER_JOINED = SERVERS + SERVER_JOINED;

    /**
     * Notify one server that a server left
     */
    String NOTIFY_SERVER_LEFT = SERVERS + SERVER_LEFT;

    /**
     * Notify one server a config is updated
     */
    String NOTIFY_CONFIG_UPDATED = SERVERS + CONFIG_UPDATED;

    /**
     * Notify one server clean cache
     */
    String NOTIFY_CACHE_CLEAR = SERVERS + CACHE_CLEAN;

    /**
     * Notify one server shutdown
     */
    String NOTIFY_SHUTDOWN = SERVERS + SHUTDOWN;

    /**
     * Check server status
     */
    String SERVER_STATUS = SERVERS + STATUS;

    /**
     * The client count of the server
     */
    String SERVER_CLIENT_COUNT = SERVERS + CLIENT_COUNT;

    /**
     * The client list of the server
     */
    String SERVER_CLIENTS = SERVERS + CLIENTS;
}
