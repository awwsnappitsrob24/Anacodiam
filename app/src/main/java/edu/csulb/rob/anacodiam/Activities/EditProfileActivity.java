package edu.csulb.rob.anacodiam.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.w3c.dom.Text;

import edu.csulb.rob.anacodiam.Activities.API.APIClient;
import edu.csulb.rob.anacodiam.Activities.API.ProfileService;
import edu.csulb.rob.anacodiam.R;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private ProfileService profileService;
    private EditProfileActivity mSelf;

    TextView firstNameView, lastNameView;
    EditText firstNameText, lastNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        profileService = APIClient.getClient().create(ProfileService.class);


        firstNameView = (TextView) findViewById(R.id.txtViewFirstName);
        lastNameView = (TextView) findViewById(R.id.txtViewLastName);
        firstNameText = (EditText) findViewById(R.id.txtFirstName);
        lastNameText = (EditText) findViewById(R.id.txtLastName);


        //Set up units spinner
        Spinner spinner = (Spinner) findViewById(R.id.spnWeightUnits);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.weight_units, android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                EditText txtWeight = (EditText) findViewById(R.id.txtWeight);

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

        Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create Profile then go back to Homepage
                createProfile();
            }
        });
    }

    public void createProfile() {
        JsonObject jObj = new JsonObject();

        // Call API and logout
        mSelf = this;
        Call<JsonElement> call = profileService.createprofile(RequestBody.create
                (MediaType.parse("application/json"), jObj.toString()));
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if(response.isSuccessful()) {
                    // Create profile then go to Profile page
                    //JsonObject jObj = response.body().getAsJsonObject();
                    //APIClient.setToken(jObj.get("token").getAsString());
                    //Log.d("token", jObj.get("token").getAsString());

                    //String firstName = jObj.get("first_name").getAsString();
                    //String lastName = jObj.get("last_name").getAsString();
                    //jObj.addProperty("first_name", firstName);
                    //jObj.addProperty("last_name", lastName);

                    Intent profileIntent = new Intent(mSelf.getApplicationContext(), ProfileActivity.class);
                    //Bundle extras = new Bundle();
                    //extras.putString("FIRST_NAME", firstName);
                    //extras.putString("LAST_NAME",lastName);
                    //profileIntent.putExtras(extras);
                    startActivity(profileIntent);
                    finish();
                } else {

                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
            }
        });
    }

}

