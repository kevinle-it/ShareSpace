package com.lmtri.sharespace.activity.hometab.search;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.gson.Gson;
import com.lmtri.sharespace.R;
import com.lmtri.sharespace.ShareSpaceApplication;
import com.lmtri.sharespace.activity.hometab.HousingDetailActivity;
import com.lmtri.sharespace.adapter.ViewPagerAdapter;
import com.lmtri.sharespace.api.model.Housing;
import com.lmtri.sharespace.customview.CustomViewPager;
import com.lmtri.sharespace.fragment.home.search.SearchHousingInputDataFragment;
import com.lmtri.sharespace.fragment.home.search.SearchResultHousingListFragment;
import com.lmtri.sharespace.fragment.home.search.SearchResultHousingMapFragment;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.busevent.housing.search.ReturnSearchHousingResultEvent;
import com.lmtri.sharespace.helper.busevent.housing.search.SearchHousingMapListViewBackButtonEvent;
import com.lmtri.sharespace.helper.busevent.housing.search.SearchHousingPostShareOfExistHousingEvent;
import com.lmtri.sharespace.helper.busevent.housing.search.SearchHousingSwitchListViewEvent;
import com.lmtri.sharespace.helper.busevent.housing.search.SearchHousingSwitchMapViewEvent;
import com.lmtri.sharespace.listener.OnHousingListInteractionListener;
import com.squareup.otto.Subscribe;

public class SearchHousingActivity extends AppCompatActivity {

    public static final String TAG = SearchHousingActivity.class.getSimpleName();

    private CustomViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private SearchHousingInputDataFragment mSearchHousingInputDataFragment;

    private OnHousingListInteractionListener mHousingListInteractionListener
            = new OnHousingListInteractionListener() {
        @Override
        public void onHousingListInteraction(Housing item) {
            Intent intent = new Intent(SearchHousingActivity.this, HousingDetailActivity.class);

            Gson gson = new Gson();
            intent.putExtra(Constants.ACTIVITY_HOUSING_DETAIL_HOUSING_EXTRA, gson.toJson(item));

            startActivityForResult(intent, Constants.START_ACTIVITY_HOUSING_DETAIL);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_housing);
        ShareSpaceApplication.BUS.register(this);

        mViewPager = (CustomViewPager) findViewById(R.id.activity_search_housing_view_pager);
        mViewPager.setPagingEnabled(false);
        mViewPager.setOffscreenPageLimit(Constants.SEARCH_HOUSING_VIEW_PAGER_OFF_SCREEN_PAGE_LIMIT);

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        mSearchHousingInputDataFragment = SearchHousingInputDataFragment.newInstance();
        mViewPagerAdapter.addFragment(mSearchHousingInputDataFragment);

        SearchResultHousingListFragment fragment1 = SearchResultHousingListFragment.newInstance(1);
        fragment1.setListener(mHousingListInteractionListener);
        mViewPagerAdapter.addFragment(fragment1);

        SearchResultHousingMapFragment fragment2 = SearchResultHousingMapFragment.newInstance();
        fragment2.setListener(mHousingListInteractionListener);
        mViewPagerAdapter.addFragment(fragment2);

        mViewPager.setAdapter(mViewPagerAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mSearchHousingInputDataFragment.onActivityResult(requestCode, resultCode, data);    // For Place Auto Complete Fragment' onPlaceSelected Callback to be called after User selected a Place
                                                                                            // and Place Auto Complete Edit Text to be filled in.
        if (requestCode == Constants.START_ACTIVITY_HOUSING_DETAIL) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
                overridePendingTransition(R.anim.stay_still, R.anim.slide_down_out);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = mViewPagerAdapter.getItem(mViewPager.getCurrentItem());
        switch (mViewPager.getCurrentItem()) {
            case Constants.SEARCH_HOUSING_VIEW_PAGER_INDEX_INPUT_SEARCHING_DATA:
                super.onBackPressed();
                break;
            case Constants.SEARCH_HOUSING_VIEW_PAGER_INDEX_LIST_RESULT:
                if (!((SearchResultHousingListFragment) currentFragment).onBackPressed()) {
                    mViewPager.setCurrentItem(Constants.SEARCH_HOUSING_VIEW_PAGER_INDEX_INPUT_SEARCHING_DATA, true);
                }
                break;
            case Constants.SEARCH_HOUSING_VIEW_PAGER_INDEX_MAP_RESULT:
                if (!((SearchResultHousingMapFragment) currentFragment).onBackPressed()) {
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
    public void returnSearchHousingResults(ReturnSearchHousingResultEvent event) {
        mViewPager.setCurrentItem(Constants.SEARCH_HOUSING_VIEW_PAGER_INDEX_LIST_RESULT, true);
    }

    @Subscribe
    public void searchHousingSwitchMapViewEvent(SearchHousingSwitchMapViewEvent event) {
        mViewPager.setCurrentItem(Constants.SEARCH_HOUSING_VIEW_PAGER_INDEX_MAP_RESULT, false);
    }

    @Subscribe
    public void searchHousingSwitchListViewEvent(SearchHousingSwitchListViewEvent event) {
        mViewPager.setCurrentItem(Constants.SEARCH_HOUSING_VIEW_PAGER_INDEX_LIST_RESULT, false);
    }

    @Subscribe
    public void searchHousingMapListViewBackButtonEvent(SearchHousingMapListViewBackButtonEvent event) {
        mViewPager.setCurrentItem(Constants.SEARCH_HOUSING_VIEW_PAGER_INDEX_INPUT_SEARCHING_DATA, true);
    }
}
