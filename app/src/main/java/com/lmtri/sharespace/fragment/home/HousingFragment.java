package com.lmtri.sharespace.fragment.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.lmtri.sharespace.R;
import com.lmtri.sharespace.ShareSpaceApplication;
import com.lmtri.sharespace.activity.LoginActivity;
import com.lmtri.sharespace.activity.hometab.search.SearchHousingActivity;
import com.lmtri.sharespace.activity.posthouse.PostHouseActivity;
import com.lmtri.sharespace.adapter.hometab.HousingRecyclerViewAdapter;
import com.lmtri.sharespace.api.model.Housing;
import com.lmtri.sharespace.api.service.RetrofitClient;
import com.lmtri.sharespace.api.service.housing.HousingClient;
import com.lmtri.sharespace.api.service.housing.get.IGetMoreNewerHousingsCallback;
import com.lmtri.sharespace.api.service.housing.get.IGetMoreOlderHousingsCallback;
import com.lmtri.sharespace.api.service.user.UserClient;
import com.lmtri.sharespace.api.service.user.post.housing.IGetMoreOlderPostedHousingsCallback;
import com.lmtri.sharespace.api.service.user.post.housing.IGetNumOfPostedHousingsCallback;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.busevent.SigninEvent;
import com.lmtri.sharespace.helper.busevent.SignoutEvent;
import com.lmtri.sharespace.helper.busevent.housing.post.DeleteHousingEvent;
import com.lmtri.sharespace.helper.busevent.housing.availability.HideHousingEvent;
import com.lmtri.sharespace.helper.busevent.housing.post.PostHousingEvent;
import com.lmtri.sharespace.helper.busevent.housing.availability.UnhideHousingEvent;
import com.lmtri.sharespace.helper.busevent.housing.post.UpdateHousingEvent;
import com.lmtri.sharespace.helper.busevent.housing.save.SaveHousingEvent;
import com.lmtri.sharespace.helper.busevent.housing.save.UnsaveHousingEvent;
import com.lmtri.sharespace.helper.busevent.housing.search.SearchHousingPostShareOfExistHousingEvent;
import com.lmtri.sharespace.listener.EndlessRecyclerViewScrollListener;
import com.lmtri.sharespace.listener.OnHousingListInteractionListener;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnHousingListInteractionListener}
 * interface to handle interaction events.
 * Use the {@link HousingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HousingFragment extends Fragment {
    
    public static final String TAG = HousingFragment.class.getSimpleName();

    // Firebase Storage.
//    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
//    private StorageReference mStorageReference;

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private LinearLayout mConnectionErrorLayout;
    private TextView mConnectionErrorMessage;
    private ViewSwitcher mViewSwitcherRetryConnect;
    private FrameLayout mRetryConnectButton;

    private Toolbar mToolbar;
    private ImageView mFilterButton;
    private ImageView mPostHouseButton;
    private SwipeRefreshLayout mSwipeRefreshContainer;
    private RecyclerView mRecyclerView;
    private OnHousingListInteractionListener mListener;
    private EndlessRecyclerViewScrollListener mRecyclerViewScrollListener;

    private boolean mIsAllowLoadMoreOlderPostedHousings = false;
    private int mNumOfPostedHousings = 0;
    private ArrayList<Housing> mPostedHousings = new ArrayList<>();
    private ArrayList<Housing> mHousings = new ArrayList<>();
    private HousingRecyclerViewAdapter mHousingRecyclerViewAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HousingFragment() {
        // Required empty public constructor
    }

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
        ShareSpaceApplication.BUS.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        View view = inflater.inflate(R.layout.fragment_housing_list, container, false);

        mConnectionErrorLayout = (LinearLayout) view.findViewById(R.id.fragment_housing_list_connection_error_layout);
        mConnectionErrorMessage = (TextView) view.findViewById(R.id.fragment_housing_list_connection_error_message);
        mViewSwitcherRetryConnect = (ViewSwitcher) view.findViewById(R.id.fragment_housing_list_view_switcher_retry_connect_to_server);
        mRetryConnectButton = (FrameLayout) view.findViewById(R.id.fragment_housing_list_retry_button);
        mRetryConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewSwitcherRetryConnect.showNext();
                mIsAllowLoadMoreOlderPostedHousings = true;
                loadMoreOlderHousingsFromApi(0);
            }
        });

        mToolbar = (Toolbar) view.findViewById(R.id.fragment_housing_list_toolbar);
        mToolbar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getContext(), R.color.white));
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

        mFilterButton = (ImageView) view.findViewById(R.id.fragment_housing_list_filter_button);
        mFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchHousingActivity.class);
                startActivityForResult(intent, Constants.START_ACTIVITY_SEARCH_HOUSING_REQUEST);
            }
        });
        mPostHouseButton = (ImageView) view.findViewById(R.id.fragment_housing_list_post_house_button);
        mPostHouseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.CURRENT_USER != null) {
                    Intent intent = new Intent(getActivity(), PostHouseActivity.class);
                    intent.putExtra(Constants.IS_POST_NEW_HOUSING_EXTRA, true);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, Constants.START_ACTIVITY_LOGIN_REQUEST);
                }
            }
        });
