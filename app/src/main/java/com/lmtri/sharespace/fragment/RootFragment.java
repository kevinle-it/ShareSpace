package com.lmtri.sharespace.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lmtri.sharespace.R;
import com.lmtri.sharespace.fragment.appointment.AppointmentFragment;
import com.lmtri.sharespace.fragment.home.HousingFragment;
import com.lmtri.sharespace.fragment.interested.InterestedFragment;
import com.lmtri.sharespace.fragment.profile.ProfileFragment;
import com.lmtri.sharespace.fragment.share.ShareHousingFragment;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.listener.OnHousingAppointmentListInteractionListener;
import com.lmtri.sharespace.listener.OnHousingListInteractionListener;
import com.lmtri.sharespace.listener.OnShareHousingAppointmentListInteractionListener;
import com.lmtri.sharespace.listener.OnShareHousingListInteractionListener;

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
    private OnHousingListInteractionListener mHousingListInteractionListener;
//    private OnSavedHousingListInteractionListener mSavedHousingListInteractionListener;
//    private OnSavedShareHousingListInteractionListener mSavedShareHousingListInteractionListener;
    private OnShareHousingListInteractionListener mShareHousingListInteractionListener;
    private OnHousingAppointmentListInteractionListener mHousingAppointmentListInteractionListener;
    private OnShareHousingAppointmentListInteractionListener mShareHousingAppointmentListInteractionListener;


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
        Fragment fragment;

        switch (viewPagerIndex) {
            case Constants.VIEW_PAGER_INDEX_HOME:
                fragment = HousingFragment.newInstance(1);
                ((HousingFragment) fragment).setListener(mHousingListInteractionListener);
                transaction.replace(R.id.root_container, fragment);
                break;
            case Constants.VIEW_PAGER_INDEX_SAVED:
                fragment = InterestedFragment.newInstance("a", "b");
                ((InterestedFragment) fragment).setHousingListInteractionListener(mHousingListInteractionListener);
                ((InterestedFragment) fragment).setShareHousingListInteractionListener(mShareHousingListInteractionListener);
                transaction.replace(R.id.root_container, fragment);
                break;
            case Constants.VIEW_PAGER_INDEX_SHARE:
                fragment = ShareHousingFragment.newInstance(1);
                ((ShareHousingFragment) fragment).setListener(mShareHousingListInteractionListener);
                transaction.replace(R.id.root_container, fragment);
                break;
            case Constants.VIEW_PAGER_INDEX_SCHEDULE:
                fragment = AppointmentFragment.newInstance("e", "f");
                ((AppointmentFragment) fragment).setHousingAppointmentListInteractionListener(mHousingAppointmentListInteractionListener);
                ((AppointmentFragment) fragment).setShareHousingAppointmentListInteractionListener(mShareHousingAppointmentListInteractionListener);
                transaction.replace(R.id.root_container, fragment);
                break;
            case Constants.VIEW_PAGER_INDEX_PROFILE:
                transaction.replace(R.id.root_container, ProfileFragment.newInstance("g", "h"));
                break;
        }
        transaction.commit();

        return view;
    }

    public void setHousingListInteractionListener(OnHousingListInteractionListener listener) {
        mHousingListInteractionListener = listener;
    }

//    public void setSavedHousingListInteractionListener(OnSavedHousingListInteractionListener listener) {
//        mSavedHousingListInteractionListener = listener;
//    }
//
//    public void setSavedShareHousingListInteractionListener(OnSavedShareHousingListInteractionListener listener) {
//        mSavedShareHousingListInteractionListener = listener;
//    }

    public void setShareHousingListInteractionListener(OnShareHousingListInteractionListener listener) {
        mShareHousingListInteractionListener = listener;
    }

    public void setHousingAppointmentListInteractionListener(OnHousingAppointmentListInteractionListener listener) {
        mHousingAppointmentListInteractionListener = listener;
    }

    public void setShareHousingAppointmentListInteractionListener(OnShareHousingAppointmentListInteractionListener listener) {
        mShareHousingAppointmentListInteractionListener = listener;
    }

    public Fragment getCurrentFragment() {
        return getChildFragmentManager().findFragmentById(R.id.root_container);
    }
}
