package com.lmtri.sharespace.activity.posthouse;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lmtri.sharespace.R;
import com.lmtri.sharespace.adapter.MyPlace;
import com.lmtri.sharespace.adapter.PlaceSuggestionAdapter;
import com.lmtri.sharespace.adapter.PlacePrediction;
import com.lmtri.sharespace.api.model.Housing;
import com.lmtri.sharespace.customview.CustomEditText;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.KeyboardHelper;
import com.lmtri.sharespace.helper.ToastHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PostHouseAddressActivity extends AppCompatActivity {

    public static final String TAG = PostHouseAddressActivity.class.getSimpleName();

    private LinearLayout mContainer;

    private Toolbar mToolbar;
    private TextView mToolbarDone;

    private TextView mShownAddress;

    private String mHouseNumber;
    private String mStreet;
    private String mWard;
    private String mDistrict;
    private String mCity;

    private CustomEditText mHouseNumberEditText;
    private CustomEditText mStreetEditText;
    private CustomEditText mWardEditText;
    private CustomEditText mDistrictEditText;
    private Spinner mCitySpinner;

    private LatLngBounds mVNLatLngBounds;
    private GoogleApiClient mGoogleApiClient;
    private PlaceSuggestionAdapter mPlaceSuggestionAdapter;
    private ArrayList<MyPlace> mPlaceSuggestionLists;
    private Spinner mPlaceSuggestionSpinner;
    private TextView mLocation;

    private SupportMapFragment mSupportMapFragment;
    private GoogleMap mGoogleMap;

    private BigDecimal mLatitudeDecimal;
    private BigDecimal mLongitudeDecimal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_house_address);

        mHouseNumber = getIntent().getStringExtra(Constants.ACTIVITY_POST_HOUSE_ADDRESS_HOUSE_NUMBER_EXTRA);
        mStreet = getIntent().getStringExtra(Constants.ACTIVITY_POST_HOUSE_ADDRESS_STREET_EXTRA);
        mWard = getIntent().getStringExtra(Constants.ACTIVITY_POST_HOUSE_ADDRESS_WARD_EXTRA);
        mDistrict = getIntent().getStringExtra(Constants.ACTIVITY_POST_HOUSE_ADDRESS_DISTRICT_EXTRA);
        mCity = getIntent().getStringExtra(Constants.ACTIVITY_POST_HOUSE_ADDRESS_CITY_EXTRA);

        String latitude = getIntent().getStringExtra(Constants.ACTIVITY_POST_HOUSE_ADDRESS_LATITUDE_EXTRA);
        String longitude = getIntent().getStringExtra(Constants.ACTIVITY_POST_HOUSE_ADDRESS_LONGITUDE_EXTRA);

        if (!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {
            mLatitudeDecimal = new BigDecimal(latitude);
            mLongitudeDecimal = new BigDecimal(longitude);
        } else {
            mLatitudeDecimal = null;
            mLongitudeDecimal = null;
        }

        mContainer = (LinearLayout) findViewById(R.id.activity_post_house_address_container);
        mContainer.requestFocus();  // Remove auto-focus from all Edit Texts.

        mToolbar = (Toolbar) findViewById(R.id.activity_post_house_address_toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        if (mToolbar.getNavigationIcon() != null) {
            mToolbar.getNavigationIcon().setColorFilter(
                    ContextCompat.getColor(this, android.R.color.white),
                    PorterDuff.Mode.SRC_ATOP
            );
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.stay_still, R.anim.push_right_out);
            }
        });

        mToolbarDone = (TextView) findViewById(R.id.activity_post_house_address_toolbar_done);
        mToolbarDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(Constants.START_ACTIVITY_POST_HOUSE_ADDRESS_REQUEST_HOUSE_NUMBER_RESULT,
                        mHouseNumberEditText.getText().toString());
                returnIntent.putExtra(Constants.START_ACTIVITY_POST_HOUSE_ADDRESS_REQUEST_STREET_RESULT,
                        mStreetEditText.getText().toString());
                returnIntent.putExtra(Constants.START_ACTIVITY_POST_HOUSE_ADDRESS_REQUEST_WARD_RESULT,
                        mWardEditText.getText().toString());
                returnIntent.putExtra(Constants.START_ACTIVITY_POST_HOUSE_ADDRESS_REQUEST_DISTRICT_RESULT,
                        mDistrictEditText.getText().toString());
                returnIntent.putExtra(Constants.START_ACTIVITY_POST_HOUSE_ADDRESS_REQUEST_CITY_RESULT,
                        mCitySpinner.getSelectedItem().toString());
                returnIntent.putExtra(Constants.START_ACTIVITY_POST_HOUSE_ADDRESS_REQUEST_LATITUDE_RESULT,
                        mLatitudeDecimal != null ? mLatitudeDecimal.toString() : "");
                returnIntent.putExtra(Constants.START_ACTIVITY_POST_HOUSE_ADDRESS_REQUEST_LONGITUDE_RESULT,
                        mLongitudeDecimal != null ? mLongitudeDecimal.toString() : "");
                setResult(RESULT_OK, returnIntent);

                if (!TextUtils.isEmpty(mDistrictEditText.getText().toString())) {      // District exists.
                    if (TextUtils.isEmpty(mHouseNumberEditText.getText().toString())) {          // House Number not exist.
                        if (mLatitudeDecimal != null && mLongitudeDecimal != null) {
                            finish();
                        } else {
                            ToastHelper.showCenterToast(
                                    getApplicationContext(),
                                    getString(R.string.activity_post_house_address_error_message_missing_latitude_longitude),
                                    Toast.LENGTH_LONG
                            );
                        }
                    } else {    // House Number exists.
                        if (!TextUtils.isEmpty(mStreetEditText.getText().toString())) {     // Street exists.
                            if (mLatitudeDecimal != null && mLongitudeDecimal != null) {
                                finish();
                            } else {
                                ToastHelper.showCenterToast(
                                        getApplicationContext(),
                                        getString(R.string.activity_post_house_address_error_message_missing_latitude_longitude),
                                        Toast.LENGTH_LONG
                                );
                            }
                        } else {        // Street don't exist.
                            String errorMessage = getString(R.string.activity_post_house_detailed_item_address_error_message_missing) + getString(R.string.activity_post_house_detailed_item_address_error_message_missing_street);
                            errorMessage = errorMessage.substring(0, errorMessage.length() - 2);

                            ToastHelper.showCenterToast(
                                    getApplicationContext(),
                                    errorMessage,
                                    Toast.LENGTH_SHORT
                            );
                        }
                    }
                } else {  // District not exist.
                    String errorMessage = getString(R.string.activity_post_house_detailed_item_address_error_message_missing) + getString(R.string.activity_post_house_detailed_item_address_error_message_missing_district);
                    errorMessage = errorMessage.substring(0, errorMessage.length() - 2);

                    ToastHelper.showCenterToast(
                            getApplicationContext(),
                            errorMessage,
                            Toast.LENGTH_SHORT
                    );
                }
            }
        });

        mHouseNumberEditText = (CustomEditText) findViewById(R.id.activity_post_house_address_house_number);
        mStreetEditText = (CustomEditText) findViewById(R.id.activity_post_house_address_street);
        mWardEditText = (CustomEditText) findViewById(R.id.activity_post_house_address_ward);
        mDistrictEditText = (CustomEditText) findViewById(R.id.activity_post_house_address_district);
        mCitySpinner = (Spinner) findViewById(R.id.activity_post_house_address_city_spinner);

        mShownAddress = (TextView) findViewById(R.id.activity_post_house_address_shown_address);
        if (!TextUtils.isEmpty(mDistrict) && !TextUtils.isEmpty(mCity)) {   // District AND City exist.
            mShownAddress.setText(
                    mDistrict + ", "
                            + mCity
            );
            if (!TextUtils.isEmpty(mHouseNumber)) {    // House Number exists.
                if (!TextUtils.isEmpty(mStreet)) {     // Street exists.
                    mShownAddress.setText(
                            mHouseNumber + ", "
                                    + mStreet + ", "
                                    + mDistrict + ", "
                                    + mCity
                    );
                }
            } else {    // House Number not exist.
                if (!TextUtils.isEmpty(mStreet)) {     // Street exists.
                    mShownAddress.setText(
                            mStreet + ", "
                                    + mDistrict + ", "
                                    + mCity
                    );
                }
            }
            if (!TextUtils.isEmpty(mWard)) {   // Ward exists.
                if (!TextUtils.isEmpty(mStreet)) {     // Street exists.
                    if (!TextUtils.isEmpty(mHouseNumber)) {    // House Number exists.
                        mShownAddress.setText(
                                mHouseNumber + ", "
                                        + mStreet + ", "
                                        + mWard + ", "
                                        + mDistrict + ", "
                                        + mCity
                        );
                    } else {    // House Number not exist.
                        mShownAddress.setText(
                                mStreet + ", "
                                        + mWard + ", "
                                        + mDistrict + ", "
                                        + mCity
                        );
                    }
                } else {    // Street not exist.
                    mShownAddress.setText(
                            mWard + ", "
                                    + mDistrict + ", "
                                    + mCity
                    );
                }
            }
        }

        TextWatcher textWatcherListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mShownAddress.setText("");
                if (!TextUtils.isEmpty(mHouseNumberEditText.getText().toString())) {
                    mShownAddress.setText(
                            mShownAddress.getText()
                            + mHouseNumberEditText.getText().toString() + ", "
                    );
                }
                if (!TextUtils.isEmpty(mStreetEditText.getText().toString())) {
                    mShownAddress.setText(
                            mShownAddress.getText()
                            + mStreetEditText.getText().toString() + ", "
                    );
                }
                if (!TextUtils.isEmpty(mWardEditText.getText().toString())) {
                    mShownAddress.setText(
                            mShownAddress.getText()
                            + mWardEditText.getText().toString() + ", "
                    );
                }
                if (!TextUtils.isEmpty(mDistrictEditText.getText().toString())) {
                    mShownAddress.setText(
                            mShownAddress.getText()
                            + mDistrictEditText.getText().toString() + ", "
                    );
                }
                mShownAddress.setText(
                        mShownAddress.getText()
                        + mCitySpinner.getSelectedItem().toString()
                );
                String constraint = "";
                if (!TextUtils.isEmpty(mDistrictEditText.getText().toString())) {   // District exists.
                    constraint = mDistrictEditText.getText().toString() + ", "
                               + mCitySpinner.getSelectedItem().toString();
                    if (!TextUtils.isEmpty(mHouseNumberEditText.getText().toString())) {    // House Number exists.
                        if (!TextUtils.isEmpty(mStreetEditText.getText().toString())) {     // Street exists.
                            constraint = mHouseNumberEditText.getText().toString() + ", "
                                    + mStreetEditText.getText().toString() + ", "
                                    + mDistrictEditText.getText().toString() + ", "
                                    + mCitySpinner.getSelectedItem().toString();
                        }
                    } else {    // House Number not exist.
                        if (!TextUtils.isEmpty(mStreetEditText.getText().toString())) {     // Street exists.
                            constraint = mStreetEditText.getText().toString() + ", "
                                    + mDistrictEditText.getText().toString() + ", "
                                    + mCitySpinner.getSelectedItem().toString();
                        }
                    }
                    if (!TextUtils.isEmpty(mWardEditText.getText().toString())) {   // Ward exists.
                        if (!TextUtils.isEmpty(mStreetEditText.getText().toString())) {     // Street exists.
                            if (!TextUtils.isEmpty(mHouseNumberEditText.getText().toString())) {    // House Number exists.
                                constraint = mHouseNumberEditText.getText().toString() + ", "
                                        + mStreetEditText.getText().toString() + ", "
                                        + mWardEditText.getText().toString() + ", "
                                        + mDistrictEditText.getText().toString() + ", "
                                        + mCitySpinner.getSelectedItem().toString();
                            } else {    // House Number not exist.
                                constraint = mStreetEditText.getText().toString() + ", "
                                        + mWardEditText.getText().toString() + ", "
                                        + mDistrictEditText.getText().toString() + ", "
                                        + mCitySpinner.getSelectedItem().toString();
                            }
                        } else {    // Street not exist.
                            constraint = mWardEditText.getText().toString() + ", "
                                    + mDistrictEditText.getText().toString() + ", "
                                    + mCitySpinner.getSelectedItem().toString();
                        }
                    }
                }
                getPredictions(constraint);
            }
        };

        mHouseNumberEditText.addTextChangedListener(textWatcherListener);
        if (!TextUtils.isEmpty(mHouseNumber)) {
            mHouseNumberEditText.setText(mHouseNumber);
        }

        mStreetEditText.addTextChangedListener(textWatcherListener);
        if (!TextUtils.isEmpty(mStreet)) {
            mStreetEditText.setText(mStreet);
        }

        mWardEditText.addTextChangedListener(textWatcherListener);
        if (!TextUtils.isEmpty(mWard)) {
            mWardEditText.setText(mWard);
        }

        mDistrictEditText.addTextChangedListener(textWatcherListener);
        mDistrictEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    KeyboardHelper.hideSoftKeyboard(PostHouseAddressActivity.this, false);
                    mContainer.requestFocus();
                    return true;
                }
                return false;
            }
        });
        if (!TextUtils.isEmpty(mDistrict)) {
            mDistrictEditText.setText(mDistrict);
        }

        mCitySpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                KeyboardHelper.hideSoftKeyboard(PostHouseAddressActivity.this, false);
                mContainer.requestFocus();
