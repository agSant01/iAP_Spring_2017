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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.affiliates.iap.iapspring2017.R;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class PresentationEval extends Fragment{
    private static final String TAG = "PresentationEval";

    static MaterialRatingBar mTechOral;
    static MaterialRatingBar mPresOral;
    static MaterialRatingBar mResOral;
    static RatingBar mMethodOral;
    private ImageView minusTech, plusTech;
    private ImageView minusPres, plusPres;
    private ImageView minusMeth, plusMeth;
    private ImageView minusRD, plusRD;
    private View mView;

    public static PresentationEval newInstance(int ps) {
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
        bind();

        mTechOral = (MaterialRatingBar) mView.findViewById(R.id.pres_rating_tech);
        mMethodOral = (RatingBar) mView.findViewById(R.id.pres_rating_method);
        mPresOral = (MaterialRatingBar) mView.findViewById(R.id.pres_rating_pres);
        mResOral = (MaterialRatingBar) mView.findViewById(R.id.pres_rating_res);

        mTechOral.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mMethodOral.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mPresOral.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mResOral.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        minusPres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresOral.setRating(mPresOral.getRating() - 1);
            }
        });
        minusMeth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMethodOral.setRating(mMethodOral.getRating() - 1);
            }
        });
        minusTech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTechOral.setRating(mTechOral.getRating() - 1);
            }
        });
        minusRD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResOral.setRating(mResOral.getRating() -1);
            }
        });

        plusPres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresOral.setRating(mPresOral.getRating()+1);
            }
        });

        plusMeth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMethodOral.setRating(mMethodOral.getRating()+1);
            }
        });

        plusTech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTechOral.setRating(mTechOral.getRating() + 1);
            }
        });

        plusRD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResOral.setRating(mResOral.getRating() + 1);
            }
        });

        mTechOral.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                Summary.mTechOral.setText((int)rating+"");
                Summary.calculateTotalOral();
                Summary.saveVote(getContext());
            }
        });
        mMethodOral.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Summary.mMethodOral.setText((int) rating+"");
                Summary.calculateTotalOral();
                Summary.saveVote(getContext());
            }
        });
        mPresOral.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                Summary.mPresOral.setText((int)rating+"");
                Summary.calculateTotalOral();
                Summary.saveVote(getContext());
            }
        });
        mResOral.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                Summary.mResOral.setText((int)rating+"");
                Summary.calculateTotalOral();
                Summary.saveVote(getContext());
            }
        });
        updateOralRating();
        return mView;
    }

    private void bind(){
        minusTech = (ImageView) mView.findViewById(R.id.minus_tech_pres);
        minusMeth = (ImageView) mView.findViewById(R.id.minus_meth_pres);
        minusPres = (ImageView) mView.findViewById(R.id.minus_pres_pres);
        minusRD = (ImageView) mView.findViewById(R.id.minus_res_pres);

        plusTech = (ImageView) mView.findViewById(R.id.plus_tech_pres);
        plusMeth = (ImageView) mView.findViewById(R.id.plus_meth_pres);
        plusPres = (ImageView) mView.findViewById(R.id.plus_pres_pres);
        plusRD = (ImageView) mView.findViewById(R.id.plus_res_pres);
    }

    static void updateOralRating(){
        mPresOral.setRating(Summary.mCompanyVote.getTechnicalScore().presentation);
        mResOral.setRating(Summary.mCompanyVote.getResultsScore().presentation);
        mTechOral.setRating(Summary.mCompanyVote.getTechnicalScore().presentation);
        mMethodOral.setRating(Summary.mCompanyVote.getMethodologyScore().presentation);
    }
}
