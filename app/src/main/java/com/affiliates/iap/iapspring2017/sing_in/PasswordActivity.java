//
//  PasswordActivity.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/07/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

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
import com.affiliates.iap.iapspring2017.R;

public class PasswordActivity extends BaseActivity {
    private static final String TAG = "PasswordActivity";
    private EditText mPassword, mConfirm;
    private ImageView passShow, confShow;
    private Button mBack, mNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        bind();
        setBack();
        setNext();
        mPassword.requestFocus();
        setPasswordShow();
        setConfirmationShow();
    }

    /**
     * Binds all the UI components to variables for use
     */
    private void bind(){
        mPassword = (EditText) findViewById(R.id.edit_password);
        mConfirm = (EditText) findViewById(R.id.edit_confirm_password);
        mBack = (Button) findViewById(R.id.backButton);
        mNext = (Button) findViewById(R.id.nextButton);
        passShow = (ImageView) findViewById(R.id.imageView18);
        confShow = (ImageView) findViewById(R.id.imageView21);
    }

    /**
     * Adds function for the back button
     */
    private void setBack(){
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     * Adds function for the back button
     */
    private void setNext(){
        // Gets email and accountType passed through the intent from the previous activities
        final String email = getIntent().getStringExtra("Email");
        final String accType  = getIntent().getStringExtra("AccountType");
        // adds the on click listener
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int error = validatePassword(mPassword.getText().toString(), mConfirm.getText().toString());
                // validation of error
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
        Log.v(TAG, email + " " + accType);
    }

    /**
     * Set on click listener for the password show icon
     */
    private void setPasswordShow(){
        passShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPassword.getTransformationMethod() != null) {                     // pass is visible
                    mPassword.setTransformationMethod(null);                          // set invisible
                    passShow.setImageResource(R.drawable.ic_hide_pass);               // change icon
                }else {                                                                     // is invisible
                    mPassword.setTransformationMethod(new PasswordTransformationMethod());  // set visible
                    passShow.setImageResource(R.drawable.ic_show_password);                 // change icon
                }
            }
        });
    }

    /**
     * Set on click listener for the confirm password show icon
     */
    private void setConfirmationShow(){
        confShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mConfirm.getTransformationMethod() != null) {                     // pass is visible
                    mConfirm.setTransformationMethod(null);                          // set invisible
                    confShow.setImageResource(R.drawable.ic_hide_pass);              // change icon
                }else {                                                                       // is invisible
                    mConfirm.setTransformationMethod(new PasswordTransformationMethod());     // set visible
                    confShow.setImageResource(R.drawable.ic_show_password);                   // change icon
                }
            }
        });
    }

    /**
     * Used to validate the password
     * @param pass password
     * @param confirm password confirmation
     * @return state
     */
    public int validatePassword(String pass, String confirm){
        if(!pass.contentEquals(confirm)) return 1;  // are equal
        if(pass.length()<6) return 2;               // less than 6 characters
        return 0;                                   // not valid
    }

    /**
     * Executed when the native back button of the android is clicked or
     * otherwise called
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.go_back_out, R.anim.go_back_in);   // animate as sliding to the right
        finish();                                                           // finish this activity
    }

    /**
     * Executed when the view is moved to the background
     */
    @Override
    protected void onPause() {
        super.onPause();
        hideProgressDialog();               // hide progress dialog if it is active
    }

    /**
     * Executed when the activity is destroyed
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }
}
