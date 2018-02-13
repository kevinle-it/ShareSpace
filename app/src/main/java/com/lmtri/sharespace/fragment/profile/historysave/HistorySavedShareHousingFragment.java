package com.lmtri.sharespace.fragment.profile.historysave;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.lmtri.sharespace.R;
import com.lmtri.sharespace.ShareSpaceApplication;
import com.lmtri.sharespace.adapter.profiletab.historysave.HistorySavedShareHousingRecyclerViewAdapter;
import com.lmtri.sharespace.api.model.SavedShareHousing;
import com.lmtri.sharespace.api.service.RetrofitClient;
import com.lmtri.sharespace.api.service.user.UserClient;
import com.lmtri.sharespace.api.service.user.save.sharehousing.IGetMoreOlderSavedShareHousingsCallback;
import com.lmtri.sharespace.api.service.user.save.sharehousing.IGetNumOfSavedShareHousingsCallback;
import com.lmtri.sharespace.helper.Constants;
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
 * Use the {@link HistorySavedShareHousingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistorySavedShareHousingFragment extends Fragment {

    public static final String TAG = HistorySavedShareHousingFragment.class.getSimpleName();

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private LinearLayout mConnectionErrorLayout;
    private TextView mConnectionErrorMessage;
    private ViewSwitcher mViewSwitcherRetryConnect;
    private FrameLayout mRetryConnectButton;

    //    private SwipeRefreshLayout mSwipeRefreshContainer;
    private RecyclerView mRecyclerView;
    private OnShareHousingListInteractionListener mListener;
    private EndlessRecyclerViewScrollListener mRecyclerViewScrollListener;

    private int mNumOfSavedShareHousings = 0;
    private List<SavedShareHousing> mSavedShareHousings = new ArrayList<>();
    private HistorySavedShareHousingRecyclerViewAdapter mHistorySavedShareHousingRecyclerViewAdapter;

    public HistorySavedShareHousingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HistorySavedShareHousingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistorySavedShareHousingFragment newInstance(int columnCount) {
        HistorySavedShareHousingFragment fragment = new HistorySavedShareHousingFragment();
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
        View view = inflater.inflate(R.layout.fragment_history_saved_share_housing_list, container, false);

        mConnectionErrorLayout = (LinearLayout) view.findViewById(R.id.fragment_history_saved_share_housing_list_connection_error_layout);
        mConnectionErrorMessage = (TextView) view.findViewById(R.id.fragment_history_saved_share_housing_list_connection_error_message);
        mViewSwitcherRetryConnect = (ViewSwitcher) view.findViewById(R.id.fragment_history_saved_share_housing_list_view_switcher_retry_connect_to_server);
        mRetryConnectButton = (FrameLayout) view.findViewById(R.id.fragment_history_saved_share_housing_list_retry_button);
        mRetryConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewSwitcherRetryConnect.showNext();
                loadMoreOlderSavedShareHousingsFromApi(0);
            }
        });
        if (Constants.CURRENT_USER == null) {
            mConnectionErrorLayout.setVisibility(View.GONE);
        }

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

//        mSwipeRefreshContainer = (SwipeRefreshLayout) view.findViewById(R.id.fragment_saved_share_housing_tab_swipe_refresh_container);
//        mSwipeRefreshContainer.setColorSchemeResources(
//                R.color.colorPrimary,
//                R.color.colorPrimaryLight,
//                R.color.colorSecondary
//        );
//        mSwipeRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                if (mSavedShareHousings != null && mSavedShareHousings.size() > 0) {
//                    if (Constants.CURRENT_USER != null) {
//                        String s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(mSavedShareHousings.get(0).getDateTimeCreated());
//                        s = s.substring(0, s.length() - 2) + ":" + s.substring(s.length() - 2, s.length());
//                        loadMoreNewerSavedShareHousingsFromApi(s);
//                    }
//                } else {
//                    if (Constants.CURRENT_USER != null) {
//                        loadMoreOlderSavedShareHousingsFromApi(0);
//                    }
//                }
//            }
//        });
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_history_saved_share_housing_list_share_list);
        // Set the adapter
        if (mColumnCount <= 1) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                    linearLayoutManager.getOrientation());

            mRecyclerView.setLayoutManager(linearLayoutManager);
//            mRecyclerView.addItemDecoration(dividerItemDecoration);
            // Retain an instance so that you can call `resetState()` for fresh searches
            mRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager, false) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    // Triggered only when new data needs to be appended to the list
                    // Add whatever code is needed to append new items to the bottom of the list
                    if (Constants.CURRENT_USER != null) {
                        loadMoreOlderSavedShareHousingsFromApi(page);
                    }
                }
            };
        } else {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), mColumnCount);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                    gridLayoutManager.getOrientation());

            mRecyclerView.setLayoutManager(gridLayoutManager);