//                mCitySpinner.performClick();
                return false;
            }
        });
        mCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mShownAddress.setText("");
                if (!TextUtils.isEmpty(mHouseNumberEditText.getText().toString())) {
                    mShownAddress.setText(
                            mShownAddress.getText()
                            + mHouseNumberEditText.getText().toString() + ", "
                    );
                }
                if (!TextUtils.isEmpty(mStreetEditText.getText().toString())) {
                    mShownAddress.setText(
                            mShownAddress.getText()
                            + mStreetEditText.getText().toString() + ", "
                    );
                }
                if (!TextUtils.isEmpty(mWardEditText.getText().toString())) {
                    mShownAddress.setText(
                            mShownAddress.getText()
                            + mWardEditText.getText().toString() + ", "
                    );
                }
                if (!TextUtils.isEmpty(mDistrictEditText.getText().toString())) {
                    mShownAddress.setText(
                            mShownAddress.getText()
                            + mDistrictEditText.getText().toString() + ", "
                    );
                }
                mShownAddress.setText(
                        mShownAddress.getText()
                        + parent.getItemAtPosition(position).toString()
                );
                String constraint = "";
                if (!TextUtils.isEmpty(mDistrictEditText.getText().toString())) {   // District exists.
                    constraint = mDistrictEditText.getText().toString() + ", "
                            + mCitySpinner.getSelectedItem().toString();
                    if (!TextUtils.isEmpty(mHouseNumberEditText.getText().toString())) {    // House Number exists.
                        if (!TextUtils.isEmpty(mStreetEditText.getText().toString())) {     // Street exists.
                            constraint = mHouseNumberEditText.getText().toString() + ", "
                                    + mStreetEditText.getText().toString() + ", "
                                    + mDistrictEditText.getText().toString() + ", "
                                    + mCitySpinner.getSelectedItem().toString();
                        }
                    } else {    // House Number not exist.
                        if (!TextUtils.isEmpty(mStreetEditText.getText().toString())) {     // Street exists.
                            constraint = mStreetEditText.getText().toString() + ", "
                                    + mDistrictEditText.getText().toString() + ", "
                                    + mCitySpinner.getSelectedItem().toString();
                        }
                    }
                    if (!TextUtils.isEmpty(mWardEditText.getText().toString())) {   // Ward exists.
                        if (!TextUtils.isEmpty(mStreetEditText.getText().toString())) {     // Street exists.
                            if (!TextUtils.isEmpty(mHouseNumberEditText.getText().toString())) {    // House Number exists.
                                constraint = mHouseNumberEditText.getText().toString() + ", "
                                        + mStreetEditText.getText().toString() + ", "
                                        + mWardEditText.getText().toString() + ", "
                                        + mDistrictEditText.getText().toString() + ", "
                                        + mCitySpinner.getSelectedItem().toString();
                            } else {    // House Number not exist.
                                constraint = mStreetEditText.getText().toString() + ", "
                                        + mWardEditText.getText().toString() + ", "
                                        + mDistrictEditText.getText().toString() + ", "
                                        + mCitySpinner.getSelectedItem().toString();
                            }
                        } else {    // Street not exist.
                            constraint = mWardEditText.getText().toString() + ", "
                                    + mDistrictEditText.getText().toString() + ", "
                                    + mCitySpinner.getSelectedItem().toString();
                        }
                    }
                }
                getPredictions(constraint);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (!TextUtils.isEmpty(mCity)) {
            mCitySpinner.setSelection(
                    ((ArrayAdapter<String>) mCitySpinner.getAdapter())
                            .getPosition(mCity)
            );
        } else {
            mCitySpinner.setSelection(
                    getResources()
                            .getStringArray(R.array.activity_post_house_detailed_item_address_city_array)
                            .length - 1
            );
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, mGoogleApiClientOnConnectionFailedListener)
                .addConnectionCallbacks(mGoogleApiClientConnectionCallbacks)
                .build();
        mPlaceSuggestionLists = new ArrayList<>();
        mPlaceSuggestionSpinner = (Spinner) findViewById(R.id.activity_post_house_address_place_suggestion_spinner);
        mPlaceSuggestionSpinner.setOnItemSelectedListener(mOnPlaceSuggestionSpinnerItemSelectedListener);
        mPlaceSuggestionAdapter = new PlaceSuggestionAdapter(
                this, android.R.layout.simple_spinner_item,
                android.R.id.text1, mPlaceSuggestionLists
        );
        mPlaceSuggestionSpinner.setAdapter(mPlaceSuggestionAdapter);

        mLocation = (TextView) findViewById(R.id.activity_post_house_address_latitude_longitude_location);
        if (mLatitudeDecimal != null && mLongitudeDecimal != null) {
            mLocation.setText(
                    getString(
                            R.string.activity_post_house_address_location,
                            mLatitudeDecimal.toString(),
                            mLongitudeDecimal.toString()
                    )
            );
        } else {
            mLocation.setText(getString(R.string.activity_post_house_address_location_undefined));
        }

        LatLng vnSouthWestBound = new LatLng(
                Constants.SEARCH_HOUSING_MAP_VIEW_VN_SOUTH_WEST_BOUND_LATITUDE.doubleValue(),
                Constants.SEARCH_HOUSING_MAP_VIEW_VN_SOUTH_WEST_BOUND_LONGITUDE.doubleValue()
        );
        LatLng vnNorthEastBound = new LatLng(
                Constants.SEARCH_HOUSING_MAP_VIEW_VN_NORTH_EAST_BOUND_LATITUDE.doubleValue(),
                Constants.SEARCH_HOUSING_MAP_VIEW_VN_NORTH_EAST_BOUND_LONGITUDE.doubleValue()
        );
        mVNLatLngBounds = new LatLngBounds(vnSouthWestBound, vnNorthEastBound); // Only VN's Bounds.

        mSupportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_post_house_address_support_map_fragment);
        mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                onMyMapReady(googleMap);
            }
        });
    }

    private void onMyMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                // Show user location.
