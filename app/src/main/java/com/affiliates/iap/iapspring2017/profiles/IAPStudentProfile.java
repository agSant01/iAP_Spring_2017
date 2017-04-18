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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class IAPStudentProfile extends BaseActivity {
    private static final String TAG = "IAPStudentProfile";

    private CircleImageView mCircleImageView;
    private LinearLayout mLinearLayout;
    private ImageView mLike, mUnlike, mUndecided;
    private TextView mProyectName;
    private TextView mGradDate;
    private Toolbar mToolbar;
    private TextView mEmail;
    private Button mResume;
    private TextView mName;
    private TextView mDept;
    private TextView mBio;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iapstudent_profile);
        bind();                 // private method to bind all the resources to the controller

        Intent in = getIntent();
        setToolBar();

        Picasso.with(getBaseContext()).load(in.getStringExtra("photoURL")).placeholder(R.drawable.ic_gender_0)
                .error(R.drawable.ic_gender_0).into(mCircleImageView);

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

        Log.v(TAG, "TIPO: " + Constants.getCurrentLoggedInUser().getAccountType().toString());
        if(Constants.getCurrentLoggedInUser().getAccountType() == User.AccountType.CompanyUser){
            if(Constants.getUndecidedStudents() == null
                    || Constants.getUnlikedStudents() == null
                    || Constants.getLikedStudents() == null ){
                Log.v(TAG, "Entro!");
                DataService.sharedInstance().getIAPStudentsOfInterest(new Callback<HashMap<String, ArrayList<IAPStudent>>>() {
                    @Override
                    public void success(HashMap<String, ArrayList<IAPStudent>> data) {
                        Log.v(TAG, "Get Successfull");
                        Constants.setUndecidedStudents(data.get("undecided"));
                        Constants.setLikedStudents(data.get("liked"));
                        Constants.setUnlikedStudents(data.get("unliked"));
                        setInterestOptions();
                        Log.v(TAG, data.get("undecided").size() + " " + data.get("liked").size() + " "+ data.get("unliked").size() + "");
                    }
                    @Override
                    public void failure(String message) {
                        Log.e(TAG, message);
                    }
                });
            } else {
                setInterestOptions();
            }
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
        setInterestStatus();

        final CompanyUser companyUser = (CompanyUser) Constants.getCurrentLoggedInUser();

        if( companyUser.isLiked(getIntent().getStringExtra("id")) ){
            mLike.setImageResource(R.drawable.ic_thumb_up_filled_green);
        } else if (companyUser.isUnliked(getIntent().getStringExtra("id"))){
            mUnlike.setImageResource(R.drawable.ic_thumb_down_filled_green);
        } else if (companyUser.isUndecided(getIntent().getStringExtra("id"))){
            mUndecided.setImageResource(R.drawable.ic_thumbs_undecided_filled_green);
        }

        mUnlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(companyUser.isUnliked(getIntent().getStringExtra("id"))) return;
                ArrayList<IAPStudent> arr;
                if(companyUser.isLiked(getIntent().getStringExtra("id"))){
                    if ( Constants.getLikedStudents() == null )
                        Constants.setLikedStudents(new ArrayList<IAPStudent>());
                    arr = Constants.getLikedStudents();
                    mLike.setImageResource(R.drawable.ic_thumb_up_unfilled);
                } else {
                    if ( Constants.getUndecidedStudents() == null)
                        Constants.setUndecidedStudents(new ArrayList<IAPStudent>());
                    arr = Constants.getUndecidedStudents();
                    mUndecided.setImageResource(R.drawable.ic_thumbs_undecided_unfilled);
                }
                for (int i = 0; i < arr.size(); i++)
                    if(arr.get(i).getUserID().equals(in.getStringExtra("id"))) {
                        Log.v(TAG,arr.get(i).getUserID());
                        Constants.getUnlikedStudents().add(arr.remove(i));
                        break;
                    }
                DataService.sharedInstance().setInterestForStudent(in.getStringExtra("id"), "Unlike");
                mUnlike.setImageResource(R.drawable.ic_thumb_down_filled_green);
                if(mToast != null)
                    mToast.cancel();
                mToast = Toast.makeText(getBaseContext(),"Set as Not Interested",Toast.LENGTH_SHORT);
                mToast.show();
            }
        });

        mLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(companyUser.isLiked(getIntent().getStringExtra("id"))) return;
                ArrayList<IAPStudent> arr;
                if(companyUser.isUnliked(getIntent().getStringExtra("id"))){
                    Log.v(TAG, "Select: Unlik");
                    if ( Constants.getUnlikedStudents() == null )
                        Constants.setUnlikedStudents(new ArrayList<IAPStudent>());
                    arr = Constants.getUnlikedStudents();
                    mUnlike.setImageResource(R.drawable.ic_thumb_down_unfilled);
                } else {
                    Log.v(TAG, "Select: Undecieds");
                    if ( Constants.getUndecidedStudents() == null)
                        Constants.setUndecidedStudents(new ArrayList<IAPStudent>());
                    arr = Constants.getUndecidedStudents();
                    mUndecided.setImageResource(R.drawable.ic_thumbs_undecided_unfilled);
                }
                for (int i = 0; i < arr.size(); i++)
                    if(arr.get(i).getUserID().contains(in.getStringExtra("id"))) {
                        Log.v(TAG, "FOUND");
                        Log.v(TAG,arr.get(i).getUserID());
                        Constants.getLikedStudents().add(arr.remove(i));
                        break;
                    }
                DataService.sharedInstance().setInterestForStudent(in.getStringExtra("id"), "Like");
                mLike.setImageResource(R.drawable.ic_thumb_up_filled_green);
                if(mToast != null)
                    mToast.cancel();
                mToast = Toast.makeText(getBaseContext(),"Set as Interested",Toast.LENGTH_SHORT);
                mToast.show();
            }
        });

        mUndecided.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(companyUser.isUndecided(getIntent().getStringExtra("id"))) return;
                ArrayList<IAPStudent> arr;
                if(companyUser.isUnliked(getIntent().getStringExtra("id"))){
                    if ( Constants.getUnlikedStudents() == null )
                        Constants.setUnlikedStudents(new ArrayList<IAPStudent>());
                    arr = Constants.getUnlikedStudents();
                    mUnlike.setImageResource(R.drawable.ic_thumb_down_unfilled);
                } else {
                    if ( Constants.getLikedStudents() == null)
                        Constants.setLikedStudents(new ArrayList<IAPStudent>());
                    arr = Constants.getLikedStudents();
                    mLike.setImageResource(R.drawable.ic_thumb_up_unfilled);
                }
                for (int i = 0; i < arr.size(); i++)
                    if(arr.get(i).getUserID().contains(in.getStringExtra("id"))) {
                        Log.v(TAG,arr.get(i).getUserID());
                        Constants.getUndecidedStudents().add(arr.remove(i));
                        break;
                    }
                DataService.sharedInstance().setInterestForStudent(in.getStringExtra("id"), "Undecided");
                mUndecided.setImageResource(R.drawable.ic_thumbs_undecided_filled_green);
                if(mToast != null)
                    mToast.cancel();
                mToast = Toast.makeText(getBaseContext(),"Set as Undecided",Toast.LENGTH_SHORT);
                mToast.show();
            }
        });
        mLinearLayout.addView(mLike);
        mLinearLayout.addView(mUndecided);
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

    private void setInterestStatus(){
        mLike = new ImageView(getBaseContext());
        mUnlike = new ImageView(getBaseContext());
        mUndecided = new ImageView(getBaseContext());

        mLike.setImageResource(R.drawable.ic_thumb_up_unfilled);
        mUnlike.setImageResource(R.drawable.ic_thumb_down_unfilled);
        mUndecided.setImageResource(R.drawable.ic_thumbs_undecided_unfilled);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        params.setMarginEnd(30);

        mLike.setMinimumHeight(135);
        mLike.setMinimumWidth(140);
        mLike.setPadding(0,0,16,0);
        mLike.setLayoutParams(params);

        params.setMarginEnd(0);
        params.setMarginStart(16);
        mUnlike.setMinimumHeight(135);
        mUnlike.setMinimumWidth(135);
        mUnlike.setPadding(8,0,0,0);
        mUnlike.setLayoutParams(params);

        mUndecided.setMinimumHeight(190);
        mUndecided.setMinimumWidth(190);
        mUndecided.setPadding(10,0,8,0);
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
