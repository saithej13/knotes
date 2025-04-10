package com.vst.knotes.MVVM.Services;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Apiclient {

    @GET("refs/heads/master/Master")
    Call<JsonObject> Login();
    @POST("auth/login")
    Call<JsonObject> login(@Body JsonObject loginData);
}
