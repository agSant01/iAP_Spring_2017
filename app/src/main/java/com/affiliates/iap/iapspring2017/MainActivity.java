//
//  MainActivity.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.tabs.TabManager;

public class MainActivity extends BaseActivity {
   private static final String TAG = "MainActivity";

   private TabManager mSectionsPagerAdapter;
   private TabLayout mTabLayout;
   private ViewPager mViewPager;      // The {ViewPager} that will host the section contents.
   private Toolbar mToolbar;
   private TextView mTitle;

   // Icons of the tabs
   private int tabIcons[] = {
           R.drawable.ic_poster,
           R.drawable.ic_schedule,
           R.drawable.ic_more };
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      this.bind();

      setSupportActionBar(mToolbar);
      getSupportActionBar().setDisplayShowTitleEnabled(false);

      // Set up the ViewPager with the sections adapter.
      mViewPager.setAdapter(mSectionsPagerAdapter);
      mTabLayout.setupWithViewPager(mViewPager);

      setTabLayout();
   }

   @Override
   protected void onResume() {
      super.onResume();
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.menu_main, menu);
      final MenuItem searchItem = menu.findItem(R.id.action_search);
      final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

      searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
         @Override
         public boolean onQueryTextSubmit(String query) {
            return false;
         }

         @Override
         public boolean onQueryTextChange(String newText) {
            return false;
         }
      });

      mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
         @Override
         public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

         @Override
         public void onPageSelected(int position) {
            if(position == 1 || position == 2)
               searchItem.setVisible(false);
            else
               searchItem.setVisible(true);
         }

         @Override
         public void onPageScrollStateChanged(int state) {}
      });
      return true;
   }

   public void updateFragment(int position){
      Fragment f =  mSectionsPagerAdapter.getItem(position);
      f.getFragmentManager().beginTransaction().detach(f).commit();
      f.getFragmentManager().beginTransaction().attach(f).commit();
   }

   private void bind(){
      // Create the adapter that will return a fragment for each of the three
      // primary sections of the activity.
      mSectionsPagerAdapter = new TabManager(getSupportFragmentManager(), MainActivity.this);
      mViewPager = (ViewPager) findViewById(R.id.container);
      mTitle = (TextView) findViewById(R.id.toolbar_title);
      mTabLayout = (TabLayout) findViewById(R.id.tabs);
      mToolbar = (Toolbar) findViewById(R.id.toolbar);
   }

   private void setTabLayout(){
      mTabLayout.getTabAt(0).setIcon(tabIcons[0]);
      mTabLayout.getTabAt(1).setIcon(tabIcons[1]);
      mTabLayout.getTabAt(2).setIcon(tabIcons[2]);

      mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
         @Override
         public void onTabSelected(TabLayout.Tab tab) {
            int pos = tab.getPosition();
            if (pos == 0) {
               mTitle.setText("Posters");
            } else if (pos == 1) {
               mTitle.setText("Schedule");
            } else {
               mTitle.setText("More");
            }
         }

         @Override
         public void onTabUnselected(TabLayout.Tab tab) {}

         @Override
         public void onTabReselected(TabLayout.Tab tab) {}
      });
   }
}
