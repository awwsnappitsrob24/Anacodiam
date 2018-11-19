package edu.csulb.rob.anacodiam.Activities.API;

import com.google.gson.JsonElement;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CalorieService {

    // Search Instant API
    @Headers({
            "x-app-id: 9024fda1",
            "x-app-key: 61c18a66be3c98d50235435cacea38eb"
    })
    @GET("v2/search/instant")
    Call<JsonElement> searchfood(@Query("query") String userQuery);

    // Nutrient Search
    @GET("calories/evaluate_nutrients?query=")
    Call<JsonElement> getfoodnutrients(@Query("query") String userQuery);

}
