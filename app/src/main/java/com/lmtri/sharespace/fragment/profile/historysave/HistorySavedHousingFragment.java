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
import com.lmtri.sharespace.adapter.profiletab.historysave.HistorySavedHousingRecyclerViewAdapter;
import com.lmtri.sharespace.api.model.SavedHousing;
import com.lmtri.sharespace.api.service.RetrofitClient;
import com.lmtri.sharespace.api.service.user.UserClient;
import com.lmtri.sharespace.api.service.user.save.housing.IGetMoreOlderSavedHousingsCallback;
import com.lmtri.sharespace.api.service.user.save.housing.IGetNumOfSavedHousingsCallback;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.busevent.housing.save.SaveHousingEvent;
import com.lmtri.sharespace.helper.busevent.housing.save.UnsaveHousingEvent;
import com.lmtri.sharespace.listener.EndlessRecyclerViewScrollListener;
import com.lmtri.sharespace.listener.OnHousingListInteractionListener;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistorySavedHousingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistorySavedHousingFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private LinearLayout mConnectionErrorLayout;
    private TextView mConnectionErrorMessage;
    private ViewSwitcher mViewSwitcherRetryConnect;
    private FrameLayout mRetryConnectButton;

    private RecyclerView mRecyclerView;
    private OnHousingListInteractionListener mListener;
    private EndlessRecyclerViewScrollListener mRecyclerViewScrollListener;

    private int mNumOfSavedHousings = 0;
    private ArrayList<SavedHousing> mSavedHousings = new ArrayList<>();
    private HistorySavedHousingRecyclerViewAdapter mHistorySavedHousingRecyclerViewAdapter;

    public HistorySavedHousingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HistorySavedHousingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistorySavedHousingFragment newInstance(int columnCount) {
        HistorySavedHousingFragment fragment = new HistorySavedHousingFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history_saved_housing_list, container, false);

        mConnectionErrorLayout = (LinearLayout) view.findViewById(R.id.fragment_history_saved_housing_list_connection_error_layout);
        mConnectionErrorMessage = (TextView) view.findViewById(R.id.fragment_history_saved_housing_list_connection_error_message);
        mViewSwitcherRetryConnect = (ViewSwitcher) view.findViewById(R.id.fragment_history_saved_housing_list_view_switcher_retry_connect_to_server);
        mRetryConnectButton = (FrameLayout) view.findViewById(R.id.fragment_history_saved_housing_list_retry_button);
        mRetryConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewSwitcherRetryConnect.showNext();
                loadMoreOlderSavedHousingsFromApi(0);
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

        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_history_saved_housing_list_home_list);
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
                        loadMoreOlderSavedHousingsFromApi(page);
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
                        loadMoreOlderSavedHousingsFromApi(page);
                    }
                }
            };
        }

        mHistorySavedHousingRecyclerViewAdapter = new HistorySavedHousingRecyclerViewAdapter(getContext(), mSavedHousings, mListener);
        mRecyclerView.setAdapter(mHistorySavedHousingRecyclerViewAdapter);
        mRecyclerView.addOnScrollListener(mRecyclerViewScrollListener);

        if (Constants.CURRENT_USER != null) {
            UserClient.getNumOfSavedHousings(new IGetNumOfSavedHousingsCallback() {
                @Override
                public void onGetComplete(Integer numOfSavedHousings) {
                    if (numOfSavedHousings != null && numOfSavedHousings > 0) {
                        mNumOfSavedHousings = numOfSavedHousings;
                        loadMoreOlderSavedHousingsFromApi(0);
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
    public void saveHousing(SaveHousingEvent event) {
        if (event.getSavedHousing() != null) {
            if (mConnectionErrorLayout.getVisibility() == View.VISIBLE) {
                mConnectionErrorLayout.setVisibility(View.GONE);
            }
            if (mSavedHousings.isEmpty()) {
                mNumOfSavedHousings = 1;
                mSavedHousings.add(0, event.getSavedHousing());
                mHistorySavedHousingRecyclerViewAdapter.notifyItemInserted(0);
            } else {
                ++mNumOfSavedHousings;
                mSavedHousings.add(0, event.getSavedHousing());
                mHistorySavedHousingRecyclerViewAdapter.notifyItemInserted(0);
            }
        }
    }

    @Subscribe
    public void unsaveHousing(UnsaveHousingEvent event) {
        if (event.getSavedHousing() != null) {
            for (int i = 0; i < mSavedHousings.size(); ++i) {
                if (event.getSavedHousing().getSavedHousing().getID()
                        == mSavedHousings.get(i).getSavedHousing().getID()) {
                    --mNumOfSavedHousings;
                    mSavedHousings.remove(i);
                    mHistorySavedHousingRecyclerViewAdapter.notifyItemRemoved(i);
                    break;
                }
            }
            if (mSavedHousings.isEmpty()) {
                mConnectionErrorLayout.setVisibility(View.VISIBLE);
                if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                    mViewSwitcherRetryConnect.showNext();
                }
            }
        }
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadMoreOlderSavedHousingsFromApi(final int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
        String currentBottomSavedHousingDateTimeCreated
                = !mSavedHousings.isEmpty()
                ? new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(mSavedHousings.get(mSavedHousings.size() - 1).getDateTimeCreated())
                : null;
        if (currentBottomSavedHousingDateTimeCreated != null) {
            currentBottomSavedHousingDateTimeCreated
                    = currentBottomSavedHousingDateTimeCreated.substring(0, currentBottomSavedHousingDateTimeCreated.length() - 2)
                    + ":" + currentBottomSavedHousingDateTimeCreated.substring(currentBottomSavedHousingDateTimeCreated.length() - 2, currentBottomSavedHousingDateTimeCreated.length());
        }
        UserClient.getMoreOlderSavedHousings(currentBottomSavedHousingDateTimeCreated, new IGetMoreOlderSavedHousingsCallback() {
            @Override
            public void onGetComplete(List<SavedHousing> olderSavedHousings) {
//                mSwipeRefreshContainer.setRefreshing(false);
                if (mSavedHousings.size() == 0) {
                    if (olderSavedHousings != null && olderSavedHousings.size() > 0) {
                        if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                            mViewSwitcherRetryConnect.showNext();
                        }
                        mConnectionErrorLayout.setVisibility(View.GONE);

                        int positionStart = mSavedHousings.size();

                        mSavedHousings.addAll(olderSavedHousings);

                        mHistorySavedHousingRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mSavedHousings.size() - positionStart);

                        if (mSavedHousings.size() < mNumOfSavedHousings) {
                            loadMoreOlderSavedHousingsFromApi(1);
                        }
                    } else {
//                        mConnectionErrorMessage.setVisibility(View.VISIBLE);
                        if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                            mViewSwitcherRetryConnect.showNext();
                        }
//                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext());
                    }
                } else if (mSavedHousings.size() > 0) {
                    if (olderSavedHousings != null && olderSavedHousings.size() > 0) {
                        int positionStart = mSavedHousings.size();

                        mSavedHousings.addAll(olderSavedHousings);

                        mHistorySavedHousingRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mSavedHousings.size() - positionStart);

                        if (mSavedHousings.size() < mNumOfSavedHousings) {
                            loadMoreOlderSavedHousingsFromApi(1);
                        }
                    }
//                    else {
//                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext());
//                    }
                }
            }

            @Override
            public void onGetFailure(Throwable t) {
//                mSwipeRefreshContainer.setRefreshing(false);

//                mConnectionErrorMessage.setVisibility(View.VISIBLE);
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
}
