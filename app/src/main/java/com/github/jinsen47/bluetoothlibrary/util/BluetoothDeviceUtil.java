package com.github.jinsen47.bluetoothlibrary.util;

import java.nio.ByteBuffer;
import java.util.IllegalFormatException;
import java.util.UUID;

/**
 * Created by Jinsen on 15/10/9.
 */
public class BluetoothDeviceUtil {
    public static final UUID service_uuid = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    public static final UUID notify_characteristic_uuid = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    public static final UUID battery_characteristic_uuid = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");
    public static final UUID command_characteristic_uuid = UUID.fromString("0000fff3-0000-1000-8000-00805f9b34fb");
    public static final UUID data_characteristic_uuid = UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb");
    public static final UUID inf_characteristic_uuid = UUID.fromString("0000fff5-0000-1000-8000-00805f9b34fb");
    public static final UUID DESC_CCC = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public static final int DEVICE_RESET = 0x03;
    public static final int OFFLINE_MODE = 0x04;
    public static final int GET_OFFLINE_DATA = 0x0B;
    public static final int GET_STORED_DATA_NUM = 0x10;
    public static final int CLEAR_OFFLINE_MODE_FLAG = 0x13;
    public static final int CHANGE_MIN_INTERVAL = 0x05;
    public static final int CHANGE_MAX_INTERVAL = 0x06;
    public static final int CHANGE_ADV_INTERVAL = 0x09;
    public static final int CHANGE_COON_TIMEOUT = 0x08;

    public enum TestDevice {Thumb, Cadence, Meter}


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


    /**
     * 我们的设备为了防止随意修改characteristic的值, 自动把值补成6字节, 数据从0字节开始, 后边补零
     * @param data
     * @return
     */
    public static byte[] getCharacteristicWriteByteArray(int data) {
        // Designed by our hardware, characteristic
        short inputShort = ((short) data);
        byte[] inputData = ByteBuffer.allocate(6).putShort(inputShort).array();
        return inputData;
    }

    public static byte[] getCommandByteArray(int data) {
        byte[] ret;
        ret = ByteBuffer.allocate(1).put(0, ((byte)data)).array();
        return ret;
    }

    /**
     * 我们的设备返回一个6字节的byte[], 其中前2字节表示总数据数目
     * @param oriData
     * @return
     */
    public static int getOffHostDataNumber(byte[] oriData) throws Exception {
        if (oriData.length < 2) throw new Exception("the length of data must be 6");
        ByteBuffer wrapped = ByteBuffer.wrap(oriData, 0, 2);
        return wrapped.getShort();
    }
}
