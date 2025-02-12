//
//  AdvisorProfile.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/11/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Profiles;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.Extensions.BaseActivity;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.Services.ConstantsService;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdvisorProfile extends BaseActivity {
    private TextView mResearchIntent, mWebsite;
    private CircleImageView mCircleImageView;
    private TextView mEmail, mName, mDept;
    private LinearLayout mProjectsList;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advisor_profile);

        bind();                            // private method to bind all the resources to the controller

        setAllProyects();
        setWebsite();
        setToolBar();

        Intent in = getIntent();
        Picasso.with(getBaseContext())
                .load(in.getStringExtra("photoURL"))
                .placeholder(R.drawable.ic_gender_0)
                .error(R.drawable.ic_gender_0)
                .into(mCircleImageView);

        mResearchIntent.setText(in.getStringExtra("research").equals("NA") ? "To be defined" : in.getStringExtra("research"));
        mEmail.setText(in.getStringExtra("email"));
        mName.setText(in.getStringExtra("name").equals("NA") ? "Name not specified" : in.getStringExtra("name"));
        mDept.setText(in.getStringExtra("dpt").equals("NA") ? "Department not specified" : in.getStringExtra("dpt"));
    }

    private void bind(){
        mCircleImageView = (CircleImageView) findViewById(R.id.profile_image_activity);
        mResearchIntent = (TextView) findViewById(R.id.profile_advisor_research);
        mWebsite = (TextView) findViewById(R.id.profile_advisor_website);
        mProjectsList = (LinearLayout) findViewById(R.id.project_list);
        mEmail = (TextView) findViewById(R.id.profile_advisor_email);
        mDept = (TextView) findViewById(R.id.profile_advisor_deptm);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_poster_desc);
        mName = (TextView) findViewById(R.id.profile_advisor_name);
    }

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

    private void setWebsite(){
        if(getIntent().getStringExtra("website").equals("NA")){
            mWebsite.setText("Website not specified");
            return;
        }
        mWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(mWebsite.getText().toString()));
                startActivity(browser);
            }
        });
        mWebsite.setLinkTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
        mWebsite.setText(getIntent().getStringExtra("website"));
    }

    private void setAllProyects(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = 11;
        if(getIntent().getStringArrayListExtra("projects") == null){
            TextView listItem = new TextView(getBaseContext());
            listItem.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            listItem.setText("No projects registered for this advisor");
            listItem.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            listItem.setLayoutParams(params);
            listItem.setTextColor(getResources().getColor(R.color.appGrey));
            mProjectsList.addView(listItem);
            return;
        }
        for (String s : getIntent().getStringArrayListExtra("projects")) {
            if (ConstantsService.getPosters().containsKey(s)) {
                TextView listItem = new TextView(getBaseContext());
                listItem.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                listItem.setText("\u2022 " + ConstantsService.getPosters().get(s).getProjectName());
                listItem.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                listItem.setLayoutParams(params);
                listItem.setTextColor(getResources().getColor(R.color.appGrey));
                mProjectsList.addView(listItem);
            }
        }
    }

    @Override
    public void onStop() {
        verbose("onStop()");
        super.onStop();
    }

    @Override
    protected void onResume() {
        verbose("onResume()");
        super.onResume();
    }

    @Override
    public void onDestroy() {
        verbose("onDestroy()");
        super.onDestroy();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
