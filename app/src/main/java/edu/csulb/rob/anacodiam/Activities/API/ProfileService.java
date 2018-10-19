package edu.csulb.rob.anacodiam.Activities.API;

import com.google.gson.JsonElement;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.Call;
import retrofit2.http.POST;

public interface ProfileService {

    @POST("profiles/")
    Call<JsonElement> createprofile(@Body RequestBody body);

}
