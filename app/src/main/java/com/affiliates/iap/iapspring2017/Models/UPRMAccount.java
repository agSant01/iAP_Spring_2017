package com.affiliates.iap.iapspring2017.Models;

import com.affiliates.iap.iapspring2017.exeptions.VoteErrorExeption;
import com.affiliates.iap.iapspring2017.interfaces.UserDelegate;

/**
 * Created by gsantiago on 02-19-17.
 */

public class UPRMAccount extends Users implements UserDelegate {


   @Override
   public void vote(String projectID) throws VoteErrorExeption {

   }
}
