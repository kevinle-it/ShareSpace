package com.lmtri.sharespace.activity.profiletab.historyphoto;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.lmtri.sharespace.R;
import com.lmtri.sharespace.activity.hometab.HousingDetailActivity;
import com.lmtri.sharespace.activity.sharetab.ShareHousingDetailActivity;
import com.lmtri.sharespace.adapter.ViewPagerAdapter;
import com.lmtri.sharespace.api.model.Housing;
import com.lmtri.sharespace.api.model.ShareHousing;
import com.lmtri.sharespace.customview.CustomViewPager;
import com.lmtri.sharespace.fragment.profile.historyphoto.HistoryHousingPhotoFragment;
import com.lmtri.sharespace.fragment.profile.historyphoto.HistoryShareHousingPhotoFragment;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.listener.OnHousingListInteractionListener;
import com.lmtri.sharespace.listener.OnShareHousingListInteractionListener;

public class HistoryPhotoActivity extends AppCompatActivity {

    private int mSelectedTab;
    private TabLayout mTabLayout;
    private CustomViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;

    private ViewPager.SimpleOnPageChangeListener mViewPagerOnPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            mTabLayout.getTabAt(position).select();
        }
    };

    private OnHousingListInteractionListener mHousingListInteractionListener = new OnHousingListInteractionListener() {
        @Override
        public void onHousingListInteraction(Housing item) {
            Intent intent = new Intent(HistoryPhotoActivity.this, HousingDetailActivity.class);

            Gson gson = new Gson();
            intent.putExtra(Constants.ACTIVITY_HOUSING_DETAIL_HOUSING_EXTRA, gson.toJson(item));

            startActivityForResult(intent, Constants.START_ACTIVITY_HOUSING_DETAIL);
        }
    };
    private OnShareHousingListInteractionListener mShareHousingListInteractionListener = new OnShareHousingListInteractionListener() {
        @Override
        public void onShareHousingListInteraction(ShareHousing item) {
            Intent intent = new Intent(HistoryPhotoActivity.this, ShareHousingDetailActivity.class);

            Gson gson = new Gson();
            intent.putExtra(Constants.ACTIVITY_SHARE_HOUSING_DETAIL_SHARE_HOUSING_EXTRA, gson.toJson(item));

            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_photo);

        mSelectedTab = getIntent().getIntExtra(Constants.SELECTED_TAB_INDEX_INTERESTED_SHARE_HOUSING, 0);

        mTabLayout = (TabLayout) findViewById(R.id.activity_history_photo_tab_layout);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.fragment_interested_housing_tab_text));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.fragment_interested_share_housing_tab_text));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.setTabTextColors(ContextCompat.getColor(this, R.color.colorPrimary), ContextCompat.getColor(this, R.color.colorPrimary));
        mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.colorPrimary));
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

        mViewPager = (CustomViewPager) findViewById(R.id.activity_history_photo_view_pager);
        mViewPager.setOffscreenPageLimit(1);

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        Fragment fragment1 = HistoryHousingPhotoFragment.newInstance(1);
        ((HistoryHousingPhotoFragment) fragment1).setListener(mHousingListInteractionListener);
        mViewPagerAdapter.addFragment(fragment1);

        Fragment fragment2 = HistoryShareHousingPhotoFragment.newInstance(1);
        ((HistoryShareHousingPhotoFragment) fragment2).setListener(mShareHousingListInteractionListener);
        mViewPagerAdapter.addFragment(fragment2);

        mViewPager.setAdapter(mViewPagerAdapter);

        mTabLayout.getTabAt(mSelectedTab).select();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mViewPager.addOnPageChangeListener(mViewPagerOnPageChangeListener);
    }

    @Override
    protected void onStop() {
        mViewPager.removeOnPageChangeListener(mViewPagerOnPageChangeListener);
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.START_ACTIVITY_HOUSING_DETAIL) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }
}
