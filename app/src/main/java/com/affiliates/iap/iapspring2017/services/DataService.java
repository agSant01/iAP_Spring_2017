//
//  DataService.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.affiliates.iap.iapspring2017.Models.Advisor;
import com.affiliates.iap.iapspring2017.Models.CompanyUser;
import com.affiliates.iap.iapspring2017.Models.CompanyVote;
import com.affiliates.iap.iapspring2017.Models.IAPStudent;
import com.affiliates.iap.iapspring2017.Models.OverallVote;
import com.affiliates.iap.iapspring2017.Models.Poster;
import com.affiliates.iap.iapspring2017.Models.Sponsors;
import com.affiliates.iap.iapspring2017.Models.UPRMAccount;
import com.affiliates.iap.iapspring2017.Models.User;
import com.affiliates.iap.iapspring2017.exeptions.InvalidAccountTypeExeption;
import com.affiliates.iap.iapspring2017.exeptions.VoteErrorException;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DataService {
   private static final String TAG = "DataService";
   private static DataService mDServiceInctance = new DataService();
   
   public static DataService sharedInstance(){
      return mDServiceInctance;
   }

   private  DatabaseReference mainRef() {
      return FirebaseDatabase.getInstance().getReference();
   }

   private DatabaseReference usersRef() {
      return mainRef().child("Users");
   }

   private DatabaseReference votesRef(){
      return mainRef().child("Votes");
   }

   private DatabaseReference companyVoteRef(){
      return votesRef().child("CompanyEval");
   }

   private DatabaseReference generalVoteRef(){
      return votesRef().child("GeneralVote");
   }

   private DatabaseReference voteSummaryRef(){
      return votesRef().child("Summary");
   }

   private DatabaseReference generalVoteSummaryRef(){
      return voteSummaryRef().child("GeneralVote");
   }

   private DatabaseReference comapnyEvalSummaryRef(){
      return voteSummaryRef().child("CompanyEval");
   }

   private DatabaseReference sponsorsRef(){
      return mainRef().child("Sponsors");
   }

   private DatabaseReference scheduleRef(){
      return mainRef().child("Schedule");
   }

   private DatabaseReference postersRef(){
      return mainRef().child("SubmitedProjects");
   }

   public void registerUser(final String name, final String email, String id, final String accountType, final String userType, final Callback callback){
     final Map<String, Object> voted = new HashMap<>();
      voted.put("BestPresentation", false);
      voted.put("BestPoster", false);

      usersRef().child(id).updateChildren(new HashMap<String, Object>(){{
         put("AccountType", accountType);
         put("Email", email);
         put("Name", name);
         put("Sex", "NA");
         put("Voted", voted);

         if(accountType.contentEquals("UPRMAccount"))
            put("UserType", userType);

      }}).addOnCompleteListener(new OnCompleteListener<Void>() {
         @Override
         public void onComplete(@NonNull Task<Void> task) {
            if(task.isSuccessful()) callback.success(null);
            else callback.failure(task.getException().getMessage());
         }
      });
   }
   public void getUserData(final String id, final Callback<User> callback) throws InvalidAccountTypeExeption{
      if(usersRef().child(id).equals(null)){
         Log.e(TAG, "No user ID Registered" );
         callback.failure("No user ID Registered");
         return;
      }

      usersRef().child(id).addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot){

            Log.v(TAG, " ID: " + id);
            Log.v(TAG, " DATA: " + dataSnapshot.getValue());
            if (!dataSnapshot.hasChildren()) {
               Log.e(TAG, "No user ID Registered" );
               callback.failure("No user ID Registered");
               return;
            }
            JSONObject json =  new JSONObject((Map) dataSnapshot.getValue());

            String accType;
            User user;

            accType = json.optString("AccountType");

            try {
               switch (User.AccountType.determineAccType(accType)) {
                  case CompanyUser:
                     callback.success(new CompanyUser(json, User.AccountType.CompanyUser, id));
                     return;
                  case Advisor:
                     callback.success(new Advisor(json, User.AccountType.Advisor, id));
                     return;
                  case UPRMAccount:
                     callback.success(new UPRMAccount(json, User.AccountType.UPRMAccount, id));
                     return;
                  case IAPStudent:
                     callback.success(new IAPStudent(json, User.AccountType.IAPStudent, id));
                     return;
                  case NA:
                     callback.failure("DataSevice -> getUserData(): Invalid account type" + accType);
                     return;
               }
            } catch (JSONException e){
               Log.e(TAG, "DataService -> getUserData() / switch()", e);
               e.printStackTrace();
            }
            callback.failure("No user ID Registered");
         }
         @Override
         public void onCancelled(DatabaseError databaseError) {
            Log.e(TAG, databaseError.toString());
         }
      });
   }

   public void setVoted(User user, final OverallVote vote){
      if(user.getAccountType() != User.AccountType.CompanyUser){
         usersRef().child(user.getUserID())
                   .child("Voted")
                   .updateChildren(
                      new HashMap<String, Object>(){{
                         put(vote.getStringFromType(), true);
                      }});
      }
   }



   public void submitGeneralVote(String projecID, int voteType, final Callback callback) throws VoteErrorException{

      if(voteType != 0 && voteType != 1)
         callback.failure("Vote type value can only be 1 or 0.");

      final String voteID = generalVoteRef().push().getKey();
      final OverallVote vote = new OverallVote(voteID, projecID, voteType);

      generalVoteRef().child(vote.getStringFromType()).updateChildren(vote.makeJSON())
         .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
               runGeneralVoteTransaction(vote);
               callback.success(null);
            }})
         .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               callback.failure("DataService.class -> submitCGeneralVote() : " + e.toString());
            }
         });
   }

   private void runGeneralVoteTransaction(OverallVote vote){
      final DatabaseReference ref = votesRef().child( vote.getStringFromType() + "/" + vote.getProjectID() );
      ref.runTransaction(new Transaction.Handler() {
         @Override
         public Transaction.Result doTransaction(MutableData mutableData) {
            Integer score = (Integer) ((mutableData.getValue() == null) ? 0 : mutableData.getValue());
            score += 1;
            mutableData.setValue(score);
            return Transaction.success(mutableData);
         }
         @Override
         public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
            // Transaction completed
            Log.d(TAG, "runGeneralVoteTransaction() -> onComplete: " + databaseError);
         }
      });
   }

   public void submitCompanyEval(final CompanyVote vote, final Callback callback) throws VoteErrorException{
      final DatabaseReference ref = votesRef().child("CompanyEval").push();
      ref.updateChildren(vote.makeJSON())
         .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               runCompanyVoteTransaction(vote);
               callback.success(null);
            }
         })
         .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               callback.failure("DataService.class -> submitCompanyEval(): " + e.toString() );
            }
         });
   }

   private void runCompanyVoteTransaction(final CompanyVote vote){
      final DatabaseReference ref = votesRef().child("Summary/CompanyEval/"+(vote.getProjectID()));
      ref.child("Presentation").runTransaction(new Transaction.Handler() {
         @Override
         public Transaction.Result doTransaction(MutableData mutableData) {
            Integer score = ((mutableData.getValue() == null) ? 0 : mutableData.getValue(Integer.class));
            score += vote.getPresentationTotal();
            mutableData.setValue(score);
            return Transaction.success(mutableData);
         }

         @Override
         public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
            Log.d(TAG, "runCompanyVoteTransaction() -> onComplete(): " + databaseError);
         }
      });

      ref.child("Poster").runTransaction(new Transaction.Handler() {
         @Override
         public Transaction.Result doTransaction(MutableData mutableData) {
            Integer score =  ((mutableData.getValue() == null) ? 0 : mutableData.getValue(Integer.class));
            score += vote.getPosterTotal();
            mutableData.setValue(score);
            return Transaction.success(mutableData);
         }

         @Override
         public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
            Log.d(TAG, "runCompanyVoteTransaction() -> onComplete(): " + databaseError);
         }
      });
   }

   public void getPosters(final Callback callback) {
      postersRef().orderByChild("number").addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot) {
            JSONObject json = new JSONObject((HashMap<String, Object>) dataSnapshot.getValue());
            HashMap<Integer, Poster> poster = new HashMap<Integer, Poster>();
            try {
               Iterator<String> x = json.keys();
               Poster p;
               for (int i = 0; i < json.length(); i++) {
                  String name = x.next();
                  Log.d(TAG, "POSTERID: " + name);
                  JSONObject posterObject = json.getJSONObject(name);
                  p = new Poster(posterObject, name);
                  poster.put(p.getPosterNumber(), p);
               }
            } catch (JSONException e) {
               Log.e(TAG, "getPosters(): " + e);
            }
            callback.success(poster);
         }

         @Override
         public void onCancelled(DatabaseError databaseError) {}
      });
   }

   public void getPosterTeamMembers(final Poster poster, final Callback callback){
      final ArrayList<IAPStudent> team = new ArrayList<IAPStudent>();
      for (int i = 0; i < poster.getTeam().size(); i++){
         getUserData(poster.getTeam().get(i), new Callback<User>() {
            @Override
            public void success(User user) {
               System.out.println(TAG + team.size() + "  " + poster.getTeam().size());
               if (user instanceof IAPStudent) {
                  team.add((IAPStudent) user);
                  if (team.size() == poster.getTeam().size())
                     callback.success(team);
               }
            }
            @Override
            public void failure(String message) {
               callback.failure(message);
            }
         });
      }
   }

   public void getPosterAdvisorMembers(final Poster poster, final Callback callback){
      final ArrayList<Advisor> advisors = new ArrayList<Advisor>();
      for (String id : poster.getAdvisors()){
         getUserData(id, new Callback<User>() {
            @Override
            public void success(User user) {
               if (user instanceof Advisor){
                  advisors.add((Advisor) user);
                  if(advisors.size() == poster.getAdvisors().size())
                     callback.success(advisors);
               }
            }

            @Override
            public void failure(String message) {
               callback.failure(message);
            }
         });
      }
   }

   public void getSponsors(final Callback callback){
      final ArrayList<Sponsors> sponsorsMap = new ArrayList<>();
      sponsorsRef().addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot) {
            JSONObject json = new JSONObject((HashMap<String,Object>)dataSnapshot.getValue());
            Iterator<String> x = json.keys();
            for (int i = 0; i < json.length(); i++){
               String s  = x.next();
               sponsorsMap.add(new Sponsors(json.optJSONObject(s),s));
            }
            callback.success(sponsorsMap);
         }

         @Override
         public void onCancelled(DatabaseError databaseError) {
            callback.failure(databaseError.getMessage());
         }
      });
   }
}