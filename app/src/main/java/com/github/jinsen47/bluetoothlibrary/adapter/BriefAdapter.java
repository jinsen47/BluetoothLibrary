package com.github.jinsen47.bluetoothlibrary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.jinsen47.bluetoothlibrary.R;
import com.github.jinsen47.bluetoothlibrary.model.CycleTestModel;
import com.github.jinsen47.bluetoothlibrary.model.LogModel;
import com.github.jinsen47.bluetoothlibrary.model.TimeModel;

/**
 * Created by Jinsen on 15/9/25.
 */
public class BriefAdapter extends RecyclerView.Adapter{
    public enum Mode {Time, CycleTest, Log}

    private final Context mContext;
    private TimeModel timeData;
    private LogModel logData;
    private CycleTestModel cycleTestModel;

    public BriefAdapter(Context mContext) {
        this.mContext = mContext;
        timeData = new TimeModel();
        logData = new LogModel();
        cycleTestModel = new CycleTestModel();
    }

    public TimeModel getTimeData() {
        return timeData;
    }

    public void setTimeData(TimeModel timeData) {
        this.timeData = timeData;
    }

    public LogModel getLogData() {
        return logData;
    }

    public void setLogData(LogModel logData) {
        this.logData = logData;
    }

    public CycleTestModel getCycleTestModel() {
        return cycleTestModel;
    }

    public void setCycleTestModel(CycleTestModel cycleTestModel) {
        this.cycleTestModel = cycleTestModel;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Mode.Time.ordinal()) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.item_time, parent, false);
            return new TimeHolder(v);
        }

        if (viewType == Mode.CycleTest.ordinal()) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.item_cycletest, parent, false);
            return new CycleTestHolder(v);
        }

        if (viewType == Mode.Log.ordinal()) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.item_log, parent, false);
            return new LogHolder(v);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == Mode.Time.ordinal()) {
            TimeHolder timeHolder = ((TimeHolder) holder);
            timeHolder.tvConnectTime.setText(String.format(mContext.getString(R.string.time_connecting), timeData.getConnectTime()));
            timeHolder.tvSearchTime.setText(String.format(mContext.getString(R.string.time_search), timeData.getSearchTime()));
            timeHolder.tvServiceDiscoverTime.setText(String.format(mContext.getString(R.string.time_service_discovering), timeData.getServiceDiscoverTime()));
            timeHolder.tvRetryTimes.setText(String.format(mContext.getString(R.string.time_retry), timeData.getRetryTimes()));
        }

        if (position == Mode.CycleTest.ordinal()) {
            CycleTestHolder cycleTestHolder = ((CycleTestHolder) holder);
            cycleTestHolder.tvMac.setText(cycleTestModel.getMac());
            cycleTestHolder.tvTestTimes.setText(String.format(mContext.getString(R.string.cycle_test_times), cycleTestModel.getTimes()));
            cycleTestHolder.tvFailTimes.setText("");
            cycleTestHolder.tvConnectPassRate.setText(String.format(mContext.getString(R.string.cycle_test_connect_success_rate), cycleTestModel.getConnectPassPercent()));
            cycleTestHolder.tvNotifyPassRate.setText(String.format(mContext.getString(R.string.cycle_test_notify_success_rate), cycleTestModel.getNotifyPassPercent()));
        }

        if (position == Mode.Log.ordinal()) {
            LogHolder logHolder = ((LogHolder) holder);
            logHolder.tvMac.setText(logData.getMac());
            logHolder.tvLog.setText(logData.getLog().toString());
        }
    }

    @Override
    public int getItemCount() {
        int i = Mode.values().length;
        return Mode.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        int i  = Mode.values()[position].ordinal();
        return Mode.values()[position].ordinal();
    }

    public class TimeHolder extends RecyclerView.ViewHolder {
        public TextView tvSearchTime;
        public TextView tvConnectTime;
        public TextView tvServiceDiscoverTime;
        public TextView tvRetryTimes;

        public TimeHolder(View itemView) {
            super(itemView);
            tvSearchTime = ((TextView) itemView.findViewById(R.id.tv_search_time));
            tvConnectTime = ((TextView) itemView.findViewById(R.id.tv_connect_time));
            tvServiceDiscoverTime = ((TextView) itemView.findViewById(R.id.tv_service_discover_time));
            tvRetryTimes = ((TextView) itemView.findViewById(R.id.tv_retry_time));
        }
    }

    public class CycleTestHolder extends RecyclerView.ViewHolder {
        public TextView tvMac;
        public TextView tvTestTimes;
        public TextView tvFailTimes;
        public TextView tvConnectPassRate;
        public TextView tvNotifyPassRate;
        public CycleTestHolder(View itemView) {
            super(itemView);
            tvMac = ((TextView) itemView.findViewById(R.id.text_mac));
            tvTestTimes = ((TextView) itemView.findViewById(R.id.text_test_times));
            tvFailTimes = ((TextView) itemView.findViewById(R.id.text_fail_times));
            tvConnectPassRate = ((TextView) itemView.findViewById(R.id.text_connect_pass_rate));
            tvNotifyPassRate = ((TextView) itemView.findViewById(R.id.text_notify_pass_rate));
        }
    }

    public class LogHolder extends RecyclerView.ViewHolder {
        public TextView tvMac;
        public TextView tvLog;

        public LogHolder(View itemView) {
            super(itemView);
            tvMac = ((TextView) itemView.findViewById(R.id.tv_mac));
            tvLog = ((TextView) itemView.findViewById(R.id.tv_log));
        }
    }
}
