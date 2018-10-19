package edu.csulb.rob.anacodiam.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.csulb.rob.anacodiam.Activities.API.APIClient;
import edu.csulb.rob.anacodiam.Activities.API.AuthenticationService;
import edu.csulb.rob.anacodiam.R;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRegistrationActivity extends AppCompatActivity {

    private TextView firstNameView, lastNameView, passwordView, confirmPasswordView, emailView;
    private EditText firstNameText, lastNameText, passwordText, confirmPasswordText, emailText;
    private Button submitButton;

    private AuthenticationService authenticationService;

    private UserRegistrationActivity mSelf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        authenticationService = APIClient.getClient().create(AuthenticationService.class);

        firstNameView = (TextView) findViewById(R.id.txtViewFirstName);
        lastNameView = (TextView) findViewById(R.id.txtViewLastName);
        passwordView = (TextView) findViewById(R.id.txtViewFirstName);
        confirmPasswordView = (TextView) findViewById(R.id.txtViewLastName);
        emailView = (TextView) findViewById(R.id.txtViewWeight);

        firstNameText = (EditText) findViewById(R.id.txtFirstName);
        lastNameText = (EditText) findViewById(R.id.txtLastName);
        passwordText = (EditText) findViewById(R.id.txtFirstName);
        confirmPasswordText = (EditText) findViewById(R.id.txtLastName);
        emailText = (EditText) findViewById(R.id.txtWeight);

        submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistration();
            }
        });
    }

    /**
     * Attempts to register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual registration attempt is made.
     */
    private void attemptRegistration() {
        // Reset errors.
        firstNameView.setError(null);
        lastNameView.setError(null);
        passwordView.setError(null);
        confirmPasswordView.setError(null);
        emailView.setError(null);

        // Store values at the time of the registration attempt.
        String fName = firstNameText.getText().toString();
        String lName = lastNameText.getText().toString();
        String password = passwordText.getText().toString();
        String confirmPassword = confirmPasswordText.getText().toString();
        String email = emailText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check if the user entered a name (field is required)
        if(TextUtils.isEmpty(fName)) {
            firstNameView.setError(getString(R.string.error_field_required));
            focusView = firstNameView;
            cancel = true;
        }
        if (TextUtils.isEmpty(lName)) {
            lastNameView.setError(getString(R.string.error_field_required));
            focusView = lastNameView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if(TextUtils.isEmpty(password)) {
            passwordView.setError(getString(R.string.error_field_required));
            focusView = passwordView;
            cancel = true;
        } else if((!isPasswordValid(password))) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }


        // Check for a valid email address, if the user entered one
        if (TextUtils.isEmpty(email)) {
            emailView.setError(getString(R.string.error_field_required));
            focusView = emailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailView.setError(getString(R.string.error_invalid_email));
            focusView = emailView;
            cancel = true;
        }

        // Check if user confirmed password (confirmed password must be same as original password)
        if(TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordView.setError(getString(R.string.error_field_required));
            focusView = confirmPasswordView;
            cancel = true;
        } else if(!confirmPasswordText.getText().toString().equals(passwordText.getText().toString())) {
            confirmPasswordView.setError("The password does not match the original password");
            focusView = confirmPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt registration and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Add user's info as properties of the JsonObject
            JsonObject jObj = new JsonObject();
            jObj.addProperty("first_name", fName);
            jObj.addProperty("last_name", lName);
            jObj.addProperty("password1", password);
            jObj.addProperty("password2", confirmPassword);
            jObj.addProperty("email", email);

            // Call API and try to authenticate
            // USE SIGNUP API!!!!
            mSelf = this;
            Call<JsonElement> call = authenticationService.signup(RequestBody.create
                    (MediaType.parse("application/json"), jObj.toString()));
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    if(response.isSuccessful()) {
                        Log.d("Response", "SIGNUP SUCCESSFUL!!");

                        JsonObject jObj = response.body().getAsJsonObject();
                        APIClient.setToken(jObj.get("token").getAsString());

                        Log.d("tokenstring", jObj.get("token").getAsString());

                        Intent homepageIntent = new Intent(mSelf.getApplicationContext(), HomepageActivity.class);
                        startActivity(homepageIntent);
                    } else {
                        //Log.d("Response", "Didn't work. SORRY!");
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    call.cancel();
                }
            });
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 8;
    }
}
