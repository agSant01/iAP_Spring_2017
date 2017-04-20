//
//  ForgotPasswordActivity.java
//  IAP
//
//  Created by Gabriel S. Santiago on 4/20/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.activities;

import com.affiliates.iap.iapspring2017.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.affiliates.iap.iapspring2017.BaseActivity;

public class LocationActivity extends BaseActivity{

    private Toolbar mToolbar;
    private Button mbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        setToolbar();
        mbutton = (Button) findViewById(R.id.button2);
        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Searches for 'Main Street' near San Francisco
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=Bioprocess Development and Training Complex (BDTC), Puerto Rico 114, Mayagüez");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
    }
    private void setToolbar(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar_poster_desc);
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
}
