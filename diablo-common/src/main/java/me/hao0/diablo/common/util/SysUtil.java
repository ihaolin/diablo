package me.hao0.diablo.common.util;

import java.lang.management.ManagementFactory;

public class SysUtil {

    private SysUtil() {}

    public static String pid() {
        String processName =
               ManagementFactory.getRuntimeMXBean().getName();
        return processName.split("@")[0];
    }
}