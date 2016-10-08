package me.hao0.diablo.client.config;

import me.hao0.diablo.client.DiabloConfig;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Component
public class MyAppConfig implements DiabloConfig {

    private String activityNo;

    private Integer activityChannel;

    private Boolean activityStart;

    private Float activityRatio;

    private Long activityCount;

    private Double activityFee;

    // {"startDate":"2016-02-13","endDate":"2016-03-24"}
    private TimeInfo timeInfo;

    // [{"startDate":"2016-01-01","endDate":"2016-02-01"},{"startDate":"2016-05-01","endDate":"2016-08-01"}]
    private List<TimeInfo> timeInfos;

    // {"second":{"startDate":"2016-05-01","endDate":"2016-08-01"},"first":{"startDate":"2016-01-01","endDate":"2016-02-01"}}
    private Map<String, TimeInfo> timeInfoMap;

    public String getActivityNo() {
        return activityNo;
    }

    public void setActivityNo(String activityNo) {
        this.activityNo = activityNo;
    }

    public Integer getActivityChannel() {
        return activityChannel;
    }

    public void setActivityChannel(Integer activityChannel) {
        this.activityChannel = activityChannel;
    }

    public Boolean getStart() {
        return activityStart;
    }

    public void setStart(Boolean start) {
        activityStart = start;
    }

    public Boolean getActivityStart() {
        return activityStart;
    }

    public void setActivityStart(Boolean activityStart) {
        this.activityStart = activityStart;
    }

    public TimeInfo getTimeInfo() {
        return timeInfo;
    }

    public void setTimeInfo(TimeInfo timeInfo) {
        this.timeInfo = timeInfo;
    }

    public Float getActivityRatio() {
        return activityRatio;
    }

    public void setActivityRatio(Float activityRatio) {
        this.activityRatio = activityRatio;
    }

    public Long getActivityCount() {
        return activityCount;
    }

    public void setActivityCount(Long activityCount) {
        this.activityCount = activityCount;
    }

    public Double getActivityFee() {
        return activityFee;
    }

    public void setActivityFee(Double activityFee) {
        this.activityFee = activityFee;
    }

    public List<TimeInfo> getTimeInfos() {
        return timeInfos;
    }

    public void setTimeInfos(List<TimeInfo> timeInfos) {
        this.timeInfos = timeInfos;
    }

    public Map<String, TimeInfo> getTimeInfoMap() {
        return timeInfoMap;
    }

    public void setTimeInfoMap(Map<String, TimeInfo> timeInfoMap) {
        this.timeInfoMap = timeInfoMap;
    }

    @Override
    public String toString() {
        return "MyAppConfig{" +
                "activityNo='" + activityNo + '\'' +
                ", activityChannel=" + activityChannel +
                ", activityStart=" + activityStart +
                ", activityRatio=" + activityRatio +
                ", activityCount=" + activityCount +
                ", activityFee=" + activityFee +
                ", timeInfo=" + timeInfo +
                ", timeInfos=" + timeInfos +
                ", timeInfoMap=" + timeInfoMap +
                '}';
    }
}
