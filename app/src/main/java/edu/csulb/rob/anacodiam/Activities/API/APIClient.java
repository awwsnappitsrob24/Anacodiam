package edu.csulb.rob.anacodiam.Activities.API;

import android.util.Log;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *  Class where all the settings for the API are done.
 */
public class APIClient {

    private static Retrofit retrofit = null;
    private static String mToken = "";

    static public Retrofit getClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient.Builder().addInterceptor(interceptor);

        builder.addInterceptor(chain -> {
            Request original = chain.request();

            Request request = original.newBuilder()
                    //.header("Authorization", "JWT" + mToken)
                    .header("Authorization", mToken)
                    .method(original.method(), original.body())
                    .build();


            return chain.proceed(request);
        });

        OkHttpClient client = builder.build();

        retrofit = new Retrofit.Builder()
//                .baseUrl("http://10.39.103.87:8000/api/")
                .baseUrl("http://192.168.0.126:8000/api/")
//                .baseUrl("http://192.168.99.100:8000/api/") //from postman
//                .baseUrl("http://10.0.2.2:8000/api/") // for emulator
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }

    static public void setToken(String token) {
        mToken = token;
    }

}

