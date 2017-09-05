//
//  Sponsors.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/06/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Sponsors {
   private ArrayList<String> companyReps;
   private String companyName;
   private String companyLogo;
   private String companyID;
   private String website;

   public Sponsors(JSONObject data, String id) {
      companyReps = getReps(data.optJSONArray("Representatives"));
      companyName = data.optString("CompanyName");
      companyLogo = data.optString("Logo");
      website = data.optString("Website");
      companyID = id;
   }
  private static ArrayList<String> getReps(JSONArray d) {
      if(d == null) return null;
      ArrayList<String> reps = new ArrayList<String>();
      for (int i = 0; i < d.length(); i++){
         reps.add(d.optString(i));
      }

      return reps;
   }

   public String getCompanyName() {
      return companyName;
   }

   public String getCompanyLogo() {
      return companyLogo;
   }

   public String getCompanyID() {
      return companyID;
   }

   public String getWebsite() {
      return website;
   }
}
