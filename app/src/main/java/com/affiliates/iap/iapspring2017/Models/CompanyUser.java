//
//  CompanyUser.java
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class CompanyUser extends User implements UserDelegate {
   private ArrayList<String> votes;
   private String companName;

   public CompanyUser(JSONObject data, AccountType accountType, String id) throws InvalidAccountTypeExeption, JSONException {
      this(checkType(accountType), data, accountType, id );
   }

   private CompanyUser(Void d, JSONObject data, AccountType accountType, String id) throws JSONException{
      super(data.optString("Email"), data.optString("Name"), id,data.optString("Sex"), accountType);
      this.votes = getVotesFromJSON(data.optJSONObject("Votes"));
      this.companName = data.optString("Company");
   }

   private static Void checkType(AccountType accountType) throws InvalidAccountTypeExeption, JSONException{
     if (accountType != AccountType.CompanyUser)
         throw new InvalidAccountTypeExeption("CompanyUser(): Invalid account type; " + accountType);
      return null;
   }

   private static ArrayList<String> getVotesFromJSON(JSONObject data) throws JSONException{
      if(data == null) return new ArrayList<String>();
      Iterator<String> x = data.keys();
      ArrayList<String> votes = new ArrayList<>();
      for (int i = 0; i < data.length(); i++){
         votes.add(x.next());
      }
      return votes;
   }

   public boolean hasEvaluated(String projectID){
      if (votes == null) return false;
      return this.votes.contains(projectID);
   }

   @Override
   public void vote(final String projectID, final Vote vote, final Context context, final Callback callback) throws VoteErrorException {
      if (vote instanceof CompanyVote){
         DataService.sharedInstance().submitCompanyEval( (CompanyVote) vote, new Callback() {
            @Override
            public void success(Object data) {
               setVoted(projectID);
               ((CompanyVote) vote).removeVoteFromMemory(context);
               callback.success(vote);
            }

            @Override
            public void failure(String message) {
               callback.failure(message);
            }
         });
      } else {
         callback.failure("Vote is a null value.");
      }
   }

   public void setVoted(final String projectID){
      votes.add(projectID);
      final DatabaseReference ref = FirebaseDatabase.getInstance()
                                       .getReference().child("Users/" + this.getUserID() + "/Votes");
      ref.updateChildren(new HashMap<String, Object>(){{
         put(projectID, true);
      }});
   }

   public CompanyVote loadVote(String projectID, Context context){
      if (hasEvaluated(projectID)){
         return null;
      }
      CompanyVote vote = null;

      try {
         FileInputStream fi = context.openFileInput(projectID);
         BufferedInputStream bis = new BufferedInputStream(fi);
         StringBuilder buffer = new StringBuilder();
         while(bis.available()!= 0){
            char c = (char) bis.read();
            buffer.append(c);
         }
         Gson gson = new Gson();
         if(buffer.length() > 3)
            return  gson.fromJson(buffer.toString(), CompanyVote.class);
         return null;
      } catch (Exception e) {
         e.printStackTrace();
         Log.e("CompanyUser.class", "loadVote() + e");
      }
      if(vote==null)
         return new CompanyVote(projectID);
      return vote;
   }

   public ArrayList<String> getVotes() {
      return votes;
   }
}
