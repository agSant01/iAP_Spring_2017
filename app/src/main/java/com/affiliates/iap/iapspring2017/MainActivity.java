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
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.sing_in.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends BaseActivity {

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
   private TextView mTitle;

   // Icons of the tabs
   private int tabIcons[] = { R.drawable.ic_schedule,
           R.drawable.ic_poster,
           R.drawable.ic_more };

   private TabManager mSectionsPagerAdapter;

   /**
    * The {@link ViewPager} that will host the section contents.
    */
   private ViewPager mViewPager;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);
      getSupportActionBar().setDisplayShowTitleEnabled(false);

      mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
      // Create the adapter that will return a fragment for each of the three
      // primary sections of the activity.
      mSectionsPagerAdapter = new TabManager(getSupportFragmentManager(), MainActivity.this);

      // Set up the ViewPager with the sections adapter.
      mViewPager = (ViewPager) findViewById(R.id.container);
      mViewPager.setAdapter(mSectionsPagerAdapter);

      final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
      tabLayout.setupWithViewPager(mViewPager);

      tabLayout.getTabAt(0).setIcon(tabIcons[0]);
      tabLayout.getTabAt(1).setIcon(tabIcons[1]);
      tabLayout.getTabAt(2).setIcon(tabIcons[2]);

      tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
         @Override
         public void onTabSelected(TabLayout.Tab tab) {
            int pos = tab.getPosition();
            if (pos == 0){
               mTitle.setText("Schedule");
            } else if(pos == 1){
               mTitle.setText("Posters");
            } else {
               mTitle.setText("More");
            }
         }

         @Override
         public void onTabUnselected(TabLayout.Tab tab) {

         }

         @Override
         public void onTabReselected(TabLayout.Tab tab) {

         }
      });
   }
}
