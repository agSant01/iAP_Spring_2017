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
import android.content.pm.LabeledIntent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.BaseActivity;
import com.affiliates.iap.iapspring2017.Constants;
import com.affiliates.iap.iapspring2017.Models.IAPStudent;
import com.affiliates.iap.iapspring2017.Models.User;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.services.DataService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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
    private LinearLayout mLinearLayout;
    private ImageView mLike, mUnlike;

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
        mLinearLayout = (LinearLayout) findViewById(R.id.linear_layout);

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

        if(Constants.getCurrentLoggedInUser().getAccountType() == User.AccountType.CompanyUser){
            mLike = new ImageView(getBaseContext());
            mUnlike = new ImageView(getBaseContext());

            mLike.setImageResource(R.drawable.ic_like);
            mUnlike.setImageResource(R.drawable.ic_unlike);

            mLike.setMinimumHeight(150);
            mLike.setMinimumWidth(150);
            mLike.setPadding(0,0,32,0);
            mLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: ASK CRISTIAN FOR THE COLOR FILLED THUMBS!!!!
                    v.setDrawingCacheBackgroundColor(getResources().getColor(R.color.appGreen));
                    if(Constants.getInterestedStudents() == null)
                        Constants.setInterestedStudents(new ArrayList<String>());
                    Constants.getInterestedStudents().add(in.getStringExtra("id"));
                    DataService.sharedInstance().setInterestedStudent(in.getStringExtra("id"));
                    mUnlike.setImageResource(R.drawable.ic_unlike);
                }
            });

            mUnlike.setMinimumHeight(150);
            mUnlike.setMinimumWidth(150);
            mUnlike.setPadding(32,0,0,0);
            mUnlike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setBackgroundColor(getResources().getColor(R.color.appGreen));
                    if(Constants.getInterestedStudents() != null){
                        Constants.getInterestedStudents().remove(in.getStringExtra("id"));
                        DataService.sharedInstance().removeInterestedStudent(in.getStringExtra("id"));
                    }
                    mLike.setImageResource(R.drawable.ic_like);
                }
            });

            mLinearLayout.addView(mLike);
            mLinearLayout.addView(mUnlike);
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
