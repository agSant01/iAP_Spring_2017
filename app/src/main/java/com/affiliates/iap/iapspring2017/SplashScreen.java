//
//  SplashScreen.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/07/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.affiliates.iap.iapspring2017.Models.User;
import com.affiliates.iap.iapspring2017.activities.MainActivity;
import com.affiliates.iap.iapspring2017.activities.NoConnectionActivity;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.services.AccountAdministration;
import com.affiliates.iap.iapspring2017.services.Client;
import com.affiliates.iap.iapspring2017.services.DataService;
import com.affiliates.iap.iapspring2017.sing_in.LogInOrRegister;
import com.google.firebase.auth.FirebaseAuthException;

public class SplashScreen extends Activity {
    private static final String TAG = "SplashScreen";
    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        setContentView(R.layout.splash_);

        final Client client = new Client(getBaseContext());
        final AccountAdministration aa = new AccountAdministration(getBaseContext());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!client.isConnectionAvailable()){
                    Intent in = new Intent(SplashScreen.this , NoConnectionActivity.class);
                    startActivity(in);
                    finish();
                } else {
                    String id = aa.getUserID();
                    if(id != null && !id.equals("")){
                        Log.v(TAG, "UserID from Memory succesfull");
                        DataService.sharedInstance().getUserData(id, new Callback<User>() {
                            @Override
                            public void success(User user) {
                                Log.v(TAG, "Get user data successfull");
                                Constants.setCurrentLogedInUser(user);

                                Intent in = new Intent(SplashScreen.this, MainActivity.class);
                                startActivity(in);
                                finish();
                            }

                            @Override
                            public void failure(String message) {
                                Log.e(TAG, "Failed get user data");
                                if(message.contains("No user ID Registered"))
                                    try {
                                        Constants.getCurrentLoggedInUser().logOut(getBaseContext());
                                    } catch (FirebaseAuthException e) {
                                        e.printStackTrace();
                                    }
                                Log.v(TAG,"No user signed in.");
                                Intent in = new Intent(SplashScreen.this, LogInOrRegister.class);
                                startActivity(in);
                                finish();
                            }
                        });
                    } else {
                        Log.v(TAG,"No user signed in.");
                        Intent in = new Intent(SplashScreen.this, LogInOrRegister.class);
                        startActivity(in);
                        finish();
                    }
                }
            }
        },1000);
    }
}
