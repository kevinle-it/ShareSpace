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
import com.lmtri.sharespace.adapter.ViewPagerAdapter;
import com.lmtri.sharespace.customview.CustomViewPager;
import com.lmtri.sharespace.fragment.HousingFragment;
import com.lmtri.sharespace.fragment.RootFragment;
import com.lmtri.sharespace.fragment.SavedFragment;
import com.lmtri.sharespace.helper.BottomNavigationViewHelper;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.model.Housing;

public class MainActivity extends AppCompatActivity implements HousingFragment.OnListFragmentInteractionListener, SavedFragment.OnFragmentInteractionListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    private BottomNavigationView mBottomNavigationView;
    private CustomViewPager mViewPager;

    private MenuItem mPrevMenuItem;

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (mPrevMenuItem != null) {
                mPrevMenuItem.setChecked(false);
            }
            else
            {
                mBottomNavigationView.getMenu().getItem(0).setChecked(false);
            }
            Log.d("page", "onPageSelected: " + position);
            mBottomNavigationView.getMenu().getItem(position).setChecked(true);
            mPrevMenuItem = mBottomNavigationView.getMenu().getItem(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

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
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
        mViewPager.setPagingEnabled(false);
        mViewPager.setOffscreenPageLimit(Constants.VIEW_PAGER_OFF_SCREEN_PAGE_LIMIT);

        // Initialize Bottom Navigation View
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(mBottomNavigationView);
        
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, Constants.LOGIN_REQUEST);
    }

    private void setupViewPager(CustomViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(RootFragment.newInstance(Constants.VIEW_PAGER_INDEX_HOME));
        adapter.addFragment(RootFragment.newInstance(Constants.VIEW_PAGER_INDEX_SAVED));
        adapter.addFragment(RootFragment.newInstance(Constants.VIEW_PAGER_INDEX_SHARE));
        adapter.addFragment(RootFragment.newInstance(Constants.VIEW_PAGER_INDEX_INBOX));
        adapter.addFragment(RootFragment.newInstance(Constants.VIEW_PAGER_INDEX_PROFILE));
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.LOGIN_REQUEST) {
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
        Toast.makeText(getApplicationContext(), item.getId(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), HousingDetailActivity.class);
        intent.putExtra("EXTRA_SESSION_ID", item.getProfileImageUrl().toString());
        startActivity(intent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
