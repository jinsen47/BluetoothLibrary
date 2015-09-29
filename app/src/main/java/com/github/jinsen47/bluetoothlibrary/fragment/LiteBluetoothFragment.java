package com.github.jinsen47.bluetoothlibrary.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.github.jinsen47.bluetoothlibrary.R;
import com.github.jinsen47.bluetoothlibrary.model.TimeModel;
import com.litesuits.bluetooth.LiteBluetooth;
import com.litesuits.bluetooth.conn.ConnectError;
import com.litesuits.bluetooth.conn.ConnectListener;
import com.litesuits.bluetooth.conn.ConnectState;
import com.litesuits.bluetooth.scan.PeriodScanCallback;

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
                byte[] scanRecord = bytes;
                if((scanRecord[8]==17 && scanRecord[9]==18 && scanRecord[10]==3)
                        || (scanRecord[11]==17 && scanRecord[12]==18 && scanRecord[13]==3)){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLiteBluetooth.stopScan(mScanCallback);
                            Log.d(TAG, "Stop scan, find Thumb!");
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
                                mTimeData.setServiceStartTime(System.currentTimeMillis());
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
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt) {
                Log.d(TAG, "Service Discovered!");
                mTimeData.setServiceStopTime(System.currentTimeMillis());
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

            }
        };
        setTimeData(mTimeData);

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
                // TODO search thumb
                mLiteBluetooth.startScan(mScanCallback);
                mTimeData.setSearchStartTime(System.currentTimeMillis());
                setStatusTitle(R.string.status_searching);
                break;
            case R.id.action_search_cadence:
                // TODO search cadence
                break;
            case R.id.action_search_meter:
                // TODO search meter
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
