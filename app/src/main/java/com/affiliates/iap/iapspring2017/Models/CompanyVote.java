//
//  CompanyVote.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Models;

import android.content.Context;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CompanyVote extends Vote implements Serializable {
   private int posterTotal;
   private int presentationTotal;
   private Technical technicalScore;
   private Results resultsScore;
   private Presentation presentationScore;
   private Methodology methodologyScore;

   public CompanyVote(String projectID) {
      super(projectID);
      this.posterTotal = 0;
      this.presentationTotal = 0;
      this.technicalScore = new Technical();
      this.resultsScore = new Results();
      this.presentationScore = new Presentation();
      this.methodologyScore = new Methodology();
   }

   private void updateTotal(){
      this.posterTotal = technicalScore.poster +
                     resultsScore.poster +
                     presentationScore.poster +
                     methodologyScore.poster;
      this.presentationTotal = presentationScore.presentation +
                           technicalScore.presentation +
                           resultsScore.presentation +
                           methodologyScore.presentation;
   }

   public HashMap<String, Object> makeJSON(){
      this.updateTotal();
      final Map<String,Object> tech = new HashMap<String, Object>(){{
         put("PresentationEval", technicalScore.presentation);
         put("PosterEval", technicalScore.poster);
      }};
      final Map<String,Object> res = new HashMap<String, Object>(){{
         put("PresentationEval", resultsScore.presentation);
         put("PosterEval", resultsScore.poster);
      }};
      final Map<String,Object> pres = new HashMap<String, Object>(){{
         put("PresentationEval", presentationScore.presentation);
         put("PosterEval", presentationScore.poster);
      }};
      final Map<String,Object> method = new HashMap<String, Object>(){{
         put("PresentationEval", methodologyScore.presentation);
         put("PosterEval", methodologyScore.poster);
      }};

      return new HashMap<String, Object>(){{
         put("PosterTotal", posterTotal);
         put("PresentationTotal", presentationTotal);
         put("Project", getProjectID());
         put("Methodology", method);
         put("TechnicalContent", tech);
         put("Results&PresentationEval", res);
         put("PresentationEval", pres);
      }};
   }

   public class Technical{
      public int presentation = 0;
      public int poster = 0;
   }

   public class Results{
      public int presentation = 0;
      public int poster = 0;
   }

   public class Presentation{
      public int presentation = 0;
      public int poster = 0;
   }

   public class Methodology{
      public int presentation = 0;
      public int poster = 0;
   }


   public int getPosterTotal() {
      return posterTotal;
   }

   public int getPresentationTotal() {
      return presentationTotal;
   }


   public Technical getTechnicalScore() {
      return technicalScore;
   }

   public Results getResultsScore() {
      return resultsScore;
   }

   public Presentation getPresentationScore() {
      return presentationScore;
   }

   public Methodology getMethodologyScore() {
      return methodologyScore;
   }

   public void setPosterTotal(int posterTotal) {
      this.posterTotal = posterTotal;
   }

   public void setPresentationTotal(int presentationTotal) {
      this.presentationTotal = presentationTotal;
   }

   public boolean validate(){
      return (presentationScore.poster != 0)
               && (presentationScore.presentation != 0)
               && (technicalScore.poster != 0)
               && (technicalScore.presentation != 0)
               && (resultsScore.poster != 0)
               && (resultsScore.presentation != 0)
               && (methodologyScore.poster != 0)
               && (methodologyScore.presentation != 0);
   }

   public void updateVote(Context context){
      try{
         FileOutputStream fileOutputStream = context.openFileOutput(this.getProjectID(), Context.MODE_PRIVATE);
         ObjectOutputStream objectInputStream = new ObjectOutputStream(fileOutputStream);
         objectInputStream.writeObject(this);
         objectInputStream.close();
         fileOutputStream.close();
      }catch (Exception e){

      }

   }

   public void removeVoteFromMemory(Context context){
     context.deleteFile(this.getProjectID());
   }
}
