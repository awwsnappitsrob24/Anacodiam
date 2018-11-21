package edu.csulb.rob.anacodiam.Activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

import edu.csulb.rob.anacodiam.Activities.ExerciseEntry;
import edu.csulb.rob.anacodiam.Activities.FoodEntry;

public class FoodExerciseEntryTabPagerAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    int tabCount;

    public FoodExerciseEntryTabPagerAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.tabCount = numberOfTabs;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        fragments.add(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        fragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                FoodEntry tab1 = new FoodEntry();
                return tab1;
            case 1:
                ExerciseEntry tab2 = new ExerciseEntry();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}