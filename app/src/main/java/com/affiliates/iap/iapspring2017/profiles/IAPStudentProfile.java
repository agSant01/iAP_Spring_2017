//
//  MainActivity.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/10/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.profiles;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.BaseActivity;
import com.affiliates.iap.iapspring2017.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class IAPStudentProfile extends BaseActivity {
    private CircleImageView mCircleImageView;
    private TextView mEmail;
    private TextView mName;
    private TextView mDept;
    private TextView mGradDate;
    private TextView mBio;
    private TextView mProyectName;
    private Button mResume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iapstudent_profile);

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
        mName = (TextView) findViewById(R.id.profile_iap_name);
        mEmail = (TextView) findViewById(R.id.profile_iap_email);
        mDept = (TextView) findViewById(R.id.profile_iap_deptm);
        mGradDate = (TextView) findViewById(R.id.profile_iap_graddate);
        mProyectName= (TextView) findViewById(R.id.profile_iap_project);
        mBio = (TextView) findViewById(R.id.profile_iap_objective) ;
        mResume = (Button) findViewById(R.id.profile_iap_resume);

        Picasso.with(getBaseContext()).load(in.getStringExtra("photoURL")).placeholder(R.drawable.ic_gender)
                .error(R.drawable.ic_gender).into(mCircleImageView);
        mName.setText(in.getStringExtra("name"));
        mEmail.setText(in.getStringExtra("email"));
        mDept.setText(in.getStringExtra("dpt"));
        mGradDate.setText(in.getStringExtra("gradDate"));
        mProyectName.setText(in.getStringExtra("projectName"));
        mBio.setText(in.getStringExtra("bio"));
        mResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = getIntent().getStringExtra("resume");
                if(!url.contains("https://firebasestorage.googleapis.com"))
                   new AlertDialog.Builder(v.getContext())
                            .setMessage("Resume not Available")
                            .setPositiveButton("OK",  new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }                // do nothing
                            }).show();
                else {
                    Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browser);
                }
            }
        });
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
