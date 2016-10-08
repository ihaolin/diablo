package me.hao0.diablo.server.service.impl;

import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import me.hao0.common.model.Page;
import me.hao0.common.security.MD5;
import me.hao0.common.util.Strings;
import me.hao0.diablo.server.dao.ConfigDao;
import me.hao0.diablo.server.dao.mgr.ConfigManager;
import me.hao0.diablo.server.model.Config;
import me.hao0.diablo.server.service.ServerService;
import me.hao0.diablo.server.support.Messages;
import me.hao0.diablo.server.util.Logs;
import me.hao0.diablo.server.util.Paging;
import me.hao0.diablo.server.model.Response;
import me.hao0.diablo.server.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private ConfigDao configDao;

    @Autowired
    private ConfigManager configManager;

    @Autowired
    private ServerService serverService;

    @Autowired
    private Messages messages;

    @Override
    public Response<Long> save(Long appId, String configName, String configValue) {
        try {

            Config exist = configDao.findByName(appId, configName);

            Boolean isCreate = Boolean.FALSE;
            if (exist == null){
                // create
                exist = new Config(appId, configName, configValue);
                isCreate = Boolean.TRUE;
            } else {
                // update
                if (Objects.equal(exist.getValue(), configValue)){
                    // value not changed
                    return Response.ok(exist.getId());
                }
                exist.setValue(configValue);
            }
            // update md5
            exist.setMd5(MD5.generate(exist.getValue(), false));

            // save the config
            configManager.save(isCreate, exist);

            if(!isCreate){
                // notify server config updated
                serverService.updatedConfig(exist.getAppId(), exist.getName());
            }

            return Response.ok(exist.getId());
        } catch (Exception e){
            Logs.error("failed to save config(appId={}, name={}, value={}), cause: {}",
                    appId, configName, configValue, Throwables.getStackTraceAsString(e));
            return Response.notOk(messages.get("config.save.failed"));
        }
    }

    @Override
    public Response<Boolean> delete(Long appId, String configName) {
        try {

            Config config = configDao.findByName(appId, configName);
            if (config == null){
                Logs.warn("the config(appId={}, configName={}) isn't exist when delete.", appId, configName);
                return Response.ok(Boolean.TRUE);
            }

            configManager.delete(config);

            return Response.ok(Boolean.TRUE);
        } catch (Exception e){
            Logs.error("failed to delete config(appId={}, name={}), cause: {}",
                    appId, configName, Throwables.getStackTraceAsString(e));
            return Response.notOk(messages.get("config.delete.failed"));
        }
    }

    @Override
    public Response<Config> findById(Long id) {
        try {
            return Response.ok(configDao.findById(id));
        } catch (Exception e){
            Logs.error("failed to find config(id={}), cause: {}", id, Throwables.getStackTraceAsString(e));
            return Response.notOk(messages.get("config.find.failed"));
        }
    }

    @Override
    public Response<Config> findByName(Long appId, String name) {
        try {
            return Response.ok(configDao.findByName(appId, name));
        } catch (Exception e){
            Logs.error("failed to find config by name(appId={}, name={}), cause: {}",
                    appId, name, Throwables.getStackTraceAsString(e));
            return Response.notOk(messages.get("config.find.failed"));
        }
    }

    @Override
    public Response<List<Config>> findByNames(Long appId, List<String> names) {
        try {
            return Response.ok(configDao.findByNames(appId, names));
        } catch (Exception e){
            Logs.error("failed to find config by names(appId={}, names={}), cause: {}",
                    appId, names, Throwables.getStackTraceAsString(e));
            return Response.notOk(messages.get("config.find.failed"));
        }
    }

    @Override
    public Response<Page<Config>> pagingConfig(Long appId, String configName, Integer pageNo, Integer pageSize) {
        try {

            // find by name
            if (!Strings.isNullOrEmpty(configName)){
                Config config = configDao.findByName(appId, configName);
                if (config == null){
                    return Response.ok(Page.empty());
                }
                return Response.ok(new Page<>(1L, Lists.newArrayList(config)));
            }

            // find paging
            Long totalCount = configDao.countByAppId(appId);
            if (totalCount <= 0L){
                return Response.ok(Page.empty());
            }

            Paging paging = new Paging(pageNo, pageSize);
            List<Config> configs = configDao.listByAppId(appId, paging.getOffset(), paging.getLimit());
            return Response.ok(new Page<>(totalCount, configs));

        } catch (Exception e){
            Logs.error("failed to paging config(appId={}, pageNo={}, pageSize={}), cause: {}",
                    appId, pageNo, pageSize, Throwables.getStackTraceAsString(e));
            return Response.notOk(messages.get("config.find.failed"));
        }
    }
}
