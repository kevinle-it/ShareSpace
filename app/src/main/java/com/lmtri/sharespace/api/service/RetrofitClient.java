package com.lmtri.sharespace.api.service;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lmtri.sharespace.R;
import com.lmtri.sharespace.helper.Constants;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lmtri on 8/1/2017.
 */

public class RetrofitClient {

    public static final String TAG = RetrofitClient.class.getSimpleName();

    private static Retrofit mRetrofit = null;

    public static Retrofit getClient() {
        if (mRetrofit == null) {
            OkHttpClient httpClient =
                    new OkHttpClient.Builder()
                            .addInterceptor(new Interceptor() {
                                @Override
                                public Response intercept(Chain chain) throws IOException {
                                    Request original = chain.request();

                                    // Request customization: add request headers.
                                    Request newRequest = original.newBuilder()
                                            .header("Authorization", "Bearer "
                                                    + Constants.SHARE_SPACE_USER_FIREBASE_TOKEN)
                                            .method(original.method(), original.body())
                                            .build();

                                    return chain.proceed(newRequest);
                                }
                            })
                            .addInterceptor(new HttpLoggingInterceptor().setLevel(Level.HEADERS))
                            .connectTimeout(5, TimeUnit.SECONDS)
                            .writeTimeout(8, TimeUnit.SECONDS)
                            .readTimeout(5, TimeUnit.SECONDS)
                            .build();

            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                    .create();

            mRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.SHARE_SPACE_SERVER_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient)
                    .build();
        }
        return mRetrofit;
    }

    public static void sendRequestWithFirebaseToken(final Call call, final Callback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.getIdToken(false).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                @Override
                public void onSuccess(GetTokenResult getTokenResult) {
                    Constants.SHARE_SPACE_USER_FIREBASE_TOKEN = getTokenResult.getToken();
                    call.enqueue(callback);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            Constants.SHARE_SPACE_USER_FIREBASE_TOKEN = "";
            call.enqueue(callback);
        }
    }

    public static void showShareSpaceServerConnectionErrorDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.cannot_connect_to_share_space_server)
                .setMessage(R.string.please_try_again_later)
                .setPositiveButton(R.string.cannot_connect_to_share_space_server_ok_button, null)
                .show();
    }

    public static void showShareSpaceServerConnectionErrorDialog(Context context, Throwable t) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.cannot_connect_to_share_space_server)
                .setMessage(context.getString(R.string.error_name_please_try_again_later, t.getMessage()))
                .setPositiveButton(R.string.cannot_connect_to_share_space_server_ok_button, null)
                .show();
    }
}
