package com.lmtri.sharespace.activity.hometab;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lmtri.sharespace.R;
import com.lmtri.sharespace.activity.postsharehouse.PostShareHouseActivity;
import com.lmtri.sharespace.activity.sharetab.ShareHousingDetailActivity;
import com.lmtri.sharespace.adapter.sharetab.ShareHousingRecyclerViewAdapter;
import com.lmtri.sharespace.api.model.Housing;
import com.lmtri.sharespace.api.model.ShareHousing;
import com.lmtri.sharespace.api.service.RetrofitClient;
import com.lmtri.sharespace.api.service.sharehousing.ShareHousingClient;
import com.lmtri.sharespace.api.service.sharehousing.get.IGetMoreOlderShareOfHousingCallback;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.listener.EndlessRecyclerViewScrollListener;
import com.lmtri.sharespace.listener.OnShareHousingListInteractionListener;

import java.util.ArrayList;
import java.util.List;

public class ShowExistSharePostsOfCurrentHousingActivity extends AppCompatActivity {

    public static final String TAG = ShowExistSharePostsOfCurrentHousingActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private TextView mToolbarPostFindingRoommateTextView;

    private LinearLayout mConnectionErrorLayout;
    private TextView mConnectionErrorMessage;
    private ViewSwitcher mViewSwitcherRetryConnect;
    private FrameLayout mRetryConnectButton;

    private Housing mCurrentHousing;
    private int mCurrentHousingID;

    private ArrayList<ShareHousing> mShareHousings = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ShareHousingRecyclerViewAdapter mShareHousingRecyclerViewAdapter;
    private EndlessRecyclerViewScrollListener mRecyclerViewScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_exist_share_post_of_current_housing);

        mConnectionErrorLayout = (LinearLayout) findViewById(R.id.activity_show_exist_share_posts_of_current_housing_connection_error_layout);
        mConnectionErrorMessage = (TextView) findViewById(R.id.activity_show_exist_share_posts_of_current_housing_connection_error_message);
        mViewSwitcherRetryConnect = (ViewSwitcher) findViewById(R.id.activity_show_exist_share_posts_of_current_housing_view_switcher_retry_connect_to_server);
        mRetryConnectButton = (FrameLayout) findViewById(R.id.activity_show_exist_share_posts_of_current_housing_retry_button);
        mRetryConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewSwitcherRetryConnect.showNext();
                loadMoreOlderShareHousingsFromApi(0);
            }
        });

        mToolbar = (Toolbar) findViewById(R.id.activity_show_exist_share_posts_of_current_housing_toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_left_arrow));
        mToolbar.getNavigationIcon().setColorFilter(ContextCompat.getColor(this, android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        mToolbarPostFindingRoommateTextView = (TextView) findViewById(R.id.activity_show_exist_share_posts_of_current_housing_toolbar_post_finding_roommate);
        mToolbarPostFindingRoommateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        ShowExistSharePostsOfCurrentHousingActivity.this,
                        PostShareHouseActivity.class
                );
                intent.putExtra(Constants.IS_FIND_ROOMMATE_EXTRA, true);
                Gson gson = new Gson();
                intent.putExtra(
                        Constants.HOUSING_INFO_FOR_FINDING_ROOMMATE_EXTRA,
                        gson.toJson(mCurrentHousing)
                );
                startActivityForResult(intent, Constants.START_ACTIVITY_POST_SHARE_HOUSE_REQUEST);
            }
        });

        Gson gson = new Gson();
        try {
            mCurrentHousing = gson.fromJson(
                    getIntent().getStringExtra(Constants.ACTIVITY_HOUSING_DETAIL_FIND_ROOMMATE_CURRENT_HOUSING_INFO_EXTRA),
                    Housing.class
            );
            if (mCurrentHousing != null) {
                mCurrentHousingID = mCurrentHousing.getID();
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.activity_show_exist_share_posts_of_current_housing_share_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager, false) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadMoreOlderShareHousingsFromApi(page);
            }
        };

        mShareHousingRecyclerViewAdapter = new ShareHousingRecyclerViewAdapter(this, new ArrayList<ShareHousing>(), mShareHousings, new OnShareHousingListInteractionListener() {
            @Override
            public void onShareHousingListInteraction(ShareHousing item) {
                Intent intent = new Intent(ShowExistSharePostsOfCurrentHousingActivity.this, ShareHousingDetailActivity.class);
                Gson gson = new Gson();
                intent.putExtra(Constants.ACTIVITY_SHARE_HOUSING_DETAIL_SHARE_HOUSING_EXTRA, gson.toJson(item));
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mShareHousingRecyclerViewAdapter);
        mRecyclerView.addOnScrollListener(mRecyclerViewScrollListener);

        loadMoreOlderShareHousingsFromApi(0);
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadMoreOlderShareHousingsFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
//        HousingClient.getMoreOlderHousings(mHousings, offset, mHousingRecyclerViewAdapter);
        ShareHousingClient.getMoreOlderShareOfHousing(mCurrentHousingID, offset, new IGetMoreOlderShareOfHousingCallback() {
            @Override
            public void onGetComplete(List<ShareHousing> shareHousings) {
                if (mShareHousings.size() == 0) {
                    if (shareHousings != null && shareHousings.size() > 0) {
                        if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                            mViewSwitcherRetryConnect.showNext();
                        }
                        mConnectionErrorLayout.setVisibility(View.GONE);

                        int positionStart = mShareHousings.size();

                        mShareHousings.addAll(shareHousings);

//                        mShareHousingRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mShareHousings.size() - positionStart);
                        mShareHousingRecyclerViewAdapter.notifyDataSetChanged();
                    } else {
                        mConnectionErrorMessage.setVisibility(View.VISIBLE);
                        if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                            mViewSwitcherRetryConnect.showNext();
                        }
                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(ShowExistSharePostsOfCurrentHousingActivity.this);
                    }
                } else if (mShareHousings.size() > 0) {
                    if (shareHousings != null && shareHousings.size() > 0) {
                        int positionStart = mShareHousings.size();

                        mShareHousings.addAll(shareHousings);

//                        mShareHousingRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mShareHousings.size() - positionStart);
                        mShareHousingRecyclerViewAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onGetFailure(Throwable t) {
                mConnectionErrorMessage.setVisibility(View.VISIBLE);
                if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                    mViewSwitcherRetryConnect.showNext();
                }
                RetrofitClient.showShareSpaceServerConnectionErrorDialog(ShowExistSharePostsOfCurrentHousingActivity.this, t);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.START_ACTIVITY_POST_SHARE_HOUSE_REQUEST) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }
}
