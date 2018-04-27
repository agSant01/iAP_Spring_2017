package com.affiliates.iap.iapspring2017.Authentication.CreateAccount;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.Extensions.BaseActivity;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.Services.Client;

public class EnterEmail extends BaseActivity {
    private EditText mEmail;
    private TextView mBack, mNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        bind();
        setBackButton();
        setNextButton();
    }

    private void bind(){
        mEmail = (EditText) findViewById(R.id.edit_email);
        mBack = (TextView) findViewById(R.id.backButton);
        mNext = (TextView) findViewById(R.id.nextButton);
    }

    private void setBackButton(){
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setNextButton(){
        final Client client = new Client(this);
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                if (!client.isConnectionAvailable()){
                    showDismissAlertDialog(
                            EnterEmail.this,
                            "Network error",
                            "Please connect to network before attempting to sign in.");
                    return;
                }
                if (email.length() == 0){
                    hideProgressDialog();
                    showShortToast("Please, enter your email");
                    return;
                } else if(validateEmail(email)) {
                    hideProgressDialog();
                    showShortToast("Sorry, Invalid Email");
                    return;
                }
                Intent in = new Intent(EnterEmail.this, AccountType.class)
                        .putExtra("Email", email);
                startActivity(
                        in,
                        R.anim.slide_in,
                        R.anim.slide_out
                );
            }
        });
    }

    private boolean validateEmail(String email){
        return !(email.contains("@")
                && ( email.contains(".com")
                || email.contains(".edu"))
                && email.length() > 5);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.go_back_out, R.anim.go_back_in);
        finish();
    }
}
