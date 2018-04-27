//
//  ForgotPasswordActivity.java
//  IAP
//
//  Created by Gabriel S. Santiago on 4/20/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Authentication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.Extensions.BaseActivity;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.Services.Client;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends BaseActivity {
    private EditText mEmail;
    private TextView mBack;
    private Button mReset;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_passsword);
        bind();
        setBack();
        setReset();
    }

    private void bind(){
        mEmail = (EditText) findViewById(R.id.email_box);
        mReset = (Button) findViewById(R.id.sign_in_button);
        mBack = (TextView) findViewById(R.id.backButton);
    }

    private void setBack(){
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
    }

    private void setReset(){
        final Client client = new Client(getBaseContext());
        mReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!client.isConnectionAvailable()){
                    showDismissAlertDialog(
                            ForgotPasswordActivity.this,
                            "Network error",
                            "Please connect to network befoore attempting to sign in."
                    );
                } else if(mEmail.getText().length() == 0) {
                    showShortToast("Please, enter your email");
                    return;
                } else if (mEmail.getText().toString().length() > 0){
                    showProgressDialog("Sending email");
                    FirebaseAuth.getInstance()
                            .sendPasswordResetEmail(mEmail.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    hideProgressDialog();
                                    if(task.isSuccessful()){
                                        setResult(RESULT_OK);
                                        onBackPressed();
                                    } else {
                                        String msg = "";
                                        if(task.getException().getMessage().contains("password is invalid")){
                                            msg = "Incorrect Password";
                                        } else if (task.getException().getMessage().contains("There is no user record corresponding to this identifier.")){
                                            msg = "An account with that email does not exist";
                                        } else if (task.getException().getMessage().contains("badly formatted")){
                                            msg = "Invalid Email";
                                        } else if(task.getException().getMessage().contains("INVALID_EMAIL")){
                                            msg = "Invalid Email";
                                        } else{
                                            msg = task.getException().getMessage();
                                        }
                                        showShortToast(msg);
                                    }
                                }
                            });
                }else if (mEmail.getText().toString().length() == 0){
                    showShortToast("Invalid Email");
                }
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
