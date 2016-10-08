package me.hao0.diablo.client;

import com.fasterxml.jackson.databind.JavaType;
import com.google.common.base.Converter;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.primitives.*;
import me.hao0.common.util.Fields;
import me.hao0.diablo.client.converter.BooleanConverter;
import me.hao0.diablo.client.converter.JavaTypeConverter;
import me.hao0.diablo.client.converter.JsonConverter;
import me.hao0.diablo.common.model.ConfigItem;
import me.hao0.diablo.common.util.CollectionUtil;
import me.hao0.diablo.common.util.JsonUtil;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
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
            configItemBean = new ConfigItemBean(configBean, configItemField, determineConverter(configItemField));
            configItemBeanSet = configItemBeans.get(configName);
            if (configItemBeanSet == null){
                configItemBeanSet = Sets.newHashSet();
                configItemBeans.put(configName, configItemBeanSet);
            }
            configItemBeanSet.add(configItemBean);
        }
    }

    /**
     * Determine the converter with the field type
     * @param field the field
     * @return the Converter
     */
    private Converter<String, ?> determineConverter(Field field) {

        Class fieldClass = field.getType();

        if (String.class.equals(fieldClass)){
            return null;
        } else if (Boolean.class.equals(fieldClass) || boolean.class.equals(fieldClass)){
            return BooleanConverter.INSTANCE;
        } else if (Integer.class.equals(fieldClass) || int.class.equals(fieldClass)){
            return Ints.stringConverter();
        } else if (Long.class.equals(fieldClass) || long.class.equals(fieldClass)) {
            return Longs.stringConverter();
        } else if (Short.class.equals(fieldClass) || short.class.equals(fieldClass)){
            return Shorts.stringConverter();
        } else if (Float.class.equals(fieldClass) || float.class.equals(fieldClass)){
            return Floats.stringConverter();
        } else if (Double.class.equals(fieldClass) || double.class.equals(fieldClass)){
            return Doubles.stringConverter();
        } else if (List.class.equals(fieldClass) || Map.class.equals(fieldClass)){
            // List or Map
            JavaType type;
            ParameterizedType parameterizedType = (ParameterizedType)field.getGenericType();
            if (List.class.equals(fieldClass)){
                // List
                Class<?> listGenericType = (Class<?>)parameterizedType.getActualTypeArguments()[0];
                type = JsonUtil.INSTANCE.createCollectionType(List.class, listGenericType);
            } else {
                // Map
                Class<?> mapKeyType = (Class<?>)parameterizedType.getActualTypeArguments()[0];
                Class<?> mapValueType = (Class<?>)parameterizedType.getActualTypeArguments()[1];
                type = JsonUtil.INSTANCE.createCollectionType(Map.class, mapKeyType, mapValueType);
            }
            return new JavaTypeConverter(type);
        }

        // Common Object
        return new JsonConverter(fieldClass);
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
                            c.converter == null ? update.getValue() : c.converter.convert(update.getValue())
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
