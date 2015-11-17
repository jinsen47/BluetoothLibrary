package com.github.jinsen47.bluetoothlibrary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.jinsen47.bluetoothlibrary.R;
import com.github.jinsen47.bluetoothlibrary.model.CycleTestModel;
import com.github.jinsen47.bluetoothlibrary.model.LogModel;
import com.github.jinsen47.bluetoothlibrary.model.OffHostModel;
import com.github.jinsen47.bluetoothlibrary.model.TimeModel;

/**
 * Created by Jinsen on 15/9/25.
 */
public class BriefAdapter extends RecyclerView.Adapter{
    public enum Mode {Time, Log, Params, OffHost}

    private final Context mContext;
    private TimeModel timeData;
    private LogModel logData;
    private OffHostModel offHostData;

    private OnLaunchClickListener mListener;
    private OnOffHostClickListener mOffHostListener;

    public BriefAdapter(Context mContext) {
        this.mContext = mContext;
        timeData = new TimeModel();
        logData = new LogModel();
        offHostData = new OffHostModel();
    }

    public void setOnLaunchClickListener(OnLaunchClickListener mListener) {
        this.mListener = mListener;
    }

    public void setOnOffHostClickListener(OnOffHostClickListener listener) {
        this.mOffHostListener = listener;
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

    public OffHostModel getOffHostData() {
        return offHostData;
    }

    public void setOffHostData(OffHostModel offHostData) {
        this.offHostData = offHostData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Mode.Time.ordinal()) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.item_time, parent, false);
//            setLayoutParamListener(v);
            return new TimeHolder(v);
        }

        if (viewType == Mode.Log.ordinal()) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.item_log, parent, false);
//            setLayoutParamListener(v);
            return new LogHolder(v);
        }

        if (viewType == Mode.Params.ordinal()) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.item_prams, parent, false);
//            setLayoutParamListener(v);
            return new ParamsHolder(v);
        }

        if (viewType == Mode.OffHost.ordinal()) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.item_off_host, parent, false);
            return new OffHostHolder(v);
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

        if (position == Mode.Log.ordinal()) {
            LogHolder logHolder = ((LogHolder) holder);
            logHolder.tvMac.setText(logData.getMac());
            logHolder.tvLog.setText(logData.getLog().toString());
        }

        if (position == Mode.Params.ordinal()) {
            ParamsHolder paramsHolder = ((ParamsHolder) holder);
            paramsHolder.setLaunchClickListener(mListener);
        }

        if (position == Mode.OffHost.ordinal()) {
            OffHostHolder offHostHolder = ((OffHostHolder) holder);
            offHostHolder.textDistance.setText(mContext.getText(R.string.off_host_distance_title) + offHostData.getDistance());
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

    public class LogHolder extends RecyclerView.ViewHolder {
        public TextView tvMac;
        public TextView tvLog;

        public LogHolder(View itemView) {
            super(itemView);
            tvMac = ((TextView) itemView.findViewById(R.id.tv_mac));
            tvLog = ((TextView) itemView.findViewById(R.id.tv_log));
        }
    }

    public class ParamsHolder extends RecyclerView.ViewHolder {
        public EditText editAd;
        public EditText editConnMin;
        public EditText editConnMax;
        public EditText editTimeout;
        public Button btnLaunch;
        public OnLaunchClickListener mListener;


        public ParamsHolder(View itemView) {
            super(itemView);
            editAd = ((EditText) itemView.findViewById(R.id.edit_ad));
            editConnMin = ((EditText) itemView.findViewById(R.id.edit_connection_min));
            editConnMax = ((EditText) itemView.findViewById(R.id.edit_connection_max));
            editTimeout = ((EditText) itemView.findViewById(R.id.edit_connection_timeout));
            btnLaunch = ((Button) itemView.findViewById(R.id.btn_launch));

            btnLaunch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onClick(v,
                                editAd.getText().toString(),
                                editConnMin.getText().toString(),
                                editConnMax.getText().toString(),
                                editTimeout.getText().toString());
                    }
                }
            });
        }

        public void setLaunchClickListener(OnLaunchClickListener listener) {
            this.mListener = listener;
        }
    }

    public class OffHostHolder extends RecyclerView.ViewHolder {
        Button btnStart;
        TextView textDistance;

        public OffHostHolder(View itemView) {
            super(itemView);
            btnStart = ((Button) itemView.findViewById(R.id.btn_start));
            textDistance = ((TextView) itemView.findViewById(R.id.text_distance));
            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOffHostListener != null) {
                        mOffHostListener.onClick(v);
                    }
                }
            });
        }
    }

    public interface OnLaunchClickListener {
        void onClick(View v, String ad, String connMin, String connMax, String timeout);
    }

    public interface OnOffHostClickListener {
        void onClick(View v);
    }

//    private void setLayoutParamListener(View v) {
//        v.setOnClickListener(this);
//    }

//    @Override
//    public void onClick(View v) {
//        ViewGroup.LayoutParams lp = v.getLayoutParams();
//        if (lp.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
//            originalHeight = lp.height;
//            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        } else if (originalHeight != 0){
//            lp.height = originalHeight;
//        } else {
//            // DO NOTHING
//        }
//        v.setLayoutParams(lp);
//    }
}
