package me.hao0.diablo.server.service;

import me.hao0.diablo.server.model.ClientSession;
import me.hao0.diablo.server.model.Response;
import java.util.List;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface ClientService {

    /**
     * Un register the client
     * @param clientId the client id
     * @return return true if un register, or false
     */
    Response<Boolean> unRegisterClient(String clientId);

    /**
     * Register the client
     * @param clientId the client id
     * @param appName the app name
     * @param appKey the app key
     * @param pid the client pid
     * @return return true if register successfully, or false
     */
    Response<Boolean> registerClient(String clientId, String appName, String appKey, String ip, Integer pid);

    /**
     * Find the client info
     * @param clientId the client id
     * @return the client info
     */
    Response<ClientSession> getSession(String clientId);

    /**
     * Get the current client count
     * @return the current client count
     */
    Response<Integer> clientCount();

    /**
     * Get the current clients
     * @param appId the app id
     * @param limit the limit
     * @return the current clients of the app
     */
    Response<List<ClientSession>> clients(Long appId, Integer limit);
}
