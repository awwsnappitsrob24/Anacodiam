package edu.csulb.rob.anacodiam.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.TextView;
import android.widget.Toast;

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


public class HomepageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AuthenticationService authenticationService;
    private HomepageActivity mSelf;

    private TextView suggestedCaloriesView, caloriesConsumedView, suggestedCaloriesNumView,
        caloriesConsumedNumView;

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


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_calories);
        fab.setOnClickListener(new View.OnClickListener() {
            // Initialize both calorie text views to 0 and add to them as you go
            int caloriesConsumed = 0;

            @Override
            public void onClick(View view) {
                // Add calories manually here
                //caloriesConsumed += caloriesConsumed;
                caloriesConsumedNumView.setGravity(Gravity.CENTER_HORIZONTAL);
                caloriesConsumedNumView.setTextSize(50);
                caloriesConsumedNumView.setText("2,000");

                suggestedCaloriesNumView.setGravity(Gravity.CENTER_HORIZONTAL);
                suggestedCaloriesNumView.setTextSize(50);
                suggestedCaloriesNumView.setText("3,250");
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
            Intent editProfileIntent = new Intent(this, EditProfileActivity.class);
            startActivity(editProfileIntent);
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
