package com.lmtri.sharespace.api.service.housing;

import com.lmtri.sharespace.api.model.Housing;
import com.lmtri.sharespace.api.model.Note;
import com.lmtri.sharespace.api.model.SearchHousingData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by lmtri on 8/1/2017.
 */

public interface IHousingClient {

    // Housing.
    @GET("housing")
    Call<List<Housing>> getMoreOlderHousings(@Query("currentBottomHousingDateTimeCreated") String currentBottomHousingDateTimeCreated);
    @GET("housing")
    Call<List<Housing>> getMoreNewerHousings(@Query("currentTopHousingDateTimeCreated") String currentTopHousingDateTimeCreated);
    @POST("housing")
    Call<Housing> postHousing(@Body Housing housing);
    @PUT("housing")
    Call<Boolean> updateHousing(@Body Housing housing);
    @DELETE("housing")
    Call<Boolean> deleteHousing(@Query("housingID") int housingID);

    // Search.
    @POST("housing/search")
    Call<List<Housing>> searchHousing(@Body SearchHousingData searchHousingData);

    // Report Housing.
    @POST("housing/report")
    Call<Boolean> reportHousing(@Query("housingID") int housingID);

    // Note.
    @GET("housing/note")
    Call<Note> getCurrentUserNote(@Query("housingID") int housingID);
    @POST("housing/note")
    Call<Note> postNote(@Body Note note, @Query("shareHousingID") int shareHousingID);
    @PUT("housing/note")
    Call<Note> updateNote(@Body Note note);
    @DELETE("housing/note")
    Call<Boolean> deleteNote(@Query("housingID") int housingID);

    // Photo.
    @GET("housing/photo")
    Call<ArrayList<String>> getCurrentUserPhotoURLs(@Query("housingID") int housingID);
    @POST("housing/photo")
    Call<Boolean> postPhotoURL(@Query("housingID") int housingID,
                               @Query("photoURL") String photoURL,
                               @Query("shareHousingID") int shareHousingID);
    @PUT("housing/photo")
    Call<Boolean> updatePhotoURL(@Query("housingID") int housingID,
                                 @Query("photoURL") String photoURL,
                                 @Query("index") int index);
    @DELETE("housing/photo")
    Call<Boolean> deletePhotoURL(@Query("housingID") int housingID,
                                 @Query("index") int index);
}
