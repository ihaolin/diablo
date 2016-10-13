package me.hao0.diablo.client.listener;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class ActivityNoListener implements ConfigListener<String> {

    @Override
    public String name() {
        return "activityNo";
    }

    @Override
    public void onUpdate(String newActivityNo) {
        System.out.println("activityNo has updated to: " + newActivityNo);
    }
}
