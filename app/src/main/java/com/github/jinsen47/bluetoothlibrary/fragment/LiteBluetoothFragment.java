package com.github.jinsen47.bluetoothlibrary.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.github.jinsen47.bluetoothlibrary.R;
import com.github.jinsen47.bluetoothlibrary.adapter.BriefAdapter;
import com.github.jinsen47.bluetoothlibrary.model.CycleTestModel;
import com.github.jinsen47.bluetoothlibrary.model.LogModel;
import com.github.jinsen47.bluetoothlibrary.model.TimeModel;
import com.github.jinsen47.bluetoothlibrary.util.BluetoothDeviceUtil;
import com.github.jinsen47.bluetoothlibrary.util.DeviceInfoUtils;
import com.litesuits.bluetooth.LiteBluetooth;
import com.litesuits.bluetooth.conn.ConnectError;
import com.litesuits.bluetooth.conn.ConnectListener;
import com.litesuits.bluetooth.conn.ConnectState;
import com.litesuits.bluetooth.conn.TimeoutCallback;
import com.litesuits.bluetooth.scan.PeriodScanCallback;
import com.litesuits.bluetooth.utils.HandlerUtil;
import com.litesuits.bluetooth.utils.HexUtil;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;

/**
 * Created by Jinsen on 15/9/28.
 */
public class LiteBluetoothFragment extends BluetoothFragment {
    private static final String TAG = LiteBluetoothFragment.class.getSimpleName();
    private static final long TIME_OUT = 5000;
    private long SCAN_INTERVAL = 5000;
    private static final String DEVICE_MAC = "B0:B4:48:DB:08:54";

    private static final int CHANGE_MIN_INTERVAL = 0x05;
    private static final int CHANGE_MAX_INTERVAL = 0x06;
    private static final int CHANGE_ADV_INTERVAL = 0x09;
    private static final int CHANGE_COON_TIMEOUT = 0x08;

    private LiteBluetooth mLiteBluetooth;
    private PeriodScanCallback mScanCallback;
    private ConnectListener mConnectListener;
    private BriefAdapter.OnLaunchClickListener mLaunchClickListener;

    private TimeModel mTimeData = new TimeModel();
    private LogModel mLogData = new LogModel();
    private CycleTestModel mCycleTestData = new CycleTestModel();

    private TestDevice device;

    private String connectingMac = DEVICE_MAC;
    private boolean isTesting = false;


