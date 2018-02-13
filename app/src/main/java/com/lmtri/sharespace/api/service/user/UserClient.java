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
import com.lmtri.sharespace.api.service.RetrofitClient;
import com.lmtri.sharespace.api.service.user.appointment.housing.IGetMoreNewerHousingAppointmentsCallback;
import com.lmtri.sharespace.api.service.user.appointment.housing.IGetMoreOlderHousingAppointmentsCallback;
import com.lmtri.sharespace.api.service.user.appointment.housing.IGetNumOfHousingAppointmentsCallback;
import com.lmtri.sharespace.api.service.user.appointment.housing.IHousingAppointmentAcceptingCallback;
import com.lmtri.sharespace.api.service.user.appointment.housing.IHousingAppointmentDeletingCallback;
import com.lmtri.sharespace.api.service.user.appointment.housing.IHousingAppointmentGettingCallback;
import com.lmtri.sharespace.api.service.user.appointment.housing.IHousingAppointmentPostingCallback;
import com.lmtri.sharespace.api.service.user.appointment.housing.IHousingAppointmentUpdatingCallback;
import com.lmtri.sharespace.api.service.user.appointment.sharehousing.IGetMoreNewerShareHousingAppointmentsCallback;
import com.lmtri.sharespace.api.service.user.appointment.sharehousing.IGetMoreOlderShareHousingAppointmentsCallback;
import com.lmtri.sharespace.api.service.user.appointment.sharehousing.IGetNumOfShareHousingAppointmentsCallback;
import com.lmtri.sharespace.api.service.user.appointment.sharehousing.IShareHousingAppointmentAcceptingCallback;
import com.lmtri.sharespace.api.service.user.appointment.sharehousing.IShareHousingAppointmentDeletingCallback;
import com.lmtri.sharespace.api.service.user.appointment.sharehousing.IShareHousingAppointmentGettingCallback;
import com.lmtri.sharespace.api.service.user.appointment.sharehousing.IShareHousingAppointmentPostingCallback;
import com.lmtri.sharespace.api.service.user.appointment.sharehousing.IShareHousingAppointmentUpdatingCallback;
import com.lmtri.sharespace.api.service.user.note.housing.IGetMoreOlderHousingsWithNotesCallback;
import com.lmtri.sharespace.api.service.user.note.housing.IGetNumOfHousingsWithNotesCallback;
import com.lmtri.sharespace.api.service.user.note.sharehousing.IGetMoreOlderShareHousingsWithNotesCallback;
import com.lmtri.sharespace.api.service.user.note.sharehousing.IGetNumOfShareHousingsWithNotesCallback;
import com.lmtri.sharespace.api.service.user.photo.housing.IGetMoreOlderHousingsWithPhotosCallback;
import com.lmtri.sharespace.api.service.user.photo.housing.IGetNumOfHousingsWithPhotosCallback;
import com.lmtri.sharespace.api.service.user.photo.sharehousing.IGetMoreOlderShareHousingsWithPhotosCallback;
import com.lmtri.sharespace.api.service.user.photo.sharehousing.IGetNumOfShareHousingsWithPhotosCallback;
import com.lmtri.sharespace.api.service.user.post.housing.IGetMoreOlderPostedHousingsCallback;
import com.lmtri.sharespace.api.service.user.post.housing.IGetNumOfPostedHousingsCallback;
import com.lmtri.sharespace.api.service.user.post.housing.availability.IGetHidingStateOfCurrentHousingCallback;
import com.lmtri.sharespace.api.service.user.post.housing.availability.IHideHousingCallback;
import com.lmtri.sharespace.api.service.user.post.housing.availability.IUnhideHousingCallback;
import com.lmtri.sharespace.api.service.user.post.sharehousing.IGetMoreOlderPostedShareHousingsCallback;
import com.lmtri.sharespace.api.service.user.post.sharehousing.IGetNumOfPostedShareHousingsCallback;
import com.lmtri.sharespace.api.service.user.post.sharehousing.availability.IGetHidingStateOfCurrentShareHousingCallback;
import com.lmtri.sharespace.api.service.user.post.sharehousing.availability.IHideShareHousingCallback;
import com.lmtri.sharespace.api.service.user.post.sharehousing.availability.IUnhideShareHousingCallback;
import com.lmtri.sharespace.api.service.user.save.housing.IGetMoreNewerSavedHousingsCallback;
import com.lmtri.sharespace.api.service.user.save.housing.IGetMoreOlderSavedHousingsCallback;
import com.lmtri.sharespace.api.service.user.save.housing.IGetNumOfSavedHousingsCallback;
import com.lmtri.sharespace.api.service.user.save.housing.IGetSavingStateOfCurrentHousingCallback;
import com.lmtri.sharespace.api.service.user.save.housing.ISaveHousingCallback;
import com.lmtri.sharespace.api.service.user.save.housing.IUnsaveHousingCallback;
import com.lmtri.sharespace.api.service.user.save.sharehousing.IGetMoreNewerSavedShareHousingsCallback;
import com.lmtri.sharespace.api.service.user.save.sharehousing.IGetMoreOlderSavedShareHousingsCallback;
import com.lmtri.sharespace.api.service.user.save.sharehousing.IGetNumOfSavedShareHousingsCallback;
import com.lmtri.sharespace.api.service.user.save.sharehousing.IGetSavingStateOfCurrentShareHousingCallback;
import com.lmtri.sharespace.api.service.user.save.sharehousing.ISaveShareHousingCallback;
import com.lmtri.sharespace.api.service.user.save.sharehousing.IUnsaveShareHousingCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lmtri on 8/15/2017.
 */

