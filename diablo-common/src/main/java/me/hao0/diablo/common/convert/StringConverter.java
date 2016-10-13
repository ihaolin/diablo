package me.hao0.diablo.common.convert;

import com.google.common.base.Converter;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class StringConverter extends Converter<String, String> {

    public static final StringConverter INSTANCE = new StringConverter();

    private StringConverter(){}

    @Override
    protected String doForward(String s) {
        return s;
    }

    @Override
    protected String doBackward(String s) {
        return s;
    }
}
