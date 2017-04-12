//
//  EvaluationActivity.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/11/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.evaluation_center;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.BaseActivity;
import com.affiliates.iap.iapspring2017.Constants;
import com.affiliates.iap.iapspring2017.Models.CompanyUser;
import com.affiliates.iap.iapspring2017.Models.CompanyVote;
import com.affiliates.iap.iapspring2017.Models.Poster;
import com.affiliates.iap.iapspring2017.PosterDescription;
import com.affiliates.iap.iapspring2017.R;
import com.matthewtamlin.sliding_intro_screen_library.indicators.DotIndicator;

public class EvaluationActivity extends BaseActivity {
    private static final String TAG = "EvaluationActivity";

    private DotIndicator mDotIndicator;
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

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new EvalManager(getSupportFragmentManager(), EvaluationActivity.this);

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mPosterEval = (PosterEval) mSectionsPagerAdapter.getItem(0);
        mSummary = (Summary) mSectionsPagerAdapter.getItem(1);
        mPresentationEval = (PresentationEval) mSectionsPagerAdapter.getItem(2);

        mTabLayout = (TabLayout) findViewById(R.id.evaluation);
        mTabLayout.setupWithViewPager(mViewPager);

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
                mDotIndicator.setSelectedItem(position,true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        Intent in = getIntent();
        String posterID = in.getStringExtra("posterID");
        CompanyUser companyUser = (CompanyUser) Constants.getCurrentLoggedInUser();
        //mCompanyVote = companyUser.loadVote(posterID,getBaseContext());
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "STOP");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "Resume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "Destroy");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void bind(){
        mViewPager = (ViewPager) findViewById(R.id.evaluation_container);
        mDotIndicator = (DotIndicator) findViewById(R.id.dots_indicator);
        mTitle = (TextView) findViewById(R.id.toolbar_title);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }
}
