package com.lmtri.sharespace.api.service.user;

import com.lmtri.sharespace.api.model.HistoryHousingNote;
import com.lmtri.sharespace.api.model.HistoryHousingPhoto;
import com.lmtri.sharespace.api.model.HistoryShareHousingNote;
import com.lmtri.sharespace.api.model.HistoryShareHousingPhoto;
import com.lmtri.sharespace.api.model.Housing;
import com.lmtri.sharespace.api.model.HousingAppointment;
import com.lmtri.sharespace.api.model.SavedHousing;
import com.lmtri.sharespace.api.model.SavedShareHousing;
import com.lmtri.sharespace.api.model.ShareHousing;
import com.lmtri.sharespace.api.model.ShareHousingAppointment;
import com.lmtri.sharespace.api.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by lmtri on 8/15/2017.
 */

public interface IUserClient {

    @POST("user")
    Call<User> register(@Body User user);

    @GET("user")
    Call<User> getUserInfo(@Query("currentDeviceToken") String currentDeviceToken);
    @GET("user")
    Call<User> getOtherUserInfo(@Query("userID") int userID);

    // Save or Unsave Housing.
    @GET("user/save/housing")
    Call<Integer> getNumOfSavedHousings(@Query("dummyParam") int dummyParam);
    @GET("user/save/housing")
    Call<List<SavedHousing>> getMoreOlderSavedHousings(@Query("currentBottomSavedHousingDateTimeCreated")
                                                       String currentBottomSavedHousingDateTimeCreated);
    @GET("user/save/housing")
    Call<List<SavedHousing>> getMoreNewerSavedHousings(@Query("currentTopSavedHousingDateTimeCreated")
                                                       String currentTopSavedHousingDateTimeCreated);
    @GET("user/save/housing")
    Call<Boolean> getSavingStateOfCurrentHousing(@Query("housingID") int housingID);
    @POST("user/save/housing")
    Call<SavedHousing> saveHousing(@Query("housingID") int housingID);
    @DELETE("user/save/housing")
    Call<SavedHousing> unsaveHousing(@Query("housingID") int housingID);

    // Save or Unsave Share Housing.
    @GET("user/save/sharehousing")
    Call<Integer> getNumOfSavedShareHousings(@Query("dummyParam") int dummyParam);
    @GET("user/save/sharehousing")
    Call<List<SavedShareHousing>> getMoreOlderSavedShareHousings(@Query("currentBottomSavedShareHousingDateTimeCreated")
                                                                 String currentBottomSavedShareHousingDateTimeCreated);
    @GET("user/save/sharehousing")
    Call<List<SavedShareHousing>> getMoreNewerSavedShareHousings(@Query("currentTopSavedShareHousingDateTimeCreated")
                                                                 String currentTopSavedShareHousingDateTimeCreated);
    @GET("user/save/sharehousing")
    Call<Boolean> getSavingStateOfCurrentShareHousing(@Query("shareHousingID") int shareHousingID);
    @POST("user/save/sharehousing")
    Call<SavedShareHousing> saveShareHousing(@Query("shareHousingID") int shareHousingID);
    @DELETE("user/save/sharehousing")
    Call<SavedShareHousing> unsaveShareHousing(@Query("shareHousingID") int shareHousingID);

    // Housing Appointment.
    @GET("user/appointment/housing")
    Call<Integer> getNumOfHousingAppointments(@Query("dummyParam") int dummyParam);
    @GET("user/appointment/housing")
    Call<List<HousingAppointment>> getMoreOlderHousingAppointments(@Query("currentBottomHousingAppointmentDateTimeCreated")
                                                                        String currentBottomHousingAppointmentDateTimeCreated);
    @GET("user/appointment/housing")
    Call<List<HousingAppointment>> getMoreNewerHousingAppointments(@Query("currentTopHousingAppointmentDateTimeCreated")
                                                                        String currentTopHousingAppointmentDateTimeCreated);
    @GET("user/appointment/housing")
    Call<HousingAppointment> getCurrentHousingAppointment(@Query("housingID") int housingID);
    @GET("user/appointment/housing")
    Call<HousingAppointment> getSpecificHousingAppointment(@Query("housingID") int housingID,
                                                           @Query("userID") int userID,
                                                           @Query("ownerID") int ownerID);
    @POST("user/appointment/housing")
    Call<HousingAppointment> setNewHousingAppointment(@Query("housingID") int housingID,
                                                      @Query("recipientID") int recipientID,
                                                      @Query("appointmentDateTime") String appointmentDateTime,
                                                      @Query("content") String content);
    @PUT("user/appointment/housing")
    Call<HousingAppointment> updateHousingAppointment(@Query("housingID") int housingID,
                                                      @Query("recipientID") int recipientID,
                                                      @Query("appointmentDateTime") String appointmentDateTime,
                                                      @Query("content") String content);
    @PUT("user/appointment/housing")
    Call<HousingAppointment> acceptHousingAppointment(@Query("housingID") int housingID,
                                                      @Query("recipientID") int recipientID);
    @DELETE("user/appointment/housing")
    Call<HousingAppointment> deleteHousingAppointment(@Query("housingID") int housingID,
                                                      @Query("recipientID") int recipientID);

