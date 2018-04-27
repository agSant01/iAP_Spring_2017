//
//  AuthenticationErrorExeption.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Exceptions;

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