//        setHasOptionsMenu(true);
//
//        mMaterialSearchView = (MaterialSearchView) view.findViewById(R.id.fragment_housing_list_search_view);
//        mMaterialSearchView.setSuggestions(new String[] {
//                "Android",
//                "iOS",
//                "Ajax",
//                "SCALA",
//                "Ruby",
//                "Arduino",
//                "JavaScript",
//                "Application"
//        });   // Test Search View.

        mSwipeRefreshContainer = (SwipeRefreshLayout) view.findViewById(R.id.fragment_housing_list_swipe_refresh_container);
        mSwipeRefreshContainer.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorPrimaryLight,
                R.color.colorSecondary
        );
        mSwipeRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mHousings != null && mHousings.size() > 0) {
                    String s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(mHousings.get(0).getDateTimeCreated());
                    s = s.substring(0, s.length() - 2) + ":" + s.substring(s.length() - 2, s.length());
                    loadMoreNewerHousingsFromApi(s);
                } else {
                    mIsAllowLoadMoreOlderPostedHousings = true;
                    loadMoreOlderHousingsFromApi(0);
                }
            }
        });
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_housing_list_home_list);
        // Set the adapter
        if (mColumnCount <= 1) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                    linearLayoutManager.getOrientation());

            mRecyclerView.setLayoutManager(linearLayoutManager);
//            mRecyclerView.addItemDecoration(dividerItemDecoration);
            // Retain an instance so that you can call `resetState()` for fresh searches
            mRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager, true) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    // Triggered only when new data needs to be appended to the list
                    // Add whatever code is needed to append new items to the bottom of the list
                    loadMoreOlderHousingsFromApi(page);
                }
            };
        } else {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), mColumnCount);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                    gridLayoutManager.getOrientation());

            mRecyclerView.setLayoutManager(gridLayoutManager);
//            mRecyclerView.addItemDecoration(dividerItemDecoration);
            // Retain an instance so that you can call `resetState()` for fresh searches
            mRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager, true) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    // Triggered only when new data needs to be appended to the list
                    // Add whatever code is needed to append new items to the bottom of the list
                    loadMoreOlderHousingsFromApi(page);
                }
            };
        }

