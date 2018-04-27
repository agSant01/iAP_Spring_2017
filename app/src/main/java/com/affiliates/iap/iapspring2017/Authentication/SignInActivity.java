//
//  SignInActivity.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Authentication;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.Authentication.IntroScreens.IntroScreensManager;
import com.affiliates.iap.iapspring2017.Extensions.BaseActivity;
import com.affiliates.iap.iapspring2017.Interfaces.CustomViewPager;
import com.affiliates.iap.iapspring2017.R;
import com.matthewtamlin.sliding_intro_screen_library.indicators.DotIndicator;

public class SignInActivity extends BaseActivity {
   private static final String TAG = "SignIn";


   private IntroScreensManager mSectionsPagerAdapter;
   private DotIndicator mDotIndicator;
   public CustomViewPager mViewPager;
   private TabLayout mTabLayout;

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
            if(position < 5) {
               setVisible(mDotIndicator);
               mDotIndicator.setSelectedItem(position, true);
               setVisible(next);
               setVisible(skip);
               next.setText("Next");
               if (position == 4 ){
                  setInvisible(skip);
                  next.setText("Sign In");
               }
            } else{
               setInvisible(next);
               setInvisible(skip);
               setInvisible(mDotIndicator);
            }
         }

         @Override
         public void onPageScrollStateChanged(int state) {}
      });
      if(getIntent().getStringExtra("splash")!=null)
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

}
