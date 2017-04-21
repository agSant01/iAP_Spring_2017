//
//  SignInActivity.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.sing_in;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.BaseActivity;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.interfaces.CustomViewPager;
import com.affiliates.iap.iapspring2017.sing_in.intro_screens.IntroScreensManager;
import com.affiliates.iap.iapspring2017.services.AccountAdministration;
import com.affiliates.iap.iapspring2017.services.Client;
import com.matthewtamlin.sliding_intro_screen_library.indicators.Dot;
import com.matthewtamlin.sliding_intro_screen_library.indicators.DotIndicator;

public class SignInActivity extends BaseActivity {
   private static final String TAG = "SignIn";
   private static final int REQUEST_EXIT = 5;

   public CustomViewPager mViewPager;
   private IntroScreensManager mSectionsPagerAdapter;
   private TabLayout mTabLayout;
   private DotIndicator mDotIndicator;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.intro_screen_manager);

      bind();

      mDotIndicator.setSelectedItem(0,true);
      // Create the adapter that will return a fragment for each of the three
      // primary sections of the activity.
      mSectionsPagerAdapter = new IntroScreensManager(getSupportFragmentManager());
      mTabLayout.setupWithViewPager(mViewPager);

      final TextView next = (TextView) findViewById(R.id._next);
      final TextView skip = (TextView) findViewById(R.id._skip);

      next.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
         }
      });

      skip.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            mViewPager.setCurrentItem(6);
         }
      });

      // Set up the ViewPager with the sections adapter.
      mViewPager.setAdapter(mSectionsPagerAdapter);
      mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
         @Override
         public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

         @Override
         public void onPageSelected(int position) {
            if(position<5) {
               mDotIndicator.setVisibility(true);
               mDotIndicator.setSelectedItem(position, true);
               next.setVisibility(View.VISIBLE);
               skip.setVisibility(View.VISIBLE);
            } else{
               next.setVisibility(View.INVISIBLE);
               skip.setVisibility(View.INVISIBLE);
               mDotIndicator.setVisibility(false);
            }
         }

         @Override
         public void onPageScrollStateChanged(int state) {}
      });

      if (getIntent().getStringExtra("splash").equals("second")){
         Log.v(TAG, "second");
         mViewPager.setCurrentItem(5);
         mDotIndicator.setSelectedItem(4,true);
         mViewPager.setPagingEnabled(false);
      }

   }

   private void bind(){
      mViewPager = (CustomViewPager) findViewById(R.id.viewpager_container);
      mDotIndicator = (DotIndicator) findViewById(R.id.dots_indicator);
      mTabLayout = (TabLayout) findViewById(R.id.tabs);
   }

   @Override
   public void onBackPressed() {
      super.onBackPressed();
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (requestCode == REQUEST_EXIT) {
         if (resultCode == RESULT_OK) {
            this.finish();
         }
      }
   }
}
