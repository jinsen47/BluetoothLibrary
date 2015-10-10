package com.github.jinsen47.bluetoothlibrary.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.github.jinsen47.bluetoothlibrary.R;
import com.github.jinsen47.bluetoothlibrary.model.LogModel;
import com.github.jinsen47.bluetoothlibrary.model.TimeModel;
import com.github.jinsen47.bluetoothlibrary.util.BluetoothDeviceUtil;
import com.github.jinsen47.bluetoothlibrary.util.LogcatUtil;
import com.litesuits.bluetooth.LiteBluetooth;
import com.litesuits.bluetooth.conn.ConnectError;
import com.litesuits.bluetooth.conn.ConnectListener;
import com.litesuits.bluetooth.conn.ConnectState;
import com.litesuits.bluetooth.scan.PeriodScanCallback;
import com.litesuits.bluetooth.utils.HexUtil;

import java.util.List;
import java.util.UUID;

/**
 * Created by Jinsen on 15/9/28.
 */
public class LiteBluetoothFragment extends BluetoothFragment {
    private static final String TAG = LiteBluetoothFragment.class.getSimpleName();
    private static final long TIME_OUT = 5000;

    private LiteBluetooth mLiteBluetooth;
    private PeriodScanCallback mScanCallback;
    private ConnectListener mConnectListener;

    private TimeModel mTimeData = new TimeModel();
    private LogModel mLogData = new LogModel();

    private TestDevice device;

    private String connectingMac;

    private final UUID service_uuid = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    private final UUID characteristic_uuid = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
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
                    connectingMac = bluetoothDevice.getAddress();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLiteBluetooth.stopScan(mScanCallback);
                            mTimeData.setSearchStopTime(System.currentTimeMillis());
                            mLiteBluetooth.connect(bluetoothDevice, true, mConnectListener);
                        }
                    });
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
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt) {
                Log.d(TAG, "Service Discovered!");
                BluetoothGattService service = gatt.getService(service_uuid);
                if (service != null) {
                    Log.d(TAG, "Enable Notifiy!");
                    BluetoothGattCharacteristic characteristic = getBluetoothGatt().getService(service_uuid).getCharacteristic(characteristic_uuid);
                    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(DESC_CCC);
                    enableCharacteristicNotification(getBluetoothGatt(), characteristic, descriptor.getUuid().toString());
                }
                mTimeData.setServiceStopTime(System.currentTimeMillis());
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
        LogcatUtil logcat = new LogcatUtil(mLogData.getLog());
        logcat.start();
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
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchDevice() {
        mLiteBluetooth.closeAllConnects();
        mTimeData.clear();
        mLiteBluetooth.startScan(mScanCallback);
        mTimeData.setSearchStartTime(System.currentTimeMillis());
        setStatusTitle(R.string.status_searching);
    }

    public static enum TestDevice {Thumb, Cadence, Meter};
}
