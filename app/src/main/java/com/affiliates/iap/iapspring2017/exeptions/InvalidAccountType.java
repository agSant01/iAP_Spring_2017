package com.affiliates.iap.iapspring2017.exeptions;

/**
 * Created by gsantiago on 02-19-17.
 */

public class InvalidAccountType extends RuntimeException {
   public InvalidAccountType() {
   }

   public InvalidAccountType(String arg0) {
      super(arg0);
   }

   public InvalidAccountType(Throwable arg0) {
      super(arg0);
   }

   public InvalidAccountType(String arg0, Throwable arg1) {
      super(arg0, arg1);
   }
}
