package com.github.jinsen47.bluetoothlibrary.util;

/**
 * Created by Jinsen on 15/10/9.
 */
public class BluetoothDeviceUtil {
    public static boolean isThumb(byte[] scanRecord) {
        if((scanRecord[8]==17 && scanRecord[9]==18 && scanRecord[10]==3)
                || (scanRecord[11]==17 && scanRecord[12]==18 && scanRecord[13]==3)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isCadence(byte[] scanRecord) {
        if ((scanRecord[8]==17 && scanRecord[9]==18 && scanRecord[10]==2)
                || (scanRecord[11]==17 && scanRecord[12]==18 && scanRecord[13]==2)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isMeter(byte[] scanRecord) {
        if((scanRecord[8]==17 && scanRecord[9]==18 && scanRecord[10]==1)
                || (scanRecord[11]==17 && scanRecord[12]==18 && scanRecord[13]==1)){
            return true;
        } else {
            return false;
        }
    }
}
