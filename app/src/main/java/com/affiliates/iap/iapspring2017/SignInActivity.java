//
//  SignInActivity.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.affiliates.iap.iapspring2017.Models.User;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.services.AccountAdministration;

public class SignInActivity extends Activity {

   // View
   private EditText mEmailField;
   private EditText mPasswordField;
   private Button mSubmit;

   private static final String TAG = "SignIn";
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      final AccountAdministration accAdmin = new AccountAdministration(getBaseContext());

      //Set view
      //Set view elements
      //set buttons
      setContentView(R.layout.activity_sign_in);
      mEmailField = (EditText) findViewById(R.id.email_box);
      mPasswordField = (EditText) findViewById(R.id.password_box);

      mSubmit = (Button) findViewById(R.id.sign_in_button);
      mSubmit.setOnClickListener(
            new View.OnClickListener()  {
               @Override
               public void onClick(View view) {
                  String email = mEmailField.getText().toString();
                  String password = mPasswordField.getText().toString();
                  if (email.length() > 0 && password.length() > 0 ){
                     System.out.println( "DATA: " + email +"   " + password);
                     User.login(getBaseContext(), email, password, new Callback<User>(){
                        @Override
                        public void success(User user) {
                           Constants.setCurrentLogedInUser(user);
                           accAdmin.saveUserID(user.getUserID());
                           System.out.println("DATA -> " + Constants.getCurrentLoggedInUser().getName());
                           Intent in = new Intent(SignInActivity.this, MainActivity.class);

                           startActivity(in);
                           finish();
                           overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        }

                        @Override
                        public void failure(String message) {
                           System.err.println(message);
                        }
                     });
                  }
               }
            });



   }
}
