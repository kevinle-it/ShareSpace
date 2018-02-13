package com.lmtri.sharespace.helper.firebasecloudmessaging;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lmtri.sharespace.api.model.AppointmentNotification;
import com.lmtri.sharespace.api.model.FCMResponse;
import com.lmtri.sharespace.helper.Constants;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lmtri on 12/26/2017.
 */

public class FCMClient {

    public static final String TAG = FCMClient.class.getSimpleName();

    private static Retrofit mRetrofit = null;

    private static IFCMClient mIFCMClient = getClient().create(IFCMClient.class);

    public static Retrofit getClient() {
        if (mRetrofit == null) {
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();

                            // Request customization: add request headers.
                            Request newRequest = original.newBuilder()
                                    .header("Authorization", "key="
                                            + Constants.FCM_SERVER_KEY)
                                    .method(original.method(), original.body())
                                    .build();

                            return chain.proceed(newRequest);
                        }
                    })
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .writeTimeout(8, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
                    .build();

            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                    .create();

            mRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.FCM_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient)
                    .build();
        }
        return mRetrofit;
    }

    public static void sendNotification(AppointmentNotification data, final IFCMSendNotificationCallback fcmSendNotificationCallback) {
        Call<FCMResponse> call = mIFCMClient.sendNotification(data);
        Callback<FCMResponse> callback = new Callback<FCMResponse>() {
            @Override
            public void onResponse(Call<FCMResponse> call, retrofit2.Response<FCMResponse> response) {
                fcmSendNotificationCallback.onSendComplete(response.body());
            }

            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {
                fcmSendNotificationCallback.onSendFailure(t);
            }
        };
        call.enqueue(callback);
    }
}
