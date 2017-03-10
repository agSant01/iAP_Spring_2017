//
//  PosterDescription.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/07/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.Models.Advisor;
import com.affiliates.iap.iapspring2017.Models.IAPStudent;
import com.affiliates.iap.iapspring2017.Models.Poster;
import com.affiliates.iap.iapspring2017.adapters.TeamMembersAdapter;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.services.DataService;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;

public class PosterDescription extends BaseActivity {
    private static final String TAG = "PosterDescription";
    private ArrayList<IAPStudent> mTeam;
    private ArrayList<Advisor> mAdvisors;
    private TeamMembersAdapter mArrayAdapter;
    private TwoWayView mLVTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster_description);

        String id = getIntent().getStringExtra("posterID");
        final Poster posterData = Constants.getPosters().get(id);

        mLVTest = (TwoWayView) findViewById(R.id.lvItems);
        mTeam = new ArrayList<>();

        DataService.sharedInstance().getPosterTeamMembers(posterData, new Callback() {
            @Override
            public void success(final Object data) {
                mTeam = (ArrayList<IAPStudent>) data;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mArrayAdapter.addAll(mTeam);
                        mArrayAdapter.notifyDataSetChanged();
                        Log.v(TAG, mTeam.size() + "");
                    }
                }).run();
            }

            @Override
            public void failure(String message) {
                Log.e(TAG,message);
            }
        });

        mArrayAdapter = new TeamMembersAdapter(PosterDescription.this, R.layout.team_member, mTeam);
        mLVTest.setAdapter(mArrayAdapter);

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

        TextView title = (TextView) findViewById(R.id.poster_desc_title);
        TextView abstrac = (TextView) findViewById(R.id.poster_desc_abstract);

        abstrac.setText(posterData.get_abstract());
        title.setText(posterData.getProjectName());



    }

    @Override
    public void onStop() {
        System.out.println("khfkuf");
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
