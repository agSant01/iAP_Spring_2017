//
//  Constants.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017;

import com.affiliates.iap.iapspring2017.Models.Poster;
import com.affiliates.iap.iapspring2017.Models.User;

import java.util.ArrayList;
import java.util.Map;

public class Constants {
   private static User currentLogedInUser;
   private static Map<String, Poster> posters;

   public static Map<String, Poster> getPosters() {
      return posters;
   }

   public static void setPosters(Map<String, Poster> posters) {
      Constants.posters = posters;
   }

   public static User getCurrentLoggedInUser() {
      return currentLogedInUser;
   }

   public static void setCurrentLogedInUser(User curr) {
      currentLogedInUser = curr;
   }
}
