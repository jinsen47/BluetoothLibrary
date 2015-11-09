package com.github.jinsen47.bluetoothlibrary.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by Jinsen on 15/11/9.
 */
public class PileLayoutManager extends LinearLayoutManager {
    private int pileHeight;


    public PileLayoutManager(Context context) {
        super(context);
    }

    public PileLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public PileLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public int getPileHeight() {
        return pileHeight;
    }

    public void setPileHeight(int pileHeight) {
        this.pileHeight = pileHeight;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        if (pileHeight != 0) {
            return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pileHeight);
        } else {
            return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public RecyclerView.LayoutParams generateLayoutParams(Context c, AttributeSet attrs) {
        RecyclerView.LayoutParams lp = super.generateLayoutParams(c, attrs);
        if (pileHeight != 0) {
            lp.height = pileHeight;
        }
        return lp;
    }

    @Override
    public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(lp);

        if (pileHeight != 0) {
            layoutParams.height = pileHeight;
        }
        return layoutParams;
    }
}
