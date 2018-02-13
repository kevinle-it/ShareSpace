package com.lmtri.sharespace.fragment.interested;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
import com.lmtri.sharespace.adapter.interestedtab.InterestedHousingRecyclerViewAdapter;
import com.lmtri.sharespace.api.model.HistoryHousingNote;
import com.lmtri.sharespace.api.model.HistoryHousingPhoto;
import com.lmtri.sharespace.api.model.SavedHousing;
import com.lmtri.sharespace.api.service.RetrofitClient;
import com.lmtri.sharespace.api.service.user.UserClient;
import com.lmtri.sharespace.api.service.user.note.housing.IGetMoreOlderHousingsWithNotesCallback;
import com.lmtri.sharespace.api.service.user.note.housing.IGetNumOfHousingsWithNotesCallback;
import com.lmtri.sharespace.api.service.user.photo.housing.IGetMoreOlderHousingsWithPhotosCallback;
import com.lmtri.sharespace.api.service.user.photo.housing.IGetNumOfHousingsWithPhotosCallback;
import com.lmtri.sharespace.api.service.user.save.housing.IGetMoreOlderSavedHousingsCallback;
import com.lmtri.sharespace.api.service.user.save.housing.IGetNumOfSavedHousingsCallback;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.busevent.SigninEvent;
import com.lmtri.sharespace.helper.busevent.SignoutEvent;
import com.lmtri.sharespace.helper.busevent.housing.HistoryPostSaveNotePhotoActivityPostShareOfExistHousingEvent;
import com.lmtri.sharespace.helper.busevent.housing.note.DeleteHousingNoteEvent;
import com.lmtri.sharespace.helper.busevent.housing.photo.DeleteHousingPhotoEvent;
import com.lmtri.sharespace.helper.busevent.housing.save.SaveHousingEvent;
import com.lmtri.sharespace.helper.busevent.housing.note.TakeHousingNoteEvent;
import com.lmtri.sharespace.helper.busevent.housing.photo.TakeHousingPhotoEvent;
import com.lmtri.sharespace.helper.busevent.housing.save.UnsaveHousingEvent;
import com.lmtri.sharespace.helper.busevent.housing.note.UpdateHousingNoteEvent;
import com.lmtri.sharespace.listener.EndlessRecyclerViewScrollListener;
import com.lmtri.sharespace.listener.OnHousingListInteractionListener;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InterestedHousingTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InterestedHousingTabFragment extends Fragment {

    public static final String TAG = InterestedHousingTabFragment.class.getSimpleName();

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private LinearLayout mConnectionErrorLayout;
    private TextView mConnectionErrorMessage;
    private ViewSwitcher mViewSwitcherRetryConnect;
    private FrameLayout mRetryConnectButton;

//    private SwipeRefreshLayout mSwipeRefreshContainer;
    private RecyclerView mRecyclerView;
    private OnHousingListInteractionListener mListener;
    private EndlessRecyclerViewScrollListener mRecyclerViewScrollListener;

    private int mNumOfSavedHousings = 0;
    private List<SavedHousing> mSavedHousings = new ArrayList<>();
    private int mNumOfHistoryHousingNotes = 0;
    private List<HistoryHousingNote> mHistoryHousingNotes = new ArrayList<>();
    private int mNumOfHistoryHousingPhotos = 0;
    private List<HistoryHousingPhoto> mHistoryHousingPhotos = new ArrayList<>();
    private InterestedHousingRecyclerViewAdapter mInterestedHousingRecyclerViewAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public InterestedHousingTabFragment() {
        // Required empty public constructor
    }

    @SuppressWarnings("unused")
    public static InterestedHousingTabFragment newInstance(int columnCount) {
        InterestedHousingTabFragment fragment = new InterestedHousingTabFragment();
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
        View view = inflater.inflate(R.layout.fragment_interested_housing_tab, container, false);

        mConnectionErrorLayout = (LinearLayout) view.findViewById(R.id.fragment_interested_housing_tab_connection_error_layout);
        mConnectionErrorMessage = (TextView) view.findViewById(R.id.fragment_interested_housing_tab_connection_error_message);
        mViewSwitcherRetryConnect = (ViewSwitcher) view.findViewById(R.id.fragment_interested_housing_tab_view_switcher_retry_connect_to_server);
        mRetryConnectButton = (FrameLayout) view.findViewById(R.id.fragment_interested_housing_tab_retry_button);
        mRetryConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewSwitcherRetryConnect.showNext();
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
                UserClient.getNumOfHousingsWithNotes(new IGetNumOfHousingsWithNotesCallback() {
                    @Override
                    public void onGetComplete(Integer numOfHousingsWithNotes) {
                        if (numOfHousingsWithNotes != null && numOfHousingsWithNotes > 0) {
                            mNumOfHistoryHousingNotes = numOfHousingsWithNotes;
                            loadMoreOlderHousingsWithNotesFromApi(0);
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
                UserClient.getNumOfHousingsWithPhotos(new IGetNumOfHousingsWithPhotosCallback() {
                    @Override
                    public void onGetComplete(Integer numOfHousingsWithPhotos) {
                        if (numOfHousingsWithPhotos != null && numOfHousingsWithPhotos > 0) {
                            mNumOfHistoryHousingPhotos = numOfHousingsWithPhotos;
                            loadMoreOlderHousingsWithPhotosFromApi(0);
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

//        mSwipeRefreshContainer = (SwipeRefreshLayout) view.findViewById(R.id.fragment_saved_housing_tab_swipe_refresh_container);
//        mSwipeRefreshContainer.setColorSchemeResources(
//                R.color.colorPrimary,
//                R.color.colorPrimaryLight,
//                R.color.colorSecondary
//        );
//        mSwipeRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                if (mSavedHousings != null && mSavedHousings.size() > 0) {
//                    if (Constants.CURRENT_USER != null) {
//                        String s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(mSavedHousings.get(0).getDateTimeCreated());
//                        s = s.substring(0, s.length() - 2) + ":" + s.substring(s.length() - 2, s.length());
//                        loadMoreNewerSavedHousingsFromApi(s);
//                    }
//                } else {
//                    if (Constants.CURRENT_USER != null) {
//                        loadMoreOlderSavedHousingsFromApi(0);
//                    }
//                }
//            }
//        });
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_interested_housing_tab_home_list);
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
//                        loadMoreOlderSavedHousingsFromApi(page);
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
//                        loadMoreOlderSavedHousingsFromApi(page);
//                    }
                }
            };
        }

        mInterestedHousingRecyclerViewAdapter = new InterestedHousingRecyclerViewAdapter(
                (AppCompatActivity) getActivity(), mSavedHousings, mHistoryHousingNotes, mHistoryHousingPhotos, mListener
        );
        mRecyclerView.setAdapter(mInterestedHousingRecyclerViewAdapter);
        mRecyclerView.addOnScrollListener(mRecyclerViewScrollListener);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.START_ACTIVITY_HISTORY_POST_SAVE_NOTE_PHOTO_REQUEST) {
            if (resultCode == getActivity().RESULT_OK) {
                ShareSpaceApplication.BUS.post(new HistoryPostSaveNotePhotoActivityPostShareOfExistHousingEvent());
            }
        }
    }

    @Subscribe
    public void userSignin(SigninEvent event) {
        if (mNumOfSavedHousings == 0 && mNumOfHistoryHousingNotes == 0 && mNumOfHistoryHousingPhotos == 0) {
            mConnectionErrorLayout.setVisibility(View.VISIBLE);
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
            UserClient.getNumOfHousingsWithNotes(new IGetNumOfHousingsWithNotesCallback() {
                @Override
                public void onGetComplete(Integer numOfHousingsWithNotes) {
                    if (numOfHousingsWithNotes != null && numOfHousingsWithNotes > 0) {
                        mNumOfHistoryHousingNotes = numOfHousingsWithNotes;
                        loadMoreOlderHousingsWithNotesFromApi(0);
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
            UserClient.getNumOfHousingsWithPhotos(new IGetNumOfHousingsWithPhotosCallback() {
                @Override
                public void onGetComplete(Integer numOfHousingsWithPhotos) {
                    if (numOfHousingsWithPhotos != null && numOfHousingsWithPhotos > 0) {
                        mNumOfHistoryHousingPhotos = numOfHousingsWithPhotos;
                        loadMoreOlderHousingsWithPhotosFromApi(0);
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

        mNumOfSavedHousings = 0;
        mSavedHousings.clear();

        mNumOfHistoryHousingNotes = 0;
        mHistoryHousingNotes.clear();

        mNumOfHistoryHousingPhotos = 0;
        mHistoryHousingPhotos.clear();

        mInterestedHousingRecyclerViewAdapter.notifyDataSetChanged();

        mSavedHousings = new ArrayList<>();
        mHistoryHousingNotes = new ArrayList<>();
        mHistoryHousingPhotos = new ArrayList<>();

        mInterestedHousingRecyclerViewAdapter = new InterestedHousingRecyclerViewAdapter(
                (AppCompatActivity) getActivity(), mSavedHousings, mHistoryHousingNotes, mHistoryHousingPhotos, mListener
        );
        mRecyclerView.setAdapter(mInterestedHousingRecyclerViewAdapter);
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
                mInterestedHousingRecyclerViewAdapter.notifyDataSetChanged();
            } else {
                ++mNumOfSavedHousings;
                mSavedHousings.add(0, event.getSavedHousing());
                mInterestedHousingRecyclerViewAdapter.notifyDataSetChanged();
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
//                    mInterestedHousingRecyclerViewAdapter.notifyItemRemoved(i);
                    mInterestedHousingRecyclerViewAdapter.notifyDataSetChanged();
                    break;
                }
            }
//            if (mNumOfSavedHousings == 0) {
//                mInterestedHousingRecyclerViewAdapter.notifyDataSetChanged();
//            } else
            if (mNumOfSavedHousings > 0) {
                loadMoreOlderSavedHousingsFromApi(1);
            }
            if (mSavedHousings.isEmpty() && mHistoryHousingNotes.isEmpty() && mHistoryHousingPhotos.isEmpty()) {
                mConnectionErrorLayout.setVisibility(View.VISIBLE);
                if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                    mViewSwitcherRetryConnect.showNext();
                }
            }
        }
    }

    @Subscribe
    public void takeHousingNote(TakeHousingNoteEvent event) {
        if (event.getHistoryHousingNote() != null) {
            if (mConnectionErrorLayout.getVisibility() == View.VISIBLE) {
                mConnectionErrorLayout.setVisibility(View.GONE);
            }
            if (mHistoryHousingNotes.isEmpty()) {
                mNumOfHistoryHousingNotes = 1;
                mHistoryHousingNotes.add(0, event.getHistoryHousingNote());
                mInterestedHousingRecyclerViewAdapter.notifyDataSetChanged();
            } else {
                ++mNumOfHistoryHousingNotes;
                mHistoryHousingNotes.add(0, event.getHistoryHousingNote());
                mInterestedHousingRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    @Subscribe
    public void updateHousingNote(UpdateHousingNoteEvent event) {
        if (event.getHistoryHousingNote() != null) {
            for (int i = 0; i < mHistoryHousingNotes.size(); ++i) {
                if (event.getHistoryHousingNote().getHousing().getID()
                        == mHistoryHousingNotes.get(i).getHousing().getID()) {
                    mHistoryHousingNotes.remove(i);
                    mHistoryHousingNotes.add(0, event.getHistoryHousingNote());
//                    mInterestedHousingRecyclerViewAdapter.notifyItemRemoved(i);
//                    mInterestedHousingRecyclerViewAdapter.notifyItemInserted(0);
                    mInterestedHousingRecyclerViewAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    @Subscribe
    public void deleteHousingNote(DeleteHousingNoteEvent event) {
        if (event.getHistoryHousingNote() != null) {
            for (int i = 0; i < mHistoryHousingNotes.size(); ++i) {
                if (event.getHistoryHousingNote().getHousing().getID()
                        == mHistoryHousingNotes.get(i).getHousing().getID()) {
                    --mNumOfHistoryHousingNotes;
                    mHistoryHousingNotes.remove(i);
//                    mInterestedHousingRecyclerViewAdapter.notifyItemRemoved(i);
                    mInterestedHousingRecyclerViewAdapter.notifyDataSetChanged();
                    break;
                }
            }
//        if (mNumOfHistoryHousingNotes == 0) {
//            mInterestedHousingRecyclerViewAdapter.notifyDataSetChanged();
//        } else
            if (mNumOfHistoryHousingNotes > 0) {
                loadMoreOlderHousingsWithNotesFromApi(1);
            }
            if (mSavedHousings.isEmpty() && mHistoryHousingNotes.isEmpty() && mHistoryHousingPhotos.isEmpty()) {
                mConnectionErrorLayout.setVisibility(View.VISIBLE);
                if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                    mViewSwitcherRetryConnect.showNext();
                }
            }
        }
    }

    @Subscribe
    public void takeHousingPhoto(TakeHousingPhotoEvent event) {
        if (event.getHistoryHousingPhoto() != null) {
            if (mConnectionErrorLayout.getVisibility() == View.VISIBLE) {
                mConnectionErrorLayout.setVisibility(View.GONE);
            }
            if (mHistoryHousingPhotos.isEmpty()) {
                mNumOfHistoryHousingPhotos = 1;
                mHistoryHousingPhotos.add(0, event.getHistoryHousingPhoto());
                mInterestedHousingRecyclerViewAdapter.notifyDataSetChanged();
            } else {
                ++mNumOfHistoryHousingPhotos;
                mHistoryHousingPhotos.add(0, event.getHistoryHousingPhoto());
                mInterestedHousingRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    @Subscribe
    public void deleteHousingPhoto(DeleteHousingPhotoEvent event) {
        if (event.getHistoryHousingPhoto() != null) {
            for (int i = 0; i < mHistoryHousingPhotos.size(); ++i) {
                if (event.getHistoryHousingPhoto().getHousing().getID()
                        == mHistoryHousingPhotos.get(i).getHousing().getID()) {
                    --mNumOfHistoryHousingPhotos;
                    mHistoryHousingPhotos.remove(i);
//                    mInterestedHousingRecyclerViewAdapter.notifyItemRemoved(i);
                    mInterestedHousingRecyclerViewAdapter.notifyDataSetChanged();
                    break;
                }
            }
//        if (mNumOfHistoryHousingPhotos == 0) {
//            mInterestedHousingRecyclerViewAdapter.notifyDataSetChanged();
//        } else
            if (mNumOfHistoryHousingPhotos > 0) {
                loadMoreOlderHousingsWithPhotosFromApi(1);
            }
            if (mSavedHousings.isEmpty() && mHistoryHousingNotes.isEmpty() && mHistoryHousingPhotos.isEmpty()) {
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
                if (mSavedHousings.isEmpty()) {
                    if (olderSavedHousings != null && olderSavedHousings.size() > 0) {
                        if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                            mViewSwitcherRetryConnect.showNext();
                        }
                        mConnectionErrorLayout.setVisibility(View.GONE);

//                        int positionStart = mSavedHousings.size();

                        for (int i = 0; i < olderSavedHousings.size(); ++i) {
                            if (mSavedHousings.size() < 3) {
                                mSavedHousings.add(olderSavedHousings.get(i));
                            } else {
                                break;
                            }
                        }

//                        mInterestedHousingRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mSavedHousings.size() - positionStart);
                        mInterestedHousingRecyclerViewAdapter.notifyDataSetChanged();

                        if (mSavedHousings.size() < 3 && mSavedHousings.size() < mNumOfSavedHousings) {
                            loadMoreOlderSavedHousingsFromApi(1);
                        }
                    } else {
//                        mConnectionErrorMessage.setVisibility(View.VISIBLE);
                        if (mHistoryHousingNotes.isEmpty() && mHistoryHousingPhotos.isEmpty()) {
                            if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                                mViewSwitcherRetryConnect.showNext();
                            }
                        }
//                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext());
                    }
                } else if (!mSavedHousings.isEmpty()) {
                    if (olderSavedHousings != null && olderSavedHousings.size() > 0) {
//                        int positionStart = mSavedHousings.size();

                        for (int i = 0; i < olderSavedHousings.size(); ++i) {
                            if (mSavedHousings.size() < 3) {
                                mSavedHousings.add(olderSavedHousings.get(i));
                            } else {
                                break;
                            }
                        }

//                        mInterestedHousingRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mSavedHousings.size() - positionStart);
                        mInterestedHousingRecyclerViewAdapter.notifyDataSetChanged();

                        if (mSavedHousings.size() < 3 && mSavedHousings.size() < mNumOfSavedHousings) {
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

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadMoreOlderHousingsWithNotesFromApi(final int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
//        int currentBottomHousingID = !mHistoryHousingNotes.isEmpty() ? mHistoryHousingNotes.get(mHistoryHousingNotes.size() - 1).getID() : -1;
        String currentBottomHousingNoteDateTimeCreated
                = !mHistoryHousingNotes.isEmpty()
                ? new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(mHistoryHousingNotes.get(mHistoryHousingNotes.size() - 1).getDateTimeCreated())
                : null;
        if (currentBottomHousingNoteDateTimeCreated != null) {
            currentBottomHousingNoteDateTimeCreated
                    = currentBottomHousingNoteDateTimeCreated.substring(0, currentBottomHousingNoteDateTimeCreated.length() - 2)
                    + ":" + currentBottomHousingNoteDateTimeCreated.substring(currentBottomHousingNoteDateTimeCreated.length() - 2, currentBottomHousingNoteDateTimeCreated.length());
        }
        UserClient.getMoreOlderHousingsWithNotes(currentBottomHousingNoteDateTimeCreated, new IGetMoreOlderHousingsWithNotesCallback() {
            @Override
            public void onGetComplete(List<HistoryHousingNote> olderHousingsWithNotes) {
                if (mHistoryHousingNotes.isEmpty()) {
                    if (olderHousingsWithNotes != null && olderHousingsWithNotes.size() > 0) {
                        if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                            mViewSwitcherRetryConnect.showNext();
                        }
                        mConnectionErrorLayout.setVisibility(View.GONE);

//                        int positionStart = mHistoryHousingNotes.size();

                        for (int i = 0; i < olderHousingsWithNotes.size(); ++i) {
                            if (mHistoryHousingNotes.size() < 3) {
                                mHistoryHousingNotes.add(olderHousingsWithNotes.get(i));
                            } else {
                                break;
                            }
                        }

//                        mInterestedHousingRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mHistoryHousingNotes.size() - positionStart);
                        mInterestedHousingRecyclerViewAdapter.notifyDataSetChanged();

                        if (mHistoryHousingNotes.size() < 3 && mHistoryHousingNotes.size() < mNumOfHistoryHousingNotes) {
                            loadMoreOlderHousingsWithNotesFromApi(1);
                        }
                    } else {
                        if (mSavedHousings.isEmpty() && mHistoryHousingPhotos.isEmpty()) {
                            if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                                mViewSwitcherRetryConnect.showNext();
                            }
                        }
//                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext());
                    }
                } else if (!mHistoryHousingNotes.isEmpty()) {
                    if (olderHousingsWithNotes != null && olderHousingsWithNotes.size() > 0) {
//                        int positionStart = mHistoryHousingNotes.size();

                        for (int i = 0; i < olderHousingsWithNotes.size(); ++i) {
                            if (mHistoryHousingNotes.size() < 3) {
                                mHistoryHousingNotes.add(olderHousingsWithNotes.get(i));
                            } else {
                                break;
                            }
                        }

//                        mInterestedHousingRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mHistoryHousingNotes.size() - positionStart);
                        mInterestedHousingRecyclerViewAdapter.notifyDataSetChanged();

                        if (mHistoryHousingNotes.size() < 3 && mHistoryHousingNotes.size() < mNumOfHistoryHousingNotes) {
                            loadMoreOlderHousingsWithNotesFromApi(1);
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
    public void loadMoreOlderHousingsWithPhotosFromApi(final int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
//        int currentBottomHousingID = !mHistoryHousingPhotos.isEmpty() ? mHistoryHousingPhotos.get(mHistoryHousingPhotos.size() - 1).getID() : -1;
        String currentBottomHousingPhotoDateTimeCreated
                = !mHistoryHousingPhotos.isEmpty()
                ? new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(mHistoryHousingPhotos.get(mHistoryHousingPhotos.size() - 1).getDateTimeCreated())
                : null;
        if (currentBottomHousingPhotoDateTimeCreated != null) {
            currentBottomHousingPhotoDateTimeCreated
                    = currentBottomHousingPhotoDateTimeCreated.substring(0, currentBottomHousingPhotoDateTimeCreated.length() - 2)
                    + ":" + currentBottomHousingPhotoDateTimeCreated.substring(currentBottomHousingPhotoDateTimeCreated.length() - 2, currentBottomHousingPhotoDateTimeCreated.length());
        }
        UserClient.getMoreOlderHousingsWithPhotos(currentBottomHousingPhotoDateTimeCreated, new IGetMoreOlderHousingsWithPhotosCallback() {
            @Override
            public void onGetComplete(List<HistoryHousingPhoto> olderHousingsWithPhotos) {
                if (mHistoryHousingPhotos.isEmpty()) {
                    if (olderHousingsWithPhotos != null && olderHousingsWithPhotos.size() > 0) {
                        if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                            mViewSwitcherRetryConnect.showNext();
                        }
                        mConnectionErrorLayout.setVisibility(View.GONE);

//                        int positionStart = mHistoryHousingPhotos.size();

                        for (int i = 0; i < olderHousingsWithPhotos.size(); ++i) {
                            if (mHistoryHousingPhotos.size() < 3) {
                                mHistoryHousingPhotos.add(olderHousingsWithPhotos.get(i));
                            } else {
                                break;
                            }
                        }

//                        mInterestedHousingRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mHistoryHousingPhotos.size() - positionStart);
                        mInterestedHousingRecyclerViewAdapter.notifyDataSetChanged();

                        if (mHistoryHousingPhotos.size() < 3 && mHistoryHousingPhotos.size() < mNumOfHistoryHousingPhotos) {
                            loadMoreOlderHousingsWithPhotosFromApi(1);
                        }
                    } else {
                        if (mSavedHousings.isEmpty() && mHistoryHousingNotes.isEmpty()) {
                            if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                                mViewSwitcherRetryConnect.showNext();
                            }
                        }
//                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext());
                    }
                } else if (!mHistoryHousingPhotos.isEmpty()) {
                    if (olderHousingsWithPhotos != null && olderHousingsWithPhotos.size() > 0) {
//                        int positionStart = mHistoryHousingPhotos.size();

                        for (int i = 0; i < olderHousingsWithPhotos.size(); ++i) {
                            if (mHistoryHousingPhotos.size() < 3) {
                                mHistoryHousingPhotos.add(olderHousingsWithPhotos.get(i));
                            } else {
                                break;
                            }
                        }

//                        mInterestedHousingRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mHistoryHousingPhotos.size() - positionStart);
                        mInterestedHousingRecyclerViewAdapter.notifyDataSetChanged();

                        if (mHistoryHousingPhotos.size() < 3 && mHistoryHousingPhotos.size() < mNumOfHistoryHousingPhotos) {
                            loadMoreOlderHousingsWithPhotosFromApi(1);
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

    public void setListener(OnHousingListInteractionListener listener) {
        mListener = listener;
    }

//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnSavedHousePostListFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onSavedHousePostListFragmentInteraction(Housing housing);
//    }
}