//        mStorageReference = mFirebaseStorage.getReferenceFromUrl(Constants.STORAGE_REFERENCE_URL);
//        for (int i = 1; i <= 11; ++i) {
//            final int j = i;
//            mStorageReference.child("dummy_item_" + i + ".jpg")
//                    .getDownloadUrl()
//                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
//                @Override
//                public void onSuccess(Uri uri) {
//                    mHousings.add(new Housing(String.valueOf(j), uri));
//                    Collections.sort(mHousings, new Comparator<Housing>() {
//                        @Override
//                        public int compare(Housing o1, Housing o2) {
//                            return o1.getId().compareTo(o2.getId());
//                        }
//                    });
//                    mHousingRecyclerViewAdapter.notifyDataSetChanged();
//
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    e.printStackTrace();
//                }
//            });
//        }
        mHousingRecyclerViewAdapter = new HousingRecyclerViewAdapter(
                getContext(), mPostedHousings, mHousings, mListener
        );
        mRecyclerView.setAdapter(mHousingRecyclerViewAdapter);
        mRecyclerView.addOnScrollListener(mRecyclerViewScrollListener);

        mIsAllowLoadMoreOlderPostedHousings = false;
        loadMoreOlderHousingsFromApi(0);

        return view;
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
////        menu.clear();
////        inflater.inflate(R.menu.menu_main, menu);
//
//        MenuItem item = menu.findItem(R.id.action_search);
//        mMaterialSearchView.setMenuItem(item);
//
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_post:
//                if (Constants.CURRENT_USER != null) {
//                    Intent intent = new Intent(getActivity(), PostHouseActivity.class);
//                    startActivityForResult(intent, Constants.START_ACTIVITY_POST_HOUSE_REQUEST);
//                } else {
//                    Intent intent = new Intent(getActivity(), LoginActivity.class);
//                    startActivityForResult(intent, Constants.START_ACTIVITY_LOGIN_REQUEST);
//                }
//                break;
//        }
//        return true;
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == Constants.START_ACTIVITY_POST_HOUSE_REQUEST) {
//            if (resultCode == getActivity().RESULT_OK) {
//                Gson gson = new Gson();
//                Housing housing = gson.fromJson(
//                        data.getStringExtra(Constants.START_ACTIVITY_POST_HOUSE_REQUEST_POSTED_HOUSING_RESULT),
//                        Housing.class
//                );
//
//                mHousings.add(0, housing);
//                mHousingRecyclerViewAdapter.notifyItemInserted(0);
////                mStorageReference.child("dummy_item_12.jpg")
////                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
////                    @Override
////                    public void onSuccess(Uri uri) {
////                        Housing housing = new Housing(
////                                uri,
////                                "New House",
////                                houseType,
////                                price,
////                                addressHouseNumber,
////                                addressStreet,
////                                addressWard,
////                                addressDistrict,
////                                addressCity,
////                                contactName,
////                                detailedInfo
////                        );
////                        mHousings.add(0, housing);
////                        mHousingRecyclerViewAdapter.notifyDataSetChanged();
////                    }
////                }).addOnFailureListener(new OnFailureListener() {
////                    @Override
////                    public void onFailure(@NonNull Exception e) {
////                        e.printStackTrace();
////                    }
////                });
//            }
//        } else
        if (requestCode == Constants.START_ACTIVITY_LOGIN_REQUEST) {
            if (resultCode == getActivity().RESULT_OK) {
                Intent intent = new Intent(getActivity(), PostHouseActivity.class);
                intent.putExtra(Constants.IS_POST_NEW_HOUSING_EXTRA, true);
                startActivity(intent);
            }
        } else if (requestCode == Constants.START_ACTIVITY_SEARCH_HOUSING_REQUEST) {
            if (resultCode == getActivity().RESULT_OK) {
                ShareSpaceApplication.BUS.post(new SearchHousingPostShareOfExistHousingEvent());
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnHousingListInteractionListener) {
//            mListener = (OnHousingListInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnHousingListInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    public boolean onBackPressed() {
//        if (mMaterialSearchView.isSearchOpen()) {
//            mMaterialSearchView.closeSearch();
//            return true;    // This fragment has consumed the back press event.
//        }
        return false;
    }

    @Subscribe
    public void userSignin(SigninEvent event) {
        if (mNumOfPostedHousings == 0) {
            UserClient.getNumOfPostedHousings(new IGetNumOfPostedHousingsCallback() {
                @Override
                public void onGetComplete(Integer numOfPostedHousings) {
                    if (numOfPostedHousings != null && numOfPostedHousings > 0) {
                        mNumOfPostedHousings = numOfPostedHousings;
                        loadMoreOlderPostedHousingsFromApi(0);
                    } else {
                        if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                            mViewSwitcherRetryConnect.showNext();
                        }
                    }
                }

                @Override
                public void onGetFailure(Throwable t) {

                }
            });
        }
    }

    @Subscribe
    public void userSignout(SignoutEvent event) {
        mNumOfPostedHousings = 0;
        mPostedHousings.clear();
        mHousingRecyclerViewAdapter.notifyDataSetChanged();

        mPostedHousings = new ArrayList<>();

        mHousingRecyclerViewAdapter = new HousingRecyclerViewAdapter(
                getContext(), mPostedHousings, mHousings, mListener
        );
        mRecyclerView.setAdapter(mHousingRecyclerViewAdapter);
    }

    @Subscribe
    public void saveHousing(SaveHousingEvent event) {
        if (event.getSavedHousing() != null) {
            for (int i = 0; i < mHousings.size(); ++i) {
                if (event.getSavedHousing().getSavedHousing().getID()
                        == mHousings.get(i).getID()) {
                    mHousings.get(i).setNumOfSaved(mHousings.get(i).getNumOfSaved() + 1);
                    break;
                }
            }
        }
    }

    @Subscribe
    public void unsaveHousing(UnsaveHousingEvent event) {
        if (event.getSavedHousing() != null) {
            for (int i = 0; i < mHousings.size(); ++i) {
                if (event.getSavedHousing().getSavedHousing().getID()
                        == mHousings.get(i).getID()) {
                    mHousings.get(i).setNumOfSaved(mHousings.get(i).getNumOfSaved() - 1);
                    break;
                }
            }
        }
    }

    @Subscribe
    public void hideHousing(HideHousingEvent event) {
        if (event.getHousing() != null) {
            for (int i = 0; i < mHousings.size(); ++i) {
                if (event.getHousing().getID() == mHousings.get(i).getID()) {
                    mHousings.remove(i);
//                    mHousingRecyclerViewAdapter.notifyItemRemoved(i);
                    mHousingRecyclerViewAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    @Subscribe
    public void unhideHousing(UnhideHousingEvent event) {
        if (event.getHousing() != null) {
            mHousings.add(0, event.getHousing());
            mHousingRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void housingPosted(PostHousingEvent event) {
        if (event.getHousing() != null) {
            if (mConnectionErrorLayout.getVisibility() == View.VISIBLE) {
                mConnectionErrorLayout.setVisibility(View.GONE);
            }
            if (mPostedHousings.isEmpty()) {
                mNumOfPostedHousings = 1;
                mPostedHousings.add(0, event.getHousing());
                mHousingRecyclerViewAdapter.notifyDataSetChanged();
            } else {
                ++mNumOfPostedHousings;
                mPostedHousings.add(0, event.getHousing());
                mHousingRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    @Subscribe
    public void housingUpdated(UpdateHousingEvent event) {
        if (event.getHousing() != null) {
            for (int i = 0; i < mPostedHousings.size(); ++i) {
                if (event.getHousing().getID() == mPostedHousings.get(i).getID()) {
                    mPostedHousings.remove(i);
                    mPostedHousings.add(0, event.getHousing());
//                    mHousingRecyclerViewAdapter.notifyItemRemoved(i);
//                    mHousingRecyclerViewAdapter.notifyItemInserted(0);
                    mHousingRecyclerViewAdapter.notifyDataSetChanged();
                    break;
                }
            }
            for (int i = 0; i < mHousings.size(); ++i) {
                if (event.getHousing().getID() == mHousings.get(i).getID()) {
                    mHousings.remove(i);
                    mHousings.add(0, event.getHousing());
//                    mHousingRecyclerViewAdapter.notifyItemRemoved(i);
//                    mHousingRecyclerViewAdapter.notifyItemInserted(0);
                    mHousingRecyclerViewAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    @Subscribe
    public void housingDeleted(DeleteHousingEvent event) {
        if (event.getHousing() != null) {
            for (int i = 0; i < mPostedHousings.size(); ++i) {
                if (event.getHousing().getID() == mPostedHousings.get(i).getID()) {
                    mPostedHousings.remove(i);
                    --mNumOfPostedHousings;
//                    mHousingRecyclerViewAdapter.notifyItemRemoved(i);
                    break;
                }
            }
            for (int i = 0; i < mHousings.size(); ++i) {
                if (event.getHousing().getID() == mHousings.get(i).getID()) {
                    mHousings.remove(i);
//                    mHousingRecyclerViewAdapter.notifyItemRemoved(i);
                    break;
                }
            }
            mHousingRecyclerViewAdapter.notifyDataSetChanged();
            if (mNumOfPostedHousings > 0) {
                loadMoreOlderPostedHousingsFromApi(1);
            }
            if (mHousings.isEmpty()) {
                mConnectionErrorLayout.setVisibility(View.VISIBLE);
                if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                    mViewSwitcherRetryConnect.showNext();
                }
            }
        }
    }

    // Only load more newer Housings if mHousings is NOT EMPTY.
    private void loadMoreNewerHousingsFromApi(String currentTopHousingDateTimeCreated) {
        HousingClient.getMoreNewerHousings(
                currentTopHousingDateTimeCreated,
                new IGetMoreNewerHousingsCallback() {
                    @Override
                    public void onGetComplete(final List<Housing> newerHousings) {
                        mSwipeRefreshContainer.setRefreshing(false);
                        if (newerHousings != null && newerHousings.size() > 0) {
                            mHousings.addAll(0, newerHousings);

//                            mHousingRecyclerViewAdapter.notifyItemRangeInserted(0, newerHousings.size());
                            mHousingRecyclerViewAdapter.notifyDataSetChanged();
                        }
                        if (Constants.CURRENT_USER != null) {
                            if (mPostedHousings.isEmpty()) {
                                UserClient.getNumOfPostedHousings(new IGetNumOfPostedHousingsCallback() {
                                    @Override
                                    public void onGetComplete(Integer numOfPostedHousings) {
                                        if (numOfPostedHousings != null && numOfPostedHousings > 0) {
                                            mNumOfPostedHousings = numOfPostedHousings;
                                            loadMoreOlderPostedHousingsFromApi(0);
                                        } else {
                                            if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                                                mViewSwitcherRetryConnect.showNext();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onGetFailure(Throwable t) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onGetFailure(Throwable t) {
                        mSwipeRefreshContainer.setRefreshing(false);
                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext(), t);
                    }
                }
        );
    }


    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadMoreOlderPostedHousingsFromApi(final int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
//        int currentBottomHousingID = !mPostedHousings.isEmpty() ? mPostedHousings.get(mPostedHousings.size() - 1).getID() : -1;
        String currentBottomHousingDateTimeCreated
                = !mPostedHousings.isEmpty()
                ? new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(mPostedHousings.get(mPostedHousings.size() - 1).getDateTimeCreated())
                : null;
        if (currentBottomHousingDateTimeCreated != null) {
            currentBottomHousingDateTimeCreated
                    = currentBottomHousingDateTimeCreated.substring(0, currentBottomHousingDateTimeCreated.length() - 2)
                    + ":" + currentBottomHousingDateTimeCreated.substring(currentBottomHousingDateTimeCreated.length() - 2, currentBottomHousingDateTimeCreated.length());
        }
        UserClient.getMoreOlderPostedHousings(currentBottomHousingDateTimeCreated, new IGetMoreOlderPostedHousingsCallback() {
            @Override
            public void onGetComplete(List<Housing> olderPostedHousings) {
                if (mPostedHousings.isEmpty()) {
                    if (olderPostedHousings != null && olderPostedHousings.size() > 0) {
                        if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                            mViewSwitcherRetryConnect.showNext();
                        }
                        mConnectionErrorLayout.setVisibility(View.GONE);

                        for (int i = 0; i < olderPostedHousings.size(); ++i) {
                            if (mPostedHousings.size() < 3) {
                                mPostedHousings.add(olderPostedHousings.get(i));
                            } else {
                                break;
                            }
                        }

                        mHousingRecyclerViewAdapter.notifyDataSetChanged();

                        if (mPostedHousings.size() < 3 && mPostedHousings.size() < mNumOfPostedHousings) {
                            loadMoreOlderPostedHousingsFromApi(1);
                        }
                    } else {
                        if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                            mViewSwitcherRetryConnect.showNext();
                        }
//                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext());
                    }
                } else if (!mPostedHousings.isEmpty()) {
                    if (olderPostedHousings != null && olderPostedHousings.size() > 0) {
//                        int positionStart = mPostedHousings.size() + 1;

                        for (int i = 0; i < olderPostedHousings.size(); ++i) {
                            if (mPostedHousings.size() < 3) {
                                mPostedHousings.add(olderPostedHousings.get(i));
                            } else {
                                break;
                            }
                        }
//                        mHousingRecyclerViewAdapter.notifyItemRangeInserted(positionStart, i);
                        mHousingRecyclerViewAdapter.notifyDataSetChanged();

                        if (mPostedHousings.size() < 3 && mPostedHousings.size() < mNumOfPostedHousings) {
                            loadMoreOlderPostedHousingsFromApi(1);
                        }
                    }
//                    else {
//                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext());
//                    }
                }
            }

            @Override
            public void onGetFailure(Throwable t) {
//                mConnectionErrorMessage.setVisibility(View.VISIBLE);
//                if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
//                    mViewSwitcherRetryConnect.showNext();
//                }
//                RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext(), t);
            }
        });
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadMoreOlderHousingsFromApi(final int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
//        int currentBottomHousingID = !mHousings.isEmpty() ? mHousings.get(mHousings.size() - 1).getID() : -1;
        String currentBottomHousingDateTimeCreated
                = !mHousings.isEmpty()
                ? new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(mHousings.get(mHousings.size() - 1).getDateTimeCreated())
                : null;
        if (currentBottomHousingDateTimeCreated != null) {
            currentBottomHousingDateTimeCreated
                    = currentBottomHousingDateTimeCreated.substring(0, currentBottomHousingDateTimeCreated.length() - 2)
                    + ":" + currentBottomHousingDateTimeCreated.substring(currentBottomHousingDateTimeCreated.length() - 2, currentBottomHousingDateTimeCreated.length());
        }
        HousingClient.getMoreOlderHousings(currentBottomHousingDateTimeCreated, new IGetMoreOlderHousingsCallback() {
            @Override
            public void onGetComplete(List<Housing> olderHousings) {
                mSwipeRefreshContainer.setRefreshing(false);
                if (mHousings.isEmpty()) {
                    if (olderHousings != null && olderHousings.size() > 0) {
                        if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                            mViewSwitcherRetryConnect.showNext();
                        }
                        mConnectionErrorLayout.setVisibility(View.GONE);

//                        int positionStart = mHousings.size();

                        mHousings.addAll(olderHousings);

//                        mHousingRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mHousings.size() - positionStart);
                        mHousingRecyclerViewAdapter.notifyDataSetChanged();
                    } else {
                        mConnectionErrorMessage.setVisibility(View.VISIBLE);
                        if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                            mViewSwitcherRetryConnect.showNext();
                        }
                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext());
                    }
                } else if (!mHousings.isEmpty()) {
                    if (olderHousings != null && olderHousings.size() > 0) {
//                        int positionStart = mHousings.size();

                        mHousings.addAll(olderHousings);

//                        mHousingRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mHousings.size() - positionStart);
                        mHousingRecyclerViewAdapter.notifyDataSetChanged();
                    }
//                    else {
//                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext());
//                    }
                }
                if (Constants.CURRENT_USER != null) {
                    if (mNumOfPostedHousings == 0 && mIsAllowLoadMoreOlderPostedHousings) {
                        mIsAllowLoadMoreOlderPostedHousings = false;
                        UserClient.getNumOfPostedHousings(new IGetNumOfPostedHousingsCallback() {
                            @Override
                            public void onGetComplete(Integer numOfPostedHousings) {
                                if (numOfPostedHousings != null && numOfPostedHousings > 0) {
                                    mNumOfPostedHousings = numOfPostedHousings;
                                    loadMoreOlderPostedHousingsFromApi(0);
                                } else {
                                    if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                                        mViewSwitcherRetryConnect.showNext();
                                    }
                                }
                            }

                            @Override
                            public void onGetFailure(Throwable t) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onGetFailure(Throwable t) {
                mSwipeRefreshContainer.setRefreshing(false);

                mConnectionErrorMessage.setVisibility(View.VISIBLE);
                if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                    mViewSwitcherRetryConnect.showNext();
                }
                RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext(), t);
            }
        });
    }

    public void setListener(OnHousingListInteractionListener listener) {
        mListener = listener;
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
//    public interface OnHousingListInteractionListener {
//        void onHousingListInteraction(Housing item);
//    }
}
