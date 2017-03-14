package com.affiliates.iap.iapspring2017.Utils;

/**
 * Created by mario on 3/13/2017.
 */

public class Utils {
    public static boolean isValidEmail(String email){
        if(email.contains("@upr.edu")) return true;
        return false;
    }
}
