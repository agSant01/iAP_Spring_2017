package com.affiliates.iap.iapspring2017.Models;

import com.affiliates.iap.iapspring2017.exeptions.InvalidAccountType;
import com.affiliates.iap.iapspring2017.exeptions.VoteErrorExeption;
import com.affiliates.iap.iapspring2017.interfaces.UserDelegate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by gsantiago on 02-19-17.
 */

public class CompanyUser extends Users implements UserDelegate {
   private String[] votes;
   private String companName;

   public CompanyUser(JSONObject data, AccountType accountType, String id) throws InvalidAccountType, JSONException {
      this(checkType(accountType), data, accountType, id );
   }

   private CompanyUser(Void d, JSONObject data, AccountType accountType, String id) throws JSONException{
      super(data.getString("Email"), data.getString("Name"), id, accountType);
      this.votes = getVotesFromJSON(data.getJSONArray("Votes"));
      this.companName = data.getString("Company");
   }

   private static Void checkType(AccountType accountType) throws InvalidAccountType, JSONException{
      if (accountType != AccountType.Advisor)
         throw new InvalidAccountType("IAPStudent(): Invalid account type; " + accountType);
      return null;
   }

   private static String[] getVotesFromJSON(JSONArray data) throws JSONException{
      ArrayList<String> votes = new ArrayList<>();
      for (int i = 0; i < data.length(); i++){
         votes.add(data.getString(i));
      }
      return (String[]) votes.toArray();
   }
   @Override
   public void vote(String projectID) throws VoteErrorExeption {

   }
}
