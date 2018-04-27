//
//  User.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Models;

import android.content.Context;
import android.util.Log;

import com.affiliates.iap.iapspring2017.Exceptions.InvalidAccountTypeExeption;
import com.affiliates.iap.iapspring2017.Interfaces.Callback;
import com.affiliates.iap.iapspring2017.Interfaces.UserDelegate;
import com.affiliates.iap.iapspring2017.Services.AccountAdministration;
import com.affiliates.iap.iapspring2017.Services.ConstantsService;
import com.affiliates.iap.iapspring2017.Services.DataService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.crash.FirebaseCrash;

import java.util.HashMap;

public abstract class User implements UserDelegate{
   private static final String TAG = "User";

   private AccountType accountType;
   private String photoURL;
   private String userID;
   private String gender;
   private String email;
   private String name;

   /**
    * User constructor used when the user creates an account
    * @param email
    * @param name
    * @param userID
    * @param gender
    * @param accType
    * @param photoURL
    */
   User(String email, String name, String userID,String gender, AccountType accType, String photoURL){
      if (accType == AccountType.NA){
         throw new InvalidAccountTypeExeption("User(): Invalid account type" + accType);
      }
      this.accountType = accType;
      this.photoURL = photoURL;
      this.userID = userID;
      this.gender = gender;
      this.email = email;
      this.name = name;
   }

   /**
    * Constructor used when the user already has an account in the DB.
    * @param email
    * @param accountType
    */
   User(String email, AccountType accountType){
      this.accountType = accountType;
      this.photoURL = "NA";
      this.userID = "NA";
      this.gender = "NA";
      this.email = email;
      this.name = "NA";
      Log.v(TAG, email);
   }

   /**
    * Login for user
    * @param email
    * @param password
    * @param callback   return user object or failure
    */
   public static void login(String email, String password, final Callback<User> callback) {
      FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).
         addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
               Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
               // If sign in fails, display a message to the user. If sign in succeeds
               // the auth state listener will be notified and logic to handle the
               // signed in user can be handled in the listener.
               if (!task.isSuccessful()) {
                  FirebaseCrash.log(TAG + "signInWithEmail:failed" + task.getException());
                  Log.e(TAG, "signInWithEmail:failed", task.getException());
                  callback.failure(task.getException().getMessage());
                  return;
               }try{
                  DataService.sharedInstance().getUserData(task.getResult().getUser().getUid(), callback);
               } catch (InvalidAccountTypeExeption e){
                  FirebaseCrash.log("User.class -> login(): Invalid Account Exeption");
                  callback.failure("User.class -> login(): Invalid Account Exeption");
               }
            }
         });
   }

   /**
    * Logout
    * @param context
    * @throws FirebaseAuthException
    */
   public void logOut(Context context) throws FirebaseAuthException{
      FirebaseAuth.getInstance().signOut();
      AccountAdministration aa = new AccountAdministration(context);
      aa.saveUserID("second");
      ConstantsService.setCurrentLogedInUser(null);
   }

   @Override
   public HashMap<String, Object> toMap() {
      return null;
   }

   /**
    * Enum of account types
    */
   public enum AccountType{
      CompanyUser, IAPStudent, Advisor, UPRMAccount, NA;
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

   // getters and setters
   public AccountType getAccountType() {
      return accountType;
   }

   public String getUserID() {
      return userID;
   }

   public String getGender() {
      return gender;
   }

   public String getEmail() {
      return email;
   }

   public String getName() {
      return name;
   }

   public String getPhotoURL() {
      return photoURL;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setPhotoURL(String photoURL) {
      this.photoURL = photoURL;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public void setID(String id) {
      this.userID = id;
   }

   public void setGender(String gender) {
      this.gender = gender;
   }

   public abstract boolean hasVoted(int type);
   public abstract void vote(OverallVote vote);
}
