package com.affiliates.iap.iapspring2017.sing_in;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.affiliates.iap.iapspring2017.BaseActivity;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.services.AccountAdministration;
import com.affiliates.iap.iapspring2017.services.DataService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PasswordActivity extends BaseActivity {
    private static boolean done;
    private static final String TAG = "PasswordActivity";
    private AccountAdministration mAdmin;
    private FirebaseAuth mFirebaseAuth;
    private EditText mPassword;
    private EditText mConfirm;
    private Button mBack;
    private Button mNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        mAdmin = new AccountAdministration(getBaseContext());
        mFirebaseAuth = FirebaseAuth.getInstance();
        bind();
        final String email = getIntent().getStringExtra("Email");

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int error = validatePassword(mPassword.getText().toString(), mConfirm.getText().toString());
                if( error == 0  ) {
                    registerUser(email, mPassword.getText().toString(), getIntent().getStringExtra("AccountType"), null);
                    Class nextClass;
                    if(PasswordActivity.done) {
                        nextClass = EmailConfirmation.class;
                    }
                    else{
                        nextClass = EnterEmail.class;
                    }
                    Intent intent = new Intent(PasswordActivity.this, nextClass);
                    if(!PasswordActivity.done)
                        intent.putExtra("AccountType", getIntent().getStringExtra("AccountType"));
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();

                }
                else if( error == 1 ) {
                    Toast.makeText(getApplicationContext(), "Passwords don't match, please try again", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Sorry, password must have at least 6 characters", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void registerUser(final String email, final String password, final String accType, final String company){
        showProgressDialog("Creating Account");
        mFirebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Log.v("Mario", "damn");
                    FirebaseUser user = mFirebaseAuth.getCurrentUser();
                    user.sendEmailVerification();
                    mAdmin.saveUserID(user.getUid());
                    Log.v("Mario", mAdmin.getUserID());
                    DataService.sharedInstance().registerUser(email, mAdmin.getUserID(), accType, company, new Callback() {
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
                else {
                    Toast.makeText(getApplicationContext(), "Something went wrong. Please, try again", Toast.LENGTH_SHORT).show();
                }
                hideProgressDialog();
            }

        });
    }

    private void bind(){
        mPassword = (EditText) findViewById(R.id.edit_password);
        mConfirm = (EditText) findViewById(R.id.edit_confirm_password);
        mBack = (Button) findViewById(R.id.backButton);
        mNext = (Button) findViewById(R.id.nextButton);
    }

    public int validatePassword(String pass, String confirm){
        if(!pass.contentEquals(confirm)) return 1;
        if(pass.length()<6) return 2;
        return 0;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.go_back_out, R.anim.go_back_in);
        finish();
    }
}
