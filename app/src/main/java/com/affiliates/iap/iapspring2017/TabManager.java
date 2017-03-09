//
//  TabManager.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.affiliates.iap.iapspring2017.tabs.MoreFragment;
import com.affiliates.iap.iapspring2017.tabs.PostersFragment;
import com.affiliates.iap.iapspring2017.tabs.ScheduleFragment;

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
         return ScheduleFragment.newInstance();
      } else if(position == 1){
         return PostersFragment.newInstance();
      }
      return MoreFragment.newInstance();
   }

   @Override
   public int getCount() {
      // Show 3 total pages.
      return 3;
   }

//   @Override
//   public CharSequence getPageTitle(int position) {
//      Drawable image = ContextCompat.getDrawable(context,tabIcons[position]);
//      image.setBounds(0,0,image.getIntrinsicWidth(),image.getIntrinsicHeight());
//      SpannableString spannableString = new SpannableString(" ");
//      ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
//      spannableString.setSpan(imageSpan, 0 , 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//      return spannableString;
//   }
}