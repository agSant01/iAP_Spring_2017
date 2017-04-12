package com.affiliates.iap.iapspring2017.sing_in;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.affiliates.iap.iapspring2017.BaseActivity;
import com.affiliates.iap.iapspring2017.Models.User;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.services.AccountAdministration;
import com.affiliates.iap.iapspring2017.services.DataService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ObjectOutputStream;

public class EmailConfirmation extends BaseActivity {
    private static final String TAG = "EmailConfirmation";

    private AccountAdministration mAdmin;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_confirmation);

        mAdmin = new AccountAdministration(getBaseContext());
        mFirebaseAuth = FirebaseAuth.getInstance();

        Button done = (Button) findViewById(R.id.doneButton);

        String email = getIntent().getStringExtra("Email"),
                password = getIntent().getStringExtra("Pass"),
                name = getIntent().getStringExtra("Name"),
                accType = getIntent().getStringExtra("AccountType"),
                userType = getIntent().getStringExtra("UserType");

        registerUser(email, password, accType);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmailConfirmation.this, SignInActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                finish();
            }
        });
    }

    private void registerUser(final String email, final String password, final String accType){
        showProgressDialog("Creating Account");
        mFirebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser user = mFirebaseAuth.getCurrentUser();
                    user.sendEmailVerification();
                    mAdmin.saveUserID(user.getUid());
                    DataService.sharedInstance().registerUser(email, mAdmin.getUserID(), accType, new Callback() {
                        @Override
                        public void success(Object data) {
                            Log.v(TAG, "User registration successful");
                        }

                        @Override
                        public void failure(String message) {
                            Log.v(TAG, message);
                        }
                    });
                }
                else
                    Toast.makeText(getApplicationContext(), "Something went wrong. Please, try again", Toast.LENGTH_SHORT).show();
                hideProgressDialog();
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
