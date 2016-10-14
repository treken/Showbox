package adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ui.fragments.FavoriteDetailsFragment;

public class FavoritesFragmentPagerAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 2;

    public FavoritesFragmentPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return FavoriteDetailsFragment.newInstance(FavoriteDetailsFragment.MOVIE_FAV);
            case 1: // Fragment # 0 - This will show FirstFragment different title
                return FavoriteDetailsFragment.newInstance(FavoriteDetailsFragment.TV_FAV);
            default:
                return null;
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Movies";
        } else {
            return "TV";
        }
    }

}