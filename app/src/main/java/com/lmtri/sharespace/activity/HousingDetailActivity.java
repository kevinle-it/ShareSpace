package com.lmtri.sharespace.activity;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lmtri.sharespace.R;
import com.lmtri.sharespace.helper.GlideCircleTransform;

public class HousingDetailActivity extends AppCompatActivity {

    public static final String TAG = HousingDetailActivity.class.getSimpleName();

    private AppBarLayout mAppBarLayout;
    private ImageView mHouseProfileImage;
    private Toolbar mToolbar;
    private NestedScrollView mNestedScrollView;
    private ImageView mHouseHostProfileImage;

    private int mProfileImageHeight;
    private int mActionBarSize;

    private AnimatorSet mAnimatorSet;
    private ValueAnimator mWhiteToTransparentToolbarValueAnimator;
    private ValueAnimator mDrakerGrayToWhiteToolbarButtonValueAnimator;
    private boolean mIsToolbarTransparent = true;

    private boolean mIsDraggingUp = true;
    private int mMaxNegativeAppBarLayoutVerticalOffset = 0;
    private int mScrollViewScrollY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_housing_detail);

        final int whiteColor = ContextCompat.getColor(getApplicationContext(), android.R.color.white);   // A = 255, R = 255, G = 255, B = 255
        final int darkerGrayColor = ContextCompat.getColor(getApplicationContext(), android.R.color.darker_gray);    // A = 255, R = 170, G = 170, B = 170
        final int transparentColor = ContextCompat.getColor(getApplicationContext(), android.R.color.transparent);   // A = 0, R = 0, G = 0, B = 0

        TypedArray styledAttributes = this.getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize });
        mActionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        mProfileImageHeight = getApplicationContext().getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_house_profile_image_height);

        mWhiteToTransparentToolbarValueAnimator = ValueAnimator.ofInt(
                Color.alpha(whiteColor),
                Color.alpha(transparentColor)
        );
        mWhiteToTransparentToolbarValueAnimator.setDuration(400);
        mWhiteToTransparentToolbarValueAnimator.setInterpolator(new DecelerateInterpolator());
        mWhiteToTransparentToolbarValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int newValue = (int) animation.getAnimatedValue();
                mToolbar.setBackgroundColor(
                        Color.argb(
                                newValue,
                                Color.red(transparentColor),
                                Color.green(transparentColor),
                                Color.blue(transparentColor)
                        )
                );
            }
        });

        mDrakerGrayToWhiteToolbarButtonValueAnimator = ValueAnimator.ofInt(
                Color.green(darkerGrayColor),
                Color.green(whiteColor)
        );
        mDrakerGrayToWhiteToolbarButtonValueAnimator.setDuration(400);
        mDrakerGrayToWhiteToolbarButtonValueAnimator.setInterpolator(new DecelerateInterpolator());
        mDrakerGrayToWhiteToolbarButtonValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int newValue = (int) animation.getAnimatedValue();
                mToolbar.getNavigationIcon().setColorFilter(
                        Color.argb(
                                Color.alpha(whiteColor),
                                newValue,
                                newValue,
                                newValue
                        ),
                        PorterDuff.Mode.SRC_ATOP
                );
            }
        });

        mAppBarLayout = (AppBarLayout) findViewById(R.id.activity_housing_detail_appbar_container);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset < mMaxNegativeAppBarLayoutVerticalOffset) {  // ScrollView is scrolling down AND Hand gesture is DRAGGING UP.
                    mMaxNegativeAppBarLayoutVerticalOffset = verticalOffset;
                    mIsDraggingUp = true;
                } else if (verticalOffset > mMaxNegativeAppBarLayoutVerticalOffset){    // ScrollView is scrolling up AND Hand gesture is DRAGGING DOWN.
                    mMaxNegativeAppBarLayoutVerticalOffset = verticalOffset;
                    mIsDraggingUp = false;
                }
                if (!mIsDraggingUp) {
                    if (verticalOffset < -(mProfileImageHeight - mActionBarSize)) {
                        mToolbar.getNavigationIcon()
                                .setColorFilter(darkerGrayColor, PorterDuff.Mode.SRC_ATOP);
                        mToolbar.setBackgroundColor(whiteColor);
                        mIsToolbarTransparent = false;
                    }
                    else if (!mIsToolbarTransparent && mScrollViewScrollY == 0) {
                        mAnimatorSet = new AnimatorSet();

                        mAnimatorSet.play(mDrakerGrayToWhiteToolbarButtonValueAnimator)
                                .with(mWhiteToTransparentToolbarValueAnimator);
                        mAnimatorSet.start();

                        mIsToolbarTransparent = true;
                    }
                }
            }
        });

        mToolbar = (Toolbar) findViewById(R.id.activity_housing_detail_toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbar.getNavigationIcon().setColorFilter(whiteColor, PorterDuff.Mode.SRC_ATOP);
        mToolbar.setBackgroundColor(transparentColor);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.stay_still, R.anim.slide_down_out);
            }
        });

        mNestedScrollView = (NestedScrollView) findViewById(R.id.activity_housing_detail_nested_scroll_view);
        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                mScrollViewScrollY = scrollY;
            }
        });

        mHouseProfileImage = (ImageView) findViewById(R.id.activity_housing_detail_house_profile_image);
        mHouseHostProfileImage = (ImageView) findViewById(R.id.activity_housing_detail_house_host_profile_image);
        
        Intent getIntent = getIntent();
        String url = getIntent.getStringExtra("EXTRA_SESSION_ID");

        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.ic_home)
                .into(mHouseProfileImage);

        Glide.with(this)
                .load(R.drawable.profile_image)
                .placeholder(R.drawable.ic_profile_picture)
                .crossFade()
                .transform(new GlideCircleTransform(this))
                .into(mHouseHostProfileImage);
    }
}
