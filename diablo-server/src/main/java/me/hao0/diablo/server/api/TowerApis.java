package me.hao0.diablo.server.api;

import com.google.common.collect.Lists;
import me.hao0.common.date.Dates;
import me.hao0.common.model.Page;
import me.hao0.diablo.common.model.JsonResponse;
import me.hao0.diablo.server.dto.ClientDto;
import me.hao0.diablo.server.dto.PushLogDto;
import me.hao0.diablo.server.model.*;
import me.hao0.diablo.server.service.*;
import me.hao0.diablo.server.util.Logs;
import me.hao0.diablo.server.util.TowerUris;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@RestController
@RequestMapping(value = TowerUris.PREFIX)
public class TowerApis {

    @Autowired
    private AppService appService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ServerService serverService;

    @Autowired
    private PushLogService pushLogService;

    /**
     * Paging apps
     */
    @RequestMapping(value = "/apps", method = RequestMethod.GET)
    public JsonResponse pageApps(
            @RequestParam(value = "appName", defaultValue = "") String appName,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){

        // find apps
        Response<Page<App>> pageResp = appService.pagingApp(appName, pageNo, pageSize);
        if (!pageResp.isSuccess()){
            Logs.error("failed to page apps(appName={}, pageNo={}, pageSize={}), cause: {}", appName, pageNo, pageSize, pageResp.getErr());
            return JsonResponse.notOk(pageResp.getErr());
        }

        return JsonResponse.ok(pageResp.getData());
    }

    /**
     * Save the app
     */
    @RequestMapping(value = "/apps", method = RequestMethod.POST)
    public JsonResponse saveApp(
            @RequestParam("appName") String appName,
            @RequestParam("appKey") String appKey,
            @RequestParam("appDesc") String appDesc,
            @RequestParam(value = "inheritAppId" ,defaultValue = "") Long inheritAppId){

        App app = new App();
        app.setAppName(appName);
        app.setAppKey(appKey);
        app.setAppDesc(appDesc);

        Response<Long> saveResp = appService.save(app);
        if (!saveResp.isSuccess()){
            Logs.error("failed to save app({}), cause: {}", app, saveResp.getErr());
            return JsonResponse.notOk(saveResp.getErr());
        }

        appService.inheritConfigs(inheritAppId, saveResp.getData());

        return JsonResponse.ok(saveResp.getData());
    }

    /**
     * Delete the app
     */
    @RequestMapping(value = "/apps/del", method = RequestMethod.POST)
    public JsonResponse delApp(@RequestParam("appName") String appName){

        Response<Boolean> delResp = appService.delete(appName);
        if (!delResp.isSuccess()){
            Logs.error("failed to delete app({}), cause: {}", appName, delResp.getErr());
        }

        return JsonResponse.ok(delResp.getData());
    }

    /**
     * Paging configs
     */
    @RequestMapping(value = "/configs", method = RequestMethod.GET)
    public JsonResponse pagingConfig(
            @RequestParam("appId") Long appId,
            @RequestParam(value = "configName", defaultValue = "") String configName,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){

        Response<Page<Config>> pagingResp = configService.pagingConfig(appId, configName, pageNo, pageSize);
        if (!pagingResp.isSuccess()){
            Logs.error("failed to paging config(appId={}, configName={}), cause: {}",
                    appId, configName, pagingResp.getErr());
            return JsonResponse.notOk(pagingResp.getErr());
        }

        return JsonResponse.ok(pagingResp.getData());
    }

    /**
     * Save the config
     */
    @RequestMapping(value = "/configs", method = RequestMethod.POST)
    public JsonResponse saveConfig(
            @RequestParam("appId") Long appId,
            @RequestParam("name") String name,
            @RequestParam("value") String value){

        Response<Long> saveResp = configService.save(appId, name, value);
        if (!saveResp.isSuccess()){
            Logs.error("failed to save config(appId={}, name={}, value={}), cause: {}",
                    appId, name, value, saveResp.getErr());
            return JsonResponse.notOk(saveResp.getErr());
        }

        return JsonResponse.ok(saveResp.getData());
    }

    /**
     * Delete the config
     */
    @RequestMapping(value = "/configs/del", method = RequestMethod.POST)
    public JsonResponse delConfig(
            @RequestParam("appId") Long appId,
            @RequestParam("configName") String configName){

        Response<Boolean> deleteResp = configService.delete(appId, configName);
        if (!deleteResp.isSuccess()){
            Logs.error("failed to delete config(appId={}, name={}), cause: {}",
                    appId, configName, deleteResp.getErr());
            return JsonResponse.notOk(deleteResp.getErr());
        }

        return JsonResponse.ok(deleteResp.getData());
    }

    /**
     * List all servers of the cluster
     */
    @RequestMapping(value = "/servers", method = RequestMethod.GET)
    public JsonResponse listServers(){

        Response<List<ServerInfo>> listResp = clusterService.listServers();
        if (!listResp.isSuccess()){
            Logs.error("failed to list all servers, cause: {}", listResp.getErr());
            return JsonResponse.notOk(listResp.getErr());
        }

        return JsonResponse.ok(listResp.getData());
    }

    /**
     * Clear the server's local cache
     */
    @RequestMapping(value = "/servers/clean_cache", method = RequestMethod.POST)
    public JsonResponse cleanServerCache(@RequestParam("server") String server){
        serverService.cleanCache(server);
        return JsonResponse.ok(true);
    }

    /**
     * Shutdown the server
     */
    @RequestMapping(value = "/servers/shutdown", method = RequestMethod.POST)
    public JsonResponse shutdownServer(@RequestParam("server") String server){
        serverService.shutdownServer(server);
        return JsonResponse.ok(true);
    }

    /**
     * Paging the config push logs
     */
    @RequestMapping(value = "/push_logs", method = RequestMethod.GET)
    public JsonResponse pagingPushLogs(
            @RequestParam("appId") Long appId,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){

        Response<Page<PushLog>> pageResp = pushLogService.pagingConfigPushLog(appId, pageNo, pageSize);
        if (!pageResp.isSuccess()){
            Logs.error("failed to paging push logs(appId={}, pageNo={}, pageSize={}), cause: {}",
                    appId, pageNo, pageSize, pageResp.getErr());
            return JsonResponse.notOk(pageResp.getErr());
        }

        Page<PushLogDto> logDtoPage = render2PushLogDto(pageResp.getData());
        return JsonResponse.ok(logDtoPage);
    }

    private Page<PushLogDto> render2PushLogDto(Page<PushLog> pushLogPage) {

        List<PushLog> pushLogs = pushLogPage.getData();
        if (pushLogPage.getTotal() <= 0 || pushLogs.size() == 0){
            return Page.empty();
        }

        List<PushLogDto> pushLogDtos = Lists.newArrayListWithExpectedSize(pushLogs.size());
        PushLogDto pushLogDto;
        for (PushLog pushLog : pushLogs){
            pushLogDto = new PushLogDto(
                    pushLog.getConfig(), pushLog.getClient(), pushLog.getServer(), Dates.format(pushLog.getCtime().getTime()));
            pushLogDtos.add(pushLogDto);
        }

        return new Page<>(pushLogPage.getTotal(), pushLogDtos);
    }

    /**
     * List the clients
     */
    @RequestMapping(value = "/clients", method = RequestMethod.GET)
    public JsonResponse clients(@RequestParam("appId") Long appId){

        Response<List<ClientDto>> clientsResp = clusterService.listClients(appId);
        if (!clientsResp.isSuccess()){
            return JsonResponse.notOk(clientsResp.getErr());
        }

        return JsonResponse.ok(clientsResp.getData());
    }
}
