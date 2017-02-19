package com.affiliates.iap.iapspring2017.interfaces;

import com.affiliates.iap.iapspring2017.exeptions.VoteErrorExeption;

/**
 * Created by gsantiago on 02-19-17.
 */

public interface UserDelegate {
   void vote(String projectID) throws VoteErrorExeption;
}
