package com.affiliates.iap.iapspring2017;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.affiliates.iap.iapspring2017.sing_in.SignInActivity;

public class EmailConfirmation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_confirmation);
        Button done = (Button) findViewById(R.id.doneButton);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmailConfirmation.this, SignInActivity.class));
            }
        });

    }
}
