//
//  BaseActivity.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Extensions;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;

public class BaseActivity extends AppCompatActivity {
   @VisibleForTesting
   public ProgressDialog mProgressDialog;
   private Toast mToast;

   public void showDismissAlertDialog(Activity activity, String title, String message){
      new AlertDialog.Builder(activity)
              .setTitle(title)
              .setMessage(message)
              .setNeutralButton("Dismiss", null).create().show();
   }

   public AlertDialog.Builder createAlertDialog(Activity activity){
      return new AlertDialog.Builder(activity);
   }

   public AlertDialog.Builder createDismissAlertDialog(Activity activity, String title, String message){
      return new AlertDialog.Builder(activity)
              .setTitle(title)
              .setMessage(message)
              .setNeutralButton("Dismiss", null);
   }

   public void showProgressDialog(String msg) {
      if (mProgressDialog == null) {
         mProgressDialog = new ProgressDialog(this);
         mProgressDialog.setMessage(msg);
         mProgressDialog.setIndeterminate(true);
         mProgressDialog.setCanceledOnTouchOutside(false);
         mProgressDialog.setCancelable(false);
      }
      mProgressDialog.show();
   }

   public void showProgressDialog() {
      if (mProgressDialog == null) {
         mProgressDialog = new ProgressDialog(this);
         mProgressDialog.setMessage("Loading");
         mProgressDialog.setIndeterminate(true);
         mProgressDialog.setCanceledOnTouchOutside(false);
         mProgressDialog.setCancelable(false);
      }
      mProgressDialog.show();
   }

   public void hideProgressDialog() {
      if (mProgressDialog != null
              && mProgressDialog.isShowing()) {
         mProgressDialog.dismiss();
      }
   }

   public void showProgressBar(ProgressBar pb){
      pb.setActivated(true);
      pb.setVisibility(View.VISIBLE);
   }

   public void hideProgressBar(ProgressBar pb){
      pb.setActivated(false);
      pb.setVisibility(View.INVISIBLE);
   }

   /**
    * Creates and displays a short toast
    * @param msg
    */
   public void showShortToast(String msg){
      if(mToast != null)
         mToast.cancel();
      mToast = Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT);
      mToast.show();
   }

   /**
    * Creates and displays a long toast
    * @param msg
    */
   public void showLongToast(String msg){
      if(mToast != null)
         mToast.cancel();
      mToast = Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG);
      mToast.show();
   }

   /**
    * Cancel toast
    **/
   public void clearToast(){
      if(mToast != null)
         mToast.cancel();
   }

   /**
    * Sets visible any view item
    * @param view
    */
   public void setVisible(View view){
      view.setVisibility(View.VISIBLE);
   }

   /**
    * Sets invisible any view item
    * @param view
    */
   public void setInvisible(View view){
      view.setVisibility(View.INVISIBLE);
   }

   /**
    * Sets gone any view item
    * @param view
    */
   public void setGone(View view){
      view.setVisibility(View.GONE);
   }


   public void startActivity(Intent intent, int enter, int exit){
      startActivity(intent);
      overridePendingTransition(enter, exit);
   }

   public void startActivity(Class classTo, int enter, int exit){
      startActivity(
              new Intent(getBaseContext(), classTo)
      );
      overridePendingTransition(enter, exit);
   }

   public void startActivity(Class classTo){
      startActivity(
              new Intent(getBaseContext(), classTo)
      );
   }

   public void verbose(String msg){
      Log.v(getLocalClassName(), msg);
   }

   public void debug(String msg){
      Log.d(getLocalClassName(), msg);
   }

   public void error(String msg){
      Log.e(getLocalClassName(), msg);
   }

   public void FCLog(String msg) {
      FirebaseCrash.log(getClass().getSimpleName() + ": " + msg);
   }
}