package com.lmtri.sharespace.fragment.profile.historypost;


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
import com.lmtri.sharespace.adapter.profiletab.historypost.HistoryShareHousingRecyclerViewAdapter;
import com.lmtri.sharespace.api.model.ShareHousing;
import com.lmtri.sharespace.api.service.RetrofitClient;
import com.lmtri.sharespace.api.service.user.UserClient;
import com.lmtri.sharespace.api.service.user.post.sharehousing.IGetMoreOlderPostedShareHousingsCallback;
import com.lmtri.sharespace.api.service.user.post.sharehousing.IGetNumOfPostedShareHousingsCallback;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.busevent.sharehousing.post.DeleteShareHousingEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.post.PostShareHousingEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.post.UpdateShareHousingEvent;
import com.lmtri.sharespace.listener.EndlessRecyclerViewScrollListener;
import com.lmtri.sharespace.listener.OnShareHousingListInteractionListener;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryShareHousingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryShareHousingFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private LinearLayout mConnectionErrorLayout;
    private TextView mConnectionErrorMessage;
    private ViewSwitcher mViewSwitcherRetryConnect;
    private FrameLayout mRetryConnectButton;

    private RecyclerView mRecyclerView;
    private OnShareHousingListInteractionListener mListener;
    private EndlessRecyclerViewScrollListener mRecyclerViewScrollListener;

    private int mNumOfPostedShareHousings = 0;
    private ArrayList<ShareHousing> mShareHousings = new ArrayList<>();
    private HistoryShareHousingRecyclerViewAdapter mHistoryShareHousingRecyclerViewAdapter;


    public HistoryShareHousingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HistoryShareHousingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryShareHousingFragment newInstance(int columnCount) {
        HistoryShareHousingFragment fragment = new HistoryShareHousingFragment();
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
        View view = inflater.inflate(R.layout.fragment_history_share_housing_list, container, false);

        mConnectionErrorLayout = (LinearLayout) view.findViewById(R.id.fragment_history_share_housing_list_connection_error_layout);
        mConnectionErrorMessage = (TextView) view.findViewById(R.id.fragment_history_share_housing_list_connection_error_message);
        mViewSwitcherRetryConnect = (ViewSwitcher) view.findViewById(R.id.fragment_history_share_housing_list_view_switcher_retry_connect_to_server);
        mRetryConnectButton = (FrameLayout) view.findViewById(R.id.fragment_history_share_housing_list_retry_button);
        mRetryConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewSwitcherRetryConnect.showNext();
                loadMoreOlderPostedShareHousingsFromApi(0);
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

        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_history_share_housing_list_share_list);
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
                        loadMoreOlderPostedShareHousingsFromApi(page);
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
                        loadMoreOlderPostedShareHousingsFromApi(page);
                    }
                }
            };
        }

        mHistoryShareHousingRecyclerViewAdapter = new HistoryShareHousingRecyclerViewAdapter(getContext(), mShareHousings, mListener);
        mRecyclerView.setAdapter(mHistoryShareHousingRecyclerViewAdapter);
        mRecyclerView.addOnScrollListener(mRecyclerViewScrollListener);

        if (Constants.CURRENT_USER != null) {
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

        return  view;
    }

    @Subscribe
    public void shareHousingPosted(PostShareHousingEvent event) {
        if (event.getShareHousing() != null) {
            if (mConnectionErrorLayout.getVisibility() == View.VISIBLE) {
                mConnectionErrorLayout.setVisibility(View.GONE);
            }
            if (mShareHousings.isEmpty()) {
                mNumOfPostedShareHousings = 1;
                mShareHousings.add(0, event.getShareHousing());
                mHistoryShareHousingRecyclerViewAdapter.notifyItemInserted(0);
            } else {
                ++mNumOfPostedShareHousings;
                mShareHousings.add(0, event.getShareHousing());
                mHistoryShareHousingRecyclerViewAdapter.notifyItemInserted(0);
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
                    mHistoryShareHousingRecyclerViewAdapter.notifyItemRemoved(i);
                    mHistoryShareHousingRecyclerViewAdapter.notifyItemInserted(0);
                    break;
                }
            }
        }
    }

    @Subscribe
    public void shareHousingDeleted(DeleteShareHousingEvent event) {
        if (event.getShareHousing() != null) {
            for (int i = 0; i < mShareHousings.size(); ++i) {
                if (event.getShareHousing().getID() == mShareHousings.get(i).getID()) {
                    mShareHousings.remove(i);
                    mHistoryShareHousingRecyclerViewAdapter.notifyItemRemoved(i);
                    break;
                }
            }
            if (mShareHousings.isEmpty()) {
                mConnectionErrorLayout.setVisibility(View.VISIBLE);
                if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                    mViewSwitcherRetryConnect.showNext();
                }
            }
        }
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadMoreOlderPostedShareHousingsFromApi(int offset) {
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
        UserClient.getMoreOlderPostedShareHousings(currentBottomShareHousingDateTimeCreated, new IGetMoreOlderPostedShareHousingsCallback() {
            @Override
            public void onGetComplete(List<ShareHousing> olderPostedShareHousings) {
                if (mShareHousings.isEmpty()) {
                    if (olderPostedShareHousings != null && olderPostedShareHousings.size() > 0) {
                        if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                            mViewSwitcherRetryConnect.showNext();
                        }
                        mConnectionErrorLayout.setVisibility(View.GONE);

                        int positionStart = mShareHousings.size();

                        mShareHousings.addAll(olderPostedShareHousings);

                        mHistoryShareHousingRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mShareHousings.size() - positionStart);

                        if (mShareHousings.size() < mNumOfPostedShareHousings) {
                            loadMoreOlderPostedShareHousingsFromApi(1);
                        }
                    } else {
                        if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                            mViewSwitcherRetryConnect.showNext();
                        }
//                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext());
                    }
                } else if (!mShareHousings.isEmpty()) {
                    if (olderPostedShareHousings != null && olderPostedShareHousings.size() > 0) {
                        int positionStart = mShareHousings.size();

                        mShareHousings.addAll(olderPostedShareHousings);

                        mHistoryShareHousingRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mShareHousings.size() - positionStart);

                        if (mShareHousings.size() < mNumOfPostedShareHousings) {
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
