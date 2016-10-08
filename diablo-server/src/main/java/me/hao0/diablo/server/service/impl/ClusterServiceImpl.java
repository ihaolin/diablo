package me.hao0.diablo.server.service.impl;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import me.hao0.diablo.common.model.ServerRouteResp;
import me.hao0.diablo.common.util.CollectionUtil;
import me.hao0.diablo.server.cluster.ServerRouter;
import me.hao0.diablo.server.dao.ServerDao;
import me.hao0.diablo.server.dto.ClientDto;
import me.hao0.diablo.server.model.*;
import me.hao0.diablo.server.service.AppService;
import me.hao0.diablo.server.service.ClusterService;
import me.hao0.diablo.server.service.ServerService;
import me.hao0.diablo.server.support.ClientIds;
import me.hao0.diablo.server.support.Messages;
import me.hao0.diablo.server.util.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Service
public class ClusterServiceImpl implements ClusterService {

    @Autowired
    private ServerDao serverDao;

    @Autowired
    private AppService appService;

    @Autowired
    private ServerService serverService;

    @Autowired
    private ClientIds clientIds;

    @Autowired
    private ServerRouter serverRouter;

    @Autowired
    private Messages messages;

    @Override
    public Response<ServerRouteResp> routeServer(String appName, String clientIp, Integer clientPid) {
        try {

            Response<App> appResp = appService.findByName(appName);
            if (!appResp.isSuccess()){
                Logs.error("failed to find app(name={}), cause: {}", appName, appResp.getErr());
                return Response.notOk(appResp.getErr());
            }

            App app = appResp.getData();
            if (app == null){
                Logs.warn("the app(name={}) isn't exist.", appName);
                return Response.notOk(messages.get("app.not.exist"));
            }

            // generate client id
            String clientId = clientIds.generate(app.getAppName(), clientIp, clientPid);

            // route a server
            String server = serverRouter.route(clientId);

            ServerRouteResp routeResp = new ServerRouteResp();
            routeResp.setClientId(clientId);
            routeResp.setServer(server);

            return Response.ok(routeResp);
        } catch (Exception e){
            Logs.error("failed to route server(appName={}, clientIp={}, clientPid={}), cause: {}",
                    appName, clientIp, clientPid, Throwables.getStackTraceAsString(e));
            return Response.notOk(messages.get("server.route.failed"));
        }
    }

    @Override
    public Response<Boolean> joinServer(final String server) {
        try {
            if (serverDao.serverOnline(server)){

                // notify a server joined
                serverService.joinedServer(server);

                return Response.ok(Boolean.TRUE);
            } else {
                Logs.warn("failed to join server({}), maybe it is existed.", server);
                return Response.ok(Boolean.FALSE);
            }
        } catch (Exception e){
            Logs.error("failed to join server({}), cause: {}", server, Throwables.getStackTraceAsString(e));
            return Response.notOk(messages.get("server.join.failed"));
        }
    }

    @Override
    public Response<Boolean> leaveServer(final String server) {
        try {
            if (serverDao.serverOffline(server)){
                // notify a server left
                serverService.leftServer(server);
                return Response.ok(Boolean.TRUE);
            } else {
                Logs.warn("failed to join server({}), maybe it is existed.", server);
                return Response.ok(Boolean.FALSE);
            }
        } catch (Exception e){
            Logs.error("failed to leave server({}), cause: {}", server, Throwables.getStackTraceAsString(e));
            return Response.notOk(messages.get("server.leave.failed"));
        }
    }

    @Override
    public Response<Set<String>> listOnlineServers() {
        try {
            return Response.ok(serverDao.servers(ServerStatus.ONLINE));
        } catch (Exception e){
            Logs.error("failed to get online servers, cause: {}", Throwables.getStackTraceAsString(e));
            return Response.notOk(messages.get("server.list.failed"));
        }
    }

    @Override
    public Response<List<ServerInfo>> listServers() {
        try {

            Set<ServerInfo> allServers = Sets.newHashSet();

            // the leader
            String leader = serverDao.getLeader();

            // online servers
            Set<String> onlines = renderServerInfo(allServers, leader, ServerStatus.ONLINE);

            // request online servers' client count
            Map<String, Integer> serversClientCountMap = serverService.clientCount(onlines);
            renderServerClientCount(allServers, serversClientCountMap);

            // offline servers
            renderServerInfo(allServers, leader, ServerStatus.OFFLINE);

            List<ServerInfo> serverInfos = Lists.newArrayList(allServers);
            if (!CollectionUtil.isEmpty(allServers)){
                Collections.sort(serverInfos, new Comparator<ServerInfo>() {
                    @Override
                    public int compare(ServerInfo s1, ServerInfo s2) {
                        int res = s2.getStatus() - s1.getStatus();
                        if (res == 0){
                            res = s2.getServer().compareTo(s1.getServer());
                        }
                        return res;
                    }
                });
            }

            return Response.ok(serverInfos);
        } catch (Exception e){
            Logs.error("failed to list all servers, cause: {}", Throwables.getStackTraceAsString(e));
            return Response.notOk(messages.get("server.list.failed"));
        }
    }

    private void renderServerClientCount(Set<ServerInfo> onlineServers, Map<String, Integer> serversClientCountMap) {
        if (!CollectionUtil.isEmpty(serversClientCountMap)){
            for (ServerInfo serverInfo : onlineServers){
                serverInfo.setClientCount(serversClientCountMap.get(serverInfo.getServer()));
            }
        }
    }

    private Set<String> renderServerInfo(Set<ServerInfo> allServers, String leader, ServerStatus status) {

        Set<String> servers = serverDao.servers(status);
        if (!CollectionUtil.isEmpty(servers)){
            ServerInfo serverInfo;
            for (String server : servers){
                serverInfo = new ServerInfo();
                serverInfo.setServer(server);
                serverInfo.setStatus(status.status());
                serverInfo.setStatusDesc(messages.get(status.code()));
                serverInfo.setLeader(server.equals(leader));
                allServers.add(serverInfo);
            }
        }

        return servers;
    }

    @Override
    public Response<List<ClientDto>> listClients(Long appId) {
        try {
            return Response.ok(serverService.clients(appId));
        } catch (Exception e){
            Logs.error("failed to get online clients, cause: {}", Throwables.getStackTraceAsString(e));
            return Response.notOk(messages.get("client.list.failed"));
        }
    }
}
