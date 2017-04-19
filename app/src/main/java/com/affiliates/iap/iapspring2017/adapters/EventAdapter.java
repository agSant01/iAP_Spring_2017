//
//  EventAdapter.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/28/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.Constants;
import com.affiliates.iap.iapspring2017.Models.Event;
import com.affiliates.iap.iapspring2017.R;

import java.util.ArrayList;

public class EventAdapter extends ArrayAdapter<Event> {
    ArrayList<Event> list1;
    ArrayList<Event> list2;

    private static class ViewHolder{
        TextView mTitle;
        TextView mTime;
        TextView separator;
        ViewHolder(View view){
            mTitle = (TextView) view.findViewById(R.id.list_event_title);
            mTime = (TextView) view.findViewById(R.id.time);
            separator = (TextView) view.findViewById(R.id.separator);
        }
    }

    public EventAdapter(Context context, ArrayList<Event> events){
        super(context, 0, events);
        splitEvents();
//        list1 = new ArrayList<>();
//        list2 = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup){
        ViewHolder viewHolder;
        TextView separator;
        Event event = getItem(position);
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.cell_event, viewGroup, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String time = event.getStartTime() + " - " + event.getEndTime();
        checkEvent(viewHolder.separator, event);
        viewHolder.mTime.setText(time);
        viewHolder.mTitle.setText(event.getEventName());


        return convertView;
    }

    public void checkEvent(TextView separator, Event event){
        if(!list1.isEmpty()) {
            if (list1.get(0) == event) {
                separator.setVisibility(View.VISIBLE);
                separator.setText("April 26");
            } else {
                if (list2.get(0) == event) {
                    separator.setVisibility(View.VISIBLE);
                    separator.setText("April 27");
                } else separator.setVisibility(View.GONE);
            }
        }
    }

    private void splitEvents(){
        list1 = new ArrayList<>();
        list2 = new ArrayList<>();
        Log.v("Split", Constants.getEvents().isEmpty()+"");
        for(Event e: Constants.getEvents()){
            Log.v("Split", e.getStartDate().toString());
            if(e.getStartDate().toString().contains("Apr 26")){
                list1.add(e);
                Log.v("Split", e.getEventName() + "Added to list 1");
            }
            else {
                list2.add(e);
                    Log.v("Split", e.getEventName() + "Added to list 2");
            }
        }

    }
}
