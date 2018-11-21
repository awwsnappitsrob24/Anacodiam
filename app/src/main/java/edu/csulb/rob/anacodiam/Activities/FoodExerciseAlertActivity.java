package edu.csulb.rob.anacodiam.Activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import edu.csulb.rob.anacodiam.R;

public class FoodExerciseAlertActivity extends AppCompatActivity {


    protected void onCreate(Bundle savedInstanceState) {
        final String TAG = "FoodExerciseAlertAct";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_exercise_alert);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //Set up the tabs
        TabLayout tabLayout =
                (TabLayout) findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("Food"));
        tabLayout.addTab(tabLayout.newTab().setText("Exercise"));

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pagFoodEntryPager);
        final PagerAdapter adapter = new FoodExerciseEntryTabPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
               @Override
               public void onTabSelected(TabLayout.Tab tab) {
                   viewPager.setCurrentItem(tab.getPosition());
               }

               @Override
               public void onTabUnselected(TabLayout.Tab tab) {

               }

               @Override
               public void onTabReselected(TabLayout.Tab tab) {

               }

        });
    }
}
