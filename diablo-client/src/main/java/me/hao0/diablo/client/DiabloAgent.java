package me.hao0.diablo.client;

import com.fasterxml.jackson.databind.JavaType;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import me.hao0.common.http.Http;
import me.hao0.common.http.HttpMethod;
import me.hao0.common.util.Strings;
import me.hao0.diablo.client.exception.AuthFailException;
import me.hao0.diablo.client.exception.RouteServerException;
import me.hao0.diablo.client.exception.Server503Exception;
import me.hao0.diablo.common.model.ConfigItem;
import me.hao0.diablo.common.model.JsonResponse;
import me.hao0.diablo.common.model.ServerRouteResp;
import me.hao0.diablo.common.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
class DiabloAgent {

    private static final Logger log = LoggerFactory.getLogger(DiabloAgent.class);

    /**
     * The client version
     */
    private static final String CLIENT_VERSION = "1.2.1";

    private AbstractDiabloClient client;

    /**
     * The current using server
     */
    private volatile String currentServer;

    /**
     * The request headers
     */
    private final Map<String, String> headers = Maps.newHashMap();

    DiabloAgent(AbstractDiabloClient client){
        this.client = client;
    }

    void start() {
        // route an available server
        routeServer();
    }

    /**
     * Fetch config from server
     * @param names config names
     * @return config items
     */
    List<ConfigItem> fetchConfigs(Set<String> names){
        Map<String, Object> params = Maps.newHashMapWithExpectedSize(1);
        params.put("names", Joiners.COMMA.join(names));
        return doGet(ClientUris.CONFIG_FETCHES, headers, params, JsonUtil.LIST_CONFIG_ITEM_TYPE);
    }

    public List<ConfigItem> fetchAllConfig() {
        return doGet(ClientUris.CONFIG_FETCH_ALL, headers, null, JsonUtil.LIST_CONFIG_ITEM_TYPE);
    }

    /**
     * Pulling updated configs
     * @param configItemMaps pulling configs: key is config name, value is md5
     * @param readTimeout read timeout(s)
     * @return updated config names
     */
    public Set<String> pullingUpdatedConfigs(Map<String, String> configItemMaps, int readTimeout) {

        Map<String, Object> params = Maps.newHashMapWithExpectedSize(1);
        params.put("configs", JsonUtil.INSTANCE.toJson(configItemMaps));

        return doPost(ClientUris.CONFIG_PULLING, headers, params, readTimeout, Set.class);
    }

    /**
     * Route a server from remote
     */
    void routeServer(){

        Map<String, Object> params = Maps.newHashMap();
        params.put("appName", client.getAppName());
        params.put("clientPid", SysUtil.pid());

        String[] serverList = client.getServers().split(",");

        for (String server : serverList){
            try {
                // request route server
                ServerRouteResp resp = doGet(server, ClientUris.SERVER_ROUTE, null, params, ServerRouteResp.class);
                if(resp == null){
                    log.warn("server({}) route is null", server);
                    continue;
                }

                String routedServer = resp.getServer();
                String clientId = resp.getClientId();


                // register to the routed server
                params.put("clientId", clientId);
                params.put("appKey", client.getAppKey());
                Boolean registerResp = doGet(routedServer, ClientUris.REGISTER, null, params, Boolean.class);
                if (registerResp == Boolean.FALSE){
                    log.warn("failed to register server({})", routedServer);
                    continue;
                }

                // set current server
                currentServer = routedServer;

                // set headers for next requests
                headers.put(Constants.CLIENT_ID_HEADER, clientId);
                headers.put(Constants.APP_KEY_HEADER, client.getAppKey());
                headers.put(Constants.CLIENT_VERSION_HEADER, CLIENT_VERSION);
                headers.put(Constants.PULLING_TIMEOUT_HEADER, String.valueOf(client.getPullingTimeout()));

                log.info("route successfully, current server is {}", currentServer);
                // route successfully
                return;
            } catch (HttpRequest.HttpRequestException e){
                log.warn("server({}) is unavailable, trying next server", server);
            }
        }
        throw new Server503Exception("all servers are unavailable: " + Arrays.toString(serverList));
    }

    protected void unRegister() {
        doGet(ClientUris.UN_REGISTER, headers, null, Boolean.class);
    }

    <T> T doGet(String uri, Map<String, String> headers, Map<String, Object> params, Object respType){
        for (;;){
            try {
                return doGet(currentServer, uri, headers, params, respType);
            } catch (AuthFailException e){
                throw new IllegalArgumentException("appKey isn't right, please check");
            } catch (RouteServerException | HttpRequest.HttpRequestException e){
                String oldServer = currentServer;
                log.warn("current server({}) is unavailable, trying to route a new server", oldServer);
                routeServer();
                log.warn("old server({}) is unavailable, routed a new server({})", oldServer, currentServer);
            }
        }
    }

    private <T> T doGet(String server, String uri, Map<String, String> headers, Map<String, Object> params, Object respType){
        return doRequest(server, uri, HttpMethod.GET, headers, params, 0, respType);
    }

    <T> T doPost(String uri, Map<String, String> headers, Map<String, Object> params, int readTimeout, Object respType){
        for (;;){
            try {
                return doPost(currentServer, uri, headers, params, readTimeout, respType);
            } catch (AuthFailException e){
                throw new IllegalArgumentException("appKey isn't right, please check");
            } catch (RouteServerException | HttpRequest.HttpRequestException e){
                String oldServer = currentServer;
                log.warn("current server({}) is maybe unavailable, trying to route a new server", oldServer);
                routeServer();
                log.warn("old server({}) is unavailable, routed a new server({})", oldServer, currentServer);
            }
        }
    }

    private <T> T doPost(String server, String uri, Map<String, String> headers, Map<String, Object> params, int readTimeout, Object respType){
        return doRequest(server, uri, HttpMethod.POST, headers, params, readTimeout, respType);
    }


    @SuppressWarnings("unchecked")
    private <T> T doRequest(String server, String uri, HttpMethod method,
                            Map<String, String> headers, Map<String, Object> params, int readTimeout, Object respType){

        String reqUri = Constants.HTTP_PREFIX + server + ClientUris.CLIENT_API + uri;

        Http http;
        if (method == HttpMethod.GET){
            http = Http.get(reqUri);
        } else {
            http = Http.post(reqUri);
        }

        if (readTimeout > 0){
            http.readTimeout(readTimeout);
        }

        if (headers != null){
            http.headers(headers);
        }

        String resp = http.params(params).request();
        if (Strings.isNullOrEmpty(resp)){
            return null;
        }

        checkRespErr(resp);

        return respType instanceof JavaType ?
                (T)JsonUtil.INSTANCE.fromJson(resp, (JavaType) respType) :
                JsonUtil.INSTANCE.fromJson(resp, (Class<T>) respType);
    }

    private void checkRespErr(String resp) {

        if (Objects.equal(JsonResponse.SERVER_ERR.getStatus().toString(), resp)){
            throw new RouteServerException();
        } else if (Objects.equal(JsonResponse.NEED_LOGIN.getStatus().toString(), resp)){
            throw new RouteServerException();
        } else if (Objects.equal(JsonResponse.AUTH_FAIL.getStatus().toString(), resp)){
            throw new AuthFailException();
        }
    }
}
