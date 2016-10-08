package me.hao0.diablo.server.api;

import me.hao0.common.model.Page;
import me.hao0.common.util.Strings;
import me.hao0.diablo.common.model.ConfigItem;
import me.hao0.diablo.common.model.ServerRouteResp;
import me.hao0.diablo.common.util.JsonUtil;
import me.hao0.diablo.common.util.Splitters;
import me.hao0.diablo.common.util.ClientUris;
import me.hao0.diablo.server.context.ClientContext;
import me.hao0.diablo.server.model.ClientSession;
import me.hao0.diablo.server.model.Config;
import me.hao0.diablo.server.model.Response;
import me.hao0.diablo.server.service.ClientService;
import me.hao0.diablo.server.service.ClusterService;
import me.hao0.diablo.server.service.ConfigService;
import me.hao0.diablo.server.support.PullingSupport;
import me.hao0.diablo.server.util.ConfigItemFactory;
import me.hao0.diablo.server.util.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@RestController
@RequestMapping(value = ClientUris.CLIENT_API)
public class ClientApis {

    @Autowired
    private ConfigService configService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private PullingSupport pullingSupport;

    /**
     * Route a server to client
     */
    @RequestMapping(value = ClientUris.SERVER_ROUTE, method = RequestMethod.GET)
    public ServerRouteResp route(
            @RequestParam("appName") String appName,
            @RequestParam("clientPid") Integer clientPid,
            HttpServletRequest req){

        Response<ServerRouteResp> routeResp = clusterService.routeServer(appName, req.getRemoteAddr(), clientPid);
        if (!routeResp.isSuccess()){
            return null;
        }

        return routeResp.getData();
    }

    /**
     * Register the client
     */
    @RequestMapping(value = ClientUris.REGISTER, method = RequestMethod.GET)
    public Boolean register(
            @RequestParam("clientId") String clientId,
            @RequestParam("appName") String appName,
            @RequestParam("appKey") String appKey,
            @RequestParam("clientPid") Integer clientPid,
            HttpServletRequest req){

        Response<Boolean> routeResp = clientService.registerClient(
                            clientId, appName, appKey, req.getRemoteAddr(), clientPid);
        if (!routeResp.isSuccess()){
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    /**
     * Un register the client
     */
    @RequestMapping(value = ClientUris.UN_REGISTER, method = RequestMethod.GET)
    public void unRegister(){

        ClientSession clientSession = ClientContext.get();
        assert clientSession != null;

        clientService.unRegisterClient(clientSession.getId());
    }

    /**
     * Long pulling config
     */
    @RequestMapping(value = ClientUris.CONFIG_PULLING, method = RequestMethod.POST)
    public void pulling(
            @RequestParam("configs") String configs,
            HttpServletRequest req, HttpServletResponse resp){

        ClientSession client = ClientContext.get();
        assert client != null;

        if (Strings.isNullOrEmpty(configs)){
            return;
        }

        Map<String, String> pullingConfigs = JsonUtil.INSTANCE.fromJson(configs, JsonUtil.MAP_STR_STR_TYPE);

        pullingSupport.pulling(client, pullingConfigs, req, resp);
    }

    /**
     * Fetch the config
     * @param name config name
     */
    @RequestMapping(value = ClientUris.CONFIG_FETCH, method = RequestMethod.GET)
    public ConfigItem fetch(@RequestParam("name") String name){

        ClientSession client = ClientContext.get();
        assert client != null;

        // fetch config
        Response<Config> configResp = configService.findByName(client.getAppId(), name);
        if (!configResp.isSuccess()){
            Logs.error("failed to find config(name={}), cause: {}", name, configResp.getErr());
            return null;
        }

        return ConfigItemFactory.create(configResp.getData());
    }

    /**
     * Fetch some configs
     * @param names config names
     */
    @RequestMapping(value = ClientUris.CONFIG_FETCHES, method = RequestMethod.GET)
    public List<ConfigItem> fetches(@RequestParam("names") String names){

        ClientSession client = ClientContext.get();
        assert client != null;

        List<String> nameList = Splitters.COMMA.splitToList(names);

        // fetch configs
        Response<List<Config>> configsResp = configService.findByNames(client.getAppId(), nameList);
        if (!configsResp.isSuccess()){
            Logs.error("failed to fetch configs(names={}), cause: {}", names, configsResp.getErr());
            return Collections.emptyList();
        }

        return ConfigItemFactory.creates(configsResp.getData());
    }

    /**
     * Fetch all configs
     */
    @RequestMapping(value = ClientUris.CONFIG_FETCH_ALL, method = RequestMethod.GET)
    public List<ConfigItem> fetchAll(){

        ClientSession client = ClientContext.get();
        assert client != null;

        // fetch all configs
        Response<Page<Config>> pageResp = configService.pagingConfig(client.getAppId(), null, 1, Integer.MAX_VALUE);
        if (!pageResp.isSuccess()){
            Logs.error("failed to fetch all configs(app={}), cause: {}", client, pageResp.getErr());
            return Collections.emptyList();
        }

        return ConfigItemFactory.creates(pageResp.getData().getData());
    }
}
