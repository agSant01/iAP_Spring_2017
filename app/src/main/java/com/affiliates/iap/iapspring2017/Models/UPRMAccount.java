//
//  UPRMAccount.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Models;

import com.affiliates.iap.iapspring2017.exeptions.InvalidAccountTypeExeption;
import com.affiliates.iap.iapspring2017.exeptions.VoteErrorException;
import com.affiliates.iap.iapspring2017.interfaces.UserDelegate;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gsantiago on 02-19-17.
 */

public class UPRMAccount extends Users implements UserDelegate {
   private UPRMAccountType userType;
   private boolean voted;


   public UPRMAccount(JSONObject data, AccountType accountType, String id)
      throws InvalidAccountTypeExeption, JSONException {
      this(checkType(accountType), data, accountType, id );
   }

   private UPRMAccount(Void n, JSONObject data, AccountType accountType, String id) throws JSONException{
      super(data.getString("Email"), data.getString("Name"), id, accountType);
      this.userType = UPRMAccountType.getAccType(data.getString("UserType"));
      this.voted = data.getBoolean("Voted");

   }

   private static Void checkType(AccountType accountType)
      throws InvalidAccountTypeExeption, JSONException{
      if (accountType != AccountType.Advisor)
         throw new InvalidAccountTypeExeption("IAPStudent(): Invalid account type; " + accountType);
      return null;
   }

   @Override
   public void vote(String projectID) throws VoteErrorException {

   }

   private enum UPRMAccountType {
      Student, Professor, NA;
      static UPRMAccountType getAccType(String accType){
         switch (accType){
            case"Student":
               return UPRMAccountType.Student;
            case "Professor":
               return UPRMAccountType.Professor;
            default:
               return UPRMAccountType.NA;
         }
      }
   }
}
