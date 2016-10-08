package me.hao0.diablo.server;

import me.hao0.diablo.server.model.Config;
import me.hao0.diablo.server.util.Maps;
import org.junit.Test;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class MapsTests {

    @Test
    public void testObject2Map() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        Config config = new Config();
        config.setAppId(1L);
        config.setName("test");
        config.setValue("test_value");
        config.setUtime(new Date());
        config.setCtime(new Date());

        Maps.toMap(config);
        int count = 10000;

        long start = System.currentTimeMillis();
        for (int i=0; i<count; i++){
            Maps.toMap(config);
        }
        System.err.println("cost: " + (System.currentTimeMillis() - start));
    }

    @Test
    public void testMap2Object(){
        Map<String, Object> map = new HashMap<>();
        map.put("name", "test");
        map.put("value", "test_value");
        map.put("ctime", new Date().getTime() + "");
        map.put("utime", new Date().getTime() + "");
        map.put("appId", "1");
        System.out.println(Maps.fromMap(map, Config.class));

        int count = 10000;

        long start = System.currentTimeMillis();
        for (int i=0; i<count; i++){
            Maps.fromMap(map, Config.class);
        }
        System.err.println("cost: " + (System.currentTimeMillis() - start));
    }
}
