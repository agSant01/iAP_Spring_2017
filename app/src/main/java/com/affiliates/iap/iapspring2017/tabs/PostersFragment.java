//
//  PostersFragment.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/7/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.tabs;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.affiliates.iap.iapspring2017.Constants;
import com.affiliates.iap.iapspring2017.Models.Poster;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.activities.MainActivity;
import com.affiliates.iap.iapspring2017.activities.PosterDescriptionActivity;
import com.affiliates.iap.iapspring2017.adapters.PosterAdapter;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.services.DataService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class PostersFragment extends Fragment {
   private static final String TAG = "PostersFragment";
   static PosterAdapter mPosterAdapter;
   private ListView mListView;
   private ProgressBar mPB;
   private View mRootView;

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

      mRootView = inflater.inflate(R.layout.list_view, container, false);
      mListView = (ListView) mRootView.findViewById(R.id.poster_listview);
      mPB = (ProgressBar) mRootView.findViewById(R.id.progressBar);
      mPB.setVerticalFadingEdgeEnabled(true);
      ((MainActivity)getActivity()).showProgressBar(mPB);
      mPosterAdapter = new PosterAdapter(getActivity().getBaseContext(), new ArrayList<Poster>());
      // run a background job and once complete
      if(Constants.getPosters() == null){
         DataService.sharedInstance().getPosters(new Callback<HashMap<Integer, Poster>>() {
            @Override
            public void success(HashMap<Integer, Poster> data) {
               Log.v(TAG, "Get posters succesfull");
               HashMap<String, Poster> ps = new HashMap<String, Poster>();
               ArrayList<Poster> sort = new ArrayList<Poster>();

               Constants.setPosters(ps);
               //Sort keys and iterate through the array of sorted keys, insted of iterating through the hashmap
               Object[] keys = data.keySet().toArray();
               Arrays.sort(keys);
               Constants.setSortedPosters(sort);

               for(Object in: keys) {
                  int i = (int) in;
                  ps.put(data.get(i).getPosterID(), data.get(i));
                  sort.add(data.get(i));
                  Log.v(TAG, data.get(i).getProjectName());
               }
               mPosterAdapter.clear();
               mPosterAdapter.addAll(sort);
                for(Poster p : Constants.getPosters().values())
                  Log.v(TAG, p.getProjectName() + "<_");

               mListView.setLayoutTransition(new LayoutTransition());
               mListView.setAdapter(mPosterAdapter);
               mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                  @Override
                  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                     Intent in = new Intent(getActivity().getBaseContext(), PosterDescriptionActivity.class);
                     String ID = mPosterAdapter.getItem(position).getPosterID();
                     in.putExtra("posterID", ID);
                     startActivity(in);
                  }
               });

               ((MainActivity)getActivity()).hideProgressBar(mPB);
            }

            @Override
            public void failure(String message) {
               ((MainActivity) getActivity()).updateFragment(position);
               Log.e(TAG, "Failed to get posters");
            }
         });
      } else{
         mPosterAdapter = new PosterAdapter(getActivity().getBaseContext(),new ArrayList<>(Constants.getSortedPosters()));
         mListView.setAdapter(mPosterAdapter);
         mListView.setLayoutTransition(null);
         mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Intent in = new Intent(getActivity().getBaseContext(), PosterDescriptionActivity.class);
               String ID = mPosterAdapter.getItem(position).getPosterID();
               in.putExtra("posterID", ID);
               startActivity(in);
            }
         });
         ((MainActivity)getActivity()).hideProgressBar(mPB);
      }
      return mRootView;
   }

   public static void setSearchableAdapter(ArrayList<Poster> posters){
      mPosterAdapter.clear();
      if(posters == null)
         mPosterAdapter.addAll(new ArrayList<Poster>());
      else
         mPosterAdapter.addAll(posters);
   }

   @Override
   public void onResume() {
      super.onResume();
      Log.v(TAG, "passed by");
      if(mPosterAdapter != null){
         mPosterAdapter.notifyDataSetChanged();
          if(Constants.getPosters()!=null)
          mPosterAdapter.addAll(Constants.getSortedPosters());
      }
   }

   @Override
   public void onPause() {
      super.onPause();
       mPosterAdapter.clear();
   }
}
