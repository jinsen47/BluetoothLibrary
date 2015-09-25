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

    public TimeModel() {
    }

    public int getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(int searchTime) {
        this.searchTime = searchTime;
    }

    public int getConnectTime() {
        return connectTime;
    }

    public void setConnectTime(int connectTime) {
        this.connectTime = connectTime;
    }

    public int getServiceDiscoverTime() {
        return serviceDiscoverTime;
    }

    public void setServiceDiscoverTime(int serviceDiscoverTime) {
        this.serviceDiscoverTime = serviceDiscoverTime;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }
}
