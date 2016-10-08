package me.hao0.diablo.server.util;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import me.hao0.diablo.common.model.ConfigItem;
import me.hao0.diablo.server.model.Config;
import java.util.Collections;
import java.util.List;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public final class ConfigItemFactory {

    private ConfigItemFactory(){}

    /**
     * Create config item from config
     * @param config the config
     * @return the config item
     */
    public static ConfigItem create(Config config){

        ConfigItem configItem = new ConfigItem();

        configItem.setName(config.getName());
        configItem.setValue(config.getValue());
        configItem.setMd5(config.getMd5());

        return configItem;
    }

    /**
     * Create config items from configs
     * @param configs the configs
     * @return the config items
     */
    public static List<ConfigItem> creates(List<Config> configs) {

        if (configs == null || configs.isEmpty()){
            return Collections.emptyList();
        }

        return Lists.transform(configs, new Function<Config, ConfigItem>() {
            @Override
            public ConfigItem apply(Config config) {
                return create(config);
            }
        });
    }
}
