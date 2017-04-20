//
//  MainActivity.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.Models.Poster;
import com.affiliates.iap.iapspring2017.interfaces.CustomViewPager;
import com.affiliates.iap.iapspring2017.tabs.PostersFragment;
import com.affiliates.iap.iapspring2017.tabs.TabManager;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {
   private static final String TAG = "MainActivity";

   private TabManager mSectionsPagerAdapter;
   private MaterialSearchView mSearchView;
   private CustomViewPager mViewPager;      // The {ViewPager} that will host the section contents.
   private TabLayout mTabLayout;
   private boolean searchMode;
   private String lastQuery;
   private Toolbar mToolbar;
   private TextView mTitle;
   private MenuItem mItem;
   private int filterType;
   private boolean hide;

   // Icons of the tabs
   private int tabIconsUnselected[] = {
           R.drawable.ic_poster_icon,
           R.drawable.ic_schedule_icon,
           R.drawable.ic_more_icon };

   private int tabIconsSelected[] = {
           R.drawable.ic_poster_icon_green,
           R.drawable.ic_schedule_icon_green,
           R.drawable.ic_more_icon_green };

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      this.bind();

      setSupportActionBar(mToolbar);
      getSupportActionBar().setDisplayShowTitleEnabled(false);
      mSectionsPagerAdapter = new TabManager(getSupportFragmentManager(), MainActivity.this);

      // Set up the ViewPager with the sections adapter.
      mViewPager.setAdapter(mSectionsPagerAdapter);
      mViewPager.setSoundEffectsEnabled(false);
      setTabLayout();
      setSearchView();

      mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
         @Override
         public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

         @Override
         public void onPageSelected(int position) {
            if(mItem == null) return;
            if(position == 1 || position == 2)
               mItem.setVisible(false);
            else
               mItem.setVisible(true);
         }

         @Override
         public void onPageScrollStateChanged(int state) {}
      });
   }

   private void bind(){
      // Create the adapter that will return a fragment for each of the three
      // primary sections of the activity.
      mViewPager = (CustomViewPager) findViewById(R.id.viewpager_container);
      mSearchView = (MaterialSearchView) findViewById(R.id.search_view);
      mTitle = (TextView) findViewById(R.id.toolbar_title);
      mTabLayout = (TabLayout) findViewById(R.id.tabs);
      mToolbar = (Toolbar) findViewById(R.id.toolbar);
   }

   private void setTabLayout(){
      searchMode = false;
      mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.appGreen));
      mTabLayout.setupWithViewPager(mViewPager);
      mTabLayout.getTabAt(0).setIcon(tabIconsSelected[0]);
      mTabLayout.getTabAt(1).setIcon(tabIconsUnselected[1]);
      mTabLayout.getTabAt(2).setIcon(tabIconsUnselected[2]);
      mTabLayout.setSoundEffectsEnabled(false);
      mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
         @Override
         public void onTabSelected(TabLayout.Tab tab) {
            int pos = tab.getPosition();

            if(!searchMode)
               tab.setIcon(tabIconsSelected[tab.getPosition()]);

            if (pos == 0) {
               mTitle.setText("Posters");

            } else if (pos == 1) {
               mTitle.setText("Schedule");
            } else {
               mTitle.setText("More");
            }
         }

         @Override
         public void onTabUnselected(TabLayout.Tab tab) {
            if(!searchMode)
               tab.setIcon(tabIconsUnselected[tab.getPosition()]);
         }

         @Override
         public void onTabReselected(TabLayout.Tab tab) {}
      });
   }

   private void setSearchView(){
      mSearchView.setHint("Filter");
      mSearchView.setVoiceSearch(false);
      mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
         @Override
         public boolean onQueryTextSubmit(String query) {
            if(hide){
               InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
               imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
            hide = true;
            onQueryTextChange(query);
            return true;
         }

         @Override
         public boolean onQueryTextChange(String newText) {
            lastQuery = newText;
            hide = true;
            if( filterType == 0)
               setSearchActivity(filterByName(newText));
            else if( filterType == 1)
               setSearchActivity(filterByKeyWord(newText));
            else
               setSearchActivity(filterByDepartment(newText));

            Log.v(TAG, "QUERY: " + filterType);
            return false;
         }
      });

      mSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
         @Override
         public void onSearchViewShown() {
            setSearchActivity(new ArrayList<Poster>());
            mViewPager.setPagingEnabled(false);
            searchMode();
         }

         @Override
         public void onSearchViewClosed() {
            mViewPager.setPagingEnabled(true);
            resetPosterFragment();
            setTabLayout();
         }
      });
   }

   private void searchMode(){
      searchMode = true;
      mTabLayout.setSelectedTabIndicatorColor(R.color.appBlack);
      mTabLayout.getTabAt(0).setIcon(null);
      mTabLayout.getTabAt(1).setIcon(null);
      mTabLayout.getTabAt(2).setIcon(null);

      mTabLayout.getTabAt(2).setText("Department");
      mTabLayout.getTabAt(1).setText("Keyword");
      mTabLayout.getTabAt(0).setText("Name");

      mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
         @Override
         public void onTabSelected(TabLayout.Tab tab) {
            Log.v(TAG, "onSelected()   "  + lastQuery);
            filterType = tab.getPosition();
            mSearchView.setQuery(lastQuery, true);
            hide = false;
         }

         @Override
         public void onTabUnselected(TabLayout.Tab tab) {

         }

         @Override
         public void onTabReselected(TabLayout.Tab tab) {

         }
      });
   }

   private ArrayList<Poster> filterByName(String query){
      ArrayList<Poster> posters = Constants.getSortedPosters();
      ArrayList<Poster> ta = new ArrayList<>();

      if(posters == null || query.equals("")) return new ArrayList<>();
      for(int i = 0; i < posters.size(); i++){
         if(posters.get(i).getProjectName().toLowerCase().contains(query.toLowerCase())){
            ta.add(posters.get(i));
         }
      }
      return ta;
   }

   private ArrayList<Poster> filterByDepartment(String query){
      ArrayList<Poster> posters = Constants.getSortedPosters();
      ArrayList<Poster> ta = new ArrayList<>();

      if(posters == null || query.equals("")) return new ArrayList<>();
      for(int i = 0; i < posters.size(); i++){
         if(posters.get(i).getPosterDptm().toLowerCase().contains(query.toLowerCase())){
            ta.add(posters.get(i));
         }
      }
      return ta;
   }

   private ArrayList<Poster> filterByKeyWord(String query){
      ArrayList<Poster> posters = Constants.getSortedPosters();
      ArrayList<Poster> ta = new ArrayList<>();
      Poster poster;

      if(posters == null || query.equals("")) return new ArrayList<>();
      for(int i = 0; i < posters.size(); i++){
         poster = posters.get(i);
         for(int j = 0; j < poster.getCategories().size(); j++){
            if(poster.getCategories().get(j).toLowerCase().contains(query.toLowerCase())){
               ta.add(posters.get(i));
               break;
            }
         }
      }
      return ta;
   }

   private void setSearchActivity(ArrayList<Poster> search){
      PostersFragment.setSearchableAdapter(search);
   }

   private void resetPosterFragment(){
      PostersFragment.setSearchableAdapter(Constants.getSortedPosters());
   }

   public void updateFragment(int position){
      Fragment f =  mSectionsPagerAdapter.getItem(position);
      f.getFragmentManager().beginTransaction().detach(f).commit();
      f.getFragmentManager().beginTransaction().attach(f).commit();
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.menu_main, menu);
      mItem = menu.findItem(R.id.action_search);
      mSearchView.setMenuItem(mItem);
      return true;
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
         ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
         Log.v(TAG, "TEST1: ");
         if (matches != null && matches.size() > 0) {
            String searchWrd = matches.get(0);
            Log.v(TAG, "TEST: ");

            if (!TextUtils.isEmpty(searchWrd)) {
               mSearchView.setQuery(searchWrd, true);
            }
         }
         return;
      }
      super.onActivityResult(requestCode, resultCode, data);
   }

   @Override
   protected void onResume() {
      super.onResume();
   }

   @Override
   public void onBackPressed() {
      if (mSearchView.isSearchOpen()) {
         mSearchView.closeSearch();
      } else {
         super.onBackPressed();
      }
   }
}
