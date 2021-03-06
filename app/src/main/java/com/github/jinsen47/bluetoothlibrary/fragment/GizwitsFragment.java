package com.github.jinsen47.bluetoothlibrary.fragment;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.jinsen47.bluetoothlibrary.R;
import com.github.jinsen47.bluetoothlibrary.adapter.BriefAdapter;
import com.github.jinsen47.bluetoothlibrary.model.LogModel;
import com.github.jinsen47.bluetoothlibrary.model.TimeModel;
import com.github.jinsen47.bluetoothlibrary.util.BluetoothDeviceUtil;
import com.github.jinsen47.bluetoothlibrary.util.DeviceInfoUtils;
import com.litesuits.bluetooth.utils.HexUtil;
import com.xtremeprog.sdk.ble.BleGattCharacteristic;
import com.xtremeprog.sdk.ble.BleGattService;
import com.xtremeprog.sdk.ble.BleService;
import com.xtremeprog.sdk.ble.IBle;

/**
 * Created by Jinsen on 15/10/29.
 */
public class GizwitsFragment extends BluetoothFragment {
    private static final String TAG = GizwitsFragment.class.getSimpleName();
    private BleService mService;
    private IBle mBle;
    private Handler mHandler;

    private BluetoothDeviceUtil.TestDevice device;

    private static final String DEVICE_MAC = "B0:B4:48:DB:08:54";
    private String connectingMac = DEVICE_MAC;

    private TimeModel mTimeData = new TimeModel();
    private LogModel mLogData = new LogModel();

