//
//  StudentsOfInterestActivity.java
//  IAP
//
//  Created by Gabriel S. Santiago on 4/07/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Tabs.MoreTab.StudentsOfInterest;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.Extensions.BaseActivity;
import com.affiliates.iap.iapspring2017.Interfaces.Callback;
import com.affiliates.iap.iapspring2017.Models.IAPStudent;
import com.affiliates.iap.iapspring2017.NoConnectionActivity;
import com.affiliates.iap.iapspring2017.Profiles.IAPStudentProfile;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.Services.Client;
import com.affiliates.iap.iapspring2017.Services.ConstantsService;
import com.affiliates.iap.iapspring2017.Services.DataService;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.HashMap;

public class StudentsOfInterestActivity extends BaseActivity {
    private static final String TAG = "StudentsOfInterest";

    private Client client;
    private FloatingActionButton mFloatingButtonInt, mFloatingButtonUnd, mFloatingButtonUnl;
    private FloatingActionMenu mFloatingActionMenu;
    private StudentsInterestAdapter mStudentAdapter;
    private TextView mToolBarText, mNoInterest;
    private AlphaAnimation mFadeOutAnimation;
    private ProgressBar mProgressBar;
    private ListView mListView;
    private Toolbar mToolbar;
    private String mState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout._listview_activity);
        bind();
        setToolbar();
        setFloatingMenu();
        setProgressBar();
        showProgressBar(mProgressBar);
        client = new Client(getBaseContext());

        if ( ConstantsService.getLikedStudents() == null ){
            DataService.sharedInstance().getIAPStudentsOfInterest(new Callback<HashMap<String, ArrayList<IAPStudent>>>() {
                @Override
                public void success(HashMap<String, ArrayList<IAPStudent>> data) {
                    Log.v(TAG, "Get Successfull");
                    ConstantsService.setUndecidedStudents(data.get("undecided"));
                    ConstantsService.setLikedStudents(data.get("liked"));
                    ConstantsService.setUnlikedStudents(data.get("unliked"));
                    setListView("Interested");
                }

                @Override
                public void failure(String message) {
                    Log.e(TAG, message);
                }
            });
        } else {
            setListView("Interested");
        }

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(!client.isConnectionAvailable()){
                    startActivity(NoConnectionActivity.class);
                    return true;
                }
                final IAPStudent student = (IAPStudent) mListView.getItemAtPosition(position);
                final ArrayList<String> a = new ArrayList<String>(){{
                    add("Interested");
                    add("Undecided");
                    add("Not Interested");
                }};
                final CharSequence[] c = new CharSequence[2];
                a.remove(mState);
                a.toArray(c);
                createAlertDialog(StudentsOfInterestActivity.this)
                        .setTitle("Change Interest")
                        .setItems(c, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                verbose("TEST: " +c[which]);
                                switch (mState){
                                    case "Undecided":
                                        ConstantsService.getUndecidedStudents().remove(student);
                                        break;
                                    case "Not Interested":
                                        ConstantsService.getUnlikedStudents().remove(student);
                                        break;
                                    default:
                                        ConstantsService.getLikedStudents().remove(student);
                                        break;
                                }
                                if(c[which].equals("Undecided")){
                                    ConstantsService.getUndecidedStudents().add(student);
                                    DataService.sharedInstance()
                                            .setInterestForStudent(student.getUserID(), "Undecided");
                                } else if ( c[which].equals("Not Interested")){
                                    ConstantsService.getUnlikedStudents().add(student);
                                    DataService.sharedInstance()
                                            .setInterestForStudent(student.getUserID(), "Unlike");
                                } else if ( c[which].equals("Interested")){
                                    ConstantsService.getLikedStudents().add(student);
                                    DataService.sharedInstance()
                                            .setInterestForStudent(student.getUserID(), "Like");
                                }
                                setListView(mState);
                            }
                        })
                        .setNegativeButton("CANCEL", null)
                        .show();
                return true;
            }
        });
    }


    private void bind() {
        mFloatingActionMenu = (FloatingActionMenu) findViewById(R.id.menu);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mToolBarText = (TextView) findViewById(R.id.toolbar_title);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_company);
        mNoInterest = (TextView) findViewById(R.id.empty_list);
        mNoInterest.setVisibility(View.INVISIBLE);
        mListView = (ListView) findViewById(R.id._listview);
        mState = "Interested";
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

        mToolBarText.setText("Interested");
    }

    private void setListView(String status){
        if (mStudentAdapter == null) {
            mStudentAdapter = new StudentsInterestAdapter(getBaseContext(), new ArrayList<IAPStudent>());
        }
        mStudentAdapter.clear();
        switch (status){
            case "Interested":
                if (ConstantsService.getLikedStudents() == null)
                    mStudentAdapter.addAll(new ArrayList<IAPStudent>());
                else
                    mStudentAdapter.addAll(ConstantsService.getLikedStudents());
                break;
            case "Not Interested":
                if (ConstantsService.getUnlikedStudents() == null)
                    mStudentAdapter.addAll(new ArrayList<IAPStudent>());
                else
                    mStudentAdapter.addAll(ConstantsService.getUnlikedStudents());
                break;
            case "Undecided":
                if (ConstantsService.getUndecidedStudents() == null)
                    mStudentAdapter.addAll(new ArrayList<IAPStudent>());
                else
                    mStudentAdapter.addAll(ConstantsService.getUndecidedStudents());
                break;
        }
        mListView.setAdapter(mStudentAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!client.isConnectionAvailable()){
                    startActivity(NoConnectionActivity.class);
                    return;
                }
                IAPStudent student = mStudentAdapter.getItem(position);
                Intent in = new Intent(StudentsOfInterestActivity.this, IAPStudentProfile.class);
                in.putStringArrayListExtra("projects", student.getProjectNames());
                in.putExtra("photoURL", student.getPhotoURL());
                in.putExtra("resume", student.getResumeURL());
                in.putExtra("gradDate", student.getGradDate());
                in.putExtra("dpt", student.getDepartment());
                in.putExtra("bio", student.getObjective());
                in.putExtra("email", student.getEmail());
                in.putExtra("name", student.getName());
                in.putExtra("id",student.getUserID());
                startActivity(in);
            }
        });

        if (mListView.getCount() == 0){
            emptyListAlert(true);
        } else {
            emptyListAlert(false);
            hideProgressBar(mProgressBar);
        }
    }

    private void setFloatingMenu(){
        mFloatingButtonInt = (FloatingActionButton) mFloatingActionMenu.getChildAt(2);
        mFloatingButtonUnd = (FloatingActionButton) mFloatingActionMenu.getChildAt(1);
        mFloatingButtonUnl = (FloatingActionButton) mFloatingActionMenu.getChildAt(0);

        mFloatingActionMenu.setIconAnimated(false);
        setVisible(mFloatingActionMenu);
        mFloatingActionMenu.setClosedOnTouchOutside(true);

        mFloatingButtonInt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verbose("PASSED1");
                mState = "Interested";
                mToolBarText.setText(mState);
                setListView(mState);
                mFloatingActionMenu.close(true);
                mFloatingActionMenu
                        .getMenuIconView()
                        .setImageResource(R.drawable.ic_thumb_up_white);
            }
        });

        mFloatingButtonUnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "PASSED2");
                mState = "Undecided";
                mToolBarText.setText(mState);
                setListView(mState);
                mFloatingActionMenu.close(true);
                mFloatingActionMenu
                        .getMenuIconView()
                        .setImageResource(R.drawable.ic_thumbs_undecided_white);
            }
        });

        mFloatingButtonUnl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mState = "Not Interested";
                mToolBarText.setText(mState);
                mFloatingActionMenu
                        .getMenuIconView()
                        .setImageResource(R.drawable.ic_thumb_down_white);
                setListView(mState);
                mFloatingActionMenu.close(true);
            }
        });
    }

    private void setProgressBar(){
        setVisible(mProgressBar);
        mProgressBar.setVerticalFadingEdgeEnabled(true);
        mFadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);//fade from 1 to 0 alpha
        mFadeOutAnimation.setDuration(1000);
        mFadeOutAnimation.setFillAfter(true);
    }

    private void emptyListAlert(boolean show){
        if(!show)
            setInvisible(mNoInterest);
        else
            setVisible(mNoInterest);

        String msg = "";
        switch (mState){
            case "Interested":
                msg = "Empty Interest";
                break;
            case "Not Interested":
                msg = "Empty Not Interest";
                break;
            case "Undecided":
                msg = "Empty Undecided";
            break;
        }
        mNoInterest.setText(msg);
        hideProgressBar(mProgressBar);
    }

    @Override
    public void onStop() {
        verbose("onStop()");
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mStudentAdapter != null) {
            mStudentAdapter.notifyDataSetChanged();
            setListView(mState);
            verbose("Passed By");
        }
        verbose("onResume() " + mStudentAdapter );
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
