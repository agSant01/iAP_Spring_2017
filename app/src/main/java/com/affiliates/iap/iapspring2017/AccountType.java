package com.affiliates.iap.iapspring2017;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.sing_in.NameActivity;

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
                startActivity(new Intent(AccountType.this, NameActivity.class));
                overridePendingTransition(R.anim.slide_in,
                        R.anim.slide_out);

            }
        });

        iapStudent = (TextView) findViewById(R.id.iapStudentButton);
        iapStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountType.this, NameActivity.class));
                overridePendingTransition(R.anim.slide_in,
                        R.anim.slide_out);

            }
        });

        studProf = (TextView) findViewById(R.id.studentProfessorButton);
        studProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountType.this, NameActivity.class));
                overridePendingTransition(R.anim.slide_in,
                        R.anim.slide_out);

            }
        });

        advisor = (TextView) findViewById(R.id.advisorButton);
        advisor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountType.this, NameActivity.class));
                overridePendingTransition(R.anim.slide_in,
                        R.anim.slide_out);
            }
        });




    }
}
