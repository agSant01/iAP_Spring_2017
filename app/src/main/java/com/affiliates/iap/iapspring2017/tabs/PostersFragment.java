//
//  PostersFragment.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/7/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.tabs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.affiliates.iap.iapspring2017.Constants;
import com.affiliates.iap.iapspring2017.Models.Poster;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.adapters.PosterAdapter;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.services.DataService;

import java.util.ArrayList;

public class PostersFragment extends android.support.v4.app.Fragment{
   private static final String TAG = "PostersFragment";
   private ArrayList<Poster> mPosters;
   private PosterAdapter mPosterAdapter;
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
               Constants.setPosters((ArrayList<Poster>) data);
               mPosterAdapter = new PosterAdapter(getActivity().getBaseContext(), Constants.getPosters());

               ListView listView = (ListView) mRootView.findViewById(R.id.poster_listview);
               listView.setAdapter(mPosterAdapter);

//               listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
//                  @Override
//                  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                     Intent in = new Intent(getActivity().getBaseContext(), PosterDescription.class);
//                     in.putExtra("posterPosition", position);
//                     Animation a = new AlphaAnimation(0.3f , 0.9f);
//                     a.setDuration(700);
//                     view.startAnimation(a);
//                     getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
//                     startActivity(in);
//                  }
//               });
            }

            @Override
            public void failure(String message) {
               Log.e(TAG, "Failed to get posters");
            }
         });
      } else{
         mPosterAdapter = new PosterAdapter(getActivity().getBaseContext(), Constants.getPosters());

         ListView listView = (ListView) mRootView.findViewById(R.id.poster_listview);
         listView.setAdapter(mPosterAdapter);

//         listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//               Intent in = new Intent(getActivity().getBaseContext(), PosterDescription.class);
//               in.putExtra("posterPosition", position);
//               Animation a = new AlphaAnimation(0.3f , 0.9f);
//               a.setDuration(700);
//               view.startAnimation(a);
//               getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
//               startActivity(in);
//            }
//         });
      }


      return mRootView;
   }
}
