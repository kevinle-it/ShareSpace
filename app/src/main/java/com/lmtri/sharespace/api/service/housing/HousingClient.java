package com.lmtri.sharespace.api.service.housing;

import com.lmtri.sharespace.api.model.Housing;
import com.lmtri.sharespace.api.model.Note;
import com.lmtri.sharespace.api.model.SearchHousingData;
import com.lmtri.sharespace.api.service.RetrofitClient;
import com.lmtri.sharespace.api.service.housing.get.IGetMoreNewerHousingsCallback;
import com.lmtri.sharespace.api.service.housing.get.IGetMoreOlderHousingsCallback;
import com.lmtri.sharespace.api.service.housing.note.INoteDeletingCallback;
import com.lmtri.sharespace.api.service.housing.note.INoteGettingCallback;
import com.lmtri.sharespace.api.service.housing.note.INotePostingCallback;
import com.lmtri.sharespace.api.service.housing.note.INoteUpdatingCallback;
import com.lmtri.sharespace.api.service.housing.photo.IPhotoDeletingCallback;
import com.lmtri.sharespace.api.service.housing.photo.IPhotoGettingCallback;
import com.lmtri.sharespace.api.service.housing.photo.IPhotoPostingCallback;
import com.lmtri.sharespace.api.service.housing.photo.IPhotoUpdatingCallback;
import com.lmtri.sharespace.api.service.housing.post.IHousingDeletingCallback;
import com.lmtri.sharespace.api.service.housing.post.IHousingPostingCallback;
import com.lmtri.sharespace.api.service.housing.post.IHousingUpdatingCallback;
import com.lmtri.sharespace.api.service.housing.report.IReportHousingCallback;
import com.lmtri.sharespace.api.service.housing.search.ISearchHousingCallback;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lmtri on 8/1/2017.
 */

public class HousingClient {
    public static final String TAG = HousingClient.class.getSimpleName();
    private static IHousingClient mIHousingClient = RetrofitClient.getClient().create(IHousingClient.class);

