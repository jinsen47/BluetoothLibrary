package com.github.jinsen47.bluetoothlibrary.fragment;

import android.bluetooth.BluetoothDevice;
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
import android.widget.Toast;

import com.github.jinsen47.bluetoothlibrary.R;
import com.github.jinsen47.bluetoothlibrary.model.LogModel;
import com.github.jinsen47.bluetoothlibrary.model.TimeModel;
import com.github.jinsen47.bluetoothlibrary.util.BluetoothDeviceUtil;
import com.github.jinsen47.bluetoothlibrary.util.DeviceInfoUtils;
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
