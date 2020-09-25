package com.usingstudioo.APIManager;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiInterface {

    //Base page for all the api's
    String kBasePage     = "index.php";

    //User will login through userLogin API.
    String kUserLogin = "login";
    String kAddPlan="addplan";

    String kgetPlan="getplan";

    //User will login through userReg API.
    String kUserReg = "signup";

    String kOtpVerify="verify_otp";

    String kSongList = "getsonglist";

    String kEmailForgot = "get_emilResetpwd";

    String kResetPassword = "resetPwd";

    String kChangePassword = "changePassword";

    String kEditProfile = "editprofile";

    String ksongListEmbed="getsonglistembade";

    /**
     * set api request with api key and corresponding parameters
     * @param APIKey  key of the url
     * @param details details contains request body parameters
     * @param files   if include file will be sent in multipart
     * @return JsonObject ie. response
     */
    @Multipart
    @POST()
    Call<JsonObject> APIRequestWithFile(
            @Url String APIKey,
            @PartMap Map<String, RequestBody> details,
            @Part List<MultipartBody.Part> files
    );


    @POST()
    Call<JsonObject> APIRequestRaw(
            @Url String APIKey,
            @Body RequestBody params
    );

    @GET()
    Call<JsonObject> APIGetRequestRaw(
            @Url String APIKey,
            @Body RequestBody params
    );

    @GET("maps/api/geocode/json")
    Call<JsonObject> getLocation(
            @Query("latlng") String latlng,
            @Query("key") String key
    );

    @GET("maps/api/place/autocomplete/json")
    Call<JsonObject> getPlaces(
            @Query("input") String input,
            @Query("key") String key,
            @Query("components") String value
    );

    @GET("maps/api/place/details/json")
    Call<JsonObject> getPlacesByID(
            @Query("placeid") String placeID,
            @Query("key") String key
    );

    @GET("maps/api/geocode/json")
    Call<JsonObject> getCordinates(
            @Query("address") String address,
            @Query("key") String key
    );

    @GET("publicationlist")
    Call<JsonObject> getPublications();

    @GET("plan_listing")
    Call<JsonObject> getPlanListing();

}


