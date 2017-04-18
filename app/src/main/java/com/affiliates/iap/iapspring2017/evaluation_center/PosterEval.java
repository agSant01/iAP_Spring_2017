//
//  PosterEval.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/11/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.evaluation_center;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.Models.CompanyVote;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.tabs.ScheduleFragment;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class PosterEval extends Fragment {
    private static final String TAG = "PosterEval";

    static MaterialRatingBar mMethodPoster;
    static MaterialRatingBar mTechPoster;
    static MaterialRatingBar mPresPoster;
    static MaterialRatingBar mResPoster;

    public static PosterEval newInstance(int p) {
        return new PosterEval();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        View view = inflater.inflate(R.layout.evaluation_poster, container, false);

        mTechPoster = (MaterialRatingBar) view.findViewById(R.id.poster_rating_tech);
        mMethodPoster = (MaterialRatingBar) view.findViewById(R.id.poster_rating_method);
        mPresPoster = (MaterialRatingBar) view.findViewById(R.id.poster_rating_pres);
        mResPoster =(MaterialRatingBar) view.findViewById(R.id.poster_rating_res);

        mTechPoster.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                Summary.mTechPoster.setText((int) rating+"");
                Summary.calculateTotalPoster();
                Summary.saveVote(getContext());
            }
        });
        mPresPoster.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                Summary.mPresPoster.setText((int) rating+"");
                Summary.calculateTotalPoster();
                Summary.saveVote(getContext());
            }
        });
        mMethodPoster.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                Summary.mMethodPoster.setText((int) rating+"");
                Summary.calculateTotalPoster();
                Summary.saveVote(getContext());
            }
        });
        mResPoster.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                Summary.mResPoster.setText((int) rating+"");
                Summary.calculateTotalPoster();
                Summary.saveVote(getContext());
            }
        });
        updateOralRating();
        return view;
    }
    static void updateOralRating(){
        mPresPoster.setRating(Summary.mCompanyVote.getPresentationScore().poster);
        mResPoster.setRating(Summary.mCompanyVote.getResultsScore().poster);
        mTechPoster.setRating(Summary.mCompanyVote.getTechnicalScore().poster);
        mMethodPoster.setRating(Summary.mCompanyVote.getMethodologyScore().poster);
    }
}
