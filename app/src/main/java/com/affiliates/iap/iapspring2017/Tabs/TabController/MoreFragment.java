//
//  MoreFragment.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Tabs.TabController;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.affiliates.iap.iapspring2017.Services.ConstantsService;
import com.affiliates.iap.iapspring2017.Models.IAPStudent;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.Tabs.MoreTab.Sponsors.CompanyListActivity;
import com.affiliates.iap.iapspring2017.Tabs.MoreTab.FeedBackActivity;
import com.affiliates.iap.iapspring2017.Tabs.MoreTab.LocationActivity;
import com.affiliates.iap.iapspring2017.NoConnectionActivity;
import com.affiliates.iap.iapspring2017.Tabs.MoreTab.ProfileEditActivity;
import com.affiliates.iap.iapspring2017.Tabs.MoreTab.StudentsOfInterest.StudentsOfInterestActivity;
import com.affiliates.iap.iapspring2017.Services.Client;
import com.affiliates.iap.iapspring2017.Authentication.SignInActivity;
import com.google.firebase.auth.FirebaseAuthException;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.affiliates.iap.iapspring2017.Models.User.AccountType.CompanyUser;

public class MoreFragment extends Fragment {
    private static final String TAG = "MoreFragment";

    private CircleImageView mCircleImageView;
    private LinearLayout mLinearLayout;
    private TextView mEditProfile;
    private TextView mCompanies;
    private TextView mUserName;
    private TextView mProjects;
    private TextView mStudInt;
    private TextView mEmail;
    private TextView mFeedBack;
    private TextView mLocation;
    private Button mSignOut;
    private View mRootView;
    private String mTester;

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
        mRootView = inflater.inflate(R.layout.fragment_more, container, false);
        this.bind();
        final Client client = new Client(getContext());
        mTester = ConstantsService.getCurrentLoggedInUser().getPhotoURL();

        String url = "NA"; //this can't be empty!
        String projects = "";
        switch (ConstantsService.getCurrentLoggedInUser().getAccountType()) {
            case CompanyUser:
                url = ConstantsService.getCurrentLoggedInUser().getPhotoURL();
                break;
            case Advisor:
                url = ConstantsService.getCurrentLoggedInUser().getPhotoURL();
                break;

            case IAPStudent:
                IAPStudent student = (IAPStudent) ConstantsService.getCurrentLoggedInUser();
                url = student.getPhotoURL();
                int i;
                for(i = 0; i < student.getProjectNames().size()-1; i++){
                    projects += student.getProjectNames().get(i) + ", ";
                }
                projects += student.getProjectNames().get(i);
                mProjects.setText(projects);
                break;
        }

        mEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!client.isConnectionAvailable()){
                    startActivity(new Intent(getActivity().getBaseContext(), NoConnectionActivity.class));
                    return;
                }
                Intent in = new Intent(getActivity(), ProfileEditActivity.class );
                startActivity(in);
            }
        });

        Picasso.with(getContext()).load(url).placeholder(R.drawable.ic_gender_0)
                .error(R.drawable.ic_gender_0).into(mCircleImageView);
        mUserName.setText(ConstantsService.getCurrentLoggedInUser().getName().equals("NA") ? "Please specify name" : ConstantsService.getCurrentLoggedInUser().getName());
        mEmail.setText(ConstantsService.getCurrentLoggedInUser().getEmail());
        mProjects.setText(projects);

        if(ConstantsService.getCurrentLoggedInUser().getAccountType() == CompanyUser){
            View v = new View(getContext());
            v.setBackgroundColor(getResources().getColor(R.color.darkGrey));
            v.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, 2));

            mStudInt = new TextView(getContext());
            mStudInt.setText("Students of Interest");
            mStudInt.setGravity(Gravity.CENTER);
            mStudInt.setTextColor(getResources().getColor(R.color.appGreen));
            mStudInt.setGravity(mCompanies.getGravity());
            mStudInt.setHeight(mCompanies.getHeight());

            float scale = getResources().getDisplayMetrics().density;
            mStudInt.setLayoutParams
                    (new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (60.0f * scale + 0.5f)));

            mStudInt.setTextSize(18);
            mLinearLayout.addView(mStudInt, 1);
            mLinearLayout.addView(v, 1);

            mStudInt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!client.isConnectionAvailable()){
                        startActivity(new Intent(getActivity().getBaseContext(), NoConnectionActivity.class));
                        return;
                    }
                    Intent in = new Intent(getActivity(), StudentsOfInterestActivity.class );
                    startActivity(in);
                }
            });
        }

        mCompanies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!client.isConnectionAvailable()){
                    startActivity(new Intent(getActivity().getBaseContext(), NoConnectionActivity.class));
                    return;
                }
                Intent in = new Intent(getActivity(), CompanyListActivity.class );
                startActivity(in);
            }
        });


        mLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!client.isConnectionAvailable()){
                    startActivity(new Intent(getActivity().getBaseContext(), NoConnectionActivity.class));
                    return;
                }
                Intent in = new Intent(getActivity(), LocationActivity.class);
                startActivity(in);
            }
        });

        mFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!client.isConnectionAvailable()){
                    startActivity(new Intent(getActivity().getBaseContext(), NoConnectionActivity.class));
                    return;
                }
                Intent in = new Intent(getActivity(), FeedBackActivity.class);
                startActivity(in);
            }
        });

        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!client.isConnectionAvailable()){
                    startActivity(new Intent(getActivity().getBaseContext(), NoConnectionActivity.class));
                    return;
                }
                new AlertDialog.Builder(getActivity())
                        .setTitle("Sign Out")
                        .setMessage("Do you want to sign out?")
                        .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            ConstantsService.getCurrentLoggedInUser().logOut(getContext());
                        } catch (FirebaseAuthException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Error login Out", Toast.LENGTH_LONG).show();
                        }
                        Intent in = new Intent(getActivity(), SignInActivity.class);
                        in.putExtra("splash", "second");
                        startActivity(in);
                        getActivity().finish();
                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                }).show();
            }
        });

        return mRootView;
    }

    private void bind(){
        mCircleImageView = (CircleImageView) mRootView.findViewById(R.id.profile_image);
        mLinearLayout = (LinearLayout) mRootView.findViewById(R.id.linear_layout);
        mCompanies = (TextView) mRootView.findViewById(R.id.more_companies);
        mSignOut = (Button) mRootView.findViewById(R.id.button_sign_out);
        mEditProfile = (TextView) mRootView.findViewById(R.id.edit_text);
        mEmail = (TextView) mRootView.findViewById(R.id._department);
        mProjects = (TextView) mRootView.findViewById(R.id._project);
        mUserName = (TextView) mRootView.findViewById(R.id._name);
        mLocation = (TextView) mRootView.findViewById(R.id.more_location);
        mFeedBack = (TextView) mRootView.findViewById(R.id.more_feedback);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mTester!=null && !mTester.equals(ConstantsService.getCurrentLoggedInUser().getPhotoURL())){
            mTester = ConstantsService.getCurrentLoggedInUser().getPhotoURL();
            Picasso.with(getContext()).load(mTester).placeholder(R.drawable.ic_gender_0)
                    .error(R.drawable.ic_gender_0).into(mCircleImageView);
            Log.v(TAG, ConstantsService.getCurrentLoggedInUser().getName());
        }
        mUserName.setText(ConstantsService.getCurrentLoggedInUser().getName().equals("NA") ? "Please specify name" :  ConstantsService.getCurrentLoggedInUser().getName());
    }
}
