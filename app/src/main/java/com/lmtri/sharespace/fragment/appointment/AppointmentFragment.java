package com.lmtri.sharespace.fragment.appointment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lmtri.sharespace.R;
import com.lmtri.sharespace.adapter.ViewPagerAdapter;
import com.lmtri.sharespace.customview.CustomViewPager;
import com.lmtri.sharespace.helper.busevent.appointment.sharehousing.OpenShareHousingAppointmentNotificationEvent;
import com.lmtri.sharespace.helper.busevent.appointment.sharehousing.SwitchToShareHousingAppointmentTabEvent;
import com.lmtri.sharespace.listener.OnHousingAppointmentListInteractionListener;
import com.lmtri.sharespace.listener.OnShareHousingAppointmentListInteractionListener;
import com.squareup.otto.Subscribe;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AppointmentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppointmentFragment extends Fragment {

    public static final String TAG = AppointmentFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TabLayout mTabLayout;
    private CustomViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;

    private ViewPager.SimpleOnPageChangeListener mViewPagerOnPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            mTabLayout.getTabAt(position).select();
        }
    };

    private OnHousingAppointmentListInteractionListener mHousingAppointmentListInteractionListener;
    private OnShareHousingAppointmentListInteractionListener mShareHousingAppointmentListInteractionListener;

    public AppointmentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AppointmentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AppointmentFragment newInstance(String param1, String param2) {
        AppointmentFragment fragment = new AppointmentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_appointment, container, false);

        mTabLayout = (TabLayout) view.findViewById(R.id.fragment_appointment_tab_layout);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.fragment_housing_appointment_tab_text));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.fragment_share_housing_appointment_tab_text));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.setTabTextColors(ContextCompat.getColor(getActivity(), R.color.colorPrimary), ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mViewPager = (CustomViewPager) view.findViewById(R.id.fragment_appointment_view_pager);
        mViewPager.setOffscreenPageLimit(1);

        mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        Fragment fragment1 = HousingAppointmentTabFragment.newInstance(1);
        ((HousingAppointmentTabFragment) fragment1).setListener(mHousingAppointmentListInteractionListener);
        mViewPagerAdapter.addFragment(fragment1);

        Fragment fragment2 = ShareHousingAppointmentTabFragment.newInstance(1);
        ((ShareHousingAppointmentTabFragment) fragment2).setListener(mShareHousingAppointmentListInteractionListener);
        mViewPagerAdapter.addFragment(fragment2);

        mViewPager.setAdapter(mViewPagerAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mViewPager.addOnPageChangeListener(mViewPagerOnPageChangeListener);
    }

    @Override
    public void onStop() {
        mViewPager.removeOnPageChangeListener(mViewPagerOnPageChangeListener);
        super.onStop();
    }

    public void setHousingAppointmentListInteractionListener(OnHousingAppointmentListInteractionListener listener) {
        mHousingAppointmentListInteractionListener = listener;
    }

    public void setShareHousingAppointmentListInteractionListener(OnShareHousingAppointmentListInteractionListener listener) {
        mShareHousingAppointmentListInteractionListener = listener;
    }

    public boolean onBackPressed() {
        return false;
    }

    @Subscribe
    public void switchToShareHousingAppointmentTabEvent(SwitchToShareHousingAppointmentTabEvent event) {
        mTabLayout.getTabAt(1).select();
    }
}
