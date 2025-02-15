//
//  PosterDescriptionActivity.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/07/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Tabs.PostersTab.PosterDescription;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.EvaluationCenter.EvaluationActivity;
import com.affiliates.iap.iapspring2017.EvaluationCenter.GeneralVoteActivity;
import com.affiliates.iap.iapspring2017.Extensions.BaseActivity;
import com.affiliates.iap.iapspring2017.Interfaces.Callback;
import com.affiliates.iap.iapspring2017.Models.Advisor;
import com.affiliates.iap.iapspring2017.Models.CompanyUser;
import com.affiliates.iap.iapspring2017.Models.IAPStudent;
import com.affiliates.iap.iapspring2017.Models.Poster;
import com.affiliates.iap.iapspring2017.Models.User;
import com.affiliates.iap.iapspring2017.Profiles.AdvisorProfile;
import com.affiliates.iap.iapspring2017.Profiles.IAPStudentProfile;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.Services.ConstantsService;
import com.affiliates.iap.iapspring2017.Services.DataService;
import com.google.firebase.auth.FirebaseAuth;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;

public class PosterDescriptionActivity extends BaseActivity {
    private TeamAdvisorsAdapter mAdvisorsAdapter;
    private TeamMembersAdapter mStudentAdapter;

    private TwoWayView mStudentScrollView;
    private TwoWayView mAdvisorScrollView;

    private ArrayList<Advisor> mAdvisors;
    private ArrayList<IAPStudent> mTeam;

    private ProgressBar mAdvisorProg;
    private ProgressBar mTeamProg;

    private TextView mNoStudentsRegistered;
    private TextView mNoAdivisorsRegistered;
    private TextView mAbstract;
    private TextView mSeeMore;
    private TextView mTitle;
    private TextView mCat;

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
        mPosterData = ConstantsService.getPosters().get(id);
        mAdvisors = new ArrayList<>();
        mTeam = new ArrayList<>();

        showProgressBar(mAdvisorProg);
        showProgressBar(mTeamProg);

        if(mPosterData.getPosterURL().equals("NA")) {
            mPosterImg.setImageResource(R.drawable.ic_poster_icon);
            mPoster.setBackgroundResource(R.drawable.button_oval_shape_grey);
        }

        mTitle.setText(mPosterData.getProjectName());
        String p = "";
        for(int i = 0; i < mPosterData.getCategories().size()-1; i++){
            p += mPosterData.getCategories().get(i) + ", ";
        }
        p += mPosterData.getCategories().get(mPosterData.getCategories().size()-1);
        mCat.setText(p);
        seeLess();
        //update to see if user can vote
        DataService.sharedInstance().getUserData( ConstantsService.getCurrentLoggedInUser().getUserID(), new Callback<User>() {
            @Override
            public void success(User data) {
                ConstantsService.setCurrentLogedInUser(data);
                //get the latest status on voted
                verbose("User updated");
            }

            @Override
            public void failure(String message) {
                FCLog(message);
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
                verbose(mPosterData.getPosterURL());
                String url = mPosterData.getPosterURL();
                if(!url.contains("https://firebasestorage.googleapis.com")){
                    createAlertDialog(PosterDescriptionActivity.this)
                            .setTitle("Poster Not Available")
                            .setPositiveButton("Dismiss", null)
                            .show();
                }else{
                    Intent in = new Intent(PosterDescriptionActivity.this, PosterViewer.class);
                    verbose(mPosterData.getPosterURL());
                    in.putExtra("url", mPosterData.getPosterURL());
                    startActivity(in);
                }
            }
        });

