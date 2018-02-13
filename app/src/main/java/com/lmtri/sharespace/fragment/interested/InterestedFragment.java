package com.lmtri.sharespace.fragment.interested;

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
import com.lmtri.sharespace.listener.OnHousingListInteractionListener;
import com.lmtri.sharespace.listener.OnShareHousingListInteractionListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InterestedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InterestedFragment extends Fragment {
    
    public static final String TAG = InterestedFragment.class.getSimpleName();
    
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

    private OnHousingListInteractionListener mHousingListInteractionListener;
    private OnShareHousingListInteractionListener mShareHousingListInteractionListener;

    public InterestedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InterestedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InterestedFragment newInstance(String param1, String param2) {
        InterestedFragment fragment = new InterestedFragment();
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
        // Inflate the layout for this fragment.
        View view = inflater.inflate(R.layout.fragment_interested, container, false);

        mTabLayout = (TabLayout) view.findViewById(R.id.fragment_interested_tab_layout);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.fragment_interested_housing_tab_text));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.fragment_interested_share_housing_tab_text));
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

        mViewPager = (CustomViewPager) view.findViewById(R.id.fragment_interested_view_pager);
        mViewPager.setOffscreenPageLimit(1);

        mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        Fragment fragment1 = InterestedHousingTabFragment.newInstance(1);
        ((InterestedHousingTabFragment) fragment1).setListener(mHousingListInteractionListener);
        mViewPagerAdapter.addFragment(fragment1);

        Fragment fragment2 = InterestedShareHousingTabFragment.newInstance(1);
        ((InterestedShareHousingTabFragment) fragment2).setListener(mShareHousingListInteractionListener);
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

    public void setHousingListInteractionListener(OnHousingListInteractionListener listener) {
        mHousingListInteractionListener = listener;
    }

    public void setShareHousingListInteractionListener(OnShareHousingListInteractionListener listener) {
        mShareHousingListInteractionListener = listener;
    }

    public boolean onBackPressed() {
        return false;
    }
}
