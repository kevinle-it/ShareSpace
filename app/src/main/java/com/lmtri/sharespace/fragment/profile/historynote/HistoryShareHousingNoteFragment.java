package com.lmtri.sharespace.fragment.profile.historynote;


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
import com.lmtri.sharespace.adapter.profiletab.historynote.HistoryShareHousingNoteRecyclerViewAdapter;
import com.lmtri.sharespace.api.model.HistoryShareHousingNote;
import com.lmtri.sharespace.api.service.RetrofitClient;
import com.lmtri.sharespace.api.service.user.UserClient;
import com.lmtri.sharespace.api.service.user.note.sharehousing.IGetMoreOlderShareHousingsWithNotesCallback;
import com.lmtri.sharespace.api.service.user.note.sharehousing.IGetNumOfShareHousingsWithNotesCallback;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.busevent.sharehousing.note.DeleteShareHousingNoteEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.note.TakeShareHousingNoteEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.note.UpdateShareHousingNoteEvent;
import com.lmtri.sharespace.listener.EndlessRecyclerViewScrollListener;
import com.lmtri.sharespace.listener.OnShareHousingListInteractionListener;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryShareHousingNoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryShareHousingNoteFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private LinearLayout mConnectionErrorLayout;
    private TextView mConnectionErrorMessage;
    private ViewSwitcher mViewSwitcherRetryConnect;
    private FrameLayout mRetryConnectButton;

    private RecyclerView mRecyclerView;
    private OnShareHousingListInteractionListener mListener;
    private EndlessRecyclerViewScrollListener mRecyclerViewScrollListener;

    private int mNumOfHistoryShareHousingNotes = 0;
    private ArrayList<HistoryShareHousingNote> mHistoryShareHousingNotes = new ArrayList<>();
    private HistoryShareHousingNoteRecyclerViewAdapter mHistoryShareHousingNoteRecyclerViewAdapter;


    public HistoryShareHousingNoteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HistoryShareHousingNoteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryShareHousingNoteFragment newInstance(int columnCount) {
        HistoryShareHousingNoteFragment fragment = new HistoryShareHousingNoteFragment();
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
        View view = inflater.inflate(R.layout.fragment_history_share_housing_note_list, container, false);

        mConnectionErrorLayout = (LinearLayout) view.findViewById(R.id.fragment_history_share_housing_note_list_connection_error_layout);
        mConnectionErrorMessage = (TextView) view.findViewById(R.id.fragment_history_share_housing_note_list_connection_error_message);
        mViewSwitcherRetryConnect = (ViewSwitcher) view.findViewById(R.id.fragment_history_share_housing_note_list_view_switcher_retry_connect_to_server);
        mRetryConnectButton = (FrameLayout) view.findViewById(R.id.fragment_history_share_housing_note_list_retry_button);
        mRetryConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewSwitcherRetryConnect.showNext();
                loadMoreOlderShareHousingsWithNotesFromApi(0);
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

        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_history_share_housing_note_list_share_list);
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
                    if (Constants.CURRENT_USER != null) {
                        loadMoreOlderShareHousingsWithNotesFromApi(page);
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
            mRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager, true) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    // Triggered only when new data needs to be appended to the list
                    // Add whatever code is needed to append new items to the bottom of the list
                    if (Constants.CURRENT_USER != null) {
                        loadMoreOlderShareHousingsWithNotesFromApi(page);
                    }
                }
            };
        }

        mHistoryShareHousingNoteRecyclerViewAdapter = new HistoryShareHousingNoteRecyclerViewAdapter(getContext(), mHistoryShareHousingNotes, mListener);
        mRecyclerView.setAdapter(mHistoryShareHousingNoteRecyclerViewAdapter);
        mRecyclerView.addOnScrollListener(mRecyclerViewScrollListener);

        if (Constants.CURRENT_USER != null) {
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
        }

        return view;
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
                mHistoryShareHousingNoteRecyclerViewAdapter.notifyItemInserted(0);
            } else {
                ++mNumOfHistoryShareHousingNotes;
                mHistoryShareHousingNotes.add(0, event.getHistoryShareHousingNote());
                mHistoryShareHousingNoteRecyclerViewAdapter.notifyItemInserted(0);
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
                    mHistoryShareHousingNoteRecyclerViewAdapter.notifyItemRemoved(i);
                    mHistoryShareHousingNoteRecyclerViewAdapter.notifyItemInserted(0);
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
                    mHistoryShareHousingNoteRecyclerViewAdapter.notifyItemRemoved(i);
                    break;
                }
            }
            if (mHistoryShareHousingNotes.isEmpty()) {
                mConnectionErrorLayout.setVisibility(View.VISIBLE);
                if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                    mViewSwitcherRetryConnect.showNext();
                }
            }
        }
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadMoreOlderShareHousingsWithNotesFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
//        int currentBottomShareHousingID = !mHistoryShareHousingNotes.isEmpty() ? mHistoryShareHousingNotes.get(mHistoryShareHousingNotes.size() - 1).getHistoryShareHousingNote().getID() : -1;
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
            public void onGetComplete(List<HistoryShareHousingNote> olderShareHousings) {
                if (mHistoryShareHousingNotes.isEmpty()) {
                    if (olderShareHousings != null && olderShareHousings.size() > 0) {
                        if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                            mViewSwitcherRetryConnect.showNext();
                        }
                        mConnectionErrorLayout.setVisibility(View.GONE);

                        int positionStart = mHistoryShareHousingNotes.size();

                        mHistoryShareHousingNotes.addAll(olderShareHousings);

                        mHistoryShareHousingNoteRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mHistoryShareHousingNotes.size() - positionStart);

                        if (mHistoryShareHousingNotes.size() < mNumOfHistoryShareHousingNotes) {
                            loadMoreOlderShareHousingsWithNotesFromApi(1);
                        }
                    } else {
                        if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                            mViewSwitcherRetryConnect.showNext();
                        }
//                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext());
                    }
                } else if (!mHistoryShareHousingNotes.isEmpty()) {
                    if (olderShareHousings != null && olderShareHousings.size() > 0) {
                        int positionStart = mHistoryShareHousingNotes.size();

                        mHistoryShareHousingNotes.addAll(olderShareHousings);

                        mHistoryShareHousingNoteRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mHistoryShareHousingNotes.size() - positionStart);

                        if (mHistoryShareHousingNotes.size() < mNumOfHistoryShareHousingNotes) {
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

    public void setListener(OnShareHousingListInteractionListener listener) {
        mListener = listener;
    }
}
