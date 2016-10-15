package me.hao0.diablo.server.support;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import me.hao0.diablo.common.util.Constants;
import me.hao0.diablo.common.util.JsonUtil;
import me.hao0.diablo.server.cluster.ServerHost;
import me.hao0.diablo.server.event.ConfigUpdatedEvent;
import me.hao0.diablo.server.event.EventDispatcher;
import me.hao0.diablo.server.model.ClientSession;
import me.hao0.diablo.server.model.PushLog;
import me.hao0.diablo.server.model.Response;
import me.hao0.diablo.server.service.LocalService;
import me.hao0.diablo.server.service.PushLogService;
import me.hao0.diablo.server.util.IP4s;
import me.hao0.diablo.server.util.Logs;
import me.hao0.diablo.server.util.Responses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Long Pulling Support
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Component
public class PullingSupport {

    private final ScheduledExecutorService longPullingScheduler;

    @Autowired
    private ServerHost serverHost;

    @Autowired
    private LocalService localService;

    @Autowired
    private PushLogService pushLogService;

    @Autowired
    private EventDispatcher eventDispatcher;

    public PullingSupport(){
        longPullingScheduler = Executors.newScheduledThreadPool(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                t.setName("DIABLO-PULLING-WORKER");
                return t;
            }
        });
    }

    /**
     * Do pulling with config items
     * @param client the client
     * @param configItems the config items need to pulling
     * @param req the HttpServletRequest
     * @param resp the HttpServletResponse
     * @return the config item names need clients update
     */
    public String pulling(ClientSession client, Map<String, String> configItems, HttpServletRequest req, HttpServletResponse resp){

        boolean longPulling = isLongPulling(req);
        if (longPulling){
            // do long pulling
            longPulling(client, configItems, req);
            return "";
        }

        // do short pulling
        shortPulling(client, configItems, resp);

        return "";
    }

    /**
     * Do short pulling
     * @param client the client
     * @param configItems configItems
     * @param resp HttpServletResponse
     */
    private void shortPulling(ClientSession client, Map<String, String> configItems, HttpServletResponse resp) {
        // compare config item's md5 and find updated configs
        sendUpdatedConfigsIfPossible(client.getAppId(), configItems, resp);
    }

    /**
     * Do long pulling
     * @param client the client
     * @param pullingConfigs pulling config items
     * @param req HttpServletRequest
     */
    private void longPulling(ClientSession client, Map<String, String> pullingConfigs, HttpServletRequest req) {

        // start async to prevent sending the client immediately by the container
        final AsyncContext asyncContext = req.startAsync();
        asyncContext.setTimeout(0L);

        // start a long pulling task
        longPullingScheduler.submit(new LongPullingTask(client, pullingConfigs, asyncContext));
    }

    /**
     * The Long Pulling Task
     */
    private class LongPullingTask implements Runnable {

        /**
         * The app id
         */
        ClientSession client;

        /**
         * pulling configs: key is config name, value is md5
         */
        Map<String, String> pullingConfigs;

        /**
         * The request async context
         */
        AsyncContext asyncContext;

        /**
         * The pulling timeout future
         */
        Future<?> pullingTimeoutFuture;

        LongPullingTask(ClientSession client, Map<String, String> pullingConfigs, AsyncContext asyncContext){
            this.client = client;
            this.pullingConfigs = pullingConfigs;
            this.asyncContext = asyncContext;
        }

        @Override
        public void run() {

            HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();

            if (sendUpdatedConfigsIfPossible(client.getAppId(), pullingConfigs, response)){
                // updated config sent

                // stop pulling
                stopPulling();

            } else {
                // no updated config sent

                // get the pulling timeout from client
                int serverKeepPullingTimeout = getPullingTimeout((HttpServletRequest) asyncContext.getRequest());
                pullingTimeoutFuture = longPullingScheduler.schedule(new Runnable() {
                    public void run() {
                        stopPulling();
                    }
                }, serverKeepPullingTimeout, TimeUnit.SECONDS);

                // start to listen config updated
                eventDispatcher.register(this);

                // NOTE: avoid the extreme case that config updated notify before registering self
                if (sendUpdatedConfigsIfPossible(client.getAppId(), pullingConfigs, response)){
                    stopPulling();
                }
            }
        }

        @Subscribe
        public void onConfigUpdated(ConfigUpdatedEvent event){

            String configName = event.getName();

            if (Objects.equal(client.getAppId(), event.getAppId())
                    && pullingConfigs.containsKey(configName)){

                // notify client config is updated
                sendUpdatedConfigs((HttpServletResponse)asyncContext.getResponse(), configName);

                // stop pulling and flush the response
                stopPulling();

                // record push log
                recordPushLog(configName);
            }
        }

        /**
         * stop current pulling task
         */
        private void stopPulling() {

            // un register this task
            eventDispatcher.unRegister(this);

            // tell the container to send response
            asyncContext.complete();

            // cancel data changed listening
            if (pullingTimeoutFuture != null){
                pullingTimeoutFuture.cancel(false);
            }
        }

        /**
         * Record the config push log
         * @param configName the config name
         */
        private void recordPushLog(String configName) {
            PushLog pushLog = new PushLog();
            pushLog.setAppId(client.getAppId());
            pushLog.setConfig(configName);
            pushLog.setClient(IP4s.intToIp(client.getIp()) + ":" + client.getPid());
            pushLog.setServer(serverHost.get());
            pushLog.setCtime(new Date());
            pushLogService.add(pushLog);
        }
    }

    /**
     * Send updated config names if were possible
     * @param appId appId
     * @param configItems pulling config items
     * @param resp HttpServletResp
     * @return return true if send updated config names, or false
     */
    private boolean sendUpdatedConfigsIfPossible(Long appId, Map<String, String> configItems, HttpServletResponse resp) {

        Response<Set<String>> findResp = localService.checkUpdatedConfigs(appId, configItems);
        if (!findResp.isSuccess()){
            Logs.error("failed to check updated configs: {}", findResp.getErr());
            return false;
        }

        // send updated config item names
        return sendUpdatedConfigs(resp, findResp.getData());
    }

    private boolean sendUpdatedConfigs(HttpServletResponse resp, String updatedConfig) {
        return sendUpdatedConfigs(resp, Sets.newHashSet(updatedConfig));
    }

    private boolean sendUpdatedConfigs(HttpServletResponse resp, Set<String> updatedConfigs) {
        if (updatedConfigs != null && !updatedConfigs.isEmpty()){
            Responses.writeJson(resp, JsonUtil.INSTANCE.toJson(updatedConfigs));
            return true;
        }
        return false;
    }

    /**
     * The request is long pulling?
     * @param req HttpServletRequest
     * @return return true if the request's headers contain Pulling-Timeout
     */
    private boolean isLongPulling(HttpServletRequest req) {
        return req.getHeader(Constants.PULLING_TIMEOUT_HEADER) != null;
    }

    /**
     * Get the Pulling-Timeout from client, but will reduce one seconds
     * @param request HttpServletRequest
     * @return (Pulling-Timeout - 1) seconds
     */
    private int getPullingTimeout(HttpServletRequest request) {
        Integer clientPullingTimeout = Integer.parseInt(request.getHeader(Constants.PULLING_TIMEOUT_HEADER));
        return clientPullingTimeout - 1;
    }

}
