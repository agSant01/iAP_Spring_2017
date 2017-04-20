//
//  CompanyListActivity.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/07/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.affiliates.iap.iapspring2017.BaseActivity;
import com.affiliates.iap.iapspring2017.Constants;
import com.affiliates.iap.iapspring2017.Models.Sponsors;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.adapters.CompanyListAdapter;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.services.DataService;
import com.google.firebase.crash.FirebaseCrash;

import java.util.ArrayList;

public class CompanyListActivity extends BaseActivity {
    private static final String TAG = "ComapanyList";

    private CompanyListAdapter mCompanyListAdapter;
    private AlphaAnimation mFadeOutAnimation;
    private ProgressBar mProgresBar;
    private ListView mListView;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout._listview_activity);

        bind();
        setToolbar();
        setProgressBar();

        if(Constants.getSponsor() == null)
            DataService.sharedInstance().getSponsors(new Callback() {
                @Override
                public void success(Object data) {
                    Constants.setSponsor((ArrayList<Sponsors>) data);
                    setListView();
                    Log.v(TAG,"Get Sponsors Success!");
                }

                @Override
                public void failure(String message) {
                    FirebaseCrash.log(TAG+ " :"+ message);
                    Log.e(TAG, message);
                }
            });
        else {
           setListView();
        }
    }

    private void bind(){
        mListView = (ListView) findViewById(R.id._listview);
        mProgresBar = (ProgressBar) findViewById(R.id.progressBar);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_company);
    }

    private void setListView(){
        mCompanyListAdapter = new CompanyListAdapter(getBaseContext(), Constants.getSponsor());
        mListView.setAdapter(mCompanyListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = Constants.getSponsor().get(position).getWebsite();
                Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(in);
            }
        });
        mProgresBar.startAnimation(mFadeOutAnimation);
        mProgresBar.setVisibility(ProgressBar.INVISIBLE);
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

    private void setProgressBar(){
        mProgresBar.setVisibility(ProgressBar.VISIBLE);
        mProgresBar.setVerticalFadingEdgeEnabled(true);
        mFadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);//fade from 1 to 0 alpha
        mFadeOutAnimation.setDuration(1000);
        mFadeOutAnimation.setFillAfter(true);
    }
}
