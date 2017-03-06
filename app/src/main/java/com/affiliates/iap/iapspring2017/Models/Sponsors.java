package com.affiliates.iap.iapspring2017.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Sponsors {
   private String companyName;
   private String companyLogo;
   private ArrayList<String> companyReps;
   private String companyID;

   public Sponsors(JSONObject data, String id) throws JSONException {
      companyID = id;
      companyLogo = data.getString("Logo");
      companyReps = getReps(data.getJSONArray("Representatives"));
      companyName = data.getString("CompanyName");
   }

   private static ArrayList<String> getReps(JSONArray d) throws JSONException {
      ArrayList<String> reps = new ArrayList<String>();
      for (int i = 0; i < d.length(); i++){
         reps.add(d.getString(i));
      }
      return reps;
   }

}
