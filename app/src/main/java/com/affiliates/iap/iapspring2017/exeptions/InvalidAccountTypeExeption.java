//
//  InvalidAccountTypeException.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.exeptions;

public class InvalidAccountTypeExeption extends RuntimeException {
   public InvalidAccountTypeExeption() {
   }

   public InvalidAccountTypeExeption(String arg0) {
      super(arg0);
   }

   public InvalidAccountTypeExeption(Throwable arg0) {
      super(arg0);
   }

   public InvalidAccountTypeExeption(String arg0, Throwable arg1) {
      super(arg0, arg1);
   }
}
