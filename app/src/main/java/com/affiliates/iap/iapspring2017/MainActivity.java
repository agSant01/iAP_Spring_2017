//
//  MainActivity.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

   /**
    * The {@link android.support.v4.view.PagerAdapter} that will provide
    * fragments for each of the sections. We use a
    * {@link FragmentPagerAdapter} derivative, which will keep every
    * loaded fragment in memory. If this becomes too memory intensive, it
    * may be best to switch to a
    * {@link android.support.v4.app.FragmentStatePagerAdapter}.
    */

   private static final String TAG = "MainActivity";
   public static final String ANONYMOUS = "anonymous";

   // Icons of the tabs
   private int tabIcons[] = { R.drawable.ic_schedule,
           R.drawable.ic_poster,
           R.drawable.ic_more };

   private TabManager mSectionsPagerAdapter;
   private FirebaseDatabase mFirebaseDatabase;
   private FirebaseAuth mFirebaseAuth;
   private FirebaseUser mFirebaseUser;

   private String mUsername;
   private String mPhotoUrl;


   /**
    * The {@link ViewPager} that will host the section contents.
    */
   private ViewPager mViewPager;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      mFirebaseAuth = FirebaseAuth.getInstance();
      mFirebaseUser = mFirebaseAuth.getCurrentUser();
      mUsername = ANONYMOUS;

      if (mFirebaseUser == null) {
         // Not signed in, launch the Sign In activity
         startActivity(new Intent(this, SignInActivity.class));
         finish();
         return;
      } else {
         mUsername = mFirebaseUser.getDisplayName();
         if (mFirebaseUser.getPhotoUrl() != null) {
            mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
         }
      }


      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);
      toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
      // Create the adapter that will return a fragment for each of the three
      // primary sections of the activity.
      mSectionsPagerAdapter = new TabManager(getSupportFragmentManager(), MainActivity.this);

      // Set up the ViewPager with the sections adapter.
      mViewPager = (ViewPager) findViewById(R.id.container);
      mViewPager.setAdapter(mSectionsPagerAdapter);

      TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
      tabLayout.setupWithViewPager(mViewPager);

      tabLayout.getTabAt(0).setIcon(tabIcons[0]);
      tabLayout.getTabAt(1).setIcon(tabIcons[1]);
      tabLayout.getTabAt(2).setIcon(tabIcons[2]);
   }
}
