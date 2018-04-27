//
//  EvaluationActivity.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/11/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.EvaluationCenter;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.Extensions.BaseActivity;
import com.affiliates.iap.iapspring2017.R;

public class EvaluationActivity extends BaseActivity {
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private Toolbar mToolbar;
    private TextView mTitle;

    PresentationEval mPresentationEval;
    EvalManager mSectionsPagerAdapter;
    PosterEval mPosterEval;
    Summary mSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation_manager);
        this.bind();
        setToolBar();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new EvalManager(getSupportFragmentManager(), EvaluationActivity.this);
        setViewPager();
    }

    private void bind(){
        mViewPager = (ViewPager) findViewById(R.id.evaluation_container);
        mTitle = (TextView) findViewById(R.id.toolbar_title);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    /**
     *  Set up the ViewPager with the sections adapter.
     */
    private void setViewPager(){
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mPosterEval = (PosterEval) mSectionsPagerAdapter.getItem(0);
        mSummary = (Summary) mSectionsPagerAdapter.getItem(1);
        mPresentationEval = (PresentationEval) mSectionsPagerAdapter.getItem(2);

        mTabLayout = (TabLayout) findViewById(R.id.evaluation);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setText("Poster");
        mTabLayout.getTabAt(1).setText("Summary");
        mTabLayout.getTabAt(2).setText("Presentation");

        mViewPager.setCurrentItem(1);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if (position == 0){
                    mTitle.setText("Poster Evaluation");
                } else if(position == 1){
                    mTitle.setText("Evaluation Summary");
                } else {
                    mTitle.setText("Presentation Evaluation");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }
    /**
     * Prepares the navigation toolbar
     */
    private void setToolBar(){
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        verbose("STOP");
    }

    @Override
    protected void onResume() {
        super.onResume();
        verbose("Resume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        verbose("Destroy");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.go_back_out, R.anim.go_back_in);
        finish();
    }
}
