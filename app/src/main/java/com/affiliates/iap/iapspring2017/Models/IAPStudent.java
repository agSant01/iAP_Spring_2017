//
//  IAPStudent.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Models;

import android.content.Context;
import android.util.Log;

import com.affiliates.iap.iapspring2017.exeptions.InvalidAccountTypeExeption;
import com.affiliates.iap.iapspring2017.exeptions.VoteErrorException;
import com.affiliates.iap.iapspring2017.interfaces.UserDelegate;
import com.affiliates.iap.iapspring2017.services.DataService;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class IAPStudent extends User implements UserDelegate{
   private HashMap<String, String> projects;
   private String department;
   private String gradDate;
   private String objective;
   private String photoURL;
   private String resumeURL;
   private Voted voted;

   public IAPStudent(JSONObject data, AccountType accountType, String id)
      throws InvalidAccountTypeExeption, JSONException {
      this(checkType(accountType), data, accountType, id );
   }

   private IAPStudent(Void d, JSONObject data, AccountType accountType, String id) throws JSONException{
      super(data.optString("Email"), data.optString("Name"), id, data.optString("Sex"), accountType);
      this.voted = new Voted(data.optJSONObject("Voted"));
      this.department = data.optString("Department");
      this.resumeURL = data.optString("ResumeLink");
      this.objective = data.optString("Objective");
      this.photoURL = data.optString("PhotoURL");
      this.gradDate = data.optString("GradDate");
      getProyectMap(data.optJSONObject("Projects"));
   }

   private static Void checkType(AccountType accountType)
      throws InvalidAccountTypeExeption, JSONException{
      if (accountType != AccountType.IAPStudent)
         throw new InvalidAccountTypeExeption("IAPStudent(): Invalid account type; " + accountType);
      return null;
   }


   private void getProyectMap(JSONObject data){
      Iterator<String> keys = data.keys();
      projects = new HashMap<>();

      while(keys.hasNext()) {
         String k = keys.next();
         projects.put(k, data.optString(k));
         Log.v("IAP", projects.get(k));
      }
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
      private boolean bestPresentation = false;
      private boolean bestPoster = false;

      public Voted (JSONObject data) throws JSONException{
         if (data == null) return;
         this.bestPresentation = data.optBoolean("BestPresentation");
         this.bestPoster = data.optBoolean("BestPoster");
      }
   }

   public HashMap<String, String> getProyectMap() {
      return projects;
   }

   public ArrayList<String> getProjectNames(){
      ArrayList<String> atr = new ArrayList<>();
      for(String s : projects.values()){
         atr.add(s);
      }
      return atr;
   }

   public String getDepartment() {
      return department;
   }

   public String getResumeURL() {
      return resumeURL;
   }

   public String getObjective() {
      return objective;
   }

   public String getGradDate() {
      return gradDate;
   }

   public String getPhotoURL() {
      return photoURL;
   }
   
   public Voted getVoted() {
      return voted;
   }
}
