package com.lmtri.sharespace.activity.hometab;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lmtri.sharespace.R;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.GlideCircleTransform;

public class HousingDetailActivity extends AppCompatActivity {

    public static final String TAG = HousingDetailActivity.class.getSimpleName();

    private AppBarLayout mAppBarLayout;
    private ImageView mHouseProfileImage;

    private Toolbar mToolbar;

    private ImageView mHeartButton;
    private boolean mIsWhiteHeart = true;
    private Drawable mDarkerGrayHeartDrawable;
    private Drawable mWrappedDarkerGrayHeartMutableDrawable;

    private ImageView mFindRoommateButton;
    private ImageView mEditPostButton;

    private NestedScrollView mNestedScrollView;
    private TextView mHouseTitle;
    private TextView mPrice;
    private TextView mDetails;
    private TextView mHouseNumber;
    private TextView mStreet;
    private TextView mWard;
    private TextView mDistrict;
    private TextView mCity;
    private TextView mHouseType;
    private TextView mOwnerName;
    private ImageView mHouseHostProfileImage;

    private int mProfileImageHeight;
    private int mActionBarSize;

    private AnimatorSet mAnimatorSet;
    private ValueAnimator mWhiteToTransparentToolbarValueAnimator;
    private ValueAnimator mDrakerGrayToWhiteToolbarButtonValueAnimator;
    private boolean mIsToolbarTransparent = true;

