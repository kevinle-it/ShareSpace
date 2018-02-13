package com.lmtri.sharespace.fragment.share.search;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lmtri.sharespace.R;
import com.lmtri.sharespace.ShareSpaceApplication;
import com.lmtri.sharespace.api.model.ShareHousing;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.HousePriceHelper;
import com.lmtri.sharespace.helper.busevent.sharehousing.post.DeleteShareHousingEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.post.UpdateShareHousingEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.save.SaveShareHousingEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.save.UnsaveShareHousingEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.search.ReturnSearchShareHousingResultEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.search.SearchShareHousingMapListViewBackButtonEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.search.SearchShareHousingSwitchListViewEvent;
import com.lmtri.sharespace.listener.OnShareHousingListInteractionListener;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchResultShareHousingMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchResultShareHousingMapFragment extends Fragment {

    private Toolbar mToolbar;
    private ImageView mSwitchToListViewType;

    private boolean mIsMapReady = false;
    private boolean mIsMapLoaded = false;

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private MarkerOptions mMarkerOptions = new MarkerOptions();

    private List<ShareHousing> mShareHousings;
    private OnShareHousingListInteractionListener mListener;

    public SearchResultShareHousingMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchResultShareHousingMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchResultShareHousingMapFragment newInstance() {
        SearchResultShareHousingMapFragment fragment = new SearchResultShareHousingMapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        ShareSpaceApplication.BUS.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_result_share_housing_map, container, false);

        mToolbar = (Toolbar) view.findViewById(R.id.fragment_search_result_share_housing_map_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        if (mToolbar.getNavigationIcon() != null) {
            mToolbar.getNavigationIcon().setColorFilter(
                    ContextCompat.getColor(getContext(), android.R.color.white),
                    PorterDuff.Mode.SRC_ATOP
            );
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareSpaceApplication.BUS.post(new SearchShareHousingMapListViewBackButtonEvent());
            }
        });

        mSwitchToListViewType = (ImageView) view.findViewById(R.id.fragment_search_result_share_housing_list_view_type);
        mSwitchToListViewType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareSpaceApplication.BUS.post(new SearchShareHousingSwitchListViewEvent());
            }
        });

        mMapView = (MapView) view.findViewById(R.id.fragment_search_result_share_housing_map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                onMyMapReady(googleMap);
            }
        });

        return view;
    }

    private void onMyMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mIsMapReady = true;

        mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mIsMapLoaded = true;
                // Show user location.
