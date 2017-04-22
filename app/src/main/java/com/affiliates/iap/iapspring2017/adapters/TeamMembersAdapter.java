//
//  TeamMembersAdapter.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/10/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.Models.IAPStudent;
import com.affiliates.iap.iapspring2017.R;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeamMembersAdapter extends ArrayAdapter<IAPStudent> {
     public TeamMembersAdapter(Context context, int resourceId, List<IAPStudent> items) {
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
        IAPStudent iapStudent = getItem(position);
        ViewHolder holder;

        if (convertView != null){
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.poster_team_member, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        Picasso.with(getContext()).load(iapStudent.getPhotoURL()).placeholder(R.drawable.ic_gender_0)
                .error(R.drawable.ic_gender_0).into(holder.mImage);
        holder.mTextView.setText(iapStudent.getName().equals("NA") ? "Name not specified" : iapStudent.getName());
        return convertView;
    }
}
