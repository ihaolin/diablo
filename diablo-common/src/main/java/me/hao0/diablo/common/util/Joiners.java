package me.hao0.diablo.common.util;

import com.google.common.base.Joiner;

public class Joiners {

    public static final Joiner DOT = Joiner.on(".").skipNulls();

    public static final Joiner COMMA = Joiner.on(",").skipNulls();

    public static final Joiner COLON = Joiner.on(":").skipNulls();

    public static final Joiner AT = Joiner.on("@").skipNulls();

    public static final Joiner SLASH = Joiner.on("/").skipNulls();

    public static final Joiner SPACE = Joiner.on(" ").skipNulls();

    public static Joiner newJoiner(String delimit){
        return Joiner.on(delimit).skipNulls();
    }
}