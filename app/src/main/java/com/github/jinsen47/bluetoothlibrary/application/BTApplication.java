package com.github.jinsen47.bluetoothlibrary.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by Jinsen on 15/9/22.
 */
public class BTApplication extends Application {
    private static Context sContext;
    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
    }
}