public class UserClient {
    public static final String TAG = UserClient.class.getSimpleName();
    private static IUserClient mIUserClient = RetrofitClient.getClient().create(IUserClient.class);

    public static void register(User user, final IRegisterCallback registerCallback) {
        Call<User> call = mIUserClient.register(user);
        Callback<User> callback = new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                registerCallback.onRegisterSuccess(response.body());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                registerCallback.onRegisterFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void getUserInfo(String currentDeviceToken, final IGetUserInfoCallback getUserInfoCallback) {
        Call<User> call = mIUserClient.getUserInfo(currentDeviceToken);
        Callback<User> callback = new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                getUserInfoCallback.onGetUserInfoSuccess(response.body());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                getUserInfoCallback.onGetUserInfoFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void getOtherUserInfo(int userID, final IGetOtherUserInfoCallback getOtherUserInfoCallback) {
        Call<User> call = mIUserClient.getOtherUserInfo(userID);
        Callback<User> callback = new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                getOtherUserInfoCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                getOtherUserInfoCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    // Save or Unsave Housing.
    public static void getNumOfSavedHousings(final IGetNumOfSavedHousingsCallback getNumOfSavedHousingsCallback) {
        Call<Integer> call = mIUserClient.getNumOfSavedHousings(0);
        Callback<Integer> callback = new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                getNumOfSavedHousingsCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                getNumOfSavedHousingsCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void getMoreOlderSavedHousings(String currentBottomSavedHousingDateTimeCreated,
                                                 final IGetMoreOlderSavedHousingsCallback getMoreOlderSavedHousingsCallback) {
        Call<List<SavedHousing>> call = mIUserClient.getMoreOlderSavedHousings(
                currentBottomSavedHousingDateTimeCreated
        );
        Callback<List<SavedHousing>> callback = new Callback<List<SavedHousing>>() {
            @Override
            public void onResponse(Call<List<SavedHousing>> call, Response<List<SavedHousing>> response) {
                getMoreOlderSavedHousingsCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<List<SavedHousing>> call, Throwable t) {
                getMoreOlderSavedHousingsCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void getMoreNewerSavedHousings(String currentTopSavedHousingDateTimeCreated,
                                                 final IGetMoreNewerSavedHousingsCallback getMoreNewerSavedHousingsCallback) {
        Call<List<SavedHousing>> call = mIUserClient.getMoreNewerSavedHousings(
                currentTopSavedHousingDateTimeCreated
        );
        Callback<List<SavedHousing>> callback = new Callback<List<SavedHousing>>() {
            @Override
            public void onResponse(Call<List<SavedHousing>> call, Response<List<SavedHousing>> response) {
                getMoreNewerSavedHousingsCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<List<SavedHousing>> call, Throwable t) {
                getMoreNewerSavedHousingsCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void getSavingStateOfCurrentHousing(int housingID,
                                                      final IGetSavingStateOfCurrentHousingCallback getSavingStateOfCurrentHousingCallback) {
        Call<Boolean> call = mIUserClient.getSavingStateOfCurrentHousing(housingID);
        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                getSavingStateOfCurrentHousingCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                getSavingStateOfCurrentHousingCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void saveHousing(int housingID,
                                   final ISaveHousingCallback saveHousingCallback) {
        Call<SavedHousing> call = mIUserClient.saveHousing(housingID);
        Callback<SavedHousing> callback = new Callback<SavedHousing>() {
            @Override
            public void onResponse(Call<SavedHousing> call, Response<SavedHousing> response) {
                saveHousingCallback.onSaveComplete(response.body());
            }

            @Override
            public void onFailure(Call<SavedHousing> call, Throwable t) {
                saveHousingCallback.onSaveFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void unsaveHousing(int housingID,
                                     final IUnsaveHousingCallback unsaveHousingCallback) {
        Call<SavedHousing> call = mIUserClient.unsaveHousing(housingID);
        Callback<SavedHousing> callback = new Callback<SavedHousing>() {
            @Override
            public void onResponse(Call<SavedHousing> call, Response<SavedHousing> response) {
                unsaveHousingCallback.onUnsaveComplete(response.body());
            }

            @Override
            public void onFailure(Call<SavedHousing> call, Throwable t) {
                unsaveHousingCallback.onUnsaveFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    // Save or Unsave Share Housing.
    public static void getNumOfSavedShareHousings(final IGetNumOfSavedShareHousingsCallback getNumOfSavedShareHousingsCallback) {
        Call<Integer> call = mIUserClient.getNumOfSavedShareHousings(0);
        Callback<Integer> callback = new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                getNumOfSavedShareHousingsCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                getNumOfSavedShareHousingsCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void getMoreOlderSavedShareHousings(String currentBottomSavedShareHousingDateTimeCreated,
                                                      final IGetMoreOlderSavedShareHousingsCallback getMoreOlderSavedShareHousingsCallback) {
        Call<List<SavedShareHousing>> call = mIUserClient.getMoreOlderSavedShareHousings(
                currentBottomSavedShareHousingDateTimeCreated
        );
        Callback<List<SavedShareHousing>> callback = new Callback<List<SavedShareHousing>>() {
            @Override
            public void onResponse(Call<List<SavedShareHousing>> call, Response<List<SavedShareHousing>> response) {
                getMoreOlderSavedShareHousingsCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<List<SavedShareHousing>> call, Throwable t) {
                getMoreOlderSavedShareHousingsCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void getMoreNewerSavedShareHousings(String currentTopSavedShareHousingDateTimeCreated,
                                                      final IGetMoreNewerSavedShareHousingsCallback getMoreNewerSavedShareHousingsCallback) {
        Call<List<SavedShareHousing>> call = mIUserClient.getMoreNewerSavedShareHousings(
                currentTopSavedShareHousingDateTimeCreated
        );
        Callback<List<SavedShareHousing>> callback = new Callback<List<SavedShareHousing>>() {
            @Override
            public void onResponse(Call<List<SavedShareHousing>> call, Response<List<SavedShareHousing>> response) {
                getMoreNewerSavedShareHousingsCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<List<SavedShareHousing>> call, Throwable t) {
                getMoreNewerSavedShareHousingsCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void getSavingStateOfCurrentShareHousing(int shareHousingID,
                                                           final IGetSavingStateOfCurrentShareHousingCallback getSavingStateOfCurrentShareHousingCallback) {
        Call<Boolean> call = mIUserClient.getSavingStateOfCurrentShareHousing(shareHousingID);
        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                getSavingStateOfCurrentShareHousingCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                getSavingStateOfCurrentShareHousingCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void saveShareHousing(int shareHousingID,
                                        final ISaveShareHousingCallback saveShareHousingCallback) {
        Call<SavedShareHousing> call = mIUserClient.saveShareHousing(shareHousingID);
        Callback<SavedShareHousing> callback = new Callback<SavedShareHousing>() {
            @Override
            public void onResponse(Call<SavedShareHousing> call, Response<SavedShareHousing> response) {
                saveShareHousingCallback.onSaveComplete(response.body());
            }

            @Override
            public void onFailure(Call<SavedShareHousing> call, Throwable t) {
                saveShareHousingCallback.onSaveFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void unsaveShareHousing(int shareHousingID,
                                          final IUnsaveShareHousingCallback unsaveShareHousingCallback) {
        Call<SavedShareHousing> call = mIUserClient.unsaveShareHousing(shareHousingID);
        Callback<SavedShareHousing> callback = new Callback<SavedShareHousing>() {
            @Override
            public void onResponse(Call<SavedShareHousing> call, Response<SavedShareHousing> response) {
                unsaveShareHousingCallback.onUnsaveComplete(response.body());
            }

            @Override
            public void onFailure(Call<SavedShareHousing> call, Throwable t) {
                unsaveShareHousingCallback.onUnsaveFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    // Housing Appointment.
    public static void getNumOfHousingAppointments(final IGetNumOfHousingAppointmentsCallback getNumOfHousingAppointmentsCallback) {
        Call<Integer> call = mIUserClient.getNumOfHousingAppointments(0);
        Callback<Integer> callback = new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                getNumOfHousingAppointmentsCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                getNumOfHousingAppointmentsCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void getMoreOlderHousingAppointments(String currentBottomHousingAppointmentDateTimeCreated,
                                                       final IGetMoreOlderHousingAppointmentsCallback getMoreOlderHousingAppointmentsCallback) {
        Call<List<HousingAppointment>> call = mIUserClient.getMoreOlderHousingAppointments(
                currentBottomHousingAppointmentDateTimeCreated
        );
        Callback<List<HousingAppointment>> callback = new Callback<List<HousingAppointment>>() {
            @Override
            public void onResponse(Call<List<HousingAppointment>> call, Response<List<HousingAppointment>> response) {
                getMoreOlderHousingAppointmentsCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<List<HousingAppointment>> call, Throwable t) {
                getMoreOlderHousingAppointmentsCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void getMoreNewerHousingAppointments(String currentTopHousingAppointmentDateTimeCreated,
                                                       final IGetMoreNewerHousingAppointmentsCallback getMoreNewerHousingAppointmentsCallback) {
        Call<List<HousingAppointment>> call = mIUserClient.getMoreNewerHousingAppointments(
                currentTopHousingAppointmentDateTimeCreated
        );
        Callback<List<HousingAppointment>> callback = new Callback<List<HousingAppointment>>() {
            @Override
            public void onResponse(Call<List<HousingAppointment>> call, Response<List<HousingAppointment>> response) {
                getMoreNewerHousingAppointmentsCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<List<HousingAppointment>> call, Throwable t) {
                getMoreNewerHousingAppointmentsCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void getCurrentHousingAppointment(int housingID,
                                                    final IHousingAppointmentGettingCallback housingAppointmentGettingCallback) {
        Call<HousingAppointment> call = mIUserClient.getCurrentHousingAppointment(housingID);
        Callback<HousingAppointment> callback = new Callback<HousingAppointment>() {
            @Override
            public void onResponse(Call<HousingAppointment> call, Response<HousingAppointment> response) {
                housingAppointmentGettingCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<HousingAppointment> call, Throwable t) {
                housingAppointmentGettingCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void getSpecificHousingAppointment(int housingID, int userID, int ownerID,
                                                     final IHousingAppointmentGettingCallback housingAppointmentGettingCallback) {
        Call<HousingAppointment> call = mIUserClient.getSpecificHousingAppointment(housingID, userID, ownerID);
        Callback<HousingAppointment> callback = new Callback<HousingAppointment>() {
            @Override
            public void onResponse(Call<HousingAppointment> call, Response<HousingAppointment> response) {
                housingAppointmentGettingCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<HousingAppointment> call, Throwable t) {
                housingAppointmentGettingCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void setNewHousingAppointment(int housingID, int recipientID,
                                                String appointmentDateTime, String content,
                                                final IHousingAppointmentPostingCallback housingAppointmentPostingCallback) {
        Call<HousingAppointment> call = mIUserClient.setNewHousingAppointment(
                housingID, recipientID, appointmentDateTime, content
        );
        Callback<HousingAppointment> callback = new Callback<HousingAppointment>() {
            @Override
            public void onResponse(Call<HousingAppointment> call, Response<HousingAppointment> response) {
                housingAppointmentPostingCallback.onPostComplete(response.body());
            }

            @Override
            public void onFailure(Call<HousingAppointment> call, Throwable t) {
                housingAppointmentPostingCallback.onPostFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void updateHousingAppointment(int housingID, int recipientID,
                                                String appointmentDateTime, String content,
                                                final IHousingAppointmentUpdatingCallback housingAppointmentUpdatingCallback) {
        Call<HousingAppointment> call = mIUserClient.updateHousingAppointment(
                housingID, recipientID, appointmentDateTime, content
        );
        Callback<HousingAppointment> callback = new Callback<HousingAppointment>() {
            @Override
            public void onResponse(Call<HousingAppointment> call, Response<HousingAppointment> response) {
                housingAppointmentUpdatingCallback.onUpdateComplete(response.body());
            }

            @Override
            public void onFailure(Call<HousingAppointment> call, Throwable t) {
                housingAppointmentUpdatingCallback.onUpdateFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void acceptHousingAppointment(int housingID, int recipientID,
                                                final IHousingAppointmentAcceptingCallback housingAppointmentDeletingCallback) {
        Call<HousingAppointment> call = mIUserClient.acceptHousingAppointment(
                housingID, recipientID
        );
        Callback<HousingAppointment> callback = new Callback<HousingAppointment>() {
            @Override
            public void onResponse(Call<HousingAppointment> call, Response<HousingAppointment> response) {
                housingAppointmentDeletingCallback.onAcceptComplete(response.body());
            }

            @Override
            public void onFailure(Call<HousingAppointment> call, Throwable t) {
                housingAppointmentDeletingCallback.onAcceptFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void deleteHousingAppointment(int housingID, int recipientID,
                                                final IHousingAppointmentDeletingCallback housingAppointmentDeletingCallback) {
        Call<HousingAppointment> call = mIUserClient.deleteHousingAppointment(housingID, recipientID);
        Callback<HousingAppointment> callback = new Callback<HousingAppointment>() {
            @Override
            public void onResponse(Call<HousingAppointment> call, Response<HousingAppointment> response) {
                housingAppointmentDeletingCallback.onDeleteComplete(response.body());
            }

            @Override
            public void onFailure(Call<HousingAppointment> call, Throwable t) {
                housingAppointmentDeletingCallback.onDeleteFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    // Share Housing Appointment.
    public static void getNumOfShareHousingAppointments(final IGetNumOfShareHousingAppointmentsCallback getNumOfShareHousingAppointmentsCallback) {
        Call<Integer> call = mIUserClient.getNumOfShareHousingAppointments(0);
        Callback<Integer> callback = new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                getNumOfShareHousingAppointmentsCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                getNumOfShareHousingAppointmentsCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void getMoreOlderShareHousingAppointments(String currentBottomShareHousingAppointmentDateTimeCreated,
                                                            final IGetMoreOlderShareHousingAppointmentsCallback getMoreOlderShareHousingAppointmentCallback) {
        Call<List<ShareHousingAppointment>> call = mIUserClient.getMoreOlderShareHousingAppointments(
                currentBottomShareHousingAppointmentDateTimeCreated
        );
        Callback<List<ShareHousingAppointment>> callback = new Callback<List<ShareHousingAppointment>>() {
            @Override
            public void onResponse(Call<List<ShareHousingAppointment>> call, Response<List<ShareHousingAppointment>> response) {
                getMoreOlderShareHousingAppointmentCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<List<ShareHousingAppointment>> call, Throwable t) {
                getMoreOlderShareHousingAppointmentCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void getMoreNewerShareHousingAppointments(String currentTopShareHousingAppointmentDateTimeCreated,
                                                            final IGetMoreNewerShareHousingAppointmentsCallback getMoreNewerShareHousingAppointmentCallback) {
        Call<List<ShareHousingAppointment>> call = mIUserClient.getMoreNewerShareHousingAppointments(
                currentTopShareHousingAppointmentDateTimeCreated
        );
        Callback<List<ShareHousingAppointment>> callback = new Callback<List<ShareHousingAppointment>>() {
            @Override
            public void onResponse(Call<List<ShareHousingAppointment>> call, Response<List<ShareHousingAppointment>> response) {
                getMoreNewerShareHousingAppointmentCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<List<ShareHousingAppointment>> call, Throwable t) {
                getMoreNewerShareHousingAppointmentCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void getCurrentShareHousingAppointment(int housingID,
                                                         final IShareHousingAppointmentGettingCallback shareHousingAppointmentGettingCallback) {
        Call<ShareHousingAppointment> call = mIUserClient.getCurrentShareHousingAppointment(housingID);
        Callback<ShareHousingAppointment> callback = new Callback<ShareHousingAppointment>() {
            @Override
            public void onResponse(Call<ShareHousingAppointment> call, Response<ShareHousingAppointment> response) {
                shareHousingAppointmentGettingCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<ShareHousingAppointment> call, Throwable t) {
                shareHousingAppointmentGettingCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void getSpecificShareHousingAppointment(int housingID, int userID, int creatorID,
                                                          final IShareHousingAppointmentGettingCallback shareHousingAppointmentGettingCallback) {
        Call<ShareHousingAppointment> call = mIUserClient.getSpecificShareHousingAppointment(housingID, userID, creatorID);
        Callback<ShareHousingAppointment> callback = new Callback<ShareHousingAppointment>() {
            @Override
            public void onResponse(Call<ShareHousingAppointment> call, Response<ShareHousingAppointment> response) {
                shareHousingAppointmentGettingCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<ShareHousingAppointment> call, Throwable t) {
                shareHousingAppointmentGettingCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void setNewShareHousingAppointment(int shareHousingID, int recipientID,
                                                    String appointmentDateTime, String content,
                                                    final IShareHousingAppointmentPostingCallback shareHousingAppointmentPostingCallback) {
        Call<ShareHousingAppointment> call = mIUserClient.setNewShareHousingAppointment(
                shareHousingID, recipientID, appointmentDateTime, content
        );
        Callback<ShareHousingAppointment> callback = new Callback<ShareHousingAppointment>() {
            @Override
            public void onResponse(Call<ShareHousingAppointment> call, Response<ShareHousingAppointment> response) {
                shareHousingAppointmentPostingCallback.onPostComplete(response.body());
            }

            @Override
            public void onFailure(Call<ShareHousingAppointment> call, Throwable t) {
                shareHousingAppointmentPostingCallback.onPostFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void updateShareHousingAppointment(int shareHousingID, int recipientID,
                                                     String appointmentDateTime, String content,
                                                     final IShareHousingAppointmentUpdatingCallback shareHousingAppointmentUpdatingCallback) {
        Call<ShareHousingAppointment> call = mIUserClient.updateShareHousingAppointment(
                shareHousingID, recipientID, appointmentDateTime, content
        );
        Callback<ShareHousingAppointment> callback = new Callback<ShareHousingAppointment>() {
            @Override
            public void onResponse(Call<ShareHousingAppointment> call, Response<ShareHousingAppointment> response) {
                shareHousingAppointmentUpdatingCallback.onUpdateComplete(response.body());
            }

            @Override
            public void onFailure(Call<ShareHousingAppointment> call, Throwable t) {
                shareHousingAppointmentUpdatingCallback.onUpdateFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void acceptShareHousingAppointment(int shareHousingID, int recipientID,
                                                     final IShareHousingAppointmentAcceptingCallback shareHousingAppointmentDeletingCallback) {
        Call<ShareHousingAppointment> call = mIUserClient.acceptShareHousingAppointment(
                shareHousingID, recipientID
        );
        Callback<ShareHousingAppointment> callback = new Callback<ShareHousingAppointment>() {
            @Override
            public void onResponse(Call<ShareHousingAppointment> call, Response<ShareHousingAppointment> response) {
                shareHousingAppointmentDeletingCallback.onAcceptComplete(response.body());
            }

            @Override
            public void onFailure(Call<ShareHousingAppointment> call, Throwable t) {
                shareHousingAppointmentDeletingCallback.onAcceptFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void deleteShareHousingAppointment(int shareHousingID, int recipientID,
                                                     final IShareHousingAppointmentDeletingCallback shareHousingAppointmentDeletingCallback) {
        Call<ShareHousingAppointment> call = mIUserClient.deleteShareHousingAppointment(shareHousingID, recipientID);
        Callback<ShareHousingAppointment> callback = new Callback<ShareHousingAppointment>() {
            @Override
            public void onResponse(Call<ShareHousingAppointment> call, Response<ShareHousingAppointment> response) {
                shareHousingAppointmentDeletingCallback.onDeleteComplete(response.body());
            }

            @Override
            public void onFailure(Call<ShareHousingAppointment> call, Throwable t) {
                shareHousingAppointmentDeletingCallback.onDeleteFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    // Posted Housing.
    public static void getNumOfPostedHousings(final IGetNumOfPostedHousingsCallback getNumOfPostedHousingsCallback) {
        Call<Integer> call = mIUserClient.getNumOfPostedHousings(0);
        Callback<Integer> callback = new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                getNumOfPostedHousingsCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                getNumOfPostedHousingsCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void getMoreOlderPostedHousings(String currentBottomHousingDateTimeCreated,
                                                  final IGetMoreOlderPostedHousingsCallback getMoreOlderPostedHousingsCallback) {
        Call<List<Housing>> call = mIUserClient.getMoreOlderPostedHousings(currentBottomHousingDateTimeCreated);
        Callback<List<Housing>> callback = new Callback<List<Housing>>() {
            @Override
            public void onResponse(Call<List<Housing>> call, Response<List<Housing>> response) {
                getMoreOlderPostedHousingsCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<List<Housing>> call, Throwable t) {
                getMoreOlderPostedHousingsCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    // Hide or Unhide Housing.
    public static void getHidingStateOfCurrentHousing(int housingID,
                                                      final IGetHidingStateOfCurrentHousingCallback getHidingStateOfCurrentHousingCallback) {
        Call<Boolean> call = mIUserClient.getHidingStateOfCurrentHousing(housingID);
        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                getHidingStateOfCurrentHousingCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                getHidingStateOfCurrentHousingCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void hideHousing(int housingID,
                                   final IHideHousingCallback hideHousingCallback) {
        Call<Boolean> call = mIUserClient.hideHousing(housingID);
        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                hideHousingCallback.onHideComplete(response.body());
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                hideHousingCallback.onHideFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void unhideHousing(int housingID,
                                     final IUnhideHousingCallback unhideHousingCallback) {
        Call<Boolean> call = mIUserClient.unhideHousing(housingID);
        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                unhideHousingCallback.onUnhideComplete(response.body());
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                unhideHousingCallback.onUnhideFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    // Posted Share Housing.
    public static void getNumOfPostedShareHousings(final IGetNumOfPostedShareHousingsCallback getNumOfPostedShareHousingsCallback) {
        Call<Integer> call = mIUserClient.getNumOfPostedShareHousings(0);
        Callback<Integer> callback = new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                getNumOfPostedShareHousingsCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                getNumOfPostedShareHousingsCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void getMoreOlderPostedShareHousings(String currentBottomShareHousingDateTimeCreated,
                                                       final IGetMoreOlderPostedShareHousingsCallback getMoreOlderPostedShareHousingsCallback) {
        Call<List<ShareHousing>> call = mIUserClient.getMoreOlderPostedShareHousings(currentBottomShareHousingDateTimeCreated);
        Callback<List<ShareHousing>> callback = new Callback<List<ShareHousing>>() {
            @Override
            public void onResponse(Call<List<ShareHousing>> call, Response<List<ShareHousing>> response) {
                getMoreOlderPostedShareHousingsCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<List<ShareHousing>> call, Throwable t) {
                getMoreOlderPostedShareHousingsCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    // Hide or Unhide Share Housing.
    public static void getHidingStateOfCurrentShareHousing(int shareHousingID,
                                                           final IGetHidingStateOfCurrentShareHousingCallback getHidingStateOfCurrentShareHousingCallback) {
        Call<Boolean> call = mIUserClient.getHidingStateOfCurrentShareHousing(shareHousingID);
        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                getHidingStateOfCurrentShareHousingCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                getHidingStateOfCurrentShareHousingCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void hideShareHousing(int shareHousingID,
                                        final IHideShareHousingCallback hideShareHousingCallback) {
        Call<Boolean> call = mIUserClient.hideShareHousing(shareHousingID);
        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                hideShareHousingCallback.onHideComplete(response.body());
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                hideShareHousingCallback.onHideFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void unhideShareHousing(int shareHousingID,
                                          final IUnhideShareHousingCallback unhideShareHousingCallback) {
        Call<Boolean> call = mIUserClient.unhideShareHousing(shareHousingID);
        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                unhideShareHousingCallback.onUnhideComplete(response.body());
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                unhideShareHousingCallback.onUnhideFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    // Posted Note.
    public static void getNumOfHousingsWithNotes(final IGetNumOfHousingsWithNotesCallback getNumOfHousingsWithNotesCallback) {
        Call<Integer> call = mIUserClient.getNumOfHousingsWithNotes(0);
        Callback<Integer> callback = new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                getNumOfHousingsWithNotesCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                getNumOfHousingsWithNotesCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void getMoreOlderHousingsWithNotes(String currentBottomHousingNoteDateTimeCreated,
                                                     final IGetMoreOlderHousingsWithNotesCallback getMoreOlderHousingsWithNotesCallback) {
        Call<List<HistoryHousingNote>> call = mIUserClient.getMoreOlderHousingsWithNotes(currentBottomHousingNoteDateTimeCreated);
        Callback<List<HistoryHousingNote>> callback = new Callback<List<HistoryHousingNote>>() {
            @Override
            public void onResponse(Call<List<HistoryHousingNote>> call, Response<List<HistoryHousingNote>> response) {
                getMoreOlderHousingsWithNotesCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<List<HistoryHousingNote>> call, Throwable t) {
                getMoreOlderHousingsWithNotesCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void getNumOfShareHousingsWithNotes(final IGetNumOfShareHousingsWithNotesCallback getNumOfShareHousingsWithNotesCallback) {
        Call<Integer> call = mIUserClient.getNumOfShareHousingsWithNotes(0);
        Callback<Integer> callback = new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                getNumOfShareHousingsWithNotesCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                getNumOfShareHousingsWithNotesCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void getMoreOlderShareHousingsWithNotes(String currentBottomShareHousingNoteDateTimeCreated,
                                                          final IGetMoreOlderShareHousingsWithNotesCallback getMoreOlderShareHousingsWithNotesCallback) {
        Call<List<HistoryShareHousingNote>> call = mIUserClient.getMoreOlderShareHousingsWithNotes(currentBottomShareHousingNoteDateTimeCreated);
        Callback<List<HistoryShareHousingNote>> callback = new Callback<List<HistoryShareHousingNote>>() {
            @Override
            public void onResponse(Call<List<HistoryShareHousingNote>> call, Response<List<HistoryShareHousingNote>> response) {
                getMoreOlderShareHousingsWithNotesCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<List<HistoryShareHousingNote>> call, Throwable t) {
                getMoreOlderShareHousingsWithNotesCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    // Posted Photo.

    public static void getNumOfHousingsWithPhotos(final IGetNumOfHousingsWithPhotosCallback getNumOfHousingsWithPhotosCallback) {
        Call<Integer> call = mIUserClient.getNumOfHousingsWithPhotos(0);
        Callback<Integer> callback = new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                getNumOfHousingsWithPhotosCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                getNumOfHousingsWithPhotosCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void getMoreOlderHousingsWithPhotos(String currentBottomHousingPhotoDateTimeCreated,
                                                      final IGetMoreOlderHousingsWithPhotosCallback getMoreOlderHousingsWithNotesCallback) {
        Call<List<HistoryHousingPhoto>> call = mIUserClient.getMoreOlderHousingsWithPhotos(currentBottomHousingPhotoDateTimeCreated);
        Callback<List<HistoryHousingPhoto>> callback = new Callback<List<HistoryHousingPhoto>>() {
            @Override
            public void onResponse(Call<List<HistoryHousingPhoto>> call, Response<List<HistoryHousingPhoto>> response) {
                getMoreOlderHousingsWithNotesCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<List<HistoryHousingPhoto>> call, Throwable t) {
                getMoreOlderHousingsWithNotesCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void getNumOfShareHousingsWithPhotos(final IGetNumOfShareHousingsWithPhotosCallback getNumOfShareHousingsWithPhotosCallback) {
        Call<Integer> call = mIUserClient.getNumOfShareHousingsWithPhotos(0);
        Callback<Integer> callback = new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                getNumOfShareHousingsWithPhotosCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                getNumOfShareHousingsWithPhotosCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void getMoreOlderShareHousingsWithPhotos(String currentBottomShareHousingPhotoDateTimeCreated,
                                                           final IGetMoreOlderShareHousingsWithPhotosCallback getMoreOlderShareHousingsWithNotesCallback) {
        Call<List<HistoryShareHousingPhoto>> call = mIUserClient.getMoreOlderShareHousingsWithPhotos(currentBottomShareHousingPhotoDateTimeCreated);
        Callback<List<HistoryShareHousingPhoto>> callback = new Callback<List<HistoryShareHousingPhoto>>() {
            @Override
            public void onResponse(Call<List<HistoryShareHousingPhoto>> call, Response<List<HistoryShareHousingPhoto>> response) {
                getMoreOlderShareHousingsWithNotesCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<List<HistoryShareHousingPhoto>> call, Throwable t) {
                getMoreOlderShareHousingsWithNotesCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }
}
