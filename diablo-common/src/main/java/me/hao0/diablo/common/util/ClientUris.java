package me.hao0.diablo.common.util;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface ClientUris {

    /**
     * Client API prefix
     */
    String CLIENT_API = "/apis";

    /**
     * The uri for routing a server
     */
    String SERVER_ROUTE = "/servers_route";

    /**
     * The uri for client register
     */
    String REGISTER = "/register";

    /**
     * The uri for client un register
     */
    String UN_REGISTER = "/un_register";

    /**
     * The uri for pulling config
     */
    String CONFIG_PULLING = "/configs_pulling";

    /**
     * The uri for fetching a config
     */
    String CONFIG_FETCH = "/configs_fetch";

    /**
     * The uri for fetching configs
     */
    String CONFIG_FETCHES = "/configs_fetches";

    /**
     * The uri for fetching all configs
     */
    String CONFIG_FETCH_ALL = "/configs_fetch_all";
}
