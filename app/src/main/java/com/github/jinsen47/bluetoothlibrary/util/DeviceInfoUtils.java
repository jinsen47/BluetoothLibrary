package com.github.jinsen47.bluetoothlibrary.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

/**
 * Created by Jinsen on 15/10/16.
 */
public class DeviceInfoUtils {
    private static volatile Context context;
    private DeviceInfoUtils() throws IllegalAccessException {
        throw new IllegalAccessException("static class");
    }
    public static void setContext(final Context context) {
        DeviceInfoUtils.context = context;
    }
    public static Activity context() {
        if (context == null)
            throw new IllegalStateException("'context' must not be null. Please init Device.setContext().");
        return ((Activity) context);
    }

    public static String getManufacture() {
        return Build.MANUFACTURER;
    }

    public static String getHardware() {
        return Build.HARDWARE;
    }

    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    public static String getSystemAPIVersion() {
        return Build.VERSION.SDK_INT + "";
    }

}
