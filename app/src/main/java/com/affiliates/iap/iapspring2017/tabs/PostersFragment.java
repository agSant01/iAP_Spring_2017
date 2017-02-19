package com.affiliates.iap.iapspring2017.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.R;

/**
 * Created by gsantiago on 02-15-17.
 */

public class PostersFragment extends Fragment{

   public PostersFragment(){
   }

   public static PostersFragment newInstance(int sectionNumber){
      return new PostersFragment();
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstance) {
      View rootView = inflater.inflate(R.layout.fragment_main, container, false);
      TextView textView = (TextView) rootView.findViewById(R.id.section_label);
      textView.setText("6");
      return rootView;
   }
}
