//
//  Poster.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Models;

import com.affiliates.iap.iapspring2017.exeptions.InvalidAccountTypeExeption;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.services.DataService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Poster {
   private String _abstract;
   private ArrayList<String> advisors;
   private ArrayList<String> team;
   private String posterURL;
   private String posterID;
   private ArrayList<String> categories;
   private String projectName;
   private ArrayList<String> sponsors;

   public Poster(JSONObject data, String id) throws InvalidAccountTypeExeption, JSONException{
      this._abstract = data.getString("Abstract");
      this.advisors = parseData(data.getJSONArray("AdvisorRef"));
      this.team = parseData(data.getJSONArray("TeamMembers"));
      this.posterURL = data.getString("PosterImg");
      this.categories = parseData(data.getJSONArray("Categories"));
      this.posterID = id;
      this.projectName = data.getString("ProjectName");
      this.sponsors = parseData(data.getJSONArray("Sponsors"));
   }

   public void getTeamData(Callback callback){
      final ArrayList<IAPStudent> team = new ArrayList<>();
      for (String id : this.team){
            DataService.sharedInstance().getUserData(id, new Callback<User>() {
               @Override
               public void success(User user) {
                  if(user instanceof IAPStudent){
                     team.add((IAPStudent) user);
                  }
               }
               @Override
               public void failure(String message) {
                  throw new InvalidAccountTypeExeption("Poster.class -> getTeamData(): " + message);
               }
            });
      }

   }

   private static ArrayList<String> parseData(JSONArray data) throws JSONException{
      ArrayList<String> ids = new ArrayList<String>();
      for (int i = 0; i < data.length(); i++)
         ids.add(data.getString(i));
      return ids;
   }

   public String get_abstract() {
      return _abstract;
   }

   public ArrayList<String> getAdvisors() {
      return advisors;
   }

   public ArrayList<String> getTeam() {
      return team;
   }

   public String getPosterURL() {
      return posterURL;
   }

   public String getPosterID() {
      return posterID;
   }

   public ArrayList<String> getCategories() {
      return categories;
   }

   public String getProjectName() {
      return projectName;
   }

   public ArrayList<String> getSponsors() {
      return sponsors;
   }
}