    private final UUID service_uuid = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    private final UUID notify_characteristic_uuid = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    private final UUID command_characteristic_uuid = UUID.fromString("0000fff3-0000-1000-8000-00805f9b34fb");
    private final UUID data_characteristic_uuid = UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb");
    public static final UUID DESC_CCC = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    @Override
    protected void initBluetooth() {
        mLiteBluetooth = new LiteBluetooth(getActivity());
        mScanCallback = new PeriodScanCallback(TIME_OUT, BluetoothAdapter.getDefaultAdapter()) {
            @Override
            public void onScanTimeout() {
                Log.d(TAG, String.format("Scan time out after %d", TIME_OUT));
            }

            @Override
            public void onLeScan(final BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
                boolean hasFindDevice = false;
                switch (device) {
                    case Thumb:
                        if (BluetoothDeviceUtil.isThumb(bytes)) {
                            Log.d(TAG, "Stop scan, find Thumb!");
                        }
                        hasFindDevice = true;
                        break;
                    case Cadence:
                        if (BluetoothDeviceUtil.isCadence(bytes)) {
                            Log.d(TAG, "Stop scan, find Cadence!");
                        }
                        hasFindDevice = true;
                        break;
                    case Meter:
                        if (BluetoothDeviceUtil.isMeter(bytes)) {
                            Log.d(TAG, "Stop scan, find Meter!");
                        }
                        hasFindDevice = true;
                        break;
                    default:
                        break;

                }
                if (hasFindDevice) {
                    if (TextUtils.isEmpty(connectingMac)) {
                        connectingMac = bluetoothDevice.getAddress();
                    }
                    if (bluetoothDevice.getAddress().equals(connectingMac)) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mScanCallback.stopScanAndNotify();
                                mTimeData.setSearchStopTime(System.currentTimeMillis());
                                mLiteBluetooth.connect(bluetoothDevice, true, mConnectListener);
                            }
                        });
                    } else {
                        // 此次扫描未发现测试设备
                    }
                }
            }
        };
        mConnectListener = new ConnectListener() {
            @Override
            public void onStateChanged(ConnectState state) {
                switch (state) {
                    case Connecting:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setStatusTitle(R.string.status_connecting);
                                mTimeData.setConnectStartTime(System.currentTimeMillis());
                            }
                        });
                        break;
                    case Connected:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setStatusTitle(R.string.status_connected);
                                mTimeData.setConnectStopTime(System.currentTimeMillis());

                                // LiteBluetooth连接超时为10s
                                mTimeData.setRetryTimes(mTimeData.getConnectTime() / 10000);

                                mTimeData.setServiceStartTime(System.currentTimeMillis());
                                mLogData.setMac(connectingMac);
                                mCycleTestData.setMac(connectingMac);
                                mTimeData.setIsConnectPassed(true);
                            }
                        });
                        break;
                    case DisConnected:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setStatusTitle(R.string.status_disconnect);
                            }
                        });
                        break;
                    default:
                        break;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDatasetChanged();
                    }
                });

            }

            @Override
            public void onFailed(ConnectError error) {
                Log.d(TAG, "Connect Fail!\t" + error.getMessage());
                setStatusTitle(R.string.status_fail);
                mTimeData.setIsConnectPassed(false);
                mTimeData.setFailMessage(error.getMessage());
                stopCurrentTest(false);
                startNextTest();
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt) {
                Log.d(TAG, "Service Discovered!");
                mTimeData.setServiceStopTime(System.currentTimeMillis());
                BluetoothGattService service = gatt.getService(service_uuid);
                if (service != null) {
                    Log.d(TAG, "Enable Notifiy!");
                    BluetoothGattCharacteristic characteristic = getBluetoothGatt().getService(service_uuid).getCharacteristic(notify_characteristic_uuid);
                    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(DESC_CCC);
                    enableCharacteristicNotification(getBluetoothGatt(), characteristic, descriptor.getUuid().toString());
                    stopCurrentTest(true);
                    startNextTest();
                } else {
                    mTimeData.setFailMessage("没有所需service");
                    mTimeData.setIsNotifyPassed(false);
                    stopCurrentTest(false);
                    startNextTest();
                }

            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                Log.d(TAG, "onCharacteristicWrite " + gatt.getDevice().getAddress());
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                String address = gatt.getDevice().getAddress();
                Log.d(TAG, "onCharacteristicChanged " + address);
                Log.d(TAG, HexUtil.encodeHexStr(characteristic.getValue()));
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorWrite(gatt, descriptor, status);
                Log.d(TAG, "onDescriptorWrite" + gatt.getDevice().getAddress());
            }


        };
        setTimeData(mTimeData);
        setLogData(mLogData);
        setCycleTestData(mCycleTestData);
        setDeviceInfo(mLogData.getLog());

        mLaunchClickListener = new BriefAdapter.OnLaunchClickListener() {
            @Override
            public void onClick(View v, String ad, String connMin, String connMax, String timeout) {
//                int adTime = Integer.parseInt(ad.trim());
                int connMinTime = Integer.parseInt(connMin.trim());
//                int connMaxTime = Integer.parseInt(connMax.trim());
//                int timeoutTime = Integer.parseInt(timeout.trim());

                if (connMinTime != 0) {
                    mConnectListener.characteristicWrite(mConnectListener.getBluetoothGatt(),
                            service_uuid.toString(),
                            data_characteristic_uuid.toString(),
                            getCharacteristicWriteByteArray(connMinTime),
                            new TimeoutCallback() {
                        @Override
                        public void onTimeout(BluetoothGatt gatt) {
                            Log.d(TAG, "write connMin data timeout");
                        }
                    });
                    mConnectListener.characteristicWrite(mConnectListener.getBluetoothGatt(),
                            service_uuid.toString(),
                            command_characteristic_uuid.toString(),
                            getCommandByteArray(CHANGE_MIN_INTERVAL),
                            new TimeoutCallback() {
                                @Override
                                public void onTimeout(BluetoothGatt gatt) {
                                    Log.d(TAG, "write connMin command timeout");
                                }
                            });
                }
            }
        };
        setLaunchClickListener(mLaunchClickListener);

    }

    private byte[] getCharacteristicWriteByteArray(int data) {
        // Designed by our hardware, characteristic
        byte[] inputData;
        if (data < 0xFF) {
            inputData = ByteBuffer.allocate(6).put(0, ((byte) data)).array();
        } else {
            inputData = ByteBuffer.allocate(6).putInt(data, 4).array();
        }
        return inputData;
    }

    private byte[] getCommandByteArray(int data) {
        byte[] ret;
        ret = ByteBuffer.allocate(1).put(0, ((byte)data)).array();
        return ret;
    }

    @Override
    protected void setListeners() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_lite_bluetooth, menu);
    }

