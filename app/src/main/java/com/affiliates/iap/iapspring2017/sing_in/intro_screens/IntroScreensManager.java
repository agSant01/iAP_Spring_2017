package com.affiliates.iap.iapspring2017.sing_in.intro_screens;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class IntroScreensManager extends FragmentPagerAdapter {
    public IntroScreensManager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position < 5) {
            Log.v("ONTROMANAGER" , position+"");
            return IntroScreen.newInstance(position);
        }else {
            return SignInFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return 6;
    }
}
