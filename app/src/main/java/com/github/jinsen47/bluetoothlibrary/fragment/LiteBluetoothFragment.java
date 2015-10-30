package com.github.jinsen47.bluetoothlibrary.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.BluetoothLeScanner;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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


    private LiteBluetooth mLiteBluetooth;
    private PeriodScanCallback mScanCallback;
    private ConnectListener mConnectListener;
    private BriefAdapter.OnLaunchClickListener mLaunchClickListener;

    private TimeModel mTimeData = new TimeModel();
    private LogModel mLogData = new LogModel();

    private BluetoothDeviceUtil.TestDevice device;

    private String connectingMac = DEVICE_MAC;
    private boolean isTesting = false;

    private DeviceCommandStatus mCommandStatus = DeviceCommandStatus.STAND_BY;
    private int mCurrentCommand;
    private int mCurrentData;

    @Override
    protected void initBluetooth() {
        mLiteBluetooth = new LiteBluetooth(getActivity());
        mLiteBluetooth.enableBluetooth();
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
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt) {
                Log.d(TAG, "Service Discovered!");
                mTimeData.setServiceStopTime(System.currentTimeMillis());
                BluetoothGattService service = gatt.getService(BluetoothDeviceUtil.service_uuid);
                if (service != null) {
//                    Log.d(TAG, "Enable Notifiy!");
//                    BluetoothGattCharacteristic characteristic = getBluetoothGatt().getService(BluetoothDeviceUtil.service_uuid).getCharacteristic(BluetoothDeviceUtil.notify_characteristic_uuid);
//                    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(BluetoothDeviceUtil.DESC_CCC);
//                    enableCharacteristicNotification(getBluetoothGatt(), characteristic, descriptor.getUuid().toString());
                } else {
                    mTimeData.setFailMessage("没有所需service");
                    mTimeData.setIsNotifyPassed(false);
                }

            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                Log.i(TAG, "onCharacteristicRead" + HexUtil.encodeHexStr(characteristic.getValue()));
                if (characteristic.getUuid().equals(BluetoothDeviceUtil.inf_characteristic_uuid)) {
                    Toast.makeText(getActivity(), "操作成功", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                Log.d(TAG, "onCharacteristicWrite " + gatt.getDevice().getAddress());

                if (mCommandStatus == DeviceCommandStatus.WRITE_DATA) {
                    writeCommandToDevice();
                    mCommandStatus = DeviceCommandStatus.WRITE_COMMAND;
                }

                if (mCommandStatus == DeviceCommandStatus.WRITE_COMMAND) {
                    BluetoothGattCharacteristic c = getCharacteristic(getBluetoothGatt(), BluetoothDeviceUtil.service_uuid.toString(),
                            BluetoothDeviceUtil.inf_characteristic_uuid.toString());
                    boolean ret = getBluetoothGatt().readCharacteristic(c);
                    Log.d(TAG, "info " + (ret ? "true" : "false"));
                    mCommandStatus = DeviceCommandStatus.READ_INFO;
                }
            }


            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                String address = gatt.getDevice().getAddress();
//                Log.d(TAG, "onCharacteristicChanged " + address);
//                Log.d(TAG, HexUtil.encodeHexStr(characteristic.getValue()));
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorWrite(gatt, descriptor, status);
                Log.d(TAG, "onDescriptorWrite" + gatt.getDevice().getAddress());
            }



        };
        setTimeData(mTimeData);
        setLogData(mLogData);
        setDeviceInfo(mLogData.getLog());

         mLaunchClickListener = new BriefAdapter.OnLaunchClickListener() {
            @Override
            public void onClick(View v, String ad, String connMin, String connMax, String timeout) {
                int adTime = 0;
                int connMinTime = 0;
                int connMaxTime = 0;
                int timeoutTime = 0;

                if (!TextUtils.isEmpty(ad.trim())) adTime = Integer.parseInt(ad.trim());
                if (!TextUtils.isEmpty(connMin.trim())) connMinTime = Integer.parseInt(connMin.trim());
                if (!TextUtils.isEmpty(connMax.trim())) connMaxTime = Integer.parseInt(connMax.trim());
                if (!TextUtils.isEmpty(timeout.trim())) timeoutTime = Integer.parseInt(timeout.trim());

                if (connMinTime != 0) {
                    mCurrentData = connMinTime;
                    mCurrentCommand = BluetoothDeviceUtil.CHANGE_MIN_INTERVAL;
                    writeDataToDevice();
                    mCommandStatus = DeviceCommandStatus.WRITE_DATA;
                }
                BluetoothGattCharacteristic c = mConnectListener.getCharacteristic(mConnectListener.getBluetoothGatt(), BluetoothDeviceUtil.service_uuid.toString(),
                        BluetoothDeviceUtil.battery_characteristic_uuid.toString());
                boolean ret = mConnectListener.getBluetoothGatt().readCharacteristic(c);
                Log.i(TAG, "battery level " + (ret ? "true" : "false"));
            }
        };
        setLaunchClickListener(mLaunchClickListener);

    }

    private void writeDataToDevice() {
        mConnectListener.characteristicWrite(mConnectListener.getBluetoothGatt(),
                BluetoothDeviceUtil.service_uuid.toString(),
                BluetoothDeviceUtil.data_characteristic_uuid.toString(),
                BluetoothDeviceUtil.getCharacteristicWriteByteArray(mCurrentData),
                new TimeoutCallback() {
                    @Override
                    public void onTimeout(BluetoothGatt gatt) {
                        Log.d(TAG, "write data time out :" + mCurrentData);
                    }
                });
    }

    private void writeCommandToDevice() {
        mConnectListener.characteristicWrite(mConnectListener.getBluetoothGatt(),
                BluetoothDeviceUtil.service_uuid.toString(),
                BluetoothDeviceUtil.command_characteristic_uuid.toString(),
                BluetoothDeviceUtil.getCommandByteArray(mCurrentCommand),
                new TimeoutCallback() {
                    @Override
                    public void onTimeout(BluetoothGatt gatt) {
                    }
                });
    }

    @Override
    protected void setListeners() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_lite_bluetooth, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search_thumb:
                device = BluetoothDeviceUtil.TestDevice.Thumb;
                searchDevice();
                break;
            case R.id.action_search_cadence:
                device = BluetoothDeviceUtil.TestDevice.Cadence;
                searchDevice();
                break;
            case R.id.action_search_meter:
                device = BluetoothDeviceUtil.TestDevice.Meter;
                searchDevice();
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

    private void setDeviceInfo(StringBuffer sb) {
        sb.append(getString(R.string.log_manufacture) + DeviceInfoUtils.getManufacture() + "\n");
        sb.append(getString(R.string.log_hardware) + DeviceInfoUtils.getHardware() + "\n");
        sb.append(getString(R.string.log_system_version) + DeviceInfoUtils.getSystemAPIVersion() + "\n");
        sb.append(getString(R.string.log_api_int) + DeviceInfoUtils.getSystemAPIVersion() + "\n");
    }


    public enum DeviceCommandStatus {
        STAND_BY,
        WRITE_DATA,
        WRITE_COMMAND,
        READ_INFO
    }
}
