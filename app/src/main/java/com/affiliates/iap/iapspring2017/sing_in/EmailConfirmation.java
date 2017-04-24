package com.affiliates.iap.iapspring2017.sing_in;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.BaseActivity;
import com.affiliates.iap.iapspring2017.MainActivity;
import com.affiliates.iap.iapspring2017.R;

public class EmailConfirmation extends BaseActivity {
    private static final String TAG = "EmailConfirmation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_confirmation);
        Button done = (Button) findViewById(R.id.doneButton);

        TextView v = (TextView) findViewById(R.id.textView25);
        v.setText("Please verify your email. By verifying your email you will be able to vote for the projects and reset your password if you need to.");
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmailConfirmation.this, MainActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                finish();
            }
        });
    }


    @Override
    public void onBackPressed(){}
}
