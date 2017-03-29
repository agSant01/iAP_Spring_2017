package com.affiliates.iap.iapspring2017.sing_in;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.services.AccountAdministration;
import com.affiliates.iap.iapspring2017.services.DataService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailConfirmation extends AppCompatActivity {
    private static final String TAG = "EmailConfirmation";;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    private AccountAdministration admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        admin = new AccountAdministration(getBaseContext());
        setContentView(R.layout.activity_email_confirmation);
        Button done = (Button) findViewById(R.id.doneButton);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        String email=  getIntent().getStringExtra("Email"),
                pass = getIntent().getStringExtra("Pass"),
                name = getIntent().getStringExtra("Name"),
                accType = getIntent().getStringExtra("AccountType"),
                userType = getIntent().getStringExtra("UserType");

        registerUser(name, email, pass, accType, userType);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmailConfirmation.this, SignInActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                finishActivity(getParent().getParent().getTaskId());
                finishActivity(getParent().getTaskId());
                finish();

            }
        });

    }

    private void registerUser(final String name, final String email, final String password, final String accType, final String userType){
        progressDialog.setMessage("Registering");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    user.sendEmailVerification();
                    admin.saveUserID(user.getUid());
                    DataService.sharedInstance().registerUser(name, email, admin.getUserID(), accType, userType, new Callback() {
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
                    Toast.makeText(getApplicationContext(), "Something went wrong. Try again", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}
