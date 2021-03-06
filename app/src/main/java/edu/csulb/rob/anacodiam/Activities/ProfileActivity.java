package edu.csulb.rob.anacodiam.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import edu.csulb.rob.anacodiam.Activities.API.APIClient;
import edu.csulb.rob.anacodiam.Activities.API.ProfileService;
import edu.csulb.rob.anacodiam.R;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private ProfileService profileService;
    private String firstName;
    private String lastName;
    private double heightInCM;
    private double weightInLBS;
    private String gender;
    private int age;
    private String activityLevel;
    private String dob;
    private EditText feetEditText;
    private EditText inchesEditText;
    private EditText cmEditText;
    private EditText lbsEditText;
    private EditText kgEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageView mountainBackground = new ImageView(this);
        mountainBackground.setImageResource(R.drawable.profile_background);

        profileService = APIClient.getClient().create(ProfileService.class);

        TextView profName = (TextView) findViewById(R.id.textActualName);
        TextView profHeight = (TextView) findViewById(R.id.textActualHeight);
        TextView profWeight = (TextView) findViewById(R.id.textActualWeight);
        TextView profAge = (TextView) findViewById(R.id.textActualAge);
        TextView profGender = (TextView) findViewById(R.id.textActualGender);
        TextView profActivityLevel = (TextView) findViewById(R.id.textActualActivityLevel);

        // Call "get profile" API to get user's information
        JsonObject jObj = new JsonObject();
        Call<JsonElement> call = profileService.getprofile();
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if(response.isSuccessful()) {
                    // Get profile and get the user's info
                    JsonObject jObj = response.body().getAsJsonObject();
                    firstName = jObj.get("first_name").getAsString();
                    lastName = jObj.get("last_name").getAsString();
                    heightInCM = jObj.get("height").getAsDouble();
                    weightInLBS = jObj.get("weight").getAsDouble();
                    gender = jObj.get("gender").getAsString();
                    age = jObj.get("age").getAsInt();
                    dob = jObj.get("dob").getAsString();
                    activityLevel = jObj.get("activity_level").getAsString();

                    // Conversions in height and weight
                    int feetPart = (int) Math.floor((heightInCM / 2.54) / 12);
                    int inchesPart = (int)Math.ceil((heightInCM / 2.54) - (feetPart * 12));
                    double weightInKG = weightInLBS / 2.205;

                    // Set text views to corresponding user information
                    profName.setText(firstName + " " + lastName);
                    profHeight.setText(feetPart + " feet " + inchesPart + " inches / " +
                            String.format("%.2f", heightInCM) + " cm");
                    profWeight.setText(weightInLBS + " lbs / " + String.format("%.2f", weightInKG) + " kg");
                    profAge.setText(Integer.toString(age));
                    if(gender.equalsIgnoreCase("M")) {
                        profGender.setText("Male");
                    } else {
                        profGender.setText("Female");
                    }
                    profActivityLevel.setText(activityLevel.substring(0,1).toUpperCase() +
                            activityLevel.substring(1));

                } else {

                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
            }
        });

        // Update part of the profile depending on which floating action button is pressed
        FloatingActionButton updateNameFab = findViewById(R.id.updateName);
        updateNameFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create dialog box
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Update name");
                builder.setMessage("What is your updated name?");

                final EditText nameInput = new EditText(ProfileActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                nameInput.setLayoutParams(lp);
                nameInput.setHint("FirstName LastName");
                builder.setView(nameInput);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Update name first
                        profName.setText(nameInput.getText().toString());

                        // Call update profile API (comment: dob will NEVER change, also split the name)
                        String[] splitName = nameInput.getText().toString().split(" +");
                        String fName = splitName[0];
                        String lname = splitName[1];
                        updateName(fName, lname);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.show();
            }
        });

        FloatingActionButton updateHeightFab = findViewById(R.id.updateHeight);
        updateHeightFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create dialog box
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Update Height");
                builder.setMessage("What is your updated height?");

                Context context = view.getContext();
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                ArrayList<String> heightArray = new ArrayList<>();
                heightArray.add("ft. in.");
                heightArray.add("cm");
                Spinner heightSpinner = new Spinner(ProfileActivity.this);
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(ProfileActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, heightArray);
                heightSpinner.setAdapter(spinnerArrayAdapter);
                layout.addView(heightSpinner);

                feetEditText = new EditText(ProfileActivity.this);
                feetEditText.setHint("feet");
                feetEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                layout.addView(feetEditText);

                inchesEditText = new EditText(ProfileActivity.this);
                inchesEditText.setHint("inches");
                inchesEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                layout.addView(inchesEditText);

                cmEditText = new EditText(ProfileActivity.this);
                cmEditText.setHint("cm");
                cmEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                layout.addView(cmEditText);


                heightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if(heightSpinner.getSelectedItem().toString().equalsIgnoreCase("ft. in.")) {
                            layout.removeView(cmEditText);
                            feetEditText.setVisibility(View.VISIBLE);
                            inchesEditText.setVisibility(View.VISIBLE);
                        } else {
                            layout.addView(cmEditText);
                            feetEditText.setVisibility(View.GONE);
                            inchesEditText.setVisibility(View.GONE);
                            cmEditText.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        // Convert ft and in to cm
                        if(heightSpinner.getSelectedItem().toString().equalsIgnoreCase("ft. in.")) {
                            if(!feetEditText.getText().toString().equals("") && !inchesEditText.getText()
                                    .toString().equals("")) {
                                int feetEntered = Integer.parseInt(feetEditText.getText().toString());
                                int inchesEntered = Integer.parseInt(inchesEditText.getText().toString());
                                double cmEquivalent = feetEntered * 12 * 2.54 + inchesEntered * 2.54;

                                profHeight.setText(feetEntered + " feet " + inchesEntered + " inches / " +
                                        String.format("%.2f", cmEquivalent) + " cm");
                                updateHeight(cmEquivalent);
                            } else {
                                // Should set error message....
                            }

                        } else {

                            // Convert cm to ft and in
                            int feet = (int) Math.floor((Double.parseDouble(cmEditText.getText().toString())
                                    / 2.54) / 12);
                            int inches = (int)Math.ceil((Double.parseDouble(cmEditText.getText().toString())
                                    / 2.54) - (feet * 12));
                            profHeight.setText(feet + " feet " + inches + " inches / " +
                                    String.format("%.2f", Double.parseDouble(cmEditText.getText().toString()))
                                    + " cm");
                            updateHeight(Double.parseDouble(cmEditText.getText().toString()));
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.setView(layout);
                builder.show();
            }
        });

        FloatingActionButton updateWeightFab = findViewById(R.id.updateWeight);
        updateWeightFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create dialog box
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Update Weight");
                builder.setMessage("What is your updated weight?");

                Context context = view.getContext();
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                ArrayList<String> weightArray = new ArrayList<>();
                weightArray.add("lbs");
                weightArray.add("kg");
                Spinner weightSpinner = new Spinner(ProfileActivity.this);
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(ProfileActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, weightArray);
                weightSpinner.setAdapter(spinnerArrayAdapter);
                layout.addView(weightSpinner);

                lbsEditText = new EditText(ProfileActivity.this);
                lbsEditText.setHint("lbs");
                lbsEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                layout.addView(lbsEditText);

                kgEditText = new EditText(ProfileActivity.this);
                kgEditText.setHint("kg");
                kgEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                layout.addView(kgEditText);

                weightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if(weightSpinner.getSelectedItem().toString().equalsIgnoreCase("lbs")) {
                            layout.removeView(kgEditText);
                            lbsEditText.setVisibility(View.VISIBLE);
                        } else {
                            layout.addView(kgEditText);
                            lbsEditText.setVisibility(View.GONE);
                            kgEditText.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Only update if required fields are filled out. If not, do nothing.
                        // Convert lbs to kg
                        if(weightSpinner.getSelectedItem().toString().equalsIgnoreCase("lbs")) {
                            if(!lbsEditText.getText().toString().equals("")) {
                                double lbsEntered = Double.parseDouble(lbsEditText.getText().toString());
                                double kgEquivalent = lbsEntered / 2.205;

                                profWeight.setText((String.format("%.2f", lbsEntered) + " lbs / " +
                                        String.format("%.2f", kgEquivalent) + " kg"));
                                updateWeight(lbsEntered);
                            }
                        } else {
                            // Convert kg to lbs
                            if(!kgEditText.getText().toString().equals("")) {
                                double kgEntered =  Double.parseDouble(kgEditText.getText().toString());
                                double lbsEquivalent = kgEntered * 2.205;
                                profWeight.setText((String.format("%.2f", lbsEquivalent) + " lbs / " +
                                        String.format("%.2f", kgEntered) + " kg"));
                                updateWeight(lbsEquivalent);
                            }
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.setView(layout);
                builder.show();
            }
        });

        FloatingActionButton updateGenderFab = findViewById(R.id.updateGender);
        updateGenderFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create dialog box
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Update Gender");
                builder.setMessage("What is your updated gender?");

                ArrayList<String> genderArray = new ArrayList<>();
                genderArray.add("Male");
                genderArray.add("Female");

                Spinner genderSpinner = new Spinner(ProfileActivity.this);
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(ProfileActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, genderArray);
                genderSpinner.setAdapter(spinnerArrayAdapter);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                genderSpinner.setLayoutParams(lp);
                builder.setView(genderSpinner);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Update name first
                        profGender.setText(genderSpinner.getSelectedItem().toString());

                        // Call update profile API (comment: dob will NEVER change, also split the name)
                        updateGender(profGender.getText().toString());
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.show();
            }
        });

        FloatingActionButton updateActivityLevelFab = findViewById(R.id.updateActivityLevel);
        updateActivityLevelFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create dialog box
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Update Activity Level");
                builder.setMessage("What is your updated activity level?");

                ArrayList<String> levelArray = new ArrayList<>();
                levelArray.add("Sedentary");
                levelArray.add("Mildly Active");
                levelArray.add("Very Active");

                Spinner levelSpinner = new Spinner(ProfileActivity.this);
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(ProfileActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, levelArray);
                levelSpinner.setAdapter(spinnerArrayAdapter);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                levelSpinner.setLayoutParams(lp);
                builder.setView(levelSpinner);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Update name first
                        profActivityLevel.setText(levelSpinner.getSelectedItem().toString());

                        // Call update profile API (comment: dob will NEVER change, also split the name)
                        updateActivityLevel(profActivityLevel.getText().toString());
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    // Helper functions below to call the update profile API depending on which field the user
    // chooses to update. Comment: there is no update AGE. The age automatically updates. Age is
    // calculated by teh DOB that was specified when creating the profile with respect to today's date.
    public void updateName(String newfname, String newlname) {
        JsonObject updatedObj = new JsonObject();
        updatedObj.addProperty("first_name", newfname);
        updatedObj.addProperty("last_name", newlname);
        updatedObj.addProperty("weight", weightInLBS);
        updatedObj.addProperty("height", heightInCM);
        updatedObj.addProperty("dob", dob);
        updatedObj.addProperty("gender", gender);
        updatedObj.addProperty("activity_level", activityLevel);

        Call<JsonElement> call = profileService.updateprofile(RequestBody.create
                (MediaType.parse("application/json"), updatedObj.toString()));
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if(response.isSuccessful()) {


                } else {

                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
            }
        });
    }

    // Either in ft'in or cm
    public void updateHeight(double newHeight) {
        // Call update profile API (comment: dob will NEVER change, also split the name)
        JsonObject updatedObj = new JsonObject();
        updatedObj.addProperty("first_name", firstName);
        updatedObj.addProperty("last_name", lastName);
        updatedObj.addProperty("weight", weightInLBS);
        updatedObj.addProperty("height", newHeight);
        updatedObj.addProperty("dob", dob);
        updatedObj.addProperty("gender", gender);
        updatedObj.addProperty("activity_level", activityLevel);

        Call<JsonElement> call = profileService.updateprofile(RequestBody.create
                (MediaType.parse("application/json"), updatedObj.toString()));
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if(response.isSuccessful()) {


                } else {

                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
            }
        });
    }

    // Either in lbs or kg
    public void updateWeight(double newWeight) {
        // Call update profile API (comment: dob will NEVER change, also split the name)
        JsonObject updatedObj = new JsonObject();
        updatedObj.addProperty("first_name", firstName);
        updatedObj.addProperty("last_name", lastName);
        updatedObj.addProperty("weight", newWeight);
        updatedObj.addProperty("height", heightInCM);
        updatedObj.addProperty("dob", dob);
        updatedObj.addProperty("gender", gender);
        updatedObj.addProperty("activity_level", activityLevel);

        Call<JsonElement> call = profileService.updateprofile(RequestBody.create
                (MediaType.parse("application/json"), updatedObj.toString()));
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if(response.isSuccessful()) {


                } else {

                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
            }
        });
    }

    public void updateGender(String newGender) {
        // Call update profile API (comment: dob will NEVER change, also split the name)
        JsonObject updatedObj = new JsonObject();
        updatedObj.addProperty("first_name", firstName);
        updatedObj.addProperty("last_name", lastName);
        updatedObj.addProperty("weight", weightInLBS);
        updatedObj.addProperty("height", heightInCM);
        updatedObj.addProperty("dob", dob);
        if(newGender.equalsIgnoreCase("Male")) {
            newGender = "M";
        } else {
            newGender = "F";
        }
        updatedObj.addProperty("gender", newGender);
        updatedObj.addProperty("activity_level", activityLevel);

        Call<JsonElement> call = profileService.updateprofile(RequestBody.create
                (MediaType.parse("application/json"), updatedObj.toString()));
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if(response.isSuccessful()) {


                } else {

                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
            }
        });
    }

    public void updateActivityLevel(String newActivityLevel) {
        // Call update profile API (comment: dob will NEVER change, also split the name)
        JsonObject updatedObj = new JsonObject();
        updatedObj.addProperty("first_name", firstName);
        updatedObj.addProperty("last_name", lastName);
        updatedObj.addProperty("weight", weightInLBS);
        updatedObj.addProperty("height", heightInCM);
        updatedObj.addProperty("dob", dob);
        updatedObj.addProperty("gender", gender);
        updatedObj.addProperty("activity_level", newActivityLevel);

        Call<JsonElement> call = profileService.updateprofile(RequestBody.create
                (MediaType.parse("application/json"), updatedObj.toString()));
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if(response.isSuccessful()) {


                } else {

                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent homePageIntent = new Intent(this, HomepageActivity.class);
        startActivity(homePageIntent);
    }

}
