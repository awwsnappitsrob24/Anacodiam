package edu.csulb.rob.anacodiam.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.csulb.rob.anacodiam.Activities.API.APIClient;
import edu.csulb.rob.anacodiam.Activities.API.AuthenticationService;
import edu.csulb.rob.anacodiam.Activities.API.CalorieService;
import edu.csulb.rob.anacodiam.Activities.API.NutritionixAPISearch;
import edu.csulb.rob.anacodiam.Activities.API.ProfileService;
import edu.csulb.rob.anacodiam.R;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.app.AlertDialog;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class HomepageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AuthenticationService authenticationService;
    private ProfileService profileService;
    private CalorieService calorieService1, calorieService2;
    private HomepageActivity mSelf;
    private TextView suggestedCaloriesView, caloriesConsumedView, suggestedCaloriesNumView,
        caloriesConsumedNumView;
    private EditText searchEditText;
    private TextView textFoodCalories;
    private ListView foodListView;
    private SearchView mySearchView;
    private ArrayList<Food> foodArrayList;
    private CustomFoodAdapter foodAdapter;
    private VerticalTextView yAxisLabel;

    // Need to be calculated
    double bmr = 0, foodCalories = 0, caloriesConsumed = 0, caloriesSuggested = 0, proteinIntake = 0,
        carbIntake = 0, fatIntake = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navdrawer_homepage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        profileService = APIClient.getClient().create(ProfileService.class);

        suggestedCaloriesView = (TextView) findViewById(R.id.suggestedCaloriesView);
        suggestedCaloriesNumView = (TextView) findViewById(R.id.suggestedCaloriesNumView);
        caloriesConsumedView = (TextView) findViewById(R.id.caloriesConsumedView);
        caloriesConsumedNumView = (TextView) findViewById(R.id.caloriesConsumedNumView);

        caloriesConsumedNumView.setGravity(Gravity.CENTER_HORIZONTAL);
        caloriesConsumedNumView.setTextSize(40);
        caloriesConsumedNumView.setText(Double.toString(caloriesConsumed));

        suggestedCaloriesNumView.setGravity(Gravity.CENTER_HORIZONTAL);
        suggestedCaloriesNumView.setTextSize(40);

        yAxisLabel = (VerticalTextView) findViewById(R.id.yAxisTextView);
        yAxisLabel.setText("Calories");

        calorieService1 = NutritionixAPISearch.getClient().create(CalorieService.class);
        calorieService2 = APIClient.getClient().create(CalorieService.class);

        // Simulate someone taking a pill. Run this method every X seconds and feed the app calories
        // Show changes in the graph.
        BarChart barChart = (BarChart) findViewById(R.id.calorieChart);
        barChart.setDrawBarShadow(true);
        barChart.setDrawBarShadow(true);
        barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(true);

        ArrayList<BarEntry> goalEntries = new ArrayList<>();
        goalEntries.add(new BarEntry(1, 0f));
        goalEntries.add(new BarEntry(2, 0f));
        goalEntries.add(new BarEntry(3, 0f));
        goalEntries.add(new BarEntry(4, 0f));

        ArrayList<BarEntry> intakeEntries = new ArrayList<>();
        intakeEntries.add(new BarEntry(1, 0f));
        intakeEntries.add(new BarEntry(2, 0f));
        intakeEntries.add(new BarEntry(3, 0f));
        intakeEntries.add(new BarEntry(4, 0f));

        BarDataSet barDataGoalSet = new BarDataSet(goalEntries, "Goals");
        barDataGoalSet.setColors(Color.GREEN);

        BarDataSet barDataIntakeSet = new BarDataSet(intakeEntries, "Intake");
        barDataIntakeSet.setColors(Color.BLUE);

        BarData data = new BarData(barDataGoalSet, barDataIntakeSet);

        setGraphSettings(barChart, data);

        String[] nutrients = new String[] {"", "Calories", "Protein", "Carbohydrates", "Fats", ""};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter(nutrients));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setCenterAxisLabels(true);
        xAxis.setAxisMinimum(1);
        xAxis.setAxisMaximum(barDataGoalSet.getXMax() + 0.99f);
        xAxis.setAxisMaximum(barDataIntakeSet.getXMax() + 0.99f);

        barChart.getAxisRight().setEnabled(false);
        barChart.setDrawBarShadow(false);
        barChart.getDescription().setEnabled(false);
        barChart.invalidate();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_calories);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Build an alert dialog where the user can enter the amount of calories ingested.
                AlertDialog.Builder builder = new AlertDialog.Builder(HomepageActivity.this);
                builder.setTitle("Calorie Input");

                Context context = view.getContext();
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                //Add Food Name Fields
                final TextView txtFoodName = new TextView (HomepageActivity.this);
                txtFoodName.setText("Food:");
                txtFoodName.setTextSize(20);
                layout.addView(txtFoodName);

                // Add food user entry
                mySearchView = new SearchView(getApplicationContext());
                mySearchView.setInputType(InputType.TYPE_CLASS_TEXT);
                int searchSrcTextId = getResources().getIdentifier("android:id/search_src_text",
                        null, null);
                searchEditText = (EditText) mySearchView.findViewById(searchSrcTextId);
                searchEditText.setTextColor(Color.BLACK);
                mySearchView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        searchEditText.setFocusable(true);
                        searchEditText.setFocusableInTouchMode(true);
                        searchEditText.requestFocus();
                        return true;
                    }
                });
                layout.addView(mySearchView);

                // Instantiate ListView
                foodListView = new ListView(HomepageActivity.this);
                foodListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        Food foodSelected = (Food)foodListView.getItemAtPosition(i);
                        String foodNameSelected = foodSelected.getFoodName();

                        searchEditText.setText(foodNameSelected);

                        // hide keyboard after clicking on item
                        mySearchView.clearFocus();

                        // hide list view
                        foodListView.setVisibility(View.GONE);

                        // make the search text not focusable since the user has already selected
                        searchEditText.setFocusable(false);

                        // Call nutrients API
                        Call<JsonElement> call = calorieService2.getfoodnutrients(mySearchView.getQuery().toString());
                        call.enqueue(new Callback<JsonElement>() {
                            @Override
                            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                                if(response.isSuccessful()) {
                                        JsonObject result = response.body().getAsJsonObject();
                                        foodCalories = result.get("calories").getAsDouble();
                                        proteinIntake += result.get("protein").getAsDouble();
                                        carbIntake += result.get("carb").getAsDouble();
                                        fatIntake += result.get("fat").getAsDouble();
                                        textFoodCalories.setText(Double.toString(foodCalories));
                                } else {

                                }
                            }

                            @Override
                            public void onFailure(Call<JsonElement> call, Throwable t) {
                                call.cancel();
                            }
                        });
                    }
                });
                layout.addView(foodListView);

                // Call "Search Instant" Nutritionix API here
                mySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {

                        // make edit text not focusable and hide list view
                        searchEditText.setFocusable(false);
                        //layout.removeView(foodListView);
                        foodListView.setVisibility(View.GONE);

                        // hide keyboard after hitting search key
                        mySearchView.clearFocus();


                        // also call nutrients api here
                        Call<JsonElement> call = calorieService2.getfoodnutrients(mySearchView.getQuery().toString());
                        call.enqueue(new Callback<JsonElement>() {
                            @Override
                            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                                if(response.isSuccessful()) {
                                    JsonObject result = response.body().getAsJsonObject();
                                    foodCalories = result.get("calories").getAsDouble();
                                    proteinIntake += result.get("protein").getAsDouble();
                                    carbIntake += result.get("carb").getAsDouble();
                                    fatIntake += result.get("fat").getAsDouble();
                                    textFoodCalories.setText(Double.toString(foodCalories));
                                } else {

                                }
                            }

                            @Override
                            public void onFailure(Call<JsonElement> call, Throwable t) {
                                call.cancel();
                            }
                        });
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String query) {
                        // Call instant search
                        //foodListView.setVisibility(View.VISIBLE);

                        // to get focus back.... works!!!!
                        searchEditText.setFocusable(true);
                        searchEditText.setFocusableInTouchMode(true);
                        searchEditText.requestFocus();

                        // Make list view visible again
                        foodListView.setVisibility(View.VISIBLE);

                        Call<JsonElement> call = calorieService1.searchfood(mySearchView.getQuery().toString());
                        call.enqueue(new Callback<JsonElement>() {
                            @Override
                            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                                if(response.isSuccessful()) {

                                    foodArrayList = new ArrayList<Food>();

                                    // Iterate through JSON response and get "COMMON" foods
                                    JsonObject jObj = response.body().getAsJsonObject();
                                    JsonArray commonArray =  jObj.getAsJsonArray("common");
                                    for(int i = 0; i < commonArray.size(); i++) {
                                        foodAdapter = new CustomFoodAdapter(HomepageActivity.this, foodArrayList);
                                        foodListView.setAdapter(foodAdapter);

                                        JsonObject obj = (JsonObject)commonArray.get(i);
                                        String foodName = obj.get("food_name").getAsString();
                                        //Log.d("foodname", foodName);

                                        // Get thumbnail from photo json object
                                        JsonObject photoObj = obj.get("photo").getAsJsonObject();
                                        String thumb;
                                        if(!photoObj.get("thumb").isJsonNull()) {
                                            thumb = photoObj.get("thumb").getAsString();
                                        }
                                        else {
                                            thumb = null;
                                        }
                                        foodArrayList.add(new Food(thumb, foodName));
                                    }

                                    // Iterate through JSON response and get "BRANDED" foods
                                    JsonArray brandedArray =  jObj.getAsJsonArray("branded");
                                    for(int i = 0; i < brandedArray.size(); i++) {
                                        foodAdapter = new CustomFoodAdapter(HomepageActivity.this, foodArrayList);
                                        foodListView.setAdapter(foodAdapter);

                                        JsonObject obj = (JsonObject)brandedArray.get(i);
                                        String foodName = obj.get("food_name").getAsString();

                                        // Get thumbnail from photo json object
                                        JsonObject photoObj = obj.get("photo").getAsJsonObject();
                                        String thumb;
                                        if(!photoObj.get("thumb").isJsonNull()) {
                                            thumb = photoObj.get("thumb").getAsString();
                                        }
                                        else {
                                            thumb = null;
                                        }
                                        foodArrayList.add(new Food(thumb, foodName));
                                    }
                                } else {

                                }
                            }

                            @Override
                            public void onFailure(Call<JsonElement> call, Throwable t) {
                                call.cancel();
                            }
                        });
                        return true;
                    }
                });

                //Add Calorie Fields
                final TextView txtCalories = new TextView (HomepageActivity.this);
                txtCalories.setText("Calories:");
                txtCalories.setTextSize(20);
                layout.addView(txtCalories);

                textFoodCalories = new TextView(HomepageActivity.this);
                //textFoodCalories.setText(Double.toString(foodCalories));
                textFoodCalories.setTextSize(25);
                layout.addView(textFoodCalories);

                //Add Date Fields
                final TextView txtDate = new TextView (HomepageActivity.this);
                txtDate.setText("Date:");
                txtDate.setTextSize(20);
                layout.addView(txtDate);

                final DatePicker pckDate = new DatePicker(HomepageActivity.this);
                pckDate.setPadding(32, 8, 32, 0);
                pckDate.setScaleX(0.9f);
                pckDate.setScaleY(0.9f);
                layout.addView(pckDate);

                builder.setView(layout);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Get value from date picker
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(pckDate.getYear(), pckDate.getMonth(), pckDate.getDayOfMonth());

                        // Get total calories, protein, carbs, and fat for food
                        caloriesConsumed += Double.parseDouble(textFoodCalories.getText().toString());
                        caloriesConsumedNumView.setText(String.format("%.2f", caloriesConsumed));

                        setCalorieIntakeToGraph(intakeEntries, barChart, data, (float)caloriesConsumed);

                        // Fat has 9 calories per gram, protein and crabs have 4 calories per gram
                        setProteinIntakeToGraph(intakeEntries, barChart, data, (float)proteinIntake * 4);
                        setCarbIntakeToGraph(intakeEntries, barChart, data, (float)carbIntake * 4);
                        setFatIntakeToGraph(intakeEntries, barChart, data, (float)fatIntake * 9);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        authenticationService = APIClient.getClient().create(AuthenticationService.class);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Calculate BMR, call API and get user's profile
        mSelf = this;
        Call<JsonElement> call = profileService.getprofile();
        call.enqueue(new Callback<JsonElement>() {
            int weightInPounds, height, age;
            double weightInKilograms, proteinGoal, carbGoal, fatGoal;
            String gender, activityLevel;
             /// use this to pass on to the graph
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if(response.isSuccessful()) {
                    // Get profile and get the user's info
                    JsonObject jObj = response.body().getAsJsonObject();

                    weightInPounds = jObj.get("weight").getAsInt();
                    height = jObj.get("height").getAsInt();
                    gender = jObj.get("gender").getAsString();
                    age = jObj.get("age").getAsInt();
                    activityLevel = jObj.get("activity_level").getAsString();
                    weightInKilograms = weightInPounds / 2.205;

                    if(gender.equals("M")) {
                        bmr = (10 * weightInKilograms) + (6.25 * height) - (5 * age) + 5;
                    } else if(gender.equals("F")) {
                        bmr = (10 * weightInKilograms) + (6.25 * height) - (5 * age) - 161;
                    }

                    if(activityLevel.equalsIgnoreCase("Sedentary")) {
                        caloriesSuggested = (int) Math.round(bmr * 1.53);
                        carbGoal = caloriesSuggested * .45;
                        proteinGoal = caloriesSuggested * .35;
                        fatGoal = caloriesSuggested * .20;
                        suggestedCaloriesNumView.setText(Double.toString(caloriesSuggested));
                    } else if(activityLevel.equalsIgnoreCase("Mildly Active")) {
                        caloriesSuggested = (int) Math.round(bmr * 1.76);
                        carbGoal = caloriesSuggested * .50;
                        proteinGoal = caloriesSuggested * .30;
                        fatGoal = caloriesSuggested * .20;
                        suggestedCaloriesNumView.setText(Double.toString(caloriesSuggested));
                    } else if(activityLevel.equalsIgnoreCase("Very Active")) {
                        caloriesSuggested = (int) Math.round(bmr * 2.25);
                        carbGoal = caloriesSuggested * .55;
                        proteinGoal = caloriesSuggested * .25;
                        fatGoal = caloriesSuggested * .20;
                        suggestedCaloriesNumView.setText(Double.toString(caloriesSuggested));
                    }

                    Log.d("stuff", Integer.toString(weightInPounds));
                    Log.d("stuff", Integer.toString(height));
                    Log.d("stuff", Integer.toString(age));
                    Log.d("stuff", gender);
                    Log.d("stuff", activityLevel);
                    Log.d("stuff", suggestedCaloriesNumView.getText().toString());

                    setCalorieGoalToGraph(goalEntries, barChart, data, (float)caloriesSuggested);
                    setProteinGoalToGraph(goalEntries, barChart, data, (float)proteinGoal);
                    setCarbGoalToGraph(goalEntries, barChart, data, (float)carbGoal);
                    setFatGoalToGraph(goalEntries, barChart, data, (float)fatGoal);

                } else {
                    Log.d("bmr", "nope");
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
            }
        });

    }

    public void setCalorieGoalToGraph(ArrayList<BarEntry> myList, BarChart myChart, BarData myData,
                                      float calorieGoalValue) {
        myList.remove(0);
        myList.add(0, new BarEntry(1, calorieGoalValue));
        myChart.getAxisLeft().setAxisMaximum(calorieGoalValue + 500);
        setGraphSettings(myChart, myData);
    }

    public void setProteinGoalToGraph(ArrayList<BarEntry> myList, BarChart myChart, BarData myData,
                                      float proteinGoalValue) {
        myList.remove(1);
        myList.add(1, new BarEntry(2, proteinGoalValue));
        setGraphSettings(myChart, myData);
    }

    public void setCarbGoalToGraph(ArrayList<BarEntry> myList, BarChart myChart, BarData myData,
                                      float carbGoalValue) {
        myList.remove(2);
        myList.add(2, new BarEntry(3, carbGoalValue));
        setGraphSettings(myChart, myData);
    }

    public void setFatGoalToGraph(ArrayList<BarEntry> myList, BarChart myChart, BarData myData,
                                   float fatGoalValue) {
        myList.remove(3);
        myList.add(3, new BarEntry(4, fatGoalValue));
        setGraphSettings(myChart, myData);
    }

    public void setCalorieIntakeToGraph(ArrayList<BarEntry> myList, BarChart myChart, BarData myData,
                                      float calorieIntakeValue) {
        myList.remove(0);
        myList.add(0, new BarEntry(1, calorieIntakeValue));
        setGraphSettings(myChart, myData);
    }

    public void setProteinIntakeToGraph(ArrayList<BarEntry> myList, BarChart myChart, BarData myData,
                                        float proteinIntakeValue) {
        myList.remove(1);
        myList.add(1, new BarEntry(2,  proteinIntakeValue));
        setGraphSettings(myChart, myData);
    }

    public void setCarbIntakeToGraph(ArrayList<BarEntry> myList, BarChart myChart, BarData myData,
                                        float carbIntakeValue) {
        myList.remove(2);
        myList.add(2, new BarEntry(3, carbIntakeValue));
        setGraphSettings(myChart, myData);
    }

    public void setFatIntakeToGraph(ArrayList<BarEntry> myList, BarChart myChart, BarData myData,
                                        float fatIntakeValue) {
        myList.remove(3);
        myList.add(3, new BarEntry(4, fatIntakeValue));
        setGraphSettings(myChart, myData);
    }

    public void setGraphSettings(BarChart myChart, BarData myData) {
        float groupSpace = 0.2f;
        float barSpace = 0.05f;
        float barWidth = 0.35f;
        myChart.setData(myData);
        myData.setBarWidth(barWidth);
        myChart.groupBars(1, groupSpace, barSpace);
        myChart.invalidate();
    }

    public class MyXAxisValueFormatter implements IAxisValueFormatter {

        private String[] mValues;

        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues[(int) value];
        }
    }


    //A container to hold data for each food.
    private class CalorieData{

        private ArrayList<String> name;
        private ArrayList<Integer> calories;
        private ArrayList<Date> dates;

        public CalorieData(){
            name = new ArrayList<String>();
            calories = new ArrayList<Integer>();
            dates = new ArrayList<Date>();
        }

        public String getName(int index){
            return name.get(index);
        }

        public int getCalories(int index){
            return calories.get(index);
        }

        public Date getDate(int index){
            return dates.get(index);
        }

        public void addFood(String inputName, int inputCalories, Date inputDate){
            name.add(inputName);
            calories.add(inputCalories);
            dates.add(inputDate);
        }

        public void removeFood(int index){
            name.remove(index);
            calories.remove(index);
            dates.remove(index);
        }

        public String toString(){
            String returnString = "";

            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");

            for(int i = 0; i < name.size(); i++){
                returnString = returnString + sdf.format(dates.get(i)) + ":  " + name.get(i) + "  --  " + calories.get(i) + " calories\n";
            }

            return returnString.substring(0, returnString.length() - 1);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.homepage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout_settings) {
            // Logout here
            JsonObject jObj = new JsonObject();

            // Call API and logout
            mSelf = this;
            Call<JsonElement> call = authenticationService.logout(RequestBody.create
                    (MediaType.parse("application/json"), jObj.toString()));
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    if(response.isSuccessful()) {
                        // Finish the Homepage activity and go back to the login page
                        Intent backToLoginIntent = new Intent(mSelf.getApplicationContext(), LoginActivity.class);
                        startActivity(backToLoginIntent);
                        finish();

                        Log.d("Logout", "Logout successful");
                    } else {
                        // Do nothing
                        Log.d("Logout", "Couldn't logout");
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    call.cancel();
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        // Go to the appropriate page depending on what is clicked
        int id = item.getItemId();

        if (id == R.id.nav_view_profile) {
            // GO TO PROFILE PAGE, bring the user's name with it
            Intent viewProfileIntent = new Intent(this, ProfileActivity.class);
            startActivity(viewProfileIntent);
        } else if (id == R.id.nav_report) {
            // GO TO REPORT PAGE
            Intent reportPageIntent = new Intent(this, ReportActivity.class);
            startActivity(reportPageIntent);
        } else if (id == R.id.nav_share) {
            // GO TO SHARE PAGE
            Intent sharePageIntent = new Intent(this, ShareActivity.class);
            startActivity(sharePageIntent);
        } else if (id == R.id.nav_messages) {
            // GO TO MESSAGE PAGE
            Intent messagePageIntent = new Intent(this, MessageActivity.class);
            startActivity(messagePageIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
