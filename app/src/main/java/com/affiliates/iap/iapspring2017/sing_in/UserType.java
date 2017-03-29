package com.affiliates.iap.iapspring2017.sing_in;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.R;

public class UserType extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);
        TextView student, proff;
        student = (TextView) findViewById(R.id.studentButton);

        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserType.this, NameActivity.class);
                intent.putExtra("AccountType", getIntent().getStringExtra("AccountType"));
                intent.putExtra("UserType", "Student");
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                startActivity(intent);
            }
        });

        proff = (TextView) findViewById(R.id.proffesorButton);

        proff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserType.this, NameActivity.class);
                intent.putExtra("AccountType", getIntent().getStringExtra("AccountType"));
                intent.putExtra("UserType", "Proffesor");
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.go_back_out, R.anim.go_back_in);
        finish();
    }
}
