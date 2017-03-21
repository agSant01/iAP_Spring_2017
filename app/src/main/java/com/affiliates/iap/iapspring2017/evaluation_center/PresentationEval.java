//
//  PresentationEval.java
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
import android.widget.RatingBar;

import com.affiliates.iap.iapspring2017.R;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class PresentationEval extends Fragment{
    private static final String TAG = "PresentationEval";
    private static int pos;

    static MaterialRatingBar mTechOral;
    static MaterialRatingBar mPresOral;
    static RatingBar mMethodOral;
    static MaterialRatingBar mResOral;
    private Button mSave;
    private View mView;

    public static PresentationEval newInstance(int ps) {
        pos = ps;
        return new PresentationEval();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        mView = inflater.inflate(R.layout.evaluation_presentation, container, false);

        mTechOral = (MaterialRatingBar) mView.findViewById(R.id.pres_rating_tech);
        mMethodOral = (RatingBar) mView.findViewById(R.id.pres_rating_method);
        mPresOral = (MaterialRatingBar) mView.findViewById(R.id.pres_rating_pres);
        mResOral = (MaterialRatingBar) mView.findViewById(R.id.pres_rating_res);

        mSave = (Button) mView.findViewById(R.id.button4);

        mTechOral.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                Summary.mTechOral.setText((int)rating+"");
                Summary.calculateTotalOral();
            }
        });
        mMethodOral.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Summary.mMethodOral.setText((int) rating+"");
                Summary.calculateTotalOral();
            }
        });
        mPresOral.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                Summary.mPresOral.setText((int)rating+"");
                Summary.calculateTotalOral();
            }
        });
        mResOral.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                Summary.mResOral.setText((int)rating+"");
                Summary.calculateTotalOral();
            }
        });
        updateOralRating();

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Summary.saveVote(getContext());
            }
        });

        return mView;
    }

    static void updateOralRating(){
        mPresOral.setRating(Summary.mCompanyVote.getTechnicalScore().presentation);
        mResOral.setRating(Summary.mCompanyVote.getResultsScore().presentation);
        mTechOral.setRating(Summary.mCompanyVote.getTechnicalScore().presentation);
        mMethodOral.setRating(Summary.mCompanyVote.getMethodologyScore().presentation);
    }
}
