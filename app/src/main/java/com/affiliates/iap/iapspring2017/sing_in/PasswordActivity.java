package com.affiliates.iap.iapspring2017.sing_in;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.affiliates.iap.iapspring2017.BaseActivity;
import com.affiliates.iap.iapspring2017.Constants;
import com.affiliates.iap.iapspring2017.Models.Advisor;
import com.affiliates.iap.iapspring2017.Models.CompanyUser;
import com.affiliates.iap.iapspring2017.Models.IAPStudent;
import com.affiliates.iap.iapspring2017.Models.UPRMAccount;
import com.affiliates.iap.iapspring2017.Models.User;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.services.AccountAdministration;
import com.affiliates.iap.iapspring2017.services.DataService;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordActivity extends BaseActivity {
    private static boolean done;
    private static final String TAG = "PasswordActivity";
    private AccountAdministration mAdmin;
    private EditText mPassword;
    private EditText mConfirm;
    private ImageView passShow;
    private ImageView confShow;
    private Button mBack;
    private Button mNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        mAdmin = new AccountAdministration(getBaseContext());
        bind();
        final String email = getIntent().getStringExtra("Email");
        final String accType  = getIntent().getStringExtra("AccountType");
        Log.v(TAG, email + " " + accType);

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
                    final Intent in = new Intent(PasswordActivity.this, NameAndGender.class);
                    in.putExtra("email", email);
                    in.putExtra("accType", accType);
                    in.putExtra("password", mPassword.getText().toString());
                    startActivity(in);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                } else if (mPassword.getText().toString().length() == 0){
                    Toast.makeText(getApplicationContext(), "Please enter a password", Toast.LENGTH_SHORT).show();
                }else if (mConfirm.getText().toString().length() == 0){
                    Toast.makeText(getApplicationContext(), "Please confirm password", Toast.LENGTH_SHORT).show();
                }  else if( error == 1 ) {
                    Toast.makeText(getApplicationContext(), "Passwords don't match, please try again", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Sorry, password must have at least 6 characters", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mPassword.requestFocus();


        passShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPassword.getTransformationMethod() != null) {
                    mPassword.setTransformationMethod(null);
                    passShow.setImageResource(R.drawable.ic_hide_pass);
                }else {
                    mPassword.setTransformationMethod(new PasswordTransformationMethod());
                    passShow.setImageResource(R.drawable.ic_show_password);
                }
            }
        });


        confShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mConfirm.getTransformationMethod() != null) {
                    mConfirm.setTransformationMethod(null);
                    confShow.setImageResource(R.drawable.ic_hide_pass);
                }else {
                    mConfirm.setTransformationMethod(new PasswordTransformationMethod());
                    confShow.setImageResource(R.drawable.ic_show_password);
                }
            }
        });
    }

    private void bind(){
        mPassword = (EditText) findViewById(R.id.edit_password);
        mConfirm = (EditText) findViewById(R.id.edit_confirm_password);
        mBack = (Button) findViewById(R.id.backButton);
        mNext = (Button) findViewById(R.id.nextButton);
        passShow = (ImageView) findViewById(R.id.imageView18);
        confShow = (ImageView) findViewById(R.id.imageView21);
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

    @Override
    protected void onPause() {
        super.onPause();
        hideProgressDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }
}
