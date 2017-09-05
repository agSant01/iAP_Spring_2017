package com.affiliates.iap.iapspring2017.sing_in;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.affiliates.iap.iapspring2017.BaseActivity;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.services.Client;

public class EnterEmail extends BaseActivity {
    private static final String TAG = "EnterEmail";

    private EditText mEmail;
    private TextView mBack, mNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        bind();

        final Client client = new Client(this);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                if (!client.isConnectionAvailable()){
                    new AlertDialog.Builder(EnterEmail.this)
                            .setTitle("Network error")
                            .setMessage("Please connect to network befoore attempting to sign in.")
                            .setPositiveButton("Dismiss", null).create().show();
                    return;
                }
                if (email.length() == 0){
                    hideProgressDialog();
                    Toast.makeText(getApplicationContext(), "Please, enter your email", Toast.LENGTH_SHORT).show();
                    return;
                } else if( !(email.contains("@") && ( email.contains(".com") || email.contains(".edu")) && email.length() > 5)) {
                    hideProgressDialog();
                    Toast.makeText(getApplicationContext(), "Sorry, Invalid Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                final Intent in = new Intent(EnterEmail.this, AccountType.class);
                in.putExtra("Email", email);
                startActivity(in);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
    }

    private void bind(){
        mEmail = (EditText) findViewById(R.id.edit_email);
        mBack = (TextView) findViewById(R.id.backButton);
        mNext = (TextView) findViewById(R.id.nextButton);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.go_back_out, R.anim.go_back_in);
        finish();
    }
}
