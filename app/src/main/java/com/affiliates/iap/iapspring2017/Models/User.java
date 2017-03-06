//
//  User.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.affiliates.iap.iapspring2017.exeptions.InvalidAccountTypeExeption;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.services.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class User {
   private static final String TAG = "User";

   private String email;
   private String name;
   private String userID;
   private String gender;
   private AccountType accountType;

   public String getEmail() {
      return email;
   }

   public String getName() {
      return name;
   }

   public String getUserID() {
      return userID;
   }

   public AccountType getAccountType() {
      return accountType;
   }

   User(String email, String name, String userID,String gender, AccountType accType){
      this.email = email;
      this.name = name;
      this.userID = userID;
      if (accType == AccountType.NA){
         throw new InvalidAccountTypeExeption("User(): Invalid account type" + accType);
      }
      this.accountType = accType;
      this.gender = gender;
   }

   public static void login(final Context context, String email, String password, final Callback<User> callback) {
      FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).
         addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
               Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

               // If sign in fails, display a message to the user. If sign in succeeds
               // the auth state listener will be notified and logic to handle the
               // signed in user can be handled in the listener.
               if (!task.isSuccessful()) {
                  Log.w(TAG, "signInWithEmail:failed", task.getException());
                  Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show();
                  return;
               }
               DataService.sharedInstance().getUserData(task.getResult().getUser().getUid(), callback);
            }
         });
   }

   public void resetPassword(String email){
      FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
            System.err.println("User.class -> resetPassword(): " + e.toString());
         }
      });
   }

   public boolean logOut(){
      return false;
   }

   public void getUserData(String userID){
      FirebaseDatabase.getInstance().getReference().child("User").
         orderByChild("userID").equalTo("AccountType");
   }

   /**
    *
    */
   public enum AccountType{
      CompanyUser, IAPStudent, Advisor,UPRMAccount, NA;
      public static AccountType determineAccType(String typeString){
         switch (typeString){
            case "Company":
               return AccountType.CompanyUser;
            case "IAPStudent":
               return AccountType.IAPStudent;
            case "Advisor":
               return AccountType.Advisor;
            case "UPRMAccount":
               return AccountType.UPRMAccount;
            default:
               return AccountType.NA;
         }

      }
   }
}
