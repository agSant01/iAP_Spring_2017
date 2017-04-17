//
//  PosterDescriptionActivity.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/07/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.affiliates.iap.iapspring2017.BaseActivity;
import com.affiliates.iap.iapspring2017.Constants;
import com.affiliates.iap.iapspring2017.Models.Advisor;
import com.affiliates.iap.iapspring2017.Models.CompanyUser;
import com.affiliates.iap.iapspring2017.Models.IAPStudent;
import com.affiliates.iap.iapspring2017.Models.Poster;
import com.affiliates.iap.iapspring2017.Models.User;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.adapters.TeamAdvisorsAdapter;
import com.affiliates.iap.iapspring2017.adapters.TeamMembersAdapter;
import com.affiliates.iap.iapspring2017.evaluation_center.EvaluationActivity;
import com.affiliates.iap.iapspring2017.evaluation_center.GeneralVoteActivity;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.profiles.AdvisorProfile;
import com.affiliates.iap.iapspring2017.profiles.IAPStudentProfile;
import com.affiliates.iap.iapspring2017.services.DataService;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;

public class PosterDescriptionActivity extends BaseActivity {
    private static final String TAG = "PosterDescription";

    private TeamAdvisorsAdapter mAdvisorsAdapter;
    private TeamMembersAdapter mStudentAdapter;

    private TwoWayView mStudentScrollView;
    private TwoWayView mAdvisorScrollView;

    private ArrayList<Advisor> mAdvisors;
    private ArrayList<IAPStudent> mTeam;

    private ProgressBar mAdvisorProg;
    private ProgressBar mTeamProg;

    private TextView mAbstract;
    private TextView mSeeMore;
    private TextView mTitle;

    private Button mVoteButton;
    private Button mPoster;

