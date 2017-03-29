//
//  MoreFragment.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.tabs;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.affiliates.iap.iapspring2017.CompanyList;
import com.affiliates.iap.iapspring2017.Constants;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.sing_in.LogInOrRegister;
import com.google.firebase.auth.FirebaseAuthException;

public class MoreFragment extends Fragment {
    private static final String TAG = "MoreFragment";

    private Button mSignOut;
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

        mSignOut = (Button) view.findViewById(R.id.button_sign_out);
        mCompanies = (TextView) view.findViewById(R.id.more_companies);
        mStudInt = (TextView) view.findViewById(R.id.more_students_interest);
        mCompanies = (TextView) view.findViewById(R.id.more_companies);

        mCompanies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), CompanyList.class );
                startActivity(in);
            }
        });
//
//        mStudInt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent in = new Intent(getActivity(), StudentsOfInterest.class );
//                startActivity(in);
//            }
//        });
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
