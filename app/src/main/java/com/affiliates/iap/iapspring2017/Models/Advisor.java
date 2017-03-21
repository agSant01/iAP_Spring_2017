//
//  Advisor.java
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class Advisor extends User implements UserDelegate {
   private ArrayList<String> classes;
   private String department;
   private String photoURL;
   private ArrayList<String> projects;
   private String researchIntent;
   private Voted voted;
   private String webPage;

   public Advisor(JSONObject data, AccountType accountType, String id)
      throws InvalidAccountTypeExeption, JSONException{
      this(checkType(accountType), data, accountType, id );
   }

   private Advisor(Void d, JSONObject data, AccountType accountType, String id) throws JSONException{
      super(data.optString("Email"), data.optString("Name"), id,data.optString("Sex"), accountType);
      this.classes = parseData(data.optJSONObject("Classes"));
      this.projects = parseData(data.optJSONObject("Projects"));
      this.department = data.optString("Department");
      this.photoURL = data.optString("PhotoURL");
      this.researchIntent = data.optString("ResearchIntent");
      this.voted = new Voted(data.optJSONObject("Voted"));
      this.webPage = data.optString("webpage");
   }

   private static Void checkType(AccountType accountType)
      throws InvalidAccountTypeExeption, JSONException{
      if (accountType != AccountType.Advisor)
         throw new InvalidAccountTypeExeption("Advisor(): Invalid account type; " + accountType);
      return null;
   }

   private static ArrayList<String> parseData(JSONObject data) throws JSONException{
      if(data == null) return null;
      ArrayList<String> d = new ArrayList<String>();
      Iterator<String> keys = data.keys();
      for (int i = 0; i < data.length(); i++)
         d.add(keys.next());
      return d;
   }

   public boolean hasVoted(OverallVote vote){
      switch (vote.getVoteType()){
         case BestPoster:
            return voted.bestPoster;
         case BestPresentation:
            return voted.bestPresentation;
         default:
            return false;
      }
   }
   @Override
   public void vote(String projectID, Vote vote, Context context, final Callback callback) throws VoteErrorException {
      if(vote instanceof OverallVote){
         DataService.sharedInstance().submitGeneralVote(projectID, ((OverallVote) vote).getNumberFromType(), new Callback<Vote>() {
            @Override
            public void success(Vote vote) {
               setVoted((OverallVote) vote);
               callback.success(null);
            }
            @Override
            public void failure(String message) {
               callback.failure(message);
            }
         });
      } else {
         callback.failure("Unable to correctly downcast vote");
      }
   }

   private void setVoted(OverallVote vote) {
      switch (vote.getVoteType()){
         case BestPoster:
            this.voted.bestPoster = true;
            break;
         case BestPresentation:
            this.voted.bestPresentation = true;
            break;
      }
      DataService.sharedInstance().setVoted(this, vote);
   }

   private class Voted{
      private boolean bestPoster = false;
      private boolean bestPresentation = false;

      public Voted (JSONObject data) throws JSONException{
         this.bestPoster = data.getBoolean("BestPoster");
         this.bestPresentation = data.getBoolean("BestPresentation");
      }
   }

   public ArrayList<String> getClasses() {
      return classes;
   }

   public String getDepartment() {
      return department;
   }

   public String getPhotoURL() {
      return photoURL;
   }

   public ArrayList<String> getProjects() {
      return projects;
   }

   public String getResearchIntent() {
      return researchIntent;
   }

   public Voted getVoted() {
      return voted;
   }

   public String getWebPage() {
      return webPage;
   }
}
