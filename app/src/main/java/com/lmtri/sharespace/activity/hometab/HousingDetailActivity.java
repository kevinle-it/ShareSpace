package com.lmtri.sharespace.activity.hometab;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lmtri.sharespace.R;
import com.lmtri.sharespace.ShareSpaceApplication;
import com.lmtri.sharespace.activity.ImageViewerActivity;
import com.lmtri.sharespace.activity.LoginActivity;
import com.lmtri.sharespace.activity.posthouse.PostHouseActivity;
import com.lmtri.sharespace.activity.postsharehouse.PostShareHouseActivity;
import com.lmtri.sharespace.adapter.ImageViewerAdapter;
import com.lmtri.sharespace.api.model.HistoryHousingNote;
import com.lmtri.sharespace.api.model.HistoryHousingPhoto;
import com.lmtri.sharespace.api.model.Housing;
import com.lmtri.sharespace.api.model.HousingAppointment;
import com.lmtri.sharespace.api.model.Note;
import com.lmtri.sharespace.api.model.SavedHousing;
import com.lmtri.sharespace.api.service.RetrofitClient;
import com.lmtri.sharespace.api.service.housing.HousingClient;
import com.lmtri.sharespace.api.service.housing.note.INoteDeletingCallback;
import com.lmtri.sharespace.api.service.housing.note.INoteGettingCallback;
import com.lmtri.sharespace.api.service.housing.note.INotePostingCallback;
import com.lmtri.sharespace.api.service.housing.note.INoteUpdatingCallback;
import com.lmtri.sharespace.api.service.housing.photo.IPhotoDeletingCallback;
import com.lmtri.sharespace.api.service.housing.photo.IPhotoGettingCallback;
import com.lmtri.sharespace.api.service.housing.report.IReportHousingCallback;
import com.lmtri.sharespace.api.service.sharehousing.ShareHousingClient;
import com.lmtri.sharespace.api.service.sharehousing.get.ICheckIfExistSharePostsOfCurrentHousingCallback;
import com.lmtri.sharespace.api.service.user.UserClient;
import com.lmtri.sharespace.api.service.user.appointment.housing.IHousingAppointmentDeletingCallback;
import com.lmtri.sharespace.api.service.user.appointment.housing.IHousingAppointmentGettingCallback;
import com.lmtri.sharespace.api.service.user.appointment.housing.IHousingAppointmentPostingCallback;
import com.lmtri.sharespace.api.service.user.appointment.housing.IHousingAppointmentUpdatingCallback;
import com.lmtri.sharespace.api.service.user.post.housing.availability.IGetHidingStateOfCurrentHousingCallback;
import com.lmtri.sharespace.api.service.user.post.housing.availability.IHideHousingCallback;
import com.lmtri.sharespace.api.service.user.post.housing.availability.IUnhideHousingCallback;
import com.lmtri.sharespace.api.service.user.save.housing.IGetSavingStateOfCurrentHousingCallback;
import com.lmtri.sharespace.api.service.user.save.housing.ISaveHousingCallback;
import com.lmtri.sharespace.api.service.user.save.housing.IUnsaveHousingCallback;
import com.lmtri.sharespace.customview.CustomEditText;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.GlideCircleTransform;
import com.lmtri.sharespace.helper.HousePriceHelper;
import com.lmtri.sharespace.helper.PermissionHelper;
import com.lmtri.sharespace.helper.ToastHelper;
import com.lmtri.sharespace.helper.busevent.appointment.housing.DeleteHousingAppointmentEvent;
import com.lmtri.sharespace.helper.busevent.appointment.housing.SetNewHousingAppointmentEvent;
import com.lmtri.sharespace.helper.busevent.appointment.housing.UpdateHousingAppointmentEvent;
import com.lmtri.sharespace.helper.busevent.housing.post.DeleteHousingEvent;
import com.lmtri.sharespace.helper.busevent.housing.note.DeleteHousingNoteEvent;
import com.lmtri.sharespace.helper.busevent.housing.photo.DeleteHousingPhotoEvent;
import com.lmtri.sharespace.helper.busevent.housing.availability.HideHousingEvent;
import com.lmtri.sharespace.helper.busevent.housing.report.ReportHousingEvent;
import com.lmtri.sharespace.helper.busevent.housing.save.SaveHousingEvent;
import com.lmtri.sharespace.helper.busevent.housing.note.TakeHousingNoteEvent;
import com.lmtri.sharespace.helper.busevent.housing.photo.TakeHousingPhotoEvent;
import com.lmtri.sharespace.helper.busevent.housing.availability.UnhideHousingEvent;
import com.lmtri.sharespace.helper.busevent.housing.save.UnsaveHousingEvent;
import com.lmtri.sharespace.helper.busevent.housing.note.UpdateHousingNoteEvent;
import com.lmtri.sharespace.helper.firebasestorage.FirebaseStorageHelper;
import com.lmtri.sharespace.helper.firebasestorage.OnPhotoPostingListener;
import com.lmtri.sharespace.helper.firebasestorage.housing.OnHousingDeletingListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import me.relex.circleindicator.CircleIndicator;

public class HousingDetailActivity extends AppCompatActivity {

    public static final String TAG = HousingDetailActivity.class.getSimpleName();

    private AppBarLayout mAppBarLayout;
    private ViewPager mImageViewer;
    private CircleIndicator mImageViewerIndicator;

    private Toolbar mToolbar;

    private ImageView mHeartButton;
    private boolean mIsWhiteHeart = true;
    private Drawable mDarkerGrayHeartDrawable;
    private Drawable mWrappedDarkerGrayHeartMutableDrawable;

    private ImageView mFindRoommateButton;
    private ImageView mReportButton;

    private ImageView mShowHideHousingButton;
    private boolean mIsShowButton = false;
    private Drawable mDarkerGrayShowHousingDrawable;
    private Drawable mDarkerGrayHideHousingDrawable;
    private Drawable mWrappedDarkerGrayShowHousingMutableDrawable;
    private Drawable mWrappedDarkerGrayHideHousingMutableDrawable;

    private NestedScrollView mNestedScrollView;

    private int mProfileImageHeight;
    private int mActionBarSize;

    private AnimatorSet mAnimatorSet;
    private ValueAnimator mWhiteToTransparentToolbarValueAnimator;
    private ValueAnimator mDrakerGrayToWhiteToolbarButtonValueAnimator;
    private boolean mIsToolbarTransparent = true;

    private boolean mIsDraggingDown = false;
    private int mMaxNegativeAppBarLayoutVerticalOffset = 0;
    private int mScrollViewScrollY = 0;

    private Housing mHousing;
    private TextView mHouseTitle;
    private TextView mPrice;
    private TextView mArea;
    private TextView mNumSaved;
    private TextView mNumPeople;
    private TextView mNumRoom;
    private TextView mNumBed;
    private TextView mNumBath;
    private TextView mDescription;
    private TextView mHouseNumber;
    private TextView mStreet;
    private TextView mWard;
    private TextView mDistrict;
    private TextView mCity;
    private TextView mTimeRestriction;
    private ImageView mPetAllow;
    private ImageView mWifiAvailable;
    private ImageView mACAvailable;
    private ImageView mParkingAvailable;
    private TextView mHouseType;
    private TextView mOwnerName;
    private TextView mOwnerEmail;
    private ImageView mOwnerProfileImagePlaceholder;
    private ImageView mOwnerProfileImage;

    private View mBookingDivider;
    private ViewStub mBookingLayoutViewStub;

    private RelativeLayout mCallButton;

    private RelativeLayout mScheduleButton;
    private HousingAppointment mHousingAppointment;
    private AlertDialog.Builder mScheduleDialogOptionsEditDeleteAppointment;
    private AlertDialog mScheduleDatePickerDialogJellyBean;
    private AlertDialog mScheduleTimePickerDialogJellyBean;
    private DatePickerDialog mScheduleDatePickerDialog;
    private TimePickerDialog mScheduleTimePickerDialog;
    private AlertDialog mScheduleNotesDialog;
    private CustomEditText mInputScheduleNotes;

    private LinearLayout mUserNoteLayout;
    private TextView mUserNoteTextView;
    private RelativeLayout mTakeNoteButton;
    private CustomEditText mInputNotes;
    private Note mUserNote;

    private PermissionHelper mPermissionHelper;
    private RelativeLayout mTakePhotoButton;
    private LinearLayout mUserPhotoLayout;
    private GridLayout mPhotoGridContainer;
    private static int mLoadPhotoIndex;
    private ArrayList<String> mUserPhotoURLs = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private int mCurrentSelectedGridItemIndex;
    private int mGridContainerWidthPixels;
    private int mGridItemMarginPixels;
    private int mGridItemWidthPixels;

    private RelativeLayout mEditPostButton;
    private RelativeLayout mDeleteHousingButton;
    
