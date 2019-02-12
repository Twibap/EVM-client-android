package io.mystudy.tnn.myevmapplication.manual;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    static final int TOTAL_PAGES = 3;

    SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment
        int sectionNumber = position + 1;
        return PlaceholderFragment
                .newInstance(sectionNumber, sectionNumber == getCount() );
    }

    @Override
    public int getCount() {
        // Show 4 total pages.
        return TOTAL_PAGES;
    }
}
