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
import com.affiliates.iap.iapspring2017.activities.NoConnectionActivity;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.services.AccountAdministration;
import com.affiliates.iap.iapspring2017.services.Client;
import com.affiliates.iap.iapspring2017.services.DataService;
import com.affiliates.iap.iapspring2017.sing_in.SignInActivity;
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
                String id = aa.getUserID();
                if(!client.isConnectionAvailable()){
                    Intent in = new Intent(SplashScreen.this , SignInActivity.class);
                    if (id != null && !id.equals(""))
                        in.putExtra("splash", "second");
                    else
                        in.putExtra("splash","first");
                    startActivity(in);
                    finish();
                } else {
                    if(id != null && !id.equals("")){
                        if(id.equals("second")){
                            Log.v(TAG,"No user signed in.");
                            Intent in = new Intent(SplashScreen.this, SignInActivity.class);
                            in.putExtra("splash", "second");
                            startActivity(in);
                            finish();
                        }else {
                            Log.v(TAG, "Getting user data");
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
                                    Log.e(TAG, "Failed get data curentRegisteringUserData");
                                    if (message.contains("No user ID Registered"))
                                        try {
                                            Constants.getCurrentLoggedInUser().logOut(getBaseContext());
                                        } catch (FirebaseAuthException e) {
                                            e.printStackTrace();
                                        } catch (NullPointerException exep) {
                                            Log.v(TAG, "No one has logged in yet!");
                                        }
                                    Log.v(TAG, "No user signed in.");
                                    Intent in = new Intent(SplashScreen.this, SignInActivity.class);
                                    in.putExtra("splash", "second");
                                    startActivity(in);
                                    finish();
                                }
                            });
                        }
                    } else {
                        Log.v(TAG,"First launch");
                        Intent in = new Intent(SplashScreen.this, SignInActivity.class);
                        in.putExtra("splash", "first");
                        startActivity(in);
                        finish();
                    }
                }
            }
        },1500);
    }
}
