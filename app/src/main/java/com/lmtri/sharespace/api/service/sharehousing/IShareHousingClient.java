package com.lmtri.sharespace.api.service.sharehousing;

import com.lmtri.sharespace.api.model.SearchShareHousingData;
import com.lmtri.sharespace.api.model.ShareHousing;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by lmtri on 8/22/2017.
 */

public interface IShareHousingClient {

    // Share Housings.
    @GET("sharehousing")
    Call<List<ShareHousing>> getMoreOlderShareHousings(@Query("currentBottomShareHousingDateTimeCreated") String currentBottomShareHousingDateTimeCreated);
    @GET("sharehousing")
    Call<List<ShareHousing>> getMoreNewerShareHousings(@Query("currentTopShareHousingDateTimeCreated") String currentTopShareHousingDateTimeCreated);
    @POST("sharehousing")
    Call<ShareHousing> postShareHousing(@Body ShareHousing shareHousing);
    @PUT("sharehousing")
    Call<Boolean> updateShareHousing(@Body ShareHousing shareHousing);
    @DELETE("sharehousing")
    Call<Boolean> deleteShareHousing(@Query("shareHousingID") int shareHousingID);

    // Search.
    @POST("sharehousing/search")
    Call<List<ShareHousing>> searchShareHousing(@Body SearchShareHousingData searchShareHousingData);

    @GET("sharehousing")
    Call<Boolean> checkIfExistSharePostsOfCurrentHousing(@Query("housingID") int housingID);
    @GET("sharehousing")
    Call<List<ShareHousing>> getMoreOlderShareOfHousing(@Query("housingID") int housingID, @Query("offset") int offset);

    // Report Share Housing.
    @POST("sharehousing/report")
    Call<Boolean> reportShareHousing(@Query("shareHousingID") int shareHousingID);
}
