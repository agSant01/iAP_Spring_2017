//
//  TabManager.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.tabs;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.affiliates.iap.iapspring2017.R;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class TabManager extends FragmentPagerAdapter {


   // Icons of the tabs
   private int tabIcons[] = { R.drawable.ic_schedule,
           R.drawable.ic_poster,
           R.drawable.ic_more };
   private Context context;

   private final int PAGE_COUNT = tabIcons.length;

   public TabManager(FragmentManager fm, Context context) {
      super(fm);
      this.context = context;
   }

   @Override
   public Fragment getItem(int position) {
      // getItem is called to instantiate the fragment for the given page.
      // Return a PlaceholderFragment (defined as a static inner class below).
      if(position == 0) {
         return PostersFragment.newInstance(position);
      } else if(position == 1){
         return ScheduleFragment.newInstance();
      }
      return MoreFragment.newInstance();
   }

   @Override
   public int getCount() {
      return PAGE_COUNT;
   }
}