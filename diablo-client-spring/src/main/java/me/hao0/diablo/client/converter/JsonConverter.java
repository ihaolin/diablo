package me.hao0.diablo.client.converter;

import com.google.common.base.Converter;
import me.hao0.diablo.common.util.JsonUtil;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class JsonConverter extends Converter<String, Object> {

    private final Class<?> clazz;

    public JsonConverter(Class<?> clazz){
        this.clazz = clazz;
    }

    @Override
    protected Object doForward(String json) {
        return JsonUtil.INSTANCE.fromJson(json, clazz);
    }

    @Override
    protected String doBackward(Object obj) {
        return JsonUtil.INSTANCE.toJson(obj);
    }
}
