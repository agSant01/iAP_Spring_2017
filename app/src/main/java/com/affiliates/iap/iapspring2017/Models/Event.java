//
//  Event.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/6/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Models;

import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Event implements Comparable<Event>{
   private final SimpleDateFormat format;
   private String eventDescription;
   private String eventName;
   private String eventType;
   private Date startDate;
   private Date endDate;
   private boolean isPresentation;

   public Event(JSONObject data) {
      format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      try {
         this.startDate = format.parse(data.optString("StartDate"));
         this.endDate = format.parse(data.optString("EndDate"));
      } catch (ParseException e){
         Log.e("EVENT Constructor", e.getMessage());
      }
      this.eventDescription = data.optString("Description");
      this.eventType = data.optString("EventType");
      this.eventName = data.optString("EventName");
   }

   public SimpleDateFormat getFormat() {
      return format;
   }

   public String getEventName() {
      return eventName;
   }

   public Date getStartDate() {
      return startDate;
   }

   public Date getEndDate() {return endDate;}

   public boolean isPresentation(){
      return this.isPresentation;
   }

   public String getStartTime(){
      String s = format.format(startDate);
      boolean am = false;
      String str = "";
      for (int i = 11; i < s.length()-3; i++){
         if(i == 11 && (s.charAt(i) == '0' || s.charAt(i+1) == '1' || s.charAt(i+1) == '0')) {
            am = true;
         }
         str += s.charAt(i);
      }

      if(am)
         str += "am";
      else {
         str += "pm";
         if(!str.substring(0,2).equals("12"))
            str = Integer.toString( Integer.parseInt( str.substring(0,2) ) - 12) + str.substring(2);
      }
      if(str.charAt(0) == '0')
         return str.substring(1);
      return str;
   }

   public String getEndTime(){
      String s = format.format(endDate);
      String str = "";
      boolean am = false;
      for (int i = 11; i < s.length()-3; i++){
         if(i == 11 && (s.charAt(i) == '0' || s.charAt(i+1) == '1' || s.charAt(i+1) == '0')) {
            am = true;
         }
         str += s.charAt(i);
      }
      if(am)
         str += "am";
      else {
         str += "pm";
         if(!str.substring(0,2).equals("12"))
            str = Integer.toString( Integer.parseInt( str.substring(0,2) ) - 12) + str.substring(2);
      }
      if(str.charAt(0) == '0')
         return str.substring(1);
      return str;
   }

   public String getFormatedStartDate(){
      return startDate.toString().substring(4,10) + ", "
              + startDate.toString().substring(startDate.toString().length()-4, startDate.toString().length());
   }

   @Override
   public int compareTo(@NonNull Event other) {
      if (this.startDate.after(other.startDate)) return 1;
      if (this.startDate.before(other.startDate)) return -1;
      return 0;
   }
}
