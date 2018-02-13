package com.lmtri.sharespace.fragment.interested;

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
import com.lmtri.sharespace.adapter.interestedtab.InterestedShareHousingRecyclerViewAdapter;
import com.lmtri.sharespace.api.model.HistoryShareHousingNote;
import com.lmtri.sharespace.api.model.HistoryShareHousingPhoto;
import com.lmtri.sharespace.api.model.SavedShareHousing;
import com.lmtri.sharespace.api.service.RetrofitClient;
import com.lmtri.sharespace.api.service.user.UserClient;
import com.lmtri.sharespace.api.service.user.note.sharehousing.IGetMoreOlderShareHousingsWithNotesCallback;
import com.lmtri.sharespace.api.service.user.note.sharehousing.IGetNumOfShareHousingsWithNotesCallback;
import com.lmtri.sharespace.api.service.user.photo.sharehousing.IGetMoreOlderShareHousingsWithPhotosCallback;
import com.lmtri.sharespace.api.service.user.photo.sharehousing.IGetNumOfShareHousingsWithPhotosCallback;
import com.lmtri.sharespace.api.service.user.save.sharehousing.IGetMoreOlderSavedShareHousingsCallback;
import com.lmtri.sharespace.api.service.user.save.sharehousing.IGetNumOfSavedShareHousingsCallback;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.busevent.SigninEvent;
import com.lmtri.sharespace.helper.busevent.SignoutEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.note.DeleteShareHousingNoteEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.photo.DeleteShareHousingPhotoEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.save.SaveShareHousingEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.note.TakeShareHousingNoteEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.photo.TakeShareHousingPhotoEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.save.UnsaveShareHousingEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.note.UpdateShareHousingNoteEvent;
import com.lmtri.sharespace.listener.EndlessRecyclerViewScrollListener;
import com.lmtri.sharespace.listener.OnShareHousingListInteractionListener;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnShareHousingListInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InterestedShareHousingTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InterestedShareHousingTabFragment extends Fragment {

    public static final String TAG = InterestedShareHousingTabFragment.class.getSimpleName();

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
    private int mNumOfHistoryShareHousingNotes = 0;
    private List<HistoryShareHousingNote> mHistoryShareHousingNotes = new ArrayList<>();
    private int mNumOfHistoryShareHousingPhotos = 0;
    private List<HistoryShareHousingPhoto> mHistoryShareHousingPhotos = new ArrayList<>();
    private InterestedShareHousingRecyclerViewAdapter mInterestedShareHousingRecyclerViewAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public InterestedShareHousingTabFragment() {
        // Required empty public constructor
    }

    @SuppressWarnings("unused")
    public static InterestedShareHousingTabFragment newInstance(int columnCount) {
        InterestedShareHousingTabFragment fragment = new InterestedShareHousingTabFragment();
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
        View view = inflater.inflate(R.layout.fragment_interested_share_housing_tab, container, false);

        mConnectionErrorLayout = (LinearLayout) view.findViewById(R.id.fragment_interested_share_housing_tab_connection_error_layout);
        mConnectionErrorMessage = (TextView) view.findViewById(R.id.fragment_interested_share_housing_tab_connection_error_message);
        mViewSwitcherRetryConnect = (ViewSwitcher) view.findViewById(R.id.fragment_interested_share_housing_tab_view_switcher_retry_connect_to_server);
        mRetryConnectButton = (FrameLayout) view.findViewById(R.id.fragment_interested_share_housing_tab_retry_button);
        mRetryConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewSwitcherRetryConnect.showNext();
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
                UserClient.getNumOfShareHousingsWithNotes(new IGetNumOfShareHousingsWithNotesCallback() {
                    @Override
                    public void onGetComplete(Integer numOfShareHousingsWithNotes) {
                        if (numOfShareHousingsWithNotes != null && numOfShareHousingsWithNotes > 0) {
                            mNumOfHistoryShareHousingNotes = numOfShareHousingsWithNotes;
                            loadMoreOlderShareHousingsWithNotesFromApi(0);
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
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_interested_share_housing_tab_share_list);
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
//                    if (Constants.CURRENT_USER != null) {
//                        loadMoreOlderSavedShareHousingsFromApi(page);
//                    }
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
//                    if (Constants.CURRENT_USER != null) {
//                        loadMoreOlderSavedShareHousingsFromApi(page);
//                    }
                }
            };
        }

        mInterestedShareHousingRecyclerViewAdapter = new InterestedShareHousingRecyclerViewAdapter(
                getContext(), mSavedShareHousings, mHistoryShareHousingNotes, mHistoryShareHousingPhotos, mListener
        );
        mRecyclerView.setAdapter(mInterestedShareHousingRecyclerViewAdapter);
        mRecyclerView.addOnScrollListener(mRecyclerViewScrollListener);

        return view;
    }

    @Subscribe
    public void userSignin(SigninEvent event) {
        if (mNumOfSavedShareHousings == 0 && mNumOfHistoryShareHousingNotes == 0 && mNumOfHistoryShareHousingPhotos == 0) {
            mConnectionErrorLayout.setVisibility(View.VISIBLE);
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
            UserClient.getNumOfShareHousingsWithNotes(new IGetNumOfShareHousingsWithNotesCallback() {
                @Override
                public void onGetComplete(Integer numOfShareHousingsWithNotes) {
                    if (numOfShareHousingsWithNotes != null && numOfShareHousingsWithNotes > 0) {
                        mNumOfHistoryShareHousingNotes = numOfShareHousingsWithNotes;
                        loadMoreOlderShareHousingsWithNotesFromApi(0);
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
    }

    @Subscribe
    public void userSignout(SignoutEvent event) {
        mConnectionErrorLayout.setVisibility(View.GONE);

        mNumOfSavedShareHousings = 0;
        mSavedShareHousings.clear();

        mNumOfHistoryShareHousingNotes = 0;
        mHistoryShareHousingNotes.clear();

        mNumOfHistoryShareHousingPhotos = 0;
        mHistoryShareHousingPhotos.clear();

        mInterestedShareHousingRecyclerViewAdapter.notifyDataSetChanged();

        mSavedShareHousings = new ArrayList<>();
        mHistoryShareHousingNotes = new ArrayList<>();
        mHistoryShareHousingPhotos = new ArrayList<>();

        mInterestedShareHousingRecyclerViewAdapter = new InterestedShareHousingRecyclerViewAdapter(
                getContext(), mSavedShareHousings, mHistoryShareHousingNotes, mHistoryShareHousingPhotos, mListener
        );
        mRecyclerView.setAdapter(mInterestedShareHousingRecyclerViewAdapter);
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
//                mInterestedShareHousingRecyclerViewAdapter.notifyItemInserted(0);
                mInterestedShareHousingRecyclerViewAdapter.notifyDataSetChanged();
            } else {
                ++mNumOfSavedShareHousings;
                mSavedShareHousings.add(0, event.getSavedShareHousing());
//                mInterestedShareHousingRecyclerViewAdapter.notifyItemInserted(0);
                mInterestedShareHousingRecyclerViewAdapter.notifyDataSetChanged();
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
//                    mInterestedShareHousingRecyclerViewAdapter.notifyItemRemoved(i);
                    mInterestedShareHousingRecyclerViewAdapter.notifyDataSetChanged();
                    break;
                }
            }
//            if (mNumOfSavedShareHousings == 0) {
//                mInterestedShareHousingRecyclerViewAdapter.notifyDataSetChanged();
//            } else
            if (mNumOfSavedShareHousings > 0) {
                loadMoreOlderSavedShareHousingsFromApi(1);
            }
            if (mSavedShareHousings.isEmpty() && mHistoryShareHousingNotes.isEmpty() && mHistoryShareHousingPhotos.isEmpty()) {
                mConnectionErrorLayout.setVisibility(View.VISIBLE);
                if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                    mViewSwitcherRetryConnect.showNext();
                }
            }
        }
    }

    @Subscribe
    public void takeShareHousingNote(TakeShareHousingNoteEvent event) {
        if (event.getHistoryShareHousingNote() != null) {
            if (mConnectionErrorLayout.getVisibility() == View.VISIBLE) {
                mConnectionErrorLayout.setVisibility(View.GONE);
            }
            if (mHistoryShareHousingNotes.isEmpty()) {
                mNumOfHistoryShareHousingNotes = 1;
                mHistoryShareHousingNotes.add(0, event.getHistoryShareHousingNote());
                mInterestedShareHousingRecyclerViewAdapter.notifyDataSetChanged();
            } else {
                ++mNumOfHistoryShareHousingNotes;
                mHistoryShareHousingNotes.add(0, event.getHistoryShareHousingNote());
                mInterestedShareHousingRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    @Subscribe
    public void updateShareHousingNote(UpdateShareHousingNoteEvent event) {
        if (event.getHistoryShareHousingNote() != null) {
            for (int i = 0; i < mHistoryShareHousingNotes.size(); ++i) {
                if (mHistoryShareHousingNotes.get(i).getShareHousing().getID()
                        == event.getHistoryShareHousingNote().getShareHousing().getID()) {
                    mHistoryShareHousingNotes.remove(i);
                    mHistoryShareHousingNotes.add(0, event.getHistoryShareHousingNote());
//                    mInterestedShareHousingRecyclerViewAdapter.notifyItemRemoved(i);
//                    mInterestedShareHousingRecyclerViewAdapter.notifyItemInserted(0);
                    mInterestedShareHousingRecyclerViewAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    @Subscribe
    public void deleteShareHousingNote(DeleteShareHousingNoteEvent event) {
        if (event.getHistoryShareHousingNote() != null) {
            for (int i = 0; i < mHistoryShareHousingNotes.size(); ++i) {
                if (event.getHistoryShareHousingNote().getShareHousing().getID()
                        == mHistoryShareHousingNotes.get(i).getShareHousing().getID()) {
                    --mNumOfHistoryShareHousingNotes;
                    mHistoryShareHousingNotes.remove(i);
//                    mInterestedShareHousingRecyclerViewAdapter.notifyItemRemoved(i);
                    mInterestedShareHousingRecyclerViewAdapter.notifyDataSetChanged();
                    break;
                }
            }
//        if (mNumOfHistoryShareHousingNotes == 0) {
//            mInterestedShareHousingRecyclerViewAdapter.notifyDataSetChanged();
//        } else
            if (mNumOfHistoryShareHousingNotes > 0) {
                loadMoreOlderShareHousingsWithNotesFromApi(1);
            }
            if (mSavedShareHousings.isEmpty() && mHistoryShareHousingNotes.isEmpty() && mHistoryShareHousingPhotos.isEmpty()) {
                mConnectionErrorLayout.setVisibility(View.VISIBLE);
                if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                    mViewSwitcherRetryConnect.showNext();
                }
            }
        }
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
                mInterestedShareHousingRecyclerViewAdapter.notifyDataSetChanged();
            } else {
                ++mNumOfHistoryShareHousingPhotos;
                mHistoryShareHousingPhotos.add(0, event.getHistoryShareHousingPhoto());
                mInterestedShareHousingRecyclerViewAdapter.notifyDataSetChanged();
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
//                    mInterestedShareHousingRecyclerViewAdapter.notifyItemRemoved(i);
                    mInterestedShareHousingRecyclerViewAdapter.notifyDataSetChanged();
                    break;
                }
            }
//        if (mNumOfHistoryShareHousingPhotos == 0) {
//            mInterestedShareHousingRecyclerViewAdapter.notifyDataSetChanged();
//        } else
            if (mNumOfHistoryShareHousingPhotos > 0) {
                loadMoreOlderShareHousingsWithPhotosFromApi(1);
            }
            if (mSavedShareHousings.isEmpty() && mHistoryShareHousingNotes.isEmpty() && mHistoryShareHousingPhotos.isEmpty()) {
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
                        if (mSavedShareHousings.isEmpty()) {
                            if (olderSavedShareHousings != null && olderSavedShareHousings.size() > 0) {
                                if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                                    mViewSwitcherRetryConnect.showNext();
                                }
                                mConnectionErrorLayout.setVisibility(View.GONE);

//                                int positionStart = mSavedShareHousings.size();

                                for (int i = 0; i < olderSavedShareHousings.size(); ++i) {
                                    if (mSavedShareHousings.size() < 3) {
                                        mSavedShareHousings.add(olderSavedShareHousings.get(i));
                                    } else {
                                        break;
                                    }
                                }

//                                mInterestedShareHousingRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mSavedShareHousings.size() - positionStart);
                                mInterestedShareHousingRecyclerViewAdapter.notifyDataSetChanged();

                                if (mSavedShareHousings.size() < 3 && mSavedShareHousings.size() < mNumOfSavedShareHousings) {
                                    loadMoreOlderSavedShareHousingsFromApi(1);
                                }
                            } else {
//                                mConnectionErrorMessage.setVisibility(View.VISIBLE);
                                if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                                    mViewSwitcherRetryConnect.showNext();
                                }
//                                RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext());
                            }
                        } else if (!mSavedShareHousings.isEmpty()) {
                            if (olderSavedShareHousings != null && olderSavedShareHousings.size() > 0) {
//                                int positionStart = mSavedShareHousings.size();

                                for (int i = 0; i < olderSavedShareHousings.size(); ++i) {
                                    if (mSavedShareHousings.size() < 3) {
                                        mSavedShareHousings.add(olderSavedShareHousings.get(i));
                                    } else {
                                        break;
                                    }
                                }

//                                mInterestedShareHousingRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mSavedShareHousings.size() - positionStart);
                                mInterestedShareHousingRecyclerViewAdapter.notifyDataSetChanged();

                                if (mSavedShareHousings.size() < 3 && mSavedShareHousings.size() < mNumOfSavedShareHousings) {
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

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadMoreOlderShareHousingsWithNotesFromApi(final int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
//        int currentBottomHousingID = !mHistoryHousingNotes.isEmpty() ? mHistoryHousingNotes.get(mHistoryHousingNotes.size() - 1).getID() : -1;
        String currentBottomShareHousingNoteDateTimeCreated
                = !mHistoryShareHousingNotes.isEmpty()
                ? new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(mHistoryShareHousingNotes.get(mHistoryShareHousingNotes.size() - 1).getDateTimeCreated())
                : null;
        if (currentBottomShareHousingNoteDateTimeCreated != null) {
            currentBottomShareHousingNoteDateTimeCreated
                    = currentBottomShareHousingNoteDateTimeCreated.substring(0, currentBottomShareHousingNoteDateTimeCreated.length() - 2)
                    + ":" + currentBottomShareHousingNoteDateTimeCreated.substring(currentBottomShareHousingNoteDateTimeCreated.length() - 2, currentBottomShareHousingNoteDateTimeCreated.length());
        }
        UserClient.getMoreOlderShareHousingsWithNotes(currentBottomShareHousingNoteDateTimeCreated, new IGetMoreOlderShareHousingsWithNotesCallback() {
            @Override
            public void onGetComplete(List<HistoryShareHousingNote> olderHousingsWithNotes) {
                if (mHistoryShareHousingNotes.isEmpty()) {
                    if (olderHousingsWithNotes != null && olderHousingsWithNotes.size() > 0) {
                        if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                            mViewSwitcherRetryConnect.showNext();
                        }
                        mConnectionErrorLayout.setVisibility(View.GONE);

//                        int positionStart = mHistoryHousingNotes.size();

                        for (int i = 0; i < olderHousingsWithNotes.size(); ++i) {
                            if (mHistoryShareHousingNotes.size() < 3) {
                                mHistoryShareHousingNotes.add(olderHousingsWithNotes.get(i));
                            } else {
                                break;
                            }
                        }

//                        mInterestedHousingRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mHistoryHousingNotes.size() - positionStart);
                        mInterestedShareHousingRecyclerViewAdapter.notifyDataSetChanged();

                        if (mHistoryShareHousingNotes.size() < 3 && mHistoryShareHousingNotes.size() < mNumOfHistoryShareHousingNotes) {
                            loadMoreOlderShareHousingsWithNotesFromApi(1);
                        }
                    } else {
                        if (mSavedShareHousings.isEmpty() && mHistoryShareHousingPhotos.isEmpty()) {
                            if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                                mViewSwitcherRetryConnect.showNext();
                            }
                        }
//                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext());
                    }
                } else if (!mHistoryShareHousingNotes.isEmpty()) {
                    if (olderHousingsWithNotes != null && olderHousingsWithNotes.size() > 0) {
//                        int positionStart = mHistoryHousingNotes.size();

                        for (int i = 0; i < olderHousingsWithNotes.size(); ++i) {
                            if (mHistoryShareHousingNotes.size() < 3) {
                                mHistoryShareHousingNotes.add(olderHousingsWithNotes.get(i));
                            } else {
                                break;
                            }
                        }

//                        mInterestedHousingRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mHistoryHousingNotes.size() - positionStart);
                        mInterestedShareHousingRecyclerViewAdapter.notifyDataSetChanged();

                        if (mHistoryShareHousingNotes.size() < 3 && mHistoryShareHousingNotes.size() < mNumOfHistoryShareHousingNotes) {
                            loadMoreOlderShareHousingsWithNotesFromApi(1);
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

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadMoreOlderShareHousingsWithPhotosFromApi(final int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
//        int currentBottomHousingID = !mHistoryHousingPhotos.isEmpty() ? mHistoryHousingPhotos.get(mHistoryHousingPhotos.size() - 1).getID() : -1;
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
            public void onGetComplete(List<HistoryShareHousingPhoto> olderHousingsWithPhotos) {
                if (mHistoryShareHousingPhotos.isEmpty()) {
                    if (olderHousingsWithPhotos != null && olderHousingsWithPhotos.size() > 0) {
                        if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                            mViewSwitcherRetryConnect.showNext();
                        }
                        mConnectionErrorLayout.setVisibility(View.GONE);

//                        int positionStart = mHistoryHousingPhotos.size();

                        for (int i = 0; i < olderHousingsWithPhotos.size(); ++i) {
                            if (mHistoryShareHousingPhotos.size() < 3) {
                                mHistoryShareHousingPhotos.add(olderHousingsWithPhotos.get(i));
                            } else {
                                break;
                            }
                        }

//                        mInterestedHousingRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mHistoryHousingPhotos.size() - positionStart);
                        mInterestedShareHousingRecyclerViewAdapter.notifyDataSetChanged();

                        if (mHistoryShareHousingPhotos.size() < 3 && mHistoryShareHousingPhotos.size() < mNumOfHistoryShareHousingPhotos) {
                            loadMoreOlderShareHousingsWithPhotosFromApi(1);
                        }
                    } else {
                        if (mSavedShareHousings.isEmpty() && mHistoryShareHousingNotes.isEmpty()) {
                            if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                                mViewSwitcherRetryConnect.showNext();
                            }
                        }
//                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext());
                    }
                } else if (!mHistoryShareHousingPhotos.isEmpty()) {
                    if (olderHousingsWithPhotos != null && olderHousingsWithPhotos.size() > 0) {
//                        int positionStart = mHistoryHousingPhotos.size();

                        for (int i = 0; i < olderHousingsWithPhotos.size(); ++i) {
                            if (mHistoryShareHousingPhotos.size() < 3) {
                                mHistoryShareHousingPhotos.add(olderHousingsWithPhotos.get(i));
                            } else {
                                break;
                            }
                        }

//                        mInterestedHousingRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mHistoryHousingPhotos.size() - positionStart);
                        mInterestedShareHousingRecyclerViewAdapter.notifyDataSetChanged();

                        if (mHistoryShareHousingPhotos.size() < 3 && mHistoryShareHousingPhotos.size() < mNumOfHistoryShareHousingPhotos) {
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
//    public interface OnSavedSharePostListFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onSavedSharePostListFragmentInteraction(ShareHousing sharedHousing);
//    }
}
