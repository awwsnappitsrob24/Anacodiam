package edu.csulb.rob.anacodiam.Activities;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import edu.csulb.rob.anacodiam.R;

public class ProfileActivity extends AppCompatActivity {

    private static int LOAD_IMAGE_RESULT = 1;
    de.hdodenhof.circleimageview.CircleImageView profilePic;

    // Save the image after closing the app
    private SharedPreferences sp;
    private SharedPreferences.Editor preferenceEditor;
    private static final int PREFERENCE_MODE_PRIVATE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageView mountainBackground = new ImageView(this);
        mountainBackground.setImageResource(R.drawable.profile_background);


        profilePic = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.profile_image);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("imagestuff", "HI!");

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), LOAD_IMAGE_RESULT);
            }
        });

        //TextView profName = (TextView) findViewB        //TextView profActivityLevel = (TextView) findViewById(R.id.activityLevelText);yId(R.id.fullNameText);
        //TextView profWeight = (TextView) findViewById(R.id.weightText);
        //TextView profHeight = (TextView) findViewById(R.id.heightText);
        //TextView profGenderAndAge = (TextView) findViewById(R.id.genderAndAgeText);

        //profName.setText("Name:");
        //profWeight.setText("Weight:");
        //profHeight.setText("Height:");
        //profGenderAndAge.setText("Gender and Age:");
        //profActivityLevel.setText("Activity Level:");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == LOAD_IMAGE_RESULT) {

                Uri selectedImageURI = data.getData();

                Glide.with(this).load(selectedImageURI).centerCrop().fitCenter()
                        .into(profilePic);
            }
        }


    }

    @Override
    public void onBackPressed() {
        Intent homePageIntent = new Intent(this, HomepageActivity.class);
        startActivity(homePageIntent);
    }

}