//    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
//        super.onPrepareOptionsMenu(menu);
//        if (isTesting) {
//            menu.clear();
//        } else if (menu.hasVisibleItems()){
//            return;
//        } else {
//            menu.clear();
//            menu.add(0, R.id.action_search_thumb, 0, R.string.action_search_thumb);
//            menu.add(0, R.id.action_search_meter, 1, R.string.action_search_cadence);
//            menu.add(0, R.id.action_search_cadence, 2, R.string.action_search_meter);
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search_thumb:
                searchDevice();
                device = TestDevice.Thumb;
                break;
            case R.id.action_search_cadence:
                searchDevice();
                device = TestDevice.Cadence;
                break;
            case R.id.action_search_meter:
                searchDevice();
                device = TestDevice.Meter;
                break;
            case R.id.action_disconnect:
                mLiteBluetooth.closeAllConnects();
                setStatusTitle("");
                connectingMac = DEVICE_MAC;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchDevice() {
        Log.d(TAG, "SearchDevice is called");
        mLiteBluetooth.closeAllConnects();
        mScanCallback.stopScanAndNotify();
        isTesting = true;
        mLiteBluetooth.startScan(mScanCallback);
        mTimeData.setSearchStartTime(System.currentTimeMillis());
        setStatusTitle(R.string.status_searching);
    }

    private void stopCurrentTest(boolean isPass) {
        mCycleTestData.addTimeModel(mTimeData, isPass);
//        mTimeData = null;
    }

    private void startNextTest() {
        Log.d(TAG, "start next test");
//        mTimeData = new TimeModel();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setTimeData(mTimeData);
            }
        });
//        HandlerUtil.HANDLER.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                searchDevice();
//            }
//        }, SCAN_INTERVAL);

    }

    private void stopCycleTest() {
        isTesting = false;
        getActivity().invalidateOptionsMenu();
    }

//    private void writeParam()

    private void setDeviceInfo(StringBuffer sb) {
        sb.append(getString(R.string.log_manufacture) + DeviceInfoUtils.getManufacture() + "\n");
        sb.append(getString(R.string.log_hardware) + DeviceInfoUtils.getHardware() + "\n");
        sb.append(getString(R.string.log_system_version) + DeviceInfoUtils.getSystemAPIVersion() + "\n");
        sb.append(getString(R.string.log_api_int) + DeviceInfoUtils.getSystemAPIVersion() + "\n");
    }

    public static enum TestDevice {Thumb, Cadence, Meter}
}
