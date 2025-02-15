//
//  EvalManager.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/11/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.EvaluationCenter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class EvalManager extends FragmentPagerAdapter {
    public EvalManager(FragmentManager fm, Context context) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            return PosterEval.newInstance(position);
        } else if(position == 1){
            return Summary.newInstance(position);
        }
        return PresentationEval.newInstance(position);
    }

    @Override
    public int getCount() {
        return 3;
    }
}
