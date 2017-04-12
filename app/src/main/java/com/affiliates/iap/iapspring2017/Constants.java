//
//  Constants.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017;

import android.graphics.drawable.Drawable;

import com.affiliates.iap.iapspring2017.Models.Event;
import com.affiliates.iap.iapspring2017.Models.IAPStudent;
import com.affiliates.iap.iapspring2017.Models.Poster;
import com.affiliates.iap.iapspring2017.Models.Sponsors;
import com.affiliates.iap.iapspring2017.Models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Constants {
   private static User currentLoggedInUser;
   private static HashMap<String, Poster> posters;
   private static ArrayList<Poster> sortedPosters;
   private static ArrayList<IAPStudent> likedStudents;
   private static ArrayList<IAPStudent> unlikedStudents;
   private static ArrayList<Event> events;
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

   public static ArrayList<IAPStudent> getLikedStudents() {
      return likedStudents;
   }

   public static void setLikedStudents(ArrayList<IAPStudent> i) {
      likedStudents = i;
   }

   public static void setSortedPosters(ArrayList<Poster> sorted) {
      sortedPosters = sorted;
   }

   public static ArrayList<Event> getEvents() {
      return events;
   }

   public static void setEvents(ArrayList<Event> events) {
      Constants.events = events;
   }

   public static ArrayList<Poster> getSortedPosters() {
      return sortedPosters;
   }

   public static ArrayList<IAPStudent> getUnlikedStudents() {
      return unlikedStudents;
   }

   public static void setUnlikedStudents(ArrayList<IAPStudent> unlikedStudents) {
      Constants.unlikedStudents = unlikedStudents;
   }
}
