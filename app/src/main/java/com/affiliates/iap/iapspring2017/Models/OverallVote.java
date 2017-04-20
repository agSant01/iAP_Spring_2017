//
//  OverallVote.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Models;

import com.affiliates.iap.iapspring2017.exeptions.VoteErrorException;
import com.google.firebase.crash.FirebaseCrash;

import java.util.HashMap;

import static com.affiliates.iap.iapspring2017.Models.OverallVote.VoteType.BestPoster;
import static com.affiliates.iap.iapspring2017.Models.OverallVote.VoteType.BestPresentation;

public class OverallVote extends Vote {
   private VoteType voteType;
   private String voteID;

   public OverallVote(String voteID, String projecID, int voteType){
      super(projecID);
      this.voteID = voteID;

      switch (voteType){
         case 0:
            this.voteType = BestPoster;
            break;
         case 1:
            this.voteType = BestPresentation;
            break;
         default:
            FirebaseCrash.log("OveralVote.class -> OveralVote(): Vote type value can only be 1 or 0");
            throw new VoteErrorException("OveralVote.class -> OveralVote(): Vote type value can only be 1 or 0");
      }
   }

   public HashMap<String, Object> makeJSON() {
      return new HashMap<String, Object>(){{
         put(voteID, getProjectID());
      }};
   }

   public String getStringFromType() {
      switch (this.voteType) {
         case BestPoster:
            return "BestPoster";
         case BestPresentation:
            return "BestPresentation";
         default:
            break;
      }
      return null;
   }

   public Integer getNumberFromType() {
      switch (this.voteType) {
         case BestPoster:
            return 0;
         case BestPresentation:
            return 1;
         default:
            break;
      }
      return null;
   }


   public String getVoteID() {
      return voteID;
   }


   public VoteType getVoteType() {
      return voteType;
   }

   enum VoteType { BestPoster, BestPresentation }
}
