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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.affiliates.iap.iapspring2017.activities.CompanyListActivity;
import com.affiliates.iap.iapspring2017.Constants;
import com.affiliates.iap.iapspring2017.Models.Advisor;
import com.affiliates.iap.iapspring2017.Models.CompanyUser;
import com.affiliates.iap.iapspring2017.Models.IAPStudent;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.activities.ProfileEditActivity;
import com.affiliates.iap.iapspring2017.activities.StudentsOfInterestActivity;
import com.affiliates.iap.iapspring2017.sing_in.LogInOrRegister;
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
    private TextView mAbout;
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
        mTester = Constants.getCurrentLoggedInUser().getPhotoURL();

        String url = "NA"; //this can't be empty!
        String projects = "";
        switch (Constants.getCurrentLoggedInUser().getAccountType()) {
            case CompanyUser:
                url = ((CompanyUser)Constants.getCurrentLoggedInUser()).getPhotoURL();
                break;
            case Advisor:
                url = ((Advisor)Constants.getCurrentLoggedInUser()).getPhotoURL();
                break;

            case IAPStudent:
                IAPStudent student = (IAPStudent) Constants.getCurrentLoggedInUser();
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
                Intent in = new Intent(getActivity(), ProfileEditActivity.class );
                startActivity(in);
            }
        });

        Picasso.with(getContext()).load(url).placeholder(R.drawable.ic_gender_0)
                .error(R.drawable.ic_gender_0).into(mCircleImageView);
        mUserName.setText(Constants.getCurrentLoggedInUser().getName());
        mEmail.setText(Constants.getCurrentLoggedInUser().getEmail());
        mProjects.setText(projects);

        if(Constants.getCurrentLoggedInUser().getAccountType() == CompanyUser){
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
                    Intent in = new Intent(getActivity(), StudentsOfInterestActivity.class );
                    startActivity(in);
                }
            });
        }

        mCompanies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), CompanyListActivity.class );
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
                new AlertDialog.Builder(getActivity()).setTitle("Confirmation")
                        .setMessage("Do you want to sign out.").setPositiveButton("yes", new DialogInterface.OnClickListener() {
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
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mTester!=null && !mTester.equals(Constants.getCurrentLoggedInUser().getPhotoURL())){
            mTester = Constants.getCurrentLoggedInUser().getPhotoURL();
            Picasso.with(getContext()).load(mTester).placeholder(R.drawable.ic_gender_0)
                    .error(R.drawable.ic_gender_0).into(mCircleImageView);
        }

    }
}
