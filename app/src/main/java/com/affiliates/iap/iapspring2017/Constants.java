//
//  Constants.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;

import com.affiliates.iap.iapspring2017.Models.CompanyVote;
import com.affiliates.iap.iapspring2017.Models.Poster;
import com.affiliates.iap.iapspring2017.Models.Sponsors;
import com.affiliates.iap.iapspring2017.Models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Constants {
   private static User currentLoggedInUser;
   private static HashMap<String, Poster> posters;
   private static ArrayList<Poster> sortedPosters;

   public static ArrayList<Poster> getSortedPosters() {
      return sortedPosters;
   }

   public static void setSortedPosters(ArrayList<Poster> sortedPosters) {
      Constants.sortedPosters = sortedPosters;
   }

   private static ArrayList<Sponsors> sponsor;
   private static Map<String, Drawable> sponsorLogos = new HashMap<>();

   public static ArrayList<Sponsors> getSponsor() {
      return sponsor;
   }

   public static Map<String, Drawable> getSponsorLogos() {
      return sponsorLogos;
   }

   public static HashMap<String, Poster> getPosters() {
      return posters;
   }

   public static void setSponsor(ArrayList<Sponsors> sponsor) {
      Constants.sponsor = sponsor;
   }

   public static void setPosters(HashMap<String, Poster> posters) {
      Constants.posters = posters;
   }

   public static User getCurrentLoggedInUser() {
      return currentLoggedInUser;
   }

   public static void setCurrentLogedInUser(User curr) {
      currentLoggedInUser = curr;
   }
}
