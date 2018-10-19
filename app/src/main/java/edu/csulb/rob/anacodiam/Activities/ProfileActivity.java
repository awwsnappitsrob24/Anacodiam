package edu.csulb.rob.anacodiam.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import edu.csulb.rob.anacodiam.R;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageView mountainBackground = new ImageView(this);
        mountainBackground.setImageResource(R.drawable.mountainsprofile);

        ImageView profilePicture = new ImageView(this);
        profilePicture.setImageResource(R.mipmap.profile_pic);

        TextView userFullName = (TextView) findViewById(R.id.userFullName);

        //Get the user's name from profile (Use getprofile API)
        //Bundle extras = getIntent().getExtras();
        //String userFirstName = extras.getString("FIRST_NAME");
        //String userLastName = extras.getString("LAST_NAME");
        //userFullName.setText(userFirstName + " " + userLastName);
    }
}
