//
//  Advisor.java
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
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.interfaces.UserDelegate;
import com.affiliates.iap.iapspring2017.services.DataService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Advisor extends User implements UserDelegate {
   private ArrayList<String> projects;
   private String researchIntent;
   private String department;
   private String webPage;
   private Voted voted;

   public Advisor(JSONObject data, AccountType accountType, String id)
      throws InvalidAccountTypeExeption, JSONException{
      this(checkType(accountType), data, accountType, id );
   }

   public Advisor(JSONObject data){
      super(data.optString("Email"), AccountType.Advisor );
      this.projects = parseData(data.optJSONObject("Projects"));
      this.researchIntent = "To be defined";
      this.voted = new Voted();
      this.department = "NA";
      this.webPage = "NA";

   }

   private Advisor(Void d, JSONObject data, AccountType accountType, String id) throws JSONException{
      super(data.optString("Email"), data.optString("Name"), id,data.optString("Sex"), accountType, data.optString("PhotoURL"));
      this.projects = parseData(data.optJSONObject("Projects"));
      this.researchIntent = data.optString("ResearchIntent");
      this.voted = new Voted(data.optJSONObject("Voted"));
      this.department = data.optString("Department");
      this.webPage = data.optString("Webpage");
   }

   private static Void checkType(AccountType accountType)
      throws InvalidAccountTypeExeption, JSONException{
      if (accountType != AccountType.Advisor)
         throw new InvalidAccountTypeExeption("Advisor(): Invalid account type; " + accountType);
      return null;
   }

   private static ArrayList<String> parseData(JSONObject data){
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
         DataService.sharedInstance().submitGeneralVote( projectID, ((OverallVote) vote).getNumberFromType(), new Callback<Vote>() {
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

   @Override
   public HashMap<String, Object> toMap() {
      return new HashMap<String, Object>(){{
         put("AccountType", "Advisor");
         put("Name", getName());
         put("Email", getEmail());
         put("PhotoURL", getPhotoURL());
         put("Sex", getGender());
         put("Department", department);
         put("ResearchIntent", researchIntent);
         put("Webpage", webPage);
         put("Projects", exportProjects());
         put("Voted", exportVoted());
      }};
   }

    @Override
    public boolean hasVoted(int type) {
        switch (type){
            case 0:
                return voted.bestPoster;
            case 1:
                return voted.bestPresentation;
        }
        return false;
    }

    @Override
    public void vote(OverallVote vote) {
        if (!hasVoted( vote)) {
            setVoted(vote);
            Log.v("Voting", "Voted!");

        }
        else{
            Log.v("Voting", "Already Voted!");
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
      private boolean bestPresentation = false;
      private boolean bestPoster = false;

      private Voted (JSONObject data) throws JSONException{
         this.bestPresentation = data.getBoolean("BestPresentation");
         this.bestPoster = data.getBoolean("BestPoster");
      }

      private Voted (){
         this.bestPresentation = false;
         this.bestPoster = false;
      }
   }

   private HashMap<String, Object> exportProjects(){
      HashMap<String, Object> p = new HashMap<>();
       if(projects!=null)
          for(int i = 0; i < projects.size(); i++) {
              p.put(projects.get(i), true);
          }
      return p;
   }

   public HashMap<String, Object> exportVoted(){
      return new HashMap<String, Object>(){{
         put("BestPresentation", voted.bestPresentation);
         put("BestPoster", voted.bestPoster);
      }};
   }

   public String getResearchIntent() {
      return researchIntent;
   }

   public ArrayList<String> getProjects() {
      return projects;
   }

   public String getDepartment() {
      return department;
   }

   public String getWebPage() {
      return webPage;
   }

   public Voted getVoted() {
      return voted;
   }

   public void setResearchIntent(String researchIntent) {
      this.researchIntent = researchIntent;
   }

   public void setDepartment(String department) {
      this.department = department;
   }

   public void setWebPage(String webPage) {
      this.webPage = webPage;
   }
}
