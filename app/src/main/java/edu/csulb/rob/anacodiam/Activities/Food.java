package edu.csulb.rob.anacodiam.Activities;

import android.widget.ImageView;

public class Food {

    private String foodImageURL;
    private String foodName;

    public Food(String imageURL, String name) {
        foodImageURL = imageURL;
        foodName = name;
    }

    public String getFoodImageURL() {
        return foodImageURL;
    }

    public String getFoodName() {
        return foodName;
    }
}
