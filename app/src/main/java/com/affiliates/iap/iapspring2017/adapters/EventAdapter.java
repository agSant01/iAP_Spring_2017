//
//  EventAdapter.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/28/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.Models.Event;
import com.affiliates.iap.iapspring2017.R;

import java.util.ArrayList;

public class EventAdapter extends ArrayAdapter<Event> {

    private static class ViewHolder{
        TextView mTitle;
        TextView mTime;
    }

    public EventAdapter(Context context, ArrayList<Event> events){
        super(context, 0, events);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup){

        ViewHolder viewHolder;
        Event event = getItem(position);
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.cell_event, viewGroup, false);
            viewHolder.mTitle = (TextView) convertView.findViewById(R.id.list_event_title);
            viewHolder.mTime = (TextView) convertView.findViewById(R.id.time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String time = event.getStartTime() + " - " + event.getEndTime();

        viewHolder.mTime.setText(time);
        viewHolder.mTitle.setText(event.getEventName());

        return convertView;
    }

}