//
//  RegisterActivitys.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.sing_in;

import android.widget.EditText;

import com.affiliates.iap.iapspring2017.BaseActivity;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends BaseActivity {

   private static final String TAG = "RegiterActivity";

   private EditText mNameField;
   private EditText mEmailFiel;
   private EditText mPasswordField;

   private FirebaseAuth mAuth;
   private FirebaseAuth.AuthStateListener mAuthListener;

}