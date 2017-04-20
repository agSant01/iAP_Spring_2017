package com.affiliates.iap.iapspring2017.sing_in;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.BaseActivity;
import com.affiliates.iap.iapspring2017.R;

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

        final Intent intent = new Intent(AccountType.this, EnterEmail.class);

         mCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "CompanyRep");
                intent.putExtra("AccountType", "CompanyUser");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        mIAPStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "IAPStudent");
                intent.putExtra("AccountType", "IAPStudent");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        mGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "UPRMAccount");
                intent.putExtra("AccountType","UPRMAccount");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

       mAdvisor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Advisor");
                intent.putExtra("AccountType","Advisor");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
    }

    private void bind(){
        mGuest = (TextView) findViewById(R.id.studentProfessorButton);
        mIAPStudent = (TextView) findViewById(R.id.iapStudentButton);
        mCompany = (TextView) findViewById(R.id.companyButton);
        mAdvisor = (TextView) findViewById(R.id.advisorButton);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.go_back_out, R.anim.go_back_in);
        finish();
    }
}
