package com.lmtri.sharespace.fragment.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lmtri.sharespace.R;
import com.lmtri.sharespace.activity.posthouse.PostHouseActivity;
import com.lmtri.sharespace.adapter.home.HousingRecyclerViewAdapter;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.listener.EndlessRecyclerViewScrollListener;
import com.lmtri.sharespace.model.Housing;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class HousingFragment extends Fragment {
    
    public static final String TAG = HousingFragment.class.getSimpleName();

    // Firebase Storage.
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private StorageReference storageReference;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private Toolbar mToolbar;
    private MaterialSearchView mMaterialSearchView;

    private int mColumnCount = 1;
    private RecyclerView mRecyclerView;
    private OnListFragmentInteractionListener mListener;
    private EndlessRecyclerViewScrollListener scrollListener;

    private List<Housing> mHousings = new ArrayList<>();
    private HousingRecyclerViewAdapter mHousingRecyclerViewAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HousingFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static HousingFragment newInstance(int columnCount) {
        HousingFragment fragment = new HousingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_housing_list, container, false);

        mToolbar = (Toolbar) view.findViewById(R.id.fragment_housing_list_toolbar);
        mToolbar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getContext(), R.color.white));
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        setHasOptionsMenu(true);

        mMaterialSearchView = (MaterialSearchView) view.findViewById(R.id.fragment_housing_list_search_view);
        mMaterialSearchView.setSuggestions(new String[] {
                "Android",
                "iOS",
                "Ajax",
                "SCALA",
                "Ruby",
                "Arduino",
                "JavaScript",
                "Application"
        });   // Test Search View.

        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_housing_list_home_list);
        // Set the adapter
        if (mColumnCount <= 1) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                    linearLayoutManager.getOrientation());

            mRecyclerView.setLayoutManager(linearLayoutManager);
//            mRecyclerView.addItemDecoration(dividerItemDecoration);
            // Retain an instance so that you can call `resetState()` for fresh searches
            scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    // Triggered only when new data needs to be appended to the list
                    // Add whatever code is needed to append new items to the bottom of the list
                    loadNextDataFromApi(page);
                }
            };
        } else {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), mColumnCount);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                    gridLayoutManager.getOrientation());

            mRecyclerView.setLayoutManager(gridLayoutManager);
//            mRecyclerView.addItemDecoration(dividerItemDecoration);
            // Retain an instance so that you can call `resetState()` for fresh searches
            scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    // Triggered only when new data needs to be appended to the list
                    // Add whatever code is needed to append new items to the bottom of the list
                    loadNextDataFromApi(page);
                }
            };
        }

        storageReference = firebaseStorage.getReferenceFromUrl(Constants.STORAGE_REFERENCE_URL);
        for (int i = 1; i <= 11; ++i) {
            final int j = i;
            storageReference.child("dummy_item_" + i + ".jpg")
                    .getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    mHousings.add(new Housing(String.valueOf(j), uri));
                    Collections.sort(mHousings, new Comparator<Housing>() {
                        @Override
                        public int compare(Housing o1, Housing o2) {
                            return o1.getId().compareTo(o2.getId());
                        }
                    });
                    mHousingRecyclerViewAdapter.notifyDataSetChanged();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        }
        mHousingRecyclerViewAdapter = new HousingRecyclerViewAdapter(getContext(), mHousings, mListener);
        mRecyclerView.setAdapter(mHousingRecyclerViewAdapter);
        mRecyclerView.addOnScrollListener(scrollListener);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        mMaterialSearchView.setMenuItem(item);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_post:
                Intent intent = new Intent(getActivity(), PostHouseActivity.class);
                startActivityForResult(intent, Constants.START_ACTIVITY_POST_HOUSE_REQUEST);
                break;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.START_ACTIVITY_POST_HOUSE_REQUEST) {
            if (resultCode == getActivity().RESULT_OK) {
                final String houseType = data.getStringExtra(Constants.START_ACTIVITY_POST_HOUSE_REQUEST_HOUSE_TYPE_RESULT);
                String area = data.getStringExtra(Constants.START_ACTIVITY_POST_HOUSE_REQUEST_AREA_RESULT);
                final String addressHouseNumber = data.getStringExtra(Constants.START_ACTIVITY_POST_HOUSE_REQUEST_ADDRESS_HOUSE_NUMBER_RESULT);
                final String addressStreet = data.getStringExtra(Constants.START_ACTIVITY_POST_HOUSE_REQUEST_ADDRESS_STREET_RESULT);
                final String addressWard = data.getStringExtra(Constants.START_ACTIVITY_POST_HOUSE_REQUEST_ADDRESS_WARD_RESULT);
                final String addressDistrict = data.getStringExtra(Constants.START_ACTIVITY_POST_HOUSE_REQUEST_ADDRESS_DISTRICT_RESULT);
                final String addressCity = data.getStringExtra(Constants.START_ACTIVITY_POST_HOUSE_REQUEST_ADDRESS_CITY_RESULT);
                String houseDirection = data.getStringExtra(Constants.START_ACTIVITY_POST_HOUSE_REQUEST_HOUSE_DIRECTION_RESULT);
                final String price = data.getStringExtra(Constants.START_ACTIVITY_POST_HOUSE_REQUEST_PRICE_RESULT);
                final String contactName = data.getStringExtra(Constants.START_ACTIVITY_POST_HOUSE_REQUEST_CONTACT_NAME_RESULT);
                String contactNumber = data.getStringExtra(Constants.START_ACTIVITY_POST_HOUSE_REQUEST_CONTACT_NUMBER_RESULT);
                String contactEmail = data.getStringExtra(Constants.START_ACTIVITY_POST_HOUSE_REQUEST_CONTACT_EMAIL_RESULT);
                final String detailsInfo = data.getStringExtra(Constants.START_ACTIVITY_POST_HOUSE_REQUEST_DETAILS_INFO_RESULT);

                storageReference.child("dummy_item_12.jpg")
                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Housing housing = new Housing(
                                uri,
                                "New House",
                                houseType,
                                price,
                                addressHouseNumber,
                                addressStreet,
                                addressWard,
                                addressDistrict,
                                addressCity,
                                contactName,
                                detailsInfo
                        );
                        mHousings.add(0, housing);
                        mHousingRecyclerViewAdapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Housing item);
    }
}
