package me.hao0.diablo.server.service.impl;

import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import me.hao0.common.model.Page;
import me.hao0.common.util.Strings;
import me.hao0.diablo.common.util.CollectionUtil;
import me.hao0.diablo.server.dao.AppDao;
import me.hao0.diablo.server.dao.mgr.AppManager;
import me.hao0.diablo.server.model.App;
import me.hao0.diablo.server.model.Config;
import me.hao0.diablo.server.model.Response;
import me.hao0.diablo.server.service.AppService;
import me.hao0.diablo.server.service.ConfigService;
import me.hao0.diablo.server.support.Messages;
import me.hao0.diablo.server.util.Logs;
import me.hao0.diablo.server.util.Paging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Service
public class AppServiceImpl implements AppService {

    @Autowired
    private Messages messages;

    @Autowired
    private AppDao appDao;

    @Autowired
    private AppManager appManager;

    @Autowired
    private ConfigService configService;

    private static final ExecutorService APP_INHERITOR = Executors.newSingleThreadExecutor();

    private static final Integer CONFIG_INHERIT_BATCH_SIZE = 100;

    /**
     * App cache for 5 mins
     */
    private final LoadingCache<String, App> APP_CACHE =
            CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build(new CacheLoader<String, App>() {
                @Override
                public App load(String appName) throws Exception {
                    return appDao.findByName(appName);
                }
            });

    @Override
    public Response<Long> save(App app) {
        try {

            App exist = appDao.findByName(app.getAppName());
            if (exist == null){
                exist = new App();
                exist.setAppName(app.getAppName());
            }
            exist.setAppKey(app.getAppKey());
            exist.setAppDesc(app.getAppDesc());

            appManager.save(exist);

            return Response.ok(exist.getId());
        } catch (Exception e){
            Logs.error("failed to save app({}), cause: {}", app, Throwables.getStackTraceAsString(e));
            return Response.notOk(messages.get("app.save.failed"));
        }
    }

    @Override
    public Response<App> findByName(String name) {
        try {
            return Response.ok(APP_CACHE.get(name));
        } catch (Exception e){
            Logs.error("failed to find app(name={}), cause: {}", name, Throwables.getStackTraceAsString(e));
            return Response.notOk(messages.get("app.save.failed"));
        }
    }

    @Override
    public Response<Page<App>> pagingApp(String appName, Integer pageNo, Integer pageSize) {
        try {

            // find an app by name
            if (!Strings.isNullOrEmpty(appName)){
                App app = appDao.findByName(appName);
                if (app == null){
                    return Response.ok(Page.empty());
                }
                return Response.ok(new Page<>(1L, Lists.newArrayList(app)));
            }

            // find apps
            Long totalCount = appDao.count();
            if (totalCount <= 0L){
                return Response.ok(Page.empty());
            }

            Paging paging = new Paging(pageNo, pageSize);
            List<App> apps = appDao.list(paging.getOffset(), paging.getLimit());

            return Response.ok(new Page<>(totalCount, apps));
        } catch (Exception e){
            Logs.error("failed to paging app(pageNo={}, pageSize={}), cause: {}",
                    pageNo, pageSize, Throwables.getStackTraceAsString(e));
            return Response.notOk(messages.get("app.find.failed"));
        }
    }

    @Override
    public Response<Boolean> delete(String appName) {
        try {
            App app = appDao.findByName(appName);
            if (app == null){
                Logs.warn("the app({}) isn't exist when delete.", appName);
                return Response.ok(true);
            }

            appManager.delete(app);

            return Response.ok(Boolean.TRUE);
        } catch (Exception e){
            Logs.error("failed to delete the app({}), cause: {}",
                    appName, Throwables.getStackTraceAsString(e));
            return Response.notOk(messages.get("app.delete.failed"));
        }
    }

    @Override
    public Response<Boolean> inheritConfigs(Long srcAppId, Long destAppId) {

        if (srcAppId != null && destAppId != null){
            APP_INHERITOR.submit(new AppConfigInheritTask(srcAppId, destAppId));
        }

        return Response.ok(Boolean.TRUE);
    }

    private class AppConfigInheritTask implements Runnable{

        private Long srcAppId;

        private Long destAppId;

        AppConfigInheritTask(Long srcAppId, Long destAppId){
            this.srcAppId = srcAppId;
            this.destAppId = destAppId;
        }

        @Override
        public void run() {
            if (appDao.findById(srcAppId) != null
                    && appDao.findById(destAppId) != null){
                Response<Page<Config>> pageResp;
                List<Config> configs;
                Response<Long> saveResp;
                for(Integer pageNo = 1;;pageNo++){

                    pageResp = configService.pagingConfig(srcAppId, null, pageNo, CONFIG_INHERIT_BATCH_SIZE);
                    if (!pageResp.isSuccess()){
                        Logs.error("failed to paging config(srcAppId={}, destAppId={}, pageNo={}, pageSize={}) when inherit config, cause: {}",
                                srcAppId, destAppId, pageNo, CONFIG_INHERIT_BATCH_SIZE, pageResp.getErr());
                        continue;
                    }

                    configs = pageResp.getData().getData();
                    if (CollectionUtil.isEmpty(configs)){
                        return;
                    }

                    for (Config config : configs){
                        saveResp = configService.save(destAppId, config.getName(), config.getValue());
                        if (!saveResp.isSuccess()){
                            Logs.error("failed to save config(srcAppId={}, destAppId={}, name={}) when inherit config, cause: {}",
                                    srcAppId, destAppId, config.getName(), saveResp.getErr());
                        }
                    }

                    if (configs.size() < CONFIG_INHERIT_BATCH_SIZE){
                        // the last page
                        return;
                    }
                }
            }
        }
    }
}
