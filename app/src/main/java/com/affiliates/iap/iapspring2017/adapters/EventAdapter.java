//
//  EventAdapter.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/28/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.Constants;
import com.affiliates.iap.iapspring2017.Models.Event;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.services.DataService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class EventAdapter extends ArrayAdapter<Event> implements StickyListHeadersAdapter{
    private LayoutInflater inflater;

    private static class ViewHolder{
        TextView mTitle;
        TextView mTime;
        ViewHolder(View view){
            mTitle = (TextView) view.findViewById(R.id.list_event_title);
            mTime = (TextView) view.findViewById(R.id.time);
        }
    }

    class HeaderViewHolder {
        TextView text;
    }

    public EventAdapter(Context context, ArrayList<Event> events){
        super(context, 0, events);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup){
        ViewHolder viewHolder;
        Event event = getItem(position);
        if (convertView == null){
            convertView = inflater.inflate(R.layout.cell_event, viewGroup, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String time = event.getStartTime() + " - " + event.getEndTime();
        viewHolder.mTime.setText(time);
        viewHolder.mTitle.setText(event.getEventName());

        Calendar calendar = Calendar.getInstance();
        calendar.set(2017,3, 26, 13, 34,34);
        Date date = calendar.getTime();

        if(Constants.currentEvent == position){
            Log.v("IN", event.getEventName());
            viewHolder.mTitle.setText(event.getEventName());
            viewHolder.mTitle.setTextAppearance(getContext(),R.style.currentEvent);
        } else {
            viewHolder.mTitle.setTextColor(this.getContext().getResources().getColorStateList(R.color.appGrey));
            viewHolder.mTime.setText(time);
            viewHolder.mTitle.setTextAppearance(getContext(),R.style.notEvent);
            viewHolder.mTitle.setText(event.getEventName());
        }
        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.header_xml, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.header);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        String headerText = "" + getItem(position).getFormatedStartDate();
        holder.text.setText(headerText);
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return getItem(position).getStartDate().toString().subSequence(0, 1).charAt(0);
    }
}
