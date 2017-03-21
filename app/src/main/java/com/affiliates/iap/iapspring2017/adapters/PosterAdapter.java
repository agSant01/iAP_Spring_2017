//
//  PosterAdapter.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/9/2017.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.Constants;
import com.affiliates.iap.iapspring2017.Models.CompanyUser;
import com.affiliates.iap.iapspring2017.Models.CompanyVote;
import com.affiliates.iap.iapspring2017.Models.Poster;
import com.affiliates.iap.iapspring2017.Models.User;
import com.affiliates.iap.iapspring2017.R;

import java.util.ArrayList;

public class PosterAdapter  extends ArrayAdapter<Poster> {

    private static class ViewHolder{
        TextView title;
        TextView mPosterNum;
        TextView mDptm;
        TextView mCategories;
        TextView mHasEval;
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
            viewHolder.mPosterNum = (TextView) convertView.findViewById(R.id.textView13);
            viewHolder.title = (TextView) convertView.findViewById(R.id.list_poster_title);
            viewHolder.mDptm = (TextView) convertView.findViewById(R.id.cell_poster_dept);
            viewHolder.mCategories = (TextView) convertView.findViewById(R.id.cell_poster_keyword);
            viewHolder.mHasEval = (TextView) convertView.findViewById(R.id.textView15);
            if(Constants.getCurrentLoggedInUser().getAccountType() == User.AccountType.CompanyUser){
                if( ((CompanyUser)Constants.getCurrentLoggedInUser()).hasEvaluated(poster.getPosterID()))
                    viewHolder.mHasEval.setBackgroundResource(R.drawable.list_circular_background);
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mPosterNum = (TextView) convertView.findViewById(R.id.textView13);
        viewHolder.title = (TextView) convertView.findViewById(R.id.list_poster_title);
        viewHolder.mDptm = (TextView) convertView.findViewById(R.id.cell_poster_dept);
        viewHolder.mCategories = (TextView) convertView.findViewById(R.id.cell_poster_keyword);
        viewHolder.mHasEval = (TextView) convertView.findViewById(R.id.textView15);

        viewHolder.title.setText(poster.getProjectName());
        viewHolder.mPosterNum.setText(poster.getPosterNumber()+"");
        viewHolder.mDptm.setText(poster.getPosterDptm());
        if(Constants.getCurrentLoggedInUser().getAccountType() == User.AccountType.CompanyUser){
            if( ((CompanyUser)Constants.getCurrentLoggedInUser()).hasEvaluated(poster.getPosterID()))
                viewHolder.mHasEval.setBackgroundResource(R.drawable.list_circular_background);
        }

        String m = "";
        ArrayList<String> l = poster.getCategories();
        for (int i = 0; i < l.size(); i++){
            m += l.get(i);
            if (l.get(i) == "" || i == l.size()-1 ){
                break;
            }
            m += ", ";
        }
        viewHolder.mCategories.setText(m);

        return convertView;
    }
}
