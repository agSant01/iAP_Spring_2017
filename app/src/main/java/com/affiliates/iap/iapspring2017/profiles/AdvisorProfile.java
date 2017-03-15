//
//  AdvisorProfile.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/11/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.profiles;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.BaseActivity;
import com.affiliates.iap.iapspring2017.Constants;
import com.affiliates.iap.iapspring2017.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdvisorProfile extends BaseActivity {
    private static final String TAG = "AdvisorProfile";

    private CircleImageView mCircleImageView;
    private TextView mEmail;
    private TextView mName;
    private TextView mDept;
    private TextView mResearchIntent;
    private TextView mWebsite;
    private LinearLayout mClassesList;
    private LinearLayout mProjectsList;
    private TextView mListItem;

    private ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advisor_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_poster_desc);
        setSupportActionBar(toolbar);

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

        final Intent in = getIntent();

        mCircleImageView = (CircleImageView) findViewById(R.id.profile_image_activity);
        mName = (TextView) findViewById(R.id.profile_advisor_name);
        mEmail = (TextView) findViewById(R.id.profile_advisor_email);
        mDept = (TextView) findViewById(R.id.profile_advisor_deptm);
        mWebsite = (TextView) findViewById(R.id.profile_advisor_website);
        mResearchIntent = (TextView) findViewById(R.id.profile_advisor_research);
        mClassesList = (LinearLayout) findViewById(R.id.classes_list);
        mProjectsList = (LinearLayout) findViewById(R.id.project_list);

        Picasso.with(getBaseContext()).load(in.getStringExtra("photoURL")).placeholder(R.drawable.ic_gender)
                .error(R.drawable.ic_gender).into(mCircleImageView);
        mName.setText(in.getStringExtra("name"));
        mEmail.setText(in.getStringExtra("email"));
        mDept.setText(in.getStringExtra("dpt"));
        mWebsite.setText(in.getStringExtra("website"));
        mWebsite.setLinkTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
        mWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(mWebsite.getText().toString()));
                startActivity(browser);
            }
        });
        mResearchIntent.setText(in.getStringExtra("research"));

        list = in.getStringArrayListExtra("classes");
        for(int i = 0; i < list.size(); i++){
            Log.v(TAG, list.get(i));
            mListItem = new TextView(getBaseContext());
            mListItem.setText((i+1) + ". " + list.get(i));
            mListItem.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            mListItem.setPadding(0,0,0,2);
            mListItem.setTextColor(getResources().getColor(R.color.appGrey));
            mClassesList.addView(mListItem);
        }

        list = in.getStringArrayListExtra("projects");
        int counter=0;
        for(String s:list){
            if(Constants.getPosters().containsKey(s)){
                mListItem = new TextView(getBaseContext());
                mListItem.setText(++counter + ". " + Constants.getPosters().get(s).getProjectName());
                mListItem.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                mListItem.setPadding(0,0,0,11);
                mListItem.setTextColor(getResources().getColor(R.color.appGrey));
                mProjectsList.addView(mListItem);
            }
        }
    }

        @Override
    public void onStop() {
        System.out.println("onStop()");
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
