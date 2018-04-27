//
//  DebugMethods.java
//  IAP
//
//  Created by Gabriel S. Santiago on 10/03/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Extensions;

import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

public class DebugMethods {

    public void verbose(String msg){ Log.v(getClass().getSimpleName(), msg); }

    public void debug(String msg){ Log.d(getClass().getSimpleName(), msg); }

    public void error(String msg) { Log.e(getClass().getSimpleName(), msg); }

    public void error(String msg, Throwable t){
        Log.e(getClass().getSimpleName(), msg, t);
    }

    public void FCLog(String msg) {
        FirebaseCrash.log(getClass().getSimpleName() + ": " + msg);
    }
}
