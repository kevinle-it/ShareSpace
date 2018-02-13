package com.lmtri.sharespace.activity.sharetab.search;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.gson.Gson;
import com.lmtri.sharespace.R;
import com.lmtri.sharespace.ShareSpaceApplication;
import com.lmtri.sharespace.activity.sharetab.ShareHousingDetailActivity;
import com.lmtri.sharespace.adapter.ViewPagerAdapter;
import com.lmtri.sharespace.api.model.ShareHousing;
import com.lmtri.sharespace.customview.CustomViewPager;
import com.lmtri.sharespace.fragment.share.search.SearchResultShareHousingListFragment;
import com.lmtri.sharespace.fragment.share.search.SearchResultShareHousingMapFragment;
import com.lmtri.sharespace.fragment.share.search.SearchShareHousingInputDataFragment;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.busevent.sharehousing.search.ReturnSearchShareHousingResultEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.search.SearchShareHousingMapListViewBackButtonEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.search.SearchShareHousingSwitchListViewEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.search.SearchShareHousingSwitchMapViewEvent;
import com.lmtri.sharespace.listener.OnShareHousingListInteractionListener;
import com.squareup.otto.Subscribe;

public class SearchShareHousingActivity extends AppCompatActivity {

    public static final String TAG = SearchShareHousingActivity.class.getSimpleName();

    private CustomViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private SearchShareHousingInputDataFragment mSearchShareHousingInputDataFragment;

    private OnShareHousingListInteractionListener mShareHousingListInteractionListener
            = new OnShareHousingListInteractionListener() {
        @Override
        public void onShareHousingListInteraction(ShareHousing item) {
            Intent intent = new Intent(SearchShareHousingActivity.this, ShareHousingDetailActivity.class);

            Gson gson = new Gson();
            intent.putExtra(Constants.ACTIVITY_SHARE_HOUSING_DETAIL_SHARE_HOUSING_EXTRA, gson.toJson(item));

            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_share_housing);
        ShareSpaceApplication.BUS.register(this);

        mViewPager = (CustomViewPager) findViewById(R.id.activity_search_share_housing_view_pager);
        mViewPager.setPagingEnabled(false);
        mViewPager.setOffscreenPageLimit(Constants.SEARCH_HOUSING_VIEW_PAGER_OFF_SCREEN_PAGE_LIMIT);

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        mSearchShareHousingInputDataFragment = mSearchShareHousingInputDataFragment.newInstance();
        mViewPagerAdapter.addFragment(mSearchShareHousingInputDataFragment);

        SearchResultShareHousingListFragment fragment1 = SearchResultShareHousingListFragment.newInstance(1);
        fragment1.setListener(mShareHousingListInteractionListener);
        mViewPagerAdapter.addFragment(fragment1);

        SearchResultShareHousingMapFragment fragment2 = SearchResultShareHousingMapFragment.newInstance();
        fragment2.setListener(mShareHousingListInteractionListener);
        mViewPagerAdapter.addFragment(fragment2);

        mViewPager.setAdapter(mViewPagerAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mSearchShareHousingInputDataFragment.onActivityResult(requestCode, resultCode, data);   // For Place Auto Complete Fragment' onPlaceSelected Callback to be called after User selected a Place
                                                                                                // and Place Auto Complete Edit Text to be filled in.
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = mViewPagerAdapter.getItem(mViewPager.getCurrentItem());
        switch (mViewPager.getCurrentItem()) {
            case Constants.SEARCH_HOUSING_VIEW_PAGER_INDEX_INPUT_SEARCHING_DATA:
                super.onBackPressed();
                break;
            case Constants.SEARCH_HOUSING_VIEW_PAGER_INDEX_LIST_RESULT:
                if (!((SearchResultShareHousingListFragment) currentFragment).onBackPressed()) {
                    mViewPager.setCurrentItem(Constants.SEARCH_HOUSING_VIEW_PAGER_INDEX_INPUT_SEARCHING_DATA, true);
                }
                break;
            case Constants.SEARCH_HOUSING_VIEW_PAGER_INDEX_MAP_RESULT:
                if (!((SearchResultShareHousingMapFragment) currentFragment).onBackPressed()) {
                    mViewPager.setCurrentItem(Constants.SEARCH_HOUSING_VIEW_PAGER_INDEX_INPUT_SEARCHING_DATA, true);
                }
                break;
            default:
                super.onBackPressed();
                overridePendingTransition(R.anim.stay_still, R.anim.slide_down_out);
                break;
        }
    }

    @Subscribe
    public void returnSearchShareHousingResults(ReturnSearchShareHousingResultEvent event) {
        mViewPager.setCurrentItem(Constants.SEARCH_HOUSING_VIEW_PAGER_INDEX_LIST_RESULT, true);
    }

    @Subscribe
    public void searchShareHousingSwitchMapViewEvent(SearchShareHousingSwitchMapViewEvent event) {
        mViewPager.setCurrentItem(Constants.SEARCH_HOUSING_VIEW_PAGER_INDEX_MAP_RESULT, false);
    }

    @Subscribe
    public void searchShareHousingSwitchListViewEvent(SearchShareHousingSwitchListViewEvent event) {
        mViewPager.setCurrentItem(Constants.SEARCH_HOUSING_VIEW_PAGER_INDEX_LIST_RESULT, false);
    }

    @Subscribe
    public void searchShareHousingMapListViewBackButtonEvent(SearchShareHousingMapListViewBackButtonEvent event) {
        mViewPager.setCurrentItem(Constants.SEARCH_HOUSING_VIEW_PAGER_INDEX_INPUT_SEARCHING_DATA, true);
    }
}
