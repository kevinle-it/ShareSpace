package com.lmtri.sharespace.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lmtri.sharespace.R;
import com.lmtri.sharespace.helper.Constants;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RootFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RootFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    private int viewPagerIndex;


    public RootFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param viewPagerIndex Parameter 1.
     * @return A new instance of fragment RootFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RootFragment newInstance(int viewPagerIndex) {
        RootFragment fragment = new RootFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ROOT_FRAGMENT_VIEW_PAGER_INDEX_PARAM, viewPagerIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            viewPagerIndex = getArguments().getInt(Constants.ROOT_FRAGMENT_VIEW_PAGER_INDEX_PARAM, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_root, container, false);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        switch (viewPagerIndex) {
            case Constants.VIEW_PAGER_INDEX_HOME:
                transaction.replace(R.id.root_container, HousingFragment.newInstance(1));
                break;
            case Constants.VIEW_PAGER_INDEX_SAVED:
                transaction.replace(R.id.root_container, SavedFragment.newInstance("a", "b"));
                break;
            case Constants.VIEW_PAGER_INDEX_SHARE:
                transaction.replace(R.id.root_container, SavedFragment.newInstance("c", "d"));
                break;
            case Constants.VIEW_PAGER_INDEX_INBOX:
                transaction.replace(R.id.root_container, SavedFragment.newInstance("e", "f"));
                break;
            case Constants.VIEW_PAGER_INDEX_PROFILE:
                transaction.replace(R.id.root_container, SavedFragment.newInstance("g", "h"));
                break;
        }
        transaction.commit();

        return view;
    }

}
