package com.affiliates.iap.iapspring2017;

import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by gsantiago on 02-19-17.
 */

public class RegisterActivity extends BaseActivity {

   private static final String TAG = "RegiterActivity";

   private EditText mNameField;
   private EditText mEmailFiel;
   private EditText mPasswordField;

   private FirebaseAuth mAuth;
   private FirebaseAuth.AuthStateListener mAuthListener;

}