//                askPermissionsAndShowMyLocation();
            }
        });
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (!marker.isInfoWindowShown()) {
                    marker.showInfoWindow();
                }
                return true;
            }
        });

        mGoogleMap.setLatLngBoundsForCameraTarget(mVNLatLngBounds);

        CameraUpdate cameraUpdate;
        if (mLatitudeDecimal != null && mLongitudeDecimal != null) {
            LatLng currentLatLng = new LatLng(mLatitudeDecimal.doubleValue(), mLongitudeDecimal.doubleValue());
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 17f);
        } else {
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(mVNLatLngBounds.getCenter(), 5.5f);
        }
        mGoogleMap.moveCamera(cameraUpdate);
    }

    private AdapterView.OnItemSelectedListener mOnPlaceSuggestionSpinnerItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Place currentPlace = mPlaceSuggestionLists.get(position).getPlace();
            LatLng currentLatLng = currentPlace.getLatLng();

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(currentLatLng);
            markerOptions.title(currentPlace.getName().toString());
            markerOptions.snippet(currentPlace.getAddress().toString());

            mLatitudeDecimal = new BigDecimal(String.valueOf(currentLatLng.latitude));
            mLongitudeDecimal = new BigDecimal(String.valueOf(currentLatLng.longitude));
            mLocation.setText(
                    getString(
                            R.string.activity_post_house_address_location,
                            mLatitudeDecimal.toString(),
                            mLongitudeDecimal.toString()
                    )
            );

            mGoogleMap.addMarker(markerOptions).showInfoWindow();

            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private ResultCallback<PlaceBuffer> mUpdateSpinnerItemPlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
