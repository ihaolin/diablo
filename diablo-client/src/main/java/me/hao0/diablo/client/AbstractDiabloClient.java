package me.hao0.diablo.client;

import com.google.common.base.Converter;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hao0.diablo.client.listener.ConfigListener;
import me.hao0.diablo.common.convert.Converters;
import me.hao0.diablo.common.model.ConfigItem;
import me.hao0.diablo.common.util.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static me.hao0.common.util.Preconditions.checkNotNullAndEmpty;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
abstract class AbstractDiabloClient {

    private static final Logger log = LoggerFactory.getLogger(AbstractDiabloClient.class);

    /**
     * The app name
     */
    private String appName;

    /**
     * The app key
     */
    private String appKey;

    /**
     * The currentServer list
     */
    private String servers;

    /**
     * The pulling timeout(s)
     */
    private int pullingTimeout = 30;

    /**
     * The config md5 s
     */
    private final Map<String, String> configMd5s = Maps.newHashMap();

    private Map<String, ConfigListenerWrapper> listeners = Maps.newHashMap();

    private volatile boolean inited;

    private volatile boolean destoryed;

    DiabloAgent agent;

    DiabloWorker worker;

    public String getAppName() {
        return appName;
    }

    public String getAppKey() {
        return appKey;
    }

    public String getServers() {
        return servers;
    }

    public int getPullingTimeout() {
        return pullingTimeout;
    }

    public void setAppName(String appName) {
        checkNotNullAndEmpty(appName, "appName");
        this.appName = appName;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public void setServers(String servers) {
        checkNotNullAndEmpty(servers, "servers");
        this.servers = servers;
    }

    public void setListeners(List<ConfigListener> listeners) {
        if (!CollectionUtil.isEmpty(listeners)){
            for (ConfigListener listener : listeners){
                addListener(listener);
            }
        }
    }

    public void addListener(ConfigListener listener){
        ConfigListenerWrapper listenerWrapper = wrapListener(listener);
        listeners.put(listener.name(), listenerWrapper);
    }

    private ConfigListenerWrapper wrapListener(ConfigListener listener) {
        Converter<String, ?> converter = Converters.determine(listener);
        return new ConfigListenerWrapper(converter, listener);
    }

    /**
     * Start client for some initialization
     */
    public void start(){

        if (inited) {
            return;
        }

        // init agent
        agent = new DiabloAgent(this);
        agent.start();

        // init config need pulling items
        Set<String> needPullingConfigs = initConfigNames();
        List<ConfigItem> pullingConfigItems;
        if (CollectionUtil.isEmpty(needPullingConfigs)){
            // fetch all config from server with current app
            pullingConfigItems = agent.fetchAllConfig();
        } else {
            // fetch with some config names
            pullingConfigItems = agent.fetchConfigs(needPullingConfigs);
        }

        if (CollectionUtil.isEmpty(pullingConfigItems)){
            log.warn("there are no configs discovered, please check your config or remove diablo component.");
            return;
        }

        refreshConfigs(pullingConfigItems, false);

        // start the pulling worker
        worker = new DiabloWorker(this);
        worker.start();

        inited = true;

        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                shutdown();
            }
        });
    }

    public void shutdown(){
        if (destoryed) {
            return;
        }

        // un register current client
        agent.unRegister();

        destoryed = true;
    }

    /**
     * Pulling all updated configs
     */
    void pullingConfigs(){

        Set<String> updatedConfigs = pullingUpdatedConfigs(configMd5s);
        if (updatedConfigs != null && !updatedConfigs.isEmpty()){
            // fetch all updated config
            List<ConfigItem> configItems = fetchConfigs(Sets.newHashSet(updatedConfigs));
            for (ConfigItem item : configItems){
                // update configs' md5
                configMd5s.put(item.getName(), item.getMd5());
            }
            // tell subclass to refresh updated configs
            refreshConfigs(configItems, true);
        }
    }

    /**
     * Pulling updated configs' names
     */
    private Set<String> pullingUpdatedConfigs(Map<String, String> configItemMaps){
        return agent.pullingUpdatedConfigs(configItemMaps, pullingTimeout);
    }

    /**
     * Fetch the configs from server
     * @param names config names
     * @return config items
     */
    private List<ConfigItem> fetchConfigs(Set<String> names){
        return agent.fetchConfigs(names);
    }

    /**
     * Refresh the config items
     * @param configItems the latest config items
     * @param notify notify the listeners or not
     */
    private void refreshConfigs(final List<ConfigItem> configItems, boolean notify){

        // update config md5
        for (ConfigItem configItem : configItems){
            configMd5s.put(configItem.getName(), configItem.getMd5());
        }

        // tell subclass to update
        onConfigsUpdated(configItems);

        // notify listeners
        if (notify){
            notifyListeners(configItems);
        }
    }

    @SuppressWarnings("unchecked")
    private void notifyListeners(final List<ConfigItem> configItems) {
        if (!listeners.isEmpty()){
            ConfigListenerWrapper wrapper;
            for (ConfigItem item : configItems){
                wrapper = listeners.get(item.getName());
                if (wrapper != null){
                    wrapper.listener.onUpdate(
                            wrapper.converter.convert(item.getValue()));
                }
            }
        }
    }

    /**
     * Let subclass start the needed config names
     * @return the needed config names
     */
    abstract Set<String> initConfigNames();

    /**
     * Tell subclass refresh config items
     * @param configItems updated configs
     */
    abstract protected void onConfigsUpdated(List<ConfigItem> configItems);

    private class ConfigListenerWrapper {

        Converter converter;

        ConfigListener listener;

        ConfigListenerWrapper(Converter converter, ConfigListener listener){
            this.converter = converter;
            this.listener = listener;
        }
    }
}
