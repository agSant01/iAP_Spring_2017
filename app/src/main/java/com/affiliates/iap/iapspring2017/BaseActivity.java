//
//  BaseActivity.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017;

import android.app.ProgressDialog;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

public class BaseActivity extends AppCompatActivity {

   @VisibleForTesting
   public ProgressDialog mProgressDialog;

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
      if (mProgressDialog != null && mProgressDialog.isShowing()) {
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

   @Override
   public void onStop() {
      super.onStop();
   }

}