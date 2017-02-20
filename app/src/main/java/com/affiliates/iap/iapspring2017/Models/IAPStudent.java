//
//  IAPStudent.java
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

public class IAPStudent extends Users implements UserDelegate{
   private String department;
   private String gradDate;
   private String objective;
   private String photoURL;
   private String proyectID;
   private String resumeURL;
   private boolean voted;

   public IAPStudent(JSONObject data, AccountType accountType, String id)
      throws InvalidAccountTypeExeption, JSONException {
      this(checkType(accountType), data, accountType, id );
   }

   private IAPStudent(Void d, JSONObject data, AccountType accountType, String id) throws JSONException{
      super(data.getString("Email"), data.getString("Name"), id, accountType);
      this.department = data.getString("Department");
      this.photoURL = data.getString("PhotoURL");
      this.gradDate = data.getString("GradDate");
      this.objective = data.getString("Objective");
      this.resumeURL = data.getString("ResumeLink");
      this.proyectID = data.getString("Project");
      this.voted = data.getBoolean("Voted");

   }

   private static Void checkType(AccountType accountType)
      throws InvalidAccountTypeExeption, JSONException{
      if (accountType != AccountType.Advisor)
         throw new InvalidAccountTypeExeption("IAPStudent(): Invalid account type; " + accountType);
      return null;
   }

   private void setVoted(){
      this.voted = true;
      //TODO: Set voted FBDatabase
   }

   @Override
   public void vote(String projectID) throws VoteErrorException {

   }
}