    private boolean mIsDraggingDown = false;
    private int mMaxNegativeAppBarLayoutVerticalOffset = 0;
    private int mScrollViewScrollY = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_housing_detail);

        final int whiteColor = ContextCompat.getColor(getApplicationContext(), android.R.color.white);   // A = 255, R = 255, G = 255, B = 255
        final int darkerGrayColor = ContextCompat.getColor(getApplicationContext(), android.R.color.darker_gray);    // A = 255, R = 170, G = 170, B = 170
        final int transparentColor = ContextCompat.getColor(getApplicationContext(), android.R.color.transparent);   // A = 0, R = 0, G = 0, B = 0

        mDarkerGrayHeartDrawable = ContextCompat
                .getDrawable(HousingDetailActivity.this, R.drawable.ic_heart_darker_gray_thicker_stroke);
        mWrappedDarkerGrayHeartMutableDrawable = DrawableCompat.wrap(mDarkerGrayHeartDrawable).mutate();

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
                if (mHeartButton.getDrawable().getConstantState() != ContextCompat
                        .getDrawable(HousingDetailActivity.this, R.drawable.ic_heart_solid_red).getConstantState()) {
                    DrawableCompat.setTint(mWrappedDarkerGrayHeartMutableDrawable, Color.argb(
                            Color.alpha(whiteColor),
                            newValue,
                            newValue,
                            newValue
                    ));
                }
                mEditPostButton.setColorFilter(Color.argb(
                        Color.alpha(whiteColor),
                        newValue,
                        newValue,
                        newValue
                ));
                mFindRoommateButton.setColorFilter(Color.argb(
                        Color.alpha(whiteColor),
                        newValue,
                        newValue,
                        newValue
                ));
            }
        });

        mAppBarLayout = (AppBarLayout) findViewById(R.id.activity_housing_detail_appbar_container);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset < mMaxNegativeAppBarLayoutVerticalOffset) {  // ScrollView is scrolling down AND Hand gesture is DRAGGING UP.
                    mMaxNegativeAppBarLayoutVerticalOffset = verticalOffset;
                    mIsDraggingDown = false;
                } else if (verticalOffset > mMaxNegativeAppBarLayoutVerticalOffset){    // ScrollView is scrolling up AND Hand gesture is DRAGGING DOWN.
                    mMaxNegativeAppBarLayoutVerticalOffset = verticalOffset;
                    mIsDraggingDown = true;
                }
                if (mIsDraggingDown) {
                    if (verticalOffset < -(mProfileImageHeight - mActionBarSize)) {
                        mToolbar.getNavigationIcon()
                                .setColorFilter(darkerGrayColor, PorterDuff.Mode.SRC_ATOP);
                        mToolbar.setBackgroundColor(whiteColor);
                        mIsToolbarTransparent = false;

                        if (mHeartButton.getDrawable().getConstantState() != ContextCompat
                                .getDrawable(HousingDetailActivity.this, R.drawable.ic_heart_solid_red).getConstantState()) {
                            mHeartButton.setImageDrawable(ContextCompat
                                    .getDrawable(HousingDetailActivity.this, R.drawable.ic_heart_darker_gray_thicker_stroke));
                        }
                        mIsWhiteHeart = false;

                        mEditPostButton.setColorFilter(darkerGrayColor);
                        mFindRoommateButton.setColorFilter(darkerGrayColor);
                    }
                    else if (!mIsToolbarTransparent && mScrollViewScrollY == 0) {
                        mAnimatorSet = new AnimatorSet();
                        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                if (mHeartButton.getDrawable().getConstantState() != ContextCompat
                                        .getDrawable(HousingDetailActivity.this, R.drawable.ic_heart_solid_red).getConstantState()) {
                                    mHeartButton.setImageDrawable(mWrappedDarkerGrayHeartMutableDrawable);
                                    DrawableCompat.setTint(mWrappedDarkerGrayHeartMutableDrawable, darkerGrayColor);
                                }
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if (mHeartButton.getDrawable().getConstantState() != ContextCompat
                                        .getDrawable(HousingDetailActivity.this, R.drawable.ic_heart_solid_red).getConstantState()) {
                                    mHeartButton.setImageDrawable(ContextCompat
                                            .getDrawable(HousingDetailActivity.this, R.drawable.ic_heart_white_thicker_stroke));
                                }
                                mIsWhiteHeart = true;
                            }
                        });

                        mAnimatorSet.play(mDrakerGrayToWhiteToolbarButtonValueAnimator)
                                .with(mWhiteToTransparentToolbarValueAnimator);
                        mAnimatorSet.start();

                        mIsToolbarTransparent = true;
                        mIsWhiteHeart = true;
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

        mHeartButton = (ImageView) findViewById(R.id.activity_housing_detail_heart_button);
        mHeartButton.setImageDrawable(ContextCompat
                .getDrawable(HousingDetailActivity.this, R.drawable.ic_heart_white_thicker_stroke));
        mHeartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorSet animatorSet = new AnimatorSet();

                ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(mHeartButton, View.SCALE_X, 0.2f, 1f);
                bounceAnimX.setDuration(300);
                bounceAnimX.setInterpolator(new OvershootInterpolator());

                ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(mHeartButton, View.SCALE_Y, 0.2f, 1f);
                bounceAnimY.setDuration(300);
                bounceAnimY.setInterpolator(new OvershootInterpolator());
                bounceAnimY.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (mHeartButton.getDrawable() != null) {
                            Drawable.ConstantState constantStateWhite, constantStateDarkerGray;

                            constantStateWhite = ContextCompat
                                    .getDrawable(HousingDetailActivity.this, R.drawable.ic_heart_white_thicker_stroke)
                                    .getConstantState();
                            constantStateDarkerGray = ContextCompat
                                    .getDrawable(HousingDetailActivity.this, R.drawable.ic_heart_darker_gray_thicker_stroke)
                                    .getConstantState();
                            if (mHeartButton.getDrawable().getConstantState() == constantStateWhite
                                    || mHeartButton.getDrawable().getConstantState() == constantStateDarkerGray) {
                                mHeartButton.setImageDrawable(ContextCompat
                                        .getDrawable(HousingDetailActivity.this, R.drawable.ic_heart_solid_red));
                            }
                            else {
                                if (mIsWhiteHeart) {
                                    mHeartButton.setImageDrawable(ContextCompat
                                            .getDrawable(HousingDetailActivity.this, R.drawable.ic_heart_white_thicker_stroke));
                                } else {
                                    mHeartButton.setImageDrawable(ContextCompat
                                            .getDrawable(HousingDetailActivity.this, R.drawable.ic_heart_darker_gray_thicker_stroke));
                                }
                            }
                        }
                    }
                });

                animatorSet.play(bounceAnimX).with(bounceAnimY);
                animatorSet.start();
            }
        });
        mHeartButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast toast = Toast.makeText(
                        HousingDetailActivity.this,
                        getString(R.string.activity_housing_detail_save_post_hint),
                        Toast.LENGTH_SHORT
                );
                toast.setGravity(
                        Gravity.TOP|Gravity.RIGHT,
                        mToolbar.getWidth() - (mToolbar.getContentInsetStartWithNavigation() + mHeartButton.getLeft()),
                        mHeartButton.getBottom()
                );
                toast.show();
                return true;
            }
        });

        mEditPostButton = (ImageView) findViewById(R.id.activity_housing_detail_edit_post_button);
        mEditPostButton.setColorFilter(whiteColor);
        mEditPostButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast toast = Toast.makeText(
                        HousingDetailActivity.this,
                        getString(R.string.activity_housing_detail_edit_post_hint),
                        Toast.LENGTH_SHORT
                );
                toast.setGravity(
                        Gravity.TOP|Gravity.RIGHT,
                        mToolbar.getWidth() - (mToolbar.getContentInsetStartWithNavigation() + mEditPostButton.getLeft()),
                        mEditPostButton.getBottom()
                );
                toast.show();
                return true;
            }
        });
        mFindRoommateButton = (ImageView) findViewById(R.id.activity_housing_detail_find_roommate_button);
        mFindRoommateButton.setColorFilter(whiteColor);
        mFindRoommateButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast toast = Toast.makeText(
                        HousingDetailActivity.this,
                        getString(R.string.activity_housing_detail_find_share_partner_hint),
                        Toast.LENGTH_SHORT
                );
                toast.setGravity(
                        Gravity.TOP|Gravity.RIGHT,
                        mToolbar.getWidth() - (mToolbar.getContentInsetStartWithNavigation() + mFindRoommateButton.getLeft()),
                        mFindRoommateButton.getBottom()
                );
                toast.show();
                return true;
            }
        });

        mNestedScrollView = (NestedScrollView) findViewById(R.id.activity_housing_detail_nested_scroll_view);
        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                mScrollViewScrollY = scrollY;
            }
        });

        String houseTitle = getIntent().getStringExtra(Constants.ACTIVITY_HOUSING_DETAIL_HOUSE_TITLE_EXTRA);
        String price = getIntent().getStringExtra(Constants.ACTIVITY_HOUSING_DETAIL_PRICE_EXTRA);
        String details = getIntent().getStringExtra(Constants.ACTIVITY_HOUSING_DETAIL_DETAILS_EXTRA);
        String houseNumber = getIntent().getStringExtra(Constants.ACTIVITY_HOUSING_DETAIL_HOUSE_NUMBER_EXTRA);
        String street = getIntent().getStringExtra(Constants.ACTIVITY_HOUSING_DETAIL_STREET_EXTRA);
        String ward = getIntent().getStringExtra(Constants.ACTIVITY_HOUSING_DETAIL_WARD_EXTRA);
        String district = getIntent().getStringExtra(Constants.ACTIVITY_HOUSING_DETAIL_DISTRICT_EXTRA);
        String city = getIntent().getStringExtra(Constants.ACTIVITY_HOUSING_DETAIL_CITY_EXTRA);
        String houseType = getIntent().getStringExtra(Constants.ACTIVITY_HOUSING_DETAIL_HOUSE_TYPE_EXTRA);
        String ownerName = getIntent().getStringExtra(Constants.ACTIVITY_HOUSING_DETAIL_OWNER_NAME_EXTRA);

        mHouseProfileImage = (ImageView) findViewById(R.id.activity_housing_detail_house_profile_image);
        mHouseHostProfileImage = (ImageView) findViewById(R.id.activity_housing_detail_house_host_profile_image);

        mHouseTitle = (TextView) findViewById(R.id.activity_housing_detail_house_title);
        mPrice = (TextView) findViewById(R.id.activity_housing_detail_price);
        mDetails = (TextView) findViewById(R.id.activity_housing_detail_about_house);
        mHouseNumber = (TextView) findViewById(R.id.activity_housing_detail_address_house_number_text);
        mStreet = (TextView) findViewById(R.id.activity_housing_detail_address_street_text);
        mWard = (TextView) findViewById(R.id.activity_housing_detail_address_ward_text);
        mDistrict = (TextView) findViewById(R.id.activity_housing_detail_district_text);
        mCity = (TextView) findViewById(R.id.activity_housing_detail_city_text);
        mHouseType = (TextView) findViewById(R.id.activity_housing_detail_house_type);
        mOwnerName = (TextView) findViewById(R.id.activity_housing_detail_house_host);

        if (houseTitle != null && !houseTitle.isEmpty()) {
            mHouseTitle.setText(houseTitle);
        }
        if (price != null && !price.isEmpty()) {
            mPrice.setText(price);
        }
        if (details != null && !details.isEmpty()) {
            mDetails.setText(details);
        }
        if (houseNumber != null && !houseNumber.isEmpty()) {
            mHouseNumber.setText(houseNumber);
        }
        if (street != null && !street.isEmpty()) {
            mStreet.setText(street);
        }
        if (ward != null && !ward.isEmpty()) {
            mWard.setText(ward);
        }
        if (district != null && !district.isEmpty()) {
            mDistrict.setText(district);
        }
        if (city != null && !city.isEmpty()) {
            mCity.setText(city);
        }

        if (houseType != null && !houseType.isEmpty()) {
            mHouseType.setText(houseType);
        }
        if (ownerName != null && !ownerName.isEmpty()) {
            mOwnerName.setText(getString(R.string.activity_housing_detail_hosted_by) + ownerName);
        }

        Glide.with(this)
                .load(getIntent().getStringExtra(Constants.ACTIVITY_HOUSING_DETAIL_PROFILE_IMAGE_URL_EXTRA))
                .placeholder(R.drawable.ic_home)
                .crossFade()
                .into(mHouseProfileImage);

        Glide.with(this)
                .load(R.drawable.profile_image_2)
                .placeholder(R.drawable.ic_profile_picture)
                .crossFade()
                .transform(new GlideCircleTransform(this))
                .into(mHouseHostProfileImage);
    }
}
