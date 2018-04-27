//
//  UPRMAccount.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Models;

import android.content.Context;
import android.util.Log;

import com.affiliates.iap.iapspring2017.Exceptions.InvalidAccountTypeExeption;
import com.affiliates.iap.iapspring2017.Exceptions.VoteErrorException;
import com.affiliates.iap.iapspring2017.Interfaces.Callback;
import com.affiliates.iap.iapspring2017.Interfaces.UserDelegate;
import com.affiliates.iap.iapspring2017.Services.DataService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class UPRMAccount extends User implements UserDelegate {
   private Voted voted;

   public UPRMAccount(JSONObject data, AccountType accountType, String id)
      throws InvalidAccountTypeExeption, JSONException {
      this(checkType(accountType), data, accountType, id );
   }

   public UPRMAccount (String email){
      super(email, AccountType.UPRMAccount);
      voted = new Voted();
   }

   private UPRMAccount(Void n, JSONObject data, AccountType accountType, String id) throws JSONException{
      super(data.getString("Email"), data.getString("Name"), id, data.optString("Sex"), accountType, data.optString("PhotoURL"));
      this.voted = new Voted(data.optJSONObject("Voted"));
   }

   private static Void checkType(AccountType accountType)
      throws InvalidAccountTypeExeption, JSONException{
      if (accountType != AccountType.UPRMAccount)
         throw new InvalidAccountTypeExeption("UPRMAccount(): Invalid account type; " + accountType);
      return null;
   }

   private boolean hasVoted(OverallVote vote){
      switch (vote.getVoteType()){
         case BestPoster:
            return voted.bestPoster;
         case BestPresentation:
            return voted.bestPresentation;
      }
      return false;
   }

   @Override
   public void vote(String projectID, final Vote vote, Context context, final Callback callback) throws VoteErrorException {
      if (vote instanceof OverallVote){
         DataService.sharedInstance().submitGeneralVote(projectID, ((OverallVote) vote).getNumberFromType(), new Callback<Vote>() {
            @Override
            public void success(Vote data) {
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
         put("AccountType", "UPRMAccount");
         put("Name", getName());
         put("Email", getEmail());
         put("Sex", getGender());
         put("PhotoURL", getPhotoURL());
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

   private void setVoted(OverallVote vote){
      switch (vote.getVoteType()){
         case BestPoster:
            voted.bestPoster = true;
            break;
         case BestPresentation:
            voted.bestPresentation = true;
            break;
      }
      DataService.sharedInstance().setVoted(this, vote);
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

   private class Voted{
      private boolean bestPresentation = false;
      private boolean bestPoster = false;

      private Voted (JSONObject data) throws JSONException{
         if(data == null) return;
         this.bestPresentation = data.optBoolean("BestPresentation");
         this.bestPoster = data.optBoolean("BestPoster");
      }

      private Voted(){
         this.bestPresentation = false;
         this.bestPoster = false;
      }
   }

   public HashMap<String, Object> exportVoted(){
      return new HashMap<String, Object>(){{
         put("BestPresentation", voted.bestPresentation);
         put("BestPoster", voted.bestPoster);
      }};
   }

}
