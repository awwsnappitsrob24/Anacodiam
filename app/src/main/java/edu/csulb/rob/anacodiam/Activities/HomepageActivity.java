package edu.csulb.rob.anacodiam.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import edu.csulb.rob.anacodiam.Activities.API.APIClient;
import edu.csulb.rob.anacodiam.Activities.API.AuthenticationService;
import edu.csulb.rob.anacodiam.R;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.app.AlertDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class HomepageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AuthenticationService authenticationService;
    private HomepageActivity mSelf;

    private TextView suggestedCaloriesView, caloriesConsumedView, suggestedCaloriesNumView,
        caloriesConsumedNumView;

    CalorieData calorieData = new CalorieData();

    // Initialize both calorie text views to 0 and add to them as you go
    int caloriesConsumed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navdrawer_homepage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        suggestedCaloriesView = (TextView) findViewById(R.id.suggestedCaloriesView);
        suggestedCaloriesNumView = (TextView) findViewById(R.id.suggestedCaloriesNumView);
        caloriesConsumedView = (TextView) findViewById(R.id.caloriesConsumedView);
        caloriesConsumedNumView = (TextView) findViewById(R.id.caloriesConsumedNumView);

        caloriesConsumedNumView.setGravity(Gravity.CENTER_HORIZONTAL);
        caloriesConsumedNumView.setTextSize(40);
        caloriesConsumedNumView.setText(Integer.toString(caloriesConsumed));

        suggestedCaloriesNumView.setGravity(Gravity.CENTER_HORIZONTAL);
        suggestedCaloriesNumView.setTextSize(40);
        suggestedCaloriesNumView.setText("3,250");

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

                final EditText editTextFoodName = new EditText(HomepageActivity.this);
                editTextFoodName.setInputType(InputType.TYPE_CLASS_TEXT);
                layout.addView(editTextFoodName);

                //Add Calorie Fields
                final TextView txtCalories = new TextView (HomepageActivity.this);
                txtCalories.setText("Calories:");
                txtCalories.setTextSize(20);
                layout.addView(txtCalories);

                final EditText editTextCalories = new EditText(HomepageActivity.this);
                editTextCalories.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
                layout.addView(editTextCalories);

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


                        Integer calorieInteger = new Integer(editTextCalories.getText().toString());
                        calorieData.addFood(editTextFoodName.getText().toString(), calorieInteger, calendar.getTime());

                        caloriesConsumed += calorieInteger.intValue();
                        caloriesConsumedNumView.setText(Integer.toString(caloriesConsumed));



                        TextView calorieList = (TextView) findViewById(R.id.editTxtCalorieList);
                        calorieList.setText(calorieData.toString());
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

        // Initialize line graph here for the calorie information with dummy values for now
        GraphView graph = (GraphView) findViewById(R.id.calorie_graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                // REPLACE THESE VALUES WITH CALORIES FROM DATABASE
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        graph.addSeries(series);
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

        if (id == R.id.nav_edit_profile) {
            // GO TO EDIT PROFILE PAGE
            Intent updateProfileIntent = new Intent(this, UpdateProfileActivity.class);
            startActivity(updateProfileIntent);
        } else if (id == R.id.nav_view_profile) {
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
