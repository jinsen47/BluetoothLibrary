package com.github.jinsen47.bluetoothlibrary.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Jinsen on 15/9/22.
 */
public abstract class BaseFragment extends Fragment{
    public BaseFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(setLayout(), container, false);
        initViews(v);
        setListeners();
        setHasOptionsMenu(true);
        return v;
    }

    protected abstract int setLayout();
    protected abstract void initViews(View rootView);
    protected abstract void setListeners();
}
