package com.affiliates.iap.iapspring2017.Models;

import com.affiliates.iap.iapspring2017.exeptions.InvalidAccountType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by gsantiago on 02-19-17.
 */

public class Poster {
   private String _abstract;
   private String advisorID;
   private String[] team;
   private String posterURL;
   private String posterID;
   private String[] categories;
   private String projectName;
   private String[] sponsors;

   public Poster(JSONObject data, String id) throws InvalidAccountType, JSONException{
      this._abstract = data.getString("Abstract");
      this.advisorID = data.getString("AdvisorRef");
      this.team = getTeamIDs(data.getJSONArray("TeamMembers"));
      this.posterURL = data.getString("PosterImg");
      this.categories = getCategories(data.getJSONArray("Categories"));
      this.posterID = id;
      this.projectName = data.getString("ProjectName");
      this.sponsors = getSponsors(data.getJSONArray("Sponsors"));
   }

   private static String[] getTeamIDs(JSONArray data) throws JSONException{
      ArrayList<String> ids = new ArrayList<>();
      for (int i = 0; i < data.length(); i++)
         ids.add(data.getString(i));
      return (String[]) ids.toArray();
   }

   private static String[] getCategories(JSONArray data) throws JSONException{
      ArrayList<String> categories = new ArrayList<>();
      for (int i = 0; i < data.length(); i++)
         categories.add(data.getString(i));
      return (String[]) categories.toArray();
   }

   private static String[] getSponsors(JSONArray data) throws JSONException{
      ArrayList<String> sponsors = new ArrayList<>();
      for (int i = 0; i < data.length(); i++)
         sponsors.add(data.getString(i));
      return (String[]) sponsors.toArray();
   }
}
