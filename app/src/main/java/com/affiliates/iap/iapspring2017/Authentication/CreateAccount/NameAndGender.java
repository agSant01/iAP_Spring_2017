package com.affiliates.iap.iapspring2017.Authentication.CreateAccount;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.affiliates.iap.iapspring2017.Extensions.BaseActivity;
import com.affiliates.iap.iapspring2017.Interfaces.Callback;
import com.affiliates.iap.iapspring2017.Models.Advisor;
import com.affiliates.iap.iapspring2017.Models.CompanyUser;
import com.affiliates.iap.iapspring2017.Models.IAPStudent;
import com.affiliates.iap.iapspring2017.Models.UPRMAccount;
import com.affiliates.iap.iapspring2017.Models.User;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.Services.AccountAdministration;
import com.affiliates.iap.iapspring2017.Services.ConstantsService;
import com.affiliates.iap.iapspring2017.Services.DataService;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.jaredrummler.materialspinner.MaterialSpinner;


public class NameAndGender extends BaseActivity{
    private MaterialSpinner mGender;
    private Button mRegister, mBack;
    private EditText mName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.name_and_gender);
        bind();
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setGenderSpinner();
        setRegister();
    }

    private void bind(){
        mName = (EditText) findViewById(R.id.edit_password);
        mGender = (MaterialSpinner) findViewById(R.id.edit_confirm_password);
        mRegister = (Button) findViewById(R.id.button4);
        mBack = (Button) findViewById(R.id.backButton);
    }

    private void setGenderSpinner(){
        mGender.setItems("Select Gender" , "Not disclosed", "Male", "Female");
        mGender.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mGender.getItems().contains("Select Gender"))
                    mGender.setItems("Not disclosed", "Male", "Female");
                return false;
            }
        });
    }

    private void setRegister(){
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGender.getSelectedIndex() > -1 && mName.getText().toString().length() > 4){
                    Intent in = getIntent();
                    registerUser(
                            in.getStringExtra("email"),
                            in.getStringExtra("password"),
                            in.getStringExtra("accType")
                    );
                }

            }
        });
    }

    private void registerUser(final String email, final String password, final String accType) {
        showProgressDialog("Creating Account");
        final User user;
        switch (accType){
            case "CompanyUser":
                user = new CompanyUser(ConstantsService.curentRegisteringUserData);
                break;
            case "IAPStudent":
                user = new IAPStudent(ConstantsService.curentRegisteringUserData);
                break;
            case "Advisor":
                user = new Advisor(ConstantsService.curentRegisteringUserData);
                break;
            default:
                user = new UPRMAccount(email);
                break;
        }
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics
                .getInstance(getBaseContext());
        firebaseAnalytics
                .setUserProperty(
                        "Sex",
                        (String) mGender.getItems().get(mGender.getSelectedIndex()));
        firebaseAnalytics
                .setUserProperty("AccountType", accType);
        user.setGender((String) mGender.getItems().get(mGender.getSelectedIndex()));
        user.setName(mName.getText().toString());

        verbose("Password: " + password);
        verbose("Email: " + user.getEmail());
        DataService.sharedInstance().createNewUser(
                user,
                password,
                new Callback<String>() {
                    @Override
                    public void success(String data) {
                        verbose("User registration successful");
                        User.login(email, password, new Callback<User>() {
                            @Override
                            public void success(User data) {
                                hideProgressDialog();
                                ConstantsService.setCurrentLogedInUser(user);
                                new AccountAdministration(getBaseContext())
                                        .saveUserID(user.getUserID());
                                verbose("DATA -> " + ConstantsService.getCurrentLoggedInUser().getName());
                                startActivity(
                                        EmailConfirmation.class,
                                        R.anim.slide_in,
                                        R.anim.slide_out
                                );
                                finishAffinity();
                            }
                            @Override
                            public void failure(String message) {
                                verbose("User.login() -> " + message);
                            }
                        });
                    }
                    @Override
                    public void failure(String message) {
                        verbose(message);
                        hideProgressDialog();
                        showShortToast(message);
                        startActivity(
                                AccountType.class,
                                R.anim.go_back_out,
                                R.anim.go_back_in
                        );
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.go_back_out, R.anim.go_back_in);
    }
}
