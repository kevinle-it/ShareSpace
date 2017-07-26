package com.lmtri.sharespace.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.lmtri.sharespace.R;
import com.lmtri.sharespace.activity.hometab.HousingDetailActivity;
import com.lmtri.sharespace.adapter.ViewPagerAdapter;
import com.lmtri.sharespace.customview.CustomViewPager;
import com.lmtri.sharespace.fragment.RootFragment;
import com.lmtri.sharespace.fragment.home.HousingFragment;
import com.lmtri.sharespace.fragment.profile.ProfileFragment;
import com.lmtri.sharespace.fragment.saved.SavedFragment;
import com.lmtri.sharespace.helper.BottomNavigationViewHelper;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.model.Housing;

public class MainActivity extends AppCompatActivity implements
        HousingFragment.OnListFragmentInteractionListener,
        SavedFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    private BottomNavigationView mBottomNavigationView;
    private CustomViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;

    private MenuItem mPrevMenuItem;

    private ViewPager.SimpleOnPageChangeListener mViewPagerOnPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            if (mPrevMenuItem != null) {
                mPrevMenuItem.setChecked(false);
            }
            else
            {
                mBottomNavigationView.getMenu().getItem(0).setChecked(false);
            }
            mBottomNavigationView.getMenu().getItem(position).setChecked(true);
            mPrevMenuItem = mBottomNavigationView.getMenu().getItem(position);
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case Constants.NAVIGATION_INDEX_HOME:
                    if (mViewPager.getCurrentItem() == Constants.VIEW_PAGER_INDEX_HOME) {
                        return false;
                    } else {
                        Toast.makeText(getApplicationContext(), "Fragment 1", Toast.LENGTH_SHORT).show();
                        mViewPager.setCurrentItem(Constants.VIEW_PAGER_INDEX_HOME, false);
                    }
                    break;
                case Constants.NAVIGATION_INDEX_SAVED:
                    if (mViewPager.getCurrentItem() == Constants.VIEW_PAGER_INDEX_SAVED) {
                        return false;
                    } else {
                        Toast.makeText(getApplicationContext(), "Fragment 2", Toast.LENGTH_SHORT).show();
                        mViewPager.setCurrentItem(Constants.VIEW_PAGER_INDEX_SAVED, false);
                    }
                    break;
                case Constants.NAVIGATION_INDEX_SHARE:
                    if (mViewPager.getCurrentItem() == Constants.VIEW_PAGER_INDEX_SHARE) {
                        return false;
                    } else {
                        Toast.makeText(getApplicationContext(), "Fragment 3", Toast.LENGTH_SHORT).show();
                        mViewPager.setCurrentItem(Constants.VIEW_PAGER_INDEX_SHARE, false);
                    }
                    break;
                case Constants.NAVIGATION_INDEX_INBOX:
                    if (mViewPager.getCurrentItem() == Constants.VIEW_PAGER_INDEX_INBOX) {
                        return false;
                    } else {
                        Toast.makeText(getApplicationContext(), "Fragment 4", Toast.LENGTH_SHORT).show();
                        mViewPager.setCurrentItem(Constants.VIEW_PAGER_INDEX_INBOX, false);
                    }
                    break;
                case Constants.NAVIGATION_INDEX_PROFILE:
                    if (mViewPager.getCurrentItem() == Constants.VIEW_PAGER_INDEX_PROFILE) {
                        return false;
                    } else {
                        Toast.makeText(getApplicationContext(), "Fragment 5", Toast.LENGTH_SHORT).show();
                        mViewPager.setCurrentItem(Constants.VIEW_PAGER_INDEX_PROFILE, false);
                    }
                    break;
            }
            return true;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize View Pager.
        mViewPager = (CustomViewPager) findViewById(R.id.view_pager);
        mViewPager.setPagingEnabled(false);
        mViewPager.setOffscreenPageLimit(Constants.VIEW_PAGER_OFF_SCREEN_PAGE_LIMIT);

        // Initialize Bottom Navigation View
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(mBottomNavigationView);

        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, Constants.START_ACTIVITY_LOGIN_REQUEST);
    }

    @Override
    protected void onStart() {
        mViewPager.addOnPageChangeListener(mViewPagerOnPageChangeListener);
        super.onStart();
    }

    @Override
    protected void onStop() {
        mViewPager.removeOnPageChangeListener(mViewPagerOnPageChangeListener);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
//        Fragment currentFragment = ((RootFragment) mViewPagerAdapter.getItem(mViewPager.getCurrentItem())).getCurrentFragment();
//        if (currentFragment instanceof PostHistoryFragment) {
//            ((PostHistoryFragment) currentFragment).onBackPressed();
//        } else {
            super.onBackPressed();
//        }
    }

    private void setupViewPager(CustomViewPager viewPager) {
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPagerAdapter.addFragment(RootFragment.newInstance(Constants.VIEW_PAGER_INDEX_HOME));
        mViewPagerAdapter.addFragment(RootFragment.newInstance(Constants.VIEW_PAGER_INDEX_SAVED));
        mViewPagerAdapter.addFragment(RootFragment.newInstance(Constants.VIEW_PAGER_INDEX_SHARE));
        mViewPagerAdapter.addFragment(RootFragment.newInstance(Constants.VIEW_PAGER_INDEX_INBOX));
        mViewPagerAdapter.addFragment(RootFragment.newInstance(Constants.VIEW_PAGER_INDEX_PROFILE));
        viewPager.setAdapter(mViewPagerAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);  // For Fragments to consume onActivityResult
                                                                // (of Activities start by Fragments) before MainActivity.
        if (requestCode == Constants.START_ACTIVITY_LOGIN_REQUEST) {
            if (resultCode == RESULT_OK) {
                setupViewPager(mViewPager);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onListFragmentInteraction(Housing item) {
        Intent intent = new Intent(this, HousingDetailActivity.class);
        intent.putExtra(Constants.ACTIVITY_HOUSING_DETAIL_PROFILE_IMAGE_URL_EXTRA, item.getProfileImageUrl().toString());
        if (item.getTitle() != null && !item.getTitle().isEmpty()) {
            intent.putExtra(Constants.ACTIVITY_HOUSING_DETAIL_HOUSE_TITLE_EXTRA, item.getTitle());
        }
        if (item.getPrice() != null && !item.getPrice().isEmpty()) {
            intent.putExtra(Constants.ACTIVITY_HOUSING_DETAIL_PRICE_EXTRA, item.getPrice());
        }
        if (item.getDetailsInfo() != null && !item.getDetailsInfo().isEmpty()) {
            intent.putExtra(Constants.ACTIVITY_HOUSING_DETAIL_DETAILS_EXTRA, item.getDetailsInfo());
        }
        if (item.getAddressHouseNumber() != null && !item.getAddressHouseNumber().isEmpty()) {
            intent.putExtra(Constants.ACTIVITY_HOUSING_DETAIL_HOUSE_NUMBER_EXTRA, item.getAddressHouseNumber());
        }
        if (item.getAddressStreet() != null && !item.getAddressStreet().isEmpty()) {
            intent.putExtra(Constants.ACTIVITY_HOUSING_DETAIL_STREET_EXTRA, item.getAddressStreet());
        }
        if (item.getAddressWard() != null && !item.getAddressWard().isEmpty()) {
            intent.putExtra(Constants.ACTIVITY_HOUSING_DETAIL_WARD_EXTRA, item.getAddressWard());
        }
        if (item.getAddressDistrict() != null && !item.getAddressDistrict().isEmpty()) {
            intent.putExtra(Constants.ACTIVITY_HOUSING_DETAIL_DISTRICT_EXTRA, item.getAddressDistrict());
        }
        if (item.getAddressCity() != null && !item.getAddressCity().isEmpty()) {
            intent.putExtra(Constants.ACTIVITY_HOUSING_DETAIL_CITY_EXTRA, item.getAddressCity());
        }
        if (item.getHouseType() != null && !item.getHouseType().isEmpty()) {
            intent.putExtra(Constants.ACTIVITY_HOUSING_DETAIL_HOUSE_TYPE_EXTRA, item.getHouseType());
        }
        if (item.getOwnerName() != null && !item.getOwnerName().isEmpty()) {
            intent.putExtra(Constants.ACTIVITY_HOUSING_DETAIL_OWNER_NAME_EXTRA, item.getOwnerName());
        }
        startActivity(intent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
