package me.hao0.diablo.client.listener;

import me.hao0.diablo.client.config.TimeInfo;
import java.util.Map;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class TimeInfoMapListener implements ConfigListener<Map<String, TimeInfo>> {

    @Override
    public String name() {
        return "timeInfoMap";
    }

    @Override
    public void onUpdate(Map<String, TimeInfo> newTimeInfoMap) {
        System.out.println("timeInfoMap has updated to: " + newTimeInfoMap);
    }
}
