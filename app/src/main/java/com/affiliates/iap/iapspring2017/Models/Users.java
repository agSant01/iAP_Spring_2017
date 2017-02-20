//
//  Users.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Models;

import com.affiliates.iap.iapspring2017.exeptions.InvalidAccountTypeExeption;
import com.google.firebase.database.FirebaseDatabase;

public class Users {
   private String email;
   private String name;
   private String userID;
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

   Users(String email, String name, String userID, AccountType accType){
      this.email = email;
      this.name = name;
      this.userID = userID;
      if (accType == AccountType.NA){
         throw new InvalidAccountTypeExeption("Users(): Invalid account type" + accType);
      }
      this.accountType = accType;
   }

   public void login(String email, String password){

   }

   public boolean resetPassword(){
      return false;
   }

   public boolean logOut(){
      return false;
   }

   public void getUserData(String userID){
      FirebaseDatabase.getInstance().getReference().child("Users").
         orderByChild("userID").equalTo("AccountType");
   }

   /**
    *
    */
   enum AccountType{
      Company, IAPStudent, Advisor,UPRMAccount, NA;
      static AccountType determineAccType(String typeString){
         switch (typeString){
            case "Company":
               return AccountType.Company;
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
