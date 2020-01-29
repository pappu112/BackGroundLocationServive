package com.example.backgroundlocationservice;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Api {
    String BASE_URL = "http://parkingkoi.xyz/car-parking";

    @FormUrlEncoded
    @POST("add-trackerdata.php")
    Call<String> addLocation(@Field("lat") String lat, @Field("lng") String lng, @Field("device_id") String id,@Field("mobile")String mobile,@Field("pass") String pass);

    @FormUrlEncoded
    @POST("signIn.php")
    Call<ArrayList<Model>> signIn(@Field("name") String name, @Field("pass") String pass);

}
