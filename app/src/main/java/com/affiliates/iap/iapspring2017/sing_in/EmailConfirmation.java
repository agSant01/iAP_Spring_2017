package com.affiliates.iap.iapspring2017.sing_in;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmailConfirmation.this, MainActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                finish();
            }
        });
    }



    public void onBackPressed(){
        Intent in = new Intent(EmailConfirmation.this, LogInOrRegister.class);
        startActivity(in);
        overridePendingTransition(R.anim.go_back_out, R.anim.go_back_in);
        finish();
    }
}
