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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.BaseActivity;
import com.affiliates.iap.iapspring2017.Constants;
import com.affiliates.iap.iapspring2017.Models.CompanyUser;
import com.affiliates.iap.iapspring2017.Models.IAPStudent;
import com.affiliates.iap.iapspring2017.Models.User;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.services.DataService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class IAPStudentProfile extends BaseActivity {
    private static final String TAG = "IAPStudentProfile";

    private CircleImageView mCircleImageView;
    private LinearLayout mLinearLayout;
    private ImageView mLike, mUnlike;
    private TextView mProyectName;
    private TextView mGradDate;
    private Toolbar mToolbar;
    private TextView mEmail;
    private Button mResume;
    private TextView mName;
    private TextView mDept;
    private TextView mBio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iapstudent_profile);
        bind();                 // private method to bind all the resources to the controller

        Intent in = getIntent();
        setToolBar();

        Picasso.with(getBaseContext()).load(in.getStringExtra("photoURL")).placeholder(R.drawable.ic_gender)
                .error(R.drawable.ic_gender).into(mCircleImageView);

        String projects = "";
        ArrayList<String> arr = in.getStringArrayListExtra("projects");
        int i;
        for(i = 0; i < arr.size()-1; i++){
            projects += (i+1) + ". " + arr.get(i) + "\n\n";
        }
        projects += (i+1) + ". " + arr.get(i);

        mProyectName.setText(projects);
        mGradDate.setText(in.getStringExtra("gradDate"));
        mEmail.setText(in.getStringExtra("email"));
        mName.setText(in.getStringExtra("name"));
        mDept.setText(in.getStringExtra("dpt"));
        mBio.setText(in.getStringExtra("bio"));
        setResume();

        if(Constants.getCurrentLoggedInUser().getAccountType() == User.AccountType.CompanyUser){
            setInterestOptions();
        }
    }

    private void bind(){
        mCircleImageView = (CircleImageView) findViewById(R.id.profile_image_activity);
        mProyectName= (TextView) findViewById(R.id.profile_iap_project);
        mLinearLayout = (LinearLayout) findViewById(R.id.linear_layout);
        mGradDate = (TextView) findViewById(R.id.profile_iap_graddate);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_poster_desc);
        mBio = (TextView) findViewById(R.id.profile_iap_objective);
        mEmail = (TextView) findViewById(R.id.profile_iap_email);
        mResume = (Button) findViewById(R.id.profile_iap_resume);
        mDept = (TextView) findViewById(R.id.profile_iap_deptm);
        mName = (TextView) findViewById(R.id.profile_iap_name);
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

    private void setInterestOptions(){
        final Intent in = getIntent();
        setLikeAndUnLike();

        CompanyUser companyUser = (CompanyUser) Constants.getCurrentLoggedInUser();

        if( companyUser.isLiked(getIntent().getStringExtra("id")) ){
            mLike.setBackgroundColor(getResources().getColor(R.color.appGreen));
        } else if (companyUser.isUnliked(getIntent().getStringExtra("id"))){
            mUnlike.setBackgroundColor(getResources().getColor(R.color.appGreen));
        }

        mUnlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundColor(getResources().getColor(R.color.appGreen));
                if(Constants.getLikedStudents() == null)
                    Constants.setLikedStudents(new ArrayList<IAPStudent>());

                ArrayList<IAPStudent> arr =  Constants.getLikedStudents();
                for (int i = 0; i < arr.size(); i++)
                    if(arr.get(i).getUserID().contains(in.getStringExtra("id"))) {
                        Log.v(TAG,arr.get(i).getUserID());
                        Constants.getUnlikedStudents().add(Constants.getLikedStudents().remove(i));
                        break;
                    }
                DataService.sharedInstance().unlikeStudent(in.getStringExtra("id"));
                mLike.setImageResource(R.drawable.ic_like);
                mLike.setBackgroundColor(getResources().getColor(R.color.appWhite));
            }
        });

        mLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: ASK CRISTIAN FOR THE COLOR FILLED THUMBS!!!!
                v.setDrawingCacheBackgroundColor(getResources().getColor(R.color.appGreen));
                if(Constants.getLikedStudents() == null)
                    Constants.setLikedStudents(new ArrayList<IAPStudent>());

                DataService.sharedInstance().getUserData(in.getStringExtra("id"), new Callback<IAPStudent>() {
                    @Override
                    public void success(IAPStudent data) {
                        if(!((CompanyUser) Constants.getCurrentLoggedInUser()).isLiked(data.getUserID())) {
                            Log.v(TAG, "FUCK");
                            Constants.getLikedStudents().add((IAPStudent) data);
                            DataService.sharedInstance().likeStudent(in.getStringExtra("id"));
                        }
                    }

                    @Override
                    public void failure(String message) {
                            Log.e(TAG, message);
                        }
                });
                mUnlike.setImageResource(R.drawable.ic_unlike);
                mUnlike.setBackgroundColor(getResources().getColor(R.color.appWhite));
            }
        });


        mLinearLayout.addView(mLike);
        mLinearLayout.addView(mUnlike);
    }
    private void setResume(){
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

    private void setLikeAndUnLike(){
        mLike = new ImageView(getBaseContext());
        mUnlike = new ImageView(getBaseContext());

        mLike.setImageResource(R.drawable.ic_like);
        mUnlike.setImageResource(R.drawable.ic_unlike);

        mLike.setMinimumHeight(150);
        mLike.setMinimumWidth(150);
        mLike.setPadding(0,0,32,0);

        mUnlike.setMinimumHeight(150);
        mUnlike.setMinimumWidth(150);
        mUnlike.setPadding(32,0,0,0);
    }
    @Override
    public void onStop() {
        Log.v(TAG, "onStop()");
        super.onStop();
    }

    @Override
    protected void onResume() {
        Log.v(TAG, "onResume()");
        super.onResume();
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