    // Housing.
    public static void getMoreOlderHousings(String currentBottomHousingDateTimeCreated,
                                            final IGetMoreOlderHousingsCallback getMoreOlderHousingsCallback) {
        Call<List<Housing>> call = mIHousingClient.getMoreOlderHousings(currentBottomHousingDateTimeCreated);
        Callback<List<Housing>> callback = new Callback<List<Housing>>() {
            @Override
            public void onResponse(Call<List<Housing>> call, Response<List<Housing>> response) {
                getMoreOlderHousingsCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<List<Housing>> call, Throwable t) {
                getMoreOlderHousingsCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void getMoreNewerHousings(String currentTopHousingDateTimeCreated,
                                            final IGetMoreNewerHousingsCallback getMoreNewerHousingsCallback) {
        Call<List<Housing>> call = mIHousingClient.getMoreNewerHousings(currentTopHousingDateTimeCreated);
        Callback<List<Housing>> callback = new Callback<List<Housing>>() {
            @Override
            public void onResponse(Call<List<Housing>> call, Response<List<Housing>> response) {
                getMoreNewerHousingsCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<List<Housing>> call, Throwable t) {
                getMoreNewerHousingsCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void searchHousing(SearchHousingData searchHousingData,
                                     final ISearchHousingCallback searchHousingCallback) {
        Call<List<Housing>> call = mIHousingClient.searchHousing(searchHousingData);
        Callback<List<Housing>> callback = new Callback<List<Housing>>() {
            @Override
            public void onResponse(Call<List<Housing>> call, Response<List<Housing>> response) {
                searchHousingCallback.onSearchComplete(response.body());
            }

            @Override
            public void onFailure(Call<List<Housing>> call, Throwable t) {
                searchHousingCallback.onSearchFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void postHousing(Housing housing,
                                   final IHousingPostingCallback housingPostingCallback) {
        Call<Housing> call = mIHousingClient.postHousing(housing);
        Callback<Housing> callback = new Callback<Housing>() {
            @Override
            public void onResponse(Call<Housing> call, Response<Housing> response) {
                housingPostingCallback.onPostComplete(response.body());
            }

            @Override
            public void onFailure(Call<Housing> call, Throwable t) {
                housingPostingCallback.onPostFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void updateHousing(Housing housing,
                                     final IHousingUpdatingCallback housingUpdatingCallback) {
        Call<Boolean> call = mIHousingClient.updateHousing(housing);
        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                housingUpdatingCallback.onUpdateComplete(response.body());
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                housingUpdatingCallback.onUpdateFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void deleteHousing(int housingID,
                                     final IHousingDeletingCallback housingDeletingCallback) {
        Call<Boolean> call = mIHousingClient.deleteHousing(housingID);
        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                housingDeletingCallback.onDeleteComplete(response.body());
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                housingDeletingCallback.onDeleteFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    // Report Housing.
    public static void reportHousing(int housingID,
                                     final IReportHousingCallback reportHousingCallback) {
        Call<Boolean> call = mIHousingClient.reportHousing(housingID);
        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                reportHousingCallback.onReportComplete(response.body());
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                reportHousingCallback.onReportFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    // Note.
    public static void getCurrentUserNote(int housingID,
                                          final INoteGettingCallback noteGettingCallback) {
        Call<Note> call = mIHousingClient.getCurrentUserNote(housingID);
        Callback<Note> callback = new Callback<Note>() {
            @Override
            public void onResponse(Call<Note> call, Response<Note> response) {
                noteGettingCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<Note> call, Throwable t) {
                noteGettingCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void postNote(Note note, int shareHousingID,
                                final INotePostingCallback notePostingCallback) {
        Call<Note> call = mIHousingClient.postNote(note, shareHousingID);
        Callback<Note> callback = new Callback<Note>() {
            @Override
            public void onResponse(Call<Note> call, Response<Note> response) {
                notePostingCallback.onPostComplete(response.body());
            }

            @Override
            public void onFailure(Call<Note> call, Throwable t) {
                notePostingCallback.onPostFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void updateNote(Note note,
                                  final INoteUpdatingCallback noteUpdatingCallback) {
        Call<Note> call = mIHousingClient.updateNote(note);
        Callback<Note> callback = new Callback<Note>() {
            @Override
            public void onResponse(Call<Note> call, Response<Note> response) {
                noteUpdatingCallback.onUpdateComplete(response.body());
            }

            @Override
            public void onFailure(Call<Note> call, Throwable t) {
                noteUpdatingCallback.onUpdateFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void deleteNote(int housingID,
                                  final INoteDeletingCallback noteDeletingCallback) {
        Call<Boolean> call = mIHousingClient.deleteNote(housingID);
        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                noteDeletingCallback.onDeleteComplete(response.body());
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                noteDeletingCallback.onDeleteFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    // User Photos.
    public static void getCurrentUserPhotoURLs(int housingID,
                                               final IPhotoGettingCallback photoGettingCallback) {
        Call<ArrayList<String>> call = mIHousingClient.getCurrentUserPhotoURLs(housingID);
        Callback<ArrayList<String>> callback = new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                photoGettingCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                photoGettingCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void postPhotoURL(int housingID, String photoURL, int shareHousingID,
                                    final IPhotoPostingCallback photoPostingCallback) {
        Call<Boolean> call = mIHousingClient.postPhotoURL(housingID, photoURL, shareHousingID);
        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                photoPostingCallback.onPostComplete(response.body());
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                photoPostingCallback.onPostFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void updatePhotoURL(int housingID, String photoURL, int index,
                                       final IPhotoUpdatingCallback photoUpdatingCallback) {
        Call<Boolean> call = mIHousingClient.updatePhotoURL(housingID, photoURL, index);
        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                photoUpdatingCallback.onUpdateComplete(response.body());
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                photoUpdatingCallback.onUpdateFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void deletePhotoURL(int housingID, int index,
                                      final IPhotoDeletingCallback photoDeletingCallback) {
        Call<Boolean> call = mIHousingClient.deletePhotoURL(housingID, index);
        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                photoDeletingCallback.onDeleteComplete(response.body());
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                photoDeletingCallback.onDeleteFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }
}
