package me.hao0.diablo.server.service.impl;

import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hao0.common.http.Http;
import me.hao0.common.util.Strings;
import me.hao0.diablo.common.util.CollectionUtil;
import me.hao0.diablo.common.util.Constants;
import me.hao0.diablo.common.util.JsonUtil;
import me.hao0.diablo.server.dao.ServerDao;
import me.hao0.diablo.server.dto.ClientDto;
import me.hao0.diablo.server.model.ServerStatus;
import me.hao0.diablo.server.service.ServerService;
import me.hao0.diablo.server.util.Logs;
import me.hao0.diablo.server.util.ServerUris;
import me.hao0.diablo.server.util.Types;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Service
public class ServerServiceImpl implements ServerService {

    private static final int RETRY_TIMES = 3;

    private static final ExecutorService executor =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setName("SERVER-SERVICE-WORKER");
                    t.setDaemon(true);
                    return t;
                }
            });

    @Autowired
    private ServerDao serverDao;

    @Override
    public void updatedConfig(Long appId, String name) {

        Set<String> servers = serverDao.servers(ServerStatus.ONLINE);

        final Map<String, Object> params = Maps.newHashMapWithExpectedSize(2);
        params.put("appId", appId);
        params.put("name", name);

        for (final String server : servers){
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    notifyServer(server, ServerUris.NOTIFY_CONFIG_UPDATED, params);
                }
            });
        }
    }

    @Override
    public void joinedServer(final String joinedServer) {
        Set<String> servers = serverDao.servers(ServerStatus.ONLINE);

        final Map<String, Object> params = Maps.newHashMapWithExpectedSize(1);
        params.put("server", joinedServer);

        for (final String server : servers){
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    notifyServer(server, ServerUris.NOTIFY_SERVER_JOINED, params);
                }
            });
        }
    }

    @Override
    public void leftServer(final String leftServer) {
        Set<String> servers = serverDao.servers(ServerStatus.ONLINE);

        // remove me
        servers.remove(leftServer);

        final Map<String, Object> params = Maps.newHashMapWithExpectedSize(1);
        params.put("server", leftServer);

        for (final String server : servers){
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    notifyServer(server, ServerUris.NOTIFY_SERVER_LEFT, params);
                }
            });
        }
    }

    @Override
    public void cleanCache(final String server) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                notifyServer(server, ServerUris.NOTIFY_CACHE_CLEAR, Collections.<String, Object>emptyMap());
            }
        });
    }

    @Override
    public void shutdownServer(final String server) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                notifyServer(server, ServerUris.NOTIFY_SHUTDOWN, Collections.<String, Object>emptyMap());
            }
        });
    }

    @Override
    public Map<String, Integer> clientCount(Set<String> servers) {

        final Map<String, Integer> clientCountMap = Maps.newHashMapWithExpectedSize(servers.size());
        final CountDownLatch latch = new CountDownLatch(servers.size());
        for (final String server : servers){
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        String clientCount = requestServer(server, ServerUris.SERVER_CLIENT_COUNT, null);
                        clientCountMap.put(server, Integer.parseInt(clientCount));
                    } catch (Exception e){
                        Logs.error("failed to request server({})'s client count, cause: {}",
                                server, Throwables.getStackTraceAsString(e));
                    }
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            // ignore
            Logs.error("failed to wait on latch, cause: {}", Throwables.getStackTraceAsString(e));
        }

        return clientCountMap;
    }

    @Override
    public List<ClientDto> clients(Long appId) {
        Set<String> onlineServers = serverDao.servers(ServerStatus.ONLINE);

        final Map<String, Object> params = Maps.newHashMap();
        params.put("appId", appId);

        final List<ClientDto> clientDtos = Lists.newArrayList();
        final CountDownLatch latch = new CountDownLatch(onlineServers.size());
        for (final String server : onlineServers){
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        String clientsJson = requestServer(server, ServerUris.SERVER_CLIENTS, params);
                        if (!Strings.isNullOrEmpty(clientsJson)){
                            List<ClientDto> serverClientDtos = JsonUtil.INSTANCE.fromJson(clientsJson, Types.LIST_CLIENT_DTO_TYPE);
                            clientDtos.addAll(serverClientDtos);
                        }

                    } catch (Exception e){
                        Logs.error("failed to request server({})'s client list, cause: {}",
                                server, Throwables.getStackTraceAsString(e));
                    }
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            // ignore
            Logs.error("failed to wait on latch, cause: {}", Throwables.getStackTraceAsString(e));
        }

        return clientDtos;
    }

    private Boolean notifyServer(String targetServer, String uri, Map<String, Object> params) {

        for (int i = 0; i< RETRY_TIMES; i++){
            try {
                String notifyResp = Http.get(Constants.HTTP_PREFIX + targetServer + uri).params(params).request();
                if(Objects.equal("true", notifyResp)){
                    Logs.info("notify server(target={}, uri={}, params={}) successfully", targetServer, uri, params);
                    return Boolean.TRUE;
                } else {
                    notifyFailed(targetServer, uri, params, i+1, "server not return true");
                }
            } catch (Exception e){
                notifyFailed(targetServer, uri, params, i+1, Throwables.getStackTraceAsString(e));
            }
        }

        return Boolean.FALSE;
    }

    private void notifyFailed(String targetServer, String uri, Map<String, Object> params, int retries, String cause) {
        try {
            Logs.warn("try {} times, failed to notify server(server={}, uri={}, params={}), cause: {}",
                    retries, targetServer, uri, params, cause);
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
        }
    }

    private String requestServer(String targetServer, String uri, Map<String, Object> params) {
        Http http = Http.get(Constants.HTTP_PREFIX + targetServer + uri);
        if (!CollectionUtil.isEmpty(params)){
            http.params(params);
        }
        return http.request();
    }
}
