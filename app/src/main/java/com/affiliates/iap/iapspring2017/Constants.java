package com.affiliates.iap.iapspring2017;

import com.affiliates.iap.iapspring2017.Models.User;

/**
 * Created by gsantiago on 02-23-17.
 */

public class Constants {
   private static User currentLogedInUser;

   public static User getCurrentLoggedInUser() {
      return currentLogedInUser;
   }

   public static void setCurrentLogedInUser(User curr) {
      currentLogedInUser = curr;
   }
}
