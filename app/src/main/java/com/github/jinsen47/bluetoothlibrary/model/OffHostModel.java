package com.github.jinsen47.bluetoothlibrary.model;

/**
 * Created by Jinsen on 15/11/17.
 */
public class OffHostModel {
    private byte[] data;
    private String distance;

    public OffHostModel() {
        distance = "";
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
