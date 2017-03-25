package com.affiliates.iap.iapspring2017.sing_in;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.Models.User;
import com.affiliates.iap.iapspring2017.R;

public class AccountType extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_type);

        TextView company, iapStudent, studProf, advisor;

        company = (TextView) findViewById(R.id.companyButton);
        company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountType.this, NameActivity.class);
                intent.putExtra("AccountType", "Company");
                startActivity(intent);
                intent.putExtra("UserType","NA");
                overridePendingTransition(R.anim.slide_in,
                        R.anim.slide_out);

            }
        });

        iapStudent = (TextView) findViewById(R.id.iapStudentButton);
        iapStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountType.this, NameActivity.class);
                intent.putExtra("AccountType", "IAPStudent");
                intent.putExtra("UserType","NA");

                startActivity(intent);
                overridePendingTransition(R.anim.slide_in,
                        R.anim.slide_out);

            }
        });

        studProf = (TextView) findViewById(R.id.studentProfessorButton);
        studProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountType.this,UserType.class);
                intent.putExtra("AccountType","UPRMAccount");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in,
                        R.anim.slide_out);

            }
        });

        advisor = (TextView) findViewById(R.id.advisorButton);
        advisor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountType.this, NameActivity.class);
                intent.putExtra("AccountType","Advisor");
                intent.putExtra("UserType","NA");

                startActivity(intent);
                overridePendingTransition(R.anim.slide_in,
                        R.anim.slide_out);
            }
        });




    }
}
