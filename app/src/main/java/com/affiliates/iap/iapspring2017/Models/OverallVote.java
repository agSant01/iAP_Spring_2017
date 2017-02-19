package com.affiliates.iap.iapspring2017.Models;

import com.affiliates.iap.iapspring2017.exeptions.VoteErrorExeption;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by gsantiago on 02-19-17.
 */

public class OverallVote {
   private String voteID;
   private String projecID;

   public OverallVote(String voteID, String projecID){
      this.projecID = projecID;
      this.voteID = voteID;
   }

   public static void submitVote(String projecID) throws VoteErrorExeption{
      DatabaseReference ref = FirebaseDatabase.getInstance()
                                               .getReference()
                                               .child("Votes")
                                               .child("OverallBest");



   }
}
