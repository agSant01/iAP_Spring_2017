package com.affiliates.iap.iapspring2017;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.affiliates.iap.iapspring2017.Models.Poster;
import com.affiliates.iap.iapspring2017.Models.Sponsors;
import com.affiliates.iap.iapspring2017.adapters.CompanyListAdapter;
import com.affiliates.iap.iapspring2017.adapters.PosterAdapter;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.services.DataService;

import java.util.ArrayList;
import java.util.HashMap;

public class CompanyList extends BaseActivity{
    private static final String TAG = "ComapanyList";

    private ListView mListView;
    private CompanyListAdapter mCompanyListAdapter;
    private ProgressBar mProgresBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.company_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_company);
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

        mListView = (ListView) findViewById(R.id.company_listview);
        mProgresBar = (ProgressBar) findViewById(R.id.progressBar);

        mProgresBar.setVisibility(ProgressBar.VISIBLE);
        mProgresBar.setVerticalFadingEdgeEnabled(true);
        final AlphaAnimation fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);//fade from 1 to 0 alpha
        fadeOutAnimation.setDuration(1000);
        fadeOutAnimation.setFillAfter(true);

        if(Constants.getSponsor() == null)
            DataService.sharedInstance().getSponsors(new Callback() {
                @Override
                public void success(Object data) {
                    Log.v(TAG,"Get Sponsors Success!");
                    Constants.setSponsor((ArrayList<Sponsors>) data);

                    mCompanyListAdapter = new CompanyListAdapter(getBaseContext(), Constants.getSponsor());
                    for(Poster p : Constants.getPosters().values())
                        Log.v(TAG, p.getProjectName() + "<_");

                    mListView.setAdapter(mCompanyListAdapter);
                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String url = Constants.getSponsor().get(position).getWebsite();
                            Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(in);
                        }
                    });
                    mProgresBar.startAnimation(fadeOutAnimation);
                    mProgresBar.setVisibility(ProgressBar.INVISIBLE);
                }

                @Override
                public void failure(String message) {
                    Log.e(TAG, message);
                }
            });
        else {
            mCompanyListAdapter = new CompanyListAdapter(getBaseContext(), Constants.getSponsor());
            for(Poster p : Constants.getPosters().values())
                Log.v(TAG, p.getProjectName() + "<_");

            mListView.setAdapter(mCompanyListAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String url = Constants.getSponsor().get(position).getWebsite();
                    Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(in);
                }
            });
            mProgresBar.startAnimation(fadeOutAnimation);
            mProgresBar.setVisibility(ProgressBar.INVISIBLE);
        }

    }

}
