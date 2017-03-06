package com.affiliates.iap.iapspring2017.Models;

import com.affiliates.iap.iapspring2017.interfaces.Callback;

/**
 * Created by gsantiago on 02-23-17.
 */

public class Vote {
   private String projectID;
   public Vote(String projectID) {
      this.projectID = projectID;
   }
   public String getProjectID() {
      return projectID;
   }
}
