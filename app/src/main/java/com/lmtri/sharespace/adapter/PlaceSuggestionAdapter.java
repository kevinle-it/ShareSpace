package com.lmtri.sharespace.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLngBounds;
import com.lmtri.sharespace.R;

import java.util.ArrayList;

/**
 * Created by lmtri on 12/22/2017.
 */

public class PlaceSuggestionAdapter extends ArrayAdapter<MyPlace> {
    private static final String TAG = "PlaceSuggestionAdapter";

    private ArrayList<MyPlace> mResultList;
    private LayoutInflater mLayoutInflater;

    /**
     * Constructor
     *
     * @param context  Context
     * @param resourceId Layout resource
     * @param textViewId Text View ID inside Layout resource
     * @param resultList List of Result Places returned by Google Place API
     */
    public PlaceSuggestionAdapter(Context context, int resourceId, int textViewId, ArrayList<MyPlace> resultList) {
        super(context, resourceId, textViewId, resultList);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResultList = resultList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(android.R.layout.simple_spinner_item, parent, false);
        }
        MyPlace item = mResultList.get(position);
        TextView itemText = (TextView) convertView.findViewById(android.R.id.text1);
        itemText.setText(item.getDescription());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater
                    .inflate(
                            R.layout.activity_post_house_address_custom_spinner_dropdown_item,
                            parent, false
                    );
        }
        MyPlace item = mResultList.get(position);
        TextView primaryPlaceInfo = (TextView) convertView.findViewById(R.id.activity_post_house_address_custom_spinner_dropdown_item_primary_place_info);
        primaryPlaceInfo.setText(item.getPlace().getName());
        TextView secondaryPlaceInfo = (TextView) convertView.findViewById(R.id.activity_post_house_address_custom_spinner_dropdown_item_secondary_place_info);
        secondaryPlaceInfo.setText(item.getDescription());

        return convertView;
    }

    @Override
    public int getCount() {
        return mResultList != null ? mResultList.size() : 0;
    }

    @Override
    public MyPlace getItem(int position) {
        return (mResultList != null && mResultList.size() > 0) ? mResultList.get(position) : null;
    }
}
