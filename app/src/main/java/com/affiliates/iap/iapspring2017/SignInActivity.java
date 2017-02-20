//
//  SignInActivity.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends BaseActivity{

   // View Elements
   private TextView mStatusTextView;
   private TextView mDetailTextView;
   private EditText mEmailField;
   private EditText mPasswordField;

   private static final String TAG = "SignIn";
   private FirebaseAuth mFirebaseAuth;
   private FirebaseAuth.AuthStateListener mAuthListener;


   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      //Set view

      //Set view elements

      //set buttons

      mFirebaseAuth = FirebaseAuth.getInstance();
      mAuthListener = new FirebaseAuth.AuthStateListener() {
         @Override
         public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
               // User is signed in
               Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
               // User is signed out
               Log.d(TAG, "onAuthStateChanged:signed_out");
            }
         }
      };
   }

   private void sendEmailVerification() {
      // Disable button
      //TODO: ADD UI Element
      //findViewById(R.id.verify_email_button).setEnabled(false);

      // Send verification email
      // [START send_email_verification]
      final FirebaseUser user = mFirebaseAuth.getCurrentUser();
      user.sendEmailVerification()
         .addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               // [START_EXCLUDE]
               // Re-enable button
               //TODO: ADD UI Element
               //findViewById(R.id.verify_email_button).setEnabled(true);

               if (task.isSuccessful()) {
                  Toast.makeText(SignInActivity.this,
                     "Verification email sent to " + user.getEmail(),
                     Toast.LENGTH_SHORT).show();
               } else {
                  Log.e(TAG, "sendEmailVerification", task.getException());
                  Toast.makeText(SignInActivity.this,
                     "Failed to send verification email.",
                     Toast.LENGTH_SHORT).show();
               }
               // [END_EXCLUDE]
            }
         });
      // [END send_email_verification]
   }

   private boolean validateForm() {
      String email = mEmailField.getText().toString();
      String password = mPasswordField.getText().toString();

      if (TextUtils.isEmpty(email)) {
         mEmailField.setError("Required.");
         return false;
      } else {
         mEmailField.setError(null);
      }

      if (TextUtils.isEmpty(password)) {
         mPasswordField.setError("Required.");
         return false;
      } else {
         mPasswordField.setError(null);
      }
      return true;
   }
}
