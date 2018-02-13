package com.lmtri.sharespace.activity;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lmtri.sharespace.R;
import com.lmtri.sharespace.adapter.ImageViewerAdapter;
import com.lmtri.sharespace.helper.Constants;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

public class ImageViewerActivity extends AppCompatActivity {
    public static final String TAG = ImageViewerActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private ArrayList<String> mPhotoURLs;
    private int mCurrentPhotoIndex;

    private ViewPager mImageViewer;
    private CircleIndicator mImageViewerIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        mPhotoURLs = getIntent().getStringArrayListExtra(Constants.ACTIVITY_IMAGE_VIEWER_PHOTO_URLS);
        mCurrentPhotoIndex = getIntent().getIntExtra(Constants.ACTIVITY_IMAGE_VIEWER_CURRENT_PHOTO_INDEX, 0);

        mToolbar = (Toolbar) findViewById(R.id.activity_image_viewer_toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbar.getNavigationIcon().setColorFilter(
                ContextCompat.getColor(getApplicationContext(), android.R.color.white),
                PorterDuff.Mode.SRC_ATOP
        );
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.stay_still, R.anim.slide_down_out);
            }
        });

        mImageViewer = (ViewPager) findViewById(R.id.activity_image_viewer_view_pager);
        mImageViewerIndicator = (CircleIndicator) findViewById(R.id.activity_image_viewer_indicator);

        if (mPhotoURLs.size() > 0) {
            mImageViewer.setAdapter(new ImageViewerAdapter(mPhotoURLs, true));
            mImageViewerIndicator.setViewPager(mImageViewer);
            mImageViewer.setCurrentItem(mCurrentPhotoIndex);
        }
    }
}
