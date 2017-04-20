package com.affiliates.iap.iapspring2017.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.affiliates.iap.iapspring2017.BaseActivity;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.services.Client;
import com.affiliates.iap.iapspring2017.sing_in.LogInOrRegister;
import com.affiliates.iap.iapspring2017.sing_in.SignInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends BaseActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_passsword);

        final EditText mEmail = (EditText) findViewById(R.id.email_box);
        Button mRestet = (Button) findViewById(R.id.sign_in_button);
        TextView mBack = (TextView) findViewById(R.id.backButton);
        final Client client = new Client(getBaseContext());

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.go_back_out, R.anim.go_back_in);
                finish();
            }
        });

        mRestet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!client.isConnectionAvailable()){
                    startActivity(new Intent(ForgotPasswordActivity.this, NoConnectionActivity.class));
                    return;
                }else if (mEmail.getText().toString().length() > 0){
                    showProgressDialog("Sending email");
                    FirebaseAuth.getInstance()
                            .sendPasswordResetEmail(mEmail.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    hideProgressDialog();
                                    if(task.isSuccessful()){
                                        setResult(RESULT_OK);
                                        startActivity(new Intent(ForgotPasswordActivity.this, LogInOrRegister.class));
                                        onBackPressed();
                                    } else {
                                        String s = "";
                                        if(task.getException().getMessage().contains("password is invalid")){
                                            s = "Incorrect Password";
                                        } else if (task.getException().getMessage().contains("There is no user record corresponding to this identifier.")){
                                            s = "An account with that email does not exist";
                                        } else if (task.getException().getMessage().contains("badly formatted")){
                                            s = "Invalid Email";
                                        } else if(task.getException().getMessage().contains("INVALID_EMAIL")){
                                            s = "Invalid Email";
                                        } else{
                                            s = task.getException().getMessage();
                                        }
                                        Toast.makeText(getBaseContext(),
                                                s,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else if (mEmail.getText().toString().length() == 0){
                    Toast.makeText(getBaseContext(), "Invalid Email", Toast.LENGTH_SHORT).show();
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
