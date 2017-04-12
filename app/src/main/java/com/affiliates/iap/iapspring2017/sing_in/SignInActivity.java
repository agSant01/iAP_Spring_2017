//
//  SignInActivity.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.sing_in;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.affiliates.iap.iapspring2017.BaseActivity;
import com.affiliates.iap.iapspring2017.Constants;
import com.affiliates.iap.iapspring2017.MainActivity;
import com.affiliates.iap.iapspring2017.Models.User;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.services.AccountAdministration;
import com.affiliates.iap.iapspring2017.services.Client;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class SignInActivity extends BaseActivity {
   private static final String TAG = "SignIn";

   private AccountAdministration mAdmin;
   private EditText mPasswordField;
   private EditText mEmailField;
   private Client mClient;
   private Button mSubmit;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_sign_in);

      this.bind();

      mAdmin = new AccountAdministration(getBaseContext());
      mClient = new Client(getBaseContext());

      mSubmit.setOnClickListener(
            new View.OnClickListener()  {
               @Override
               public void onClick(View view) {
                  String email = mEmailField.getText().toString();
                  String password = mPasswordField.getText().toString();
                  if(!mClient.isConnectionAvailable()){
                     try {
                        Thread.sleep(100);
                     } catch (InterruptedException e) {
                        e.printStackTrace();
                     }
                     hideProgressDialog();
                     Toast.makeText(getBaseContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
                  }else if (email.length() > 0 && password.length() > 0 ){
                     System.out.println( "DATA: " + email +"   " + password);
                     showProgressDialog();

                     User.login(email, password, new Callback<User>(){
                        @Override
                        public void success(User user) {
                           Constants.setCurrentLogedInUser(user);
                           mAdmin.saveUserID(user.getUserID());
                           System.out.println("DATA -> " + Constants.getCurrentLoggedInUser().getName());
                           Intent in = new Intent(SignInActivity.this, MainActivity.class);

                           hideProgressDialog();
                           startActivity(in);
                           overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                           finishAffinity();
                        }

                        @Override
                        public void failure(String message) {
                           String s = "";
                           if(message.contains("password is invalid")){
                              s = "Incorrect PasswordActivity";
                           } else if (message.contains("There is no user record corresponding to this identifier.")){
                              s = "Incorrect Email";
                           } else if (message.contains("badly formatted")){
                              s = "Invalid Email";
                           } else{
                              Log.v(TAG, message);
                           }
                           hideProgressDialog();
                           Log.e(TAG, message);
                           Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                        }
                     });
                  }else if (email.length() == 0 && password.length() == 0) {
                     Toast.makeText(getBaseContext(), "Please, enter credentials", Toast.LENGTH_SHORT).show();
                  }
                  else if (email.length() == 0){
                     Toast.makeText(getBaseContext(), "Invalid Email", Toast.LENGTH_SHORT).show();
                  } else if(password.length() == 0){
                     Toast.makeText(getBaseContext(), "Invalid PasswordActivity", Toast.LENGTH_SHORT).show();
                  }
               }
            });
   }

   private void bind(){
      mPasswordField = (EditText) findViewById(R.id.password_box);
      mEmailField = (EditText) findViewById(R.id.email_box);
      mSubmit = (Button) findViewById(R.id.sign_in_button);
   }

   @Override
   public void onBackPressed() {
      super.onBackPressed();
      overridePendingTransition(R.anim.go_back_out, R.anim.go_back_in);
      finish();
   }
}
