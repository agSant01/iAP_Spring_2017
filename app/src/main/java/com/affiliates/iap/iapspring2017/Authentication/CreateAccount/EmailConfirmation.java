package com.affiliates.iap.iapspring2017.Authentication.CreateAccount;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.Extensions.BaseActivity;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.Tabs.TabController.HomeActivity;

public class EmailConfirmation extends BaseActivity {

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
                startActivity(
                        HomeActivity.class,
                        R.anim.slide_in,
                        R.anim.slide_out
                );
            }
        });
    }
    @Override
    public void onBackPressed(){}
}
