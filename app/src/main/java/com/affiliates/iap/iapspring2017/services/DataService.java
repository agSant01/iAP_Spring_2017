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
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
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
   private static DataService mDServiceInstance = new DataService();
   
   public static DataService sharedInstance(){
      return mDServiceInstance;
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

   private DatabaseReference feedBackRef() {
      return mainRef().child("ProjectFeedback");
   }


   /**
    * Gets user data
    * @param id            user firebase UID
    * @param callback      callback used to return the data and keep the process asynchronous
    */
   public void getUserData(final String id, final Callback<User> callback) {
      usersRef().child(id).addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot){
            if (!dataSnapshot.hasChildren()) {
               FirebaseCrash.log(TAG + ": No user ID Registered ");
               Log.e(TAG, "No user ID Registered " + id );
               callback.failure("No user ID Registered " + id);
               return;
            }

            // get FB response and parde it as a JSON
            JSONObject json =  new JSONObject((Map) dataSnapshot.getValue());

            // get accountType
            String accType = json.optString("AccountType");
            Log.v(TAG, json.toString());

            try {
               // build the user object based on the correct account type
               // return it through the callback
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
                     FirebaseCrash.log("DataSevice -> getUserData(): Invalid account type" + accType);
                     callback.failure("DataSevice -> getUserData(): Invalid account type" + accType);
                     return;
               }
            } catch (JSONException e){
               FirebaseCrash.log(TAG + "DataService -> getUserData() / switch()"+ e);
               Log.e(TAG, "DataService -> getUserData() / switch()", e);
               e.printStackTrace();
            }
            FirebaseCrash.log("No user ID Registered");
            callback.failure("No user ID Registered");
         }
         @Override
         public void onCancelled(DatabaseError databaseError) {
            // an error with the communication occurred
            FirebaseCrash.log(TAG + ": getUserData() -> " + databaseError.toString());
            Log.e(TAG, databaseError.toString());
         }
      });
   }

   /**
    * Used for every user type except company user, to set as voted on of the categories:
    * poster o presentation
    * @param user   user object
    * @param vote   vote object
    */
   public void setVoted(User user, final OverallVote vote){
      if(user.getAccountType() != User.AccountType.CompanyUser){     // user is not company
         usersRef().child(user.getUserID())                          // get reference to node
                   .child("Voted")
                   .updateChildren(                                  // update the corresponding node
                      new HashMap<String, Object>(){{                // given by vote.getStringFromTYpe()
                         put(vote.getStringFromType(), true);
                      }});
      }
   }

   /**
    * Submit general vote
    * @param projectID project id
    * @param voteType  vote type
    * @param callback  used to return the completion status
    */
   public void submitGeneralVote(String projectID, int voteType, final Callback<Vote> callback) {
      if(voteType != 0 && voteType != 1) {                   // is invalid vote type
         FirebaseCrash.log(TAG +": submitGeneralVoe() -> Vote type value can only be 1 or 0.");
         callback.failure("Vote type value can only be 1 or 0.");
      }

      // generate new reference in DB
      final String voteID = generalVoteRef().push().getKey();

      // create new overall vote object
      final OverallVote vote = new OverallVote(voteID, projectID, voteType);

      // get reference
      generalVoteRef().child(vote.getStringFromType()).updateChildren(vote.makeJSON())
         .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
               // relies on leaving the voting method individually on each user type
               Constants.getCurrentLoggedInUser().vote(vote);
               // do a transaction: this is for system redundancy
               runGeneralVoteTransaction(vote);
               callback.success(null);
            }})
         .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               FirebaseCrash.log("DataService.class -> submitCGeneralVote() : " + e.toString());
               callback.failure("DataService.class -> submitCGeneralVote() : " + e.toString());
            }
         });
   }

   /**
    * Run transaction on the votes node
    * @param vote    vote object
    */
   private void runGeneralVoteTransaction(OverallVote vote){
      // gets the reference if exists, otherwise creates a new one
      final DatabaseReference ref = generalVoteSummaryRef()
              .child(vote.getStringFromType() + "/" + vote.getProjectID());

      // run transaction on that reference
      ref.runTransaction(new Transaction.Handler() {
         @Override
         public Transaction.Result doTransaction(MutableData mutableData) {
             Object data = mutableData.getValue();
             try{
                 // add one extra vote for that category
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
            FirebaseCrash.log(TAG + ": runGeneralVoteTransaction() -> onComplete: " + databaseError);
            Log.d(TAG, "runGeneralVoteTransaction() -> onComplete: " + databaseError);

         }
      });
   }

   /**
    * Submit company evaluation
    * @param vote       vote object
    * @param callback   used to return completion status
    */
   public void submitCompanyEval(final CompanyVote vote, final Callback<Object> callback) {
      final DatabaseReference ref = companyVoteRef().push();      // create new reference for the vote
      ref.updateChildren(vote.makeJSON())                         // update data for the reference
         .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               runCompanyVoteTransaction(vote);                   // when the update of the data on ref finishes
               callback.success(null);                            // run a transaction of company vote
            }
         })
         .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               callback.failure("DataService.class -> submitCompanyEval(): " + e.toString() );
            }
         });
   }

   /**
    * Run transaction on the company votes node
    * @param vote    vote object
    */
   private void runCompanyVoteTransaction(final CompanyVote vote){
      // gets the reference if exists, otherwise creates a new one
      final DatabaseReference ref = companyEvalSummaryRef().child(vote.getProjectID());

      // run transaction on the reference child: Presentation
      ref.child("Presentation").runTransaction(new Transaction.Handler() {
         @Override
         public Transaction.Result doTransaction(MutableData mutableData) {
            // add to the current score, the vote score
            Integer score = ((mutableData.getValue() == null) ? 0 : mutableData.getValue(Integer.class));
            score += vote.getPresentationTotal();
            // set new added score
            mutableData.setValue(score);
            return Transaction.success(mutableData);
         }

         @Override
         public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
            FirebaseCrash.log(TAG + ": runCompanyVoteTransaction() -> onComplete(): " + databaseError);
            Log.d(TAG, "runCompanyVoteTransaction() -> onComplete(): " + databaseError);
         }
      });

      // run transaction on the reference child: Poster
      ref.child("Poster").runTransaction(new Transaction.Handler() {
         @Override
         public Transaction.Result doTransaction(MutableData mutableData) {
            // add to the current score, the vote score
            Integer score =  ((mutableData.getValue() == null) ? 0 : mutableData.getValue(Integer.class));
            score += vote.getPosterTotal();
            // set new added score
            mutableData.setValue(score);
            return Transaction.success(mutableData);
         }

         @Override
         public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
            FirebaseCrash.log(TAG + "runCompanyVoteTransaction() -> onComplete(): " + databaseError);
            Log.d(TAG, "runCompanyVoteTransaction() -> onComplete(): " + databaseError);
         }
      });
   }

   /**
    * Get posters
    * @param callback used to return HashMap of the posters
    */
   public void getPosters(final Callback<HashMap<Integer, Poster>> callback) {
      postersRef().orderByChild("number").addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot) {
            JSONObject json = new JSONObject((HashMap<String, Object>) dataSnapshot.getValue());
            HashMap<Integer, Poster> poster = new HashMap<>();
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
               FirebaseCrash.log(TAG + ": getPosters() -> " + e);
               Log.e(TAG, "getPosters(): " + e);
            }
             callback.success(poster);
         }

         @Override
         public void onCancelled(DatabaseError databaseError) {
            FirebaseCrash.log(TAG + ": getPosters() -> " + databaseError.getMessage());
            callback.failure(databaseError.getMessage()) ;
         }
      });
   }

   /**
    * Get Poster team members
    * @param poster     poster object
    * @param callback   used to return an arraylist of the team members
    */
   public void getPosterTeamMembers(final Poster poster, final Callback<ArrayList<IAPStudent>> callback){
      postersRef().child(poster.getPosterID()).child("TeamMembers")
              .addListenerForSingleValueEvent(new ValueEventListener() {
                 @Override
                 public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists() || !dataSnapshot.hasChildren()){
                       callback.failure("Empty");
                       return;
                    }
                    JSONObject json = new JSONObject((HashMap<String, Object>) dataSnapshot.getValue());
                    Log.e(TAG, json.toString());
                    Iterator<String> ids = json.keys();
                    final Queue<String> dispatch = new ArrayDeque<>();
                    final ArrayList<IAPStudent> team = new ArrayList<>();
                    while (ids.hasNext()){
                       String id  = ids.next();
                       dispatch.add(id);                          // add the id of each student
                       getUserData(id, new Callback<User>() {     // get data for each student
                          @Override
                          public void success(User user) {
                             if (user instanceof IAPStudent){     // is IAPStudent
                                dispatch.poll();                  // remove last added id
                                team.add((IAPStudent) user);      // add user to team array
                                if(dispatch.isEmpty()) {          // if dispatch is empty then all students have been processed
                                   callback.success(team);        // return a success
                                }
                             }
                          }

                          @Override
                          public void failure(String message) {
                             FirebaseCrash.log(TAG + ": getPosterTeamMembers() -> " + message);
                             dispatch.poll();                     // remove last added is
                             callback.failure(message);           // return a failure
                          }
                       });
                    }

                 }

                 @Override
                 public void onCancelled(DatabaseError databaseError) {

                 }
              });
   }

   /**
    * Get poster advisors
    * @param poster     poster object
    * @param callback   used to return advisor array
    */
   public void getPosterAdvisorMembers(final Poster poster, final Callback<ArrayList<Advisor>> callback){
      postersRef().child(poster.getPosterID())
              .child("Advisors")
              .addListenerForSingleValueEvent(new ValueEventListener() {
                 @Override
                 public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists() || !dataSnapshot.hasChildren()) {
                       callback.failure("Empty");
                       return;
                    }
                    JSONObject json = new JSONObject((HashMap<String, Object>) dataSnapshot.getValue());
                    Iterator<String> ids = json.keys();
                    final Queue<String> dispatch = new ArrayDeque<>();
                    final ArrayList<Advisor> advisors = new ArrayList<>();
                     while (ids.hasNext()){
                        String id  = ids.next();
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
                              FirebaseCrash.log(TAG + ": getPosterAdvisors() -> " + message);
                              dispatch.poll();
                              callback.failure(message);
                           }
                        });
                     }
                 }
                 @Override
                 public void onCancelled(DatabaseError databaseError) {
                    FirebaseCrash.log(databaseError.toString());
                 }
              });
   }

   /**
    * Get sponsor information
    * @param callback used to return an array of the sponsors
    */
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
            callback.success(sponsorsMap);                                    // return success with sponsors array
         }

         @Override
         public void onCancelled(DatabaseError databaseError) {
            FirebaseCrash.log(TAG +": getSponsors() -> " + databaseError.getMessage());
            callback.failure(databaseError.getMessage());
         }
      });
   }

   /**
    * Get data for the events
    * @param callback returns the array of events
    */
   public void getEvent(final Callback<ArrayList<Event>> callback){
      final ArrayList<Event> events = new ArrayList<>();
      scheduleRef().addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot) {
            JSONObject json = new JSONObject((HashMap<String,Object>)dataSnapshot.getValue());
            Iterator<String> it = json.keys();
            for(int i = 0; i < json.length(); i++){
               JSONObject event = json.optJSONObject(it.next());
               if (event != null)
                  events.add(new Event(event));
            }
            callback.success(events);                                          // return success with events array
         }

         @Override
         public void onCancelled(DatabaseError databaseError) {
            callback.failure(databaseError.getMessage());
         }
      });
   }

    /**
     * Get all students of interests with their respective labels and return them as a Map of [label: array{student_object}]
     * @param callback  returns all the students of interest in HashMap<String, ArrayList<student_object>> form
     */
   public void getIAPStudentsOfInterest(final Callback<HashMap<String, ArrayList<IAPStudent>>> callback){
      usersOfInterestRef().child(Constants.getCurrentLoggedInUser().getUserID())
              .addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot) {
            // create the HashMap to return
            final HashMap<String, ArrayList<IAPStudent>> interest = new HashMap<>();

            // create arrays to populate
            final ArrayList<IAPStudent> liked = new ArrayList<>();
            final ArrayList<IAPStudent> unLiked = new ArrayList<>();
            final ArrayList<IAPStudent> undecided = new ArrayList<>();
            if (dataSnapshot.getValue() != null){                                       // verifies that there is data on reference
               Log.v(TAG, "WHAT");
               final JSONObject json = new JSONObject(
                       (HashMap<String,Object>) dataSnapshot.getValue());
               final Iterator<String> studentIDs = json.keys();                         // get student ids from the json
               final Queue<String> dispatch = new ArrayDeque<>();                       // used to keep task synchronous
               while (studentIDs.hasNext()) {
                  final String id = studentIDs.next();
                  dispatch.add(id);                                                     // add id to the dispatch
                  Log.v(TAG, id);
                  getUserData(id, new Callback<User>() {                                // get student data
                     @Override
                     public void success(User data) {
                        dispatch.poll();                                                // pop successful task from dispatch
                        if(json.optString(id).equals("Like")) {                         // student label == Like
                           Log.v(TAG, "like");
                           liked.add((IAPStudent) data);
                        }else if (json.optString(id).equals("Unlike")) {                // student label == Unlike
                           Log.v(TAG, "unlike");
                           unLiked.add((IAPStudent) data);
                        } else if (json.optString(id).equals("Undecided")){             // student label == Undecided
                           undecided.add((IAPStudent) data);
                        }
                        if(dispatch.isEmpty()){                                         // if dispatch is empty all student data was queried
                           Log.v(TAG, "getStudInterest sec");
                           interest.put("liked", liked);
                           interest.put("unliked", unLiked);
                           interest.put("undecided", undecided);
                           callback.success(interest);
                        }
                     }
                     @Override
                     public void failure(String message) {
                        dispatch.poll();
                        if(dispatch.isEmpty()){
                            Log.v(TAG, "getStudInterest failure last");
                            interest.put("liked", liked);
                            interest.put("unliked", unLiked);
                            interest.put("undecided", undecided);
                            callback.success(interest);
                        }
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
            FirebaseCrash.log(TAG + ": getIAPStudentsOfInterest() -> " + databaseError);
            callback.failure(databaseError.getMessage());
         }
      });
   }

    /**
     * Set interest for a student
     * @param id            student firebase UID
     * @param interest     future status of student
     */
   public void setInterestForStudent(final String id, final String interest){
      usersOfInterestRef().child(Constants.getCurrentLoggedInUser().getUserID())                // get reference to the user node of userOfInterest
              .updateChildren(new HashMap<String, Object>() {{ put(id, interest);}})            // update child data of that node
              .addOnCompleteListener(new OnCompleteListener<Void>() {
                 @Override
                 public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){                                                   // task is successful
                       Log.v(TAG, "setInterestForStudent(): succesfull -> " + id);
                    } else {                                                                    // task is unsuccessful
                       FirebaseCrash.log(TAG + "setInterestForStudent(): unsuccesfull ");
                       Log.v(TAG, "setInterestForStudent(): unsuccesfull -> " + id);
                    }
                 }
              });
   }

    /**
     * Update user data
     * @param user          user object
     * @param resumeUri     resume uri
     * @param callback      returns updated User object
     */
   public void updateUserData(final User user,final Uri resumeUri, final Callback<User> callback){
       if(resumeUri != null){                                                                        // if resumeUri is not null update resume and user info
            uploadUserResume(user, resumeUri, new Callback<User>() {
                @Override
                public void success(User data) {                                                     // after success of updating resume update user info
                    Log.v(TAG, "uploadUserResume() succesfull");
                    usersRef().child(Constants.getCurrentLoggedInUser().getUserID())                 // get user ref
                            .updateChildren(user.toMap())                                            // update values
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                           if(!task.isSuccessful()){
                              FirebaseCrash.log("DataService.class -> updateUserData(): " + task.getException().getMessage());
                              callback.failure("DataService.class -> updateUserData(): " + task.getException().getMessage());
                           }
                           callback.success(user);                                                   // return updated user object
                        }
                    });
                }
                @Override
                public void failure(String message) {
                   FirebaseCrash.log(TAG + ": uploadUserResume() -> " +message);
                }
            });
       }else{                                                                                        // resumeUri is null: only update user data
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

    /**
     * Upload user image to Firebase Storage and update it on user information node
     * @param user          user object
     * @param bytes         image in bytes array
     * @param callback      returns updated user object
     */
   public void uploadUserImage(final User user, byte[] bytes, final Callback<User> callback){
      Log.v(TAG, "uploadUserImage()");
      // Create file metadata including the content type
      StorageMetadata metadata = new StorageMetadata.Builder()                                  // creates metadata for the image upload
              .setContentType("image/png")
              .build();

      //Register observers to listen for when the download is done or if it fails
      StorageReference s = FirebaseStorage.getInstance().getReference()                         // get (or create reference if not exists) of the profile image in FB storage
              .child("ProfilePictures").child(user.getUserID()+"_ProfileImage.png");
      Log.v(TAG, "uploadUserImage()+ storageRef");
      UploadTask uploadTask = s.putBytes(bytes ,metadata);                                      // start upload and save the uploadTask object

      uploadTask.addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception exception) {                                  // failure
            // Handle unsuccessful uploads
            Log.v(TAG, "uploadUserImage() ->  task failure");
            FirebaseCrash.log(TAG+  ": uploadUserImage() ->  task failure");
            callback.failure(exception.getMessage());
         }
      }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
         @Override
         public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {                          // success
            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
            Log.v(TAG, "uploadUserImage() ->  task success: " + taskSnapshot.getTotalByteCount());
            Uri downloadUrl = taskSnapshot.getDownloadUrl();                                    // get download url
            Constants.getCurrentLoggedInUser().setPhotoURL(downloadUrl.toString());             // update data on user
            callback.success(user);
         }
      });
   }

    /**
     * Upload user resume on Storage and update its downloadURL on the user information node on DB
     * @param user          user object
     * @param uri           resume uri
     * @param callback      return updated user object
     */
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

    /**
     * Verify if the user has the permission to create an account with the asked privileges
     * @param accountType       account type requested
     * @param email             user email
     * @param callback          returns user object
     */
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
                              String key = json.keys().next();
                             Log.v("KEY", key);
                             Constants.curentRegisteringUserData = json.optJSONObject(key);
                             Advisor advisor = new Advisor(json.optJSONObject(key));
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
                     String key = json.keys().next();
                      Constants.curentRegisteringUserData = json.optJSONObject(key);
                      CompanyUser companyUser = new CompanyUser(json.optJSONObject(key));
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

   public void createNewUser(final User user, String password, final Callback<String> callback){
      FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.getEmail(), password)
              .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
         @Override
         public void onComplete(@NonNull final Task<AuthResult> task) {
            if (!task.isSuccessful()) {
               Log.v(TAG, task.getException().getMessage());
               FirebaseCrash.log(TAG + "createNewUser() -> " + task.getException().getMessage());
               callback.failure(task.getException().getLocalizedMessage());
               return;
            } else {
               final FirebaseUser firebaseUser = task.getResult().getUser();
               user.setID(firebaseUser.getUid());
               updateUserData(user, null, new Callback<User>() {
                  @Override
                  public void success(User data) {
                     firebaseUser.sendEmailVerification().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                           callback.failure(e.getMessage());
                        }
                     });

                     if (user.getAccountType().equals(User.AccountType.Advisor)) {
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
                     callback.failure(message);
                  }
               });
            }
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
                  dispatch.poll();
                  if (!task.isSuccessful())
                     callback.failure("addAdvisorToProjects(): " +task.getException().getMessage());
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

   private void addIAPStudentToProjects(final IAPStudent student, final Callback<String> callback) {
      final Queue<String> dispatch = new ArrayDeque<>();
      Set<String> projectIDs = student.getProyectMap().keySet();
      for (final String project : projectIDs) {
         dispatch.add(project);
         postersRef().child(project).child("TeamMembers").updateChildren(new HashMap<String, Object>() {{
            put(student.getUserID(), true);
         }}).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               dispatch.poll();
               if (!task.isSuccessful()) {
                  callback.failure("addIAPStudentToProjects(): " + task.getException().getMessage());
               }
               if (dispatch.isEmpty())
                  callback.success("Student added to all projects successful");
            }
         });
      }
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

    /**
     * Submit feedback
     * @param subject     feedback subject
     * @param sug         suggestion
     * @param callback    returns success or failure
     */
   public void submitFeedback(final String subject, final String sug, final Callback<String> callback){
      feedBackRef().push().updateChildren(new HashMap<String, Object>(){{
         put("Subject", subject);
         put("Text",  sug);
      }}).addOnCompleteListener(new OnCompleteListener<Void>() {
         @Override
         public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()){
               callback.success("Succes");
               return;
            }
            callback.failure("error sending feedback");
         }
      });
   }

}