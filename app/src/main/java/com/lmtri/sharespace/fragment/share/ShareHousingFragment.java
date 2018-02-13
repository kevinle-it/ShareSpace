package com.lmtri.sharespace.fragment.share;

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
import com.lmtri.sharespace.activity.postsharehouse.PostShareHouseActivity;
import com.lmtri.sharespace.activity.sharetab.search.SearchShareHousingActivity;
import com.lmtri.sharespace.adapter.sharetab.ShareHousingRecyclerViewAdapter;
import com.lmtri.sharespace.api.model.ShareHousing;
import com.lmtri.sharespace.api.service.RetrofitClient;
import com.lmtri.sharespace.api.service.sharehousing.ShareHousingClient;
import com.lmtri.sharespace.api.service.sharehousing.get.IGetMoreNewerShareHousingsCallback;
import com.lmtri.sharespace.api.service.sharehousing.get.IGetMoreOlderShareHousingsCallback;
import com.lmtri.sharespace.api.service.user.UserClient;
import com.lmtri.sharespace.api.service.user.post.sharehousing.IGetMoreOlderPostedShareHousingsCallback;
import com.lmtri.sharespace.api.service.user.post.sharehousing.IGetNumOfPostedShareHousingsCallback;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.busevent.SigninEvent;
import com.lmtri.sharespace.helper.busevent.SignoutEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.post.DeleteShareHousingEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.availability.HideShareHousingEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.post.PostShareHousingEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.availability.UnhideShareHousingEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.post.UpdateShareHousingEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.save.SaveShareHousingEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.save.UnsaveShareHousingEvent;
import com.lmtri.sharespace.listener.EndlessRecyclerViewScrollListener;
import com.lmtri.sharespace.listener.OnShareHousingListInteractionListener;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnShareHousingListInteractionListener} interface to handle interaction events.
 * Use the {@link ShareHousingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShareHousingFragment extends Fragment {

    public static final String TAG = ShareHousingFragment.class.getSimpleName();

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
    private OnShareHousingListInteractionListener mListener;
    private EndlessRecyclerViewScrollListener mRecyclerViewScrollListener;

    private boolean mIsAllowLoadMoreOlderPostedShareHousings = false;
    private int mNumOfPostedShareHousings = 0;
    private ArrayList<ShareHousing> mPostedShareHousings = new ArrayList<>();
    private ArrayList<ShareHousing> mShareHousings = new ArrayList<>();
    private ShareHousingRecyclerViewAdapter mShareHousingRecyclerViewAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ShareHousingFragment() {
        // Required empty public constructor
    }

    @SuppressWarnings("unused")
    public static ShareHousingFragment newInstance(int columnCount) {
        ShareHousingFragment fragment = new ShareHousingFragment();
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
        View view = inflater.inflate(R.layout.fragment_share_housing_list, container, false);

        mConnectionErrorLayout = (LinearLayout) view.findViewById(R.id.fragment_share_housing_list_connection_error_layout);
        mConnectionErrorMessage = (TextView) view.findViewById(R.id.fragment_share_housing_list_connection_error_message);
        mViewSwitcherRetryConnect = (ViewSwitcher) view.findViewById(R.id.fragment_share_housing_list_view_switcher_retry_connect_to_server);
        mRetryConnectButton = (FrameLayout) view.findViewById(R.id.fragment_share_housing_list_retry_button);
        mRetryConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewSwitcherRetryConnect.showNext();
                mIsAllowLoadMoreOlderPostedShareHousings = true;
                loadMoreOlderShareHousingsFromApi(0);
            }
        });

        mToolbar = (Toolbar) view.findViewById(R.id.fragment_share_housing_list_toolbar);
        mToolbar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getContext(), R.color.white));
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

        mFilterButton = (ImageView) view.findViewById(R.id.fragment_share_housing_list_filter_button);
        mFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchShareHousingActivity.class);
                startActivity(intent);
            }
        });
        mPostHouseButton = (ImageView) view.findViewById(R.id.fragment_share_housing_list_post_house_button);
        mPostHouseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.CURRENT_USER != null) {
                    Intent intent = new Intent(getActivity(), PostShareHouseActivity.class);
                    intent.putExtra(Constants.IS_POST_NEW_SHARE_HOUSING_EXTRA, true);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, Constants.START_ACTIVITY_LOGIN_REQUEST);
                }
            }
        });
