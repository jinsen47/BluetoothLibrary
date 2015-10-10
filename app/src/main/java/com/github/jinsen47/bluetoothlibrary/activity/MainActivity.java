package com.github.jinsen47.bluetoothlibrary.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.github.jinsen47.bluetoothlibrary.R;
import com.github.jinsen47.bluetoothlibrary.adapter.MainPagerAdapter;

public class MainActivity extends AppCompatActivity {
    private TabLayout layoutTab;
    private ViewPager mPager;
    private MainPagerAdapter mAdapter;
    private FloatingActionButton fab;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        setListeners();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        layoutTab = ((TabLayout) findViewById(R.id.tab_main));
        mPager = ((ViewPager) findViewById(R.id.pager_main));
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }
    private void initData() {
        mAdapter = new MainPagerAdapter(getSupportFragmentManager(), this);
        mPager.setAdapter(mAdapter);
        layoutTab.setTabsFromPagerAdapter(mAdapter);
        layoutTab.setupWithViewPager(mPager);
    }

    private void setListeners() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
