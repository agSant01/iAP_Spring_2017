//
//  ScheduleFragment.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ProgressBar;

import com.affiliates.iap.iapspring2017.Constants;
import com.affiliates.iap.iapspring2017.Models.Event;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.adapters.EventAdapter;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.services.DataService;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ScheduleFragment extends Fragment {
    private static final String TAG = "ScheduleFragment";
    private EventAdapter mEventAdapter;
    private StickyListHeadersListView mListView;
    private ProgressBar mPB;
    private View mRootView;


    public static ScheduleFragment newInstance(){
        return new ScheduleFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstance) {
        mRootView = inflater.inflate(R.layout.schedule_listview, container, false);

        mListView = (StickyListHeadersListView) mRootView.findViewById(R.id.sticky_list);
        mPB = (ProgressBar) mRootView.findViewById(R.id.progressBar);
        mPB.setVisibility(ProgressBar.VISIBLE);
        mPB.setVerticalFadingEdgeEnabled(true);
        final AlphaAnimation fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);//fade from 1 to 0 alpha
        fadeOutAnimation.setDuration(1000);
        fadeOutAnimation.setFillAfter(true);//to keep it at 0 when animation ends

        if (Constants.getEvents() == null){
            DataService.sharedInstance().getEvent(new Callback<ArrayList<Event>>() {
                @Override
                public void success(ArrayList<Event> data) {
                    Log.v(TAG, "Get events succesfull");
                    Constants.setEvents(data);
                    Constants.sortEvents();
                    mEventAdapter = new EventAdapter(getActivity(), Constants.getEvents());
                    mListView.setAdapter(mEventAdapter);
                    mListView.setSelection(Constants.currentEvent);
                    Log.v(TAG, "Schedule " + Constants.currentEvent);
                    mPB.startAnimation(fadeOutAnimation);
                    mPB.setVisibility(ProgressBar.INVISIBLE);
                }

                @Override
                public void failure(String message) {

                }
            });
        } else {
            Constants.sortEvents();
            mEventAdapter = new EventAdapter(getContext(), Constants.getEvents());
            mListView.setAdapter(mEventAdapter);
            mListView.setSelection(Constants.currentEvent);
            Log.v(TAG, "ITEM " + Constants.currentEvent);
            mPB.startAnimation(fadeOutAnimation);
            mPB.setVisibility(ProgressBar.INVISIBLE);
        }
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mListView != null) {
            mEventAdapter = new EventAdapter(getContext(), Constants.getEvents());
            Constants.sortEvents();
            mListView.smoothScrollToPosition(Constants.currentEvent);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mListView != null) {
            mEventAdapter = new EventAdapter(getContext(), Constants.getEvents());
            Constants.sortEvents();
            mListView.smoothScrollToPosition(Constants.currentEvent);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mListView != null) {
            mEventAdapter = new EventAdapter(getContext(), Constants.getEvents());
            Constants.sortEvents();
            mListView.smoothScrollToPosition(Constants.currentEvent);
        }
    }
}
