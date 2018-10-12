package edu.csulb.rob.anacodiam.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import edu.csulb.rob.anacodiam.R;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        /*TextView dummyText = (TextView)findViewById(R.id.dummy);
        dummyText.setText("PROFILE PAGE");*/

        //Set up units spinner
        Spinner spinner = (Spinner) findViewById(R.id.spnWeightUnits);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.weight_units, android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                EditText txtWeight = (EditText) findViewById(R.id.txtEmail);

                if (txtWeight.getText() != null && txtWeight.getText().toString() != null && ! txtWeight.getText().toString().equals("")) {

                    double previousWeight = Double.parseDouble(txtWeight.getText().toString());

                    if (parent.getItemAtPosition(position).toString().equals("kg")) {
                        if (txtWeight.getText().toString() != null) {

                            txtWeight.setText(Double.toString(Math.round(previousWeight * (453.6 / 1000))));
                        }
                    } else {
                        if (txtWeight.getText().toString() != null) {

                            txtWeight.setText(Double.toString(Math.round(previousWeight * (1000 / 453.6))));
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Set up height spinner
        spinner = (Spinner) findViewById(R.id.spnHeight);

        adapter = ArrayAdapter.createFromResource(this,
                R.array.height_units, android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                EditText txtHeight1 = (EditText) findViewById(R.id.txtHeight);
                EditText txtHeight2 = (EditText) findViewById(R.id.txtHeight2);

                TextView txtViewHeight1 = (TextView) findViewById(R.id.txtViewHeightUnit1);
                TextView txtViewHeight2 = (TextView) findViewById(R.id.txtViewHeightUnit2);

                double previousHeight1 = 0, previousHeight2 = 0;
                boolean hasHeightValue = false;

                if (! txtHeight1.getText().toString().equals("")){
                    previousHeight1 = Double.parseDouble(txtHeight1.getText().toString());
                    hasHeightValue = true;
                }
                if (! txtHeight2.getText().toString().equals("")){
                    previousHeight2 = Double.parseDouble(txtHeight2.getText().toString());
                    hasHeightValue = true;
                }

                if (parent.getItemAtPosition(position).toString().equals("ft in")) {
                    txtHeight2.setVisibility(View.VISIBLE);
                    txtHeight2.setEnabled(true);
                    txtViewHeight2.setVisibility(View.VISIBLE);

                    txtViewHeight1.setText("ft");

                    if (hasHeightValue) {
                        double cmToIn = previousHeight1 / 2.54;

                        txtHeight1.setText(Double.toString(Math.round(cmToIn / 12)));
                        txtHeight2.setText(Double.toString(Math.round(cmToIn % 12)));
                    }
                } else {
                    txtHeight2.setVisibility(View.INVISIBLE);
                    txtHeight2.setEnabled(false);
                    txtViewHeight2.setVisibility(View.INVISIBLE);

                    txtViewHeight1.setText("cm");

                    if (hasHeightValue) {
                        double inTocm = previousHeight1 * 12 * 2.54 + previousHeight2 * 2.54;

                        txtHeight1.setText(Double.toString(Math.round(inTocm)));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Set up gender spinner
        spinner = (Spinner) findViewById(R.id.spnGender);

        adapter = ArrayAdapter.createFromResource(this,
                R.array.gender, android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        //Set up activity spinner
        spinner = (Spinner) findViewById(R.id.spnActivity);

        adapter = ArrayAdapter.createFromResource(this,
                R.array.activity_level, android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
    }

}

