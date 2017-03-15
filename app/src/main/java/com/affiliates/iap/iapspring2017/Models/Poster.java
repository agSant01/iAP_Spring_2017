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
import java.util.HashMap;
import java.util.Iterator;

public class Poster {
   private String _abstract;
   private ArrayList<String> advisors;
   private ArrayList<String> team;
   private String posterURL;
   private String posterID;
   private ArrayList<String> categories;
   private String projectName;
   private ArrayList<String> sponsors;
   private int posterNumber;
   private String posterDptm;

   public Poster(JSONObject data, String id) throws InvalidAccountTypeExeption, JSONException{
      this.posterID = id;
      this._abstract = data.optString("Abstract");
      this.advisors = parseData(data.optJSONObject("Advisors"));
      this.team = parseData(data.optJSONObject("TeamMembers"));
      this.posterURL = data.optString("PosterImg");
      this.categories = parseData(data.optJSONObject("Categories"));
      this.projectName = data.optString("ProjectName");
      this.sponsors = parseData(data.optJSONObject("Sponsors"));
      this.posterNumber = data.optInt("number");
      this.posterDptm = data.optString("Department");
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

   public int getPosterNumber() {
      return posterNumber;
   }

   public String getPosterDptm() {
      return posterDptm;
   }
}
