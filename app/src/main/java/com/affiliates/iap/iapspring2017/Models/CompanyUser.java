//
//  CompanyUser.java
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class CompanyUser extends User implements UserDelegate {
   private ArrayList<String> votes;
   private String companName;

   public CompanyUser(JSONObject data, AccountType accountType, String id) throws InvalidAccountTypeExeption, JSONException {
      this(checkType(accountType), data, accountType, id );
   }

   private CompanyUser(Void d, JSONObject data, AccountType accountType, String id) throws JSONException{
      super(data.getString("Email"), data.getString("Name"), id,data.getString("Sex"), accountType);
      this.votes = getVotesFromJSON(data.getJSONArray("Votes"));
      this.companName = data.getString("Company");
   }

   private static Void checkType(AccountType accountType) throws InvalidAccountTypeExeption, JSONException{
      if (accountType != AccountType.Advisor)
         throw new InvalidAccountTypeExeption("IAPStudent(): Invalid account type; " + accountType);
      return null;
   }

   private static ArrayList<String> getVotesFromJSON(JSONArray data) throws JSONException{
      ArrayList<String> votes = new ArrayList<>();
      for (int i = 0; i < data.length(); i++){
         votes.add(data.getString(i));
      }
      return votes;
   }

   public boolean hasEvaluated(String projectID){
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
               callback.success(null);
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

   private void setVoted(final String projectID){
      this.votes.add(projectID);
      final DatabaseReference ref = FirebaseDatabase.getInstance()
                                       .getReference().child("Users/" + this.getUserID() + "/Votes");
      ref.updateChildren(new HashMap<String, Object>(){{
         put(projectID, true);
      }});
   }

   CompanyVote loadVote(String projectID, Context context){
      if (hasEvaluated(projectID)){
         return null;
      }
      FileInputStream fileInputStream = null;
      CompanyVote vote = null;
      try {
         fileInputStream = context.openFileInput(projectID);
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }
      if(fileInputStream != null){
         ObjectInputStream objectInputStream = null;
         try {
            objectInputStream = new ObjectInputStream(fileInputStream);
            vote = (CompanyVote) objectInputStream.readObject();
         } catch (IOException e) {
            e.printStackTrace();
         } catch (ClassNotFoundException e) {
            e.printStackTrace();
         }
      } else {
         vote = new CompanyVote(projectID);
         FileOutputStream fileOutputStream = null;
         try {
            fileOutputStream = context.openFileOutput(projectID, Context.MODE_PRIVATE);

            ObjectOutputStream os = new ObjectOutputStream(fileOutputStream);
            os.writeObject(vote);
            os.close();
            fileOutputStream.close();
         }  catch (IOException e) {
            e.printStackTrace();
         }
      }
      return vote;
   }
}
