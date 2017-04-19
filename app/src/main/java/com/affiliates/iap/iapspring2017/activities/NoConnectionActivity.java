//
//  NoConnectionActivity.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;

import com.affiliates.iap.iapspring2017.BaseActivity;
import com.affiliates.iap.iapspring2017.MainActivity;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.services.AccountAdministration;
import com.affiliates.iap.iapspring2017.services.Client;
import com.affiliates.iap.iapspring2017.sing_in.RegisterActivity;
import com.affiliates.iap.iapspring2017.sing_in.SignInActivity;

public class NoConnectionActivity extends BaseActivity {
    private Client mClient;
    private AccountAdministration mAccAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_connection);
        final String message = "Without Connection";
        final String Tmessage = "Conection Established";

        mClient = new Client(getBaseContext());
        mAccAdmin = new AccountAdministration(getBaseContext());

        Button tryAgain = (Button) findViewById(R.id.try_again);
        tryAgain.setOnClickListener(new View.OnClickListener() {                                       // Listener for try again
            @Override
            public void onClick(View v) {
                showProgressDialog();
                if (mClient.isConnectionAvailable()){
                    if(mAccAdmin.getUserID() != null){
                        hideProgressDialog();
                        Intent in = new Intent(NoConnectionActivity.this, MainActivity.class);
                        startActivity(in);
                        finish();
                    } else {
                        hideProgressDialog();
                        Intent in = new Intent(NoConnectionActivity.this, SignInActivity.class);
                        startActivity(in);
                        finish();
                    }
                    hideProgressDialog();
                    Snackbar.make(findViewById(R.id.no_conection), Tmessage, Snackbar.LENGTH_SHORT).show();
                    Intent in = new Intent(NoConnectionActivity.this, RegisterActivity.class);
                    startActivity(in);
                    finish();
                } else {
                    //There is no internet yet.
                    hideProgressDialog();
                    Snackbar.make(findViewById(R.id.no_conection), message, Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        System.out.println("BOOM");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {}                                                                  // disables back button
}
