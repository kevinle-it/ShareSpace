package com.lmtri.sharespace.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lmtri.sharespace.api.model.Housing;
import com.lmtri.sharespace.api.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmtri on 8/14/2017.
 */

public class SharedPreferencesHelper {
    public static void saveCurrentUser(Context context, String userUID, User user) {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(Constants.SHARE_SPACE_SHARED_PREFERENCES, Context.MODE_PRIVATE);

        String jsonUser = gson.toJson(user);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(
                Constants.CURRENT_USER_SHARED_PREFERENCES + userUID,
                jsonUser
        );
        editor.apply();
    }

    public static User getCurrentUser(Context context, String userUID) {
        Gson gson = new Gson();
        User user;

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(Constants.SHARE_SPACE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String jsonUser = sharedPreferences
                .getString(Constants.CURRENT_USER_SHARED_PREFERENCES + userUID, "");

        user = gson.fromJson(jsonUser, User.class);

        return user;
    }

    public static void removeCurrentUser(Context context, String userUID) {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(Constants.SHARE_SPACE_SHARED_PREFERENCES, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Constants.CURRENT_USER_SHARED_PREFERENCES + userUID);
        editor.apply();
    }

    public static void savePostedHousingList(Context context, String userUID, List<Housing> housings) {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(Constants.SHARE_SPACE_SHARED_PREFERENCES, Context.MODE_PRIVATE);

        String jsonHousings = gson.toJson(housings);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(
                Constants.LIST_OF_POSTED_HOUSINGS_SHARED_PREFERENCES + userUID,
                jsonHousings
        );
        editor.apply();
    }

    public static void savePostedHousing(Context context, String userUID, Housing housing) {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(Constants.SHARE_SPACE_SHARED_PREFERENCES, Context.MODE_PRIVATE);

        String jsonHousingsSaved = sharedPreferences
                .getString(Constants.LIST_OF_POSTED_HOUSINGS_SHARED_PREFERENCES + userUID, "");
        String jsonNewHousingToAdd = gson.toJson(housing);

        JSONArray jsonArrayHousings = new JSONArray();

        try {
            if (jsonHousingsSaved.length() != 0) {
                jsonArrayHousings = new JSONArray(jsonHousingsSaved);
            }
            jsonArrayHousings.put(new JSONObject(jsonNewHousingToAdd));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Save new array.
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(
                Constants.LIST_OF_POSTED_HOUSINGS_SHARED_PREFERENCES + userUID,
                jsonArrayHousings.toString()
        );
        editor.apply();
    }

    public static ArrayList<Housing> getPostedHousingList(Context context, String userUID) {
        Gson gson = new Gson();
        ArrayList<Housing> housings;

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(Constants.SHARE_SPACE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String jsonHousings = sharedPreferences
                .getString(Constants.LIST_OF_POSTED_HOUSINGS_SHARED_PREFERENCES + userUID, "");

        Type type = new TypeToken<ArrayList<Housing>>() {}.getType();

        housings = gson.fromJson(jsonHousings, type);

        return housings;
    }

    public static void removePostedHousingFromSharedPreferences(Context context, String userUID, Housing housing) {
        Gson gson = new Gson();
        ArrayList<Housing> housings;

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(Constants.SHARE_SPACE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String jsonHousings = sharedPreferences
                .getString(Constants.LIST_OF_POSTED_HOUSINGS_SHARED_PREFERENCES + userUID, "");

        Type type = new TypeToken<ArrayList<Housing>>() {}.getType();

        housings = gson.fromJson(jsonHousings, type);

        housings.remove(housing);

        savePostedHousingList(context, userUID, housings);
    }
}