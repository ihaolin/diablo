package me.hao0.diablo.common.util;

import java.util.Collection;
import java.util.Map;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public abstract class CollectionUtil {

    public static boolean isEmpty(Collection c){
        return c == null || c.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> m){
        return m == null || m.isEmpty();
    }
}
