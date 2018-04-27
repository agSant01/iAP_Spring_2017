//
//  CompanyListActivity.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/07/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Tabs.MoreTab.Sponsors;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.affiliates.iap.iapspring2017.Extensions.BaseActivity;
import com.affiliates.iap.iapspring2017.Interfaces.Callback;
import com.affiliates.iap.iapspring2017.Models.Sponsors;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.Services.ConstantsService;
import com.affiliates.iap.iapspring2017.Services.DataService;

import java.util.ArrayList;

public class CompanyListActivity extends BaseActivity {
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
        setListView();
    }

    /**
     * Binds all the UI components to variables for use
     */
    private void bind(){
        mListView = (ListView) findViewById(R.id._listview);
        mProgresBar = (ProgressBar) findViewById(R.id.progressBar);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_company);
    }

    /**
     * Sets company data on the listView
     */
    private void setListView(){
        // Checks if there was previous data on memory
        if(ConstantsService.getSponsor() == null)
            // Gets data from the server
            DataService.sharedInstance().getSponsors(new Callback<ArrayList<Sponsors>>() {
                @Override
                public void success(ArrayList<Sponsors> data) {
                    ConstantsService.setSponsor(data);               // saves it on memory
                    setListAdapter();                                // sets the adapter of the data to the listView
                    verbose("Get Sponsors Success!");
                }

                @Override
                public void failure(String message) {
                    FCLog(message);
                    error(message);
                }
            });
        else {                             // previous data on memory
            setListAdapter();              // sets the adapter of the data to the listView
        }
    }

    /**
     * Bundles the data into the CompanyListAdapter and sets it to the listView
     */
    private void setListAdapter(){
        // creates the adapter
        mCompanyListAdapter = new CompanyListAdapter(getBaseContext(), ConstantsService.getSponsor());
        mListView.setAdapter(mCompanyListAdapter);

        // on click: get url of company and open that url on the default web browser
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = ConstantsService.getSponsor().get(position).getWebsite();    // get url
                Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(url));        // create new intent
                startActivity(in);                                                 // start intent
            }
        });
        mProgresBar.startAnimation(mFadeOutAnimation);
        mProgresBar.setVisibility(ProgressBar.INVISIBLE);                          // hide progressbar
    }

    /**
     *  Sets the toolbar
     */
    private void setToolbar(){
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolbar.setNavigationIcon(R.drawable.ic_back_arrow);                   // set back_arrow icon
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {      // when clicked execute onBackPressed()
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     * Sets the properties of the progress bar
     */
    private void setProgressBar(){
        mProgresBar.setVisibility(ProgressBar.VISIBLE);
        mProgresBar.setVerticalFadingEdgeEnabled(true);
        mFadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);//fade from 1 to 0 alpha
        mFadeOutAnimation.setDuration(1000);
        mFadeOutAnimation.setFillAfter(true);
    }
}
