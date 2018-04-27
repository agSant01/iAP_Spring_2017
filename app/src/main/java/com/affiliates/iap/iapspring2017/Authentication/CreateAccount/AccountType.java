//
//  AccountType.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/28/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Authentication.CreateAccount;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.Extensions.BaseActivity;
import com.affiliates.iap.iapspring2017.Interfaces.Callback;
import com.affiliates.iap.iapspring2017.Models.User;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.Services.DataService;

public class AccountType extends BaseActivity {
    private TextView mIAPStudent, mAdvisor, mCompany, mGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_type);
        bind();
        setButtons();
    }

    private void bind(){
        mGuest = (TextView) findViewById(R.id.studentProfessorButton);
        mIAPStudent = (TextView) findViewById(R.id.iapStudentButton);
        mCompany = (TextView) findViewById(R.id.companyButton);
        mAdvisor = (TextView) findViewById(R.id.advisorButton);
    }

    private void setButtons(){
        mCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verbose("CompanyRep");
                verify(User.AccountType.CompanyUser);
            }
        });

        mIAPStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verbose("IAPStudent");
                verify(User.AccountType.IAPStudent);
            }
        });

        mGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verbose("UPRMAccount");
                verify(User.AccountType.UPRMAccount);
            }
        });

        mAdvisor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verbose("Advisor");
                verify(User.AccountType.Advisor);
            }
        });
    }

    private void verify(final User.AccountType accountType){
        showProgressDialog("Validating Email");
        final Intent intent = new Intent(AccountType.this, PasswordActivity.class);
        final String email = getIntent().getStringExtra("Email");
        intent.putExtra("AccountType", accountType.toString());
        intent.putExtra("Email", email);

        if(accountType != User.AccountType.UPRMAccount){
            DataService.sharedInstance().verifyUser(accountType, email , new Callback<User>() {
                @Override
                public void success(User data) {
                    hideProgressDialog();
                    startActivity(
                            intent,
                            R.anim.slide_in,
                            R.anim.slide_out
                    );
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
                    showShortToast("Sorry, email not registered for " + acc);
                    onBackPressed();
                }
            });
        } else {
            hideProgressDialog();
            startActivity(
                    intent,
                    R.anim.slide_in,
                    R.anim.slide_out
            );
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
