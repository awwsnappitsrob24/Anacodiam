package edu.csulb.rob.anacodiam.Activities;

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
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.SearchView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import edu.csulb.rob.anacodiam.Activities.API.APIClient;
import edu.csulb.rob.anacodiam.Activities.API.CalorieService;
import edu.csulb.rob.anacodiam.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ExerciseEntry extends Fragment {
    Calendar calendar;
    View exerciseFragmentView;
    SearchView exerciseSearch;
    EditText exerciseText, caloriesBurnedText;
    CalorieService exerciseService;

    public ExerciseEntry() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Inflate the layout for this fragment
        View exerciseFragmentView = inflater.inflate(R.layout.fragment_exercise_entry, container, false);

        //Set the listener for the CalendarView
        final CalendarView calDate = (CalendarView) exerciseFragmentView.findViewById(R.id.calExerciseEntry);
        calDate.setDate(System.currentTimeMillis(),false,true);


        calDate.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                calendar = new GregorianCalendar(year, month, dayOfMonth);
            }
        });

        caloriesBurnedText = (EditText)  exerciseFragmentView.findViewById(R.id.txtExerciseCalories);
        caloriesBurnedText.setFocusable(false);

        exerciseSearch = exerciseFragmentView.findViewById(R.id.txtExerciseName);
        exerciseSearch.setInputType(InputType.TYPE_CLASS_TEXT);
        int searchSrcTextId = getResources().getIdentifier("android:id/search_src_text",
                null, null);
        exerciseText = (EditText) exerciseFragmentView.findViewById(searchSrcTextId);
        exerciseText.setTextColor(Color.BLACK);
        exerciseSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                exerciseText.setFocusable(true);
                exerciseText.setFocusableInTouchMode(true);
                exerciseText.requestFocus();
                return true;
            }
        });
        exerciseSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // make edit text not focusable and hide list view
                exerciseText.setFocusable(false);

                // hide keyboard after hitting search key
                exerciseSearch.clearFocus();

                // call exercise api here
                exerciseService =  APIClient.getClient().create(CalorieService.class);
                Call<JsonElement> call = exerciseService.getcaloriesburned(exerciseSearch.getQuery().toString());
                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                        if(response.isSuccessful()) {
                            JsonObject result = response.body().getAsJsonObject();
                            double caloriesBurned = result.get("calories").getAsDouble();
                            caloriesBurnedText.setText(Double.toString(caloriesBurned));
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
                return false;
            }
        });


        //Set the button click events
        Button temp = (Button) exerciseFragmentView.findViewById(R.id.btnExerciseEntryOK);
        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(), HomepageActivity.class);

                //Place a value, indicating whether it's a food or exercise
                i.putExtra("type", "exercise");

                //Get value from Name field
                i.putExtra("name", exerciseText.getText().toString());

                //Get value from Calories field
                i.putExtra("caloriesburned", Double.parseDouble(caloriesBurnedText.getText().toString()));

                //Set value from the calendar
                if(calendar == null){
                    //If calendar has not been selected, set the date to today.
                    calendar = new GregorianCalendar();
                }

                i.putExtra("date", calendar);



                startActivity(i);
            }
        });

        temp = (Button) exerciseFragmentView.findViewById(R.id.btnExerciseEntryCancel);
        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), HomepageActivity.class);
                startActivity(i);
            }
        });

        //Return the view
        return exerciseFragmentView;
    }
}
