package edu.csulb.rob.anacodiam.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import edu.csulb.rob.anacodiam.Activities.API.APIClient;
import edu.csulb.rob.anacodiam.Activities.API.CalorieService;
import edu.csulb.rob.anacodiam.Activities.API.NutritionixAPISearch;
import edu.csulb.rob.anacodiam.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FoodEntry extends Fragment {

    Calendar calendar;
    View foodFragmentView;

    SearchView mySearchView;
    EditText searchEditText;
    ListView foodListView;
    double foodCalories;
    TextView textFoodCalories;

    CalorieService calorieService1, calorieService2;

    private ArrayList<Food> foodArrayList;
    private CustomFoodAdapter foodAdapter;

    public FoodEntry() {

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Inflate the layout for this fragment
        View foodFragmentView = inflater.inflate(R.layout.fragment_food_entry, container, false);

        //Set the listener for the CalendarView
        final CalendarView calDate = (CalendarView) foodFragmentView.findViewById(R.id.calFoodEntry);
        calDate.setDate(System.currentTimeMillis(),false,true);


        calDate.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                calendar = new GregorianCalendar(year, month, dayOfMonth);
            }
        });


        //------------------------Set the list view-----------------------------
        // Add food user entry

        //Variable setup
        calorieService1 = NutritionixAPISearch.getClient().create(CalorieService.class);
        calorieService2 = APIClient.getClient().create(CalorieService.class);

        textFoodCalories = foodFragmentView.findViewById(R.id.txtFoodCalories);

        mySearchView = foodFragmentView.findViewById(R.id.txtFoodName);
        mySearchView.setInputType(InputType.TYPE_CLASS_TEXT);
        int searchSrcTextId = getResources().getIdentifier("android:id/search_src_text",
                null, null);
        searchEditText = (EditText) foodFragmentView.findViewById(searchSrcTextId);
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

        // Instantiate ListView
        foodListView = (ListView) foodFragmentView.findViewById(R.id.lstFoodSearch);
        foodListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("didyouclicksomething", "YES"); // works thank goodness

                Food foodSelected = (Food)foodListView.getItemAtPosition(i);
                String foodNameSelected = foodSelected.getFoodName();
                Log.d("whatyouclick", foodNameSelected);

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

        // Call "Search Instant" Nutritionix API here
        mySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("donezo", "done");

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









        //Set the button click events
        Button temp = (Button) foodFragmentView.findViewById(R.id.btnFoodEntryOK);
        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(), HomepageActivity.class);

                //Place a value, indicating whether it's a food or exercise
                i.putExtra("type", "food");

                //Get value from Name field
                EditText txt = (EditText) foodFragmentView.findViewById(R.id.txtFoodName);
                i.putExtra("name", txt.getText().toString());

                //Get value from Calories field
                txt = (EditText) foodFragmentView.findViewById(R.id.txtFoodCalories);
                i.putExtra("calories", Integer.parseInt(txt.getText().toString()));

                //Get value from Carbohydrates field
                txt = (EditText) foodFragmentView.findViewById(R.id.txtFoodEntryCarbohydrates);
                i.putExtra("carbohydrates", Float.parseFloat(txt.getText().toString()));

                //Get value from Fats field
                txt = (EditText) foodFragmentView.findViewById(R.id.txtFoodEntryFats);
                i.putExtra("fats", Float.parseFloat(txt.getText().toString()));

                //Get value from Protein field
                txt = (EditText) foodFragmentView.findViewById(R.id.txtFoodEntryProtein);
                i.putExtra("protein", Float.parseFloat(txt.getText().toString()));

                //Set value from the calendar
                if(calendar == null){
                    //If calendar has not been selected, set the date to today.
                    calendar = new GregorianCalendar();
                }

                i.putExtra("date", calendar);



                startActivity(i);
            }
        });

        temp = (Button) foodFragmentView.findViewById(R.id.btnFoodEntryCancel);
        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), HomepageActivity.class);
                startActivity(i);
            }
        });

        //Return the view
        return foodFragmentView;
    }
}
