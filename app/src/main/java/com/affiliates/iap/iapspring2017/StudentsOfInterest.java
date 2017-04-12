//
//  StudentsOfInterest.java
//  IAP
//
//  Created by Gabriel S. Santiago on 4/07/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.Models.IAPStudent;
import com.affiliates.iap.iapspring2017.adapters.StudentsInterestAdapter;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.profiles.IAPStudentProfile;
import com.affiliates.iap.iapspring2017.services.DataService;

import java.util.ArrayList;

public class StudentsOfInterest extends BaseActivity {
    private static final String TAG = "StudentsOfInterest";

    private StudentsInterestAdapter mStudentsInterestAdapter;
    private AlphaAnimation mFadeOutAnimation;
    private RelativeLayout mRelativeLayout;
    private ProgressBar mProgressBar;
    private TextView mToolBarText;
    private TextView mNoInterest;
    private ListView mListView;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout._listview_activity);

        bind();
        setToolbar();
        setProgressBar();
        showProgressBar(mProgressBar);

        mToolBarText.setText("Students of Interest");

        if ( Constants.getLikedStudents() == null){
            DataService.sharedInstance().getInterestedStudents(new Callback() {
                @Override
                public void success(Object data) {
                    Log.v(TAG, "Get Successfull");
                    setListView();
                }

                @Override
                public void failure(String message) {
                    Log.e(TAG, message);
                }
            });
        } else {
            setListView();
        }
    }


    private void bind() {
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relative_layout);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mToolBarText = (TextView) findViewById(R.id.toolbar_title);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_company);
        mListView = (ListView) findViewById(R.id._listview);
    }

    private void setToolbar(){
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

    private void setListView(){
        hideProgressBar(mProgressBar);
        mStudentsInterestAdapter = new StudentsInterestAdapter(getBaseContext(), Constants.getLikedStudents());
        mListView.setAdapter(mStudentsInterestAdapter);

        if (mStudentsInterestAdapter.getCount() == 0){
           emptyListAlert();
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IAPStudent student = mStudentsInterestAdapter.getItem(position);
                Intent in = new Intent(StudentsOfInterest.this, IAPStudentProfile.class);
                in.putStringArrayListExtra("projects", student.getProjectNames());
                in.putExtra("photoURL", student.getPhotoURL());
                in.putExtra("resume", student.getResumeURL());
                in.putExtra("gradDate", student.getGradDate());
                in.putExtra("dpt", student.getDepartment());
                in.putExtra("bio", student.getObjective());
                in.putExtra("email", student.getEmail());
                in.putExtra("name", student.getName());
                in.putExtra("id",student.getUserID());
                startActivity(in);
            }
        });
    }

    private void setProgressBar(){
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        mProgressBar.setVerticalFadingEdgeEnabled(true);
        mFadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);//fade from 1 to 0 alpha
        mFadeOutAnimation.setDuration(1000);
        mFadeOutAnimation.setFillAfter(true);
    }

    private void emptyListAlert(){
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mNoInterest = new TextView(getBaseContext());
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mNoInterest.setTextSize(16);
        mNoInterest.setText("No Students of Interest");
        mNoInterest.setLayoutParams(layoutParams);
        mNoInterest.setVisibility(View.VISIBLE);
        mRelativeLayout.addView(mNoInterest);
    }

    @Override
    public void onStop() {
        System.out.println("onStop()");
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mStudentsInterestAdapter != null) {
            mStudentsInterestAdapter.notifyDataSetChanged();
            if( mStudentsInterestAdapter.isEmpty() ) {
                showProgressBar(mProgressBar);
                setListView();
                Log.d(TAG, "WHY");
            }
        }
        Log.v(TAG,"onResume()");
    }

    @Override
    public void onDestroy() {
        System.out.println("onDestroy()");
        super.onDestroy();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
