package edu.csulb.rob.anacodiam.Activities.API;

import com.google.gson.JsonElement;

import java.text.SimpleDateFormat;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ProfileService {

    @POST("profiles/")
    Call<JsonElement> createprofile(@Body RequestBody body);

    @GET("profiles/")
    Call<JsonElement> getprofile();

    @PUT("profiles/")
    Call<JsonElement> updateprofile(@Body RequestBody body);
}
