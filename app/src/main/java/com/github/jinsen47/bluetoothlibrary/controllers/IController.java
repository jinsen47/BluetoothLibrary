package com.github.jinsen47.bluetoothlibrary.controllers;

/**
 * Created by Jinsen on 15/9/25.
 */
public interface IController {
    /**
     * Life cycle method
     */
    public void onStart();
    public void onStop();

    /**
     * Bluetooth method
     */
    public void startScan();
    public void stopScan();
    public void connect(String mac);
    public void disconnect(String mac);
}
