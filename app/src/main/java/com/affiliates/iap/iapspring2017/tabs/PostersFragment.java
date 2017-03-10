//
//  PostersFragment.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/7/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ListView;

import com.affiliates.iap.iapspring2017.Constants;
import com.affiliates.iap.iapspring2017.Models.Poster;
import com.affiliates.iap.iapspring2017.PosterDescription;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.adapters.PosterAdapter;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.services.DataService;

import java.util.ArrayList;
import java.util.Map;

public class PostersFragment extends android.support.v4.app.Fragment{
   private static final String TAG = "PostersFragment";
   private ArrayList<Poster> mPosters;
   private PosterAdapter mPosterAdapter;
   private ListView mListView;
   private View mRootView;

   public static PostersFragment newInstance(){
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

      if(Constants.getPosters() == null){
         DataService.sharedInstance().getPosters(new Callback() {
            @Override
            public void success(Object data) {
               Log.v(TAG, "Get posters succesfull");
               Constants.setPosters((Map<String, Poster>) data);

               mPosterAdapter = new PosterAdapter(getActivity().getBaseContext(), new ArrayList<Poster>(Constants.getPosters().values()));

               mListView = (ListView) mRootView.findViewById(R.id.poster_listview);
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
            }

            @Override
            public void failure(String message) {
               Log.e(TAG, "Failed to get posters");
            }
         });
      } else{
         mPosterAdapter = new PosterAdapter(getActivity().getBaseContext(),new ArrayList<Poster>(Constants.getPosters().values()));
         mListView = (ListView) mRootView.findViewById(R.id.poster_listview);
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
      }

      return mRootView;
   }
}
