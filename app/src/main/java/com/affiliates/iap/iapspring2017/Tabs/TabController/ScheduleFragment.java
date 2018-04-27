//
//  ScheduleFragment.java
//  IAP
//
//  Created by Gabriel S. Santiago on 2/19/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Tabs.TabController;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ProgressBar;

import com.affiliates.iap.iapspring2017.Interfaces.Callback;
import com.affiliates.iap.iapspring2017.Models.Event;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.Services.ConstantsService;
import com.affiliates.iap.iapspring2017.Services.DataService;
import com.affiliates.iap.iapspring2017.Tabs.EventTab.EventAdapter;

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

        if (ConstantsService.getEvents() == null){
            DataService.sharedInstance().getEvent(new Callback<ArrayList<Event>>() {
                @Override
                public void success(ArrayList<Event> data) {
                    Log.v(TAG, "Get events succesfull");
                    ConstantsService.setEvents(data);
                    ConstantsService.sortEvents();
                    mEventAdapter = new EventAdapter(getActivity(), ConstantsService.getEvents());
                    mListView.setAdapter(mEventAdapter);
                    mListView.setSelection(ConstantsService.currentEvent);
                    Log.v(TAG, "Schedule " + ConstantsService.currentEvent);
                    mPB.startAnimation(fadeOutAnimation);
                    mPB.setVisibility(ProgressBar.INVISIBLE);
                }

                @Override
                public void failure(String message) {

                }
            });
        } else {
            ConstantsService.sortEvents();
            mEventAdapter = new EventAdapter(getContext(), ConstantsService.getEvents());
            mListView.setAdapter(mEventAdapter);
            mListView.setSelection(ConstantsService.currentEvent);
            Log.v(TAG, "ITEM " + ConstantsService.currentEvent);
            mPB.startAnimation(fadeOutAnimation);
            mPB.setVisibility(ProgressBar.INVISIBLE);
        }
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mListView != null) {
            mEventAdapter = new EventAdapter(getContext(), ConstantsService.getEvents());
            ConstantsService.sortEvents();
            mListView.smoothScrollToPosition(ConstantsService.currentEvent);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mListView != null) {
            mEventAdapter = new EventAdapter(getContext(), ConstantsService.getEvents());
            ConstantsService.sortEvents();
            mListView.smoothScrollToPosition(ConstantsService.currentEvent);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mListView != null) {
            mEventAdapter = new EventAdapter(getContext(), ConstantsService.getEvents());
            ConstantsService.sortEvents();
            mListView.smoothScrollToPosition(ConstantsService.currentEvent);
        }
    }
}
