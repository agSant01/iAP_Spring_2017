//
//  HomeActivity.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/10/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Profiles;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.Extensions.BaseActivity;
import com.affiliates.iap.iapspring2017.Interfaces.Callback;
import com.affiliates.iap.iapspring2017.Models.CompanyUser;
import com.affiliates.iap.iapspring2017.Models.IAPStudent;
import com.affiliates.iap.iapspring2017.Models.User;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.Services.ConstantsService;
import com.affiliates.iap.iapspring2017.Services.DataService;
import com.affiliates.iap.iapspring2017.Tabs.PostersTab.PosterDescription.PosterViewer;
import com.google.firebase.crash.FirebaseCrash;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class IAPStudentProfile extends BaseActivity {
    private TextView mProyectName, mGradDate, mName, mDept, mBio, mobjective, mEmail;
    private ImageView mLike, mUnlike, mUndecided;
    private CircleImageView mCircleImageView;
    private LinearLayout mLinearLayout;
    private TableLayout mAllThings;
    private View mView1, mView2;
    private IAPStudent student;
    private Toolbar mToolbar;
    private ProgressBar pb;
    private Button mResume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iapstudent_profile);
        bind();                 // private method to bind all the resources to the controller

        final Intent in = getIntent();
        setToolBar();
        showProgressBar(pb);
        DataService.sharedInstance().getUserData(in.getStringExtra("id"), new Callback<User>() {
            @Override
            public void success(User data) {
                student = (IAPStudent) data;
                verbose("TIPO: " + ConstantsService.getCurrentLoggedInUser().getAccountType().toString());
                if(ConstantsService.getCurrentLoggedInUser().getAccountType() == User.AccountType.CompanyUser){
                    if(validateSOI()){
                        verbose("Entro!");
                        getStudentsInterest();
                    } else {
                        setInterestOptions();
                    }
                }

                Picasso.with(getBaseContext())
                        .load(in.getStringExtra("photoURL"))
                        .placeholder(R.drawable.ic_gender_0)
                        .error(R.drawable.ic_gender_0)
                        .into(mCircleImageView);
                String projects = "";
                ArrayList<String> arr = in.getStringArrayListExtra("projects");
                int i;
                for(i = 0; i < arr.size(); i++){
                    projects += "\u2022 " + arr.get(i) + "\n";
                }
                mProyectName.setText(projects);
                mGradDate.setText(in.getStringExtra("gradDate").equals("NA") ? "Graduation date not specified" : in.getStringExtra("gradDate"));
                mEmail.setText(in.getStringExtra("email"));
                mName.setText(in.getStringExtra("name").equals("NA") ? "Name not specified" : in.getStringExtra("name"));
                mDept.setText(in.getStringExtra("dpt").equals("NA") ? "Department not specified" : in.getStringExtra("dpt"));
                mBio.setText(in.getStringExtra("bio").equals("NA") ? "Objective not specified" : in.getStringExtra("bio"));
                setResume();

                hideProgressBar(pb);
                setVisibility(true);
            }

            @Override
            public void failure(String message) {

            }
        });
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
        mAllThings = (TableLayout) findViewById(R.id.tableLayout5);
        mView1 = findViewById(R.id.view);
        mView2 = findViewById(R.id.view3);
        mobjective = (TextView) findViewById(R.id.textView10);
        pb = (ProgressBar) findViewById(R.id.progressBar2);
        setVisibility(false);
    }

    private boolean validateSOI(){
        return ConstantsService.getUndecidedStudents() == null
                || ConstantsService.getUnlikedStudents() == null
                || ConstantsService.getLikedStudents() == null;
    }

    private void getStudentsInterest(){
        DataService.sharedInstance().getIAPStudentsOfInterest(new Callback<HashMap<String, ArrayList<IAPStudent>>>() {
            @Override
            public void success(HashMap<String, ArrayList<IAPStudent>> data) {
                verbose("Get Successfull");
                ConstantsService.setUndecidedStudents(data.get("undecided"));
                ConstantsService.setLikedStudents(data.get("liked"));
                ConstantsService.setUnlikedStudents(data.get("unliked"));
                verbose(data.get("undecided").size() + " " + data.get("liked").size() + " "+ data.get("unliked").size() + "");
                setInterestOptions();
            }
            @Override
            public void failure(String message) {
                FirebaseCrash.log(getLocalClassName() + ": " + message);
                error(message);
            }
        });
    }

    private void setVisibility(boolean state){
        mCircleImageView.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
        mProyectName.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
        mLinearLayout.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
        mGradDate.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
        mBio.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
        mEmail.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
        mResume.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
        mDept.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
        mName.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
        mAllThings.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
        mView1.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
        mView2.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
        mobjective.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
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

        final CompanyUser companyUser = (CompanyUser) ConstantsService.getCurrentLoggedInUser();

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
                final ArrayList<IAPStudent> arr;
                if(companyUser.isLiked(getIntent().getStringExtra("id"))){
                    if ( ConstantsService.getLikedStudents() == null )
                        ConstantsService.setLikedStudents(new ArrayList<IAPStudent>());
                    arr = ConstantsService.getLikedStudents();
                    mLike.setImageResource(R.drawable.ic_thumb_up_unfilled);
                } else if(companyUser.isUndecided(getIntent().getStringExtra("id"))) {
                    if ( ConstantsService.getUndecidedStudents() == null)
                        ConstantsService.setUndecidedStudents(new ArrayList<IAPStudent>());
                    arr = ConstantsService.getUndecidedStudents();
                    mUndecided.setImageResource(R.drawable.ic_thumbs_undecided_unfilled);
                } else {
                    if ( ConstantsService.getUnlikedStudents() == null)
                        ConstantsService.setUnlikedStudents(new ArrayList<IAPStudent>());
                    ConstantsService.getUnlikedStudents().add(student);
                    DataService.sharedInstance().setInterestForStudent(in.getStringExtra("id"), "Unlike");
                    mUnlike.setImageResource(R.drawable.ic_thumb_down_filled_green);
                    showShortToast("Set as Not Interested");
                    return;
                }
                for (int i = 0; i < arr.size(); i++)
                    if(arr.get(i).getUserID().equals(in.getStringExtra("id"))) {
                        verbose(arr.get(i).getUserID());
                        ConstantsService.getUnlikedStudents().add(arr.remove(i));
                        break;
                    }
                DataService.sharedInstance().setInterestForStudent(in.getStringExtra("id"), "Unlike");
                mUnlike.setImageResource(R.drawable.ic_thumb_down_filled_green);
                showShortToast("Set as Not Interested");
            }
        });

        mLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(companyUser.isLiked(getIntent().getStringExtra("id"))) return;
                final ArrayList<IAPStudent> arr;
                if(companyUser.isUnliked(getIntent().getStringExtra("id"))){
                    verbose("Select: Unlik");
                    if ( ConstantsService.getUnlikedStudents() == null )
                        ConstantsService.setUnlikedStudents(new ArrayList<IAPStudent>());
                    arr = ConstantsService.getUnlikedStudents();
                    mUnlike.setImageResource(R.drawable.ic_thumb_down_unfilled);
                } else if (companyUser.isUndecided(getIntent().getStringExtra("id"))){
                    verbose("Select: Undecieds");
                    if ( ConstantsService.getUndecidedStudents() == null)
                        ConstantsService.setUndecidedStudents(new ArrayList<IAPStudent>());
                    arr = ConstantsService.getUndecidedStudents();
                    mUndecided.setImageResource(R.drawable.ic_thumbs_undecided_unfilled);
                } else {
                    if ( ConstantsService.getLikedStudents() == null)
                        ConstantsService.setLikedStudents(new ArrayList<IAPStudent>());
                    ConstantsService.getLikedStudents().add(student);
                    DataService.sharedInstance().setInterestForStudent(in.getStringExtra("id"), "Like");
                    mLike.setImageResource(R.drawable.ic_thumb_up_filled_green);
                    showShortToast("Set as Interested");
                    return;
                }
                for (int i = 0; i < arr.size(); i++)
                    if(arr.get(i).getUserID().contains(in.getStringExtra("id"))) {
                        verbose(arr.get(i).getUserID());
                        ConstantsService.getLikedStudents().add(arr.remove(i));
                        break;
                    }
                DataService.sharedInstance().setInterestForStudent(in.getStringExtra("id"), "Like");
                mLike.setImageResource(R.drawable.ic_thumb_up_filled_green);
                showShortToast("Set as Interested");
            }
        });

        mUndecided.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(companyUser.isUndecided(getIntent().getStringExtra("id"))) return;
                final ArrayList<IAPStudent> arr;
                error("UN");
                if(companyUser.isUnliked(getIntent().getStringExtra("id"))){
                    if ( ConstantsService.getUnlikedStudents() == null )
                        ConstantsService.setUnlikedStudents(new ArrayList<IAPStudent>());
                    arr = ConstantsService.getUnlikedStudents();
                    mUnlike.setImageResource(R.drawable.ic_thumb_down_unfilled);
                } else if (companyUser.isLiked(getIntent().getStringExtra("id"))){
                    if ( ConstantsService.getLikedStudents() == null)
                        ConstantsService.setLikedStudents(new ArrayList<IAPStudent>());
                    arr = ConstantsService.getLikedStudents();
                    mLike.setImageResource(R.drawable.ic_thumb_up_unfilled);
                } else {
                    error("NONE");
                    if ( ConstantsService.getUndecidedStudents() == null)
                        ConstantsService.setUndecidedStudents(new ArrayList<IAPStudent>());
                    ConstantsService.getUndecidedStudents().add(student);
                    DataService.sharedInstance().setInterestForStudent(in.getStringExtra("id"), "Undecided");
                    mUndecided.setImageResource(R.drawable.ic_thumbs_undecided_filled_green);
                    showShortToast("Set as Undecided");
                    return;
                }

                for (int i = 0; i < arr.size(); i++)
                    if(arr.get(i).getUserID().contains(in.getStringExtra("id"))) {
                        verbose(arr.get(i).getUserID());
                        ConstantsService.getUndecidedStudents().add(arr.remove(i));
                        break;
                    }
                error("CORRECT");
                mUndecided.setImageResource(R.drawable.ic_thumbs_undecided_filled_green);
                DataService.sharedInstance().setInterestForStudent(in.getStringExtra("id"), "Undecided");
                showShortToast("Set as Undecided");
            }
        });
        mLinearLayout.addView(mLike);
        mLinearLayout.addView(mUndecided);
        mLinearLayout.addView(mUnlike);
    }

    private void setResume(){
        final String url = getIntent().getStringExtra("resume");
        if(!url.contains("https://firebasestorage.googleapis.com")){
            mResume.setBackgroundResource(R.drawable.button_oval_shape_grey);
        }
        mResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!url.contains("https://firebasestorage.googleapis.com")){
                    createAlertDialog(IAPStudentProfile.this)
                            .setTitle("Resume Not Available")
                            .setPositiveButton("Dismiss", null).show();
                } else {
                    Intent in = new Intent(IAPStudentProfile.this, PosterViewer.class);
                    in.putExtra("url", student.getResumeURL());
                    in.putExtra("name", student.getName());
                    startActivity(in);
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
