package me.hao0.diablo.client.listener;

import me.hao0.diablo.client.config.TimeInfo;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class TimeInfoListener implements ConfigListener<TimeInfo> {

    @Override
    public String name() {
        return "timeInfo";
    }

    @Override
    public void onUpdate(TimeInfo newTimeInfo) {
        System.out.println("timeInfo has updated to: " + newTimeInfo);
    }
}
