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
import android.widget.ImageView;
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
        ImageView check;
        ViewHolder(View view){
            check = (ImageView) view.findViewById(R.id.check);
            mPosterNum = (TextView) view.findViewById(R.id.poster_number);
            title = (TextView) view.findViewById(R.id.list_poster_title);
            mDptm = (TextView) view.findViewById(R.id.cell_poster_dept);
            mCategories = (TextView) view.findViewById(R.id.cell_poster_keyword);
            mHasEval = (TextView) view.findViewById(R.id.textView15);
        }
    }

    public PosterAdapter(Context context, ArrayList<Poster> posters) {
        super(context, 0, posters);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup){
        ViewHolder viewHolder;
        Poster poster = getItem(position);
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.cell_posters, viewGroup, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.title.setText(poster.getProjectName());
        viewHolder.mPosterNum.setText(poster.getPosterNumber() + "");
        viewHolder.mDptm.setText(poster.getPosterDptm());
        if(Constants.getCurrentLoggedInUser().getAccountType() == User.AccountType.CompanyUser){
            if( ((CompanyUser)Constants.getCurrentLoggedInUser()).hasEvaluated(poster.getPosterID())){
                viewHolder.mPosterNum.setVisibility(View.INVISIBLE);
                viewHolder.mHasEval.setVisibility(View.INVISIBLE);
                viewHolder.check.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mPosterNum.setVisibility(View.VISIBLE);
                viewHolder.mHasEval.setVisibility(View.VISIBLE);
                viewHolder.check.setVisibility(View.INVISIBLE);
            }
        }else{
            if(Constants.getCurrentLoggedInUser().hasVoted(0) && Constants.getCurrentLoggedInUser().hasVoted(1)){
                viewHolder.mPosterNum.setVisibility(View.INVISIBLE);
                viewHolder.mHasEval.setVisibility(View.INVISIBLE);
                viewHolder.check.setVisibility(View.VISIBLE);
            }
            else{
                viewHolder.mPosterNum.setVisibility(View.VISIBLE);
                viewHolder.mHasEval.setVisibility(View.VISIBLE);
                viewHolder.check.setVisibility(View.INVISIBLE);
            }
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
