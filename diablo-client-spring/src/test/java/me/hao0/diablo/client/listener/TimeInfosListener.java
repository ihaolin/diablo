package me.hao0.diablo.client.listener;

import me.hao0.diablo.client.config.TimeInfo;

import java.util.List;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class TimeInfosListener implements ConfigListener<List<TimeInfo>> {

    @Override
    public String name() {
        return "timeInfos";
    }

    @Override
    public void onUpdate(List<TimeInfo> newTimeInfos) {
        System.out.println("timeInfos has updated to: " + newTimeInfos);
    }
}
