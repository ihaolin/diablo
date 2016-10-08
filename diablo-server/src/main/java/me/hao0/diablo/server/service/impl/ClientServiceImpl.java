package me.hao0.diablo.server.service.impl;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import me.hao0.common.date.Dates;
import me.hao0.common.util.Strings;
import me.hao0.diablo.common.util.CollectionUtil;
import me.hao0.diablo.server.cluster.ServerHost;
import me.hao0.diablo.server.dto.ClientDto;
import me.hao0.diablo.server.model.App;
import me.hao0.diablo.server.model.ClientSession;
import me.hao0.diablo.server.model.Response;
import me.hao0.diablo.server.service.AppService;
import me.hao0.diablo.server.service.ClientService;
import me.hao0.diablo.server.support.Messages;
import me.hao0.diablo.server.util.IP4s;
import me.hao0.diablo.server.util.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private AppService appService;

    @Autowired
    private ServerHost serverHost;

    @Autowired
    private Messages messages;

    /**
     * If client unconnected, remove it after the minutes
     */
    private static final int CLIENT_SESSION_EXPIRED_SECONDS = 60;

    /**
     * The client sessions
     */
    private final Cache<String, ClientSession> CLIENTS =
            CacheBuilder.newBuilder().expireAfterAccess(CLIENT_SESSION_EXPIRED_SECONDS, TimeUnit.SECONDS).build();

    @Override
    public Response<Boolean> unRegisterClient(String clientId) {
        try {
            CLIENTS.invalidate(clientId);
            return Response.ok(true);
        } catch (Exception e){
            Logs.error("failed to un register client(clientId={}), cause: {}",
                    clientId, Throwables.getStackTraceAsString(e));
            return Response.notOk(messages.get("client.unregister.failed"));
        }
    }

    @Override
    public Response<Boolean> registerClient(String clientId, String appName, String appKey, String ip, Integer pid) {
        try {

            App app = checkApp(appName, appKey);

            // register the client info locally
            ClientSession clientSession = new ClientSession(clientId, app.getId(), app.getAppKey(), IP4s.ipToInt(ip), pid);
            CLIENTS.put(clientSession.getId(), clientSession);

            return Response.ok(true);
        } catch (Exception e){
            Logs.error("failed to register client(clientId={}, appName={}, ip={}, pid={}), cause: {}",
                    clientId, appName, ip, pid, Throwables.getStackTraceAsString(e));
            return Response.notOk(messages.get("client.register.failed"));
        }
    }

    private App checkApp(String appName, String appKey){
        Response<App> appResp = appService.findByName(appName);
        if (!appResp.isSuccess()){
            Logs.error("failed to find app(name={}), cause: {}", appName, appResp.getErr());
            throw new IllegalStateException(appResp.getErr());
        }

        App app = appResp.getData();
        if (app == null){
            Logs.warn("the app(name={}) isn't exist.", appName);
            throw new IllegalArgumentException("app["+ appName +"] not exist");
        }

        if (!Strings.isNullOrEmpty(appKey)){
            if (!Objects.equal(appKey, app.getAppKey())){
                throw new IllegalArgumentException("app["+ appName +"]'s appKey isn't right.");
            }
        }

        return app;
    }

    @Override
    public Response<ClientSession> getSession(String clientId) {
        try {
            ClientSession session = CLIENTS.getIfPresent(clientId);
            if (session != null){
                session.setUptime(new Date());
            }
            return Response.ok(session);
        } catch (Exception e){
            Logs.error("failed to find client info(clientId={}), cause: {}", clientId, Throwables.getStackTraceAsString(e));
            return Response.notOk(messages.get("client.find.failed"));
        }
    }

    @Override
    public Response<Integer> clientCount() {
        try {
            // NOTE: guava cache don't update cache size accurately when cache item expired
            return Response.ok(CLIENTS.asMap().values().toArray().length);
        } catch (Exception e){
            Logs.error("failed to get client count, cause: {}", Throwables.getStackTraceAsString(e));
            return Response.notOk(messages.get("client.count.failed"));
        }
    }

    @Override
    public Response<List<ClientSession>> clients(final Long appId, Integer limit) {
        try {

            List<ClientSession> sessions = ImmutableList.copyOf(CLIENTS.asMap().values());
            if (sessions.isEmpty()){
                return Response.ok(Collections.emptyList());
            }

            // filter the app's sessions
            Iterable<ClientSession> appSessionIt = Iterables.filter(sessions, new Predicate<ClientSession>() {
                @Override
                public boolean apply(ClientSession session) {
                    return Objects.equal(appId, session.getAppId());
                }
            });
            List<ClientSession> appSessions = Lists.newArrayList(appSessionIt);
            if (appSessions.isEmpty()){
                return Response.ok(Collections.emptyList());
            }

            // only max limit sessions return
            limit = appSessions.size() < limit ? appSessions.size() : limit;
            return Response.ok(renderClientDto(appSessions.subList(0, limit)));

        } catch (Exception e){
            Logs.error("failed to get client count, cause: {}", Throwables.getStackTraceAsString(e));
            return Response.notOk(messages.get("client.find.failed"));
        }
    }

    private List<ClientDto> renderClientDto(List<ClientSession> clientSessions) {

        if (CollectionUtil.isEmpty(clientSessions)){
            return Collections.emptyList();
        }

        List<ClientDto> clientDtos = Lists.newArrayListWithExpectedSize(clientSessions.size());
        ClientDto clientDto;
        for (ClientSession clientSession : clientSessions){
            clientDto = new ClientDto(
                                clientSession.getId(),
                                serverHost.get(),
                                IP4s.intToIp(clientSession.getIp()) + ":" + clientSession.getPid(),
                                Dates.format(clientSession.getUptime()));
            clientDtos.add(clientDto);
        }

        return clientDtos;
    }
}
