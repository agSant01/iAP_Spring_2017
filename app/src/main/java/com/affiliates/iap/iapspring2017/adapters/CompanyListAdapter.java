//
//  CompanyListAdapter.java
//  IAP
//
//  Created by Gabriel S. Santiago on 4/10/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.affiliates.iap.iapspring2017.Constants;
import com.affiliates.iap.iapspring2017.Models.Sponsors;
import com.affiliates.iap.iapspring2017.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CompanyListAdapter extends ArrayAdapter<Sponsors> {
    public CompanyListAdapter( Context context, ArrayList<Sponsors> list) {
        super(context, 0, list);
    }

    private static class ViewHolder{
        ImageView companyLogo;

        ViewHolder(View view){
            companyLogo = (ImageView) view.findViewById(R.id.image_view);
        }
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        String url = getItem(position).getCompanyLogo();
        if (convertView != null ){
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.cell_compay_logo, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
            Log.v("URL: ", url);
        }

        if(!Constants.getSponsorLogos().containsKey(getItem(position).getCompanyID())){
            Picasso.with(getContext()).load(url).placeholder(R.drawable.ic_logo).into(viewHolder.companyLogo, new Callback() {
                @Override
                public void onSuccess() {
                    Constants.getSponsorLogos().put(getItem(position)
                            .getCompanyID(),viewHolder.companyLogo.getDrawable());
                }

                @Override
                public void onError() {}
            });
        } else {
            viewHolder.companyLogo.setImageDrawable(
                    Constants.getSponsorLogos().
                            get(getItem(position).getCompanyID()));
        }
        return convertView;
    }
}