    // Share Housing Appointment.
    @GET("user/appointment/housing")
    Call<Integer> getNumOfShareHousingAppointments(@Query("dummyParam") int dummyParam);
    @GET("user/appointment/sharehousing")
    Call<List<ShareHousingAppointment>> getMoreOlderShareHousingAppointments(@Query("currentBottomShareHousingAppointmentDateTimeCreated")
                                                                                          String currentBottomShareHousingAppointmentDateTimeCreated);
    @GET("user/appointment/sharehousing")
    Call<List<ShareHousingAppointment>> getMoreNewerShareHousingAppointments(@Query("currentTopShareHousingAppointmentDateTimeCreated")
                                                                                          String currentTopShareHousingAppointmentDateTimeCreated);
    @GET("user/appointment/sharehousing")
    Call<ShareHousingAppointment> getCurrentShareHousingAppointment(@Query("shareHousingID") int shareHousingID);
    @GET("user/appointment/sharehousing")
    Call<ShareHousingAppointment> getSpecificShareHousingAppointment(@Query("shareHousingID") int shareHousingID,
                                                                     @Query("userID") int userID,
                                                                     @Query("creatorID") int creatorID);
    @POST("user/appointment/sharehousing")
    Call<ShareHousingAppointment> setNewShareHousingAppointment(@Query("shareHousingID") int shareHousingID,
                                                                @Query("recipientID") int recipientID,
                                                                @Query("appointmentDateTime") String appointmentDateTime,
                                                                @Query("content") String content);
    @PUT("user/appointment/sharehousing")
    Call<ShareHousingAppointment> updateShareHousingAppointment(@Query("shareHousingID") int shareHousingID,
                                                                @Query("recipientID") int recipientID,
                                                                @Query("appointmentDateTime") String appointmentDateTime,
                                                                @Query("content") String content);
    @PUT("user/appointment/sharehousing")
    Call<ShareHousingAppointment> acceptShareHousingAppointment(@Query("shareHousingID") int shareHousingID,
                                                                @Query("recipientID") int recipientID);
    @DELETE("user/appointment/sharehousing")
    Call<ShareHousingAppointment> deleteShareHousingAppointment(@Query("shareHousingID") int shareHousingID,
                                                                @Query("recipientID") int recipientID);

    // Posted Housing.
    @GET("user/post/housing")
    Call<Integer> getNumOfPostedHousings(@Query("dummyParam") int dummyParam);
    @GET("user/post/housing")
    Call<List<Housing>> getMoreOlderPostedHousings(@Query("currentBottomHousingDateTimeCreated") String currentBottomHousingDateTimeCreated);

    // Hide or Unhide Housing.
    @GET("user/post/housing/availability")
    Call<Boolean> getHidingStateOfCurrentHousing(@Query("housingID") int housingID);
    @PUT("user/post/housing/availability")
    Call<Boolean> unhideHousing(@Query("housingID") int housingID);
    @DELETE("user/post/housing/availability")
    Call<Boolean> hideHousing(@Query("housingID") int housingID);

    // Posted Share Housing.
    @GET("user/post/sharehousing")
    Call<Integer> getNumOfPostedShareHousings(@Query("dummyParam") int dummyParam);
    @GET("user/post/sharehousing")
    Call<List<ShareHousing>> getMoreOlderPostedShareHousings(@Query("currentBottomShareHousingDateTimeCreated") String currentBottomShareHousingDateTimeCreated);

    // Hide or Unhide Share Housing.
    @GET("user/post/sharehousing/availability")
    Call<Boolean> getHidingStateOfCurrentShareHousing(@Query("shareHousingID") int shareHousingID);
    @PUT("user/post/sharehousing/availability")
    Call<Boolean> unhideShareHousing(@Query("shareHousingID") int shareHousingID);
    @DELETE("user/post/sharehousing/availability")
    Call<Boolean> hideShareHousing(@Query("shareHousingID") int shareHousingID);

    // Posted Note.
    @GET("user/note/housing")
    Call<Integer> getNumOfHousingsWithNotes(@Query("dummyParam") int dummyParam);
    @GET("user/note/housing")
    Call<List<HistoryHousingNote>> getMoreOlderHousingsWithNotes(@Query("currentBottomHousingNoteDateTimeCreated") String currentBottomHousingNoteDateTimeCreated);
    @GET("user/note/sharehousing")
    Call<Integer> getNumOfShareHousingsWithNotes(@Query("dummyParam") int dummyParam);
    @GET("user/note/sharehousing")
    Call<List<HistoryShareHousingNote>> getMoreOlderShareHousingsWithNotes(@Query("currentBottomShareHousingNoteDateTimeCreated") String currentBottomShareHousingNoteDateTimeCreated);

    // Posted Photo.
    @GET("user/photo/housing")
    Call<Integer> getNumOfHousingsWithPhotos(@Query("dummyParam") int dummyParam);
    @GET("user/photo/housing")
    Call<List<HistoryHousingPhoto>> getMoreOlderHousingsWithPhotos(@Query("currentBottomHousingPhotoDateTimeCreated") String currentBottomHousingPhotoDateTimeCreated);
    @GET("user/photo/sharehousing")
    Call<Integer> getNumOfShareHousingsWithPhotos(@Query("dummyParam") int dummyParam);
    @GET("user/photo/sharehousing")
    Call<List<HistoryShareHousingPhoto>> getMoreOlderShareHousingsWithPhotos(@Query("currentBottomShareHousingPhotoDateTimeCreated") String currentBottomShareHousingPhotoDateTimeCreated);
}
