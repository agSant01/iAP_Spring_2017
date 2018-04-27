//
//  TeamAdvisorAdapter.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/11/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Tabs.PostersTab.PosterDescription;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.Models.Advisor;
import com.affiliates.iap.iapspring2017.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeamAdvisorsAdapter extends ArrayAdapter<Advisor> {
    public TeamAdvisorsAdapter(Context context, int resourceId, List<Advisor> items) {
        super(context, resourceId, items);
    }

    private class ViewHolder {
        CircleImageView mImage;
        TextView mTextView;
        ViewHolder(View view){
            mImage = (CircleImageView) view.findViewById(R.id.profile_image);
            mTextView = (TextView) view.findViewById(R.id.ol);
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Advisor advisor = getItem(position);
        ViewHolder holder;

        if (convertView != null){
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.poster_team_member, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        Picasso.with(getContext()).load(advisor.getPhotoURL()).placeholder(R.drawable.ic_gender_0)
                .error(R.drawable.ic_gender_0).into(holder.mImage);
        holder.mTextView.setText(advisor.getName().length()<5 ? "Name not specified" : advisor.getName());
        return convertView;
    }
}
