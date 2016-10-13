package me.hao0.diablo.client;

import com.google.common.base.Converter;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hao0.common.util.Fields;
import me.hao0.diablo.common.convert.Converters;
import me.hao0.diablo.common.model.ConfigItem;
import me.hao0.diablo.common.util.CollectionUtil;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class SpringDiabloClient extends AbstractDiabloClient implements InitializingBean, DisposableBean {

    /**
     * The configs store: key is config name, value are the beans contain the config name
     */
    private Map<String, Set<ConfigItemBean>> configItemBeans = Maps.newHashMap();

    @Autowired
    private ApplicationContext springContext;

    @Override
    protected Set<String> initConfigNames() {

        // start pulling configs from spring context
        Map<String, DiabloConfig> configBeans = springContext.getBeansOfType(DiabloConfig.class);
        if (configBeans.isEmpty()){
            // there are no config beans
            return Collections.emptySet();
        }

        for (DiabloConfig configBean : configBeans.values()){
            resolveConfigBeanItem(configBean);
        }

        return configItemBeans.keySet();
    }

    private void resolveConfigBeanItem(DiabloConfig configBean) {

        Class beanClazz = configBean.getClass();
        Field[] configItemFields = beanClazz.getDeclaredFields();
        if (configItemFields.length == 0){
            return;
        }

        String configName;
        ConfigItemBean configItemBean;
        Set<ConfigItemBean> configItemBeanSet;
        // resolve fields to ConfigItemBean s
        for (Field configItemField : configItemFields){
            configName = configItemField.getName();
            configItemBean = new ConfigItemBean(configBean, configItemField, Converters.determine(configItemField));
            configItemBeanSet = configItemBeans.get(configName);
            if (configItemBeanSet == null){
                configItemBeanSet = Sets.newHashSet();
                configItemBeans.put(configName, configItemBeanSet);
            }
            configItemBeanSet.add(configItemBean);
        }
    }

    @Override
    protected void onConfigsUpdated(List<ConfigItem> updates) {
        // update DiabloConfig beans' configs
        if (!CollectionUtil.isEmpty(updates)){
            Set<ConfigItemBean> configItemBeanSet;
            for (ConfigItem update : updates){
                configItemBeanSet = configItemBeans.get(update.getName());
                if (configItemBeanSet != null){
                    for (ConfigItemBean c : configItemBeanSet){
                        // convert & set
                        Fields.put(
                            c.bean,
                            c.configItem,
                            c.converter.convert(update.getValue())
                        );
                    }
                }
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // start the client
        start();
    }

    @Override
    public void destroy() throws Exception {
        // shutdown the client
        shutdown();
    }

    private class ConfigItemBean {

        /**
         * The bean implements DiabloConfig
         */
        DiabloConfig bean;

        /**
         * The bean's config item
         */
        Field configItem;

        /**
         * The config item value converter
         */
        Converter<String, ?> converter;

        ConfigItemBean(DiabloConfig bean, Field configItem, Converter<String, ?> converter){
            this.bean = bean;
            this.configItem = configItem;
            this.converter = converter;
        }
    }
}
