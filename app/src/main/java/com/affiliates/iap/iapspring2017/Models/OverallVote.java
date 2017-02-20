//
//  OverallVote.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Models;

import com.affiliates.iap.iapspring2017.exeptions.VoteErrorException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OverallVote {
   private String voteID;
   private String projecID;

   public OverallVote(String voteID, String projecID){
      this.projecID = projecID;
      this.voteID = voteID;
   }

   public static void submitVote(String projecID) throws VoteErrorException {
      DatabaseReference ref = FirebaseDatabase.getInstance()
                                               .getReference()
                                               .child("Votes")
                                               .child("OverallBest");



   }
}
