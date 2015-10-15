package com.github.jinsen47.bluetoothlibrary.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.jinsen47.bluetoothlibrary.R;
import com.github.jinsen47.bluetoothlibrary.adapter.BriefAdapter;
import com.github.jinsen47.bluetoothlibrary.model.CycleTestModel;
import com.github.jinsen47.bluetoothlibrary.model.LogModel;
import com.github.jinsen47.bluetoothlibrary.model.TimeModel;

/**
 * Created by Jinsen on 15/9/22.
 */
public abstract class BluetoothFragment extends BaseFragment {
    private RecyclerView rvBluetooth;
    private TextView tvStatus;
    private BriefAdapter mAdapter;

    @Override
    protected int setLayout() {
        return R.layout.fragment_bluetooth;
    }

    @Override
    protected void initViews(View rootView) {
        tvStatus = ((TextView) rootView.findViewById(R.id.tv_status));
        rvBluetooth = ((RecyclerView) rootView.findViewById(R.id.rv_bluetooth));

        mAdapter = new BriefAdapter(getContext());
        rvBluetooth.setLayoutManager(new LinearLayoutManager(getContext()));
        rvBluetooth.setAdapter(mAdapter);
        initBluetooth();
    }

    protected abstract void initBluetooth();

    protected void setStatusTitle(String s) {
        tvStatus.setText(s);
    }

    protected void setStatusTitle(int resId) {
        tvStatus.setText(resId);
    }

    protected void setTimeData(TimeModel data) {
        mAdapter.setTimeData(data);
        mAdapter.notifyDataSetChanged();
    }

    protected void setLogData(LogModel data) {
        mAdapter.setLogData(data);
        mAdapter.notifyDataSetChanged();
    }

    protected void setCycleTestData(CycleTestModel data) {
        mAdapter.setCycleTestModel(data);
        mAdapter.notifyDataSetChanged();
    }

    protected void notifyDatasetChanged() {
        mAdapter.notifyDataSetChanged();
    }
}