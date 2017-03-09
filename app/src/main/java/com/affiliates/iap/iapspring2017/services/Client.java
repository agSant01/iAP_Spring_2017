package com.affiliates.iap.iapspring2017.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static java.security.AccessController.getContext;

/**
 * Created by gsantiago on 03-07-17.
 */

public class Client {
    private static final String TAG = "Client";
    private Context context;

    /**
     * Creates a client object
     * @param context
     */
    public Client(Context context){
        this.context = context;
    }

    public Context getContext(){
        return this.context;
    }

    public boolean isConnectionAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null &&
                activeNetwork.isConnected());
    }
}
