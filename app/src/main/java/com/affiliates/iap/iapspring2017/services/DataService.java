package com.affiliates.iap.iapspring2017.services;

import android.support.annotation.NonNull;
import android.telecom.Call;
import android.util.Log;

import com.affiliates.iap.iapspring2017.Constants;
import com.affiliates.iap.iapspring2017.Models.Advisor;
import com.affiliates.iap.iapspring2017.Models.CompanyUser;
import com.affiliates.iap.iapspring2017.Models.CompanyVote;
import com.affiliates.iap.iapspring2017.Models.IAPStudent;
import com.affiliates.iap.iapspring2017.Models.OverallVote;
import com.affiliates.iap.iapspring2017.Models.Poster;
import com.affiliates.iap.iapspring2017.Models.UPRMAccount;
import com.affiliates.iap.iapspring2017.Models.User;
import com.affiliates.iap.iapspring2017.SignInActivity;
import com.affiliates.iap.iapspring2017.exeptions.InvalidAccountTypeExeption;
import com.affiliates.iap.iapspring2017.exeptions.VoteErrorException;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.google.android.gms.common.UserRecoverableException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

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

   private DatabaseReference scheduleRef(){
      return mainRef().child("Schedule");
   }

   private DatabaseReference postersRef(){
      return mainRef().child("SubmitedProjects");
   }

   public void getUserData(String id, final Callback<User> callback) throws InvalidAccountTypeExeption{
      final String ID = id;

      usersRef().child(id).addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot){
            JSONObject json = new JSONObject((Map<String, Object>) dataSnapshot.getValue());
            String accType = null;
            User user = null;

            try {
               accType = json.getString("AccountType");
            } catch (JSONException e) {
               Log.e(TAG, "DataService -> getUserData()", e);
               e.printStackTrace();
            }
            try {
               switch (User.AccountType.determineAccType(accType)) {
                  case CompanyUser:
                     callback.success(new CompanyUser(json, User.AccountType.CompanyUser, ID));
                     break;
                  case Advisor:
                     callback.success(new Advisor(json, User.AccountType.Advisor, ID));
                     break;
                  case UPRMAccount:
                     callback.success(new UPRMAccount(json, User.AccountType.UPRMAccount, ID));
                     break;
                  case IAPStudent:
                     callback.success(new IAPStudent(json, User.AccountType.IAPStudent, ID));
                     break;
                  case NA:
                     callback.failure("DataSevice -> getUserData(): Invalid account type" + accType);
               }
            } catch (JSONException e){
               Log.e(TAG, "DataService -> getUserData() / switch()", e);
               e.printStackTrace();
            }
         }
         @Override
         public void onCancelled(DatabaseError databaseError) {}
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

      if(voteType != 0 || voteType != 1)
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
      final DatabaseReference ref = votesRef().child("Sumary/CompanyEval/"+(vote.getProjectID()));
      ref.child("Presentation").runTransaction(new Transaction.Handler() {
         @Override
         public Transaction.Result doTransaction(MutableData mutableData) {
            Integer score = (Integer) ((mutableData.getValue() == null) ? 0 : mutableData.getValue());
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
            Integer score = (Integer) ((mutableData.getValue() == null) ? 0 : mutableData.getValue());
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
      postersRef().addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot) {
            JSONArray jsonArray = dataSnapshot.getValue(JSONArray.class);
            ArrayList<Poster> posters = new ArrayList<Poster>();
            try {
               JSONObject object;
               for(int i = 0; i < jsonArray.length(); i++){
                  object = jsonArray.getJSONObject(i);
                  posters.add(new Poster(object, object.keys().toString()));

                  System.out.println("TESTING: " + posters.get(i).getProjectName());
               }
            }catch (JSONException e){
               Log.e(TAG, "getPosters(): "  + e);
            }
            callback.success(posters);
         }

         @Override
         public void onCancelled(DatabaseError databaseError) {}
      });
   }

   public void getPosterTeamMembers(Poster poster, final Callback callback){
      final ArrayList<IAPStudent> team = new ArrayList<IAPStudent>();
      for (String id : poster.getTeam()){
         getUserData(id, new Callback<User>() {
            @Override
            public void success(User user) {
               if (user instanceof IAPStudent) {
                  team.add((IAPStudent) user);
               }
            }
            @Override
            public void failure(String message) {
               callback.failure(message);
            }
         });
      }
      callback.success(team);
   }

   public void getPosterAdvisorMembers(Poster poster, final Callback callback){
      final ArrayList<Advisor> advisors = new ArrayList<Advisor>();
      for (String id : poster.getAdvisors()){
         getUserData(id, new Callback<User>() {
            @Override
            public void success(User user) {
               if (user instanceof Advisor){
                  advisors.add((Advisor) user);
               }
            }

            @Override
            public void failure(String message) {
               callback.failure(message);
            }
         });
      }
      callback.success(advisors);
   }
}