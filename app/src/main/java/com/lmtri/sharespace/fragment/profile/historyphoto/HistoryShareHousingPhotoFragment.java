package com.lmtri.sharespace.fragment.profile.historyphoto;


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
import com.lmtri.sharespace.adapter.profiletab.historyphoto.HistoryShareHousingPhotoRecyclerViewAdapter;
import com.lmtri.sharespace.api.model.HistoryShareHousingPhoto;
import com.lmtri.sharespace.api.service.RetrofitClient;
import com.lmtri.sharespace.api.service.user.UserClient;
import com.lmtri.sharespace.api.service.user.photo.sharehousing.IGetMoreOlderShareHousingsWithPhotosCallback;
import com.lmtri.sharespace.api.service.user.photo.sharehousing.IGetNumOfShareHousingsWithPhotosCallback;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.busevent.sharehousing.photo.DeleteShareHousingPhotoEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.photo.TakeShareHousingPhotoEvent;
import com.lmtri.sharespace.listener.EndlessRecyclerViewScrollListener;
import com.lmtri.sharespace.listener.OnShareHousingListInteractionListener;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryShareHousingPhotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryShareHousingPhotoFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private LinearLayout mConnectionErrorLayout;
    private TextView mConnectionErrorMessage;
    private ViewSwitcher mViewSwitcherRetryConnect;
    private FrameLayout mRetryConnectButton;

    private RecyclerView mRecyclerView;
    private OnShareHousingListInteractionListener mListener;
    private EndlessRecyclerViewScrollListener mRecyclerViewScrollListener;

    private int mNumOfHistoryShareHousingPhotos = 0;
    private ArrayList<HistoryShareHousingPhoto> mHistoryShareHousingPhotos = new ArrayList<>();
    private HistoryShareHousingPhotoRecyclerViewAdapter mHistoryShareHousingPhotoRecyclerViewAdapter;


    public HistoryShareHousingPhotoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HistoryShareHousingPhotoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryShareHousingPhotoFragment newInstance(int columnCount) {
        HistoryShareHousingPhotoFragment fragment = new HistoryShareHousingPhotoFragment();
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
        View view = inflater.inflate(R.layout.fragment_history_share_housing_photo_list, container, false);

        mConnectionErrorLayout = (LinearLayout) view.findViewById(R.id.fragment_history_share_housing_photo_list_connection_error_layout);
        mConnectionErrorMessage = (TextView) view.findViewById(R.id.fragment_history_share_housing_photo_list_connection_error_message);
        mViewSwitcherRetryConnect = (ViewSwitcher) view.findViewById(R.id.fragment_history_share_housing_photo_list_view_switcher_retry_connect_to_server);
        mRetryConnectButton = (FrameLayout) view.findViewById(R.id.fragment_history_share_housing_photo_list_retry_button);
        mRetryConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewSwitcherRetryConnect.showNext();
                loadMoreOlderShareHousingsWithPhotosFromApi(0);
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

        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_history_share_housing_photo_list_share_list);
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
                        loadMoreOlderShareHousingsWithPhotosFromApi(page);
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
                        loadMoreOlderShareHousingsWithPhotosFromApi(page);
                    }
                }
            };
        }

        mHistoryShareHousingPhotoRecyclerViewAdapter = new HistoryShareHousingPhotoRecyclerViewAdapter(getContext(), mHistoryShareHousingPhotos, mListener);
        mRecyclerView.setAdapter(mHistoryShareHousingPhotoRecyclerViewAdapter);
        mRecyclerView.addOnScrollListener(mRecyclerViewScrollListener);

        if (Constants.CURRENT_USER != null) {
            UserClient.getNumOfShareHousingsWithPhotos(new IGetNumOfShareHousingsWithPhotosCallback() {
                @Override
                public void onGetComplete(Integer numOfShareHousingsWithPhotos) {
                    if (numOfShareHousingsWithPhotos != null && numOfShareHousingsWithPhotos > 0) {
                        mNumOfHistoryShareHousingPhotos = numOfShareHousingsWithPhotos;
                        loadMoreOlderShareHousingsWithPhotosFromApi(0);
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
    public void takeShareHousingPhoto(TakeShareHousingPhotoEvent event) {
        if (event.getHistoryShareHousingPhoto() != null) {
            if (mConnectionErrorLayout.getVisibility() == View.VISIBLE) {
                mConnectionErrorLayout.setVisibility(View.GONE);
            }
            if (mHistoryShareHousingPhotos.isEmpty()) {
                mNumOfHistoryShareHousingPhotos = 1;
                mHistoryShareHousingPhotos.add(0, event.getHistoryShareHousingPhoto());
                mHistoryShareHousingPhotoRecyclerViewAdapter.notifyItemInserted(0);
            } else {
                ++mNumOfHistoryShareHousingPhotos;
                mHistoryShareHousingPhotos.add(0, event.getHistoryShareHousingPhoto());
                mHistoryShareHousingPhotoRecyclerViewAdapter.notifyItemInserted(0);
            }
        }
    }

    @Subscribe
    public void deleteHousingPhoto(DeleteShareHousingPhotoEvent event) {
        if (event.getHistoryShareHousingPhoto() != null) {
            for (int i = 0; i < mHistoryShareHousingPhotos.size(); ++i) {
                if (event.getHistoryShareHousingPhoto().getShareHousing().getID()
                        == mHistoryShareHousingPhotos.get(i).getShareHousing().getID()) {
                    --mNumOfHistoryShareHousingPhotos;
                    mHistoryShareHousingPhotos.remove(i);
                    mHistoryShareHousingPhotoRecyclerViewAdapter.notifyItemRemoved(i);
                    break;
                }
            }
            if (mHistoryShareHousingPhotos.isEmpty()) {
                mConnectionErrorLayout.setVisibility(View.VISIBLE);
                if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                    mViewSwitcherRetryConnect.showNext();
                }
            }
        }
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadMoreOlderShareHousingsWithPhotosFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
//        int currentBottomShareHousingID = !mHistoryShareHousingPhotos.isEmpty() ? mHistoryShareHousingPhotos.get(mHistoryShareHousingPhotos.size() - 1).getID() : -1;
        String currentBottomShareHousingPhotoDateTimeCreated
                = !mHistoryShareHousingPhotos.isEmpty()
                ? new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(mHistoryShareHousingPhotos.get(mHistoryShareHousingPhotos.size() - 1).getDateTimeCreated())
                : null;
        if (currentBottomShareHousingPhotoDateTimeCreated != null) {
            currentBottomShareHousingPhotoDateTimeCreated
                    = currentBottomShareHousingPhotoDateTimeCreated.substring(0, currentBottomShareHousingPhotoDateTimeCreated.length() - 2)
                    + ":" + currentBottomShareHousingPhotoDateTimeCreated.substring(currentBottomShareHousingPhotoDateTimeCreated.length() - 2, currentBottomShareHousingPhotoDateTimeCreated.length());
        }
        UserClient.getMoreOlderShareHousingsWithPhotos(currentBottomShareHousingPhotoDateTimeCreated, new IGetMoreOlderShareHousingsWithPhotosCallback() {
            @Override
            public void onGetComplete(List<HistoryShareHousingPhoto> olderShareHousingsWithPhotos) {
                if (mHistoryShareHousingPhotos.isEmpty()) {
                    if (olderShareHousingsWithPhotos != null && olderShareHousingsWithPhotos.size() > 0) {
                        if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                            mViewSwitcherRetryConnect.showNext();
                        }
                        mConnectionErrorLayout.setVisibility(View.GONE);

                        int positionStart = mHistoryShareHousingPhotos.size();

                        mHistoryShareHousingPhotos.addAll(olderShareHousingsWithPhotos);

                        mHistoryShareHousingPhotoRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mHistoryShareHousingPhotos.size() - positionStart);

                        if (mHistoryShareHousingPhotos.size() < mNumOfHistoryShareHousingPhotos) {
                            loadMoreOlderShareHousingsWithPhotosFromApi(1);
                        }
                    } else {
                        if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                            mViewSwitcherRetryConnect.showNext();
                        }
//                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext());
                    }
                } else if (!mHistoryShareHousingPhotos.isEmpty()) {
                    if (olderShareHousingsWithPhotos != null && olderShareHousingsWithPhotos.size() > 0) {
                        int positionStart = mHistoryShareHousingPhotos.size();

                        mHistoryShareHousingPhotos.addAll(olderShareHousingsWithPhotos);

                        mHistoryShareHousingPhotoRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mHistoryShareHousingPhotos.size() - positionStart);

                        if (mHistoryShareHousingPhotos.size() < mNumOfHistoryShareHousingPhotos) {
                            loadMoreOlderShareHousingsWithPhotosFromApi(1);
                        }
                    }
//                    else {
//                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext());
//                    }
                }
            }

            @Override
            public void onGetFailure(Throwable t) {

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
}
