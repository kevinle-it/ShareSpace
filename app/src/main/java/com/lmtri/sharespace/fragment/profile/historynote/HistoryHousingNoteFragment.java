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
import com.lmtri.sharespace.adapter.profiletab.historynote.HistoryHousingNoteRecyclerViewAdapter;
import com.lmtri.sharespace.api.model.HistoryHousingNote;
import com.lmtri.sharespace.api.service.RetrofitClient;
import com.lmtri.sharespace.api.service.user.UserClient;
import com.lmtri.sharespace.api.service.user.note.housing.IGetMoreOlderHousingsWithNotesCallback;
import com.lmtri.sharespace.api.service.user.note.housing.IGetNumOfHousingsWithNotesCallback;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.busevent.housing.note.DeleteHousingNoteEvent;
import com.lmtri.sharespace.helper.busevent.housing.note.TakeHousingNoteEvent;
import com.lmtri.sharespace.helper.busevent.housing.note.UpdateHousingNoteEvent;
import com.lmtri.sharespace.listener.EndlessRecyclerViewScrollListener;
import com.lmtri.sharespace.listener.OnHousingListInteractionListener;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryHousingNoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryHousingNoteFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private LinearLayout mConnectionErrorLayout;
    private TextView mConnectionErrorMessage;
    private ViewSwitcher mViewSwitcherRetryConnect;
    private FrameLayout mRetryConnectButton;

    private RecyclerView mRecyclerView;
    private OnHousingListInteractionListener mListener;
    private EndlessRecyclerViewScrollListener mRecyclerViewScrollListener;

    private int mNumOfHistoryHousingNotes = 0;
    private ArrayList<HistoryHousingNote> mHistoryHousingNotes = new ArrayList<>();
    private HistoryHousingNoteRecyclerViewAdapter mHistoryHousingNoteRecyclerViewAdapter;

    public HistoryHousingNoteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HistoryHousingNoteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryHousingNoteFragment newInstance(int columnCount) {
        HistoryHousingNoteFragment fragment = new HistoryHousingNoteFragment();
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
        View view = inflater.inflate(R.layout.fragment_history_housing_note_list, container, false);

        mConnectionErrorLayout = (LinearLayout) view.findViewById(R.id.fragment_history_housing_note_list_connection_error_layout);
        mConnectionErrorMessage = (TextView) view.findViewById(R.id.fragment_history_housing_note_list_connection_error_message);
        mViewSwitcherRetryConnect = (ViewSwitcher) view.findViewById(R.id.fragment_history_housing_note_list_view_switcher_retry_connect_to_server);
        mRetryConnectButton = (FrameLayout) view.findViewById(R.id.fragment_history_housing_note_list_retry_button);
        mRetryConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewSwitcherRetryConnect.showNext();
                loadMoreOlderHousingsWithNotesFromApi(0);
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

        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_history_housing_note_list_home_list);
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
                        loadMoreOlderHousingsWithNotesFromApi(page);
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
                        loadMoreOlderHousingsWithNotesFromApi(page);
                    }
                }
            };
        }

        mHistoryHousingNoteRecyclerViewAdapter = new HistoryHousingNoteRecyclerViewAdapter(getContext(), mHistoryHousingNotes, mListener);
        mRecyclerView.setAdapter(mHistoryHousingNoteRecyclerViewAdapter);
        mRecyclerView.addOnScrollListener(mRecyclerViewScrollListener);

        if (Constants.CURRENT_USER != null) {
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
        }

        return view;
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
                mHistoryHousingNoteRecyclerViewAdapter.notifyItemInserted(0);
            } else {
                ++mNumOfHistoryHousingNotes;
                mHistoryHousingNotes.add(0, event.getHistoryHousingNote());
                mHistoryHousingNoteRecyclerViewAdapter.notifyItemInserted(0);
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
                    mHistoryHousingNoteRecyclerViewAdapter.notifyItemRemoved(i);
                    mHistoryHousingNoteRecyclerViewAdapter.notifyItemInserted(0);
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
                    mHistoryHousingNoteRecyclerViewAdapter.notifyItemRemoved(i);
                    break;
                }
            }
            if (mHistoryHousingNotes.isEmpty()) {
                mConnectionErrorLayout.setVisibility(View.VISIBLE);
                if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                    mViewSwitcherRetryConnect.showNext();
                }
            }
        }
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

                        int positionStart = mHistoryHousingNotes.size();

                        mHistoryHousingNotes.addAll(olderHousingsWithNotes);

                        mHistoryHousingNoteRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mHistoryHousingNotes.size() - positionStart);

                        if (mHistoryHousingNotes.size() < mNumOfHistoryHousingNotes) {
                            loadMoreOlderHousingsWithNotesFromApi(1);
                        }
                    } else {
                        if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                            mViewSwitcherRetryConnect.showNext();
                        }
//                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext());
                    }
                } else if (!mHistoryHousingNotes.isEmpty()) {
                    if (olderHousingsWithNotes != null && olderHousingsWithNotes.size() > 0) {
                        int positionStart = mHistoryHousingNotes.size();

                        mHistoryHousingNotes.addAll(olderHousingsWithNotes);

                        mHistoryHousingNoteRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mHistoryHousingNotes.size() - positionStart);

                        if (mHistoryHousingNotes.size() < mNumOfHistoryHousingNotes) {
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

    public void setListener(OnHousingListInteractionListener listener) {
        mListener = listener;
    }
}
