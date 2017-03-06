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
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
      super(data.getString("Email"), data.getString("Name"), id,data.getString("Sex"), accountType);
      this.classes = parseData(data.getJSONArray("Classes"));
      this.projects = parseData(data.getJSONArray("Projects"));
      this.department = data.getString("Department");
      this.photoURL = data.getString("PhotoURL");
      this.researchIntent = data.getString("ResearchIntent");
      this.voted = new Voted(data.getJSONObject("Voted"));
      this.webPage = data.getString("webpage");
   }

   private static Void checkType(AccountType accountType)
      throws InvalidAccountTypeExeption, JSONException{
      if (accountType != AccountType.Advisor)
         throw new InvalidAccountTypeExeption("Advisor(): Invalid account type; " + accountType);
      return null;
   }

   private static ArrayList<String> parseData(JSONArray data) throws JSONException{
      ArrayList<String> d = new ArrayList<>();
      for (int i = 0; i < data.length(); i++){
         d.add(data.getString(i));
      }
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
}
