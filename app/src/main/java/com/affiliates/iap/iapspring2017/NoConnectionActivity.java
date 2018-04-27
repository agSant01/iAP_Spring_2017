//
//  NoConnectionActivity.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.affiliates.iap.iapspring2017.Extensions.BaseActivity;
import com.affiliates.iap.iapspring2017.Services.Client;

public class NoConnectionActivity extends BaseActivity {
    private Client mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_connection);
        final String message = "Without Connection";
        final String Tmessage = "Connection Established";

        mClient = new Client(getBaseContext());

        Button tryAgain = (Button) findViewById(R.id.try_again);
        tryAgain.setOnClickListener(new View.OnClickListener() {        // Listener for try again
            @Override
            public void onClick(View v) {
                showProgressDialog();
                if (mClient.isConnectionAvailable()){
                    hideProgressDialog();
                    showShortToast(Tmessage);
                    if(getIntent().getStringExtra("Splash") != null){
                        if(getIntent().getStringExtra("Splash").equals("splash"))
                            startActivity(new Intent(getBaseContext(), SplashScreen.class));
                    }
                    finish();
                } else {
                    //There is no internet yet.
                    hideProgressDialog();
                    showShortToast(message);
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
