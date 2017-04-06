//
//  MoreFragment.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.tabs;

import android.app.VoiceInteractor;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.affiliates.iap.iapspring2017.CompanyList;
import com.affiliates.iap.iapspring2017.Constants;
import com.affiliates.iap.iapspring2017.Models.User;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.StudentsOfInterest;
import com.affiliates.iap.iapspring2017.sing_in.LogInOrRegister;
import com.google.firebase.auth.FirebaseAuthException;

public class MoreFragment extends Fragment {
    private static final String TAG = "MoreFragment";

    private Button mSignOut;
    private LinearLayout mLinearLayout;
    private TextView mCompanies;
    private TextView mStudInt;
    private TextView mAbout;

    public static MoreFragment newInstance(){
        return new MoreFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);

        mLinearLayout = (LinearLayout) view.findViewById(R.id.linear_layout);
        mSignOut = (Button) view.findViewById(R.id.button_sign_out);
        mCompanies = (TextView) view.findViewById(R.id.more_companies);

        if(Constants.getCurrentLoggedInUser().getAccountType() == User.AccountType.CompanyUser){
            View v = new View(getContext());
            v.setBackgroundColor(getResources().getColor(R.color.darkGrey));

            v.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, 2));

            mStudInt = new TextView(getContext());
            mStudInt.setText("Students of Interest");
            mStudInt.setGravity(Gravity.CENTER);
            mStudInt.setTextColor(getResources().getColor(R.color.appGreen));
            mStudInt.setGravity(mCompanies.getGravity());
            mStudInt.setHeight(mCompanies.getHeight());
            mStudInt.setLayoutParams
                    (new ViewGroup.MarginLayoutParams
                            (ViewGroup.LayoutParams.MATCH_PARENT, 200));

            mStudInt.setTextSize(18);

            mLinearLayout.addView(mStudInt, 1);
            mLinearLayout.addView(v, 1);

//            mStudInt.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent in = new Intent(getActivity(), StudentsOfInterest.class );
//                    startActivity(in);
//                }
//            });
        }

        mCompanies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), CompanyList.class );
                startActivity(in);
            }
        });

//
//        mAbout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent in = new Intent(getActivity(), About.class);
//                startActivity(in);
//            }
//        });

        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity()).setTitle("Confirmation").setMessage("Do you want to sign out.").setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            Constants.getCurrentLoggedInUser().logOut(getContext());
                        } catch (FirebaseAuthException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Error login Out", Toast.LENGTH_LONG).show();
                        }
                        Intent in = new Intent(getActivity(), LogInOrRegister.class);
                        startActivity(in);
                        getActivity().finish();
                    }
                }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                }).show();
            }
        });

        return view;
    }

}
