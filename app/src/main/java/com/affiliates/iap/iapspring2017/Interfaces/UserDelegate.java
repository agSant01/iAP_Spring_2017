//
//  UserDelegate.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Interfaces;

import android.content.Context;

import com.affiliates.iap.iapspring2017.Exceptions.VoteErrorException;
import com.affiliates.iap.iapspring2017.Models.Vote;

import java.util.HashMap;

public interface UserDelegate {
   void vote(String projectID, Vote vote, Context context, Callback callback) throws VoteErrorException;

   HashMap<String, Object> toMap();
}
