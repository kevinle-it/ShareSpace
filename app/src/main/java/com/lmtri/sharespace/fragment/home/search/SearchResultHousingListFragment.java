package com.lmtri.sharespace.fragment.home.search;


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
import com.lmtri.sharespace.adapter.profiletab.historypost.HistoryHousingRecyclerViewAdapter;
import com.lmtri.sharespace.api.model.Housing;
import com.lmtri.sharespace.helper.busevent.housing.post.DeleteHousingEvent;
import com.lmtri.sharespace.helper.busevent.housing.post.UpdateHousingEvent;
import com.lmtri.sharespace.helper.busevent.housing.save.SaveHousingEvent;
import com.lmtri.sharespace.helper.busevent.housing.save.UnsaveHousingEvent;
import com.lmtri.sharespace.helper.busevent.housing.search.ReturnSearchHousingResultEvent;
import com.lmtri.sharespace.helper.busevent.housing.search.SearchHousingMapListViewBackButtonEvent;
import com.lmtri.sharespace.helper.busevent.housing.search.SearchHousingSwitchMapViewEvent;
import com.lmtri.sharespace.listener.OnHousingListInteractionListener;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchResultHousingListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchResultHousingListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private Toolbar mToolbar;
    private ImageView mSwitchToMapViewType;

    private RecyclerView mRecyclerView;
    private OnHousingListInteractionListener mListener;

    private List<Housing> mHousings = new ArrayList<>();
    private HistoryHousingRecyclerViewAdapter mHousingRecyclerViewAdapter;

    public SearchResultHousingListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchResultHousingListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchResultHousingListFragment newInstance(int columnCount) {
        SearchResultHousingListFragment fragment = new SearchResultHousingListFragment();
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
        View view = inflater.inflate(R.layout.fragment_search_result_housing_list, container, false);

        mToolbar = (Toolbar) view.findViewById(R.id.fragment_search_result_housing_list_toolbar);
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
                ShareSpaceApplication.BUS.post(new SearchHousingMapListViewBackButtonEvent());
            }
        });

        mSwitchToMapViewType = (ImageView) view.findViewById(R.id.fragment_search_result_housing_map_view_type);
        mSwitchToMapViewType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareSpaceApplication.BUS.post(new SearchHousingSwitchMapViewEvent());
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_search_result_housing_list_home_list);
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
    public void returnSearchHousingResults(ReturnSearchHousingResultEvent event) {
        if (event.getHousingResults() != null) {
            mHousings = event.getHousingResults();
            mHousingRecyclerViewAdapter = new HistoryHousingRecyclerViewAdapter(getContext(), mHousings, mListener);
            mRecyclerView.setAdapter(mHousingRecyclerViewAdapter);
        }
    }

    @Subscribe
    public void saveHousing(SaveHousingEvent event) {
        if (event.getSavedHousing() != null) {
            for (int i = 0; i < mHousings.size(); ++i) {
                if (event.getSavedHousing().getSavedHousing().getID()
                        == mHousings.get(i).getID()) {
                    mHousings.get(i).setNumOfSaved(mHousings.get(i).getNumOfSaved() + 1);
                    break;
                }
            }
        }
    }

    @Subscribe
    public void unsaveHousing(UnsaveHousingEvent event) {
        if (event.getSavedHousing() != null) {
            for (int i = 0; i < mHousings.size(); ++i) {
                if (event.getSavedHousing().getSavedHousing().getID()
                        == mHousings.get(i).getID()) {
                    mHousings.get(i).setNumOfSaved(mHousings.get(i).getNumOfSaved() - 1);
                    break;
                }
            }
        }
    }

    @Subscribe
    public void housingUpdated(UpdateHousingEvent event) {
        if (event.getHousing() != null) {
            for (int i = 0; i < mHousings.size(); ++i) {
                if (mHousings.get(i).getID() == event.getHousing().getID()) {
                    mHousings.remove(i);
                    mHousings.add(0, event.getHousing());
                    mHousingRecyclerViewAdapter.notifyItemRemoved(i);
                    mHousingRecyclerViewAdapter.notifyItemInserted(0);
                    break;
                }
            }
        }
    }

    @Subscribe
    public void housingDeleted(DeleteHousingEvent event) {
        if (event.getHousing() != null) {
            for (int i = 0; i < mHousings.size(); ++i) {
                if (event.getHousing().getID() == mHousings.get(i).getID()) {
                    mHousings.remove(i);
                    mHousingRecyclerViewAdapter.notifyItemRemoved(i);
                    break;
                }
            }
        }
    }

    public void setListener(OnHousingListInteractionListener listener) {
        mListener = listener;
    }
}
