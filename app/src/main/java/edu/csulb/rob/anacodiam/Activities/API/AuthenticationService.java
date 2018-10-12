package edu.csulb.rob.anacodiam.Activities.API;

import com.google.gson.JsonElement;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.Call;
import retrofit2.http.POST;

public interface AuthenticationService {

    @POST("rest-auth/login/")
    Call<JsonElement> signin(@Body RequestBody body);

    @POST("rest-auth/registration/")
    Call<JsonElement> signup(@Body RequestBody body);

}
