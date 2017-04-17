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

import com.affiliates.iap.iapspring2017.Constants;
import com.affiliates.iap.iapspring2017.exeptions.InvalidAccountTypeExeption;
import com.affiliates.iap.iapspring2017.exeptions.VoteErrorException;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.interfaces.UserDelegate;
import com.affiliates.iap.iapspring2017.services.DataService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class CompanyUser extends User implements UserDelegate {
   private ArrayList<String> votes;
   private String companyName;

   public CompanyUser(JSONObject data, AccountType accountType, String id)
           throws InvalidAccountTypeExeption, JSONException {
      this(checkType(accountType), data, accountType, id );
   }

   public CompanyUser(JSONObject data){
      super(data.optString("Email"), AccountType.CompanyUser);
      votes = new ArrayList<String>();
      companyName = data.optString("Company");
   }

   private CompanyUser(Void d, JSONObject data, AccountType accountType, String id) throws JSONException{
      super(data.optString("Email"), data.optString("Name"), id,data.optString("Sex"), accountType, data.optString("PhotoURL"));
      this.votes = getVotesFromJSON(data.optJSONObject("Votes"));
      this.companyName = data.optString("Company");
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

   @Override
   public HashMap<String, Object> toMap() {
      return new HashMap<String, Object>(){{
         put("AccountType", "Company");
         put("Name", getName());
         put("Email", getEmail());
         put("PhotoURL", getPhotoURL());
         put("Sex", getGender());
         put("Votes", exportVote());
      }};
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
      return new CompanyVote(projectID);
   }

   public boolean isLiked(String id){
      if(Constants.getLikedStudents() == null) return false;
      for(int i = 0; i < Constants.getLikedStudents().size(); i++){
         if(Constants.getLikedStudents().get(i).getUserID().equals(id))
            return true;
      }
      return false;
   }

    public boolean isUnliked(String id) {
       if (Constants.getUnlikedStudents() == null) return false;
       for (int i = 0; i < Constants.getUnlikedStudents().size(); i++) {
          if (Constants.getUnlikedStudents().get(i).getUserID().equals(id))
             return true;
       }
       return false;
    }

    public boolean isUndecided(String id) {
       if (Constants.getUndecidedStudents() == null) return false;
       for (int i = 0; i < Constants.getUndecidedStudents().size(); i++) {
          if (Constants.getUndecidedStudents().get(i).getUserID().equals(id))
             return true;
       }
       return false;
    }

    public HashMap<String, Object> exportVote(){
       HashMap<String, Object> v = new HashMap<>();
       for(int i = 0; i < votes.size(); i++)
          v.put(votes.get(i), "true");
       return v;
    }

    public ArrayList<String> getVotes() {
      return votes;
   }

   public String getCompanyName() {
      return companyName;
   }
}
