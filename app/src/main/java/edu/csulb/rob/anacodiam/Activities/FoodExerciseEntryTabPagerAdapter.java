package edu.csulb.rob.anacodiam.Activities.API;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import edu.csulb.rob.anacodiam.Activities.ExerciseEntry;
import edu.csulb.rob.anacodiam.Activities.FoodEntry;

public class FoodExerciseEntryTabPagerAdapter extends FragmentPagerAdapter {

    int tabCount;

    public FoodExerciseEntryTabPagerAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.tabCount = numberOfTabs;
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