package com.affiliates.iap.iapspring2017.exeptions;

/**
 * Created by gsantiago on 02-23-17.
 */

public class AuthenticationErrorExeption extends RuntimeException {
   public AuthenticationErrorExeption() {
      super();
   }

   public AuthenticationErrorExeption(String message) {
      super(message);
   }

   public AuthenticationErrorExeption(String message, Throwable cause) {
      super(message, cause);
   }

   public AuthenticationErrorExeption(Throwable cause) {
      super(cause);
   }

}
