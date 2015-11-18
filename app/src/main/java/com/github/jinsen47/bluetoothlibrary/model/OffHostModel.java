package com.github.jinsen47.bluetoothlibrary.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jinsen on 15/11/17.
 */
public class OffHostModel {
    private List<Byte> data;
    private String distance;
    private int count;

    public OffHostModel() {
        distance = "";
        data = new ArrayList<>();
    }

    public List<Byte> getData() {
        return data;
    }

    public void setData(List<Byte> data) {
        this.data = data;
    }

    public void appendData(byte[] newData) {
        if (data == null) return;
        if (newData.length == 0) return;

        for (byte b : newData) {
            data.add(b);
        }
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
