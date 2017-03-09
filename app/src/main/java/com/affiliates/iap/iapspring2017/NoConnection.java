package com.affiliates.iap.iapspring2017;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;

import com.affiliates.iap.iapspring2017.services.AccountAdministration;
import com.affiliates.iap.iapspring2017.services.Client;

public class NoConnection extends Activity{
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
                if (mClient.isConnectionAvailable()){
                    if(mAccAdmin.getUserID() != null){
                        Intent in = new Intent(NoConnection.this, MainActivity.class);
                        startActivity(in);
                        finish();
                    } else {
                        Intent in = new Intent(NoConnection.this, SignInActivity.class);
                        startActivity(in);
                        finish();
                    }

                    Snackbar.make(findViewById(R.id.no_conection), Tmessage, Snackbar.LENGTH_SHORT).show();
                    Intent in = new Intent(NoConnection.this, RegisterActivity.class);
                    startActivity(in);
                    finish();
                } else {
                    //There is no internet yet.
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