    private boolean mIsUserLayoutInflated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_housing_detail);

        mUserNoteLayout = (LinearLayout) findViewById(R.id.activity_housing_detail_user_note_layout);

        mUserPhotoLayout = (LinearLayout) findViewById(R.id.activity_housing_detail_user_photo_layout);
        mPermissionHelper = new PermissionHelper(HousingDetailActivity.this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mGridContainerWidthPixels = displayMetrics.widthPixels - 2 * getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_content_left_right_margin);
        mGridItemMarginPixels = getResources().getDimensionPixelSize(R.dimen.activity_post_house_add_photo_box_margin);
        mGridItemWidthPixels = (mGridContainerWidthPixels - mGridItemMarginPixels * 8) / 4;

        mNestedScrollView = (NestedScrollView) findViewById(R.id.activity_share_housing_detail_nested_scroll_view);
        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                mScrollViewScrollY = scrollY;
            }
        });

        Gson gson = new Gson();

        try {
            mHousing = gson.fromJson(
                    getIntent().getStringExtra(Constants.ACTIVITY_HOUSING_DETAIL_HOUSING_EXTRA),
                    Housing.class
            );
            if (mHousing != null) {
                initAppBarLayout();
                if (Constants.CURRENT_USER != null) {
                    // Current User is the Owner of this House.
                    if (Constants.CURRENT_USER.getUserID() == mHousing.getOwner().getUserID()) {
                        initOwnerAppBarAndBookingLayout();
                    } else {
                        initUserAppBarAndPhotosAndBookingLayout();
                    }
                } else {
                    mIsUserLayoutInflated = true;
                    initUserAppBarAndPhotosAndBookingLayout();
                }

                mImageViewer = (ViewPager) findViewById(R.id.activity_housing_detail_house_profile_image_viewer);
                mImageViewerIndicator = (CircleIndicator) findViewById(R.id.activity_housing_detail_house_profile_image_indicator);

                if (mHousing.getPhotoURLs().size() > 0) {
                    mImageViewer.setAdapter(new ImageViewerAdapter(mHousing.getPhotoURLs()));
                    mImageViewerIndicator.setViewPager(mImageViewer);
                    mImageViewer.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        }

                        @Override
                        public void onPageSelected(int position) {
                            ((ImageViewerAdapter) mImageViewer.getAdapter()).setCurrentPhotoIndex(position);
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });
                }

                mOwnerProfileImagePlaceholder = (ImageView) findViewById(R.id.activity_housing_detail_owner_profile_image_placeholder);
                mOwnerProfileImage = (ImageView) findViewById(R.id.activity_housing_detail_owner_profile_image);

                mHouseTitle = (TextView) findViewById(R.id.activity_housing_detail_house_title);
                mPrice = (TextView) findViewById(R.id.activity_housing_detail_price);
                mArea = (TextView) findViewById(R.id.activity_housing_detail_area);
                mNumSaved = (TextView) findViewById(R.id.activity_housing_detail_num_saved);
                mNumPeople = (TextView) findViewById(R.id.activity_housing_detail_num_people_text);
                mNumRoom = (TextView) findViewById(R.id.activity_housing_detail_num_room_text);
                mNumBed = (TextView) findViewById(R.id.activity_housing_detail_num_bed_text);
                mNumBath = (TextView) findViewById(R.id.activity_housing_detail_num_bath_text);
                mDescription = (TextView) findViewById(R.id.activity_housing_detail_about_house);
                mHouseNumber = (TextView) findViewById(R.id.activity_housing_detail_address_house_number_text);
                mStreet = (TextView) findViewById(R.id.activity_housing_detail_address_street_text);
                mWard = (TextView) findViewById(R.id.activity_housing_detail_address_ward_text);
                mDistrict = (TextView) findViewById(R.id.activity_housing_detail_district_text);
                mCity = (TextView) findViewById(R.id.activity_housing_detail_city_text);
                mTimeRestriction = (TextView) findViewById(R.id.activity_housing_detail_time_restriction_text);
                mPetAllow = (ImageView) findViewById(R.id.activity_housing_detail_allow_pet);
                mWifiAvailable = (ImageView) findViewById(R.id.activity_housing_detail_wifi_available);
                mACAvailable = (ImageView) findViewById(R.id.activity_housing_detail_AC_available);
                mParkingAvailable = (ImageView) findViewById(R.id.activity_housing_detail_parking_available);
                mHouseType = (TextView) findViewById(R.id.activity_housing_detail_house_type);
                mOwnerName = (TextView) findViewById(R.id.activity_housing_detail_house_owner);
                mOwnerEmail = (TextView) findViewById(R.id.activity_housing_detail_house_owner_email);

                if (!TextUtils.isEmpty(mHousing.getTitle())) {
                    mHouseTitle.setText(mHousing.getTitle());
                } else {
                    mHouseTitle.setText(getString(R.string.activity_housing_detail_house_title_empty));
                }
                Pair<String, String> pair = HousePriceHelper.parseForHousing(mHousing.getPrice(), this);
                if (pair.first == null) {
                    mPrice.setText(pair.second);
                } else {
                    mPrice.setText(pair.first + " " + pair.second);
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    mArea.setText(Html.fromHtml(String.format(Locale.US, "%.2f", mHousing.getArea()) + getString(R.string.activity_housing_detail_area_square_meter)));
                } else {
                    mArea.setText(Html.fromHtml(String.format(Locale.US, "%.2f", mHousing.getArea()) + getString(R.string.activity_housing_detail_area_square_meter), Html.FROM_HTML_MODE_LEGACY));
                }
                if (mHousing.getNumOfSaved() >= 0) {
                    mNumSaved.setText(String.valueOf(mHousing.getNumOfSaved()) + getString(R.string.activity_housing_detail_saved));
                } else {
                    mNumSaved.setText(0 + getString(R.string.activity_housing_detail_saved));
                }

                if (mHousing.getNumOfPeople() != -1) {
                    mNumPeople.setText(String.valueOf(mHousing.getNumOfPeople()) + getString(R.string.activity_housing_detail_people));
                } else {    // NumOfPeople == -1
                    mNumPeople.setText(getString(R.string.activity_housing_detail_not_available_field));
                }
                if (mHousing.getNumOfRoom() != -1) {
                    mNumRoom.setText(String.valueOf(mHousing.getNumOfRoom()) + getString(R.string.activity_housing_detail_room));
                } else {    // NumOfRoom == -1
                    mNumRoom.setText(getString(R.string.activity_housing_detail_not_available_field));
                }
                if (mHousing.getNumOfBed() != -1) {
                    mNumBed.setText(String.valueOf(mHousing.getNumOfBed()) + getString(R.string.activity_housing_detail_bed));
                } else {    // NumOfBed == -1
                    mNumBed.setText(getString(R.string.activity_housing_detail_not_available_field));
                }
                if (mHousing.getNumOfBath() != -1) {
                    mNumBath.setText(String.valueOf(mHousing.getNumOfBath()) + getString(R.string.activity_housing_detail_bath));
                } else {    // NumOfBath == -1
                    mNumBath.setText(getString(R.string.activity_housing_detail_not_available_field));
                }

                if (!TextUtils.isEmpty(mHousing.getDescription())) {
                    mDescription.setText(mHousing.getDescription());
                }

                if (!TextUtils.isEmpty(mHousing.getAddressHouseNumber())) {
                    mHouseNumber.setText(mHousing.getAddressHouseNumber());
                } else {
                    mHouseNumber.setText(getString(R.string.activity_housing_detail_address_empty));
                }
                if (!TextUtils.isEmpty(mHousing.getAddressStreet())) {
                    mStreet.setText(mHousing.getAddressStreet());
                } else {
                    mStreet.setText(getString(R.string.activity_housing_detail_address_empty));
                }
                if (!TextUtils.isEmpty(mHousing.getAddressWard())) {
                    mWard.setText(mHousing.getAddressWard());
                } else {
                    mWard.setText(getString(R.string.activity_housing_detail_address_empty));
                }
                if (!TextUtils.isEmpty(mHousing.getAddressDistrict())) {
                    mDistrict.setText(mHousing.getAddressDistrict());
                } else {
                    mDistrict.setText(getString(R.string.activity_housing_detail_address_empty));
                }
                if (!TextUtils.isEmpty(mHousing.getAddressCity())) {
                    mCity.setText(mHousing.getAddressCity());
                } else {
                    mCity.setText(getString(R.string.activity_housing_detail_address_empty));
                }

                if (!TextUtils.isEmpty(mHousing.getTimeRestriction())) {
                    mTimeRestriction.setText(mHousing.getTimeRestriction());
                } else {
                    mTimeRestriction.setText(getString(R.string.activity_housing_detail_not_available_field));
                }
                if (mHousing.isAllowPet()) {
                    mPetAllow.setImageResource(R.drawable.ic_check_mark_thicker);
                } else {
                    mPetAllow.setImageResource(R.drawable.ic_cross);
                }
                if (mHousing.hasWifi()) {
                    mWifiAvailable.setImageResource(R.drawable.ic_check_mark_thicker);
                } else {
                    mWifiAvailable.setImageResource(R.drawable.ic_cross);
                }
                if (mHousing.hasAC()) {
                    mACAvailable.setImageResource(R.drawable.ic_check_mark_thicker);
                } else {
                    mACAvailable.setImageResource(R.drawable.ic_cross);
                }
                if (mHousing.hasParking()) {
                    mParkingAvailable.setImageResource(R.drawable.ic_check_mark_thicker);
                } else {
                    mParkingAvailable.setImageResource(R.drawable.ic_cross);
                }

                if (!TextUtils.isEmpty(mHousing.getHouseType())) {
                    mHouseType.setText(mHousing.getHouseType());
                } else {
                    mHouseType.setText(getString(R.string.activity_housing_detail_house_type_empty));
                }
                if (mHousing.getOwner() != null) {
                    if (!TextUtils.isEmpty(mHousing.getOwner().getLastName())) {
                        mOwnerName.setText(
                                getString(R.string.activity_housing_detail_house_owner)
                                        + mHousing.getOwner().getLastName() + " "
                                        + mHousing.getOwner().getFirstName()
                        );
                    } else {
                        mOwnerName.setText(
                                getString(R.string.activity_housing_detail_house_owner)
                                        + mHousing.getOwner().getFirstName()
                        );
                    }
                    if (!TextUtils.isEmpty(mHousing.getOwner().getEmail())) {
                        mOwnerEmail.setText(mHousing.getOwner().getEmail());
                    }
                }

                Glide.with(this)
                        .load(R.drawable.ic_profile_picture)
                        .crossFade()
                        .transform(new GlideCircleTransform(this))
                        .into(mOwnerProfileImagePlaceholder);

