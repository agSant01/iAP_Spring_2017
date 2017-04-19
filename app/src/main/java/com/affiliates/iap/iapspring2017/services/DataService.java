//
//  DataService.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.services;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.affiliates.iap.iapspring2017.Constants;
import com.affiliates.iap.iapspring2017.Models.Advisor;
import com.affiliates.iap.iapspring2017.Models.CompanyUser;
import com.affiliates.iap.iapspring2017.Models.CompanyVote;
import com.affiliates.iap.iapspring2017.Models.Event;
import com.affiliates.iap.iapspring2017.Models.IAPStudent;
import com.affiliates.iap.iapspring2017.Models.OverallVote;
import com.affiliates.iap.iapspring2017.Models.Poster;
import com.affiliates.iap.iapspring2017.Models.Sponsors;
import com.affiliates.iap.iapspring2017.Models.UPRMAccount;
import com.affiliates.iap.iapspring2017.Models.User;
import com.affiliates.iap.iapspring2017.Models.Vote;
import com.affiliates.iap.iapspring2017.exeptions.InvalidAccountTypeExeption;
import com.affiliates.iap.iapspring2017.exeptions.VoteErrorException;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

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

   private DatabaseReference companyEvalSummaryRef(){
      return voteSummaryRef().child("CompanyEval");
   }

   private DatabaseReference validUsersRef(){ return mainRef().child("ValidUsers"); }

   private DatabaseReference sponsorsRef(){
      return mainRef().child("Sponsors");
   }

   private DatabaseReference scheduleRef(){
      return mainRef().child("Schedule");
   }

   private DatabaseReference postersRef(){
      return mainRef().child("SubmitedProjects");
   }

   private DatabaseReference usersOfInterestRef(){
      return mainRef().child("UsersOfInterest");
   }

   public void getUserData(final String id, final Callback<User> callback) throws InvalidAccountTypeExeption{
      usersRef().child(id).addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot){
            if (!dataSnapshot.hasChildren()) {
               Log.e(TAG, "No user ID Registered " + id );
               callback.failure("No user ID Registered " + id);
               return;
            }
            JSONObject json =  new JSONObject((Map) dataSnapshot.getValue());
            String accType =json.optString("AccountType");
            Log.v(TAG, json.toString());

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

   public void submitGeneralVote(String projecID, int voteType, final Callback<Vote> callback) throws VoteErrorException{
      if(voteType != 0 && voteType != 1)
         callback.failure("Vote type value can only be 1 or 0.");

      final String voteID = generalVoteRef().push().getKey();
      final OverallVote vote = new OverallVote(voteID, projecID, voteType);


      generalVoteRef().child(vote.getStringFromType()).updateChildren(vote.makeJSON())
         .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
               Constants.getCurrentLoggedInUser().vote(vote);
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
      final DatabaseReference ref = generalVoteSummaryRef().child(vote.getStringFromType() + "/" + vote.getProjectID());
      ref.runTransaction(new Transaction.Handler() {
         @Override
         public Transaction.Result doTransaction(MutableData mutableData) {
             Object data = mutableData.getValue();
             try{
                 Integer score = (Integer) ((data == null) ? 0 : data);
                 score += 1;
                 mutableData.setValue(score);
             }catch(ClassCastException e){
                Long score = (Long) ((data == null) ? 0 : data);
                 score += 1;
                 mutableData.setValue(score);
             }
            return Transaction.success(mutableData);
         }
         @Override
         public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
            // Transaction completed
            Log.d(TAG, "runGeneralVoteTransaction() -> onComplete: " + databaseError);

         }
      });
   }

   public void submitCompanyEval(final CompanyVote vote, final Callback<Object> callback) throws VoteErrorException{
      final DatabaseReference ref = companyVoteRef().push();
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
      final DatabaseReference ref = companyEvalSummaryRef().child(vote.getProjectID());
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

   public void getPosters(final Callback<HashMap<Integer, Poster>> callback) {
      postersRef().orderByChild("number").addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot) {
            JSONObject json = new JSONObject((HashMap<String, Object>) dataSnapshot.getValue());
            HashMap<Integer, Poster> poster = new HashMap<Integer, Poster>();
             int last = -1;
            try {
               Iterator<String> x = json.keys();
               Poster p;
               for (int i = 0; i < json.length(); i++) {
                  String name = x.next();
                  Log.d(TAG, "POSTERID: " + name);
                  JSONObject posterObject = json.getJSONObject(name);
                  p = new Poster(posterObject, name);
                  poster.put(p.getPosterNumber(), p);
                  last = p.getPosterNumber();
               }
            } catch (JSONException e) {
               Log.e(TAG, "getPosters(): " + e);
            }
             callback.success(poster);
         }

         @Override
         public void onCancelled(DatabaseError databaseError) {
            callback.failure(databaseError.getMessage()) ;
         }
      });
   }

   public void getPosterTeamMembers(final Poster poster, final Callback callback){
      final ArrayList<IAPStudent> team = new ArrayList<IAPStudent>();
      if(poster.getTeam() == null){
         callback.failure("No members registered in the research: " + poster.getProjectName());
         return;
      }
      final Queue<String> dispatch = new ArrayDeque<String>();
      for (int i = 0; i < poster.getTeam().size(); i++){
         dispatch.add(poster.getTeam().get(i));
          Log.v(TAG, "getTeam()// dispach.add()"+dispatch.element());
         getUserData(poster.getTeam().get(i), new Callback<User>() {
            @Override
            public void success(User user) {
               System.out.println(TAG + team.size() + "  " + poster.getTeam().size());
               if (user instanceof IAPStudent) {
                   Log.v(TAG, "getTeam()// dispach.poll()"+dispatch.poll());;
                  team.add((IAPStudent) user);
                  if (dispatch.isEmpty())
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

   public void getPosterAdvisorMembers(final Poster poster, final Callback<ArrayList<Advisor>> callback){
      final ArrayList<Advisor> advisors = new ArrayList<>();
      if(poster.getAdvisors() == null){
         callback.failure("No advisors registered in the research: " + poster.getProjectName());
         return;
      }
      final Queue<String> dispatch = new ArrayDeque<String>();
      for (String id : poster.getAdvisors()){
         dispatch.add(id);
         getUserData(id, new Callback<User>() {
            @Override
            public void success(User user) {
               if (user instanceof Advisor){
                  dispatch.poll();
                  advisors.add((Advisor) user);
                  if(dispatch.isEmpty())
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

   public void getSponsors(final Callback<ArrayList<Sponsors>> callback){
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

   public void getEvent(final Callback<ArrayList<Event>> callback){
      final ArrayList<Event> events = new ArrayList<>();
      scheduleRef().addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot) {
            JSONObject json = new JSONObject((HashMap<String,Object>)dataSnapshot.getValue());
            Iterator<String> it = json.keys();
            Event e;
            for(int i = 0; i < json.length(); i++){
               JSONObject event = json.optJSONObject(it.next());
               if (event != null)
                  events.add(new Event(event));
            }
            callback.success(events);
         }

         @Override
         public void onCancelled(DatabaseError databaseError) {
            callback.failure(databaseError.getMessage());
         }
      });
   }

   public void getIAPStudentsOfInterest(final Callback<HashMap<String, ArrayList<IAPStudent>>> callback){
      usersOfInterestRef().child(Constants.getCurrentLoggedInUser().getUserID())
              .addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot) {
            final HashMap<String, ArrayList<IAPStudent>> interest = new HashMap<String, ArrayList<IAPStudent>>();
            final ArrayList<IAPStudent> liked = new ArrayList<IAPStudent>();
            final ArrayList<IAPStudent> unLiked = new ArrayList<IAPStudent>();
            final ArrayList<IAPStudent> undecided = new ArrayList<IAPStudent>();
            if (dataSnapshot.getValue() != null){
               Log.v(TAG, "WHAT");
               final JSONObject json = new JSONObject((HashMap<String,Object>) dataSnapshot.getValue());
               final Iterator<String> it = json.keys();
               final Queue<String> diapatch = new ArrayDeque<String>();
               while (it.hasNext()) {
                  final String id = it.next();
                  diapatch.add(id);
                  Log.v(TAG, id);
                  getUserData(id, new Callback<User>() {
                     @Override
                     public void success(User data) {
                        diapatch.poll();
                        if(json.optString(id).equals("Like")) {
                           Log.v(TAG, "like");
                           liked.add((IAPStudent) data);
                        }else if (json.optString(id).equals("Unlike")) {
                           Log.v(TAG, "unlike");
                           unLiked.add((IAPStudent) data);
                        } else if (json.optString(id).equals("Undecided")){
                           undecided.add((IAPStudent) data);
                        }
                        if(diapatch.isEmpty()){
                           interest.put("liked", liked);
                           interest.put("unliked", unLiked);
                           interest.put("undecided", undecided);
                           callback.success(interest);
                        }
                     }
                     @Override
                     public void failure(String message) {
                        Log.e(TAG, message);
                     }
                  });
               }
            } else {
               Log.v(TAG, "getIAPStudentsOfInterest(): No hay nada");
               interest.put("liked", liked);
               interest.put("unliked", unLiked);
               interest.put("undecided", undecided);
               callback.success(interest);
            }
         }
         @Override
         public void onCancelled(DatabaseError databaseError) {
            callback.failure(databaseError.getMessage());
         }
      });
   }

   public void setInterestForStudent(final String id, final String interest){
      usersOfInterestRef().child(Constants.getCurrentLoggedInUser().getUserID())
              .updateChildren(new HashMap<String, Object>() {{ put(id, interest);}})
              .addOnCompleteListener(new OnCompleteListener<Void>() {
                 @Override
                 public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                       Log.v(TAG, "setInterestForStudent(): succesfull -> " + id);
                    } else {
                       Log.v(TAG, "setInterestForStudent(): unsuccesfull -> " + id);
                    }
                 }
              });
   }

   public void updateUserData(final User user,final Uri uri, final Callback<User> callback){
       if(uri != null){
            DataService.sharedInstance().uploadUserResume(user, uri, new Callback<User>() {
                @Override
                public void success(User data) {
                    Log.v(TAG, "uploadUserResume() succesfull");
                    usersRef().child(Constants.getCurrentLoggedInUser().getUserID())
                            .updateChildren(user.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                           if(!task.isSuccessful()){
                              callback.failure("DataService.class -> updateUserData(): " + task.getException().getMessage());
                           }
                           callback.success(user);
                        }
                    });
                }
                @Override
                public void failure(String message) {

                }
            });
       }else{
           usersRef().child(user.getUserID())
                   .updateChildren(user.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
               @Override
               public void onComplete(@NonNull Task<Void> task) {
                   if(!task.isSuccessful()){
                      callback.failure("DataService.class -> updateUserData(): " + task.getException().getMessage());
                   }
                   callback.success(user);
               }
           });
       }
   }

   public void uploadUserImage(final User user, final Uri uri, final Callback<User> callback){
      Log.v(TAG, "uploadUserImage()");
      // Create file metadata including the content type
      StorageMetadata metadata = new StorageMetadata.Builder()
              .setContentType("image/png")
              .build();

      //Register observers to listen for when the download is done or if it fails
      StorageReference s = FirebaseStorage.getInstance().getReference()
              .child("ProfilePictures").child(user.getUserID()+"_ProfileImage.png");
      Log.v(TAG, "uploadUserImage()+ storageRef");
      UploadTask uploadTask = s.putFile(uri,metadata);
      uploadTask.addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception exception) {
            // Handle unsuccessful uploads
            Log.v(TAG, "uploadUserImage() ->  task failure");
            callback.failure(exception.getMessage());
         }
      }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
         @Override
         public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
            Log.v(TAG, "uploadUserImage() ->  task success");
            Uri downloadUrl = taskSnapshot.getDownloadUrl();
            Constants.getCurrentLoggedInUser().setPhotoURL(downloadUrl.toString());
            callback.success(user);
         }
      });
   }

   private void uploadUserResume(final User user, Uri uri,final Callback<User> callback){
       Log.v(TAG, "uploadUserResume()");
       // Create file metadata including the content type
       StorageMetadata metadata = new StorageMetadata.Builder()
               .setContentType("application/pdf")
               .build();

       //Register observers to listen for when the download is done or if it fails
       StorageReference s = FirebaseStorage.getInstance().getReference().child("Resumes")
               .child(user.getUserID()+"_IAPStudentResume.pdf");
       Log.v(TAG, "uploadUserImage()+ storageRef");
       UploadTask uploadTask = s.putFile(uri,metadata);
       uploadTask.addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception exception) {
               // Handle unsuccessful uploads
               Log.v(TAG, "uploadUserResume() -> task failure");
               callback.failure(exception.getMessage());
           }
       }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
           @Override
           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
               Log.v(TAG, "uploadUserResume() -> task success");
               Uri downloadUrl = taskSnapshot.getDownloadUrl();
               ((IAPStudent)Constants.getCurrentLoggedInUser()).setResumeURL(downloadUrl.toString());
               callback.success(user);
           }
       });
   }

   public void verifyUser(User.AccountType accountType, final String email , final Callback<User> callback) {
      switch (accountType) {
         case Advisor:
            validUsersRef().child("Advisors").orderByChild("Email")
                    .equalTo(email)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(DataSnapshot dataSnapshot) {
                          if (dataSnapshot.exists()) {
                             JSONObject json = new JSONObject((HashMap<String, Object>) dataSnapshot.getValue());
                              Constants.curentRegisteringUserData = json.optJSONObject(parseEmailToKey(email));
                             Advisor advisor = new Advisor(json.optJSONObject(parseEmailToKey(email)));
                             Log.v(TAG, "Advisor Valid");
                             callback.success(advisor);
                          } else {
                             callback.failure("verifyUser(): User not valid Advisor");
                          }
                       }

                       @Override
                       public void onCancelled(DatabaseError databaseError) {
                          callback.failure(databaseError.getMessage());
                       }
                    });
            break;

         case IAPStudent:
            validUsersRef().child("IAPStudent").orderByChild("Email")
                    .equalTo(email)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {
                  if (dataSnapshot.exists()) {
                     JSONObject json = new JSONObject((HashMap<String, Object>) dataSnapshot.getValue());
                      Constants.curentRegisteringUserData = json.optJSONObject(parseEmailToKey(email));
                      IAPStudent student = new IAPStudent(json.optJSONObject(parseEmailToKey(email)));
                     Log.v(TAG, "IAPStudent Valid");
                     callback.success(student);
                  } else {
                     callback.failure("verifyUser(): User not valid IAPStudent");
                  }
               }

               @Override
               public void onCancelled(DatabaseError databaseError) {
                  callback.failure(databaseError.getMessage());
               }
            });
            break;

         case CompanyUser:
            validUsersRef().child("CompanyRep").orderByChild("Email")
                    .equalTo(email)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {
                  if (dataSnapshot.exists()) {
                     JSONObject json = new JSONObject((HashMap<String, Object>) dataSnapshot.getValue());
                      Constants.curentRegisteringUserData = json.optJSONObject(parseEmailToKey(email));
                      CompanyUser companyUser = new CompanyUser(json.optJSONObject(parseEmailToKey(email)));
                     Log.v(TAG, "Company User Valid");
                     callback.success(companyUser);
                  } else {
                     callback.failure("verifyUser(): User not valid CompanyUser");
                  }
               }

               @Override
               public void onCancelled(DatabaseError databaseError) {
                  callback.failure(databaseError.getMessage());
               }
            });
            break;

         default:
            UPRMAccount uprmAccount = new UPRMAccount(email);
            callback.success(uprmAccount);
            Log.v(TAG, "UPRM User Created");
            break;
      }
   }

   public void createNewUser(final User user, String password, final FirebaseAnalytics fbAnalytics, final Callback<String> callback){
      FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.getEmail(), password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
         @Override
         public void onComplete(@NonNull final Task<AuthResult> task) {
            if(!task.isSuccessful()){
               Log.v(TAG, task.getException().getMessage());
               callback.failure(task.getException().getLocalizedMessage());
               return;
            }
            final FirebaseUser firebaseUser = task.getResult().getUser();
            user.setID(firebaseUser.getUid());
            updateUserData(user, null, new Callback<User>() {
               @Override
               public void success(User data) {
                  fbAnalytics.setUserProperty("Sex", user.getGender());
                  fbAnalytics.setUserProperty("AccountType", user.getAccountType().toString());
                  firebaseUser.sendEmailVerification().addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                        callback.failure(e.getMessage());
                     }
                  });

                  if(user.getAccountType().equals(User.AccountType.Advisor)){
                     addAdvisorToProjects((Advisor) user, new Callback<String>() {
                           @Override
                           public void success(String data) {
                              Log.v(TAG, data);
                              callback.success("Advisor account created successfully");
                           }

                           @Override
                           public void failure(String message) {

                               Log.e(TAG, message);
                           }
                        });
                  } else if (user.getAccountType().equals(User.AccountType.CompanyUser)){
                     addCompanyRepToSponsor((CompanyUser) user, new Callback<String>() {
                        @Override
                        public void success(String data) {
                           Log.v(TAG, data);
                           callback.success("CompanyUser account created successfully");
                        }

                        @Override
                        public void failure(String message) {
                           Log.e(TAG, message);
                        }
                     });
                  } else if (user.getAccountType().equals(User.AccountType.IAPStudent)) {
                     addIAPStudentToProjects((IAPStudent) user, new Callback<String>() {
                        @Override
                        public void success(String data) {
                           Log.v(TAG, data);
                           callback.success("IAPStudent account created successfully");
                        }

                        @Override
                        public void failure(String message) {
                           Log.e(TAG, message);
                        }
                     });
                  } else {
                     callback.success("UPRMAccount created successfully");
                  }
               }

               @Override
               public void failure(String message) {
                  Log.e(TAG, message);
                  callback.equals(message);
               }
            });
         }
      });
   }

   private void addAdvisorToProjects(final Advisor advisor, final Callback<String> callback){
      final Queue<String> dispatch = new ArrayDeque<>();
      if (advisor.getProjects() != null) {
         for(String project : advisor.getProjects()){
            dispatch.add(project);
            postersRef().child(project).child("Advisors").updateChildren(new HashMap<String, Object>(){{
               put(advisor.getUserID(), true);
            }}).addOnCompleteListener(new OnCompleteListener<Void>() {
               @Override
               public void onComplete(@NonNull Task<Void> task) {
                  if (!task.isSuccessful())
                     callback.failure("addAdvisorToProjects(): " +task.getException().getMessage());
                  dispatch.poll();
                  if(dispatch.isEmpty())
                     callback.success("Advisor added to all projects successful");
               }
            });
         }
      }
      else{
          callback.success("Advisor doesn't has any Poster");
      }

   }

   private void addIAPStudentToProjects(final IAPStudent student, final Callback<String> callback){
      final Queue<String> dispatch = new ArrayDeque<>();
      Set<String> projectIDs = student.getProyectMap().keySet();
      for(final String project : projectIDs){
         dispatch.add(project);
         postersRef().child(project).child("TeamMembers").updateChildren(new HashMap<String, Object>(){{
            put(student.getUserID(), true);
         }}).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               if (!task.isSuccessful()) {
                  callback.failure("addIAPStudentToProjects(): " + task.getException().getMessage());
               }
               dispatch.poll();
               if(dispatch.isEmpty())
                  callback.success("Student added to all projects successful");
            }
         });
      }
   }

   private void addCompanyRepToSponsor(final CompanyUser represntative, final Callback<String> callback){
      sponsorsRef().child(represntative.getCompanyName()).child("Representatives")
              .updateChildren(new HashMap<String, Object>() {{
                 put(represntative.getUserID(), true);
              }}).addOnCompleteListener(new OnCompleteListener<Void>() {
         @Override
         public void onComplete(@NonNull Task<Void> task) {
            if(task.isSuccessful()){
               callback.success("Representative added to sponsor successful");
            } else {
               callback.failure("addCompanyRepToSponsor(): " + task.getException().getMessage());
            }
         }
      });
   }


    public static String parseEmailToKey(String email){
        int i, split = email.indexOf("@");
        email = email.substring(0, split);
        String[] k = email.split("[.]+");
        String str = "";

        for(i = 0; i < k.length-1; i++)
            str += k[i] + "_";
        return str + k[i];
    }

    public static String keyToName(String key){
        String ntr = "";
        int split = key.indexOf("_");
        ntr += Character.toUpperCase(key.charAt(0));
        ntr += key.substring(1,split) + " ";
        ntr += Character.toUpperCase(key.charAt(split+1));
        ntr += key.substring(split+2);
        return ntr.replaceAll("[1234567890]+", "");
    }

}