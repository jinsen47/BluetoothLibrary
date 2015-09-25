package com.github.jinsen47.bluetoothlibrary.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.jinsen47.bluetoothlibrary.R;
import com.github.jinsen47.bluetoothlibrary.adapter.BriefAdapter;

/**
 * Created by Jinsen on 15/9/22.
 */
public class BluetoothFragment extends BaseFragment {
    private RecyclerView rvBluetooth;
    private TextView tvStatus;
    private BriefAdapter mAdapter;

    @Override
    int setLayout() {
        return R.layout.fragment_bluetooth;
    }

    @Override
    void initViews(View rootView) {
        tvStatus = ((TextView) rootView.findViewById(R.id.tv_status));
        rvBluetooth = ((RecyclerView) rootView.findViewById(R.id.rv_bluetooth));

        mAdapter = new BriefAdapter(getContext());
        rvBluetooth.setLayoutManager(new LinearLayoutManager(getContext()));
        rvBluetooth.setAdapter(mAdapter);
    }

    @Override
    void setListeners() {

    }
}
