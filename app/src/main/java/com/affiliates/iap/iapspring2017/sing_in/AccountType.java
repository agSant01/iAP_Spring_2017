//
//  AccountType.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/28/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.sing_in;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.affiliates.iap.iapspring2017.BaseActivity;
import com.affiliates.iap.iapspring2017.Constants;
import com.affiliates.iap.iapspring2017.Models.UPRMAccount;
import com.affiliates.iap.iapspring2017.Models.User;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.services.DataService;

public class AccountType extends BaseActivity {
    private static final String TAG = "AccountType";

    private TextView mIAPStudent;
    private TextView mAdvisor;
    private TextView mCompany;
    private TextView mGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_type);
        bind();

         mCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "CompanyRep");
                verify(User.AccountType.CompanyUser);
            }
        });

        mIAPStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "IAPStudent");
                verify(User.AccountType.IAPStudent);
            }
        });

        mGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "UPRMAccount");
                verify(User.AccountType.UPRMAccount);
            }
        });

       mAdvisor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Advisor");
                verify(User.AccountType.Advisor);
            }
        });
    }

    private void bind(){
        mGuest = (TextView) findViewById(R.id.studentProfessorButton);
        mIAPStudent = (TextView) findViewById(R.id.iapStudentButton);
        mCompany = (TextView) findViewById(R.id.companyButton);
        mAdvisor = (TextView) findViewById(R.id.advisorButton);
    }

    private void verify(final User.AccountType accountType){
        showProgressDialog("Validating Email");
        final Intent intent = new Intent(AccountType.this, PasswordActivity.class);
        final String email = getIntent().getStringExtra("Email");
        intent.putExtra("AccountType", accountType.toString());
        Log.v(TAG, email);
        intent.putExtra("Email", email);

        if(accountType != User.AccountType.UPRMAccount){
            DataService.sharedInstance().verifyUser(accountType, email , new Callback<User>() {
                @Override
                public void success(User data) {
                    hideProgressDialog();
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                }

                @Override
                public void failure(String message) {
                    hideProgressDialog();
                    String acc = "";
                    switch (accountType){
                        case CompanyUser:
                            acc = "company";
                            break;
                        case Advisor:
                            acc = "advisor";
                            break;
                        case IAPStudent:
                            acc = "IAP student";
                            break;
                    }
                    Toast.makeText(getApplicationContext(), "Sorry, email not registered for " + acc, Toast.LENGTH_LONG).show();
                    onBackPressed();
                }
            });
        } else {
            hideProgressDialog();
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.go_back_out, R.anim.go_back_in);
        finish();
    }
}
