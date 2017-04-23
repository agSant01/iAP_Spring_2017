//
//  Constants.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.affiliates.iap.iapspring2017.Models.Event;
import com.affiliates.iap.iapspring2017.Models.IAPStudent;
import com.affiliates.iap.iapspring2017.Models.Poster;
import com.affiliates.iap.iapspring2017.Models.Sponsors;
import com.affiliates.iap.iapspring2017.Models.User;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Constants {
   private static User currentLoggedInUser;
   public static int currentEvent;
   private static HashMap<String, Poster> posters;
   private static ArrayList<Poster> sortedPosters;
   private static ArrayList<IAPStudent> likedStudents;
   private static ArrayList<IAPStudent> unlikedStudents;
   private static ArrayList<IAPStudent> undecidedStudents;
   private static ArrayList<Event> events;
   private static ArrayList<Sponsors> sponsor;
   private static Map<String, Drawable> sponsorLogos = new HashMap<>();
   public static JSONObject curentRegisteringUserData;

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

   public static ArrayList<IAPStudent> getUndecidedStudents() {
      return undecidedStudents;
   }

   public static void setUndecidedStudents(ArrayList<IAPStudent> undecidedStudents) {
      Constants.undecidedStudents = undecidedStudents;
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

   public static void sortEvents(){
       PriorityQueue<Event> sorted = new PriorityQueue<>();
       sorted.addAll(events == null ? new ArrayList<Event>() : events);
      Date date = new Date();
      events = new ArrayList<>();

//      Calendar ca = Calendar.getInstance();
//      ca.set(2017,3,26,12,45,34);
//      date = ca.getTime();

      int c = 0;
      Log.v("DATE", date.toString());
      Constants.currentEvent = -1;
      while(!sorted.isEmpty()){
         Event event = sorted.remove();
         if(date.after(event.getStartDate()) && date.before(event.getEndDate())){
            Constants.currentEvent = c;
            Log.v("Constants" , event.getEventName());
         }
         c++;
         events.add(event);
         Log.v("Sorting", events.get(events.size()-1).getStartDate().toString());
      }
   }
}