    private ImageView mPosterImg;
    private ImageView mVoteImg;
    private Poster mPosterData;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster_description);

        bind();
        setToolBar();

        String id = getIntent().getStringExtra("posterID");
        mPosterData = Constants.getPosters().get(id);
        mAdvisors = new ArrayList<>();
        mTeam = new ArrayList<>();

        showProgressBar(mAdvisorProg);
        showProgressBar(mTeamProg);

        if(mPosterData.getPosterURL().equals("NA")) {
            mPosterImg.setImageResource(R.drawable.ic_poster_icon);
            mPoster.setBackgroundResource(R.drawable.button_oval_shape_grey);
        }

        mTitle.setText(mPosterData.getProjectName());
        seeLess();
        //update to see if user can vote
        DataService.sharedInstance().getUserData( Constants.getCurrentLoggedInUser().getUserID(), new Callback() {
            @Override
            public void success(Object data) {
                Constants.setCurrentLogedInUser((User) data);
                //get the latest status on voted
                Log.v(TAG, "User updated");
            }

            @Override
            public void failure(String message) {

            }
        });
        mSeeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView seeMore = (TextView) v;
                if(seeMore.getText().toString().toLowerCase().contains("more")){
                    seeMore();
                } else {
                    seeLess();
                }
            }
        });

        mPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, mPosterData.getPosterURL());
                String url = mPosterData.getPosterURL();
                if(!url.contains("https://firebasestorage.googleapis.com")){
                    new AlertDialog.Builder(PosterDescriptionActivity.this)
                            .setMessage("Poster Not Available")
                            .setPositiveButton("OK",  new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {}                // do nothing
                            }).create().show();
                }else{
                    Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(mPosterData.getPosterURL()));
                    startActivity(browser);
                }
            }
        });

        if(Constants.getCurrentLoggedInUser().getAccountType() == User.AccountType.CompanyUser){
            CompanyUser companyUser = (CompanyUser) Constants.getCurrentLoggedInUser();
            setCompanyEvaluation(companyUser);
        } else {
            setFavoriteEvaluation(Constants.getCurrentLoggedInUser());
        }

        DataService.sharedInstance().getPosterTeamMembers(mPosterData, new Callback() {
            @Override
            public void success(final Object data) {
                mTeam = (ArrayList<IAPStudent>) data;
                mStudentAdapter.addAll(mTeam);
                hideProgressBar(mTeamProg);
            }

            @Override
            public void failure(String message) {
                Log.e(TAG,message);
            }
        });

        DataService.sharedInstance().getPosterAdvisorMembers(mPosterData, new Callback<ArrayList<Advisor>>() {
            @Override
            public void success(ArrayList<Advisor> data) {
                mAdvisors = data;
                mAdvisorsAdapter.addAll(mAdvisors);
                hideProgressBar(mAdvisorProg);
            }

            @Override
            public void failure(String message) {
                Log.e(TAG, message);
            }
        });

        mAdvisorsAdapter = new TeamAdvisorsAdapter(PosterDescriptionActivity.this, R.layout.poster_team_member, mAdvisors);
        mStudentAdapter = new TeamMembersAdapter(PosterDescriptionActivity.this, R.layout.poster_team_member, mTeam);

        mAdvisorScrollView.setAdapter(mAdvisorsAdapter);
        mStudentScrollView.setAdapter(mStudentAdapter);

        mStudentScrollView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IAPStudent s = (IAPStudent) mStudentScrollView.getItemAtPosition(position);
                Intent in = new Intent(PosterDescriptionActivity.this, IAPStudentProfile.class);
                in.putStringArrayListExtra("projects", s.getProjectNames());
                in.putExtra("name", s.getName());
                in.putExtra("email", s.getEmail());
                in.putExtra("dpt", s.getDepartment());
                in.putExtra("gradDate", s.getGradDate());
                in.putExtra("photoURL", s.getPhotoURL());
                in.putExtra("bio", s.getObjective());
                in.putExtra("resume", s.getResumeURL());
                in.putExtra("id",s.getUserID());
                startActivity(in);
            }
        });

        mAdvisorScrollView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Advisor a = (Advisor) mAdvisorScrollView.getItemAtPosition(position);
                Intent in = new Intent(PosterDescriptionActivity.this, AdvisorProfile.class);
                in.putExtra("name", a.getName());
                in.putExtra("email", a.getEmail());
                in.putExtra("dpt", a.getDepartment());
                in.putExtra("website", a.getWebPage());
                in.putExtra("photoURL", a.getPhotoURL());
                in.putExtra("research", a.getResearchIntent());
                in.putStringArrayListExtra("projects", a.getProjects());
                startActivity(in);
            }
        });
    }

    private void bind(){
        mAdvisorScrollView = (TwoWayView) findViewById(R.id.poster_team_advisors);
        mStudentScrollView = (TwoWayView) findViewById(R.id.poster_team_members);
        mAdvisorProg = (ProgressBar) findViewById(R.id.advisor_progress);
        mAbstract = (TextView) findViewById(R.id.poster_desc_abstract);
        mTeamProg = (ProgressBar) findViewById(R.id.member_progress);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_poster_desc);
        mVoteButton = (Button) findViewById(R.id.button_evaluate);
        mTitle = (TextView) findViewById(R.id.poster_desc_title);
        mPosterImg = (ImageView) findViewById(R.id.poster_link);
        mSeeMore = (TextView) findViewById(R.id.seeMoreButton);
        mVoteImg = (ImageView) findViewById(R.id.poster_vote);
        mPoster = (Button) findViewById(R.id.button_poster);


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

    private  void setCompanyEvaluation(CompanyUser user){
        if(!user.hasEvaluated(mPosterData.getPosterID())){
            mVoteButton.setText("Evaluate");
            mVoteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent eval = new Intent(getBaseContext(), EvaluationActivity.class);
                    eval.putExtra("posterID", mPosterData.getPosterID());
                    startActivity(eval);
                }
            });
        } else {
            mVoteImg.setImageResource(R.drawable.ic_evaluate);
            mVoteButton.setText("Evaluated");
            mVoteButton.setBackgroundResource(R.drawable.button_oval_shape_grey);
            mVoteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(PosterDescriptionActivity.this)
                            .setMessage("Poster Already Evaluated")
                            .setPositiveButton("OK",  new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {}                // do nothing
                            }).create().show();
                }
            });
        }
    }

    private  void setFavoriteEvaluation(final com.affiliates.iap.iapspring2017.Models.User user){
        mVoteButton.setText("Favorite");
        mVoteImg.setImageResource(R.drawable.ic_thumb_up_filled_green);
        if(!(user.hasVoted(0) && user.hasVoted(1))){
        mVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PosterDescriptionActivity.this, GeneralVoteActivity.class);
                intent.putExtra("posterID", mPosterData.getPosterID());
                intent.putExtra("posterName", mPosterData.getProjectName());
                startActivity(intent);
            }});
        }
        else {
            mVoteImg.setImageResource(R.drawable.ic_thumb_up_grey);
            mVoteButton.setBackgroundResource(R.drawable.button_oval_shape_grey);
            mVoteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v("Mario", "Clicked");
                    Toast.makeText(getApplicationContext(), "Sorry, you have already spent your votes", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void seeMore(){
        if(mPosterData.get_abstract().length() <= 300 ){
            mSeeMore.setVisibility(View.INVISIBLE);
        }
        mAbstract.setText(mPosterData.get_abstract());
        mSeeMore.setText("See less");
    }

    private void seeLess(){
        if(mPosterData.get_abstract().length() <= 300 ){
            mSeeMore.setVisibility(View.INVISIBLE);
            seeMore();
            return;
        }
        if(mPosterData.get_abstract().length()>300)
            mAbstract.setText(mPosterData.get_abstract().substring(0,300) + "...");
        mSeeMore.setText("See more");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Constants.getCurrentLoggedInUser().getAccountType() == User.AccountType.CompanyUser){
            setCompanyEvaluation((CompanyUser) Constants.getCurrentLoggedInUser());
        } else {
            setFavoriteEvaluation(Constants.getCurrentLoggedInUser());
        }
        System.out.println("ON RESUME YEAH YEAH");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG,"STOP");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "DESTROY");
    }
}
