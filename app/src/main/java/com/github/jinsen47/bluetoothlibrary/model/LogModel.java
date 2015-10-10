package com.github.jinsen47.bluetoothlibrary.model;

/**
 * Created by Jinsen on 15/10/10.
 */
public class LogModel {
    private String mac;
    private StringBuffer log;

    public LogModel() {
        mac = "";
        log = new StringBuffer();
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public StringBuffer getLog() {
        return log;
    }

    public void setLog(StringBuffer log) {
        this.log = log;
    }
}
