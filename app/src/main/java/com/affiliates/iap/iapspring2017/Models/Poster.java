//
//  PosterEval.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Models;

import com.affiliates.iap.iapspring2017.exeptions.InvalidAccountTypeExeption;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class Poster {
   private ArrayList<String> categories;
   private ArrayList<String> sponsors;
   private ArrayList<String> advisors;
   private ArrayList<String> team;
   private String projectName;
   private String posterDptm;
   private String _abstract;
   private String posterURL;
   private int posterNumber;
   private String posterID;

   public Poster(JSONObject data, String id) throws InvalidAccountTypeExeption, JSONException{
      this.categories = parseData(data.optJSONObject("Categories"));
      this.sponsors = parseData(data.optJSONObject("Sponsors"));
      this.advisors = parseData(data.optJSONObject("Advisors"));
      this.team = parseData(data.optJSONObject("TeamMembers"));
      this.projectName = data.optString("ProjectName");
      this.posterDptm = data.optString("Department");
      this.posterURL = data.optString("PosterImg");
      this._abstract = data.optString("Abstract");
      this.posterNumber = data.optInt("number");
      this.posterID = id;
   }

   private static ArrayList<String> parseData(JSONObject data) throws JSONException{
      if(data == null){
         return null;
      }
      ArrayList<String> ids = new ArrayList<String>();
      Iterator<String> keys = data.keys();
      for (int i = 0; i < data.length(); i++)
         ids.add(keys.next());
      return ids;
   }

   public ArrayList<String> getCategories() {
      return categories;
   }

   public ArrayList<String> getSponsors() {
      return sponsors;
   }

   public ArrayList<String> getAdvisors() {
      return advisors;
   }

   public String getProjectName() {
      return projectName;
   }

   public int getPosterNumber() {
      return posterNumber;
   }

   public String getPosterDptm() {
      return posterDptm;
   }

   public ArrayList<String> getTeam() {
      return team;
   }

   public String get_abstract() {
      return _abstract;
   }

   public String getPosterURL() {
      return posterURL;
   }

   public String getPosterID() {
      return posterID;
   }
}
