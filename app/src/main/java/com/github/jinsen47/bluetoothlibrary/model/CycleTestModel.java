package com.github.jinsen47.bluetoothlibrary.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jinsen on 15/10/12.
 */
public class CycleTestModel {
    private String mac = "";
    private int times;
    private int failTimes = 0;
    private List<TimeModel> timeModelList;
    private float connectPassRate = 0.0f;
    private float notifyPassRate = 0.0f;
    private boolean isRateValid = false;

    public CycleTestModel() {
        timeModelList = new ArrayList<>();
        times = timeModelList.size();
    }

    public void addTimeModel(TimeModel t, boolean isPass) {
        if (t == null) return;
        isRateValid = false;
        timeModelList.add(t);
        times = timeModelList.size();
        requestCaculateRate();
        if (!isPass) failTimes++;
    }

    private void requestCaculateRate() {
        if (timeModelList == null || timeModelList.isEmpty()) {
            connectPassRate = 0.0f;
            notifyPassRate = 0.0f;
            isRateValid = true;
        } else {
            int connectPassCount = 0;
            int notiryPassCount = 0;
            for (TimeModel t : timeModelList) {
                if (t.isConnectPassed()) connectPassCount++;
                if (t.isNotifyPassed()) notiryPassCount++;
            }
            connectPassRate = connectPassCount / times;
            notifyPassRate = notiryPassCount / times;
            isRateValid = true;
        }
    }

    public float getConnectPassRate() {
        if (!isRateValid) requestCaculateRate();
        return connectPassRate;
    }

    public int getConnectPassPercent() {
        return ((int) (connectPassRate * 100));
    }

    public float getNotifyPassRate() {
        if (!isRateValid) requestCaculateRate();
        return notifyPassRate;
    }

    public int getNotifyPassPercent() {
        return ((int) (notifyPassRate * 100));
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getTimes() {
        return times;
    }
}
