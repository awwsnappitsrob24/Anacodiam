package edu.csulb.rob.anacodiam.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;

import java.util.Calendar;
import java.util.GregorianCalendar;

import edu.csulb.rob.anacodiam.R;


public class ExerciseEntry extends Fragment {
    Calendar calendar;
    View exerciseFragmentView;

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

        //Set the button click events
        Button temp = (Button) exerciseFragmentView.findViewById(R.id.btnExerciseEntryOK);
        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(), HomepageActivity.class);

                //Place a value, indicating whether it's a food or exercise
                i.putExtra("type", "exercise");

                //Get value from Name field
                EditText txt = (EditText) exerciseFragmentView.findViewById(R.id.txtExerciseName);
                i.putExtra("name", txt.getText().toString());

                //Get value from Calories field
                txt = (EditText) exerciseFragmentView.findViewById(R.id.txtExerciseCalories);
                i.putExtra("calories", Integer.parseInt(txt.getText().toString()));

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
