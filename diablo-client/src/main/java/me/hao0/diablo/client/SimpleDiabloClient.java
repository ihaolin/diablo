package me.hao0.diablo.client;

import com.google.common.collect.Maps;
import me.hao0.common.util.Strings;
import me.hao0.diablo.common.model.ConfigItem;
import me.hao0.diablo.common.util.JsonUtil;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class SimpleDiabloClient extends AbstractDiabloClient {

    /**
     * The local config items: name -> value
     */
    private Map<String, String> configValues = Maps.newHashMap();

    /**
     * The config names need pulling
     */
    private final Set<String> configNames;

    public SimpleDiabloClient() {
        this(null);
    }

    public SimpleDiabloClient(Set<String> configNames){
        this.configNames = configNames;
    }

    public Set<String> initConfigNames() {
        return configNames;
    }

    /**
     * Refresh local configs
     * @param configItems latest configs
     */
    @Override
    public void onConfigsUpdated(List<ConfigItem> configItems) {
        if (configItems != null && !configItems.isEmpty()){
            for (ConfigItem configItem : configItems){
                configValues.put(configItem.getName(), configItem.getValue());
            }
        }
    }

    /**
     * Get the config's latest value
     * @param configName the config name
     * @return the config's latest value
     */
    public String get(String configName){
        return configValues.get(configName);
    }

    /**
     * Get the config's latest value as JSON object
     * @param configName config name
     * @param classType object class type
     * @param <T> class generic type
     * @return the config's latest JSON object
     */
    public <T> T get(String configName, Class<T> classType){
        String configJson = configValues.get(configName);
        if (Strings.isNullOrEmpty(configJson)){
            return null;
        }
        return JsonUtil.INSTANCE.fromJson(configJson, classType);
    }
}
