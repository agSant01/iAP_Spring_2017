//
//  StudentsInterestAdapter.java
//  IAP
//
//  Created by Gabriel S. Santiago on 4/11/2017.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.content.Context;
import android.view.ViewGroup;
import android.view.View;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import com.affiliates.iap.iapspring2017.Models.IAPStudent;
import com.affiliates.iap.iapspring2017.R;
import com.squareup.picasso.Picasso;

public class StudentsInterestAdapter extends ArrayAdapter<IAPStudent> {
    private static class ViewHolder{
        private CircleImageView mProfilePic;
        private TextView mDepartment;
        private TextView mProjets;
        private TextView mName;
        ViewHolder(View view){
            mProfilePic = (CircleImageView) view.findViewById(R.id.profile_image);
            mDepartment = (TextView) view.findViewById(R.id._department);
            mName = (TextView) view.findViewById(R.id._name);
            mProjets = (TextView) view.findViewById(R.id._project);
        }
    }

    public StudentsInterestAdapter(Context context, ArrayList<IAPStudent> iapStudents){
        super(context, 0, iapStudents);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        IAPStudent iapStudent = getItem(position);
        String projects = "";

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.cell_student_interest, viewGroup, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Picasso.with(getContext()).load(iapStudent.getPhotoURL())
                .placeholder(R.drawable.ic_gender_0).error(R.drawable.ic_gender_0).into(holder.mProfilePic);
        holder.mName.setText(iapStudent.getName());
        holder.mDepartment.setText(iapStudent.getDepartment());
        int i;
        for(i = 0; i < iapStudent.getProjectNames().size()-1; i++){
            projects += iapStudent.getProjectNames().get(i) + ", ";
        }
        holder.mProjets.setText(projects + iapStudent.getProjectNames().get(i));
        Log.v("STUD: ", holder.mProjets.getText().toString());

        return convertView;
    }
}
