//
//  IAPStudent.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Models;

import android.content.Context;

import com.affiliates.iap.iapspring2017.exeptions.InvalidAccountTypeExeption;
import com.affiliates.iap.iapspring2017.exeptions.VoteErrorException;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.interfaces.UserDelegate;
import com.affiliates.iap.iapspring2017.services.DataService;

import org.json.JSONException;
import org.json.JSONObject;

public class IAPStudent extends User implements UserDelegate{
   private String department;
   private String gradDate;
   private String objective;
   private String photoURL;
   private String proyectID;
   private String resumeURL;
   private Voted voted;

   public IAPStudent(JSONObject data, AccountType accountType, String id)
      throws InvalidAccountTypeExeption, JSONException {
      this(checkType(accountType), data, accountType, id );
   }

   private IAPStudent(Void d, JSONObject data, AccountType accountType, String id) throws JSONException{
      super(data.optString("Email"), data.optString("Name"), id, data.optString("Sex"), accountType);
      this.department = data.optString("Department");
      this.photoURL = data.optString("PhotoURL");
      this.gradDate = data.optString("GradDate");
      this.objective = data.optString("Objective");
      this.resumeURL = data.optString("ResumeLink");
      this.proyectID = data.optString("Project");
      this.voted = new Voted(data.optJSONObject("Voted"));

   }

   private static Void checkType(AccountType accountType)
      throws InvalidAccountTypeExeption, JSONException{
      if (accountType != AccountType.IAPStudent)
         throw new InvalidAccountTypeExeption("IAPStudent(): Invalid account type; " + accountType);
      return null;
   }

   private boolean hasVoted(OverallVote vote){
      switch (vote.getVoteType()){
         case BestPoster:
            return voted.bestPoster;
         case BestPresentation:
            return voted.bestPresentation;
         default:
            return false;
      }
   }

   private void setVoted(OverallVote vote){
      switch (vote.getVoteType()){
         case BestPoster:
            this.voted.bestPoster = true;
            break;
         case BestPresentation:
            voted.bestPresentation = true;
            break;
      }
      DataService.sharedInstance().setVoted(this, vote);
   }

   @Override
   public void vote(String projectID, final Vote vote, Context context, final Callback callback) throws VoteErrorException {
      if (vote instanceof OverallVote){
         if(this.hasVoted((OverallVote) vote)){
            DataService.sharedInstance().submitGeneralVote(projectID, ((OverallVote) vote).getNumberFromType(), new Callback() {
               @Override
               public void success(Object data) {
                  setVoted((OverallVote) vote);
                  callback.success(null);
               }

               @Override
               public void failure(String message) {
                  callback.failure(message);
               }
            });
         } else {
            callback.failure("Vote Error: Already voted");
         }
      } else {
         callback.failure("Vote Error: Downcasting failed.");
      }
   }


   private class Voted{
      private boolean bestPoster = false;
      private boolean bestPresentation = false;

      public Voted (JSONObject data) throws JSONException{
         if (data == null) return;
         this.bestPoster = data.optBoolean("BestPoster");
         this.bestPresentation = data.optBoolean("BestPresentation");
      }
   }

   public String getDepartment() {
      return department;
   }

   public String getGradDate() {
      return gradDate;
   }

   public String getObjective() {
      return objective;
   }

   public String getPhotoURL() {
      return photoURL;
   }

   public String getProyectID() {
      return proyectID;
   }

   public String getResumeURL() {
      return resumeURL;
   }

   public Voted getVoted() {
      return voted;
   }
}
