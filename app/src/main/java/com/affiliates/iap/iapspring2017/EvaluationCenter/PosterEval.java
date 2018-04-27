//
//  PosterEval.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/11/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.EvaluationCenter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.affiliates.iap.iapspring2017.R;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class PosterEval extends Fragment {
    static MaterialRatingBar mMethodPoster;
    static MaterialRatingBar mTechPoster;
    static MaterialRatingBar mPresPoster;
    static MaterialRatingBar mResPoster;
    private ImageView minusTech, plusTech;
    private ImageView minusPres, plusPres;
    private ImageView minusMeth, plusMeth;
    private ImageView minusRD, plusRD;
    private View mRootView;

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
        mRootView = inflater.inflate(R.layout.evaluation_poster, container, false);
        bind();
        setMinuses();
        setPluses();
        setRatingStars();
        updateOralRating();
        return mRootView;
    }

    private void bind(){
        minusTech = (ImageView) mRootView.findViewById(R.id.minus_tech_poster);
        minusMeth = (ImageView) mRootView.findViewById(R.id.minus_meth_poster);
        minusPres = (ImageView) mRootView.findViewById(R.id.minnus_pres_poster);
        minusRD = (ImageView) mRootView.findViewById(R.id.minus_rd_poster);

        plusTech = (ImageView) mRootView.findViewById(R.id.plus_tech_poster);
        plusMeth = (ImageView) mRootView.findViewById(R.id.plus_meth_poster);
        plusPres = (ImageView) mRootView.findViewById(R.id.plus_pres_poster);
        plusRD = (ImageView) mRootView.findViewById(R.id.plus_rd_poster);

        mTechPoster = (MaterialRatingBar) mRootView.findViewById(R.id.poster_rating_tech);
        mMethodPoster = (MaterialRatingBar) mRootView.findViewById(R.id.poster_rating_method);
        mPresPoster = (MaterialRatingBar) mRootView.findViewById(R.id.poster_rating_pres);
        mResPoster =(MaterialRatingBar) mRootView.findViewById(R.id.poster_rating_res);

        mTechPoster.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mMethodPoster.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mPresPoster.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mResPoster.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    private void setMinuses(){
        minusPres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresPoster.setRating(mPresPoster.getRating() - 1);
            }
        });
        minusMeth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMethodPoster.setRating(mMethodPoster.getRating() - 1);
            }
        });
        minusTech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTechPoster.setRating(mTechPoster.getRating() - 1);
            }
        });
        minusRD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResPoster.setRating(mResPoster.getRating() -1);
            }
        });
    }

    private void setPluses(){
        plusPres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresPoster.setRating(mPresPoster.getRating()+1);
            }
        });

        plusMeth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMethodPoster.setRating(mMethodPoster.getRating()+1);
            }
        });

        plusTech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTechPoster.setRating(mTechPoster.getRating() + 1);
            }
        });

        plusRD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResPoster.setRating(mResPoster.getRating() + 1);
            }
        });
    }

    private void setRatingStars(){
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
    }

    static void updateOralRating(){
        mPresPoster.setRating(Summary.mCompanyVote.getPresentationScore().poster);
        mResPoster.setRating(Summary.mCompanyVote.getResultsScore().poster);
        mTechPoster.setRating(Summary.mCompanyVote.getTechnicalScore().poster);
        mMethodPoster.setRating(Summary.mCompanyVote.getMethodologyScore().poster);
    }
}
