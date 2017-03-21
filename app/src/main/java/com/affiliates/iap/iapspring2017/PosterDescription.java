//
//  PosterDescription.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/07/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.Models.Advisor;
import com.affiliates.iap.iapspring2017.Models.CompanyUser;
import com.affiliates.iap.iapspring2017.Models.IAPStudent;
import com.affiliates.iap.iapspring2017.Models.Poster;
import com.affiliates.iap.iapspring2017.Models.User;
import com.affiliates.iap.iapspring2017.adapters.TeamAdvisorsAdapter;
import com.affiliates.iap.iapspring2017.adapters.TeamMembersAdapter;
import com.affiliates.iap.iapspring2017.evaluation_center.EvaluationActivity;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.profiles.AdvisorProfile;
import com.affiliates.iap.iapspring2017.profiles.IAPStudentProfile;
import com.affiliates.iap.iapspring2017.services.DataService;
import com.affiliates.iap.iapspring2017.tabs.PostersFragment;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;

public class PosterDescription extends BaseActivity {
    private static final String TAG = "PosterDescription";

    private ImageView mPoster;
    private ImageView mVote;
    private ArrayList<IAPStudent> mTeam;
    private ArrayList<Advisor> mAdvisors;
    private TeamMembersAdapter mStudentAdapter;
    private TeamAdvisorsAdapter mAdvisorsAdapter;
    private TwoWayView mStudentScrollView;
    private TwoWayView mAdvisorScrollView;
    private TextView mVoteTV;
    private TextView mAbstract;
    private Poster mPosterData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster_description);

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

        String id = getIntent().getStringExtra("posterID");
        mPosterData = Constants.getPosters().get(id);

        mVoteTV = (TextView) findViewById(R.id.textView9);
        mPoster = (ImageView) findViewById(R.id.poster_link);

        mPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, mPosterData.getPosterURL());
                String url = mPosterData.getPosterURL();
                if(!url.contains("https://firebasestorage.googleapis.com")){
                    new AlertDialog.Builder(v.getContext())
                            .setMessage("PosterEval not Available")
                            .setPositiveButton("OK",  new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {}                // do nothing
                            }).show();
                }else{
                    Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(mPosterData.getPosterURL()));
                    startActivity(browser);
                }
            }
        });

        if(Constants.getCurrentLoggedInUser().getAccountType() == User.AccountType.CompanyUser){
            CompanyUser companyUser = (CompanyUser) Constants.getCurrentLoggedInUser();
            mVote = (ImageView) findViewById(R.id.poster_vote);
            if(!companyUser.hasEvaluated(mPosterData.getPosterID())){
                mVoteTV.setText("Evaluate");
                mVote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent eval = new Intent(getBaseContext(), EvaluationActivity.class);
                        eval.putExtra("posterID", mPosterData.getPosterID());
                        startActivity(eval);
                    }
                });
            } else {
                mVote.setImageResource(R.drawable.ic_evaluate);
                mVoteTV.setText("Evaluated");
            }
        } else if (Constants.getCurrentLoggedInUser().getAccountType() == User.AccountType.Advisor){
            Advisor advisor = (Advisor) Constants.getCurrentLoggedInUser();

        } else if (Constants.getCurrentLoggedInUser().getAccountType() == User.AccountType.IAPStudent){
            IAPStudent iapStudent = (IAPStudent) Constants.getCurrentLoggedInUser();
        }


        mStudentScrollView = (TwoWayView) findViewById(R.id.poster_team_members);
        mTeam = new ArrayList<>();
        DataService.sharedInstance().getPosterTeamMembers(mPosterData, new Callback() {
            @Override
            public void success(final Object data) {
                mTeam = (ArrayList<IAPStudent>) data;
                mStudentAdapter.addAll(mTeam);
            }

            @Override
            public void failure(String message) {
                Log.e(TAG,message);
            }
        });

        mAdvisorScrollView = (TwoWayView) findViewById(R.id.poster_team_advisors);
        mAdvisors = new ArrayList<>();
        DataService.sharedInstance().getPosterAdvisorMembers(mPosterData, new Callback() {
            @Override
            public void success(Object data) {
                mAdvisors = (ArrayList<Advisor>) data;
                mAdvisorsAdapter.addAll(mAdvisors);
            }

            @Override
            public void failure(String message) {
                Log.e(TAG, message);
            }
        });

        mStudentAdapter = new TeamMembersAdapter(PosterDescription.this, R.layout.poster_team_member, mTeam);
        mStudentScrollView.setAdapter(mStudentAdapter);

        mAdvisorsAdapter = new TeamAdvisorsAdapter(PosterDescription.this, R.layout.poster_team_member, mAdvisors);
        mAdvisorScrollView.setAdapter(mAdvisorsAdapter);

        mStudentScrollView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IAPStudent s = (IAPStudent) mStudentScrollView.getItemAtPosition(position);
                Intent in = new Intent(PosterDescription.this, IAPStudentProfile.class);
                in.putExtra("name", s.getName());
                in.putExtra("email", s.getEmail());
                in.putExtra("dpt", s.getDepartment());
                in.putExtra("gradDate", s.getGradDate());
                in.putExtra("projectName", mPosterData.getProjectName());
                in.putExtra("photoURL", s.getPhotoURL());
                in.putExtra("bio", s.getObjective());
                in.putExtra("resume", s.getResumeURL());
                startActivity(in);
            }
        });

        mAdvisorScrollView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Advisor a = (Advisor) mAdvisorScrollView.getItemAtPosition(position);
                Intent in = new Intent(PosterDescription.this, AdvisorProfile.class);
                in.putExtra("name", a.getName());
                in.putExtra("email", a.getEmail());
                in.putExtra("dpt", a.getDepartment());
                in.putExtra("website", a.getWebPage());
                in.putExtra("photoURL", a.getPhotoURL());
                in.putExtra("research", a.getResearchIntent());
                in.putStringArrayListExtra("classes", a.getClasses());
                in.putStringArrayListExtra("projects", a.getProjects());
                startActivity(in);
            }
        });


        TextView title = (TextView) findViewById(R.id.poster_desc_title);
        mAbstract = (TextView) findViewById(R.id.poster_desc_abstract);
        mAbstract.setText(mPosterData.get_abstract());

        title.setText(mPosterData.getProjectName());
    }

    @Override
    public void onStop() {
        System.out.println("khfkuf");
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Constants.getCurrentLoggedInUser().getAccountType() == User.AccountType.CompanyUser){
            CompanyUser companyUser = (CompanyUser) Constants.getCurrentLoggedInUser();
            mVote = (ImageView) findViewById(R.id.poster_vote);
            if(companyUser.hasEvaluated(mPosterData.getPosterID())){
                mVoteTV.setText("Evaluate");
                mVote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                mVote.setImageResource(R.drawable.ic_evaluate);
                mVoteTV.setText("Evaluated");
            }
        }
        System.out.println("ON RESUME YEAH YEAH");
    }

    @Override
    public void onDestroy() {
        System.out.println("BOOM");
        super.onDestroy();
        finish();
    }
}
