package com.github.jinsen47.bluetoothlibrary.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.github.jinsen47.bluetoothlibrary.fragment.BluetoothFragment;

/**
 * Created by Jinsen on 15/9/22.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {
    private static final String[] TITLES = {"Lite BT", "Smart BT"};

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new BluetoothFragment();
                break;
            case 1:
                fragment = new BluetoothFragment();
                break;
            default:
                fragment = new BluetoothFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }
}
