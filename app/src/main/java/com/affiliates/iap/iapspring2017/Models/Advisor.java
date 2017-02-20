//
//  Advisor.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Models;

import com.affiliates.iap.iapspring2017.exeptions.InvalidAccountTypeExeption;
import com.affiliates.iap.iapspring2017.exeptions.VoteErrorException;
import com.affiliates.iap.iapspring2017.interfaces.UserDelegate;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Advisor extends Users implements UserDelegate {
   private String[] classes;
   private String department;
   private String photoURL;
   private String[] projects;
   private String researchIntent;
   private Boolean voted;
   private String webPage;

   public Advisor(JSONObject data, AccountType accountType, String id)
      throws InvalidAccountTypeExeption, JSONException{
      this(checkType(accountType), data, accountType, id );
   }

   private Advisor(Void d, JSONObject data, AccountType accountType, String id) throws JSONException{
      super(data.getString("Email"), data.getString("Name"), id, accountType);
      this.classes = getClassesFromJSON(data.getJSONArray("Classes"));
      this.projects = getProjectsFromJSON(data.getJSONArray("Projects"));
      this.department = data.getString("Department");
      this.photoURL = data.getString("PhotoURL");
      this.researchIntent = data.getString("ResearchIntent");
      this.voted = data.getBoolean("Voted");
      this.webPage = data.getString("webpage");
   }

   private static Void checkType(AccountType accountType)
      throws InvalidAccountTypeExeption, JSONException{
      if (accountType != AccountType.Advisor)
         throw new InvalidAccountTypeExeption("Advisor(): Invalid account type; " + accountType);
      return null;
   }

   private static String[] getClassesFromJSON(JSONArray data) throws JSONException{
      ArrayList<String> classes = new ArrayList<>();
      for (int i = 0; i < data.length(); i++){
         classes.add(data.getString(i));
      }
      return (String[]) classes.toArray();
   }

   private static String[] getProjectsFromJSON(JSONArray data) throws JSONException{
      ArrayList<String> projects = new ArrayList<>();
      for (int i = 0; i < data.length(); i++){
         projects.add(data.getString(i));
      }
      return (String[]) projects.toArray();
   }

   @Override
   public void vote(String projectID) throws VoteErrorException {

   }

   public void setVoted() {
      this.voted = true;
      FirebaseDatabase.getInstance().getReference().child("User").orderByChild(getUserID());
   }
}