//            mRecyclerView.addItemDecoration(dividerItemDecoration);
            // Retain an instance so that you can call `resetState()` for fresh searches
            mRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager, false) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    // Triggered only when new data needs to be appended to the list
                    // Add whatever code is needed to append new items to the bottom of the list
                    if (Constants.CURRENT_USER != null) {
                        loadMoreOlderSavedShareHousingsFromApi(page);
                    }
                }
            };
        }

        mHistorySavedShareHousingRecyclerViewAdapter = new HistorySavedShareHousingRecyclerViewAdapter(getContext(), mSavedShareHousings, mListener);
        mRecyclerView.setAdapter(mHistorySavedShareHousingRecyclerViewAdapter);
        mRecyclerView.addOnScrollListener(mRecyclerViewScrollListener);

        if (Constants.CURRENT_USER != null) {
            UserClient.getNumOfSavedShareHousings(new IGetNumOfSavedShareHousingsCallback() {
                @Override
                public void onGetComplete(Integer numOfSavedShareHousings) {
                    if (numOfSavedShareHousings != null && numOfSavedShareHousings > 0) {
                        mNumOfSavedShareHousings = numOfSavedShareHousings;
                        loadMoreOlderSavedShareHousingsFromApi(0);
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

        return view;
    }

    @Subscribe
    public void saveShareHousing(SaveShareHousingEvent event) {
        if (event.getSavedShareHousing() != null) {
            if (mConnectionErrorLayout.getVisibility() == View.VISIBLE) {
                mConnectionErrorLayout.setVisibility(View.GONE);
            }
            if (mSavedShareHousings.isEmpty()) {
                mNumOfSavedShareHousings = 1;
                mSavedShareHousings.add(0, event.getSavedShareHousing());
                mHistorySavedShareHousingRecyclerViewAdapter.notifyItemInserted(0);
            } else {
                ++mNumOfSavedShareHousings;
                mSavedShareHousings.add(0, event.getSavedShareHousing());
                mHistorySavedShareHousingRecyclerViewAdapter.notifyItemInserted(0);
            }
        }
    }

    @Subscribe
    public void unsaveShareHousing(UnsaveShareHousingEvent event) {
        if (event.getSavedShareHousing() != null) {
            for (int i = 0; i < mSavedShareHousings.size(); ++i) {
                if (event.getSavedShareHousing().getSavedShareHousing().getID()
                        == mSavedShareHousings.get(i).getSavedShareHousing().getID()) {
                    --mNumOfSavedShareHousings;
                    mSavedShareHousings.remove(i);
                    mHistorySavedShareHousingRecyclerViewAdapter.notifyItemRemoved(i);
                    break;
                }
            }
            if (mSavedShareHousings.isEmpty()) {
                mConnectionErrorLayout.setVisibility(View.VISIBLE);
                if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                    mViewSwitcherRetryConnect.showNext();
                }
            }
        }
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadMoreOlderSavedShareHousingsFromApi(final int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
        String currentBottomSavedShareHousingDateTimeCreated
                = !mSavedShareHousings.isEmpty()
                ? new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(mSavedShareHousings.get(mSavedShareHousings.size() - 1).getDateTimeCreated())
                : null;
        if (currentBottomSavedShareHousingDateTimeCreated != null) {
            currentBottomSavedShareHousingDateTimeCreated
                    = currentBottomSavedShareHousingDateTimeCreated.substring(0, currentBottomSavedShareHousingDateTimeCreated.length() - 2)
                    + ":" + currentBottomSavedShareHousingDateTimeCreated.substring(currentBottomSavedShareHousingDateTimeCreated.length() - 2, currentBottomSavedShareHousingDateTimeCreated.length());
        }
        UserClient.getMoreOlderSavedShareHousings(currentBottomSavedShareHousingDateTimeCreated,
                new IGetMoreOlderSavedShareHousingsCallback() {
                    @Override
                    public void onGetComplete(List<SavedShareHousing> olderSavedShareHousings) {
//                        mSwipeRefreshContainer.setRefreshing(false);
                        if (mSavedShareHousings.size() == 0) {
                            if (olderSavedShareHousings != null && olderSavedShareHousings.size() > 0) {
                                if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                                    mViewSwitcherRetryConnect.showNext();
                                }
                                mConnectionErrorLayout.setVisibility(View.GONE);

                                int positionStart = mSavedShareHousings.size();

                                mSavedShareHousings.addAll(olderSavedShareHousings);

                                mHistorySavedShareHousingRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mSavedShareHousings.size() - positionStart);

                                if (mSavedShareHousings.size() < mNumOfSavedShareHousings) {
                                    loadMoreOlderSavedShareHousingsFromApi(1);
                                }
                            } else {
//                                mConnectionErrorMessage.setVisibility(View.VISIBLE);
                                if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                                    mViewSwitcherRetryConnect.showNext();
                                }
//                                RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext());
                            }
                        } else if (mSavedShareHousings.size() > 0) {
                            if (olderSavedShareHousings != null && olderSavedShareHousings.size() > 0) {
                                int positionStart = mSavedShareHousings.size();

                                mSavedShareHousings.addAll(olderSavedShareHousings);

                                mHistorySavedShareHousingRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mSavedShareHousings.size() - positionStart);

                                if (mSavedShareHousings.size() < mNumOfSavedShareHousings) {
                                    loadMoreOlderSavedShareHousingsFromApi(1);
                                }
                            }
//                            else {
//                                RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext());
//                            }
                        }
                    }

                    @Override
                    public void onGetFailure(Throwable t) {
//                        mSwipeRefreshContainer.setRefreshing(false);

//                        mConnectionErrorMessage.setVisibility(View.VISIBLE);
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
}
