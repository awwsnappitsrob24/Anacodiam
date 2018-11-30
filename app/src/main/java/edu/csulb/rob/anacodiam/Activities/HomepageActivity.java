package edu.csulb.rob.anacodiam.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.StyleSpan;
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
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
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
            caloriesConsumedNumView, caloriesBurnedTxt;
    private EditText searchEditText;
    private TextView textFoodCalories;
    private ListView foodListView;
    private SearchView mySearchView;
    private ArrayList<Food> foodArrayList;
    private CustomFoodAdapter foodAdapter;
    private VerticalTextView yAxisLabel;

    // Need to be calculated
    double bmr = 0;
    int caloriesSuggested = 0, caloriesConsumed = 0, caloriesBurned = 0;

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
        caloriesConsumedNumView.setText(Integer.toString(caloriesConsumed));

        suggestedCaloriesNumView.setGravity(Gravity.CENTER_HORIZONTAL);
        suggestedCaloriesNumView.setTextSize(40);

        caloriesBurnedTxt = (TextView) findViewById(R.id.caloriesBurnedNumView);
        caloriesBurnedTxt.setGravity(Gravity.CENTER_HORIZONTAL);
        caloriesBurnedTxt.setTextSize(40);
        caloriesBurnedTxt.setText(Integer.toString(caloriesBurned));

        textFoodCalories = (TextView) findViewById(R.id.editTxtCalorieList);

        yAxisLabel = (VerticalTextView) findViewById(R.id.yAxisTextView);
        yAxisLabel.setText("Calories");

        calorieService1 = NutritionixAPISearch.getClient().create(CalorieService.class);
        calorieService2 = APIClient.getClient().create(CalorieService.class);

        //Set the table of food calorie data

        FoodData foodData = Global.getInstance().getFoodData();
        if(foodData == null){
            foodData = new FoodData();
            Global.getInstance().setFoodData(foodData);
        }

        final GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] {Color.parseColor("#C0C0C0"), Color.parseColor("#C0C0C0")});
        gradientDrawable.setGradientCenter(0.f, 1.f);
        gradientDrawable.setLevel(2);

        //TableLayout table = (TableLayout) findViewById(R.id.tblFoodEntries);
        //TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        //tableRowParams.setMargins(3, 3, 2, 10);
        //table.setLayoutParams(tableRowParams);
        //table.setBackgroundDrawable(gradientDrawable);

        TextView newText;
        TableRow.LayoutParams lp;
        SpannableString spanString;
        TableRow row;

        int indexOffset = 4;
        int rowHeight = 70;

        foodData.sortByDate();

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");

        int indexCounter = 0;

        for(int x = foodData.getSize() - 1; x >= 0 ; x--) {

            //Food Name and Date
            row = new TableRow(this);
            row.setBackgroundColor(Color.CYAN);
            row.setWeightSum(1);
            //lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 50);
            //row.setLayoutParams(lp);

            spanString = new SpannableString(foodData.getName(x));
            spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
            newText = new TextView(this);
            newText.setText(spanString);
            newText.setGravity(Gravity.LEFT);
            lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.5f);
            newText.setLayoutParams(lp);
            row.addView(newText);

            spanString = new SpannableString(sdf.format(foodData.getDate(x)));
            spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
            newText = new TextView(this);
            newText.setText(spanString);
            newText.setGravity(Gravity.RIGHT);
            lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.5f);
            newText.setLayoutParams(lp);
            row.addView(newText);

            //table.addView(row, indexCounter);
            indexCounter++;


            //Calorie Row
            row = new TableRow(this);
            row.setGravity(Gravity.CENTER);
            row.setWeightSum(1);

            newText = new TextView(this);
            spanString = new SpannableString("Calories: ");
            spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
            newText.setText(spanString);
            lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.2f);
            newText.setLayoutParams(lp);
            row.addView(newText);

            newText = new TextView(this);
            newText.setText(String.valueOf(foodData.getCalories(x)));
            lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.8f);
            newText.setLayoutParams(lp);
            row.addView(newText);

            //table.addView(row, indexCounter);
            indexCounter++;


            //Macros Header Row
            row = new TableRow(this);
            row.setGravity(Gravity.CENTER);
            row.setWeightSum(1);


            newText = new TextView(this);
            spanString = new SpannableString("Carbohydrates");
            spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
            newText.setText(spanString);
            lp = new TableRow.LayoutParams(0, rowHeight, 0.33f);
            lp.topMargin = 30;
            newText.setLayoutParams(lp);
            row.addView(newText);

            newText = new TextView(this);
            spanString = new SpannableString("Fats");
            spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
            newText.setText(spanString);
            lp = new TableRow.LayoutParams(0, rowHeight, 0.34f);
            lp.topMargin = 30;
            newText.setLayoutParams(lp);
            row.addView(newText);

            newText = new TextView(this);
            spanString = new SpannableString("Protein");
            spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
            newText.setText(spanString);
            lp = new TableRow.LayoutParams(0, rowHeight, 0.33f);
            lp.topMargin = 30;
            newText.setLayoutParams(lp);
            row.addView(newText);

            //table.addView(row, indexCounter);
            indexCounter++;


            //Macros Data Row
            row = new TableRow(this);
            row.setGravity(Gravity.CENTER);
            row.setWeightSum(1);

            newText = new TextView(this);
            newText.setText(String.valueOf((int) foodData.getCarbohydrates(x)));
            lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.33f);
            lp.leftMargin = 10;
            newText.setLayoutParams(lp);
            row.addView(newText);

            newText = new TextView(this);
            newText.setText(String.valueOf((int) foodData.getFats(x)));
            lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.34f);
            lp.leftMargin = 10;
            newText.setLayoutParams(lp);
            row.addView(newText);

            newText = new TextView(this);
            newText.setText(String.valueOf((int) foodData.getProtein(x)));
            lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.33f);
            lp.leftMargin = 10;
            newText.setLayoutParams(lp);
            row.addView(newText);

            //table.addView(row, indexCounter);
            indexCounter++;
        }

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
                Intent a = new Intent(getApplicationContext(), FoodExerciseAlertActivity.class);
                startActivity(a);
            }
        });

        authenticationService = APIClient.getClient().create(AuthenticationService.class);

        // Calculate BMR, call API and get user's profile
        mSelf = this;

        Call<JsonElement> call = profileService.getprofile();
        call.enqueue(new Callback<JsonElement>() {
            int weightInPounds, height, age;
            double weightInKilograms, proteinGoal, carbGoal, fatGoal;
            String firstName = " ", lastName = " ";
            String gender, activityLevel;
            /// use this to pass on to the graph
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if(response.isSuccessful()) {
                    // Get profile and get the user's info
                    JsonObject jObj = response.body().getAsJsonObject();
                    firstName = jObj.get("first_name").getAsString();
                    lastName = jObj.get("last_name").getAsString();
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
                        suggestedCaloriesNumView.setText(Integer.toString(caloriesSuggested));
                    } else if(activityLevel.equalsIgnoreCase("Mildly Active")) {
                        caloriesSuggested = (int) Math.round(bmr * 1.76);
                        carbGoal = caloriesSuggested * .50;
                        proteinGoal = caloriesSuggested * .30;
                        fatGoal = caloriesSuggested * .20;
                        suggestedCaloriesNumView.setText(Integer.toString(caloriesSuggested));
                    } else if(activityLevel.equalsIgnoreCase("Very Active")) {
                        caloriesSuggested = (int) Math.round(bmr * 2.25);
                        carbGoal = caloriesSuggested * .55;
                        proteinGoal = caloriesSuggested * .25;
                        fatGoal = caloriesSuggested * .20;
                        suggestedCaloriesNumView.setText(Integer.toString(caloriesSuggested));
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

                    JsonObject userObj = jObj.get("user").getAsJsonObject();
                    String email = " ";
                    if(!userObj.get("email").isJsonNull()) {
                        email = userObj.get("email").getAsString();
                    }

                    setDrawerHeader(firstName, lastName, email);

                } else {
                    Log.d("bmr", "nope");
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Get info from user entries and set them to the graph
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // from food
            float caloriesFromEntry = extras.getFloat("calories", 0);
            float carbsFromEntry = extras.getFloat("carbohydrates", 0);
            float fatsFromEntry = extras.getFloat("fats", 0);
            float proteinsFromEntry = extras.getFloat("protein", 0);

            // from exercise
            double caloriesFromExerciseEntry = extras.getDouble("caloriesburned", 0);
            int caloriesBurnedRounded = (int)Math.round(caloriesFromExerciseEntry);
            Log.d("burned", Double.toString(caloriesFromExerciseEntry));

            // Fat has 9 calories per gram, protein and carbs have 4 calories per gram
            setCalorieIntakeToGraph(intakeEntries, barChart, data, caloriesFromEntry);
            setProteinIntakeToGraph(intakeEntries, barChart, data, proteinsFromEntry * 4);
            setCarbIntakeToGraph(intakeEntries, barChart, data, carbsFromEntry * 4);
            setFatIntakeToGraph(intakeEntries, barChart, data, fatsFromEntry * 9);

            // Get total calories, protein, carbs, and fat for food
            caloriesConsumed += caloriesFromEntry;
            caloriesConsumedNumView.setText(Integer.toString(caloriesConsumed));

            // Get calories burned from exercise
            caloriesBurnedTxt.setText(Integer.toString(caloriesBurnedRounded));
        }

    }

    public void setDrawerHeader(String fName, String lName, String email) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);
        TextView navFullName = (TextView) headerView.findViewById(R.id.navFullName);
        navFullName.setText(fName + " " + lName);
        TextView navEmail = (TextView) headerView.findViewById(R.id.navEmail);
        navEmail.setText(email);

        navigationView.setNavigationItemSelectedListener(this);
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
        } else if (id == R.id.nav_link) {
            // GO TO LINK PAGE
            Intent linkPageIntent = new Intent(this, LinkActivity.class);
            startActivity(linkPageIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