//                Log.e(LOG_TAG, "Place query did not complete. Error: " +
//                        places.getStatus().toString());
                return;
            }
            Iterator<Place> iterator = places.iterator();
            mPlaceSuggestionLists.clear();
            while (iterator.hasNext()) {
                Place place = iterator.next();
                mPlaceSuggestionLists.add(new MyPlace(place, place.getAddress().toString()));
            }
            ((ArrayAdapter<MyPlace>) mPlaceSuggestionSpinner.getAdapter()).notifyDataSetChanged();
            mPlaceSuggestionSpinner.setSelection(0);
            mPlaceSuggestionSpinner.getOnItemSelectedListener().onItemSelected(null, null, 0, 0);
        }
    };

    private GoogleApiClient.ConnectionCallbacks mGoogleApiClientConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            if (!TextUtils.isEmpty(mDistrict) && !TextUtils.isEmpty(mCity)) {   // District AND City exist.
                String constraint = mDistrict + ", " + mCity;
                if (!TextUtils.isEmpty(mHouseNumber)) {    // House Number exists.
                    if (!TextUtils.isEmpty(mStreet)) {     // Street exists.
                        constraint = mHouseNumber + ", "
                                + mStreet + ", "
                                + mDistrict + ", "
                                + mCity;
                    }
                } else {    // House Number not exist.
                    if (!TextUtils.isEmpty(mStreet)) {     // Street exists.
                        constraint = mStreet + ", "
                                + mDistrict + ", "
                                + mCity;
                    }
                }
                if (!TextUtils.isEmpty(mWard)) {   // Ward exists.
                    if (!TextUtils.isEmpty(mStreet)) {     // Street exists.
                        if (!TextUtils.isEmpty(mHouseNumber)) {    // House Number exists.
                            constraint = mHouseNumber + ", "
                                    + mStreet + ", "
                                    + mWard + ", "
                                    + mDistrict + ", "
                                    + mCity;
                        } else {    // House Number not exist.
                            constraint = mStreet + ", "
                                    + mWard + ", "
                                    + mDistrict + ", "
                                    + mCity;
                        }
                    } else {    // Street not exist.
                        constraint = mWard + ", "
                                + mDistrict + ", "
                                + mCity;
                    }
                }
                getPredictions(constraint);
            }
        }

        @Override
        public void onConnectionSuspended(int i) {

        }
    };

    private GoogleApiClient.OnConnectionFailedListener mGoogleApiClientOnConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            ToastHelper.showCenterToast(
                    PostHouseAddressActivity.this,
                    "Google Places API connection failed with error code:"
                            + connectionResult.getErrorCode()
                            + "\nRestarting activity to reconnect...",
                    Toast.LENGTH_LONG
            );
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    finish();
                    startActivity(getIntent());
                }
            }, 5000);
        }
    };

    private ResultCallback<AutocompletePredictionBuffer> mAutocompletePredictionCallback = new ResultCallback<AutocompletePredictionBuffer>() {
        @Override
        public void onResult(@NonNull AutocompletePredictionBuffer autocompletePredictions) {
            final Status status = autocompletePredictions.getStatus();
            if (!status.isSuccess()) {
                Toast.makeText(PostHouseAddressActivity.this, "Error: " + status.toString(),
                        Toast.LENGTH_SHORT).show();
//                Log.e(TAG, "Error getting place predictions: " + status
//                        .toString());
                autocompletePredictions.release();
                return;
            }

//            Log.i(TAG, "Query completed. Received " + autocompletePredictions.getCount()
//                    + " predictions.");
            Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
            ArrayList<PlacePrediction> resultList = new ArrayList<>(autocompletePredictions.getCount());
            while (iterator.hasNext()) {
                AutocompletePrediction prediction = iterator.next();
                resultList.add(new PlacePrediction(prediction.getPlaceId(),
                        prediction.getFullText(null)));
            }
            // Buffer release
            autocompletePredictions.release();

            if (resultList != null && resultList.size() > 0) {
                String[] predictionPlaceIDs = new String[resultList.size()];
                for (int i = 0; i < resultList.size(); ++i) {
                    predictionPlaceIDs[i] = resultList.get(i).getPlaceID();
                }
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, predictionPlaceIDs);
                placeResult.setResultCallback(mUpdateSpinnerItemPlaceDetailsCallback);
            } else {
//                ToastHelper.showCenterToast(
//                        getApplicationContext(),
//                        R.string.activity_post_house_address_error_message_not_found_suggested_places,
//                        Toast.LENGTH_LONG
//                );
                ((ArrayAdapter<MyPlace>) mPlaceSuggestionSpinner.getAdapter()).notifyDataSetInvalidated();
            }
        }
    };

    private void getPredictions(CharSequence constraint) {
        if (mGoogleApiClient != null) {
//            Log.i(TAG, "Executing autocomplete query for: " + constraint);
            AutocompleteFilter filter = new AutocompleteFilter.Builder()
                    .setCountry("VN")
                    .build();
            PendingResult<AutocompletePredictionBuffer> results =
                    Places.GeoDataApi
                            .getAutocompletePredictions(
                                    mGoogleApiClient, constraint.toString(),
                                    mVNLatLngBounds, filter
                            );
            // Wait for predictions, set the timeout.
//            AutocompletePredictionBuffer autocompletePredictions = results
//                    .await(60, TimeUnit.SECONDS);
            results.setResultCallback(mAutocompletePredictionCallback);
        }
//        Log.e(TAG, "Google API client is not connected.");
    }
}
