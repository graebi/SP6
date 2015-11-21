package de.tg76.sp6;

/*
 * PagerAdapter - Develop an adapter which will plug the fragment screen into the activity - The adapter helps to place data onto the screen
 * Thorsten Graebner
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

class PagerAdapter extends FragmentStatePagerAdapter {
    private final int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Log.d("Testing", "PagerAdapter Fragment1");
                return new Fragment1();
            case 1:
                Log.d("Testing", "PagerAdapter Fragment2");
                return new Fragment2();
            case 2:
                Log.d("Testing", "PagerAdapter Fragment3");
                return new Fragment3();
            case 3:
                Log.d("Testing", "PagerAdapter Fragment4");
                return new Fragment4();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}