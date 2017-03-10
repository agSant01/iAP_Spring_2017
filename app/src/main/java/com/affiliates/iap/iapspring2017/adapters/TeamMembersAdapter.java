package com.affiliates.iap.iapspring2017.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.Models.IAPStudent;
import com.affiliates.iap.iapspring2017.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class TeamMembersAdapter extends ArrayAdapter<IAPStudent> {
    private Context context;

    public TeamMembersAdapter(Context context, int resourceId, List<IAPStudent> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    private class ViewHolder {
        CircleImageView image;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        IAPStudent rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = mInflater.inflate(R.layout.team_member, null);
        holder = new ViewHolder();
        holder.image = (CircleImageView) convertView.findViewById(R.id.profile_image);

        Picasso.with(context).load(rowItem.getPhotoURL()).placeholder(R.drawable.ic_gender)
                .error(R.drawable.ic_gender).into(holder.image);

        convertView.setTag(holder);
        TextView tv = (TextView) convertView.findViewById(R.id.ol);
        tv.setText(rowItem.getName());
        return convertView;
    }
}
