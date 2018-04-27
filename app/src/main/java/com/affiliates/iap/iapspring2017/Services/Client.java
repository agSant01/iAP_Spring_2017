//
//  Client.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/7/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Client {
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