//                Glide.with(this)
//                        .load(R.drawable.profile_image_2)
//                        .crossFade()
//                        .transform(new GlideCircleTransform(this))
//                        .into(mOwnerProfileImage);
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    private void initAppBarLayout() {
        final int whiteColor = ContextCompat.getColor(getApplicationContext(), android.R.color.white);   // A = 255, R = 255, G = 255, B = 255
        final int darkerGrayColor = ContextCompat.getColor(getApplicationContext(), android.R.color.darker_gray);    // A = 255, R = 170, G = 170, B = 170
        final int transparentColor = ContextCompat.getColor(getApplicationContext(), android.R.color.transparent);   // A = 0, R = 0, G = 0, B = 0

        mDarkerGrayHeartDrawable = ContextCompat
                .getDrawable(HousingDetailActivity.this, R.drawable.ic_heart_darker_gray_thicker_stroke);
        mWrappedDarkerGrayHeartMutableDrawable = DrawableCompat.wrap(mDarkerGrayHeartDrawable).mutate();

        final Drawable.ConstantState showHousingConstantStateWhite,
                hideHousingConstantStateWhite,
                showHousingConstantStateDarkerGray,
                hideHousingConstantStateDarkerGray;
        showHousingConstantStateWhite = ContextCompat
                .getDrawable(HousingDetailActivity.this, R.drawable.ic_show_white)
                .getConstantState();
        hideHousingConstantStateWhite = ContextCompat
                .getDrawable(HousingDetailActivity.this, R.drawable.ic_hide_white)
                .getConstantState();
        showHousingConstantStateDarkerGray = ContextCompat
                .getDrawable(HousingDetailActivity.this, R.drawable.ic_show_darker_gray)
                .getConstantState();
        hideHousingConstantStateDarkerGray = ContextCompat
                .getDrawable(HousingDetailActivity.this, R.drawable.ic_hide_darker_gray)
                .getConstantState();
        mDarkerGrayShowHousingDrawable = ContextCompat
                .getDrawable(HousingDetailActivity.this, R.drawable.ic_show_darker_gray);
        mDarkerGrayHideHousingDrawable = ContextCompat
                .getDrawable(HousingDetailActivity.this, R.drawable.ic_hide_darker_gray);
        mWrappedDarkerGrayShowHousingMutableDrawable = DrawableCompat.wrap(mDarkerGrayShowHousingDrawable).mutate();
        mWrappedDarkerGrayHideHousingMutableDrawable = DrawableCompat.wrap(mDarkerGrayHideHousingDrawable).mutate();

        TypedArray styledAttributes = this.getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize });
        mActionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        mProfileImageHeight = getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_house_profile_image_height);

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
                mFindRoommateButton.setColorFilter(Color.argb(
                        Color.alpha(whiteColor),
                        newValue,
                        newValue,
                        newValue
                ));
                mReportButton.setColorFilter(Color.argb(
                        Color.alpha(whiteColor),
                        newValue,
                        newValue,
                        newValue
                ));
                if (mIsShowButton) {
                    DrawableCompat.setTint(mWrappedDarkerGrayShowHousingMutableDrawable, Color.argb(
                            Color.alpha(whiteColor),
                            newValue,
                            newValue,
                            newValue
                    ));
                } else {
                    DrawableCompat.setTint(mWrappedDarkerGrayHideHousingMutableDrawable, Color.argb(
                            Color.alpha(whiteColor),
                            newValue,
                            newValue,
                            newValue
                    ));
                }
            }
        });

        mAppBarLayout = (AppBarLayout) findViewById(R.id.activity_share_housing_detail_appbar_container);
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

                        mFindRoommateButton.setColorFilter(darkerGrayColor);
                        mReportButton.setColorFilter(darkerGrayColor);
                        if (mIsShowButton) {
                            mShowHideHousingButton.setImageDrawable(ContextCompat
                                    .getDrawable(HousingDetailActivity.this, R.drawable.ic_show_darker_gray));
                        } else {
                            mShowHideHousingButton.setImageDrawable(ContextCompat
                                    .getDrawable(HousingDetailActivity.this, R.drawable.ic_hide_darker_gray));
                        }
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
                                if (mIsShowButton) {
                                    mShowHideHousingButton.setImageDrawable(mWrappedDarkerGrayShowHousingMutableDrawable);
                                    DrawableCompat.setTint(mWrappedDarkerGrayShowHousingMutableDrawable, darkerGrayColor);
                                } else {
                                    mShowHideHousingButton.setImageDrawable(mWrappedDarkerGrayHideHousingMutableDrawable);
                                    DrawableCompat.setTint(mWrappedDarkerGrayHideHousingMutableDrawable, darkerGrayColor);
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

                                if (mIsShowButton) {
                                    mShowHideHousingButton.setImageDrawable(ContextCompat
                                            .getDrawable(HousingDetailActivity.this, R.drawable.ic_show_white));
                                } else {
                                    mShowHideHousingButton.setImageDrawable(ContextCompat
                                            .getDrawable(HousingDetailActivity.this, R.drawable.ic_hide_white));
                                }
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

        mToolbar = (Toolbar) findViewById(R.id.activity_share_housing_detail_toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        if (mToolbar.getNavigationIcon() != null) {
            mToolbar.getNavigationIcon().setColorFilter(whiteColor, PorterDuff.Mode.SRC_ATOP);
        }
        mToolbar.setBackgroundColor(transparentColor);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.stay_still, R.anim.slide_down_out);
            }
        });

        mHeartButton = (ImageView) findViewById(R.id.activity_share_housing_detail_heart_button);
        if (Constants.CURRENT_USER != null) {
            mHeartButton.setClickable(false);   // Still respond if set at runtime.
            mHeartButton.setEnabled(false);     // Does NOT respond to any response.
            UserClient.getSavingStateOfCurrentHousing(
                    mHousing.getID(),
                    new IGetSavingStateOfCurrentHousingCallback() {
                        @Override
                        public void onGetComplete(Boolean isSaved) {
                            if (isSaved) {
                                mHeartButton.setImageDrawable(ContextCompat
                                        .getDrawable(HousingDetailActivity.this, R.drawable.ic_heart_solid_red));
                            } else {
                                mHeartButton.setImageDrawable(ContextCompat
                                        .getDrawable(HousingDetailActivity.this, R.drawable.ic_heart_white_thicker_stroke));
                            }
                            mHeartButton.setClickable(true);
                            mHeartButton.setEnabled(true);
                        }

                        @Override
                        public void onGetFailure(Throwable t) {

                        }
                    }
            );
        }
        mHeartButton.setImageDrawable(ContextCompat
                .getDrawable(HousingDetailActivity.this, R.drawable.ic_heart_white_thicker_stroke));
        mHeartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.CURRENT_USER != null) {
                    final AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (mHeartButton.getDrawable().getConstantState()
                                    == ContextCompat
                                    .getDrawable(HousingDetailActivity.this, R.drawable.ic_heart_solid_red)
                                    .getConstantState()) {
                                UserClient.saveHousing(
                                        mHousing.getID(),
                                        new ISaveHousingCallback() {
                                            @Override
                                            public void onSaveComplete(SavedHousing savedHousing) {
                                                if (savedHousing != null) {
                                                    ToastHelper.showCenterToast(
                                                            getApplicationContext(),
                                                            R.string.activity_housing_detail_save_housing_successfully_message,
                                                            Toast.LENGTH_SHORT
                                                    );
                                                    ShareSpaceApplication.BUS.post(new SaveHousingEvent(savedHousing));
                                                } else {
                                                    ToastHelper.showCenterToast(
                                                            getApplicationContext(),
                                                            R.string.activity_housing_detail_save_housing_unsuccessfully_message,
                                                            Toast.LENGTH_LONG
                                                    );
                                                    animatorSet.start();
                                                }
                                            }

                                            @Override
                                            public void onSaveFailure(Throwable t) {
                                                ToastHelper.showCenterToast(
                                                        getApplicationContext(),
                                                        R.string.activity_housing_detail_save_housing_unsuccessfully_message,
                                                        Toast.LENGTH_LONG
                                                );
                                                animatorSet.start();
                                                RetrofitClient.showShareSpaceServerConnectionErrorDialog(HousingDetailActivity.this, t);
                                            }
                                        }
                                );
                            } else {
                                UserClient.unsaveHousing(
                                        mHousing.getID(),
                                        new IUnsaveHousingCallback() {
                                            @Override
                                            public void onUnsaveComplete(SavedHousing savedHousing) {
                                                if (savedHousing != null) {
                                                    ToastHelper.showCenterToast(
                                                            getApplicationContext(),
                                                            R.string.activity_housing_detail_unsave_housing_successfully_message,
                                                            Toast.LENGTH_SHORT
                                                    );
                                                    ShareSpaceApplication.BUS.post(new UnsaveHousingEvent(savedHousing));
                                                } else {
                                                    ToastHelper.showCenterToast(
                                                            getApplicationContext(),
                                                            R.string.activity_housing_detail_unsave_housing_unsuccessfully_message,
                                                            Toast.LENGTH_LONG
                                                    );
                                                    animatorSet.start();
                                                }
                                            }

                                            @Override
                                            public void onUnsaveFailure(Throwable t) {
                                                ToastHelper.showCenterToast(
                                                        getApplicationContext(),
                                                        R.string.activity_housing_detail_unsave_housing_unsuccessfully_message,
                                                        Toast.LENGTH_LONG
                                                );
                                                animatorSet.start();
                                                RetrofitClient.showShareSpaceServerConnectionErrorDialog(HousingDetailActivity.this, t);
                                            }
                                        }
                                );
                            }
                        }
                    });

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
                                } else {
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
                } else {
                    new AlertDialog.Builder(HousingDetailActivity.this)
                            .setTitle(R.string.activity_housing_detail_login_required_feature_dialog_title)
                            .setMessage(R.string.activity_housing_detail_login_required_feature_dialog_message)
                            .setPositiveButton(R.string.activity_housing_detail_login_required_feature_dialog_positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(HousingDetailActivity.this, LoginActivity.class);
                                    startActivityForResult(intent, Constants.START_ACTIVITY_LOGIN_REQUEST);
                                }
                            })
                            .setNegativeButton(R.string.activity_housing_detail_login_required_feature_dialog_negative, null)
                            .show();
                }
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
                        Gravity.TOP|Gravity.END,
                        mToolbar.getWidth() - (mToolbar.getContentInsetStartWithNavigation() + mHeartButton.getLeft()),
                        mHeartButton.getBottom()
                );
                toast.show();
                return true;
            }
        });

        mFindRoommateButton = (ImageView) findViewById(R.id.activity_housing_detail_find_roommate_button);
        mFindRoommateButton.setColorFilter(whiteColor);
        mFindRoommateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.CURRENT_USER != null) {
                    final ProgressDialog progressDialog = new ProgressDialog(
                            HousingDetailActivity.this, R.style.LoginProgressDialog
                    );
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage(getString(R.string.activity_housing_detail_find_share_partner_progress_dialog_message));
                    progressDialog.setCanceledOnTouchOutside(false);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    }
                    progressDialog.show();
                    ShareHousingClient.checkIfExistSharePostsOfCurrentHousing(
                            mHousing.getID(),
                            new ICheckIfExistSharePostsOfCurrentHousingCallback() {
                                @Override
                                public void onCheckComplete(final Boolean isExist) {
                                    progressDialog.dismiss();
                                    if (isExist) {
                                        new AlertDialog.Builder(HousingDetailActivity.this)
                                                .setTitle(getString(R.string.activity_housing_detail_share_post_history_dialog_title))
                                                .setMessage(getString(R.string.activity_housing_detail_share_post_history_dialog_message))
                                                .setPositiveButton(getString(R.string.activity_housing_detail_share_post_history_dialog_positive), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent(
                                                                HousingDetailActivity.this,
                                                                ShowExistSharePostsOfCurrentHousingActivity.class
                                                        );
                                                        Gson gson = new Gson();
                                                        intent.putExtra(
                                                                Constants.ACTIVITY_HOUSING_DETAIL_FIND_ROOMMATE_CURRENT_HOUSING_INFO_EXTRA,
                                                                gson.toJson(mHousing)
                                                        );
                                                        startActivityForResult(intent, Constants.START_ACTIVITY_SHOW_EXIST_SHARE_POSTS_OF_CURRENT_HOUSING_REQUEST);
                                                    }
                                                })
                                                .setNegativeButton(getString(R.string.activity_housing_detail_share_post_history_dialog_negative), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent(
                                                                HousingDetailActivity.this,
                                                                PostShareHouseActivity.class
                                                        );
                                                        intent.putExtra(Constants.IS_FIND_ROOMMATE_EXTRA, true);
                                                        Gson gson = new Gson();
                                                        intent.putExtra(
                                                                Constants.HOUSING_INFO_FOR_FINDING_ROOMMATE_EXTRA,
                                                                gson.toJson(mHousing)
                                                        );
                                                        startActivityForResult(intent, Constants.START_ACTIVITY_POST_SHARE_HOUSE_REQUEST);
                                                    }
                                                })
                                                .setNeutralButton(getString(R.string.activity_housing_detail_share_post_history_dialog_neutral), null)
                                                .show();
                                    } else {
                                        Intent intent = new Intent(
                                                HousingDetailActivity.this,
                                                PostShareHouseActivity.class
                                        );
                                        intent.putExtra(Constants.IS_FIND_ROOMMATE_EXTRA, true);
                                        Gson gson = new Gson();
                                        intent.putExtra(
                                                Constants.HOUSING_INFO_FOR_FINDING_ROOMMATE_EXTRA,
                                                gson.toJson(mHousing)
                                        );
                                        startActivityForResult(intent, Constants.START_ACTIVITY_POST_SHARE_HOUSE_REQUEST);
                                    }
                                }

                                @Override
                                public void onCheckFailure(Throwable t) {
                                    progressDialog.dismiss();
                                    RetrofitClient.showShareSpaceServerConnectionErrorDialog(HousingDetailActivity.this, t);
                                }
                            }
                    );
                } else {
                    new AlertDialog.Builder(HousingDetailActivity.this)
                            .setTitle(R.string.activity_housing_detail_login_required_feature_dialog_title)
                            .setMessage(R.string.activity_housing_detail_login_required_feature_dialog_message)
                            .setPositiveButton(R.string.activity_housing_detail_login_required_feature_dialog_positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(HousingDetailActivity.this, LoginActivity.class);
                                    startActivityForResult(intent, Constants.START_ACTIVITY_LOGIN_REQUEST);
                                }
                            })
                            .setNegativeButton(R.string.activity_housing_detail_login_required_feature_dialog_negative, null)
                            .show();
                }
            }
        });
        mFindRoommateButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast toast = Toast.makeText(
                        HousingDetailActivity.this,
                        getString(R.string.activity_housing_detail_find_share_partner_hint),
                        Toast.LENGTH_SHORT
                );
                toast.setGravity(
                        Gravity.TOP|Gravity.END,
                        mToolbar.getWidth() - (mToolbar.getContentInsetStartWithNavigation() + mFindRoommateButton.getLeft()),
                        mFindRoommateButton.getBottom()
                );
                toast.show();
                return true;
            }
        });

        mReportButton = (ImageView) findViewById(R.id.activity_share_housing_detail_report_button);
        mReportButton.setColorFilter(whiteColor);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.CURRENT_USER != null) {
                    final CustomEditText editText = new CustomEditText(HousingDetailActivity.this);
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                    editText.setFilters(
                            new InputFilter[] {
                                    new InputFilter.LengthFilter(
                                            Constants.ACTIVITY_HOUSING_DETAIL_NOTE_NUM_CHARACTERS_LIMIT
                                    )
                            }
                    );
                    FrameLayout container = new FrameLayout(HousingDetailActivity.this);
                    FrameLayout.LayoutParams inputReportLayoutParams = new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    inputReportLayoutParams.setMargins(
                            getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_user_note_dialog_edittext_left_right_margin),
                            0,
                            getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_user_note_dialog_edittext_left_right_margin),
                            0
                    );
                    editText.setLayoutParams(inputReportLayoutParams);
                    container.addView(editText);

                    new AlertDialog.Builder(HousingDetailActivity.this)
                            .setTitle(R.string.activity_housing_detail_write_report_dialog_title)
                            .setMessage(R.string.activity_housing_detail_write_note_report_dialog_message)
                            .setView(container)
                            .setPositiveButton(R.string.activity_housing_detail_write_report_dialog_send,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            HousingClient.reportHousing(
                                                    mHousing.getID(),
                                                    new IReportHousingCallback() {
                                                        @Override
                                                        public void onReportComplete(Boolean isSuccess) {
                                                            if (isSuccess) {
                                                                ToastHelper.showCenterToast(
                                                                        getApplicationContext(),
                                                                        R.string.activity_housing_detail_report_successfully_message,
                                                                        Toast.LENGTH_LONG
                                                                );
                                                                ShareSpaceApplication.BUS.post(new ReportHousingEvent());
                                                            } else {
                                                                ToastHelper.showCenterToast(
                                                                        getApplicationContext(),
                                                                        R.string.activity_housing_detail_report_unsuccessfully_message,
                                                                        Toast.LENGTH_LONG
                                                                );
                                                            }
                                                        }

                                                        @Override
                                                        public void onReportFailure(Throwable t) {
                                                            ToastHelper.showCenterToast(
                                                                    getApplicationContext(),
                                                                    R.string.activity_housing_detail_report_unsuccessfully_message,
                                                                    Toast.LENGTH_LONG
                                                            );
                                                            RetrofitClient.showShareSpaceServerConnectionErrorDialog(HousingDetailActivity.this, t);
                                                        }
                                                    }
                                            );
                                        }
                                    })
                            .setNegativeButton(R.string.activity_housing_detail_write_report_dialog_cancel, null)
                            .show();
                } else {
                    new AlertDialog.Builder(HousingDetailActivity.this)
                            .setTitle(R.string.activity_housing_detail_login_required_feature_dialog_title)
                            .setMessage(R.string.activity_housing_detail_login_required_feature_dialog_message)
                            .setPositiveButton(R.string.activity_housing_detail_login_required_feature_dialog_positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(HousingDetailActivity.this, LoginActivity.class);
                                    startActivityForResult(intent, Constants.START_ACTIVITY_LOGIN_REQUEST);
                                }
                            })
                            .setNegativeButton(R.string.activity_housing_detail_login_required_feature_dialog_negative, null)
                            .show();
                }
            }
        });
        mReportButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast toast = Toast.makeText(
                        HousingDetailActivity.this,
                        getString(R.string.activity_housing_detail_report_violation_hint),
                        Toast.LENGTH_SHORT
                );
                toast.setGravity(
                        Gravity.TOP|Gravity.END,
                        mToolbar.getWidth() - (mToolbar.getContentInsetStartWithNavigation() + mReportButton.getLeft()),
                        mReportButton.getBottom()
                );
                toast.show();
                return true;
            }
        });

        mShowHideHousingButton = (ImageView) findViewById(R.id.activity_housing_detail_show_hide_housing_button);
        if (Constants.CURRENT_USER != null
                && Constants.CURRENT_USER.getUserID() == mHousing.getOwner().getUserID()) {
            mShowHideHousingButton.setClickable(false);
            mShowHideHousingButton.setEnabled(false);
            UserClient.getHidingStateOfCurrentHousing(
                    mHousing.getID(),
                    new IGetHidingStateOfCurrentHousingCallback() {
                        @Override
                        public void onGetComplete(Boolean isHidden) {
                            if (isHidden) {
                                if (mShowHideHousingButton.getDrawable().getConstantState()
                                        == hideHousingConstantStateDarkerGray) {
                                    mShowHideHousingButton.setImageDrawable(
                                            ContextCompat.getDrawable(HousingDetailActivity.this, R.drawable.ic_show_darker_gray)
                                    );
                                } else {
                                    mShowHideHousingButton.setImageDrawable(
                                            ContextCompat.getDrawable(HousingDetailActivity.this, R.drawable.ic_show_white)
                                    );
                                }
                                mIsShowButton = true;
                            } else {
                                if (mShowHideHousingButton.getDrawable().getConstantState()
                                        == showHousingConstantStateDarkerGray) {
                                    mShowHideHousingButton.setImageDrawable(
                                            ContextCompat.getDrawable(HousingDetailActivity.this, R.drawable.ic_hide_darker_gray)
                                    );
                                } else {
                                    mShowHideHousingButton.setImageDrawable(
                                            ContextCompat.getDrawable(HousingDetailActivity.this, R.drawable.ic_hide_white)
                                    );
                                }
                                mIsShowButton = false;
                            }
                            mShowHideHousingButton.setClickable(true);
                            mShowHideHousingButton.setEnabled(true);
                        }

                        @Override
                        public void onGetFailure(Throwable t) {

                        }
                    }
            );
        }
        mShowHideHousingButton.setImageDrawable(
                ContextCompat.getDrawable(HousingDetailActivity.this, R.drawable.ic_show_white)
        );
        mShowHideHousingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShowHideHousingButton.setClickable(false);
                mShowHideHousingButton.setEnabled(false);
                if (mIsShowButton) {
                    UserClient.unhideHousing(
                            mHousing.getID(),
                            new IUnhideHousingCallback() {
                                @Override
                                public void onUnhideComplete(Boolean isSuccess) {
                                    if (isSuccess) {
                                        ToastHelper.showCenterToast(
                                                getApplicationContext(),
                                                R.string.activity_housing_detail_show_housing_successfully_message,
                                                Toast.LENGTH_SHORT
                                        );
                                        mHousing.setAvailability(true);
                                        if (mShowHideHousingButton.getDrawable().getConstantState()
                                                == showHousingConstantStateDarkerGray) {
                                            mShowHideHousingButton.setImageDrawable(
                                                    ContextCompat.getDrawable(HousingDetailActivity.this, R.drawable.ic_hide_darker_gray)
                                            );
                                        } else {
                                            mShowHideHousingButton.setImageDrawable(
                                                    ContextCompat.getDrawable(HousingDetailActivity.this, R.drawable.ic_hide_white)
                                            );
                                        }
                                        mIsShowButton = false;
                                        ShareSpaceApplication.BUS.post(new UnhideHousingEvent(mHousing));
                                    } else {
                                        ToastHelper.showCenterToast(
                                                getApplicationContext(),
                                                R.string.activity_housing_detail_show_housing_unsuccessfully_message,
                                                Toast.LENGTH_LONG
                                        );
                                        if (mShowHideHousingButton.getDrawable().getConstantState()
                                                == hideHousingConstantStateDarkerGray) {
                                            mShowHideHousingButton.setImageDrawable(
                                                    ContextCompat.getDrawable(HousingDetailActivity.this, R.drawable.ic_show_darker_gray)
                                            );
                                        } else {
                                            mShowHideHousingButton.setImageDrawable(
                                                    ContextCompat.getDrawable(HousingDetailActivity.this, R.drawable.ic_show_white)
                                            );
                                        }
                                        mIsShowButton = true;
                                    }
                                    mShowHideHousingButton.setClickable(true);
                                    mShowHideHousingButton.setEnabled(true);
                                }

                                @Override
                                public void onUnhideFailure(Throwable t) {
                                    ToastHelper.showCenterToast(
                                            getApplicationContext(),
                                            R.string.activity_housing_detail_show_housing_unsuccessfully_message,
                                            Toast.LENGTH_LONG
                                    );
                                    if (mShowHideHousingButton.getDrawable().getConstantState()
                                            == hideHousingConstantStateDarkerGray) {
                                        mShowHideHousingButton.setImageDrawable(
                                                ContextCompat.getDrawable(HousingDetailActivity.this, R.drawable.ic_show_darker_gray)
                                        );
                                    } else {
                                        mShowHideHousingButton.setImageDrawable(
                                                ContextCompat.getDrawable(HousingDetailActivity.this, R.drawable.ic_show_white)
                                        );
                                    }
                                    mIsShowButton = true;
                                    mShowHideHousingButton.setClickable(true);
                                    mShowHideHousingButton.setEnabled(true);
                                    RetrofitClient.showShareSpaceServerConnectionErrorDialog(HousingDetailActivity.this, t);
                                }
                            }
                    );
                } else {
                    UserClient.hideHousing(
                            mHousing.getID(),
                            new IHideHousingCallback() {
                                @Override
                                public void onHideComplete(Boolean isSuccess) {
                                    if (isSuccess) {
                                        ToastHelper.showCenterToast(
                                                getApplicationContext(),
                                                R.string.activity_housing_detail_hide_housing_successfully_message,
                                                Toast.LENGTH_SHORT
                                        );
                                        mHousing.setAvailability(false);
                                        if (mShowHideHousingButton.getDrawable().getConstantState()
                                                == hideHousingConstantStateDarkerGray) {
                                            mShowHideHousingButton.setImageDrawable(
                                                    ContextCompat.getDrawable(HousingDetailActivity.this, R.drawable.ic_show_darker_gray)
                                            );
                                        } else {
                                            mShowHideHousingButton.setImageDrawable(
                                                    ContextCompat.getDrawable(HousingDetailActivity.this, R.drawable.ic_show_white)
                                            );
                                        }
                                        mIsShowButton = true;
                                        ShareSpaceApplication.BUS.post(new HideHousingEvent(mHousing));
                                    } else {
                                        ToastHelper.showCenterToast(
                                                getApplicationContext(),
                                                R.string.activity_housing_detail_hide_housing_unsuccessfully_message,
                                                Toast.LENGTH_LONG
                                        );
                                        if (mShowHideHousingButton.getDrawable().getConstantState()
                                                == showHousingConstantStateDarkerGray) {
                                            mShowHideHousingButton.setImageDrawable(
                                                    ContextCompat.getDrawable(HousingDetailActivity.this, R.drawable.ic_hide_darker_gray)
                                            );
                                        } else {
                                            mShowHideHousingButton.setImageDrawable(
                                                    ContextCompat.getDrawable(HousingDetailActivity.this, R.drawable.ic_hide_white)
                                            );
                                        }
                                        mIsShowButton = false;
                                    }
                                    mShowHideHousingButton.setClickable(true);
                                    mShowHideHousingButton.setEnabled(true);
                                }

                                @Override
                                public void onHideFailure(Throwable t) {
                                    ToastHelper.showCenterToast(
                                            getApplicationContext(),
                                            R.string.activity_housing_detail_hide_housing_unsuccessfully_message,
                                            Toast.LENGTH_LONG
                                    );
                                    if (mShowHideHousingButton.getDrawable().getConstantState()
                                            == showHousingConstantStateDarkerGray) {
                                        mShowHideHousingButton.setImageDrawable(
                                                ContextCompat.getDrawable(HousingDetailActivity.this, R.drawable.ic_hide_darker_gray)
                                        );
                                    } else {
                                        mShowHideHousingButton.setImageDrawable(
                                                ContextCompat.getDrawable(HousingDetailActivity.this, R.drawable.ic_hide_white)
                                        );
                                    }
                                    mIsShowButton = false;
                                    mShowHideHousingButton.setClickable(true);
                                    mShowHideHousingButton.setEnabled(true);
                                    RetrofitClient.showShareSpaceServerConnectionErrorDialog(HousingDetailActivity.this, t);
                                }
                            }
                    );
                }
            }
        });
        mShowHideHousingButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast toast = Toast.makeText(
                        HousingDetailActivity.this,
                        getString(R.string.activity_housing_detail_show_hide_housing_hint),
                        Toast.LENGTH_SHORT
                );
                toast.setGravity(
                        Gravity.TOP|Gravity.END,
                        mToolbar.getWidth() - (mToolbar.getContentInsetStartWithNavigation() + mShowHideHousingButton.getLeft()),
                        mShowHideHousingButton.getBottom()
                );
                toast.show();
                return true;
            }
        });
    }

    private void initUserAppBarAndPhotosAndBookingLayout() {
        mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        mShowHideHousingButton.setVisibility(View.GONE);

        mBookingLayoutViewStub = (ViewStub) findViewById(R.id.activity_share_housing_detail_booking_layout_view_stub);
        mBookingLayoutViewStub.setLayoutResource(R.layout.activity_housing_detail_user_booking_layout);
        mBookingLayoutViewStub.inflate();

        mBookingDivider = findViewById(R.id.activity_share_housing_detail_booking_divider);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_horizontal_line_height)
        );
        layoutParams.addRule(RelativeLayout.ABOVE, R.id.activity_housing_detail_booking_layout);
        mBookingDivider.setLayoutParams(layoutParams);

        mCallButton = (RelativeLayout) findViewById(R.id.activity_housing_detail_call_button);
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.CURRENT_USER != null) {
                    Intent intent = new Intent(
                            Intent.ACTION_DIAL,
                            Uri.fromParts("tel", mHousing.getOwner().getPhoneNumber(), null)
                    );
                    startActivity(intent);
                } else {
                    new AlertDialog.Builder(HousingDetailActivity.this)
                            .setTitle(R.string.activity_housing_detail_login_required_feature_dialog_title)
                            .setMessage(R.string.activity_housing_detail_login_required_feature_dialog_message)
                            .setPositiveButton(R.string.activity_housing_detail_login_required_feature_dialog_positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(HousingDetailActivity.this, LoginActivity.class);
                                    startActivityForResult(intent, Constants.START_ACTIVITY_LOGIN_REQUEST);
                                }
                            })
                            .setNegativeButton(R.string.activity_housing_detail_login_required_feature_dialog_negative, null)
                            .show();
                }
            }
        });
        mScheduleButton = (RelativeLayout) findViewById(R.id.activity_housing_detail_schedule_button);
        mInputScheduleNotes = new CustomEditText(HousingDetailActivity.this);
        mInputScheduleNotes.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        mInputScheduleNotes.setFilters(
                new InputFilter[] {
                        new InputFilter.LengthFilter(
                                Constants.ACTIVITY_HOUSING_DETAIL_SCHEDULE_NOTE_NUM_CHARACTERS_LIMIT
                        )
                }
        );
        mScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.CURRENT_USER != null) {
                    if (mHousingAppointment != null) {
                        mScheduleDialogOptionsEditDeleteAppointment
                                = new AlertDialog.Builder(HousingDetailActivity.this);
                        if (!mHousingAppointment.isOwnerConfirmed()) {
                            mScheduleDialogOptionsEditDeleteAppointment.setTitle(
                                    getString(R.string.activity_housing_detail_schedule_dialog_options_title) + "\n" +
                                    getString(R.string.activity_housing_detail_schedule_dialog_options_message)
                            );
                        } else {
                            mScheduleDialogOptionsEditDeleteAppointment.setTitle(
                                    getString(R.string.activity_housing_detail_schedule_dialog_options_title)
                            );
                        }
                        CharSequence options[] = new CharSequence[] {
                                getString(R.string.activity_housing_detail_update_schedule_dialog_option_menu),
                                getString(R.string.activity_housing_detail_delete_schedule_dialog_option_menu)
                        };
                        mScheduleDialogOptionsEditDeleteAppointment.setItems(options,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                                    handleHousingAppointment(which);
                                                } else {
                                                    handleHousingAppointmentJellyBean(which);
                                                }
                                            }
                                        })
                                .show();
                    } else {
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            handleHousingAppointment(2);
                        } else {
                            handleHousingAppointmentJellyBean(2);
                        }
                    }
                } else {
                    new AlertDialog.Builder(HousingDetailActivity.this)
                            .setTitle(R.string.activity_housing_detail_login_required_feature_dialog_title)
                            .setMessage(R.string.activity_housing_detail_login_required_feature_dialog_message)
                            .setPositiveButton(R.string.activity_housing_detail_login_required_feature_dialog_positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(HousingDetailActivity.this, LoginActivity.class);
                                    startActivityForResult(intent, Constants.START_ACTIVITY_LOGIN_REQUEST);
                                }
                            })
                            .setNegativeButton(R.string.activity_housing_detail_login_required_feature_dialog_negative, null)
                            .show();
                }
            }
        });
        mTakeNoteButton = (RelativeLayout) findViewById(R.id.activity_housing_detail_take_note_button);
        mInputNotes = new CustomEditText(HousingDetailActivity.this);
        mInputNotes.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        mInputNotes.setFilters(
                new InputFilter[] {
                        new InputFilter.LengthFilter(
                                Constants.ACTIVITY_HOUSING_DETAIL_NOTE_NUM_CHARACTERS_LIMIT
                        )
                }
        );
        if (mUserNoteTextView != null) {
            mInputNotes.setText(mUserNoteTextView.getText());
        } else {
            mInputNotes.setText("");
        }
        mTakeNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.CURRENT_USER != null) {
                    if (mUserNoteTextView != null) {
                        mInputNotes.setText(mUserNoteTextView.getText());
                    } else {
                        mInputNotes.setText("");
                    }
                    FrameLayout container = new FrameLayout(HousingDetailActivity.this);
                    FrameLayout.LayoutParams inputNotesLayoutParams = new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    inputNotesLayoutParams.setMargins(
                            getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_user_note_dialog_edittext_left_right_margin),
                            0,
                            getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_user_note_dialog_edittext_left_right_margin),
                            0
                    );
                    mInputNotes.setLayoutParams(inputNotesLayoutParams);
                    if (mInputNotes.getParent() != null) {
                        ((ViewGroup) mInputNotes.getParent()).removeView(mInputNotes);
                    }
                    container.addView(mInputNotes);

                    final AlertDialog notesDialog = new AlertDialog.Builder(HousingDetailActivity.this).create();
                    notesDialog.setTitle(R.string.activity_housing_detail_write_note_dialog_title);
                    notesDialog.setMessage(getString(R.string.activity_housing_detail_write_note_report_dialog_message));
                    notesDialog.setView(container);
                    notesDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.activity_housing_detail_write_note_dialog_save),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final String tempNotes = mUserNoteTextView == null ? "" : mUserNoteTextView.getText().toString();
                                    if (!TextUtils.isEmpty(mInputNotes.getText().toString().trim())) {
                                        if (mUserNoteTextView == null) {
                                            inflateUserNoteLayout();
                                        }
                                        mUserNoteTextView.setText(mInputNotes.getText().toString().trim());
                                    } else {
                                        if (mUserNoteTextView != null) {
                                            inflateUserNoteEmptyLayout();
                                        }
                                    }
                                    mUserNote = new Note(
                                            mHousing.getID(),
                                            mHousing.getTitle(),
                                            mInputNotes.getText().toString().trim(),
                                            new Date()
                                    );
                                    if (!TextUtils.isEmpty(mInputNotes.getText().toString().trim())) {
                                        if (!TextUtils.isEmpty(tempNotes)) {
                                            HousingClient.updateNote(
                                                    mUserNote,
                                                    new INoteUpdatingCallback() {
                                                        @Override
                                                        public void onUpdateComplete(Note note) {
                                                            if (note != null) {
                                                                mUserNote = note;
                                                                ToastHelper.showCenterToast(
                                                                        getApplicationContext(),
                                                                        R.string.activity_housing_detail_note_updated_message,
                                                                        Toast.LENGTH_SHORT
                                                                );
                                                                ShareSpaceApplication.BUS.post(new UpdateHousingNoteEvent(
                                                                        new HistoryHousingNote(mHousing, note.getDateTimeCreated())
                                                                ));
                                                            } else {
                                                                ToastHelper.showCenterToast(
                                                                        getApplicationContext(),
                                                                        R.string.activity_housing_detail_note_not_updated_message,
                                                                        Toast.LENGTH_LONG
                                                                );
                                                            }
                                                        }

                                                        @Override
                                                        public void onUpdateFailure(Throwable t) {
                                                            ToastHelper.showCenterToast(
                                                                    getApplicationContext(),
                                                                    R.string.activity_housing_detail_note_not_updated_message,
                                                                    Toast.LENGTH_LONG
                                                            );
                                                            RetrofitClient.showShareSpaceServerConnectionErrorDialog(HousingDetailActivity.this, t);
                                                        }
                                                    }
                                            );
                                        } else {
                                            HousingClient.postNote(
                                                    mUserNote, -1,
                                                    new INotePostingCallback() {
                                                        @Override
                                                        public void onPostComplete(Note note) {
                                                            if (note != null) {
                                                                mUserNote = note;
                                                                ToastHelper.showCenterToast(
                                                                        getApplicationContext(),
                                                                        R.string.activity_housing_detail_note_saved_message,
                                                                        Toast.LENGTH_SHORT
                                                                );
                                                                ShareSpaceApplication.BUS.post(new TakeHousingNoteEvent(
                                                                        new HistoryHousingNote(mHousing, note.getDateTimeCreated())
                                                                ));
                                                            } else {
                                                                ToastHelper.showCenterToast(
                                                                        getApplicationContext(),
                                                                        R.string.activity_housing_detail_note_not_saved_message,
                                                                        Toast.LENGTH_LONG
                                                                );
                                                            }
                                                        }

                                                        @Override
                                                        public void onPostFailure(Throwable t) {
                                                            ToastHelper.showCenterToast(
                                                                    getApplicationContext(),
                                                                    R.string.activity_housing_detail_note_not_saved_message,
                                                                    Toast.LENGTH_LONG
                                                            );
                                                            RetrofitClient.showShareSpaceServerConnectionErrorDialog(HousingDetailActivity.this, t);
                                                        }
                                                    }
                                            );
                                        }
                                    } else {
                                        new AlertDialog.Builder(HousingDetailActivity.this)
                                                .setTitle(R.string.activity_housing_detail_delete_note_confirm_dialog_title)
                                                .setMessage(R.string.activity_housing_detail_delete_note_confirm_dialog_message)
                                                .setPositiveButton(R.string.activity_housing_detail_delete_note_confirm_dialog_positive,
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                mInputNotes.setText(tempNotes);
                                                                notesDialog.show();
                                                            }
                                                        })
                                                .setNegativeButton(R.string.activity_housing_detail_delete_note_confirm_dialog_negative,
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                HousingClient.deleteNote(
                                                                        mHousing.getID(),
                                                                        new INoteDeletingCallback() {
                                                                            @Override
                                                                            public void onDeleteComplete(Boolean isDeleted) {
                                                                                if (isDeleted) {
                                                                                    ToastHelper.showCenterToast(
                                                                                            getApplicationContext(),
                                                                                            R.string.activity_housing_detail_note_deleted_message,
                                                                                            Toast.LENGTH_SHORT
                                                                                    );
                                                                                    ShareSpaceApplication.BUS.post(new DeleteHousingNoteEvent(
                                                                                            new HistoryHousingNote(mHousing, mUserNote.getDateTimeCreated())
                                                                                    ));
                                                                                    mUserNote = null;
                                                                                } else {
                                                                                    ToastHelper.showCenterToast(
                                                                                            getApplicationContext(),
                                                                                            R.string.activity_housing_detail_note_not_deleted_message,
                                                                                            Toast.LENGTH_LONG
                                                                                    );
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onDeleteFailure(Throwable t) {
                                                                                ToastHelper.showCenterToast(
                                                                                        getApplicationContext(),
                                                                                        R.string.activity_housing_detail_note_not_deleted_message,
                                                                                        Toast.LENGTH_LONG
                                                                                );
                                                                                RetrofitClient.showShareSpaceServerConnectionErrorDialog(HousingDetailActivity.this, t);
                                                                            }
                                                                        }
                                                                );
                                                            }
                                                        })
                                                .show();
                                    }
                                }
                            });
                    notesDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.activity_housing_detail_write_note_dialog_cancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (TextUtils.isEmpty(mInputNotes.getText().toString())) {
                                        dialog.dismiss();
                                    } else {
                                        new AlertDialog.Builder(HousingDetailActivity.this)
                                                .setTitle(R.string.activity_housing_detail_close_writing_note_confirm_dialog_title)
                                                .setMessage(R.string.activity_housing_detail_close_writing_note_confirm_dialog_message)
                                                .setPositiveButton(R.string.activity_housing_detail_close_writing_note_confirm_dialog_positive,
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                notesDialog.show();
                                                            }
                                                        })
                                                .setNegativeButton(R.string.activity_housing_detail_close_writing_note_confirm_dialog_negative, null)
                                                .show();
                                    }
                                }
                            });
                    notesDialog.setCanceledOnTouchOutside(false);
                    notesDialog.show();
                } else {
                    new AlertDialog.Builder(HousingDetailActivity.this)
                            .setTitle(R.string.activity_housing_detail_login_required_feature_dialog_title)
                            .setMessage(R.string.activity_housing_detail_login_required_feature_dialog_message)
                            .setPositiveButton(R.string.activity_housing_detail_login_required_feature_dialog_positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(HousingDetailActivity.this, LoginActivity.class);
                                    startActivityForResult(intent, Constants.START_ACTIVITY_LOGIN_REQUEST);
                                }
                            })
                            .setNegativeButton(R.string.activity_housing_detail_login_required_feature_dialog_negative, null)
                            .show();
                }
            }
        });
        mTakePhotoButton = (RelativeLayout) findViewById(R.id.activity_housing_detail_take_photo_button);
        mTakePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.CURRENT_USER != null) {
                    if (mUserPhotoURLs.size() < Constants.ACTIVITY_HOUSING_DETAIL_NUM_PHOTOS_LIMIT) {
                        mPermissionHelper.capturePhotoWithCheckPermissions(getString(R.string.activity_post_house_camera_image_directory));
                    } else {
                        ToastHelper.showCenterToast(
                                getApplicationContext(),
                                getString(R.string.activity_housing_detail_photos_reached_limit_message, Constants.ACTIVITY_HOUSING_DETAIL_NUM_PHOTOS_LIMIT),
                                Toast.LENGTH_LONG
                        );
                    }
                } else {
                    new AlertDialog.Builder(HousingDetailActivity.this)
                            .setTitle(R.string.activity_housing_detail_login_required_feature_dialog_title)
                            .setMessage(R.string.activity_housing_detail_login_required_feature_dialog_message)
                            .setPositiveButton(R.string.activity_housing_detail_login_required_feature_dialog_positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(HousingDetailActivity.this, LoginActivity.class);
                                    startActivityForResult(intent, Constants.START_ACTIVITY_LOGIN_REQUEST);
                                }
                            })
                            .setNegativeButton(R.string.activity_housing_detail_login_required_feature_dialog_negative, null)
                            .show();
                }
            }
        });
        if (Constants.CURRENT_USER != null) {
            mScheduleButton.setClickable(false);
            mScheduleButton.setEnabled(false);
            UserClient.getCurrentHousingAppointment(mHousing.getID(), new IHousingAppointmentGettingCallback() {
                @Override
                public void onGetComplete(HousingAppointment housingAppointment) {
                    mHousingAppointment = housingAppointment;
                    mScheduleButton.setClickable(true);
                    mScheduleButton.setEnabled(true);
                }

                @Override
                public void onGetFailure(Throwable t) {

                }
            });
            mTakeNoteButton.setClickable(false);
            mTakeNoteButton.setEnabled(false);
            HousingClient.getCurrentUserNote(mHousing.getID(), new INoteGettingCallback() {
                @Override
                public void onGetComplete(Note note) {
                    if (note != null && !TextUtils.isEmpty(note.getContent())) {
                        inflateUserNoteLayout();
                        mUserNoteTextView.setText(note.getContent());
                    } else {
                        mUserNoteTextView = null;
                    }
                    mTakeNoteButton.setClickable(true);
                    mTakeNoteButton.setEnabled(true);
                }

                @Override
                public void onGetFailure(Throwable t) {
                    RetrofitClient.showShareSpaceServerConnectionErrorDialog(HousingDetailActivity.this, t);
                }
            });
            mTakePhotoButton.setClickable(false);
            mTakePhotoButton.setEnabled(false);
            HousingClient.getCurrentUserPhotoURLs(mHousing.getID(), new IPhotoGettingCallback() {
                @Override
                public void onGetComplete(ArrayList<String> photoURLs) {
                    if (photoURLs != null && photoURLs.size() > 0) {
                        ViewGroup parent = (ViewGroup) mUserPhotoLayout.getParent();
                        int index = parent.indexOfChild(mUserPhotoLayout);
                        parent.removeViewAt(index);

                        View userPhotoLayout = mLayoutInflater.inflate(R.layout.activity_housing_detail_user_photo, parent, false);
                        userPhotoLayout.setId(R.id.activity_housing_detail_user_photo_layout);

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        layoutParams.setMargins(
                                getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_content_left_right_margin),
                                0,
                                getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_content_left_right_margin),
                                0
                        );
                        userPhotoLayout.setLayoutParams(layoutParams);

                        parent.addView(userPhotoLayout, index);
                        mUserPhotoLayout = (LinearLayout) findViewById(R.id.activity_housing_detail_user_photo_layout);
                        mPhotoGridContainer = (GridLayout) mUserPhotoLayout.findViewById(R.id.activity_housing_detail_photo_grid_container);

                        if (photoURLs != null) {
                            mUserPhotoURLs = photoURLs;
                        }
                        mLoadPhotoIndex = 0;
                        loadAllPhoto();
                    }
                    mTakePhotoButton.setClickable(true);
                    mTakePhotoButton.setEnabled(true);
                }

                @Override
                public void onGetFailure(Throwable t) {
                    RetrofitClient.showShareSpaceServerConnectionErrorDialog(HousingDetailActivity.this, t);
                }
            });
        }
    }

    private void initOwnerAppBarAndBookingLayout() {
        mShowHideHousingButton.setVisibility(View.VISIBLE);

        mHeartButton.setVisibility(View.GONE);
        mFindRoommateButton.setVisibility(View.GONE);
        mReportButton.setVisibility(View.GONE);

        mUserNoteLayout.setVisibility(View.GONE);
        mUserPhotoLayout.setVisibility(View.GONE);

        if (!mIsUserLayoutInflated) {
            if (mHousing.isAvailable()) {
                mShowHideHousingButton.setImageDrawable(
                        ContextCompat.getDrawable(HousingDetailActivity.this, R.drawable.ic_hide_white)
                );
                mIsShowButton = false;
            } else {
                mShowHideHousingButton.setImageDrawable(
                        ContextCompat.getDrawable(HousingDetailActivity.this, R.drawable.ic_show_white)
                );
                mIsShowButton = true;
            }

            mBookingLayoutViewStub = (ViewStub) findViewById(R.id.activity_share_housing_detail_booking_layout_view_stub);
            mBookingLayoutViewStub.setLayoutResource(R.layout.activity_housing_detail_owner_booking_layout);
            mBookingLayoutViewStub.inflate();

            mBookingDivider = findViewById(R.id.activity_share_housing_detail_booking_divider);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_horizontal_line_height)
            );
            layoutParams.addRule(RelativeLayout.ABOVE, R.id.activity_housing_detail_booking_layout);
            mBookingDivider.setLayoutParams(layoutParams);
        } else {
            View bookingLayout = findViewById(R.id.activity_housing_detail_booking_layout);
            ViewGroup parent = (ViewGroup) bookingLayout.getParent();
            int index = parent.indexOfChild(bookingLayout);
            parent.removeViewAt(index);
            bookingLayout = mLayoutInflater.inflate(R.layout.activity_housing_detail_owner_booking_layout, parent, false);
            bookingLayout.setId(R.id.activity_housing_detail_booking_layout);

            RelativeLayout.LayoutParams bookingLayoutParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            );
            bookingLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            bookingLayout.setLayoutParams(bookingLayoutParams);

            RelativeLayout.LayoutParams bookingDividerLayoutParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_horizontal_line_height)
            );
            bookingDividerLayoutParams.addRule(RelativeLayout.ABOVE, R.id.activity_housing_detail_booking_layout);
            mBookingDivider.setLayoutParams(bookingDividerLayoutParams);

            parent.addView(bookingLayout, index);
        }
        mEditPostButton = (RelativeLayout) findViewById(R.id.activity_housing_detail_edit_post_button);
        mEditPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        HousingDetailActivity.this,
                        PostHouseActivity.class
                );
                intent.putExtra(Constants.IS_EDIT_HOUSING_EXTRA, true);
                Gson gson = new Gson();
                intent.putExtra(
                        Constants.HOUSING_INFO_FOR_EDITING_POST_EXTRA,
                        gson.toJson(mHousing)
                );
                startActivityForResult(intent, Constants.START_ACTIVITY_POST_HOUSE_REQUEST);
            }
        });
        mDeleteHousingButton = (RelativeLayout) findViewById(R.id.activity_housing_detail_delete_housing_button);
        mDeleteHousingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(HousingDetailActivity.this)
                        .setTitle(R.string.activity_housing_detail_delete_housing_dialog_title)
                        .setMessage(R.string.activity_housing_detail_delete_housing_dialog_message)
                        .setPositiveButton(R.string.activity_housing_detail_delete_housing_dialog_positive,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FirebaseStorageHelper.deleteHousing(
                                                HousingDetailActivity.this,
                                                mHousing.getID(),
                                                new OnHousingDeletingListener() {
                                                    @Override
                                                    public void onDeleteComplete(Boolean isDeleted) {
                                                        ToastHelper.showCenterToast(
                                                                getApplicationContext(),
                                                                R.string.activity_housing_detail_delete_housing_successfully_toast_message,
                                                                Toast.LENGTH_SHORT
                                                        );
                                                        ShareSpaceApplication.BUS.post(new DeleteHousingEvent(mHousing));
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onDeleteFailure(Throwable t) {
                                                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(HousingDetailActivity.this, t);
                                                    }
                                                }
                                        );
                                    }
                                })
                        .setNegativeButton(R.string.activity_housing_detail_delete_housing_dialog_negative, null)
                        .show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mPermissionHelper.getCameraHelper()
                .onActivityResult(requestCode, resultCode, data)) {
            final Uri imageUri = Uri.parse(mPermissionHelper.getCameraHelper().getCurrentImagePath());
            if (imageUri != null) {
                FirebaseStorageHelper.postPhoto(
                        this, mHousing.getID(), imageUri, -1,
                        mHousing.getTitle(), new OnPhotoPostingListener() {
                            @Override
                            public void onPostSuccess(String photoURL) {
                                if (mUserPhotoURLs.isEmpty()) {
                                    ViewGroup parent = (ViewGroup) mUserPhotoLayout.getParent();
                                    int index = parent.indexOfChild(mUserPhotoLayout);
                                    parent.removeViewAt(index);

                                    View userPhotoLayout = mLayoutInflater.inflate(R.layout.activity_housing_detail_user_photo, parent, false);
                                    userPhotoLayout.setId(R.id.activity_housing_detail_user_photo_layout);

                                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                                    );
                                    layoutParams.setMargins(
                                            getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_content_left_right_margin),
                                            0,
                                            getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_content_left_right_margin),
                                            0
                                    );
                                    userPhotoLayout.setLayoutParams(layoutParams);

                                    parent.addView(userPhotoLayout, index);
                                    mUserPhotoLayout = (LinearLayout) findViewById(R.id.activity_housing_detail_user_photo_layout);
                                    mPhotoGridContainer = (GridLayout) mUserPhotoLayout.findViewById(R.id.activity_housing_detail_photo_grid_container);

                                    ShareSpaceApplication.BUS.post(new TakeHousingPhotoEvent(
                                            new HistoryHousingPhoto(mHousing, new Date())
                                    ));
                                }
                                mUserPhotoURLs.add(imageUri.toString());
                                ToastHelper.showCenterToast(
                                        getApplicationContext(),
                                        R.string.activity_housing_detail_photos_synced_message,
                                        Toast.LENGTH_SHORT
                                );
                                loadOnePhoto();
                            }

                            @Override
                            public void onPostFailure(Throwable t) {
                                ToastHelper.showCenterToast(
                                        getApplicationContext(),
                                        R.string.activity_housing_detail_photos_not_synced_message,
                                        Toast.LENGTH_LONG
                                );
                                RetrofitClient.showShareSpaceServerConnectionErrorDialog(HousingDetailActivity.this, t);
                            }
                        }
                );
            }
        }
        if (requestCode == Constants.START_ACTIVITY_LOGIN_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (Constants.CURRENT_USER != null) {
                    // Current User is the Owner of this House.
                    if (mHousing.getOwner().getUserID() == Constants.CURRENT_USER.getUserID()) {
                        initOwnerAppBarAndBookingLayout();
                    }
                }
            }
        } else if (requestCode == Constants.START_ACTIVITY_SHOW_EXIST_SHARE_POSTS_OF_CURRENT_HOUSING_REQUEST) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
        } else if (requestCode == Constants.START_ACTIVITY_POST_HOUSE_REQUEST) {
            if (resultCode == RESULT_OK) {
                String housingResult = data.getStringExtra(Constants.ACTIVITY_HOUSING_DETAIL_HOUSING_EXTRA);
                Intent intent = new Intent(this, HousingDetailActivity.class);
                intent.putExtra(Constants.ACTIVITY_HOUSING_DETAIL_HOUSING_EXTRA, housingResult);
                finish();
                startActivity(intent);
            }
        } else if (requestCode == Constants.START_ACTIVITY_POST_SHARE_HOUSE_REQUEST) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    private void handleHousingAppointment(int selectedOption) {
        final Calendar calendar = Calendar.getInstance();
        if (selectedOption == 0) {
            DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendar.set(Calendar.MINUTE, minute);

                            if (!TextUtils.isEmpty(mHousingAppointment.getContent())) {
                                mInputScheduleNotes.setText(mHousingAppointment.getContent());
                            } else {
                                mInputScheduleNotes.setText("");
                            }
                            FrameLayout container = new FrameLayout(HousingDetailActivity.this);
                            FrameLayout.LayoutParams inputScheduleNotesLayoutParams = new FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            inputScheduleNotesLayoutParams.setMargins(
                                    getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_user_note_dialog_edittext_left_right_margin),
                                    0,
                                    getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_user_note_dialog_edittext_left_right_margin),
                                    0
                            );
                            mInputScheduleNotes.setLayoutParams(inputScheduleNotesLayoutParams);
                            if (mInputScheduleNotes.getParent() != null) {
                                ((ViewGroup) mInputScheduleNotes.getParent()).removeView(mInputScheduleNotes);
                            }
                            container.addView(mInputScheduleNotes);

                            mScheduleNotesDialog = new AlertDialog.Builder(HousingDetailActivity.this).create();
                            mScheduleNotesDialog.setTitle(R.string.activity_housing_detail_schedule_note_dialog_title);
                            mScheduleNotesDialog.setMessage(getString(R.string.activity_housing_detail_schedule_note_dialog_message));
                            mScheduleNotesDialog.setView(container);
                            mScheduleNotesDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.activity_housing_detail_schedule_note_dialog_save),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    String s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(calendar.getTime());
                                                    s = s.substring(0, s.length() - 2) + ":" + s.substring(s.length() - 2, s.length());
                                                    UserClient.updateHousingAppointment(
                                                            mHousing.getID(), mHousing.getOwner().getUserID(),
                                                            s, mInputScheduleNotes.getText().toString(),
                                                            new IHousingAppointmentUpdatingCallback() {
                                                                @Override
                                                                public void onUpdateComplete(HousingAppointment housingAppointment) {
                                                                    if (housingAppointment != null) {
                                                                        mHousingAppointment = housingAppointment;
                                                                        ToastHelper.showCenterToast(
                                                                                getApplicationContext(),
                                                                                R.string.activity_housing_detail_set_schedule_successfully_message,
                                                                                Toast.LENGTH_LONG
                                                                        );
                                                                        ShareSpaceApplication.BUS.post(new UpdateHousingAppointmentEvent(
                                                                                housingAppointment
                                                                        ));
                                                                    } else {
                                                                        ToastHelper.showCenterToast(
                                                                                getApplicationContext(),
                                                                                R.string.activity_housing_detail_set_schedule_unsuccessfully_message,
                                                                                Toast.LENGTH_LONG
                                                                        );
                                                                    }
                                                                }

                                                                @Override
                                                                public void onUpdateFailure(Throwable t) {
                                                                    ToastHelper.showCenterToast(
                                                                            getApplicationContext(),
                                                                            R.string.activity_housing_detail_set_schedule_unsuccessfully_message,
                                                                            Toast.LENGTH_LONG
                                                                    );
                                                                    RetrofitClient.showShareSpaceServerConnectionErrorDialog(HousingDetailActivity.this, t);
                                                                }
                                                            }
                                                    );
                                                }
                                            });
                            mScheduleNotesDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.activity_housing_detail_schedule_note_dialog_cancel),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    new AlertDialog.Builder(HousingDetailActivity.this)
                                                            .setTitle(R.string.activity_housing_detail_update_schedule_note_confirm_dialog_title)
                                                            .setMessage(R.string.activity_housing_detail_update_schedule_note_confirm_dialog_message)
                                                            .setPositiveButton(R.string.activity_housing_detail_update_schedule_note_confirm_dialog_positive,
                                                                    new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            mScheduleNotesDialog.show();
                                                                        }
                                                                    })
                                                            .setNegativeButton(R.string.activity_housing_detail_update_schedule_note_confirm_dialog_negative, null)
                                                            .show();
                                                }
                                            });
                            mScheduleNotesDialog.setCanceledOnTouchOutside(false);
                            mScheduleNotesDialog.show();
                        }
                    };
                    mScheduleTimePickerDialog =
                            new TimePickerDialog(HousingDetailActivity.this, onTimeSetListener,
                                    Integer.parseInt(new SimpleDateFormat("HH").format(mHousingAppointment.getAppointmentDateTime())),
                                    Integer.parseInt(new SimpleDateFormat("mm").format(mHousingAppointment.getAppointmentDateTime())),
                                    true
                            );
                    mScheduleTimePickerDialog.setCanceledOnTouchOutside(false);
                    mScheduleTimePickerDialog.show();
                }
            };
            mScheduleDatePickerDialog =
                    new DatePickerDialog(HousingDetailActivity.this, onDateSetListener,
                            Integer.parseInt(new SimpleDateFormat("yyyy").format(mHousingAppointment.getAppointmentDateTime())),
                            Integer.parseInt(new SimpleDateFormat("MM").format(mHousingAppointment.getAppointmentDateTime())) - 1,
                            Integer.parseInt(new SimpleDateFormat("dd").format(mHousingAppointment.getAppointmentDateTime())));
            mScheduleDatePickerDialog.setTitle(R.string.activity_housing_detail_update_schedule_dialog_title);
            mScheduleDatePickerDialog.setCanceledOnTouchOutside(false);
            mScheduleDatePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            mScheduleDatePickerDialog.show();
        } else if (selectedOption == 1) {
            new AlertDialog.Builder(HousingDetailActivity.this)
                    .setTitle(R.string.activity_housing_detail_delete_schedule_note_confirm_dialog_title)
                    .setMessage(R.string.activity_housing_detail_delete_schedule_note_confirm_dialog_message)
                    .setPositiveButton(R.string.activity_housing_detail_delete_schedule_note_confirm_dialog_positive,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    UserClient.deleteHousingAppointment(
                                            mHousing.getID(), mHousing.getOwner().getUserID(),
                                            new IHousingAppointmentDeletingCallback() {
                                                @Override
                                                public void onDeleteComplete(HousingAppointment housingAppointment) {
                                                    if (housingAppointment != null) {
                                                        mHousingAppointment = null;
                                                        ToastHelper.showCenterToast(
                                                                getApplicationContext(),
                                                                R.string.activity_housing_detail_delete_schedule_successfully_message,
                                                                Toast.LENGTH_LONG
                                                        );
                                                        ShareSpaceApplication.BUS.post(new DeleteHousingAppointmentEvent(
                                                                housingAppointment
                                                        ));
                                                    } else {
                                                        ToastHelper.showCenterToast(
                                                                getApplicationContext(),
                                                                R.string.activity_housing_detail_delete_schedule_unsuccessfully_message,
                                                                Toast.LENGTH_LONG
                                                        );
                                                    }
                                                }

                                                @Override
                                                public void onDeleteFailure(Throwable t) {
                                                    ToastHelper.showCenterToast(
                                                            getApplicationContext(),
                                                            R.string.activity_housing_detail_delete_schedule_unsuccessfully_message,
                                                            Toast.LENGTH_LONG
                                                    );
                                                    RetrofitClient.showShareSpaceServerConnectionErrorDialog(HousingDetailActivity.this, t);
                                                }
                                            }
                                    );
                                }
                            })
                    .setNegativeButton(R.string.activity_housing_detail_delete_schedule_note_confirm_dialog_negative, null)
                    .show();
        } else if (selectedOption == 2) {
            DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendar.set(Calendar.MINUTE, minute);

                            mInputScheduleNotes.setText("");
                            FrameLayout container = new FrameLayout(HousingDetailActivity.this);
                            FrameLayout.LayoutParams inputScheduleNotesLayoutParams = new FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            inputScheduleNotesLayoutParams.setMargins(
                                    getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_user_note_dialog_edittext_left_right_margin),
                                    0,
                                    getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_user_note_dialog_edittext_left_right_margin),
                                    0
                            );
                            mInputScheduleNotes.setLayoutParams(inputScheduleNotesLayoutParams);
                            if (mInputScheduleNotes.getParent() != null) {
                                ((ViewGroup) mInputScheduleNotes.getParent()).removeView(mInputScheduleNotes);
                            }
                            container.addView(mInputScheduleNotes);

                            mScheduleNotesDialog = new AlertDialog.Builder(HousingDetailActivity.this).create();
                            mScheduleNotesDialog.setTitle(R.string.activity_housing_detail_schedule_note_dialog_title);
                            mScheduleNotesDialog.setMessage(getString(R.string.activity_housing_detail_schedule_note_dialog_message));
                            mScheduleNotesDialog.setView(container);
                            mScheduleNotesDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.activity_housing_detail_schedule_note_dialog_save),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    String s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(calendar.getTime());
                                                    s = s.substring(0, s.length() - 2) + ":" + s.substring(s.length() - 2, s.length());
                                                    UserClient.setNewHousingAppointment(
                                                            mHousing.getID(), mHousing.getOwner().getUserID(),
                                                            s, mInputScheduleNotes.getText().toString(),
                                                            new IHousingAppointmentPostingCallback() {
                                                                @Override
                                                                public void onPostComplete(HousingAppointment housingAppointment) {
                                                                    if (housingAppointment != null) {
                                                                        mHousingAppointment = housingAppointment;
                                                                        ToastHelper.showCenterToast(
                                                                                getApplicationContext(),
                                                                                R.string.activity_housing_detail_set_schedule_successfully_message,
                                                                                Toast.LENGTH_SHORT
                                                                        );
                                                                        ShareSpaceApplication.BUS.post(new SetNewHousingAppointmentEvent(
                                                                                housingAppointment
                                                                        ));
                                                                    } else {
                                                                        ToastHelper.showCenterToast(
                                                                                getApplicationContext(),
                                                                                R.string.activity_housing_detail_set_schedule_unsuccessfully_message,
                                                                                Toast.LENGTH_LONG
                                                                        );
                                                                    }
                                                                }

                                                                @Override
                                                                public void onPostFailure(Throwable t) {
                                                                    ToastHelper.showCenterToast(
                                                                            getApplicationContext(),
                                                                            R.string.activity_housing_detail_set_schedule_unsuccessfully_message,
                                                                            Toast.LENGTH_LONG
                                                                    );
                                                                    RetrofitClient.showShareSpaceServerConnectionErrorDialog(HousingDetailActivity.this, t);
                                                                }
                                                            }
                                                    );
                                                }
                                            });
                            mScheduleNotesDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.activity_housing_detail_schedule_note_dialog_cancel),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    new AlertDialog.Builder(HousingDetailActivity.this)
                                                            .setTitle(R.string.activity_housing_detail_close_schedule_note_confirm_dialog_title)
                                                            .setMessage(R.string.activity_housing_detail_close_schedule_note_confirm_dialog_message)
                                                            .setPositiveButton(R.string.activity_housing_detail_close_schedule_note_confirm_dialog_positive,
                                                                    new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            mScheduleNotesDialog.show();
                                                                        }
                                                                    })
                                                            .setNegativeButton(R.string.activity_housing_detail_close_schedule_note_confirm_dialog_negative, null)
                                                            .show();
                                                }
                                            });
                            mScheduleNotesDialog.setCanceledOnTouchOutside(false);
                            mScheduleNotesDialog.show();
                        }
                    };
                    mScheduleTimePickerDialog =
                            new TimePickerDialog(HousingDetailActivity.this, onTimeSetListener,
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE), true
                            );
                    mScheduleTimePickerDialog.setCanceledOnTouchOutside(false);
                    mScheduleTimePickerDialog.show();
                }
            };
            mScheduleDatePickerDialog =
                    new DatePickerDialog(HousingDetailActivity.this, onDateSetListener,
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH));
            mScheduleDatePickerDialog.setTitle(R.string.activity_housing_detail_set_schedule_dialog_title);
            mScheduleDatePickerDialog.setCanceledOnTouchOutside(false);
            mScheduleDatePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            mScheduleDatePickerDialog.show();
        }
    }

    private void handleHousingAppointmentJellyBean(int selectedOption) {
        final Calendar calendar = Calendar.getInstance();
        if (selectedOption == 0) {
            final DatePicker datePicker = new DatePicker(HousingDetailActivity.this);
            datePicker.init(
                    Integer.parseInt(new SimpleDateFormat("yyyy").format(mHousingAppointment.getAppointmentDateTime())),
                    Integer.parseInt(new SimpleDateFormat("MM").format(mHousingAppointment.getAppointmentDateTime())) - 1,
                    Integer.parseInt(new SimpleDateFormat("dd").format(mHousingAppointment.getAppointmentDateTime())),
                    null
            );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                datePicker.setCalendarViewShown(false);
            }
            datePicker.setMinDate(calendar.getTimeInMillis());

            mScheduleDatePickerDialogJellyBean = new AlertDialog.Builder(HousingDetailActivity.this).create();
            mScheduleDatePickerDialogJellyBean.setTitle(R.string.activity_housing_detail_update_schedule_dialog_title);
            mScheduleDatePickerDialogJellyBean.setView(datePicker);
            mScheduleDatePickerDialogJellyBean.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.activity_housing_detail_set_schedule_dialog_positive_jelly_bean),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            calendar.set(Calendar.YEAR, datePicker.getYear());
                            calendar.set(Calendar.MONTH, datePicker.getMonth());
                            calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());

                            final TimePicker timePicker = new TimePicker(HousingDetailActivity.this);
                            timePicker.setIs24HourView(true);
                            timePicker.setCurrentHour(Integer.parseInt(new SimpleDateFormat("HH").format(mHousingAppointment.getAppointmentDateTime())));
                            timePicker.setCurrentMinute(Integer.parseInt(new SimpleDateFormat("mm").format(mHousingAppointment.getAppointmentDateTime())));

                            mScheduleTimePickerDialogJellyBean = new AlertDialog.Builder(HousingDetailActivity.this).create();
                            mScheduleTimePickerDialogJellyBean.setTitle(R.string.activity_housing_detail_set_time_schedule_dialog_title_jelly_bean);
                            mScheduleTimePickerDialogJellyBean.setView(timePicker);
                            mScheduleTimePickerDialogJellyBean.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.activity_housing_detail_set_schedule_dialog_positive_jelly_bean),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                                            calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());

                                            if (!TextUtils.isEmpty(mHousingAppointment.getContent())) {
                                                mInputScheduleNotes.setText(mHousingAppointment.getContent());
                                            } else {
                                                mInputScheduleNotes.setText("");
                                            }
                                            FrameLayout container = new FrameLayout(HousingDetailActivity.this);
                                            FrameLayout.LayoutParams inputScheduleNotesLayoutParams = new FrameLayout.LayoutParams(
                                                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                            inputScheduleNotesLayoutParams.setMargins(
                                                    getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_user_note_dialog_edittext_left_right_margin),
                                                    0,
                                                    getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_user_note_dialog_edittext_left_right_margin),
                                                    0
                                            );
                                            mInputScheduleNotes.setLayoutParams(inputScheduleNotesLayoutParams);
                                            if (mInputScheduleNotes.getParent() != null) {
                                                ((ViewGroup) mInputScheduleNotes.getParent()).removeView(mInputScheduleNotes);
                                            }
                                            container.addView(mInputScheduleNotes);

                                            mScheduleNotesDialog = new AlertDialog.Builder(HousingDetailActivity.this).create();
                                            mScheduleNotesDialog.setTitle(R.string.activity_housing_detail_schedule_note_dialog_title);
                                            mScheduleNotesDialog.setMessage(getString(R.string.activity_housing_detail_schedule_note_dialog_message));
                                            mScheduleNotesDialog.setView(container);
                                            mScheduleNotesDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.activity_housing_detail_schedule_note_dialog_save),
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            String s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(calendar.getTime());
                                                            s = s.substring(0, s.length() - 2) + ":" + s.substring(s.length() - 2, s.length());
                                                            UserClient.updateHousingAppointment(
                                                                    mHousing.getID(), mHousing.getOwner().getUserID(),
                                                                    s, mInputScheduleNotes.getText().toString(),
                                                                    new IHousingAppointmentUpdatingCallback() {
                                                                        @Override
                                                                        public void onUpdateComplete(HousingAppointment housingAppointment) {
                                                                            if (housingAppointment != null) {
                                                                                mHousingAppointment = housingAppointment;
                                                                                ToastHelper.showCenterToast(
                                                                                        getApplicationContext(),
                                                                                        R.string.activity_housing_detail_set_schedule_successfully_message,
                                                                                        Toast.LENGTH_LONG
                                                                                );
                                                                                ShareSpaceApplication.BUS.post(new UpdateHousingAppointmentEvent(
                                                                                        housingAppointment
                                                                                ));
                                                                            } else {
                                                                                ToastHelper.showCenterToast(
                                                                                        getApplicationContext(),
                                                                                        R.string.activity_housing_detail_set_schedule_unsuccessfully_message,
                                                                                        Toast.LENGTH_LONG
                                                                                );
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onUpdateFailure(Throwable t) {
                                                                            ToastHelper.showCenterToast(
                                                                                    getApplicationContext(),
                                                                                    R.string.activity_housing_detail_set_schedule_unsuccessfully_message,
                                                                                    Toast.LENGTH_LONG
                                                                            );
                                                                            RetrofitClient.showShareSpaceServerConnectionErrorDialog(HousingDetailActivity.this, t);
                                                                        }
                                                                    }
                                                            );
                                                        }
                                                    });
                                            mScheduleNotesDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.activity_housing_detail_schedule_note_dialog_cancel),
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            new AlertDialog.Builder(HousingDetailActivity.this)
                                                                    .setTitle(R.string.activity_housing_detail_update_schedule_note_confirm_dialog_title)
                                                                    .setMessage(R.string.activity_housing_detail_update_schedule_note_confirm_dialog_message)
                                                                    .setPositiveButton(R.string.activity_housing_detail_update_schedule_note_confirm_dialog_positive,
                                                                            new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    mScheduleNotesDialog.show();
                                                                                }
                                                                            })
                                                                    .setNegativeButton(R.string.activity_housing_detail_update_schedule_note_confirm_dialog_negative, null)
                                                                    .show();
                                                        }
                                                    });
                                            mScheduleNotesDialog.setCanceledOnTouchOutside(false);
                                            mScheduleNotesDialog.show();
                                        }
                                    });
                            mScheduleTimePickerDialogJellyBean.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.activity_housing_detail_set_schedule_dialog_negative_jelly_bean),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            mScheduleTimePickerDialogJellyBean.setCanceledOnTouchOutside(false);
                            mScheduleTimePickerDialogJellyBean.show();
                        }
                    });
            mScheduleDatePickerDialogJellyBean.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.activity_housing_detail_set_schedule_dialog_negative_jelly_bean),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            mScheduleDatePickerDialogJellyBean.setCanceledOnTouchOutside(false);
            mScheduleDatePickerDialogJellyBean.show();
        } else if (selectedOption == 1) {
            new AlertDialog.Builder(HousingDetailActivity.this)
                    .setTitle(R.string.activity_housing_detail_delete_schedule_note_confirm_dialog_title)
                    .setMessage(R.string.activity_housing_detail_delete_schedule_note_confirm_dialog_message)
                    .setPositiveButton(R.string.activity_housing_detail_delete_schedule_note_confirm_dialog_positive,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    UserClient.deleteHousingAppointment(
                                            mHousing.getID(), mHousing.getOwner().getUserID(),
                                            new IHousingAppointmentDeletingCallback() {
                                                @Override
                                                public void onDeleteComplete(HousingAppointment housingAppointment) {
                                                    if (housingAppointment != null) {
                                                        mHousingAppointment = null;
                                                        ToastHelper.showCenterToast(
                                                                getApplicationContext(),
                                                                R.string.activity_housing_detail_delete_schedule_successfully_message,
                                                                Toast.LENGTH_LONG
                                                        );
                                                        ShareSpaceApplication.BUS.post(new DeleteHousingAppointmentEvent(
                                                                housingAppointment
                                                        ));
                                                    } else {
                                                        ToastHelper.showCenterToast(
                                                                getApplicationContext(),
                                                                R.string.activity_housing_detail_delete_schedule_unsuccessfully_message,
                                                                Toast.LENGTH_LONG
                                                        );
                                                    }
                                                }

                                                @Override
                                                public void onDeleteFailure(Throwable t) {
                                                    ToastHelper.showCenterToast(
                                                            getApplicationContext(),
                                                            R.string.activity_housing_detail_delete_schedule_unsuccessfully_message,
                                                            Toast.LENGTH_LONG
                                                    );
                                                    RetrofitClient.showShareSpaceServerConnectionErrorDialog(HousingDetailActivity.this, t);
                                                }
                                            }
                                    );
                                }
                            })
                    .setNegativeButton(R.string.activity_housing_detail_delete_schedule_note_confirm_dialog_negative, null)
                    .show();
        } else if (selectedOption == 2) {
            final DatePicker datePicker = new DatePicker(HousingDetailActivity.this);
            datePicker.init(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    null
            );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                datePicker.setCalendarViewShown(false);
            }
            datePicker.setMinDate(calendar.getTimeInMillis());

            mScheduleDatePickerDialogJellyBean = new AlertDialog.Builder(HousingDetailActivity.this).create();
            mScheduleDatePickerDialogJellyBean.setTitle(R.string.activity_housing_detail_set_schedule_dialog_title);
            mScheduleDatePickerDialogJellyBean.setView(datePicker);
            mScheduleDatePickerDialogJellyBean.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.activity_housing_detail_set_schedule_dialog_positive_jelly_bean),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            calendar.set(Calendar.YEAR, datePicker.getYear());
                            calendar.set(Calendar.MONTH, datePicker.getMonth());
                            calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());

                            final TimePicker timePicker = new TimePicker(HousingDetailActivity.this);
                            timePicker.setIs24HourView(true);
                            timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
                            timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));

                            mScheduleTimePickerDialogJellyBean = new AlertDialog.Builder(HousingDetailActivity.this).create();
                            mScheduleTimePickerDialogJellyBean.setTitle(R.string.activity_housing_detail_set_time_schedule_dialog_title_jelly_bean);
                            mScheduleTimePickerDialogJellyBean.setView(timePicker);
                            mScheduleTimePickerDialogJellyBean.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.activity_housing_detail_set_schedule_dialog_positive_jelly_bean),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                                            calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());

                                            mInputScheduleNotes.setText("");
                                            FrameLayout container = new FrameLayout(HousingDetailActivity.this);
                                            FrameLayout.LayoutParams inputScheduleNotesLayoutParams = new FrameLayout.LayoutParams(
                                                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                            inputScheduleNotesLayoutParams.setMargins(
                                                    getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_user_note_dialog_edittext_left_right_margin),
                                                    0,
                                                    getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_user_note_dialog_edittext_left_right_margin),
                                                    0
                                            );
                                            mInputScheduleNotes.setLayoutParams(inputScheduleNotesLayoutParams);
                                            if (mInputScheduleNotes.getParent() != null) {
                                                ((ViewGroup) mInputScheduleNotes.getParent()).removeView(mInputScheduleNotes);
                                            }
                                            container.addView(mInputScheduleNotes);

                                            mScheduleNotesDialog = new AlertDialog.Builder(HousingDetailActivity.this).create();
                                            mScheduleNotesDialog.setTitle(R.string.activity_housing_detail_schedule_note_dialog_title);
                                            mScheduleNotesDialog.setMessage(getString(R.string.activity_housing_detail_schedule_note_dialog_message));
                                            mScheduleNotesDialog.setView(container);
                                            mScheduleNotesDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.activity_housing_detail_schedule_note_dialog_save),
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            String s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(calendar.getTime());
                                                            s = s.substring(0, s.length() - 2) + ":" + s.substring(s.length() - 2, s.length());
                                                            UserClient.updateHousingAppointment(
                                                                    mHousing.getID(), mHousing.getOwner().getUserID(),
                                                                    s, mInputScheduleNotes.getText().toString(),
                                                                    new IHousingAppointmentUpdatingCallback() {
                                                                        @Override
                                                                        public void onUpdateComplete(HousingAppointment housingAppointment) {
                                                                            if (housingAppointment != null) {
                                                                                mHousingAppointment = housingAppointment;
                                                                                ToastHelper.showCenterToast(
                                                                                        getApplicationContext(),
                                                                                        R.string.activity_housing_detail_set_schedule_successfully_message,
                                                                                        Toast.LENGTH_LONG
                                                                                );
                                                                                ShareSpaceApplication.BUS.post(new UpdateHousingAppointmentEvent(
                                                                                        housingAppointment
                                                                                ));
                                                                            } else {
                                                                                ToastHelper.showCenterToast(
                                                                                        getApplicationContext(),
                                                                                        R.string.activity_housing_detail_set_schedule_unsuccessfully_message,
                                                                                        Toast.LENGTH_LONG
                                                                                );
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onUpdateFailure(Throwable t) {
                                                                            ToastHelper.showCenterToast(
                                                                                    getApplicationContext(),
                                                                                    R.string.activity_housing_detail_set_schedule_unsuccessfully_message,
                                                                                    Toast.LENGTH_LONG
                                                                            );
                                                                            RetrofitClient.showShareSpaceServerConnectionErrorDialog(HousingDetailActivity.this, t);
                                                                        }
                                                                    }
                                                            );
                                                        }
                                                    });
                                            mScheduleNotesDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.activity_housing_detail_schedule_note_dialog_cancel),
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            new AlertDialog.Builder(HousingDetailActivity.this)
                                                                    .setTitle(R.string.activity_housing_detail_update_schedule_note_confirm_dialog_title)
                                                                    .setMessage(R.string.activity_housing_detail_update_schedule_note_confirm_dialog_message)
                                                                    .setPositiveButton(R.string.activity_housing_detail_update_schedule_note_confirm_dialog_positive,
                                                                            new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    mScheduleNotesDialog.show();
                                                                                }
                                                                            })
                                                                    .setNegativeButton(R.string.activity_housing_detail_update_schedule_note_confirm_dialog_negative, null)
                                                                    .show();
                                                        }
                                                    });
                                            mScheduleNotesDialog.setCanceledOnTouchOutside(false);
                                            mScheduleNotesDialog.show();
                                        }
                                    });
                            mScheduleTimePickerDialogJellyBean.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.activity_housing_detail_set_schedule_dialog_negative_jelly_bean),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            mScheduleTimePickerDialogJellyBean.setCanceledOnTouchOutside(false);
                            mScheduleTimePickerDialogJellyBean.show();
                        }
                    });
            mScheduleDatePickerDialogJellyBean.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.activity_housing_detail_set_schedule_dialog_negative_jelly_bean),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            mScheduleDatePickerDialogJellyBean.setCanceledOnTouchOutside(false);
            mScheduleDatePickerDialogJellyBean.show();
        }
    }

    private void inflateUserNoteLayout() {
        ViewGroup parent = (ViewGroup) mUserNoteLayout.getParent();
        int index = parent.indexOfChild(mUserNoteLayout);
        parent.removeViewAt(index);

        View userNoteLayout = mLayoutInflater.inflate(R.layout.activity_housing_detail_user_note, parent, false);
        userNoteLayout.setId(R.id.activity_housing_detail_user_note_layout);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(
                getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_content_left_right_margin),
                0,
                getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_content_left_right_margin),
                0
        );
        userNoteLayout.setLayoutParams(layoutParams);

        parent.addView(userNoteLayout, index);
        mUserNoteLayout = (LinearLayout) findViewById(R.id.activity_housing_detail_user_note_layout);
        mUserNoteTextView = (TextView) mUserNoteLayout.findViewById(R.id.activity_housing_detail_user_note_text_view);
    }

    private void inflateUserNoteEmptyLayout() {
        ViewGroup parent = (ViewGroup) mUserNoteLayout.getParent();
        int index = parent.indexOfChild(mUserNoteLayout);
        parent.removeViewAt(index);

        View userNoteLayout = mLayoutInflater.inflate(R.layout.activity_housing_detail_user_note_empty, parent, false);
        userNoteLayout.setId(R.id.activity_housing_detail_user_note_layout);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(
                getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_content_left_right_margin),
                0,
                getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_content_left_right_margin),
                0
        );
        userNoteLayout.setLayoutParams(layoutParams);

        parent.addView(userNoteLayout, index);
        mUserNoteLayout = (LinearLayout) findViewById(R.id.activity_housing_detail_user_note_layout);
        mUserNoteTextView = null;
    }

    private void loadAllPhoto() {
        if (mLoadPhotoIndex < mUserPhotoURLs.size()) {
            Glide.with(this)
                    .load(mUserPhotoURLs.get(mLoadPhotoIndex))
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(new SimpleTarget<Bitmap>(mGridItemWidthPixels, mGridItemWidthPixels) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            Drawable drawable = new BitmapDrawable(HousingDetailActivity.this.getResources(), resource);
                            // Inflate User Photo.
                            inflateUserPhoto(drawable);

                            if (++mLoadPhotoIndex < mUserPhotoURLs.size()) {
                                loadAllPhoto();
                            }
                        }
                    });
        }
    }

    private void loadOnePhoto() {
        if (mLoadPhotoIndex < mUserPhotoURLs.size()) {
            Glide.with(this)
                    .load(mUserPhotoURLs.get(mLoadPhotoIndex))
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(new SimpleTarget<Bitmap>(mGridItemWidthPixels, mGridItemWidthPixels) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            Drawable drawable = new BitmapDrawable(HousingDetailActivity.this.getResources(), resource);
                            // Inflate Normal Photo.
                            inflateUserPhoto(drawable);

                            ++mLoadPhotoIndex;
                        }
                    });
        }
    }

    private void inflateUserPhoto(Drawable drawable) {
        RelativeLayout userPhotoGridItem =
                (RelativeLayout) mLayoutInflater
                        .inflate(R.layout.activity_post_house_add_normal_photo, null);
        userPhotoGridItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentSelectedGridItemIndex = (int) v.getTag();
                Intent intent = new Intent(HousingDetailActivity.this, ImageViewerActivity.class);
                intent.putStringArrayListExtra(Constants.ACTIVITY_IMAGE_VIEWER_PHOTO_URLS, mUserPhotoURLs);
                intent.putExtra(Constants.ACTIVITY_IMAGE_VIEWER_CURRENT_PHOTO_INDEX, mCurrentSelectedGridItemIndex);
                startActivity(intent);
            }
        });
        userPhotoGridItem.setBackground(drawable);
        userPhotoGridItem
                .findViewById(R.id.activity_post_house_add_normal_photo_plus_sign)
                .setVisibility(View.GONE);
        ImageView removeButton =
                (ImageView) userPhotoGridItem
                        .findViewById(R.id.activity_post_house_add_normal_photo_remove_button);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int itemIndex = (int) ((RelativeLayout) v.getParent()).getTag();
                final View photo = mPhotoGridContainer.getChildAt(itemIndex);
                mPhotoGridContainer.removeViewAt(itemIndex);
                ToastHelper.showCenterToast(
                        getApplicationContext(),
                        R.string.activity_housing_detail_syncing_photo_message,
                        Toast.LENGTH_LONG
                );
                HousingClient.deletePhotoURL(mHousing.getID(), itemIndex, new IPhotoDeletingCallback() {
                    @Override
                    public void onDeleteComplete(Boolean isSuccess) {
                        if (isSuccess) {
                            mUserPhotoURLs.remove(itemIndex);

                            for (int i = 0; i < mPhotoGridContainer.getChildCount(); ++i) {
                                mPhotoGridContainer.getChildAt(i).setTag(i);
                            }
                            mLoadPhotoIndex = mPhotoGridContainer.getChildCount();
                            ToastHelper.showCenterToast(
                                    getApplicationContext(),
                                    R.string.activity_housing_detail_photos_synced_message,
                                    Toast.LENGTH_SHORT
                            );
                            if (mUserPhotoURLs.isEmpty()) {
                                ShareSpaceApplication.BUS.post(new DeleteHousingPhotoEvent(
                                        new HistoryHousingPhoto(mHousing, new Date())
                                ));
                            }
                        } else {
                            mPhotoGridContainer.addView(photo, itemIndex);
                            ToastHelper.showCenterToast(
                                    getApplicationContext(),
                                    R.string.activity_housing_detail_photos_not_synced_message,
                                    Toast.LENGTH_LONG
                            );
                        }
                    }

                    @Override
                    public void onDeleteFailure(Throwable t) {
                        mPhotoGridContainer.addView(photo, itemIndex);
                        ToastHelper.showCenterToast(
                                getApplicationContext(),
                                R.string.activity_housing_detail_photos_not_synced_message,
                                Toast.LENGTH_LONG
                        );
                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(HousingDetailActivity.this, t);
                    }
                });
            }
        });
        removeButton.setVisibility(View.VISIBLE);

        GridLayout.LayoutParams photoGridItemLayoutParams = new GridLayout.LayoutParams();
        photoGridItemLayoutParams.setMargins(
                mGridItemMarginPixels,
                mGridItemMarginPixels,
                mGridItemMarginPixels,
                mGridItemMarginPixels
        );
        photoGridItemLayoutParams.width = mGridItemWidthPixels;
        photoGridItemLayoutParams.height = mGridItemWidthPixels;

        userPhotoGridItem.setLayoutParams(photoGridItemLayoutParams);

        // Set Tag as Current Index of it in Array List of Uris and PhotoGridContainer.
        userPhotoGridItem.setTag(mPhotoGridContainer.getChildCount());
        mPhotoGridContainer.addView(userPhotoGridItem);
    }
}
