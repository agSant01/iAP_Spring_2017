//
//  Summary.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/11/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.evaluation_center;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.affiliates.iap.iapspring2017.Constants;
import com.affiliates.iap.iapspring2017.Models.CompanyUser;
import com.affiliates.iap.iapspring2017.Models.CompanyVote;
import com.affiliates.iap.iapspring2017.Models.Poster;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.services.Client;
import com.affiliates.iap.iapspring2017.services.DataService;
import com.google.gson.Gson;

import java.io.FileOutputStream;
import java.io.IOException;

public class Summary extends Fragment{
    private static final String TAG = "Summary";

    static TextView mProjectName;
    static TextView mTechPoster;
    static TextView mPresPoster;
    static TextView mMethodPoster;
    static TextView mResPoster;
    static TextView mTotalPoster;
    static TextView mTechOral;
    static TextView mPresOral;
    static TextView mMethodOral;
    static TextView mResOral;
    static TextView mTotalOral;

    static Button mCancel;
    static Button mSubmit;

    static CompanyVote mCompanyVote;
    static String mPosterID;
    static CompanyUser companyUser;

    private static int pos;

    public static Summary newInstance(int p){
        pos = p;
        return new Summary();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        View view = inflater.inflate(R.layout.evaluation_summary, container, false);
        final Client client = new Client(getContext());
        mProjectName = (TextView) view.findViewById(R.id.evaluation_summary_project_name);

        mTechPoster = (TextView) view.findViewById(R.id.sumary_tech_poster);
        mMethodPoster = (TextView) view.findViewById(R.id.sumary_method_poster);
        mResPoster = (TextView) view.findViewById(R.id.sumary_res_poster);
        mPresPoster = (TextView) view.findViewById(R.id.sumary_presentation_poster);
        mTotalPoster = (TextView) view.findViewById(R.id.sumary_total_poster);

        mPresOral = (TextView) view.findViewById(R.id.sumary_presentation_oral);
        mTechOral = (TextView) view.findViewById(R.id.sumary_tech_oral);
        mMethodOral = (TextView) view.findViewById(R.id.sumary_method_oral);
        mResOral = (TextView) view.findViewById(R.id.sumary_res_oral);
        mTotalOral = (TextView) view.findViewById(R.id.sumary_total_oral);

        mSubmit = (Button) view.findViewById(R.id.button3);

        Intent in = getActivity().getIntent();
        mPosterID = in.getStringExtra("posterID");

        companyUser = (CompanyUser) Constants.getCurrentLoggedInUser();
        final Poster poster = Constants.getPosters().get(mPosterID);
        mCompanyVote = companyUser.loadVote(mPosterID, getContext());

        mProjectName.setText(poster.getProjectName());

        Log.v(TAG, mCompanyVote.getTechnicalScore().poster+"");

        mTechPoster.setText(mCompanyVote.getTechnicalScore().poster+"");
        mMethodPoster.setText(mCompanyVote.getMethodologyScore().poster+"");
        mResPoster.setText(mCompanyVote.getResultsScore().poster+"");
        mPresPoster.setText(mCompanyVote.getPresentationScore().poster+"");
        mTotalPoster.setText(mCompanyVote.getPosterTotal()+"");

        mTechOral.setText(mCompanyVote.getTechnicalScore().presentation+"");
        mMethodOral.setText(mCompanyVote.getMethodologyScore().presentation+"");
        mResOral.setText(mCompanyVote.getResultsScore().presentation+"");
        mPresOral.setText(mCompanyVote.getPresentationScore().presentation+"");
        mTotalOral.setText(mCompanyVote.getPresentationTotal()+"");

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveVote(getContext());
                new AlertDialog.Builder(getContext())
                        .setTitle("Confirm Evaluation")
                        .setMessage("Are you sure you want to submit this evaluation?")
                        .setPositiveButton("CONFIRM",  new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int which) {
                                if(!client.isConnectionAvailable()){
                                    Toast.makeText(getContext(), "Connection error, make sure you are connected",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                ((EvaluationActivity) getActivity()).showProgressDialog("Submitting Evaluation");
                                DataService.sharedInstance().submitCompanyEval(mCompanyVote,
                                        new Callback<Object>() {
                                    @Override
                                    public void success(Object data) {
                                        Log.v(TAG, "Evaluation submissson was good!");
                                        ((CompanyUser) Constants.getCurrentLoggedInUser()).setVoted(mPosterID);
                                        mCompanyVote.removeVoteFromMemory(getContext());
                                        ((EvaluationActivity) getActivity()).hideProgressDialog();
                                        Toast.makeText(getContext(), "Submission succesful",Toast.LENGTH_SHORT).show();
                                        getActivity().onBackPressed();
                                    }

                                    @Override
                                    public void failure(String message) {
                                        Log.v(TAG, message);
                                        ((EvaluationActivity) getActivity()).hideProgressDialog();
                                        Toast.makeText(getContext(), "Error on submission, try again shortly",Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });
                            }
                        }).setNegativeButton("Cancel", null).create().show();
            }
        });
        return view;
    }

    static void calculateTotalOral(){
        int sum = Integer.parseInt(mMethodOral.getText().toString()) +
                Integer.parseInt(mTechOral.getText().toString()) +
                Integer.parseInt(mResOral.getText().toString()) +
                Integer.parseInt(mPresOral.getText().toString());
        mTotalOral.setText(Integer.toString(sum));
    }

    static void calculateTotalPoster(){
        int sum = Integer.parseInt(mMethodPoster.getText().toString()) +
                Integer.parseInt(mTechPoster.getText().toString()) +
                Integer.parseInt(mResPoster.getText().toString()) +
                Integer.parseInt(mPresPoster.getText().toString());
        mTotalPoster.setText(Integer.toString(sum));
    }

    static boolean saveVote(Context context){
        mCompanyVote.getTechnicalScore().presentation = Integer.parseInt(mTechOral.getText().toString());
        mCompanyVote.getMethodologyScore().presentation = Integer.parseInt(mMethodOral.getText().toString());
        mCompanyVote.getResultsScore().presentation =  Integer.parseInt(mResOral.getText().toString());
        mCompanyVote.getPresentationScore().presentation =  Integer.parseInt(mPresOral.getText().toString());
        mCompanyVote.setPresentationTotal( mCompanyVote.getTechnicalScore().presentation +
                mCompanyVote.getMethodologyScore().presentation +
                mCompanyVote.getResultsScore().presentation +
                mCompanyVote.getPresentationScore().presentation );

        mCompanyVote.getTechnicalScore().poster = Integer.parseInt(mTechPoster.getText().toString());
        mCompanyVote.getMethodologyScore().poster = Integer.parseInt(mMethodPoster.getText().toString());
        mCompanyVote.getResultsScore().poster =  Integer.parseInt(mResPoster.getText().toString());
        mCompanyVote.getPresentationScore().poster =  Integer.parseInt(mPresPoster.getText().toString());
        mCompanyVote.setPosterTotal( mCompanyVote.getTechnicalScore().poster +
                mCompanyVote.getMethodologyScore().poster +
                mCompanyVote.getResultsScore().poster +
                mCompanyVote.getPresentationScore().poster);

        Gson gson = new Gson();

        String jsonComapanyVote = gson.toJson(mCompanyVote);
        Log.v(TAG, jsonComapanyVote);

        try {
            FileOutputStream fo = context.openFileOutput(mPosterID, Context.MODE_PRIVATE);
            fo.write(jsonComapanyVote.getBytes());
            fo.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Summary.class -> saveVote(), " + e);
        }
        return true;
    }
}