//                askPermissionsAndShowMyLocation();
            }
        });
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });
        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Integer shareHousingID = (Integer) marker.getTag();
                if (shareHousingID != null) {
                    if (mShareHousings != null && mShareHousings.size() > 0) {
                        for (ShareHousing shareHousing : mShareHousings) {
                            if (shareHousing.getID() == shareHousingID.intValue()) {
                                if (mListener != null) {
                                    mListener.onShareHousingListInteraction(shareHousing);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        });
        LatLng vnSouthWestMapViewBound = new LatLng(
                Constants.SEARCH_HOUSING_MAP_VIEW_VN_SOUTH_WEST_MAP_VIEW_BOUND_LATITUDE.doubleValue(),
                Constants.SEARCH_HOUSING_MAP_VIEW_VN_SOUTH_WEST_MAP_VIEW_BOUND_LONGITUDE.doubleValue()
        );
        LatLng vnNorthEastMapViewBound = new LatLng(
                Constants.SEARCH_HOUSING_MAP_VIEW_VN_NORTH_EAST_MAP_VIEW_BOUND_LATITUDE.doubleValue(),
                Constants.SEARCH_HOUSING_MAP_VIEW_VN_NORTH_EAST_MAP_VIEW_BOUND_LONGITUDE.doubleValue()
        );
        LatLngBounds vnLatLngMapViewBounds = new LatLngBounds(vnSouthWestMapViewBound, vnNorthEastMapViewBound);    // Bigger Map View Bounds (including several neighbor countries).

//        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(vnLatLngMapViewBounds.getCenter(), 10);
//        mGoogleMap.moveCamera(cameraUpdate);

        LatLng vnSouthWestBound = new LatLng(
                Constants.SEARCH_HOUSING_MAP_VIEW_VN_SOUTH_WEST_BOUND_LATITUDE.doubleValue(),
                Constants.SEARCH_HOUSING_MAP_VIEW_VN_SOUTH_WEST_BOUND_LONGITUDE.doubleValue()
        );
        LatLng vnNorthEastBound = new LatLng(
                Constants.SEARCH_HOUSING_MAP_VIEW_VN_NORTH_EAST_BOUND_LATITUDE.doubleValue(),
                Constants.SEARCH_HOUSING_MAP_VIEW_VN_NORTH_EAST_BOUND_LONGITUDE.doubleValue()
        );
        LatLngBounds vnLatLngBounds = new LatLngBounds(vnSouthWestBound, vnNorthEastBound); // Only VN's Bounds.
        mGoogleMap.setLatLngBoundsForCameraTarget(vnLatLngBounds);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(vnLatLngBounds.getCenter(), 5.5f);
        mGoogleMap.moveCamera(cameraUpdate);
    }

    @Override
    public void onResume() {
        if (mMapView != null) {
            mMapView.onResume();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    public boolean onBackPressed() {
        return false;
    }

    private void askPermissionsAndShowMyLocation() {
        if (Build.VERSION.SDK_INT >= 23) {
            int accessCoarsePermission
                    = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
            int accessFinePermission
                    = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);


            if (accessCoarsePermission != PackageManager.PERMISSION_GRANTED
                    || accessFinePermission != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION};

                ActivityCompat.requestPermissions(getActivity(), permissions,
                        Constants.ACCESS_FINE_LOCATION_PERMISSION_REQUEST);
                return;
            }
        }
        showMyLocation();
    }


    // When user respond to permission asking dialog (granted or denied).
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.ACCESS_FINE_LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(getContext(), "Permission granted!", Toast.LENGTH_LONG).show();

                showMyLocation();
            } else {
                Toast.makeText(getContext(), "Permission denied!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getEnabledLocationProvider() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        // Criteria for searching a location provider.
        Criteria criteria = new Criteria();

        // Search for the best location provider based on the above criteria.
        // ==> "gps", "network",...
        String bestProvider = locationManager.getBestProvider(criteria, true);

        boolean enabled = locationManager.isProviderEnabled(bestProvider);

        if (!enabled) {
            Toast.makeText(getContext(), "No location provider enabled!", Toast.LENGTH_LONG).show();
            return null;
        }
        return bestProvider;
    }

    // Only call this method if ACCESS_COARSE_LOCATION OR ACCESS_FINE_LOCATION Permission is granted.
    private void showMyLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        String locationProvider = getEnabledLocationProvider();

        if (locationProvider == null) {
            return;
        }

        // Millisecond
        final long MIN_TIME_BW_UPDATES = 1000;
        // Met
        final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;

        Location myLocation = null;
        try {
            mGoogleMap.setMyLocationEnabled(true);

            // This need permissions granted. (Requested above).
            locationManager.requestLocationUpdates(
                    locationProvider,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {

                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    }
            );
            // Get current location.
            myLocation = locationManager
                    .getLastKnownLocation(locationProvider);
        } catch (SecurityException e) {   // API >= 23 need to catch this Security Exception.
            Toast.makeText(getContext(), "Show My Location Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return;
        }

        if (myLocation != null) {
            LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)             // Sets the center of the map to location user
                    .zoom(15)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
//                    .tilt(40)                   // Sets the tilt of the camera to 40 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            // Thêm Marker cho Map:
            MarkerOptions option = new MarkerOptions();
            option.title("My Location");
            option.snippet("....");
            option.position(latLng);
            Marker currentMarker = mGoogleMap.addMarker(option);
            currentMarker.showInfoWindow();
        } else {
            Toast.makeText(getContext(), "Location not found!", Toast.LENGTH_LONG).show();
        }
    }

    @Subscribe
    public void returnSearchShareHousingResults(ReturnSearchShareHousingResultEvent event) {
        if (event.getShareHousingResults() != null) {
            mShareHousings = event.getShareHousingResults();
            if (mGoogleMap != null && mIsMapLoaded) {
                loadAllMarkers();
            } else {
                Handler mainHandler = new Handler(Looper.getMainLooper());

                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (!mIsMapLoaded && !mIsMapReady) {
                            mMapView.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(GoogleMap googleMap) {
                                    mGoogleMap = googleMap;
                                    mIsMapReady = true;

                                    mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                                        @Override
                                        public void onMapLoaded() {
                                            mIsMapLoaded = true;
                                            loadAllMarkers();
                                        }
                                    });
                                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                    mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
                                    mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                        @Override
                                        public boolean onMarkerClick(Marker marker) {
                                            marker.showInfoWindow();
                                            return true;
                                        }
                                    });
                                    mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                        @Override
                                        public void onInfoWindowClick(Marker marker) {
                                            Integer shareHousingID = (Integer) marker.getTag();
                                            if (shareHousingID != null) {
                                                if (mShareHousings != null && mShareHousings.size() > 0) {
                                                    for (ShareHousing shareHousing : mShareHousings) {
                                                        if (shareHousing.getID() == shareHousingID.intValue()) {
                                                            if (mListener != null) {
                                                                mListener.onShareHousingListInteraction(shareHousing);
                                                            }
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                };
                mainHandler.post(myRunnable);
            }
        }
    }

    @Subscribe
    public void saveShareHousing(SaveShareHousingEvent event) {
        if (event.getSavedShareHousing() != null) {
            for (int i = 0; i < mShareHousings.size(); ++i) {
                if (event.getSavedShareHousing().getSavedShareHousing().getID()
                        == mShareHousings.get(i).getID()) {
                    mShareHousings.get(i).setNumOfSaved(mShareHousings.get(i).getNumOfSaved() + 1);
                    break;
                }
            }
        }
    }

    @Subscribe
    public void unsaveShareHousing(UnsaveShareHousingEvent event) {
        if (event.getSavedShareHousing() != null) {
            for (int i = 0; i < mShareHousings.size(); ++i) {
                if (event.getSavedShareHousing().getSavedShareHousing().getID()
                        == mShareHousings.get(i).getID()) {
                    mShareHousings.get(i).setNumOfSaved(mShareHousings.get(i).getNumOfSaved() - 1);
                    break;
                }
            }
        }
    }

    @Subscribe
    public void shareHousingUpdated(UpdateShareHousingEvent event) {
        if (event.getShareHousing() != null) {
            for (int i = 0; i < mShareHousings.size(); ++i) {
                if (mShareHousings.get(i).getID() == event.getShareHousing().getID()) {
                    mShareHousings.remove(i);
                    mShareHousings.add(0, event.getShareHousing());
                    break;
                }
            }
            LatLng vnSouthWestBound = new LatLng(
                    Constants.SEARCH_HOUSING_MAP_VIEW_VN_SOUTH_WEST_BOUND_LATITUDE.doubleValue(),
                    Constants.SEARCH_HOUSING_MAP_VIEW_VN_SOUTH_WEST_BOUND_LONGITUDE.doubleValue()
            );
            LatLng vnNorthEastBound = new LatLng(
                    Constants.SEARCH_HOUSING_MAP_VIEW_VN_NORTH_EAST_BOUND_LATITUDE.doubleValue(),
                    Constants.SEARCH_HOUSING_MAP_VIEW_VN_NORTH_EAST_BOUND_LONGITUDE.doubleValue()
            );
            LatLngBounds vnLatLngBounds = new LatLngBounds(vnSouthWestBound, vnNorthEastBound); // Only VN's Bounds.

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(vnLatLngBounds.getCenter(), 5.5f);
            mGoogleMap.moveCamera(cameraUpdate);
            loadAllMarkers();
        }
    }

    @Subscribe
    public void shareHousingDeleted(DeleteShareHousingEvent event) {
        if (event.getShareHousing() != null) {
            for (int i = 0; i < mShareHousings.size(); ++i) {
                if (event.getShareHousing().getID() == mShareHousings.get(i).getID()) {
                    mShareHousings.remove(i);
                    break;
                }
            }
            LatLng vnSouthWestBound = new LatLng(
                    Constants.SEARCH_HOUSING_MAP_VIEW_VN_SOUTH_WEST_BOUND_LATITUDE.doubleValue(),
                    Constants.SEARCH_HOUSING_MAP_VIEW_VN_SOUTH_WEST_BOUND_LONGITUDE.doubleValue()
            );
            LatLng vnNorthEastBound = new LatLng(
                    Constants.SEARCH_HOUSING_MAP_VIEW_VN_NORTH_EAST_BOUND_LATITUDE.doubleValue(),
                    Constants.SEARCH_HOUSING_MAP_VIEW_VN_NORTH_EAST_BOUND_LONGITUDE.doubleValue()
            );
            LatLngBounds vnLatLngBounds = new LatLngBounds(vnSouthWestBound, vnNorthEastBound); // Only VN's Bounds.

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(vnLatLngBounds.getCenter(), 5.5f);
            mGoogleMap.moveCamera(cameraUpdate);
            loadAllMarkers();
        }
    }

    private void loadAllMarkers() {
        mGoogleMap.clear();
        LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();
        List<Marker> markerList = new ArrayList<>();
        for (int i = 0; i < mShareHousings.size(); ++i) {
            ShareHousing currentShareHousing = mShareHousings.get(i);

            LatLng currentLatLng = new LatLng(
                    currentShareHousing.getHousing().getLatitude().doubleValue(),
                    currentShareHousing.getHousing().getLongitude().doubleValue()
            );

            latLngBoundsBuilder.include(currentLatLng);

            mMarkerOptions.position(currentLatLng);
            mMarkerOptions.title(currentShareHousing.getHousing().getTitle());
            mMarkerOptions.snippet(currentShareHousing.getDescription());
//            Pair<String, String> pair = HousePriceHelper.parseForShareHousing(currentShareHousing.getPricePerMonthOfOne(), getActivity());
//            if (pair.first == null) {
//                mMarkerOptions.snippet(currentShareHousing.getDescription() + "\n" + pair.second);
//            } else {
//                mMarkerOptions.snippet(currentShareHousing.getDescription() + "\n" + pair.first + " " + pair.second);
//            }

            Marker currentMarker = mGoogleMap.addMarker(mMarkerOptions);
            currentMarker.setTag(currentShareHousing.getID());
            currentMarker.showInfoWindow();

            markerList.add(currentMarker);
        }
        if (markerList != null) {
            if (markerList.size() > 1) {
                LatLngBounds latLngBounds = latLngBoundsBuilder.build();
                int padding = 200; // offset from edges of the map in pixels
                CameraUpdate cameraUpdate =
                        CameraUpdateFactory.newLatLngBounds(latLngBounds, padding);
                mGoogleMap.animateCamera(cameraUpdate);
            } else if (markerList.size() == 1) {
                mGoogleMap.animateCamera(
                        CameraUpdateFactory
                                .newLatLngZoom(markerList.get(0).getPosition(), 17)
                );
            }
        }
    }

    public void setListener(OnShareHousingListInteractionListener listener) {
        mListener = listener;
    }
}
