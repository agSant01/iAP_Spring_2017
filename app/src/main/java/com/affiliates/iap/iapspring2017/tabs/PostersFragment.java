//
//  PostersFragment.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/7/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.tabs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.Constants;
import com.affiliates.iap.iapspring2017.MainActivity;
import com.affiliates.iap.iapspring2017.Models.Poster;
import com.affiliates.iap.iapspring2017.PosterDescription;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.adapters.PosterAdapter;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.services.DataService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class PostersFragment extends Fragment {
   private static final String TAG = "PostersFragment";
   private PosterAdapter mPosterAdapter;
   private ListView mListView;
   private View mRootView;
   private ProgressBar mPB;

   private static int position;

   public static PostersFragment newInstance(int p){
      position = p;
      return new PostersFragment();
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstance) {

      mRootView = inflater.inflate(R.layout.fragment_posters, container, false);
      mListView = (ListView) mRootView.findViewById(R.id.poster_listview);
      mPB = (ProgressBar) mRootView.findViewById(R.id.progressBar);
      mPB.setVisibility(ProgressBar.VISIBLE);
      mPB.setVerticalFadingEdgeEnabled(true);
      final AlphaAnimation fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);//fade from 1 to 0 alpha
      fadeOutAnimation.setDuration(1000);
      fadeOutAnimation.setFillAfter(true);//to keep it at 0 when animation ends

      // run a background job and once complete

      if(Constants.getPosters() == null){
         DataService.sharedInstance().getPosters(new Callback() {
            @Override
            public void success(Object data) {
               Log.v(TAG, "Get posters succesfull");
               Map<Integer, Poster> d = (HashMap<Integer, Poster>) data;
               HashMap<String, Poster> ps = new HashMap<String, Poster>();
               ArrayList<Poster> sort = new ArrayList<Poster>();
               for(int i = 0; i < d.size(); i++) {
                  ps.put(d.get(i).getPosterID(), d.get(i));
                  sort.add(d.get(i));
                  Log.v(TAG, d.get(i).getProjectName());
               }

               Constants.setPosters(ps);
               Constants.setSortedPosters(sort);

               mPosterAdapter = new PosterAdapter(getActivity().getBaseContext(), new ArrayList<Poster>(Constants.getSortedPosters()));
               for(Poster p : Constants.getPosters().values())
                  Log.v(TAG, p.getProjectName() + "<_");

               mListView.setAdapter(mPosterAdapter);
               mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                  @Override
                  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                     Intent in = new Intent(getActivity().getBaseContext(), PosterDescription.class);
                     String ID = mPosterAdapter.getItem(position).getPosterID();
                     in.putExtra("posterID", ID);
                     startActivity(in);
                  }
               });
               mPB.startAnimation(fadeOutAnimation);
               mPB.setVisibility(ProgressBar.INVISIBLE);
            }

            @Override
            public void failure(String message) {
               ((MainActivity) getActivity()).updateFragment(position);
               Log.e(TAG, "Failed to get posters");
            }
         });
      } else{
         mPosterAdapter = new PosterAdapter(getActivity().getBaseContext(),new ArrayList<Poster>(Constants.getPosters().values()));
         mListView.setAdapter(mPosterAdapter);
         mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Intent in = new Intent(getActivity().getBaseContext(), PosterDescription.class);
               String ID = mPosterAdapter.getItem(position).getPosterID();
               in.putExtra("posterID", ID);
               startActivity(in);
            }
         });
         mPB.startAnimation(fadeOutAnimation);
         mPB.setVisibility(ProgressBar.INVISIBLE);
      }

      return mRootView;
   }

   @Override
   public void onResume() {
      super.onResume();
      Log.v(TAG, "passed by");
      if(mPosterAdapter != null){
         mPosterAdapter.notifyDataSetChanged();
         mListView.setAdapter(mPosterAdapter);
      }
   }
}
