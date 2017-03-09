//
//  Vote.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Models;

public class Vote {
   private String projectID;
   public Vote(String projectID) {
      this.projectID = projectID;
   }
   public String getProjectID() {
      return projectID;
   }
}
