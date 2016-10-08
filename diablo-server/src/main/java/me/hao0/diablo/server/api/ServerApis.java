package me.hao0.diablo.server.api;

import me.hao0.diablo.common.util.Constants;
import me.hao0.diablo.server.cluster.ServerRouter;
import me.hao0.diablo.server.event.ConfigUpdatedEvent;
import me.hao0.diablo.server.event.EventDispatcher;
import me.hao0.diablo.server.model.ClientSession;
import me.hao0.diablo.server.model.Response;
import me.hao0.diablo.server.service.ClientService;
import me.hao0.diablo.server.service.LocalService;
import me.hao0.diablo.server.util.Logs;
import me.hao0.diablo.server.util.ServerUris;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@RestController
@RequestMapping(ServerUris.SERVERS)
public class ServerApis {

    @Autowired
    private ServerRouter serverRouter;

    @Autowired
    private LocalService localService;

    @Autowired
    private EventDispatcher eventDispatcher;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ConfigurableApplicationContext context;

    @RequestMapping(value = ServerUris.CONFIG_UPDATED, method = RequestMethod.GET)
    public boolean configUpdated(@RequestParam("appId") Long appId,
                                 @RequestParam(value = "name") String name){
        Logs.nofity("notified: config(appId={}, name={}) is updated", appId, name);

        // config is updated, sync local cache
        localService.sync(appId, name);

        // post config updated event
        eventDispatcher.post(ConfigUpdatedEvent.newEvent(appId, name));

        return true;
    }

    @RequestMapping(value = ServerUris.SERVER_JOINED, method = RequestMethod.GET)
    public boolean serverJoined(@RequestParam(value = "server") String server){
        Logs.nofity("notified: the new server({}) joined", server);
        serverRouter.join(server);
        return true;
    }

    @RequestMapping(value = ServerUris.SERVER_LEFT, method = RequestMethod.GET)
    public boolean serverLeft(@RequestParam(value = "server") String server){
        Logs.nofity("notified: the old server({}) left", server);
        serverRouter.leave(server);
        return true;
    }

    @RequestMapping(value = ServerUris.CACHE_CLEAN, method = RequestMethod.GET)
    public boolean cleanCache(){
        Logs.nofity("notified: will clean cache");
        localService.cleanCache();
        return true;
    }

    @RequestMapping(value = ServerUris.SHUTDOWN, method = RequestMethod.GET)
    public boolean shutdown(){
        Logs.nofity("notified: will be shutdown.");
        try {
            return true;
        } finally {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(500L);
                    } catch (InterruptedException ex) {
                        // Swallow exception and continue
                    }
                    context.stop();
                    System.exit(0);
                }
            }).start();
        }
    }

    @RequestMapping(value = ServerUris.STATUS, method = RequestMethod.GET)
    public String health(){
        return Constants.SERVER_OK;
    }

    @RequestMapping(value = ServerUris.CLIENT_COUNT, method = RequestMethod.GET)
    public Integer clientCount(){
        Response<Integer> clientCountResp = clientService.clientCount();
        if (!clientCountResp.isSuccess()){
            return 0;
        }
        return clientCountResp.getData();
    }

    @RequestMapping(value = ServerUris.CLIENTS, method = RequestMethod.GET)
    public List<ClientSession> clients(
        @RequestParam(value = "appId") Long appId,
        @RequestParam(value = "limit", defaultValue = "100") Integer limit){

        Response<List<ClientSession>> clientsResp = clientService.clients(appId, limit);
        if (!clientsResp.isSuccess()){
            return null;
        }

        return clientsResp.getData();
    }
}
