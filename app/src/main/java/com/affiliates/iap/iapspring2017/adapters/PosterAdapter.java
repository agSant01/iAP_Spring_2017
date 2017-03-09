package com.affiliates.iap.iapspring2017.adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import com.affiliates.iap.iapspring2017.Models.Poster;
import com.affiliates.iap.iapspring2017.R;

public class PosterAdapter  extends ArrayAdapter<Poster> {

    private static class ViewHolder{
        TextView title;
    }

    public PosterAdapter(Context context, ArrayList<Poster> posters){
        super(context, 0, posters);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup){

        ViewHolder viewHolder;
        Poster poster = getItem(position);
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.cell_posters, viewGroup, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.list_poster_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.title = (TextView) convertView.findViewById(R.id.list_poster_title);
        viewHolder.title.setText(poster.getProjectName());

        return convertView;
    }
}
