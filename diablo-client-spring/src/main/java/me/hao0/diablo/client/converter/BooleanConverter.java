package me.hao0.diablo.client.converter;

import com.google.common.base.Converter;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class BooleanConverter extends Converter<String, Boolean> {

    public static final BooleanConverter INSTANCE = new BooleanConverter();

    private BooleanConverter(){}

    @Override
    protected Boolean doForward(String s) {
        return Boolean.parseBoolean(s);
    }

    @Override
    protected String doBackward(Boolean b) {
        return String.valueOf(b);
    }
}
