package me.hao0.diablo.client.converter;

import com.fasterxml.jackson.databind.JavaType;
import com.google.common.base.Converter;
import me.hao0.diablo.common.util.JsonUtil;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class JavaTypeConverter extends Converter<String, Object> {

    private final JavaType type;

    public JavaTypeConverter(JavaType type){
        this.type = type;
    }

    @Override
    protected Object doForward(String json) {
        return JsonUtil.INSTANCE.fromJson(json, type);
    }

    @Override
    protected String doBackward(Object obj) {
        return JsonUtil.INSTANCE.toJson(obj);
    }
}
