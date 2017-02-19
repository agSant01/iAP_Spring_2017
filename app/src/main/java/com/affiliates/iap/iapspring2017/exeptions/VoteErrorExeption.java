package com.affiliates.iap.iapspring2017.exeptions;

/**
 * Created by gsantiago on 02-19-17.
 */

public class VoteErrorExeption extends RuntimeException {
   public VoteErrorExeption() {
   }

   public VoteErrorExeption(String arg0) {
      super(arg0);
   }

   public VoteErrorExeption(Throwable arg0) {
      super(arg0);
   }

   public VoteErrorExeption(String arg0, Throwable arg1) {
      super(arg0, arg1);
   }
}
