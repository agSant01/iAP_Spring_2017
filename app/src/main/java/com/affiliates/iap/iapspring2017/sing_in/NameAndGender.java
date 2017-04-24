package com.affiliates.iap.iapspring2017.sing_in;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.jaredrummler.materialspinner.MaterialSpinner;


public class NameAndGender extends BaseActivity{
    final private static String TAG = "NameAndGender";

    private EditText mName;
    private MaterialSpinner mGender;
    private Button mRegister;
    private Button mBack;
    private AccountAdministration mAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.name_and_gender);
        bind();
        mAdmin = new AccountAdministration(getBaseContext());
        mGender.setItems("Select Gender" , "Not disclosed", "Male", "Female");
        mGender.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mGender.getItems().contains("Select Gender"))
                    mGender.setItems("Not disclosed", "Male", "Female");
                return false;
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGender.getSelectedIndex() > -1 && mName.getText().toString().length() > 4){
                    Intent in = getIntent();
                    registerUser(in.getStringExtra("email"), in.getStringExtra("password"),in.getStringExtra("accType"));
                }

            }
        });

    }

    private void bind(){
        mName = (EditText) findViewById(R.id.edit_password);
        mGender = (MaterialSpinner) findViewById(R.id.edit_confirm_password);
        mRegister = (Button) findViewById(R.id.button4);
        mBack = (Button) findViewById(R.id.backButton);
    }

    private void registerUser(final String email, final String password, final String accType) {
        showProgressDialog("Creating Account");
        final User user;
        switch (accType){
            case "CompanyUser":
                user = new CompanyUser(Constants.curentRegisteringUserData);
                break;
            case "IAPStudent":
                user = new IAPStudent(Constants.curentRegisteringUserData);
                break;
            case "Advisor":
                user = new Advisor(Constants.curentRegisteringUserData);
                break;
            default:
                user = new UPRMAccount(email);
                break;
        }
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(getBaseContext());
        firebaseAnalytics.setUserProperty("Sex", (String) mGender.getItems().get(mGender.getSelectedIndex()));
        firebaseAnalytics.setUserProperty("AccountType", accType);
        user.setGender((String) mGender.getItems().get(mGender.getSelectedIndex()));
        user.setName(mName.getText().toString());
        Log.v(TAG,"Password: "+password);
        Log.v(TAG,"Email: " + user.getEmail());
        DataService.sharedInstance().createNewUser(
                user,
                password,
                new Callback<String>() {
                    @Override
                    public void success(String data) {
                        Log.v(TAG, "User registration successful");
                        User.login(email, password, new Callback<User>() {
                            @Override
                            public void success(User data) {
                                hideProgressDialog();
                                Constants.setCurrentLogedInUser(user);
                                mAdmin.saveUserID(user.getUserID());
                                System.out.println("DATA -> " + Constants.getCurrentLoggedInUser().getName());
                                Intent in = new Intent(NameAndGender.this, EmailConfirmation.class);
                                hideProgressDialog();
                                startActivity(in);
                                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                finishAffinity();
                            }
                            @Override
                            public void failure(String message) {
                                Log.v(TAG, "User.login() -> " + message);
                            }
                        });
                    }
                    @Override
                    public void failure(String message) {
                        Log.v(TAG, message);
                        hideProgressDialog();
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(NameAndGender.this, AccountType.class));
                        overridePendingTransition(R.anim.go_back_out, R.anim.go_back_in);
                        finish();
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.go_back_out, R.anim.go_back_in);
        finish();
    }
}