//        setHasOptionsMenu(true);

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

        mSwipeRefreshContainer = (SwipeRefreshLayout) view.findViewById(R.id.fragment_share_housing_list_swipe_refresh_container);
        mSwipeRefreshContainer.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorPrimaryLight,
                R.color.colorSecondary
        );
        mSwipeRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mShareHousings != null && mShareHousings.size() > 0) {
                    String s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(mShareHousings.get(0).getDateTimeCreated());
                    s = s.substring(0, s.length() - 2) + ":" + s.substring(s.length() - 2, s.length());
                    loadMoreNewerShareHousingsFromApi(s);
                } else {
                    mIsAllowLoadMoreOlderPostedShareHousings = true;
                    loadMoreOlderShareHousingsFromApi(0);
                }
            }
        });
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_share_housing_list_share_list);
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
                    loadMoreOlderShareHousingsFromApi(page);
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
                    loadMoreOlderShareHousingsFromApi(page);
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
//                    mShareHousings.add(new Housing(String.valueOf(j), uri));
//                    Collections.sort(mShareHousings, new Comparator<Housing>() {
//                        @Override
//                        public int compare(Housing o1, Housing o2) {
//                            return o1.getId().compareTo(o2.getId());
//                        }
//                    });
//                    mShareHousingRecyclerViewAdapter.notifyDataSetChanged();
//
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    e.printStackTrace();
//                }
//            });
//        }
        mShareHousingRecyclerViewAdapter = new ShareHousingRecyclerViewAdapter(
                getContext(), mPostedShareHousings, mShareHousings, mListener
        );
        mRecyclerView.setAdapter(mShareHousingRecyclerViewAdapter);
        mRecyclerView.addOnScrollListener(mRecyclerViewScrollListener);

        mIsAllowLoadMoreOlderPostedShareHousings = false;
        loadMoreOlderShareHousingsFromApi(0);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == Constants.START_ACTIVITY_POST_HOUSE_REQUEST) {
//            if (resultCode == getActivity().RESULT_OK) {
//                Gson gson = new Gson();
//                ShareHousing shareHousing = gson.fromJson(
//                        data.getStringExtra(Constants.START_ACTIVITY_POST_HOUSE_REQUEST_POSTED_HOUSING_RESULT),
//                        ShareHousing.class
//                );
//
//                mShareHousings.add(0, shareHousing);
//                mShareHousingRecyclerViewAdapter.notifyItemInserted(0);
////                mStorageReference.child("dummy_item_12.jpg")
////                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
////                    @Override
////                    public void onSuccess(Uri uri) {
////                        Housing shareHousing = new Housing(
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
////                        mShareHousings.add(0, shareHousing);
////                        mShareHousingRecyclerViewAdapter.notifyDataSetChanged();
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
                Intent intent = new Intent(getActivity(), PostShareHouseActivity.class);
                intent.putExtra(Constants.IS_POST_NEW_SHARE_HOUSING_EXTRA, true);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnShareHousingListInteractionListener) {
//            mListener = (OnShareHousingListInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnShareHousingListInteractionListener");
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
        if (mNumOfPostedShareHousings == 0) {
            UserClient.getNumOfPostedShareHousings(new IGetNumOfPostedShareHousingsCallback() {
                @Override
                public void onGetComplete(Integer numOfPostedShareHousings) {
                    if (numOfPostedShareHousings != null && numOfPostedShareHousings > 0) {
                        mNumOfPostedShareHousings = numOfPostedShareHousings;
                        loadMoreOlderPostedShareHousingsFromApi(0);
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
        mNumOfPostedShareHousings = 0;
        mPostedShareHousings.clear();
        mShareHousingRecyclerViewAdapter.notifyDataSetChanged();

        mPostedShareHousings = new ArrayList<>();

        mShareHousingRecyclerViewAdapter = new ShareHousingRecyclerViewAdapter(
                getContext(), mPostedShareHousings, mShareHousings, mListener
        );
        mRecyclerView.setAdapter(mShareHousingRecyclerViewAdapter);
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
    public void hideShareHousing(HideShareHousingEvent event) {
        if (event.getShareHousing() != null) {
            for (int i = 0; i < mShareHousings.size(); ++i) {
                if (event.getShareHousing().getID() == mShareHousings.get(i).getID()) {
                    mShareHousings.remove(i);
//                    mShareHousingRecyclerViewAdapter.notifyItemRemoved(i);
                    mShareHousingRecyclerViewAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    @Subscribe
    public void unhideShareHousing(UnhideShareHousingEvent event) {
        if (event.getShareHousing() != null) {
            mShareHousings.add(0, event.getShareHousing());
            mShareHousingRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void shareHousingPosted(PostShareHousingEvent event) {
        if (event.getShareHousing() != null) {
            if (mConnectionErrorLayout.getVisibility() == View.VISIBLE) {
                mConnectionErrorLayout.setVisibility(View.GONE);
            }
            if (mPostedShareHousings.isEmpty()) {
                mNumOfPostedShareHousings = 1;
                mPostedShareHousings.add(0, event.getShareHousing());
                mShareHousingRecyclerViewAdapter.notifyDataSetChanged();
            } else {
                ++mNumOfPostedShareHousings;
                mPostedShareHousings.add(0, event.getShareHousing());
                mShareHousingRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    @Subscribe
    public void shareHousingUpdated(UpdateShareHousingEvent event) {
        if (event.getShareHousing() != null) {
            for (int i = 0; i < mPostedShareHousings.size(); ++i) {
                if (event.getShareHousing().getID() == mPostedShareHousings.get(i).getID()) {
                    mPostedShareHousings.remove(i);
                    mPostedShareHousings.add(0, event.getShareHousing());
//                    mShareHousingRecyclerViewAdapter.notifyItemRemoved(i);
                    mShareHousingRecyclerViewAdapter.notifyDataSetChanged();
                    break;
                }
            }
            for (int i = 0; i < mShareHousings.size(); ++i) {
                if (event.getShareHousing().getID() == mShareHousings.get(i).getID()) {
                    mShareHousings.remove(i);
                    mShareHousings.add(0, event.getShareHousing());
//                    mShareHousingRecyclerViewAdapter.notifyItemRemoved(i);
                    mShareHousingRecyclerViewAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    @Subscribe
    public void shareHousingDeleted(DeleteShareHousingEvent event) {
        if (event.getShareHousing() != null) {
            for (int i = 0; i < mPostedShareHousings.size(); ++i) {
                if (event.getShareHousing().getID() == mPostedShareHousings.get(i).getID()) {
                    mPostedShareHousings.remove(i);
                    --mNumOfPostedShareHousings;
//                    mShareHousingRecyclerViewAdapter.notifyItemRemoved(i);
                    break;
                }
            }
            for (int i = 0; i < mShareHousings.size(); ++i) {
                if (event.getShareHousing().getID() == mShareHousings.get(i).getID()) {
                    mShareHousings.remove(i);
//                    mShareHousingRecyclerViewAdapter.notifyItemRemoved(i);
                    break;
                }
            }
            mShareHousingRecyclerViewAdapter.notifyDataSetChanged();
            if (mNumOfPostedShareHousings > 0) {
                loadMoreOlderPostedShareHousingsFromApi(1);
            }
            if (mShareHousings.isEmpty()) {
                mConnectionErrorLayout.setVisibility(View.VISIBLE);
                if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                    mViewSwitcherRetryConnect.showNext();
                }
            }
        }
    }

    // Only load more newer Share Housings if mHousings is NOT EMPTY.
    private void loadMoreNewerShareHousingsFromApi(String currentTopShareHousingDateTimeCreated) {
        ShareHousingClient.getMoreNewerShareHousings(
                currentTopShareHousingDateTimeCreated,
                new IGetMoreNewerShareHousingsCallback() {
                    @Override
                    public void onGetComplete(final List<ShareHousing> newerShareHousings) {
                        mSwipeRefreshContainer.setRefreshing(false);
                        if (newerShareHousings != null && newerShareHousings.size() > 0) {
                            mShareHousings.addAll(0, newerShareHousings);

//                            mShareHousingRecyclerViewAdapter.notifyItemRangeInserted(0, newerShareHousings.size());
                            mShareHousingRecyclerViewAdapter.notifyDataSetChanged();
                        }
                        if (Constants.CURRENT_USER != null) {
                            if (mPostedShareHousings.isEmpty()) {
                                UserClient.getNumOfPostedShareHousings(new IGetNumOfPostedShareHousingsCallback() {
                                    @Override
                                    public void onGetComplete(Integer numOfPostedHousings) {
                                        if (numOfPostedHousings != null && numOfPostedHousings > 0) {
                                            mNumOfPostedShareHousings = numOfPostedHousings;
                                            loadMoreOlderPostedShareHousingsFromApi(0);
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
    public void loadMoreOlderPostedShareHousingsFromApi(final int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
//        int currentBottomHousingID = !mPostedShareHousings.isEmpty() ? mPostedShareHousings.get(mPostedShareHousings.size() - 1).getID() : -1;
        String currentBottomShareHousingDateTimeCreated
                = !mPostedShareHousings.isEmpty()
                ? new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(mPostedShareHousings.get(mPostedShareHousings.size() - 1).getDateTimeCreated())
                : null;
        if (currentBottomShareHousingDateTimeCreated != null) {
            currentBottomShareHousingDateTimeCreated
                    = currentBottomShareHousingDateTimeCreated.substring(0, currentBottomShareHousingDateTimeCreated.length() - 2)
                    + ":" + currentBottomShareHousingDateTimeCreated.substring(currentBottomShareHousingDateTimeCreated.length() - 2, currentBottomShareHousingDateTimeCreated.length());
        }
        UserClient.getMoreOlderPostedShareHousings(currentBottomShareHousingDateTimeCreated, new IGetMoreOlderPostedShareHousingsCallback() {
            @Override
            public void onGetComplete(List<ShareHousing> olderPostedShareHousings) {
                if (mPostedShareHousings.isEmpty()) {
                    if (olderPostedShareHousings != null && olderPostedShareHousings.size() > 0) {
                        if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                            mViewSwitcherRetryConnect.showNext();
                        }
                        mConnectionErrorLayout.setVisibility(View.GONE);

                        for (int i = 0; i < olderPostedShareHousings.size(); ++i) {
                            if (mPostedShareHousings.size() < 3) {
                                mPostedShareHousings.add(olderPostedShareHousings.get(i));
                            } else {
                                break;
                            }
                        }

                        mShareHousingRecyclerViewAdapter.notifyDataSetChanged();

                        if (mNumOfPostedShareHousings < 3 && mPostedShareHousings.size() < mNumOfPostedShareHousings) {
                            loadMoreOlderPostedShareHousingsFromApi(1);
                        }
                    } else {
                        if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                            mViewSwitcherRetryConnect.showNext();
                        }
//                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext());
                    }
                } else if (!mPostedShareHousings.isEmpty()) {
                    if (olderPostedShareHousings != null && olderPostedShareHousings.size() > 0) {
//                        int positionStart = mPostedShareHousings.size() + 1;

                        for (int i = 0; i < olderPostedShareHousings.size(); ++i) {
                            if (mPostedShareHousings.size() < 3) {
                                mPostedShareHousings.add(olderPostedShareHousings.get(i));
                            } else {
                                break;
                            }
                        }
//                        mShareHousingRecyclerViewAdapter.notifyItemRangeInserted(positionStart, i);
                        mShareHousingRecyclerViewAdapter.notifyDataSetChanged();

                        if (mNumOfPostedShareHousings < 3 && mPostedShareHousings.size() < mNumOfPostedShareHousings) {
                            loadMoreOlderPostedShareHousingsFromApi(1);
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
    public void loadMoreOlderShareHousingsFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
//        int currentBottomShareHousingID = !mShareHousings.isEmpty() ? mShareHousings.get(mShareHousings.size() - 1).getID() : -1;
        String currentBottomShareHousingDateTimeCreated
                = !mShareHousings.isEmpty()
                ? new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(mShareHousings.get(mShareHousings.size() - 1).getDateTimeCreated())
                : null;
        if (currentBottomShareHousingDateTimeCreated != null) {
            currentBottomShareHousingDateTimeCreated
                    = currentBottomShareHousingDateTimeCreated.substring(0, currentBottomShareHousingDateTimeCreated.length() - 2)
                    + ":" + currentBottomShareHousingDateTimeCreated.substring(currentBottomShareHousingDateTimeCreated.length() - 2, currentBottomShareHousingDateTimeCreated.length());
        }
        ShareHousingClient.getMoreOlderShareHousings(currentBottomShareHousingDateTimeCreated, new IGetMoreOlderShareHousingsCallback() {
            @Override
            public void onGetComplete(List<ShareHousing> olderShareHousings) {
                mSwipeRefreshContainer.setRefreshing(false);
                if (mShareHousings.isEmpty()) {
                    if (olderShareHousings != null && olderShareHousings.size() > 0) {
                        if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                            mViewSwitcherRetryConnect.showNext();
                        }
                        mConnectionErrorLayout.setVisibility(View.GONE);

//                        int positionStart = mShareHousings.size();

                        mShareHousings.addAll(olderShareHousings);

//                        mShareHousingRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mShareHousings.size() - positionStart);
                        mShareHousingRecyclerViewAdapter.notifyDataSetChanged();
                    } else {
                        mConnectionErrorMessage.setVisibility(View.VISIBLE);
                        if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                            mViewSwitcherRetryConnect.showNext();
                        }
                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext());
                    }
                } else if (!mShareHousings.isEmpty()) {
                    if (olderShareHousings != null && olderShareHousings.size() > 0) {
                        int positionStart = mShareHousings.size();

                        mShareHousings.addAll(olderShareHousings);

//                        mShareHousingRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mShareHousings.size() - positionStart);
                        mShareHousingRecyclerViewAdapter.notifyDataSetChanged();
                    }
//                    else {
//                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext());
//                    }
                }
                if (Constants.CURRENT_USER != null) {
                    if (mNumOfPostedShareHousings == 0 && mIsAllowLoadMoreOlderPostedShareHousings) {
                        mIsAllowLoadMoreOlderPostedShareHousings = false;
                        UserClient.getNumOfPostedShareHousings(new IGetNumOfPostedShareHousingsCallback() {
                            @Override
                            public void onGetComplete(Integer numOfPostedShareHousings) {
                                if (numOfPostedShareHousings != null && numOfPostedShareHousings > 0) {
                                    mNumOfPostedShareHousings = numOfPostedShareHousings;
                                    loadMoreOlderPostedShareHousingsFromApi(0);
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

    public void setListener(OnShareHousingListInteractionListener listener) {
        mListener = listener;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnShareHousingListInteractionListener {
//        void onShareHousingListInteraction(ShareHousing item);
//    }
}
