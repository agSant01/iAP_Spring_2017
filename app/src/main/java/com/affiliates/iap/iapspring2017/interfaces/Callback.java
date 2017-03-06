//
//  Callback.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.interfaces;

public interface Callback<T> {
   void success(T data);
   void failure(String message);
}
