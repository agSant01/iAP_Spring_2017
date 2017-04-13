//
//  Guest.java
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

public class Guest extends User implements UserDelegate {
   private UPRMAccountType userType;
   private Voted voted;


   public Guest(JSONObject data, AccountType accountType, String id)
      throws InvalidAccountTypeExeption, JSONException {
      this(checkType(accountType), data, accountType, id );
   }

   private Guest(Void n, JSONObject data, AccountType accountType, String id) throws JSONException{
      super(data.getString("Email"), data.getString("Name"), id, data.optString("Sex"), accountType);
      this.voted = new Voted(data.optJSONObject("Voted"));
   }

   private static Void checkType(AccountType accountType)
      throws InvalidAccountTypeExeption, JSONException{
      if (accountType != AccountType.Guest)
         throw new InvalidAccountTypeExeption("Guest(): Invalid account type; " + accountType);
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
         callback.failure("Unable to correctly downcast vote");
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

      public Voted (JSONObject data) throws JSONException{
         this.bestPresentation = data.optBoolean("BestPresentation");
         this.bestPoster = data.optBoolean("BestPoster");
      }
   }
}
