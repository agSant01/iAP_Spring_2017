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

public class Constants {
   private static User currentLogedInUser;
   private static ArrayList<Poster> posters;

   public static ArrayList<Poster> getPosters() {
      return posters;
   }

   public static void setPosters(ArrayList<Poster> posters) {
      Constants.posters = posters;
   }

   public static User getCurrentLoggedInUser() {
      return currentLogedInUser;
   }

   public static void setCurrentLogedInUser(User curr) {
      currentLogedInUser = curr;
   }
}
