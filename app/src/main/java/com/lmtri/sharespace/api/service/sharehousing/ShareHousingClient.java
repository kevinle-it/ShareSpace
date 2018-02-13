package com.lmtri.sharespace.api.service.sharehousing;

import com.lmtri.sharespace.api.model.SearchShareHousingData;
import com.lmtri.sharespace.api.model.ShareHousing;
import com.lmtri.sharespace.api.service.RetrofitClient;
import com.lmtri.sharespace.api.service.sharehousing.get.ICheckIfExistSharePostsOfCurrentHousingCallback;
import com.lmtri.sharespace.api.service.sharehousing.get.IGetMoreNewerShareHousingsCallback;
import com.lmtri.sharespace.api.service.sharehousing.get.IGetMoreOlderShareHousingsCallback;
import com.lmtri.sharespace.api.service.sharehousing.get.IGetMoreOlderShareOfHousingCallback;
import com.lmtri.sharespace.api.service.sharehousing.post.IShareHousingDeletingCallback;
import com.lmtri.sharespace.api.service.sharehousing.post.IShareHousingPostingCallback;
import com.lmtri.sharespace.api.service.sharehousing.post.IShareHousingUpdatingCallback;
import com.lmtri.sharespace.api.service.sharehousing.report.IReportShareHousingCallback;
import com.lmtri.sharespace.api.service.sharehousing.search.ISearchShareHousingCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lmtri on 8/22/2017.
 */

public class ShareHousingClient {
    public static final String TAG = ShareHousingClient.class.getSimpleName();
    private static IShareHousingClient mIShareHousingClient = RetrofitClient.getClient().create(IShareHousingClient.class);

    // Housing.
    public static void getMoreOlderShareHousings(String currentBottomShareHousingDateTimeCreated,
                                                 final IGetMoreOlderShareHousingsCallback getMoreOlderShareHousingsCallback) {
        Call<List<ShareHousing>> call = mIShareHousingClient.getMoreOlderShareHousings(currentBottomShareHousingDateTimeCreated);
        Callback<List<ShareHousing>> callback = new Callback<List<ShareHousing>>() {
            @Override
            public void onResponse(Call<List<ShareHousing>> call, Response<List<ShareHousing>> response) {
                getMoreOlderShareHousingsCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<List<ShareHousing>> call, Throwable t) {
                getMoreOlderShareHousingsCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void getMoreNewerShareHousings(String currentTopShareHousingDateTimeCreated,
                                                 final IGetMoreNewerShareHousingsCallback getMoreNewerShareHousingsCallback) {
        Call<List<ShareHousing>> call = mIShareHousingClient.getMoreNewerShareHousings(currentTopShareHousingDateTimeCreated);
        Callback<List<ShareHousing>> callback = new Callback<List<ShareHousing>>() {
            @Override
            public void onResponse(Call<List<ShareHousing>> call, Response<List<ShareHousing>> response) {
                getMoreNewerShareHousingsCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<List<ShareHousing>> call, Throwable t) {
                getMoreNewerShareHousingsCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void searchShareHousing(SearchShareHousingData searchShareHousingData,
                                          final ISearchShareHousingCallback searchShareHousingCallback) {
        Call<List<ShareHousing>> call = mIShareHousingClient.searchShareHousing(searchShareHousingData);
        Callback<List<ShareHousing>> callback = new Callback<List<ShareHousing>>() {
            @Override
            public void onResponse(Call<List<ShareHousing>> call, Response<List<ShareHousing>> response) {
                searchShareHousingCallback.onSearchComplete(response.body());
            }

            @Override
            public void onFailure(Call<List<ShareHousing>> call, Throwable t) {
                searchShareHousingCallback.onSearchFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void postShareHousing(ShareHousing shareHousing,
                                   final IShareHousingPostingCallback shareHousingPostingCallback) {
        Call<ShareHousing> call = mIShareHousingClient.postShareHousing(shareHousing);
        Callback<ShareHousing> callback = new Callback<ShareHousing>() {
            @Override
            public void onResponse(Call<ShareHousing> call, Response<ShareHousing> response) {
                shareHousingPostingCallback.onPostComplete(response.body());
            }

            @Override
            public void onFailure(Call<ShareHousing> call, Throwable t) {
                shareHousingPostingCallback.onPostFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void updateShareHousing(ShareHousing housing,
                                     final IShareHousingUpdatingCallback shareHousingUpdatingCallback) {
        Call<Boolean> call = mIShareHousingClient.updateShareHousing(housing);
        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                shareHousingUpdatingCallback.onUpdateComplete(response.body());
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                shareHousingUpdatingCallback.onUpdateFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void deleteShareHousing(int housingID,
                                     final IShareHousingDeletingCallback shareHousingDeletingCallback) {
        Call<Boolean> call = mIShareHousingClient.deleteShareHousing(housingID);
        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                shareHousingDeletingCallback.onDeleteComplete(response.body());
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                shareHousingDeletingCallback.onDeleteFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void checkIfExistSharePostsOfCurrentHousing(int housingID,
                                                              final ICheckIfExistSharePostsOfCurrentHousingCallback checkIfExistSharePostsOfCurrentHousingCallback) {
        Call<Boolean> call = mIShareHousingClient.checkIfExistSharePostsOfCurrentHousing(housingID);
        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                checkIfExistSharePostsOfCurrentHousingCallback.onCheckComplete(response.body());
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                checkIfExistSharePostsOfCurrentHousingCallback.onCheckFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    public static void getMoreOlderShareOfHousing(int housingID, int offset,
                                                  final IGetMoreOlderShareOfHousingCallback getMoreOlderShareOfHousingCallback) {
        Call<List<ShareHousing>> call = mIShareHousingClient.getMoreOlderShareOfHousing(housingID, offset);
        Callback<List<ShareHousing>> callback = new Callback<List<ShareHousing>>() {
            @Override
            public void onResponse(Call<List<ShareHousing>> call, Response<List<ShareHousing>> response) {
                getMoreOlderShareOfHousingCallback.onGetComplete(response.body());
            }

            @Override
            public void onFailure(Call<List<ShareHousing>> call, Throwable t) {
                getMoreOlderShareOfHousingCallback.onGetFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }

    // Report Housing.
    public static void reportShareHousing(int shareHousingID,
                                          final IReportShareHousingCallback reportShareHousingCallback) {
        Call<Boolean> call = mIShareHousingClient.reportShareHousing(shareHousingID);
        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                reportShareHousingCallback.onReportComplete(response.body());
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                reportShareHousingCallback.onReportFailure(t);
            }
        };
        RetrofitClient.sendRequestWithFirebaseToken(call, callback);
    }
}