        if(ConstantsService.getCurrentLoggedInUser().getAccountType() == User.AccountType.CompanyUser){
            CompanyUser companyUser = (CompanyUser) ConstantsService.getCurrentLoggedInUser();
            setCompanyEvaluation(companyUser);
        } else {
            setFavoriteEvaluation(ConstantsService.getCurrentLoggedInUser());
        }
        DataService.sharedInstance().getPosterTeamMembers(mPosterData, new Callback<ArrayList<IAPStudent>>() {
            @Override
            public void success(final ArrayList<IAPStudent> data) {
                mTeam = data;
                mStudentAdapter.addAll(mTeam);
                hideProgressBar(mTeamProg);
            }

            @Override
            public void failure(String message) {
                hideProgressBar(mTeamProg);
                setVisible(mNoStudentsRegistered);
                error(message);
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
                hideProgressBar(mAdvisorProg);
                setVisible(mNoAdivisorsRegistered);
                error(message);
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
        mNoAdivisorsRegistered = (TextView) findViewById(R.id._no_advisors);
        mNoStudentsRegistered = (TextView) findViewById(R.id._no_students);
        mAdvisorProg = (ProgressBar) findViewById(R.id.advisor_progress);
        mAbstract = (TextView) findViewById(R.id.poster_desc_abstract);
        mTeamProg = (ProgressBar) findViewById(R.id.member_progress);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_poster_desc);
        mVoteButton = (Button) findViewById(R.id.button_evaluate);
        mTitle = (TextView) findViewById(R.id.poster_desc_title);
        mPosterImg = (ImageView) findViewById(R.id.poster_image);
        mSeeMore = (TextView) findViewById(R.id.seeMoreButton);
        mVoteImg = (ImageView) findViewById(R.id.presentation_image);
        mPoster = (Button) findViewById(R.id.button_poster);
        mCat = (TextView) findViewById(R.id.textView20);
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
        FirebaseAuth.getInstance().getCurrentUser().reload();
        if(!user.hasEvaluated(mPosterData.getPosterID())){
            mVoteButton.setText("Evaluate");
            mVoteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
                        createAlertDialog(PosterDescriptionActivity.this)
                                .setTitle("Email Vertification Required")
                                .setMessage("You must first verify your email to be able to vote for this project. "
                                + "Would you like us to resend you the email verification?")
                                .setPositiveButton("RESEND", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                                    }
                                }).setNegativeButton("NO", null).create().show();
                    } else {
                        Intent eval = new Intent(getBaseContext(), EvaluationActivity.class);
                        eval.putExtra("posterID", mPosterData.getPosterID());
                        startActivity(
                                eval,
                                R.anim.slide_in,
                                R.anim.slide_out
                        );
                    }
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
                            .setTitle("Poster Alerady Evaluated")
                            .setMessage("You can only evaluate a poster only once")
                            .setPositiveButton("Dismiss", null)
                            .show();
                }
            });
        }
    }

    private  void setFavoriteEvaluation(final User user){
        mVoteButton.setText("Favorite");
        if(mPosterData.getPosterID().equals("IAP")){
            mVoteImg.setImageResource(R.drawable.ic_thumb_up_grey);
            mVoteButton.setBackgroundResource(R.drawable.button_oval_shape_grey);
            mVoteButton.setText("Disabled");
            mVoteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   createAlertDialog(PosterDescriptionActivity.this)
                            .setTitle("Disabled")
                            .setMessage("This poster is not available for voting in this category. Sorry for the inconvenience")
                            .setPositiveButton("Dismiss", null)
                            .show();
                }
            });
        } else if(!(user.hasVoted(0) && user.hasVoted(1))){
            mVoteImg.setImageResource(R.drawable.ic_thumb_up_filled_green);
            mVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().getCurrentUser().reload();
                if(!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
                    AlertDialog alertDialog = createAlertDialog(PosterDescriptionActivity.this)
                            .setTitle("Email Vertification Required")
                            .setMessage("You must first verify your email to be able to vote for this project. "
                                    + "Would you like us to resend you the email verification?")
                            .setPositiveButton("RESEND", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                                }
                            })
                            .setNegativeButton("NO", null)
                            .create();
                    alertDialog.show();
                }else {
                    Intent intent = new Intent(PosterDescriptionActivity.this, GeneralVoteActivity.class);
                    intent.putExtra("posterID", mPosterData.getPosterID());
                    intent.putExtra("posterName", mPosterData.getProjectName());
                    verbose("QUEPASA?");
                    startActivity(
                            intent,
                            R.anim.slide_in,
                            R.anim.slide_out
                    );
                }
            }});
        }
        else {
            mVoteImg.setImageResource(R.drawable.ic_thumb_up_grey);
            mVoteButton.setText("Favorited");
            mVoteButton.setBackgroundResource(R.drawable.button_oval_shape_grey);
            mVoteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createAlertDialog(PosterDescriptionActivity.this)
                            .setTitle("Unable to Vote")
                            .setMessage("Sorry, you have already spent your votes")
                            .setPositiveButton("Dismiss", null)
                            .show();
                }
            });
        }
    }

    private void seeMore(){
        if(mPosterData.get_abstract().length() <= 300 ){
            setVisible(mSeeMore);
        }
        mAbstract.setText(mPosterData.get_abstract().equals("NA") ? "Abstract currently unavailable" : mPosterData.get_abstract());
        mSeeMore.setText("See less");
    }

    private void seeLess(){
        if(mPosterData.get_abstract().length() <= 300 ){
            setInvisible(mSeeMore);
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
        FirebaseAuth.getInstance().getCurrentUser().reload();
        if(ConstantsService.getCurrentLoggedInUser().getAccountType() == User.AccountType.CompanyUser){
            setCompanyEvaluation((CompanyUser) ConstantsService.getCurrentLoggedInUser());
        } else {
            setFavoriteEvaluation(ConstantsService.getCurrentLoggedInUser());
        }
        verbose("onResume()");
    }

    @Override
    public void onStop() {
        super.onStop();
        verbose("STOP");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        verbose("DESTROY");
    }
}
