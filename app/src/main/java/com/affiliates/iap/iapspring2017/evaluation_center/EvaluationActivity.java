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

    private TextView mTitle;
    EvalManager mSectionsPagerAdapter;
    private DotIndicator mDotIndicator;

    PosterEval mPosterEval;
    PresentationEval mPresentationEval;
    Summary mSummary;
    CompanyVote mCompanyVote;

    /**
     * {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation_manager);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mTitle = (TextView) findViewById(R.id.toolbar_title);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new EvalManager(getSupportFragmentManager(), EvaluationActivity.this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.evaluation_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mDotIndicator = (DotIndicator) findViewById(R.id.dots_indicator);

        mPosterEval = (PosterEval) mSectionsPagerAdapter.getItem(0);
        mSummary = (Summary) mSectionsPagerAdapter.getItem(1);
        mPresentationEval = (PresentationEval) mSectionsPagerAdapter.getItem(2);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.evaluation);
        tabLayout.setupWithViewPager(mViewPager);

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
        Poster poster = Constants.getPosters().get(posterID);
        mCompanyVote = companyUser.loadVote(posterID,getBaseContext());
    }

    @Override
    public void onStop() {
        System.out.println("khfkuf");
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("ON RESUME YEAH YEAH");
    }

    @Override
    public void onDestroy() {
        System.out.println("BOOM");
        super.onDestroy();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
