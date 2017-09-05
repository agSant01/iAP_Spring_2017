//
//  AccountAdministration.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/7/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.services;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class AccountAdministration {
    private Context context;

    public AccountAdministration(Context context){
        this.context = context;
    }


    private Context getContext(){
        return this.context;
    }

    /**
     * @return userID
     */
    public String getUserID () {
        try {
            FileInputStream fi = getContext().openFileInput("userID");
            BufferedInputStream bis = new BufferedInputStream(fi);
            StringBuilder buffer = new StringBuilder();
            while(bis.available()!= 0){
                char c = (char) bis.read();
                buffer.append(c);
            }
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("AccountAdministration.class : getUserID(), " + e);
        }
        return null;
    }

    /**
     * Saves the user email
     * @param userID
     * @return true if saving was succesfull; false otherwise
     */
    public boolean saveUserID (String userID){
        try {
            FileOutputStream fo = getContext().openFileOutput("userID", Context.MODE_PRIVATE);
            fo.write(userID.getBytes());
            fo.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("AccountAdministration.class -> saveEmail(), " + e);
        }
        return false;
    }
}