    private BriefAdapter.OnLaunchClickListener mLaunchClickListener;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((BleService.LocalBinder) service).getService();
            mBle = mService.getBle();
            if (mBle != null && !mBle.adapterEnabled()) {
                // TODO: enable adapter
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    @Override
    protected void initBluetooth() {
        Intent bindIntent = new Intent(getActivity(), BleService.class);
        getActivity().bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        mHandler = new Handler();

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

                if (adTime != 0) {
                    changeCharacteristicFlow(adTime, BluetoothDeviceUtil.CHANGE_ADV_INTERVAL);
                }

                if (connMinTime != 0) {
                    changeCharacteristicFlow(connMinTime, BluetoothDeviceUtil.CHANGE_MIN_INTERVAL);
                }

                if (connMaxTime != 0) {
                    changeCharacteristicFlow(connMaxTime, BluetoothDeviceUtil.CHANGE_MAX_INTERVAL);
                }

                if (timeoutTime != 0) {
                    changeCharacteristicFlow(timeoutTime, BluetoothDeviceUtil.CHANGE_COON_TIMEOUT);
                }
            }
        };
        setLaunchClickListener(mLaunchClickListener);
    }

    private void changeCharacteristicFlow(int data, int command) {
        BleGattCharacteristic dataChar = mBle.getService(connectingMac, BluetoothDeviceUtil.service_uuid).getCharacteristic(BluetoothDeviceUtil.data_characteristic_uuid);
        BleGattCharacteristic commandChar = mBle.getService(connectingMac, BluetoothDeviceUtil.service_uuid).getCharacteristic(BluetoothDeviceUtil.command_characteristic_uuid);
        BleGattCharacteristic infChar = mBle.getService(connectingMac, BluetoothDeviceUtil.service_uuid).getCharacteristic(BluetoothDeviceUtil.inf_characteristic_uuid);

        dataChar.setValue(BluetoothDeviceUtil.getCharacteristicWriteByteArray(data));
        commandChar.setValue(BluetoothDeviceUtil.getCommandByteArray(command));
        mBle.requestWriteCharacteristic(connectingMac, dataChar, "");
        mBle.requestWriteCharacteristic(connectingMac, commandChar, "");
        mBle.requestReadCharacteristic(connectingMac, infChar);

    }

    @Override
    protected void setListeners() {

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mBleReceiver, BleService.getIntentFilter());
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mBleReceiver);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_gizwits, menu);
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
                // TODO disconnect
                setStatusTitle("");
                mBle.disconnect(connectingMac);
                connectingMac = DEVICE_MAC;
                break;
            case R.id.action_reset:
                if (getStatusTitle().equals(getString(R.string.status_connected))) {
                    changeCharacteristicFlow(0, BluetoothDeviceUtil.DEVICE_RESET);
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchDevice() {
        mBle.startScan();
        mTimeData.setSearchStartTime(System.currentTimeMillis());
        setStatusTitle(R.string.status_searching);
    }

    private final BroadcastReceiver mBleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BleService.BLE_NOT_SUPPORTED.equals(action)) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "并不支持BLE", Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }
                });
            } else if (BleService.BLE_DEVICE_FOUND.equals(action)) {
                boolean hasFindDevice = false;
                BluetoothDevice bluetoothDevice = intent.getExtras().getParcelable(BleService.EXTRA_DEVICE);
                byte[] bytes = intent.getExtras().getByteArray(BleService.EXTRA_SCAN_RECORD);
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
                                mBle.stopScan();
                                mTimeData.setSearchStopTime(System.currentTimeMillis());
                                mBle.requestConnect(connectingMac);
                                setStatusTitle(R.string.status_connecting);
                                mTimeData.setConnectStartTime(System.currentTimeMillis());
                            }
                        });
                    } else {
                        // 此次扫描未发现测试设备
                    }
                }
            } else if (BleService.BLE_GATT_CONNECTED.equals(intent.getAction())) {
                setStatusTitle(R.string.status_connected);
                mTimeData.setConnectStopTime(System.currentTimeMillis());

                mTimeData.setServiceStartTime(System.currentTimeMillis());
                mLogData.setMac(connectingMac);
                mTimeData.setIsConnectPassed(true);
                mBle.discoverServices(connectingMac);
            } else if (BleService.BLE_GATT_DISCONNECTED.equals(intent.getAction())) {
                setStatusTitle(R.string.status_disconnect);
            } else if (BleService.BLE_SERVICE_DISCOVERED.equals(intent.getAction())) {
                mTimeData.setServiceStopTime(System.currentTimeMillis());
                BleGattService service = mBle.getService(connectingMac, BluetoothDeviceUtil.service_uuid);
                if (service != null) {
//                    Log.d(TAG, "Enable Notifiy!");
//                    BluetoothGattCharacteristic characteristic = getBluetoothGatt().getService(BluetoothDeviceUtil.service_uuid).getCharacteristic(BluetoothDeviceUtil.notify_characteristic_uuid);
//                    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(BluetoothDeviceUtil.DESC_CCC);
//                    enableCharacteristicNotification(getBluetoothGatt(), characteristic, descriptor.getUuid().toString());
                } else {
                    mTimeData.setFailMessage("没有所需service");
                    mTimeData.setIsNotifyPassed(false);
                }
            } else if (BleService.BLE_CHARACTERISTIC_WRITE.equals(intent.getAction())) {
                // TODO onCharacteristicWrite
            } else if (BleService.BLE_CHARACTERISTIC_READ.equals(intent.getAction())) {
                // TODO onCharacteristicRead
                byte[] val = intent.getExtras().getByteArray(BleService.EXTRA_VALUE);
                Log.d(TAG, "inf read " + HexUtil.encodeHexStr(val));
                if (HexUtil.encodeHexStr(val).equals("02")) {
                    Toast.makeText(getActivity(), "操作成功!", Toast.LENGTH_SHORT).show();
                }

            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDatasetChanged();
                }
            });
        }
    };

    private void setDeviceInfo(StringBuffer sb) {
        sb.append(getString(R.string.log_manufacture) + DeviceInfoUtils.getManufacture() + "\n");
        sb.append(getString(R.string.log_hardware) + DeviceInfoUtils.getHardware() + "\n");
        sb.append(getString(R.string.log_system_version) + DeviceInfoUtils.getSystemAPIVersion() + "\n");
        sb.append(getString(R.string.log_api_int) + DeviceInfoUtils.getSystemAPIVersion() + "\n");
    }
}
