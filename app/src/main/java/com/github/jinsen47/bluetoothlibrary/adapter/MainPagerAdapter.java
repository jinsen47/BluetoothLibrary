package com.github.jinsen47.bluetoothlibrary.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.github.jinsen47.bluetoothlibrary.R;
import com.github.jinsen47.bluetoothlibrary.fragment.BluetoothFragment;
import com.github.jinsen47.bluetoothlibrary.fragment.LiteBluetoothFragment;

/**
 * Created by Jinsen on 15/9/22.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public MainPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getStringArray(R.array.bluetooth_library)[position];
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new LiteBluetoothFragment();
                break;
            case 1:
                // TODO smart bluetooth
                fragment = new LiteBluetoothFragment();
                break;
            default:
                fragment = new LiteBluetoothFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return mContext.getResources().getStringArray(R.array.bluetooth_library).length;
    }
}
