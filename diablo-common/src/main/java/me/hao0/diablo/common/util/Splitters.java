package me.hao0.diablo.common.util;

import com.google.common.base.Splitter;

public class Splitters {

    public static final Splitter DOT = Splitter.on(".").omitEmptyStrings().trimResults();

    public static final Splitter COMMA = Splitter.on(",").omitEmptyStrings().trimResults();

    public static final Splitter COLON = Splitter.on(":").omitEmptyStrings().trimResults();

    public static final Splitter AT = Splitter.on("@").omitEmptyStrings().trimResults();

    public static final Splitter SLASH = Splitter.on("/").omitEmptyStrings().trimResults();

    public static final Splitter SPACE = Splitter.on(" ").omitEmptyStrings().trimResults();

    public static final Splitter UNDERSCORE = Splitter.on("_").omitEmptyStrings().trimResults();

}