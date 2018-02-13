package com.lmtri.sharespace.fragment.share.search;


import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lmtri.sharespace.R;
import com.lmtri.sharespace.ShareSpaceApplication;
import com.lmtri.sharespace.adapter.profiletab.historypost.HistoryShareHousingRecyclerViewAdapter;
import com.lmtri.sharespace.api.model.ShareHousing;
import com.lmtri.sharespace.helper.busevent.sharehousing.post.DeleteShareHousingEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.post.UpdateShareHousingEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.save.SaveShareHousingEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.save.UnsaveShareHousingEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.search.ReturnSearchShareHousingResultEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.search.SearchShareHousingMapListViewBackButtonEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.search.SearchShareHousingSwitchMapViewEvent;
import com.lmtri.sharespace.listener.OnShareHousingListInteractionListener;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchResultShareHousingListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchResultShareHousingListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private Toolbar mToolbar;
    private ImageView mSwitchToMapViewType;

    private RecyclerView mRecyclerView;
    private OnShareHousingListInteractionListener mListener;

    private List<ShareHousing> mShareHousings = new ArrayList<>();
    private HistoryShareHousingRecyclerViewAdapter mShareHousingRecyclerViewAdapter;

    public SearchResultShareHousingListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchResultShareHousingListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchResultShareHousingListFragment newInstance(int columnCount) {
        SearchResultShareHousingListFragment fragment = new SearchResultShareHousingListFragment();
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
        View view = inflater.inflate(R.layout.fragment_search_result_share_housing_list, container, false);

        mToolbar = (Toolbar) view.findViewById(R.id.fragment_search_result_share_housing_list_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        if (mToolbar.getNavigationIcon() != null) {
            mToolbar.getNavigationIcon().setColorFilter(
                    ContextCompat.getColor(getContext(), android.R.color.white),
                    PorterDuff.Mode.SRC_ATOP
            );
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareSpaceApplication.BUS.post(new SearchShareHousingMapListViewBackButtonEvent());
            }
        });

        mSwitchToMapViewType = (ImageView) view.findViewById(R.id.fragment_search_result_share_housing_map_view_type);
        mSwitchToMapViewType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareSpaceApplication.BUS.post(new SearchShareHousingSwitchMapViewEvent());
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_search_result_share_housing_list_share_list);
        // Set the adapter
        if (mColumnCount <= 1) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                    linearLayoutManager.getOrientation());

            mRecyclerView.setLayoutManager(linearLayoutManager);
//            mRecyclerView.addItemDecoration(dividerItemDecoration);
        } else {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), mColumnCount);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                    gridLayoutManager.getOrientation());

            mRecyclerView.setLayoutManager(gridLayoutManager);
//            mRecyclerView.addItemDecoration(dividerItemDecoration);
        }

        return view;
    }

    public boolean onBackPressed() {
        return false;
    }

    @Subscribe
    public void returnSearchShareHousingResults(ReturnSearchShareHousingResultEvent event) {
        if (event.getShareHousingResults() != null) {
            mShareHousings = event.getShareHousingResults();
            mShareHousingRecyclerViewAdapter = new HistoryShareHousingRecyclerViewAdapter(getContext(), mShareHousings, mListener);
            mRecyclerView.setAdapter(mShareHousingRecyclerViewAdapter);
        }
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
    public void shareHousingUpdated(UpdateShareHousingEvent event) {
        if (event.getShareHousing() != null) {
            for (int i = 0; i < mShareHousings.size(); ++i) {
                if (mShareHousings.get(i).getID() == event.getShareHousing().getID()) {
                    mShareHousings.remove(i);
                    mShareHousings.add(0, event.getShareHousing());
                    mShareHousingRecyclerViewAdapter.notifyItemRemoved(i);
                    mShareHousingRecyclerViewAdapter.notifyItemInserted(0);
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
                    mShareHousingRecyclerViewAdapter.notifyItemRemoved(i);
                    break;
                }
            }
        }
    }

    public void setListener(OnShareHousingListInteractionListener listener) {
        mListener = listener;
    }

}
