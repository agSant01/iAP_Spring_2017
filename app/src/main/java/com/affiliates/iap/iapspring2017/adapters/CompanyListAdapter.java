package com.affiliates.iap.iapspring2017.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.Constants;
import com.affiliates.iap.iapspring2017.Models.Sponsors;
import com.affiliates.iap.iapspring2017.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class CompanyListAdapter extends ArrayAdapter<Sponsors> {
    private Context context;

    public CompanyListAdapter( Context context, List<Sponsors> list) {
        super(context, 0, list);
        this.context = context;
    }

    private static class ViewHolder{
        ImageView companyLogo;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewHolder viewHolder;
        String url = getItem(position).getCompanyLogo();
        if (convertView == null ){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.image_logo, parent, false);
            viewHolder.companyLogo = (ImageView) convertView.findViewById(R.id.image_view);
            convertView.setTag(viewHolder);
            Log.v("my test", url);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(!Constants.getSponsorLogos().containsKey(getItem(position).getCompanyID())){
            Picasso.with(context).load(url).into(viewHolder.companyLogo, new Callback() {
                @Override
                public void onSuccess() {
                    Constants.getSponsorLogos().put(getItem(position).getCompanyID(),viewHolder.companyLogo.getDrawable());
                }

                @Override
                public void onError() {

                }
            });
        } else {
            viewHolder.companyLogo.setImageDrawable(Constants.getSponsorLogos().get(getItem(position).getCompanyID()));
            Log.v("lKKNN" , Constants.getSponsorLogos().get(getItem(position).getCompanyID()).toString());
        }
        return convertView;
    }
}
