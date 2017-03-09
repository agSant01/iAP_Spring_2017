//
//  MoreFragment.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.affiliates.iap.iapspring2017.Constants;
import com.affiliates.iap.iapspring2017.MainActivity;
import com.affiliates.iap.iapspring2017.Models.User;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.SignInActivity;
import com.affiliates.iap.iapspring2017.services.DataService;
import com.google.firebase.auth.FirebaseAuthException;

import org.w3c.dom.Text;

public class MoreFragment extends Fragment {
    private static final String TAG = "MoreFragment";

    private Button mSignOut;

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

        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Constants.getCurrentLoggedInUser().logOut(getContext());
                } catch (FirebaseAuthException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error login Out", Toast.LENGTH_LONG).show();
                }
                Intent in = new Intent(getActivity(), SignInActivity.class);
                startActivity(in);
                getActivity().finish();
            }
        });

        return view;
    }

}
