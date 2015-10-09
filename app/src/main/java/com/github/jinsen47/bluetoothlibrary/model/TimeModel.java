package com.github.jinsen47.bluetoothlibrary.model;

import java.io.Serializable;

/**
 * Created by Jinsen on 15/9/25.
 */
public class TimeModel implements Serializable {
    private int searchTime;
    private int connectTime;
    private int serviceDiscoverTime;
    private int retryTimes;

    private long searchStartTime;
    private long connectStartTime;
    private long serviceDiscoverStartTime;

    public TimeModel() {
    }

    public void clear() {
        searchTime = 0;
        connectTime = 0;
        serviceDiscoverTime = 0;
        retryTimes = 0;

        searchStartTime = 0;
        connectStartTime = 0;
        serviceDiscoverStartTime = 0;
    }

    public int getSearchTime() {
        return searchTime;
    }

    public void setSearchStartTime(long searchStartTime) {
        this.searchStartTime = searchStartTime;
    }

    public void setSearchStopTime(long searchStopTime) {
        this.searchTime = ((int) (searchStopTime - searchStartTime));
    }

    public void setConnectStartTime(long connectStartTime) {
        this.connectStartTime = connectStartTime;
    }

    public void setConnectStopTime(long connectStopTime) {
        this.connectTime = ((int) (connectStopTime - connectStartTime));
    }

    public int getConnectTime() {
        return connectTime;
    }

    public void setServiceStartTime(long serviceStartTime) {
        this.serviceDiscoverStartTime = serviceStartTime;
    }

    public void setServiceStopTime(long serviceStopTime) {
        this.serviceDiscoverTime = ((int) (serviceStopTime - serviceDiscoverStartTime));
    }

    public int getServiceDiscoverTime() {
        return serviceDiscoverTime;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

}
