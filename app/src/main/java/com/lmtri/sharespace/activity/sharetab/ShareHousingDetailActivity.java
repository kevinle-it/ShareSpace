package com.lmtri.sharespace.activity.sharetab;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
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
import com.lmtri.sharespace.activity.postsharehouse.PostShareHouseActivity;
import com.lmtri.sharespace.adapter.ImageViewerAdapter;
import com.lmtri.sharespace.api.model.HistoryShareHousingNote;
import com.lmtri.sharespace.api.model.HistoryShareHousingPhoto;
import com.lmtri.sharespace.api.model.HousingAppointment;
import com.lmtri.sharespace.api.model.Note;
import com.lmtri.sharespace.api.model.SavedShareHousing;
import com.lmtri.sharespace.api.model.ShareHousing;
import com.lmtri.sharespace.api.model.ShareHousingAppointment;
import com.lmtri.sharespace.api.service.RetrofitClient;
import com.lmtri.sharespace.api.service.housing.HousingClient;
import com.lmtri.sharespace.api.service.housing.note.INoteDeletingCallback;
import com.lmtri.sharespace.api.service.housing.note.INoteGettingCallback;
import com.lmtri.sharespace.api.service.housing.note.INotePostingCallback;
import com.lmtri.sharespace.api.service.housing.note.INoteUpdatingCallback;
import com.lmtri.sharespace.api.service.housing.photo.IPhotoDeletingCallback;
import com.lmtri.sharespace.api.service.housing.photo.IPhotoGettingCallback;
import com.lmtri.sharespace.api.service.sharehousing.ShareHousingClient;
import com.lmtri.sharespace.api.service.sharehousing.report.IReportShareHousingCallback;
import com.lmtri.sharespace.api.service.user.UserClient;
import com.lmtri.sharespace.api.service.user.appointment.housing.IHousingAppointmentDeletingCallback;
import com.lmtri.sharespace.api.service.user.appointment.housing.IHousingAppointmentGettingCallback;
import com.lmtri.sharespace.api.service.user.appointment.housing.IHousingAppointmentPostingCallback;
import com.lmtri.sharespace.api.service.user.appointment.housing.IHousingAppointmentUpdatingCallback;
import com.lmtri.sharespace.api.service.user.appointment.sharehousing.IShareHousingAppointmentDeletingCallback;
import com.lmtri.sharespace.api.service.user.appointment.sharehousing.IShareHousingAppointmentGettingCallback;
import com.lmtri.sharespace.api.service.user.appointment.sharehousing.IShareHousingAppointmentPostingCallback;
import com.lmtri.sharespace.api.service.user.appointment.sharehousing.IShareHousingAppointmentUpdatingCallback;
import com.lmtri.sharespace.api.service.user.post.sharehousing.availability.IGetHidingStateOfCurrentShareHousingCallback;
import com.lmtri.sharespace.api.service.user.post.sharehousing.availability.IHideShareHousingCallback;
import com.lmtri.sharespace.api.service.user.post.sharehousing.availability.IUnhideShareHousingCallback;
import com.lmtri.sharespace.api.service.user.save.sharehousing.IGetSavingStateOfCurrentShareHousingCallback;
import com.lmtri.sharespace.api.service.user.save.sharehousing.ISaveShareHousingCallback;
import com.lmtri.sharespace.api.service.user.save.sharehousing.IUnsaveShareHousingCallback;
import com.lmtri.sharespace.customview.CustomEditText;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.GlideCircleTransform;
import com.lmtri.sharespace.helper.HousePriceHelper;
import com.lmtri.sharespace.helper.PermissionHelper;
import com.lmtri.sharespace.helper.ToastHelper;
import com.lmtri.sharespace.helper.busevent.appointment.housing.DeleteHousingAppointmentEvent;
import com.lmtri.sharespace.helper.busevent.appointment.housing.SetNewHousingAppointmentEvent;
import com.lmtri.sharespace.helper.busevent.appointment.housing.UpdateHousingAppointmentEvent;
import com.lmtri.sharespace.helper.busevent.appointment.sharehousing.DeleteShareHousingAppointmentEvent;
import com.lmtri.sharespace.helper.busevent.appointment.sharehousing.SetNewShareHousingAppointmentEvent;
import com.lmtri.sharespace.helper.busevent.appointment.sharehousing.UpdateShareHousingAppointmentEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.post.DeleteShareHousingEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.note.DeleteShareHousingNoteEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.photo.DeleteShareHousingPhotoEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.availability.HideShareHousingEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.report.ReportShareHousingEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.save.SaveShareHousingEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.note.TakeShareHousingNoteEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.photo.TakeShareHousingPhotoEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.availability.UnhideShareHousingEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.save.UnsaveShareHousingEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.note.UpdateShareHousingNoteEvent;
import com.lmtri.sharespace.helper.firebasestorage.FirebaseStorageHelper;
import com.lmtri.sharespace.helper.firebasestorage.OnPhotoPostingListener;
import com.lmtri.sharespace.helper.firebasestorage.sharehousing.OnShareHousingDeletingListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import me.relex.circleindicator.CircleIndicator;

public class ShareHousingDetailActivity extends AppCompatActivity {

    public static final String TAG = ShareHousingDetailActivity.class.getSimpleName();

    private AppBarLayout mAppBarLayout;
    private ViewPager mImageViewer;
    private CircleIndicator mImageViewerIndicator;

    private Toolbar mToolbar;

    private ImageView mHeartButton;
    private boolean mIsWhiteHeart = true;
    private Drawable mDarkerGrayHeartDrawable;
    private Drawable mWrappedDarkerGrayHeartMutableDrawable;

    private ImageView mReportButton;

    private ImageView mShowHideShareHousingButton;
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

    private ShareHousing mShareHousing;
    private TextView mHouseTitle;
    private TextView mPricePerMonthOfOne;
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

    private TextView mRequiredNumOfPeople;
    private TextView mRequiredGender;
    private TextView mRequiredWorkType;
    private ImageView mAllowSmokingCheckmark;
    private ImageView mAllowAlcoholCheckmark;
    private ImageView mPrivateKeyAvailableCheckmark;
    private ImageView mSharerProfileImagePlaceholder;
    private ImageView mSharerProfileImage;
    private TextView mSharerName;
    private TextView mSharerEmail;

    private View mBookingDivider;
    private ViewStub mBookingLayoutViewStub;

    private RelativeLayout mCallButton;

    private RelativeLayout mScheduleButton;
    private HousingAppointment mHousingAppointment;
    private ShareHousingAppointment mShareHousingAppointment;
    private AlertDialog.Builder mScheduleDialogOptionsHousingOwnerShareHousingCreator;
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
    private RelativeLayout mDeleteShareHousingButton;

    private boolean mIsUserLayoutInflated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_housing_detail);

        mUserNoteLayout = (LinearLayout) findViewById(R.id.activity_housing_detail_user_note_layout);

        mUserPhotoLayout = (LinearLayout) findViewById(R.id.activity_housing_detail_user_photo_layout);
        mPermissionHelper = new PermissionHelper(ShareHousingDetailActivity.this);

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
            mShareHousing = gson.fromJson(
                    getIntent().getStringExtra(Constants.ACTIVITY_SHARE_HOUSING_DETAIL_SHARE_HOUSING_EXTRA),
                    ShareHousing.class
            );
            if (mShareHousing != null) {
                initAppBarLayout();
                if (Constants.CURRENT_USER != null) {
                    // Current User is the Owner of this Share Post.
//                    if (Constants.CURRENT_USER.getUserID() == mShareHousing.getHousing().getOwner().getUserID()
//                            || Constants.CURRENT_USER.getUserID() == mShareHousing.getCreator().getUserID()) {
                    if (Constants.CURRENT_USER.getUserID() == mShareHousing.getCreator().getUserID()) {
                        initOwnerAppBarAndBookingLayout();
                    } else if (Constants.CURRENT_USER.getUserID() == mShareHousing.getHousing().getOwner().getUserID()) {
                        mHeartButton.setVisibility(View.GONE);
                        mReportButton.setVisibility(View.GONE);
                        mShowHideShareHousingButton.setVisibility(View.GONE);

                        mUserNoteLayout.setVisibility(View.GONE);
                        mUserPhotoLayout.setVisibility(View.GONE);

                        mBookingLayoutViewStub = (ViewStub) findViewById(R.id.activity_share_housing_detail_booking_layout_view_stub);
                        mBookingLayoutViewStub.setVisibility(View.GONE);

                        mBookingDivider = findViewById(R.id.activity_share_housing_detail_booking_divider);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_horizontal_line_height)
                        );
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        mBookingDivider.setLayoutParams(layoutParams);
                    } else {
                        initUserAppBarAndPhotosAndBookingLayout();
                    }
                } else {
                    mIsUserLayoutInflated = true;
                    initUserAppBarAndPhotosAndBookingLayout();
                }

                mImageViewer = (ViewPager) findViewById(R.id.activity_share_housing_detail_house_profile_image_viewer);
                mImageViewerIndicator = (CircleIndicator) findViewById(R.id.activity_share_housing_detail_house_profile_image_indicator);

                if (mShareHousing.getHousing().getPhotoURLs().size() > 0) {
                    mImageViewer.setAdapter(new ImageViewerAdapter(mShareHousing.getHousing().getPhotoURLs()));
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
                mPricePerMonthOfOne = (TextView) findViewById(R.id.activity_housing_detail_price);
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

                mRequiredNumOfPeople = (TextView) findViewById(R.id.activity_share_housing_detail_required_num_people_text);
                mRequiredGender = (TextView) findViewById(R.id.activity_share_housing_detail_required_gender_text);
                mRequiredWorkType = (TextView) findViewById(R.id.activity_share_housing_detail_required_work_type_text);
                mAllowSmokingCheckmark = (ImageView) findViewById(R.id.activity_share_housing_detail_allow_smoking);
                mAllowAlcoholCheckmark = (ImageView) findViewById(R.id.activity_share_housing_detail_allow_alcohol);
                mPrivateKeyAvailableCheckmark = (ImageView) findViewById(R.id.activity_share_housing_detail_private_key_available);
                mSharerProfileImagePlaceholder = (ImageView) findViewById(R.id.activity_share_housing_detail_sharer_profile_image_placeholder);
                mSharerProfileImage = (ImageView) findViewById(R.id.activity_share_housing_detail_sharer_profile_image);
                mSharerName = (TextView) findViewById(R.id.activity_share_housing_detail_sharer);
                mSharerEmail = (TextView) findViewById(R.id.activity_share_housing_detail_sharer_email);

                if (!TextUtils.isEmpty(mShareHousing.getHousing().getTitle())) {
                    mHouseTitle.setText(mShareHousing.getHousing().getTitle());
                } else {
                    mHouseTitle.setText(getString(R.string.activity_housing_detail_house_title_empty));
                }
                Pair<String, String> pair = HousePriceHelper.parseForShareHousing(mShareHousing.getPricePerMonthOfOne(), this);
                if (pair.first == null) {
                    mPricePerMonthOfOne.setText(pair.second);
                } else {
                    mPricePerMonthOfOne.setText(pair.first + " " + pair.second);
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    mArea.setText(Html.fromHtml(String.format(Locale.US, "%.2f", mShareHousing.getHousing().getArea()) + getString(R.string.activity_housing_detail_area_square_meter)));
                } else {
                    mArea.setText(Html.fromHtml(String.format(Locale.US, "%.2f", mShareHousing.getHousing().getArea()) + getString(R.string.activity_housing_detail_area_square_meter), Html.FROM_HTML_MODE_LEGACY));
                }
                if (mShareHousing.getNumOfSaved() >= 0) {
                    mNumSaved.setText(String.valueOf(mShareHousing.getNumOfSaved()) + getString(R.string.activity_housing_detail_saved));
                } else {
                    mNumSaved.setText(0 + getString(R.string.activity_housing_detail_saved));
                }

                if (mShareHousing.getHousing().getNumOfPeople() > -1) {
                    mNumPeople.setText(String.valueOf(mShareHousing.getHousing().getNumOfPeople()) + getString(R.string.activity_housing_detail_people));
                } else {    // NumOfPeople == -1
                    mNumPeople.setText(getString(R.string.activity_housing_detail_not_available_field));
                }
                if (mShareHousing.getHousing().getNumOfRoom() > -1) {
                    mNumRoom.setText(String.valueOf(mShareHousing.getHousing().getNumOfRoom()) + getString(R.string.activity_housing_detail_room));
                } else {    // NumOfRoom == -1
                    mNumRoom.setText(getString(R.string.activity_housing_detail_not_available_field));
                }
                if (mShareHousing.getHousing().getNumOfBed() > -1) {
                    mNumBed.setText(String.valueOf(mShareHousing.getHousing().getNumOfBed()) + getString(R.string.activity_housing_detail_bed));
                } else {    // NumOfBed == -1
                    mNumBed.setText(getString(R.string.activity_housing_detail_not_available_field));
                }
                if (mShareHousing.getHousing().getNumOfBath() > -1) {
                    mNumBath.setText(String.valueOf(mShareHousing.getHousing().getNumOfBath()) + getString(R.string.activity_housing_detail_bath));
                } else {    // NumOfBath == -1
                    mNumBath.setText(getString(R.string.activity_housing_detail_not_available_field));
                }

                if (!TextUtils.isEmpty(mShareHousing.getHousing().getDescription())) {
                    mDescription.setText(mShareHousing.getHousing().getDescription());
                }

                if (!TextUtils.isEmpty(mShareHousing.getHousing().getAddressHouseNumber())) {
                    mHouseNumber.setText(mShareHousing.getHousing().getAddressHouseNumber());
                } else {
                    mHouseNumber.setText(getString(R.string.activity_housing_detail_address_empty));
                }
                if (!TextUtils.isEmpty(mShareHousing.getHousing().getAddressStreet())) {
                    mStreet.setText(mShareHousing.getHousing().getAddressStreet());
                } else {
                    mStreet.setText(getString(R.string.activity_housing_detail_address_empty));
                }
                if (!TextUtils.isEmpty(mShareHousing.getHousing().getAddressWard())) {
                    mWard.setText(mShareHousing.getHousing().getAddressWard());
                } else {
                    mWard.setText(getString(R.string.activity_housing_detail_address_empty));
                }
                if (!TextUtils.isEmpty(mShareHousing.getHousing().getAddressDistrict())) {
                    mDistrict.setText(mShareHousing.getHousing().getAddressDistrict());
                } else {
                    mDistrict.setText(getString(R.string.activity_housing_detail_address_empty));
                }
                if (!TextUtils.isEmpty(mShareHousing.getHousing().getAddressCity())) {
                    mCity.setText(mShareHousing.getHousing().getAddressCity());
                } else {
                    mCity.setText(getString(R.string.activity_housing_detail_address_empty));
                }

                if (!TextUtils.isEmpty(mShareHousing.getHousing().getTimeRestriction())) {
                    mTimeRestriction.setText(mShareHousing.getHousing().getTimeRestriction());
                } else {
                    mTimeRestriction.setText(getString(R.string.activity_housing_detail_not_available_field));
                }
                if (mShareHousing.getHousing().isAllowPet()) {
                    mPetAllow.setImageResource(R.drawable.ic_check_mark_thicker);
                } else {
                    mPetAllow.setImageResource(R.drawable.ic_cross);
                }
                if (mShareHousing.getHousing().hasWifi()) {
                    mWifiAvailable.setImageResource(R.drawable.ic_check_mark_thicker);
                } else {
                    mWifiAvailable.setImageResource(R.drawable.ic_cross);
                }
                if (mShareHousing.getHousing().hasAC()) {
                    mACAvailable.setImageResource(R.drawable.ic_check_mark_thicker);
                } else {
                    mACAvailable.setImageResource(R.drawable.ic_cross);
                }
                if (mShareHousing.getHousing().hasParking()) {
                    mParkingAvailable.setImageResource(R.drawable.ic_check_mark_thicker);
                } else {
                    mParkingAvailable.setImageResource(R.drawable.ic_cross);
                }

                if (!TextUtils.isEmpty(mShareHousing.getHousing().getHouseType())) {
                    mHouseType.setText(mShareHousing.getHousing().getHouseType());
                } else {
                    mHouseType.setText(getString(R.string.activity_housing_detail_house_type_empty));
                }
                if (mShareHousing.getHousing().getOwner() != null) {
                    if (!TextUtils.isEmpty(mShareHousing.getHousing().getOwner().getLastName())) {
                        mOwnerName.setText(
                                getString(R.string.activity_housing_detail_house_owner)
                                        + mShareHousing.getHousing().getOwner().getLastName() + " "
                                        + mShareHousing.getHousing().getOwner().getFirstName()
                        );
                    } else {
                        mOwnerName.setText(
                                getString(R.string.activity_housing_detail_house_owner)
                                        + mShareHousing.getHousing().getOwner().getFirstName()
                        );
                    }
                    if (!TextUtils.isEmpty(mShareHousing.getHousing().getOwner().getEmail())) {
                        mOwnerEmail.setText(mShareHousing.getHousing().getOwner().getEmail());
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

                if (mShareHousing.getRequiredNumOfPeople() > 0) {
                    mRequiredNumOfPeople.setText(String.valueOf(mShareHousing.getRequiredNumOfPeople()) + getString(R.string.activity_housing_detail_people));
                } else {    // RequiredNumOfPeople == 0 || == -1
                    mRequiredNumOfPeople.setText(getString(R.string.activity_housing_detail_not_available_field));
                }
                if (!TextUtils.isEmpty(mShareHousing.getRequiredGender())) {
                    mRequiredGender.setText(mShareHousing.getRequiredGender());
                } else {
                    mRequiredGender.setText(R.string.hint_signup_gender_female);
                }
                if (!TextUtils.isEmpty(mShareHousing.getRequiredWorkType())) {
                    mRequiredWorkType.setText(mShareHousing.getRequiredWorkType());
                } else {
                    mRequiredWorkType.setText(R.string.share_housing_recycler_view_adapter_work_type_student);
                }
                if (mShareHousing.isAllowSmoking()) {
                    mAllowSmokingCheckmark.setImageResource(R.drawable.ic_check_mark_thicker);
                } else {
                    mAllowSmokingCheckmark.setImageResource(R.drawable.ic_cross);
                }
                if (mShareHousing.isAllowAlcohol()) {
                    mAllowAlcoholCheckmark.setImageResource(R.drawable.ic_check_mark_thicker);
                } else {
                    mAllowAlcoholCheckmark.setImageResource(R.drawable.ic_cross);
                }
                if (mShareHousing.hasPrivateKey()) {
                    mPrivateKeyAvailableCheckmark.setImageResource(R.drawable.ic_check_mark_thicker);
                } else {
                    mPrivateKeyAvailableCheckmark.setImageResource(R.drawable.ic_cross);
                }if (mShareHousing.getCreator() != null) {
                    if (!TextUtils.isEmpty(mShareHousing.getCreator().getLastName())) {
                        mSharerName.setText(
                                getString(R.string.activity_share_housing_detail_share_creator)
                                        + mShareHousing.getCreator().getLastName() + " "
                                        + mShareHousing.getCreator().getFirstName()
                        );
                    } else {
                        mSharerName.setText(
                                getString(R.string.activity_share_housing_detail_share_creator)
                                        + mShareHousing.getCreator().getFirstName()
                        );
                    }
                    if (!TextUtils.isEmpty(mShareHousing.getCreator().getEmail())) {
                        mSharerEmail.setText(mShareHousing.getCreator().getEmail());
                    }
                }
                Glide.with(this)
                        .load(R.drawable.ic_profile_picture)
                        .crossFade()
                        .transform(new GlideCircleTransform(this))
                        .into(mSharerProfileImagePlaceholder);

//                Glide.with(this)
//                        .load(R.drawable.profile_image_2)
//                        .crossFade()
//                        .transform(new GlideCircleTransform(this))
//                        .into(mSharerProfileImage);
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
                .getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_heart_darker_gray_thicker_stroke);
        mWrappedDarkerGrayHeartMutableDrawable = DrawableCompat.wrap(mDarkerGrayHeartDrawable).mutate();

        final Drawable.ConstantState showHousingConstantStateWhite,
                hideHousingConstantStateWhite,
                showHousingConstantStateDarkerGray,
                hideHousingConstantStateDarkerGray;
        showHousingConstantStateWhite = ContextCompat
                .getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_show_white)
                .getConstantState();
        hideHousingConstantStateWhite = ContextCompat
                .getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_hide_white)
                .getConstantState();
        showHousingConstantStateDarkerGray = ContextCompat
                .getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_show_darker_gray)
                .getConstantState();
        hideHousingConstantStateDarkerGray = ContextCompat
                .getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_hide_darker_gray)
                .getConstantState();
        mDarkerGrayShowHousingDrawable = ContextCompat
                .getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_show_darker_gray);
        mDarkerGrayHideHousingDrawable = ContextCompat
                .getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_hide_darker_gray);
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
                        .getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_heart_solid_red).getConstantState()) {
                    DrawableCompat.setTint(mWrappedDarkerGrayHeartMutableDrawable, Color.argb(
                            Color.alpha(whiteColor),
                            newValue,
                            newValue,
                            newValue
                    ));
                }
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
                                .getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_heart_solid_red).getConstantState()) {
                            mHeartButton.setImageDrawable(ContextCompat
                                    .getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_heart_darker_gray_thicker_stroke));
                        }
                        mIsWhiteHeart = false;

                        mReportButton.setColorFilter(darkerGrayColor);
                        if (mIsShowButton) {
                            mShowHideShareHousingButton.setImageDrawable(ContextCompat
                                    .getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_show_darker_gray));
                        } else {
                            mShowHideShareHousingButton.setImageDrawable(ContextCompat
                                    .getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_hide_darker_gray));
                        }
                    }
                    else if (!mIsToolbarTransparent && mScrollViewScrollY == 0) {
                        mAnimatorSet = new AnimatorSet();
                        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                if (mHeartButton.getDrawable().getConstantState() != ContextCompat
                                        .getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_heart_solid_red).getConstantState()) {
                                    mHeartButton.setImageDrawable(mWrappedDarkerGrayHeartMutableDrawable);
                                    DrawableCompat.setTint(mWrappedDarkerGrayHeartMutableDrawable, darkerGrayColor);
                                }
                                if (mIsShowButton) {
                                    mShowHideShareHousingButton.setImageDrawable(mWrappedDarkerGrayShowHousingMutableDrawable);
                                    DrawableCompat.setTint(mWrappedDarkerGrayShowHousingMutableDrawable, darkerGrayColor);
                                } else {
                                    mShowHideShareHousingButton.setImageDrawable(mWrappedDarkerGrayHideHousingMutableDrawable);
                                    DrawableCompat.setTint(mWrappedDarkerGrayHideHousingMutableDrawable, darkerGrayColor);
                                }
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if (mHeartButton.getDrawable().getConstantState() != ContextCompat
                                        .getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_heart_solid_red).getConstantState()) {
                                    mHeartButton.setImageDrawable(ContextCompat
                                            .getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_heart_white_thicker_stroke));
                                }
                                mIsWhiteHeart = true;

                                if (mIsShowButton) {
                                    mShowHideShareHousingButton.setImageDrawable(ContextCompat
                                            .getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_show_white));
                                } else {
                                    mShowHideShareHousingButton.setImageDrawable(ContextCompat
                                            .getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_hide_white));
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
        mHeartButton.setClickable(false);   // Still respond if set at runtime.
        mHeartButton.setEnabled(false);     // Does NOT respond to any response.
        if (Constants.CURRENT_USER != null) {
            UserClient.getSavingStateOfCurrentShareHousing(
                    mShareHousing.getID(),
                    new IGetSavingStateOfCurrentShareHousingCallback() {
                        @Override
                        public void onGetComplete(Boolean isSaved) {
                            if (isSaved) {
                                mHeartButton.setImageDrawable(ContextCompat
                                        .getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_heart_solid_red));
                            } else {
                                mHeartButton.setImageDrawable(ContextCompat
                                        .getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_heart_white_thicker_stroke));
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
                .getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_heart_white_thicker_stroke));
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
                                    .getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_heart_solid_red)
                                    .getConstantState()) {
                                UserClient.saveShareHousing(
                                        mShareHousing.getID(),
                                        new ISaveShareHousingCallback() {
                                            @Override
                                            public void onSaveComplete(SavedShareHousing savedShareHousing) {
                                                if (savedShareHousing != null) {
                                                    ToastHelper.showCenterToast(
                                                            getApplicationContext(),
                                                            R.string.activity_share_housing_detail_save_share_housing_successfully_message,
                                                            Toast.LENGTH_SHORT
                                                    );
                                                    ShareSpaceApplication.BUS.post(new SaveShareHousingEvent(savedShareHousing));
                                                } else {
                                                    ToastHelper.showCenterToast(
                                                            getApplicationContext(),
                                                            R.string.activity_share_housing_detail_save_share_housing_unsuccessfully_message,
                                                            Toast.LENGTH_LONG
                                                    );
                                                    animatorSet.start();
                                                }
                                            }

                                            @Override
                                            public void onSaveFailure(Throwable t) {
                                                ToastHelper.showCenterToast(
                                                        getApplicationContext(),
                                                        R.string.activity_share_housing_detail_save_share_housing_unsuccessfully_message,
                                                        Toast.LENGTH_LONG
                                                );
                                                animatorSet.start();
                                                RetrofitClient.showShareSpaceServerConnectionErrorDialog(ShareHousingDetailActivity.this, t);
                                            }
                                        }
                                );
                            } else {
                                UserClient.unsaveShareHousing(
                                        mShareHousing.getID(),
                                        new IUnsaveShareHousingCallback() {
                                            @Override
                                            public void onUnsaveComplete(SavedShareHousing savedShareHousing) {
                                                if (savedShareHousing != null) {
                                                    ToastHelper.showCenterToast(
                                                            getApplicationContext(),
                                                            R.string.activity_share_housing_detail_unsave_share_housing_successfully_message,
                                                            Toast.LENGTH_SHORT
                                                    );
                                                    ShareSpaceApplication.BUS.post(new UnsaveShareHousingEvent(savedShareHousing));
                                                } else {
                                                    ToastHelper.showCenterToast(
                                                            getApplicationContext(),
                                                            R.string.activity_share_housing_detail_unsave_share_housing_unsuccessfully_message,
                                                            Toast.LENGTH_LONG
                                                    );
                                                    animatorSet.start();
                                                }
                                            }

                                            @Override
                                            public void onUnsaveFailure(Throwable t) {
                                                ToastHelper.showCenterToast(
                                                        getApplicationContext(),
                                                        R.string.activity_share_housing_detail_unsave_share_housing_unsuccessfully_message,
                                                        Toast.LENGTH_LONG
                                                );
                                                animatorSet.start();
                                                RetrofitClient.showShareSpaceServerConnectionErrorDialog(ShareHousingDetailActivity.this, t);
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
                                        .getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_heart_white_thicker_stroke)
                                        .getConstantState();
                                constantStateDarkerGray = ContextCompat
                                        .getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_heart_darker_gray_thicker_stroke)
                                        .getConstantState();
                                if (mHeartButton.getDrawable().getConstantState() == constantStateWhite
                                        || mHeartButton.getDrawable().getConstantState() == constantStateDarkerGray) {
                                    mHeartButton.setImageDrawable(ContextCompat
                                            .getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_heart_solid_red));
                                } else {
                                    if (mIsWhiteHeart) {
                                        mHeartButton.setImageDrawable(ContextCompat
                                                .getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_heart_white_thicker_stroke));
                                    } else {
                                        mHeartButton.setImageDrawable(ContextCompat
                                                .getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_heart_darker_gray_thicker_stroke));
                                    }
                                }
                            }
                        }
                    });

                    animatorSet.play(bounceAnimX).with(bounceAnimY);
                    animatorSet.start();
                } else {
                    new AlertDialog.Builder(ShareHousingDetailActivity.this)
                            .setTitle(R.string.activity_housing_detail_login_required_feature_dialog_title)
                            .setMessage(R.string.activity_housing_detail_login_required_feature_dialog_message)
                            .setPositiveButton(R.string.activity_housing_detail_login_required_feature_dialog_positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(ShareHousingDetailActivity.this, LoginActivity.class);
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
                        ShareHousingDetailActivity.this,
                        getString(R.string.activity_share_housing_detail_save_post_hint),
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

        mReportButton = (ImageView) findViewById(R.id.activity_share_housing_detail_report_button);
        mReportButton.setColorFilter(whiteColor);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.CURRENT_USER != null) {
                    final CustomEditText editText = new CustomEditText(ShareHousingDetailActivity.this);
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                    editText.setFilters(
                            new InputFilter[] {
                                    new InputFilter.LengthFilter(
                                            Constants.ACTIVITY_HOUSING_DETAIL_NOTE_NUM_CHARACTERS_LIMIT
                                    )
                            }
                    );
                    FrameLayout container = new FrameLayout(ShareHousingDetailActivity.this);
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

                    new AlertDialog.Builder(ShareHousingDetailActivity.this)
                            .setTitle(R.string.activity_housing_detail_write_report_dialog_title)
                            .setMessage(R.string.activity_housing_detail_write_note_report_dialog_message)
                            .setView(container)
                            .setPositiveButton(R.string.activity_housing_detail_write_report_dialog_send,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ShareHousingClient.reportShareHousing(
                                                    mShareHousing.getID(),
                                                    new IReportShareHousingCallback() {
                                                        @Override
                                                        public void onReportComplete(Boolean isSuccess) {
                                                            if (isSuccess) {
                                                                ToastHelper.showCenterToast(
                                                                        getApplicationContext(),
                                                                        R.string.activity_housing_detail_report_successfully_message,
                                                                        Toast.LENGTH_LONG
                                                                );
                                                                ShareSpaceApplication.BUS.post(new ReportShareHousingEvent());
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
                                                            RetrofitClient.showShareSpaceServerConnectionErrorDialog(ShareHousingDetailActivity.this, t);
                                                        }
                                                    }
                                            );
                                        }
                                    })
                            .setNegativeButton(R.string.activity_housing_detail_write_report_dialog_cancel, null)
                            .show();
                } else {
                    new AlertDialog.Builder(ShareHousingDetailActivity.this)
                            .setTitle(R.string.activity_housing_detail_login_required_feature_dialog_title)
                            .setMessage(R.string.activity_housing_detail_login_required_feature_dialog_message)
                            .setPositiveButton(R.string.activity_housing_detail_login_required_feature_dialog_positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(ShareHousingDetailActivity.this, LoginActivity.class);
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
                        ShareHousingDetailActivity.this,
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

        mShowHideShareHousingButton = (ImageView) findViewById(R.id.activity_share_housing_detail_show_hide_share_housing_button);
        mShowHideShareHousingButton.setClickable(false);
        mShowHideShareHousingButton.setEnabled(false);
        if (Constants.CURRENT_USER != null
                 && Constants.CURRENT_USER.getUserID() == mShareHousing.getCreator().getUserID()) {
            UserClient.getHidingStateOfCurrentShareHousing(
                    mShareHousing.getID(),
                    new IGetHidingStateOfCurrentShareHousingCallback() {
                        @Override
                        public void onGetComplete(Boolean isHidden) {
                            if (isHidden) {
                                if (mShowHideShareHousingButton.getDrawable().getConstantState()
                                        == hideHousingConstantStateDarkerGray) {
                                    mShowHideShareHousingButton.setImageDrawable(
                                            ContextCompat.getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_show_darker_gray)
                                    );
                                } else {
                                    mShowHideShareHousingButton.setImageDrawable(
                                            ContextCompat.getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_show_white)
                                    );
                                }
                                mIsShowButton = true;
                            } else {
                                if (mShowHideShareHousingButton.getDrawable().getConstantState()
                                        == showHousingConstantStateDarkerGray) {
                                    mShowHideShareHousingButton.setImageDrawable(
                                            ContextCompat.getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_hide_darker_gray)
                                    );
                                } else {
                                    mShowHideShareHousingButton.setImageDrawable(
                                            ContextCompat.getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_hide_white)
                                    );
                                }
                                mIsShowButton = false;
                            }
                            mShowHideShareHousingButton.setClickable(true);
                            mShowHideShareHousingButton.setEnabled(true);
                        }

                        @Override
                        public void onGetFailure(Throwable t) {

                        }
                    }
            );
        }
        mShowHideShareHousingButton.setImageDrawable(
                ContextCompat.getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_show_white)
        );
        mShowHideShareHousingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShowHideShareHousingButton.setClickable(false);
                mShowHideShareHousingButton.setEnabled(false);
                if (mIsShowButton) {
                    UserClient.unhideShareHousing(
                            mShareHousing.getID(),
                            new IUnhideShareHousingCallback() {
                                @Override
                                public void onUnhideComplete(Boolean isSuccess) {
                                    if (isSuccess) {
                                        ToastHelper.showCenterToast(
                                                getApplicationContext(),
                                                R.string.activity_share_housing_detail_show_share_housing_successfully_message,
                                                Toast.LENGTH_SHORT
                                        );
                                        mShareHousing.setAvailability(true);
                                        if (mShowHideShareHousingButton.getDrawable().getConstantState()
                                                == showHousingConstantStateDarkerGray) {
                                            mShowHideShareHousingButton.setImageDrawable(
                                                    ContextCompat.getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_hide_darker_gray)
                                            );
                                        } else {
                                            mShowHideShareHousingButton.setImageDrawable(
                                                    ContextCompat.getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_hide_white)
                                            );
                                        }
                                        mIsShowButton = false;
                                        ShareSpaceApplication.BUS.post(new UnhideShareHousingEvent(mShareHousing));
                                    } else {
                                        ToastHelper.showCenterToast(
                                                getApplicationContext(),
                                                R.string.activity_share_housing_detail_show_share_housing_unsuccessfully_message,
                                                Toast.LENGTH_LONG
                                        );
                                        if (mShowHideShareHousingButton.getDrawable().getConstantState()
                                                == hideHousingConstantStateDarkerGray) {
                                            mShowHideShareHousingButton.setImageDrawable(
                                                    ContextCompat.getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_show_darker_gray)
                                            );
                                        } else {
                                            mShowHideShareHousingButton.setImageDrawable(
                                                    ContextCompat.getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_show_white)
                                            );
                                        }
                                        mIsShowButton = true;
                                    }
                                    mShowHideShareHousingButton.setClickable(true);
                                    mShowHideShareHousingButton.setEnabled(true);
                                }

                                @Override
                                public void onUnhideFailure(Throwable t) {
                                    ToastHelper.showCenterToast(
                                            getApplicationContext(),
                                            R.string.activity_share_housing_detail_show_share_housing_unsuccessfully_message,
                                            Toast.LENGTH_LONG
                                    );
                                    if (mShowHideShareHousingButton.getDrawable().getConstantState()
                                            == hideHousingConstantStateDarkerGray) {
                                        mShowHideShareHousingButton.setImageDrawable(
                                                ContextCompat.getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_show_darker_gray)
                                        );
                                    } else {
                                        mShowHideShareHousingButton.setImageDrawable(
                                                ContextCompat.getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_show_white)
                                        );
                                    }
                                    mIsShowButton = true;
                                    mShowHideShareHousingButton.setClickable(true);
                                    mShowHideShareHousingButton.setEnabled(true);
                                    RetrofitClient.showShareSpaceServerConnectionErrorDialog(ShareHousingDetailActivity.this, t);
                                }
                            }
                    );
                } else {
                    UserClient.hideShareHousing(
                            mShareHousing.getID(),
                            new IHideShareHousingCallback() {
                                @Override
                                public void onHideComplete(Boolean isSuccess) {
                                    if (isSuccess) {
                                        ToastHelper.showCenterToast(
                                                getApplicationContext(),
                                                R.string.activity_share_housing_detail_hide_share_housing_successfully_message,
                                                Toast.LENGTH_SHORT
                                        );
                                        mShareHousing.setAvailability(false);
                                        if (mShowHideShareHousingButton.getDrawable().getConstantState()
                                                == hideHousingConstantStateDarkerGray) {
                                            mShowHideShareHousingButton.setImageDrawable(
                                                    ContextCompat.getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_show_darker_gray)
                                            );
                                        } else {
                                            mShowHideShareHousingButton.setImageDrawable(
                                                    ContextCompat.getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_show_white)
                                            );
                                        }
                                        mIsShowButton = true;
                                        ShareSpaceApplication.BUS.post(new HideShareHousingEvent(mShareHousing));
                                    } else {
                                        ToastHelper.showCenterToast(
                                                getApplicationContext(),
                                                R.string.activity_share_housing_detail_hide_share_housing_unsuccessfully_message,
                                                Toast.LENGTH_LONG
                                        );
                                        if (mShowHideShareHousingButton.getDrawable().getConstantState()
                                                == showHousingConstantStateDarkerGray) {
                                            mShowHideShareHousingButton.setImageDrawable(
                                                    ContextCompat.getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_hide_darker_gray)
                                            );
                                        } else {
                                            mShowHideShareHousingButton.setImageDrawable(
                                                    ContextCompat.getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_hide_white)
                                            );
                                        }
                                        mIsShowButton = false;
                                    }
                                    mShowHideShareHousingButton.setClickable(true);
                                    mShowHideShareHousingButton.setEnabled(true);
                                }

                                @Override
                                public void onHideFailure(Throwable t) {
                                    ToastHelper.showCenterToast(
                                            getApplicationContext(),
                                            R.string.activity_share_housing_detail_hide_share_housing_unsuccessfully_message,
                                            Toast.LENGTH_LONG
                                    );
                                    if (mShowHideShareHousingButton.getDrawable().getConstantState()
                                            == showHousingConstantStateDarkerGray) {
                                        mShowHideShareHousingButton.setImageDrawable(
                                                ContextCompat.getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_hide_darker_gray)
                                        );
                                    } else {
                                        mShowHideShareHousingButton.setImageDrawable(
                                                ContextCompat.getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_hide_white)
                                        );
                                    }
                                    mIsShowButton = false;
                                    mShowHideShareHousingButton.setClickable(true);
                                    mShowHideShareHousingButton.setEnabled(true);
                                    RetrofitClient.showShareSpaceServerConnectionErrorDialog(ShareHousingDetailActivity.this, t);
                                }
                            }
                    );
                }
            }
        });
        mShowHideShareHousingButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast toast = Toast.makeText(
                        ShareHousingDetailActivity.this,
                        getString(R.string.activity_share_housing_detail_show_hide_share_housing_hint),
                        Toast.LENGTH_SHORT
                );
                toast.setGravity(
                        Gravity.TOP|Gravity.END,
                        mToolbar.getWidth() - (mToolbar.getContentInsetStartWithNavigation() + mShowHideShareHousingButton.getLeft()),
                        mShowHideShareHousingButton.getBottom()
                );
                toast.show();
                return true;
            }
        });
    }

    private void initUserAppBarAndPhotosAndBookingLayout() {
        mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        mShowHideShareHousingButton.setVisibility(View.GONE);

        if (Constants.CURRENT_USER != null) {
            UserClient.getCurrentHousingAppointment(mShareHousing.getHousing().getID(),
                    new IHousingAppointmentGettingCallback() {
                        @Override
                        public void onGetComplete(HousingAppointment housingAppointment) {
                            mHousingAppointment = housingAppointment;
                        }

                        @Override
                        public void onGetFailure(Throwable t) {

                        }
                    });
            UserClient.getCurrentShareHousingAppointment(mShareHousing.getID(),
                    new IShareHousingAppointmentGettingCallback() {
                        @Override
                        public void onGetComplete(ShareHousingAppointment shareHousingAppointment) {
                            mShareHousingAppointment = shareHousingAppointment;
                        }

                        @Override
                        public void onGetFailure(Throwable t) {

                        }
            });
            HousingClient.getCurrentUserNote(mShareHousing.getHousing().getID(), new INoteGettingCallback() {
                @Override
                public void onGetComplete(Note note) {
                    if (note != null && !TextUtils.isEmpty(note.getContent())) {
                        inflateUserNoteLayout();
                        mUserNoteTextView.setText(note.getContent());
                    } else {
                        mUserNoteTextView = null;
                    }
                }

                @Override
                public void onGetFailure(Throwable t) {
                    RetrofitClient.showShareSpaceServerConnectionErrorDialog(ShareHousingDetailActivity.this, t);
                }
            });
            HousingClient.getCurrentUserPhotoURLs(mShareHousing.getHousing().getID(), new IPhotoGettingCallback() {
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
                }

                @Override
                public void onGetFailure(Throwable t) {
                    RetrofitClient.showShareSpaceServerConnectionErrorDialog(ShareHousingDetailActivity.this, t);
                }
            });
        }

        mBookingLayoutViewStub = (ViewStub) findViewById(R.id.activity_share_housing_detail_booking_layout_view_stub);
        mBookingLayoutViewStub.setLayoutResource(R.layout.activity_housing_detail_user_booking_layout);
        mBookingLayoutViewStub.inflate();

        mBookingDivider = findViewById(R.id.activity_share_housing_detail_booking_divider);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_horizontal_line_height)
        );
        layoutParams.addRule(RelativeLayout.ABOVE, R.id.activity_share_housing_detail_booking_layout);
        mBookingDivider.setLayoutParams(layoutParams);

        mCallButton = (RelativeLayout) findViewById(R.id.activity_housing_detail_call_button);
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.CURRENT_USER != null) {
                    CharSequence options[] = new CharSequence[] {
                            getString(R.string.activity_share_housing_detail_call_dialog_options_call_housing_owner),
                            getString(R.string.activity_share_housing_detail_call_dialog_options_call_share_housing_creator)
                    };
                    new AlertDialog.Builder(ShareHousingDetailActivity.this)
                            .setTitle(R.string.activity_share_housing_detail_call_dialog_options_title)
                            .setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent;
                                    if (which == 0) {
                                        intent = new Intent(
                                                Intent.ACTION_DIAL,
                                                Uri.fromParts("tel", mShareHousing.getHousing().getOwner().getPhoneNumber(), null)
                                        );
                                    } else {
                                        intent = new Intent(
                                                Intent.ACTION_DIAL,
                                                Uri.fromParts("tel", mShareHousing.getCreator().getPhoneNumber(), null)
                                        );
                                    }
                                    startActivity(intent);
                                }
                            })
                            .show();
                } else {
                    new AlertDialog.Builder(ShareHousingDetailActivity.this)
                            .setTitle(R.string.activity_housing_detail_login_required_feature_dialog_title)
                            .setMessage(R.string.activity_housing_detail_login_required_feature_dialog_message)
                            .setPositiveButton(R.string.activity_housing_detail_login_required_feature_dialog_positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(ShareHousingDetailActivity.this, LoginActivity.class);
                                    startActivityForResult(intent, Constants.START_ACTIVITY_LOGIN_REQUEST);
                                }
                            })
                            .setNegativeButton(R.string.activity_housing_detail_login_required_feature_dialog_negative, null)
                            .show();
                }
            }
        });
        mScheduleButton = (RelativeLayout) findViewById(R.id.activity_housing_detail_schedule_button);
        mInputScheduleNotes = new CustomEditText(ShareHousingDetailActivity.this);
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
                    CharSequence housingOwnerShareHousingCreatorOptions[] = new CharSequence[] {
                            getString(R.string.activity_share_housing_detail_schedule_dialog_options_housing_owner),
                            getString(R.string.activity_share_housing_detail_schedule_dialog_options_share_housing_creator)
                    };
                    final CharSequence editDeleteOptions[] = new CharSequence[] {
                            getString(R.string.activity_housing_detail_update_schedule_dialog_option_menu),
                            getString(R.string.activity_housing_detail_delete_schedule_dialog_option_menu)
                    };
                    mScheduleDialogOptionsHousingOwnerShareHousingCreator = new AlertDialog.Builder(ShareHousingDetailActivity.this);
                    if (mHousingAppointment != null && mShareHousingAppointment != null) {
                        if (!mHousingAppointment.isOwnerConfirmed() && !mShareHousingAppointment.isCreatorConfirmed()) {
                            mScheduleDialogOptionsHousingOwnerShareHousingCreator.setTitle(
                                    getString(R.string.activity_share_housing_detail_schedule_dialog_options_title) + "\n" +
                                    getString(R.string.activity_share_housing_detail_schedule_dialog_options_message_wait_both_housing_owner_and_share_housing_creator_confirm)
                            );
                        } else if (!mHousingAppointment.isOwnerConfirmed()) {
                            mScheduleDialogOptionsHousingOwnerShareHousingCreator.setTitle(
                                    getString(R.string.activity_share_housing_detail_schedule_dialog_options_title) + "\n" +
                                    getString(R.string.activity_housing_detail_schedule_dialog_options_message)
                            );
                        } else if (!mShareHousingAppointment.isCreatorConfirmed()) {
                            mScheduleDialogOptionsHousingOwnerShareHousingCreator.setTitle(
                                    getString(R.string.activity_share_housing_detail_schedule_dialog_options_title) + "\n" +
                                    getString(R.string.activity_share_housing_detail_schedule_dialog_options_message)
                            );
                        } else {
                            mScheduleDialogOptionsHousingOwnerShareHousingCreator.setTitle(
                                    getString(R.string.activity_share_housing_detail_schedule_dialog_options_title)
                            );
                        }
                    } else if (mHousingAppointment != null) {
                        if (!mHousingAppointment.isOwnerConfirmed()) {
                            mScheduleDialogOptionsHousingOwnerShareHousingCreator.setTitle(
                                    getString(R.string.activity_share_housing_detail_schedule_dialog_options_title) + "\n" +
                                    getString(R.string.activity_housing_detail_schedule_dialog_options_message)
                            );
                        } else {
                            mScheduleDialogOptionsHousingOwnerShareHousingCreator.setTitle(
                                    getString(R.string.activity_share_housing_detail_schedule_dialog_options_title)
                            );
                        }
                    } else if (mShareHousingAppointment != null) {
                        if (!mShareHousingAppointment.isCreatorConfirmed()) {
                            mScheduleDialogOptionsHousingOwnerShareHousingCreator.setTitle(
                                    getString(R.string.activity_share_housing_detail_schedule_dialog_options_title) + "\n" +
                                    getString(R.string.activity_share_housing_detail_schedule_dialog_options_message)
                            );
                        } else {
                            mScheduleDialogOptionsHousingOwnerShareHousingCreator.setTitle(
                                    getString(R.string.activity_share_housing_detail_schedule_dialog_options_title)
                            );
                        }
                    } else {
                        mScheduleDialogOptionsHousingOwnerShareHousingCreator.setTitle(
                                getString(R.string.activity_share_housing_detail_schedule_dialog_options_title)
                        );
                    }
                    mScheduleDialogOptionsHousingOwnerShareHousingCreator.setItems(housingOwnerShareHousingCreatorOptions,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            mScheduleDialogOptionsEditDeleteAppointment = new AlertDialog.Builder(ShareHousingDetailActivity.this);
                                            if (which == 0) {
                                                if (mHousingAppointment != null) {
                                                    mScheduleDialogOptionsEditDeleteAppointment.setTitle(R.string.activity_housing_detail_schedule_dialog_options_title)
                                                            .setItems(editDeleteOptions,
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
                                                                    }).show();
                                                } else {
                                                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                                        handleHousingAppointment(2);
                                                    } else {
                                                        handleHousingAppointmentJellyBean(2);
                                                    }
                                                }
                                            } else {
                                                if (mShareHousingAppointment != null) {
                                                    mScheduleDialogOptionsEditDeleteAppointment.setTitle(R.string.activity_housing_detail_schedule_dialog_options_title)
                                                            .setItems(editDeleteOptions,
                                                                    new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            dialog.dismiss();
                                                                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                                                                handleShareHousingAppointment(which);
                                                                            } else {
                                                                                handleShareHousingAppointment(which);
                                                                            }
                                                                        }
                                                                    }).show();
                                                } else {
                                                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                                        handleShareHousingAppointment(2);
                                                    } else {
                                                        handleShareHousingAppointmentJellyBean(2);
                                                    }
                                                }
                                            }
                                        }
                                    });
                    mScheduleDialogOptionsHousingOwnerShareHousingCreator.show();
                } else {
                    new AlertDialog.Builder(ShareHousingDetailActivity.this)
                            .setTitle(R.string.activity_housing_detail_login_required_feature_dialog_title)
                            .setMessage(R.string.activity_housing_detail_login_required_feature_dialog_message)
                            .setPositiveButton(R.string.activity_housing_detail_login_required_feature_dialog_positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(ShareHousingDetailActivity.this, LoginActivity.class);
                                    startActivityForResult(intent, Constants.START_ACTIVITY_LOGIN_REQUEST);
                                }
                            })
                            .setNegativeButton(R.string.activity_housing_detail_login_required_feature_dialog_negative, null)
                            .show();
                }
            }
        });
        mTakeNoteButton = (RelativeLayout) findViewById(R.id.activity_housing_detail_take_note_button);
        mInputNotes = new CustomEditText(ShareHousingDetailActivity.this);
        mInputNotes.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        mInputNotes.setFilters(
                new InputFilter[] {
                        new InputFilter.LengthFilter(
                                Constants.ACTIVITY_HOUSING_DETAIL_NOTE_NUM_CHARACTERS_LIMIT
                        )
                }
        );
        if (mUserNoteTextView != null) {
            mInputNotes.setText(mUserNoteTextView.getText().toString());
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
                    FrameLayout container = new FrameLayout(ShareHousingDetailActivity.this);
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

                    final AlertDialog notesDialog = new AlertDialog.Builder(ShareHousingDetailActivity.this).create();
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
                                            mShareHousing.getHousing().getID(),
                                            mShareHousing.getHousing().getTitle(),
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
                                                                ShareSpaceApplication.BUS.post(new UpdateShareHousingNoteEvent(
                                                                        new HistoryShareHousingNote(mShareHousing, note.getDateTimeCreated())
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
                                                            RetrofitClient.showShareSpaceServerConnectionErrorDialog(ShareHousingDetailActivity.this, t);
                                                        }
                                                    }
                                            );
                                        } else {
                                            HousingClient.postNote(
                                                    mUserNote, mShareHousing.getID(),
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
                                                                ShareSpaceApplication.BUS.post(new TakeShareHousingNoteEvent(
                                                                        new HistoryShareHousingNote(mShareHousing, note.getDateTimeCreated())
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
                                                            RetrofitClient.showShareSpaceServerConnectionErrorDialog(ShareHousingDetailActivity.this, t);
                                                        }
                                                    }
                                            );
                                        }
                                    } else {
                                        new AlertDialog.Builder(ShareHousingDetailActivity.this)
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
                                                                        mShareHousing.getHousing().getID(),
                                                                        new INoteDeletingCallback() {
                                                                            @Override
                                                                            public void onDeleteComplete(Boolean isDeleted) {
                                                                                if (isDeleted) {
                                                                                    ToastHelper.showCenterToast(
                                                                                            getApplicationContext(),
                                                                                            R.string.activity_housing_detail_note_deleted_message,
                                                                                            Toast.LENGTH_SHORT
                                                                                    );
                                                                                    ShareSpaceApplication.BUS.post(new DeleteShareHousingNoteEvent(
                                                                                            new HistoryShareHousingNote(mShareHousing, mUserNote.getDateTimeCreated())
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
                                                                                RetrofitClient.showShareSpaceServerConnectionErrorDialog(ShareHousingDetailActivity.this, t);
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
                                        new AlertDialog.Builder(ShareHousingDetailActivity.this)
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
                    new AlertDialog.Builder(ShareHousingDetailActivity.this)
                            .setTitle(R.string.activity_housing_detail_login_required_feature_dialog_title)
                            .setMessage(R.string.activity_housing_detail_login_required_feature_dialog_message)
                            .setPositiveButton(R.string.activity_housing_detail_login_required_feature_dialog_positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(ShareHousingDetailActivity.this, LoginActivity.class);
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
                    new AlertDialog.Builder(ShareHousingDetailActivity.this)
                            .setTitle(R.string.activity_housing_detail_login_required_feature_dialog_title)
                            .setMessage(R.string.activity_housing_detail_login_required_feature_dialog_message)
                            .setPositiveButton(R.string.activity_housing_detail_login_required_feature_dialog_positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(ShareHousingDetailActivity.this, LoginActivity.class);
                                    startActivityForResult(intent, Constants.START_ACTIVITY_LOGIN_REQUEST);
                                }
                            })
                            .setNegativeButton(R.string.activity_housing_detail_login_required_feature_dialog_negative, null)
                            .show();
                }
            }
        });
    }

    private void initOwnerAppBarAndBookingLayout() {
        mShowHideShareHousingButton.setVisibility(View.VISIBLE);

        mHeartButton.setVisibility(View.GONE);
        mReportButton.setVisibility(View.GONE);

        mUserNoteLayout.setVisibility(View.GONE);
        mUserPhotoLayout.setVisibility(View.GONE);

        if (!mIsUserLayoutInflated) {
            if (mShareHousing.isAvailable()) {
                mShowHideShareHousingButton.setImageDrawable(
                        ContextCompat.getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_hide_white)
                );
                mIsShowButton = false;
            } else {
                mShowHideShareHousingButton.setImageDrawable(
                        ContextCompat.getDrawable(ShareHousingDetailActivity.this, R.drawable.ic_show_white)
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
            layoutParams.addRule(RelativeLayout.ABOVE, R.id.activity_share_housing_detail_booking_layout);
            mBookingDivider.setLayoutParams(layoutParams);
        } else {
            View bookingLayout = findViewById(R.id.activity_share_housing_detail_booking_layout);
            ViewGroup parent = (ViewGroup) bookingLayout.getParent();
            int index = parent.indexOfChild(bookingLayout);
            parent.removeViewAt(index);
            bookingLayout = mLayoutInflater.inflate(R.layout.activity_housing_detail_owner_booking_layout, parent, false);
            bookingLayout.setId(R.id.activity_share_housing_detail_booking_layout);

            RelativeLayout.LayoutParams bookingLayoutParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            );
            bookingLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            bookingLayout.setLayoutParams(bookingLayoutParams);

            RelativeLayout.LayoutParams bookingDividerLayoutParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_horizontal_line_height)
            );
            bookingDividerLayoutParams.addRule(RelativeLayout.ABOVE, R.id.activity_share_housing_detail_booking_layout);
            mBookingDivider.setLayoutParams(bookingDividerLayoutParams);

            parent.addView(bookingLayout, index);
        }
        mEditPostButton = (RelativeLayout) findViewById(R.id.activity_housing_detail_edit_post_button);
        mEditPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        ShareHousingDetailActivity.this,
                        PostShareHouseActivity.class
                );
                intent.putExtra(Constants.IS_EDIT_SHARE_HOUSING_EXTRA, true);
                Gson gson = new Gson();
                intent.putExtra(
                        Constants.SHARE_HOUSING_INFO_FOR_EDITING_POST_EXTRA,
                        gson.toJson(mShareHousing)
                );
                startActivityForResult(intent, Constants.START_ACTIVITY_POST_SHARE_HOUSE_REQUEST);
            }
        });
        mDeleteShareHousingButton = (RelativeLayout) findViewById(R.id.activity_housing_detail_delete_housing_button);
        mDeleteShareHousingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ShareHousingDetailActivity.this)
                        .setTitle(R.string.activity_share_housing_detail_delete_share_housing_dialog_title)
                        .setMessage(R.string.activity_share_housing_detail_delete_share_housing_dialog_message)
                        .setPositiveButton(R.string.activity_share_housing_detail_delete_share_housing_dialog_positive,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FirebaseStorageHelper.deleteShareHousing(
                                                ShareHousingDetailActivity.this,
                                                mShareHousing.getID(),
                                                new OnShareHousingDeletingListener() {
                                                    @Override
                                                    public void onDeleteComplete(Boolean isDeleted) {
                                                        ToastHelper.showCenterToast(
                                                                getApplicationContext(),
                                                                R.string.activity_share_housing_detail_delete_share_housing_successfully_toast_message,
                                                                Toast.LENGTH_SHORT
                                                        );
                                                        ShareSpaceApplication.BUS.post(new DeleteShareHousingEvent(mShareHousing));
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onDeleteFailure(Throwable t) {
                                                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(ShareHousingDetailActivity.this, t);
                                                    }
                                                }
                                        );
                                    }
                                })
                        .setNegativeButton(R.string.activity_share_housing_detail_delete_share_housing_dialog_negative, null)
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
                        this, mShareHousing.getHousing().getID(), imageUri, mShareHousing.getID(),
                        mShareHousing.getHousing().getTitle(), new OnPhotoPostingListener() {
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

                                    ShareSpaceApplication.BUS.post(new TakeShareHousingPhotoEvent(
                                            new HistoryShareHousingPhoto(mShareHousing, new Date())
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
                                RetrofitClient.showShareSpaceServerConnectionErrorDialog(ShareHousingDetailActivity.this, t);
                            }
                        }
                );
            }
        }
        if (requestCode == Constants.START_ACTIVITY_LOGIN_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (Constants.CURRENT_USER != null) {
                    // Current User is the Owner of this Share Post.
                    if (Constants.CURRENT_USER.getUserID() == mShareHousing.getCreator().getUserID()) {
                        initOwnerAppBarAndBookingLayout();
                    } else if (Constants.CURRENT_USER.getUserID() == mShareHousing.getHousing().getOwner().getUserID()) {
                        mHeartButton.setVisibility(View.GONE);
                        mReportButton.setVisibility(View.GONE);
                        mShowHideShareHousingButton.setVisibility(View.GONE);

                        mUserNoteLayout.setVisibility(View.GONE);
                        mUserPhotoLayout.setVisibility(View.GONE);

                        View bookingLayout = findViewById(R.id.activity_share_housing_detail_booking_layout);
                        bookingLayout.setVisibility(View.GONE);

                        mBookingDivider = findViewById(R.id.activity_share_housing_detail_booking_divider);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_horizontal_line_height)
                        );
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        mBookingDivider.setLayoutParams(layoutParams);
                    }
//                    if ((
//                            mShareHousing.getHousing().getOwner().getUserID() == Constants.CURRENT_USER.getUserID()
//                                    && mShareHousing.getCreator().getUserID() == Constants.CURRENT_USER.getUserID()
//                    )
//                            || mShareHousing.getCreator().getUserID() == Constants.CURRENT_USER.getUserID()) {
//                        initOwnerAppBarAndBookingLayout();
//                    }
                }
            }
        } else if (requestCode == Constants.START_ACTIVITY_POST_SHARE_HOUSE_REQUEST) {
            if (resultCode == RESULT_OK) {
                String shareHousingResult = data.getStringExtra(Constants.ACTIVITY_SHARE_HOUSING_DETAIL_SHARE_HOUSING_EXTRA);
                Intent intent = new Intent(this, ShareHousingDetailActivity.class);
                intent.putExtra(Constants.ACTIVITY_SHARE_HOUSING_DETAIL_SHARE_HOUSING_EXTRA, shareHousingResult);
                finish();
                startActivity(intent);
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
                            FrameLayout container = new FrameLayout(ShareHousingDetailActivity.this);
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

                            mScheduleNotesDialog = new AlertDialog.Builder(ShareHousingDetailActivity.this).create();
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
                                                            mShareHousing.getHousing().getID(), mShareHousing.getHousing().getOwner().getUserID(),
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
                                                                    RetrofitClient.showShareSpaceServerConnectionErrorDialog(ShareHousingDetailActivity.this, t);
                                                                }
                                                            }
                                                    );
                                                }
                                            });
                            mScheduleNotesDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.activity_housing_detail_schedule_note_dialog_cancel),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    new AlertDialog.Builder(ShareHousingDetailActivity.this)
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
                            new TimePickerDialog(ShareHousingDetailActivity.this, onTimeSetListener,
                                    Integer.parseInt(new SimpleDateFormat("HH").format(mHousingAppointment.getAppointmentDateTime())),
                                    Integer.parseInt(new SimpleDateFormat("mm").format(mHousingAppointment.getAppointmentDateTime())),
                                    true
                            );
                    mScheduleTimePickerDialog.setCanceledOnTouchOutside(false);
                    mScheduleTimePickerDialog.show();
                }
            };
            mScheduleDatePickerDialog =
                    new DatePickerDialog(ShareHousingDetailActivity.this, onDateSetListener,
                            Integer.parseInt(new SimpleDateFormat("yyyy").format(mHousingAppointment.getAppointmentDateTime())),
                            Integer.parseInt(new SimpleDateFormat("MM").format(mHousingAppointment.getAppointmentDateTime())) - 1,
                            Integer.parseInt(new SimpleDateFormat("dd").format(mHousingAppointment.getAppointmentDateTime())));
            mScheduleDatePickerDialog.setTitle(R.string.activity_housing_detail_update_schedule_dialog_title);
            mScheduleDatePickerDialog.setCanceledOnTouchOutside(false);
            mScheduleDatePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            mScheduleDatePickerDialog.show();
        } else if (selectedOption == 1) {
            new AlertDialog.Builder(ShareHousingDetailActivity.this)
                    .setTitle(R.string.activity_housing_detail_delete_schedule_note_confirm_dialog_title)
                    .setMessage(R.string.activity_housing_detail_delete_schedule_note_confirm_dialog_message)
                    .setPositiveButton(R.string.activity_housing_detail_delete_schedule_note_confirm_dialog_positive,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    UserClient.deleteHousingAppointment(
                                            mShareHousing.getHousing().getID(), mShareHousing.getHousing().getOwner().getUserID(),
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
                                                    RetrofitClient.showShareSpaceServerConnectionErrorDialog(ShareHousingDetailActivity.this, t);
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
                            FrameLayout container = new FrameLayout(ShareHousingDetailActivity.this);
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

                            mScheduleNotesDialog = new AlertDialog.Builder(ShareHousingDetailActivity.this).create();
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
                                                            mShareHousing.getHousing().getID(), mShareHousing.getHousing().getOwner().getUserID(),
                                                            s, mInputScheduleNotes.getText().toString(),
                                                            new IHousingAppointmentPostingCallback() {
                                                                @Override
                                                                public void onPostComplete(HousingAppointment housingAppointment) {
                                                                    if (housingAppointment != null) {
                                                                        mHousingAppointment = housingAppointment;
                                                                        ToastHelper.showCenterToast(
                                                                                getApplicationContext(),
                                                                                R.string.activity_housing_detail_set_schedule_successfully_message,
                                                                                Toast.LENGTH_LONG
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
                                                                    RetrofitClient.showShareSpaceServerConnectionErrorDialog(ShareHousingDetailActivity.this, t);
                                                                }
                                                            }
                                                    );
                                                }
                                            });
                            mScheduleNotesDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.activity_housing_detail_schedule_note_dialog_cancel),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    new AlertDialog.Builder(ShareHousingDetailActivity.this)
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
                            new TimePickerDialog(ShareHousingDetailActivity.this, onTimeSetListener,
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE), true
                            );
                    mScheduleTimePickerDialog.setCanceledOnTouchOutside(false);
                    mScheduleTimePickerDialog.show();
                }
            };
            mScheduleDatePickerDialog =
                    new DatePickerDialog(ShareHousingDetailActivity.this, onDateSetListener,
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH));
            mScheduleDatePickerDialog.setTitle(R.string.activity_housing_detail_set_schedule_dialog_title);
            mScheduleDatePickerDialog.setCanceledOnTouchOutside(false);
            mScheduleDatePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            mScheduleDatePickerDialog.show();
        }
    }

    private void handleShareHousingAppointment(int selectedOption) {
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

                            if (!TextUtils.isEmpty(mShareHousingAppointment.getContent())) {
                                mInputScheduleNotes.setText(mShareHousingAppointment.getContent());
                            } else {
                                mInputScheduleNotes.setText("");
                            }
                            FrameLayout container = new FrameLayout(ShareHousingDetailActivity.this);
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

                            mScheduleNotesDialog = new AlertDialog.Builder(ShareHousingDetailActivity.this).create();
                            mScheduleNotesDialog.setTitle(R.string.activity_housing_detail_schedule_note_dialog_title);
                            mScheduleNotesDialog.setMessage(getString(R.string.activity_housing_detail_schedule_note_dialog_message));
                            mScheduleNotesDialog.setView(container);
                            mScheduleNotesDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.activity_housing_detail_schedule_note_dialog_save),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    String s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(calendar.getTime());
                                                    s = s.substring(0, s.length() - 2) + ":" + s.substring(s.length() - 2, s.length());
                                                    UserClient.updateShareHousingAppointment(
                                                            mShareHousing.getID(), mShareHousing.getCreator().getUserID(),
                                                            s, mInputScheduleNotes.getText().toString(),
                                                            new IShareHousingAppointmentUpdatingCallback() {
                                                                @Override
                                                                public void onUpdateComplete(ShareHousingAppointment shareHousingAppointment) {
                                                                    if (shareHousingAppointment != null) {
                                                                        mShareHousingAppointment = shareHousingAppointment;
                                                                        ToastHelper.showCenterToast(
                                                                                getApplicationContext(),
                                                                                R.string.activity_share_housing_detail_set_schedule_successfully_message,
                                                                                Toast.LENGTH_LONG
                                                                        );
                                                                        ShareSpaceApplication.BUS.post(new UpdateShareHousingAppointmentEvent(
                                                                                shareHousingAppointment
                                                                        ));
                                                                    } else {
                                                                        ToastHelper.showCenterToast(
                                                                                getApplicationContext(),
                                                                                R.string.activity_share_housing_detail_set_schedule_unsuccessfully_message,
                                                                                Toast.LENGTH_LONG
                                                                        );
                                                                    }
                                                                }

                                                                @Override
                                                                public void onUpdateFailure(Throwable t) {
                                                                    ToastHelper.showCenterToast(
                                                                            getApplicationContext(),
                                                                            R.string.activity_share_housing_detail_set_schedule_unsuccessfully_message,
                                                                            Toast.LENGTH_LONG
                                                                    );
                                                                    RetrofitClient.showShareSpaceServerConnectionErrorDialog(ShareHousingDetailActivity.this, t);
                                                                }
                                                            }
                                                    );
                                                }
                                            });
                            mScheduleNotesDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.activity_housing_detail_schedule_note_dialog_cancel),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    new AlertDialog.Builder(ShareHousingDetailActivity.this)
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
                            new TimePickerDialog(ShareHousingDetailActivity.this, onTimeSetListener,
                                    Integer.parseInt(new SimpleDateFormat("HH").format(mShareHousingAppointment.getAppointmentDateTime())),
                                    Integer.parseInt(new SimpleDateFormat("mm").format(mShareHousingAppointment.getAppointmentDateTime())),
                                    true
                            );
                    mScheduleTimePickerDialog.setCanceledOnTouchOutside(false);
                    mScheduleTimePickerDialog.show();
                }
            };
            mScheduleDatePickerDialog =
                    new DatePickerDialog(ShareHousingDetailActivity.this, onDateSetListener,
                            Integer.parseInt(new SimpleDateFormat("yyyy").format(mShareHousingAppointment.getAppointmentDateTime())),
                            Integer.parseInt(new SimpleDateFormat("MM").format(mShareHousingAppointment.getAppointmentDateTime())) - 1,
                            Integer.parseInt(new SimpleDateFormat("dd").format(mShareHousingAppointment.getAppointmentDateTime())));
            mScheduleDatePickerDialog.setTitle(R.string.activity_share_housing_detail_update_schedule_dialog_title);
            mScheduleDatePickerDialog.setCanceledOnTouchOutside(false);
            mScheduleDatePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            mScheduleDatePickerDialog.show();
        } else if (selectedOption == 1) {
            new AlertDialog.Builder(ShareHousingDetailActivity.this)
                    .setTitle(R.string.activity_share_housing_detail_delete_schedule_note_confirm_dialog_title)
                    .setMessage(R.string.activity_housing_detail_delete_schedule_note_confirm_dialog_message)
                    .setPositiveButton(R.string.activity_housing_detail_delete_schedule_note_confirm_dialog_positive,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    UserClient.deleteShareHousingAppointment(
                                            mShareHousing.getID(), mShareHousing.getCreator().getUserID(),
                                            new IShareHousingAppointmentDeletingCallback() {
                                                @Override
                                                public void onDeleteComplete(ShareHousingAppointment shareHousingAppointment) {
                                                    if (shareHousingAppointment != null) {
                                                        mShareHousingAppointment = null;
                                                        ToastHelper.showCenterToast(
                                                                getApplicationContext(),
                                                                R.string.activity_share_housing_detail_delete_schedule_successfully_message,
                                                                Toast.LENGTH_LONG
                                                        );
                                                        ShareSpaceApplication.BUS.post(new DeleteShareHousingAppointmentEvent(
                                                                shareHousingAppointment
                                                        ));
                                                    } else {
                                                        ToastHelper.showCenterToast(
                                                                getApplicationContext(),
                                                                R.string.activity_share_housing_detail_delete_schedule_unsuccessfully_message,
                                                                Toast.LENGTH_LONG
                                                        );
                                                    }
                                                }

                                                @Override
                                                public void onDeleteFailure(Throwable t) {
                                                    ToastHelper.showCenterToast(
                                                            getApplicationContext(),
                                                            R.string.activity_share_housing_detail_delete_schedule_unsuccessfully_message,
                                                            Toast.LENGTH_LONG
                                                    );
                                                    RetrofitClient.showShareSpaceServerConnectionErrorDialog(ShareHousingDetailActivity.this, t);
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
                            FrameLayout container = new FrameLayout(ShareHousingDetailActivity.this);
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

                            mScheduleNotesDialog = new AlertDialog.Builder(ShareHousingDetailActivity.this).create();
                            mScheduleNotesDialog.setTitle(R.string.activity_housing_detail_schedule_note_dialog_title);
                            mScheduleNotesDialog.setMessage(getString(R.string.activity_housing_detail_schedule_note_dialog_message));
                            mScheduleNotesDialog.setView(container);
                            mScheduleNotesDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.activity_housing_detail_schedule_note_dialog_save),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    String s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(calendar.getTime());
                                                    s = s.substring(0, s.length() - 2) + ":" + s.substring(s.length() - 2, s.length());
                                                    UserClient.setNewShareHousingAppointment(
                                                            mShareHousing.getID(), mShareHousing.getCreator().getUserID(),
                                                            s, mInputScheduleNotes.getText().toString(),
                                                            new IShareHousingAppointmentPostingCallback() {
                                                                @Override
                                                                public void onPostComplete(ShareHousingAppointment shareHousingAppointment) {
                                                                    if (shareHousingAppointment != null) {
                                                                        mShareHousingAppointment = shareHousingAppointment;
                                                                        ToastHelper.showCenterToast(
                                                                                getApplicationContext(),
                                                                                R.string.activity_share_housing_detail_set_schedule_successfully_message,
                                                                                Toast.LENGTH_LONG
                                                                        );
                                                                        ShareSpaceApplication.BUS.post(new SetNewShareHousingAppointmentEvent(
                                                                                shareHousingAppointment
                                                                        ));
                                                                    } else {
                                                                        ToastHelper.showCenterToast(
                                                                                getApplicationContext(),
                                                                                R.string.activity_share_housing_detail_set_schedule_unsuccessfully_message,
                                                                                Toast.LENGTH_LONG
                                                                        );
                                                                    }
                                                                }

                                                                @Override
                                                                public void onPostFailure(Throwable t) {
                                                                    ToastHelper.showCenterToast(
                                                                            getApplicationContext(),
                                                                            R.string.activity_share_housing_detail_set_schedule_unsuccessfully_message,
                                                                            Toast.LENGTH_LONG
                                                                    );
                                                                    RetrofitClient.showShareSpaceServerConnectionErrorDialog(ShareHousingDetailActivity.this, t);
                                                                }
                                                            }
                                                    );
                                                }
                                            });
                            mScheduleNotesDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.activity_housing_detail_schedule_note_dialog_cancel),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    new AlertDialog.Builder(ShareHousingDetailActivity.this)
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
                            new TimePickerDialog(ShareHousingDetailActivity.this, onTimeSetListener,
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE), true
                            );
                    mScheduleTimePickerDialog.setCanceledOnTouchOutside(false);
                    mScheduleTimePickerDialog.show();
                }
            };
            mScheduleDatePickerDialog =
                    new DatePickerDialog(ShareHousingDetailActivity.this, onDateSetListener,
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH));
            mScheduleDatePickerDialog.setTitle(R.string.activity_share_housing_detail_set_schedule_dialog_title);
            mScheduleDatePickerDialog.setCanceledOnTouchOutside(false);
            mScheduleDatePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            mScheduleDatePickerDialog.show();
        }
    }

    private void handleHousingAppointmentJellyBean(int selectedOption) {
        final Calendar calendar = Calendar.getInstance();
        if (selectedOption == 0) {
            final DatePicker datePicker = new DatePicker(ShareHousingDetailActivity.this);
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

            mScheduleDatePickerDialogJellyBean = new AlertDialog.Builder(ShareHousingDetailActivity.this).create();
            mScheduleDatePickerDialogJellyBean.setTitle(R.string.activity_housing_detail_update_schedule_dialog_title);
            mScheduleDatePickerDialogJellyBean.setView(datePicker);
            mScheduleDatePickerDialogJellyBean.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.activity_housing_detail_set_schedule_dialog_positive_jelly_bean),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            calendar.set(Calendar.YEAR, datePicker.getYear());
                            calendar.set(Calendar.MONTH, datePicker.getMonth());
                            calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());

                            final TimePicker timePicker = new TimePicker(ShareHousingDetailActivity.this);
                            timePicker.setIs24HourView(true);
                            timePicker.setCurrentHour(Integer.parseInt(new SimpleDateFormat("HH").format(mHousingAppointment.getAppointmentDateTime())));
                            timePicker.setCurrentMinute(Integer.parseInt(new SimpleDateFormat("mm").format(mHousingAppointment.getAppointmentDateTime())));

                            mScheduleTimePickerDialogJellyBean = new AlertDialog.Builder(ShareHousingDetailActivity.this).create();
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
                                            FrameLayout container = new FrameLayout(ShareHousingDetailActivity.this);
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

                                            mScheduleNotesDialog = new AlertDialog.Builder(ShareHousingDetailActivity.this).create();
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
                                                                    mShareHousing.getHousing().getID(), mShareHousing.getHousing().getOwner().getUserID(),
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
                                                                            RetrofitClient.showShareSpaceServerConnectionErrorDialog(ShareHousingDetailActivity.this, t);
                                                                        }
                                                                    }
                                                            );
                                                        }
                                                    });
                                            mScheduleNotesDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.activity_housing_detail_schedule_note_dialog_cancel),
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            new AlertDialog.Builder(ShareHousingDetailActivity.this)
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
            new AlertDialog.Builder(ShareHousingDetailActivity.this)
                    .setTitle(R.string.activity_housing_detail_delete_schedule_note_confirm_dialog_title)
                    .setMessage(R.string.activity_housing_detail_delete_schedule_note_confirm_dialog_message)
                    .setPositiveButton(R.string.activity_housing_detail_delete_schedule_note_confirm_dialog_positive,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    UserClient.deleteHousingAppointment(
                                            mShareHousing.getHousing().getID(), mShareHousing.getHousing().getOwner().getUserID(),
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
                                                    RetrofitClient.showShareSpaceServerConnectionErrorDialog(ShareHousingDetailActivity.this, t);
                                                }
                                            }
                                    );
                                }
                            })
                    .setNegativeButton(R.string.activity_housing_detail_delete_schedule_note_confirm_dialog_negative, null)
                    .show();
        } else if (selectedOption == 2) {
            final DatePicker datePicker = new DatePicker(ShareHousingDetailActivity.this);
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

            mScheduleDatePickerDialogJellyBean = new AlertDialog.Builder(ShareHousingDetailActivity.this).create();
            mScheduleDatePickerDialogJellyBean.setTitle(R.string.activity_housing_detail_set_schedule_dialog_title);
            mScheduleDatePickerDialogJellyBean.setView(datePicker);
            mScheduleDatePickerDialogJellyBean.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.activity_housing_detail_set_schedule_dialog_positive_jelly_bean),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            calendar.set(Calendar.YEAR, datePicker.getYear());
                            calendar.set(Calendar.MONTH, datePicker.getMonth());
                            calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());

                            final TimePicker timePicker = new TimePicker(ShareHousingDetailActivity.this);
                            timePicker.setIs24HourView(true);
                            timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
                            timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));

                            mScheduleTimePickerDialogJellyBean = new AlertDialog.Builder(ShareHousingDetailActivity.this).create();
                            mScheduleTimePickerDialogJellyBean.setTitle(R.string.activity_housing_detail_set_time_schedule_dialog_title_jelly_bean);
                            mScheduleTimePickerDialogJellyBean.setView(timePicker);
                            mScheduleTimePickerDialogJellyBean.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.activity_housing_detail_set_schedule_dialog_positive_jelly_bean),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                                            calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());

                                            mInputScheduleNotes.setText("");
                                            FrameLayout container = new FrameLayout(ShareHousingDetailActivity.this);
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

                                            mScheduleNotesDialog = new AlertDialog.Builder(ShareHousingDetailActivity.this).create();
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
                                                                    mShareHousing.getHousing().getID(), mShareHousing.getHousing().getOwner().getUserID(),
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
                                                                            RetrofitClient.showShareSpaceServerConnectionErrorDialog(ShareHousingDetailActivity.this, t);
                                                                        }
                                                                    }
                                                            );
                                                        }
                                                    });
                                            mScheduleNotesDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.activity_housing_detail_schedule_note_dialog_cancel),
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            new AlertDialog.Builder(ShareHousingDetailActivity.this)
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

    private void handleShareHousingAppointmentJellyBean(int selectedOption) {
        final Calendar calendar = Calendar.getInstance();
        if (selectedOption == 0) {
            final DatePicker datePicker = new DatePicker(ShareHousingDetailActivity.this);
            datePicker.init(
                    Integer.parseInt(new SimpleDateFormat("yyyy").format(mShareHousingAppointment.getAppointmentDateTime())),
                    Integer.parseInt(new SimpleDateFormat("MM").format(mShareHousingAppointment.getAppointmentDateTime())) - 1,
                    Integer.parseInt(new SimpleDateFormat("dd").format(mShareHousingAppointment.getAppointmentDateTime())),
                    null
            );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                datePicker.setCalendarViewShown(false);
            }
            datePicker.setMinDate(calendar.getTimeInMillis());

            mScheduleDatePickerDialogJellyBean = new AlertDialog.Builder(ShareHousingDetailActivity.this).create();
            mScheduleDatePickerDialogJellyBean.setTitle(R.string.activity_share_housing_detail_update_schedule_dialog_title);
            mScheduleDatePickerDialogJellyBean.setView(datePicker);
            mScheduleDatePickerDialogJellyBean.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.activity_housing_detail_set_schedule_dialog_positive_jelly_bean),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            calendar.set(Calendar.YEAR, datePicker.getYear());
                            calendar.set(Calendar.MONTH, datePicker.getMonth());
                            calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());

                            final TimePicker timePicker = new TimePicker(ShareHousingDetailActivity.this);
                            timePicker.setIs24HourView(true);
                            timePicker.setCurrentHour(Integer.parseInt(new SimpleDateFormat("HH").format(mShareHousingAppointment.getAppointmentDateTime())));
                            timePicker.setCurrentMinute(Integer.parseInt(new SimpleDateFormat("mm").format(mShareHousingAppointment.getAppointmentDateTime())));

                            mScheduleTimePickerDialogJellyBean = new AlertDialog.Builder(ShareHousingDetailActivity.this).create();
                            mScheduleTimePickerDialogJellyBean.setTitle(R.string.activity_housing_detail_set_time_schedule_dialog_title_jelly_bean);
                            mScheduleTimePickerDialogJellyBean.setView(timePicker);
                            mScheduleTimePickerDialogJellyBean.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.activity_housing_detail_set_schedule_dialog_positive_jelly_bean),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                                            calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());

                                            if (!TextUtils.isEmpty(mShareHousingAppointment.getContent())) {
                                                mInputScheduleNotes.setText(mShareHousingAppointment.getContent());
                                            } else {
                                                mInputScheduleNotes.setText("");
                                            }
                                            FrameLayout container = new FrameLayout(ShareHousingDetailActivity.this);
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

                                            mScheduleNotesDialog = new AlertDialog.Builder(ShareHousingDetailActivity.this).create();
                                            mScheduleNotesDialog.setTitle(R.string.activity_housing_detail_schedule_note_dialog_title);
                                            mScheduleNotesDialog.setMessage(getString(R.string.activity_housing_detail_schedule_note_dialog_message));
                                            mScheduleNotesDialog.setView(container);
                                            mScheduleNotesDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.activity_housing_detail_schedule_note_dialog_save),
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            String s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(calendar.getTime());
                                                            s = s.substring(0, s.length() - 2) + ":" + s.substring(s.length() - 2, s.length());
                                                            UserClient.updateShareHousingAppointment(
                                                                    mShareHousing.getID(), mShareHousing.getCreator().getUserID(),
                                                                    s, mInputScheduleNotes.getText().toString(),
                                                                    new IShareHousingAppointmentUpdatingCallback() {
                                                                        @Override
                                                                        public void onUpdateComplete(ShareHousingAppointment shareHousingAppointment) {
                                                                            if (shareHousingAppointment != null) {
                                                                                mShareHousingAppointment = shareHousingAppointment;
                                                                                ToastHelper.showCenterToast(
                                                                                        getApplicationContext(),
                                                                                        R.string.activity_share_housing_detail_set_schedule_successfully_message,
                                                                                        Toast.LENGTH_LONG
                                                                                );
                                                                                ShareSpaceApplication.BUS.post(new UpdateShareHousingAppointmentEvent(
                                                                                        shareHousingAppointment
                                                                                ));
                                                                            } else {
                                                                                ToastHelper.showCenterToast(
                                                                                        getApplicationContext(),
                                                                                        R.string.activity_share_housing_detail_set_schedule_unsuccessfully_message,
                                                                                        Toast.LENGTH_LONG
                                                                                );
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onUpdateFailure(Throwable t) {
                                                                            ToastHelper.showCenterToast(
                                                                                    getApplicationContext(),
                                                                                    R.string.activity_share_housing_detail_set_schedule_unsuccessfully_message,
                                                                                    Toast.LENGTH_LONG
                                                                            );
                                                                            RetrofitClient.showShareSpaceServerConnectionErrorDialog(ShareHousingDetailActivity.this, t);
                                                                        }
                                                                    }
                                                            );
                                                        }
                                                    });
                                            mScheduleNotesDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.activity_housing_detail_schedule_note_dialog_cancel),
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            new AlertDialog.Builder(ShareHousingDetailActivity.this)
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
            new AlertDialog.Builder(ShareHousingDetailActivity.this)
                    .setTitle(R.string.activity_share_housing_detail_delete_schedule_note_confirm_dialog_title)
                    .setMessage(R.string.activity_housing_detail_delete_schedule_note_confirm_dialog_message)
                    .setPositiveButton(R.string.activity_housing_detail_delete_schedule_note_confirm_dialog_positive,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    UserClient.deleteShareHousingAppointment(
                                            mShareHousing.getID(), mShareHousing.getCreator().getUserID(),
                                            new IShareHousingAppointmentDeletingCallback() {
                                                @Override
                                                public void onDeleteComplete(ShareHousingAppointment shareHousingAppointment) {
                                                    if (shareHousingAppointment != null) {
                                                        mShareHousingAppointment = null;
                                                        ToastHelper.showCenterToast(
                                                                getApplicationContext(),
                                                                R.string.activity_share_housing_detail_delete_schedule_successfully_message,
                                                                Toast.LENGTH_LONG
                                                        );
                                                        ShareSpaceApplication.BUS.post(new DeleteShareHousingAppointmentEvent(
                                                                shareHousingAppointment
                                                        ));
                                                    } else {
                                                        ToastHelper.showCenterToast(
                                                                getApplicationContext(),
                                                                R.string.activity_share_housing_detail_delete_schedule_unsuccessfully_message,
                                                                Toast.LENGTH_LONG
                                                        );
                                                    }
                                                }

                                                @Override
                                                public void onDeleteFailure(Throwable t) {
                                                    ToastHelper.showCenterToast(
                                                            getApplicationContext(),
                                                            R.string.activity_share_housing_detail_delete_schedule_unsuccessfully_message,
                                                            Toast.LENGTH_LONG
                                                    );
                                                    RetrofitClient.showShareSpaceServerConnectionErrorDialog(ShareHousingDetailActivity.this, t);
                                                }
                                            }
                                    );
                                }
                            })
                    .setNegativeButton(R.string.activity_housing_detail_delete_schedule_note_confirm_dialog_negative, null)
                    .show();
        } else if (selectedOption == 2) {
            final DatePicker datePicker = new DatePicker(ShareHousingDetailActivity.this);
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

            mScheduleDatePickerDialogJellyBean = new AlertDialog.Builder(ShareHousingDetailActivity.this).create();
            mScheduleDatePickerDialogJellyBean.setTitle(R.string.activity_share_housing_detail_set_schedule_dialog_title);
            mScheduleDatePickerDialogJellyBean.setView(datePicker);
            mScheduleDatePickerDialogJellyBean.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.activity_housing_detail_set_schedule_dialog_positive_jelly_bean),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            calendar.set(Calendar.YEAR, datePicker.getYear());
                            calendar.set(Calendar.MONTH, datePicker.getMonth());
                            calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());

                            final TimePicker timePicker = new TimePicker(ShareHousingDetailActivity.this);
                            timePicker.setIs24HourView(true);
                            timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
                            timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));

                            mScheduleTimePickerDialogJellyBean = new AlertDialog.Builder(ShareHousingDetailActivity.this).create();
                            mScheduleTimePickerDialogJellyBean.setTitle(R.string.activity_housing_detail_set_time_schedule_dialog_title_jelly_bean);
                            mScheduleTimePickerDialogJellyBean.setView(timePicker);
                            mScheduleTimePickerDialogJellyBean.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.activity_housing_detail_set_schedule_dialog_positive_jelly_bean),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                                            calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());

                                            mInputScheduleNotes.setText("");
                                            FrameLayout container = new FrameLayout(ShareHousingDetailActivity.this);
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

                                            mScheduleNotesDialog = new AlertDialog.Builder(ShareHousingDetailActivity.this).create();
                                            mScheduleNotesDialog.setTitle(R.string.activity_housing_detail_schedule_note_dialog_title);
                                            mScheduleNotesDialog.setMessage(getString(R.string.activity_housing_detail_schedule_note_dialog_message));
                                            mScheduleNotesDialog.setView(container);
                                            mScheduleNotesDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.activity_housing_detail_schedule_note_dialog_save),
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            String s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(calendar.getTime());
                                                            s = s.substring(0, s.length() - 2) + ":" + s.substring(s.length() - 2, s.length());
                                                            UserClient.setNewShareHousingAppointment(
                                                                    mShareHousing.getID(), mShareHousing.getCreator().getUserID(),
                                                                    s, mInputScheduleNotes.getText().toString(),
                                                                    new IShareHousingAppointmentPostingCallback() {
                                                                        @Override
                                                                        public void onPostComplete(ShareHousingAppointment shareHousingAppointment) {
                                                                            if (shareHousingAppointment != null) {
                                                                                mShareHousingAppointment = shareHousingAppointment;
                                                                                ToastHelper.showCenterToast(
                                                                                        getApplicationContext(),
                                                                                        R.string.activity_share_housing_detail_set_schedule_successfully_message,
                                                                                        Toast.LENGTH_LONG
                                                                                );
                                                                                ShareSpaceApplication.BUS.post(new SetNewShareHousingAppointmentEvent(
                                                                                        shareHousingAppointment
                                                                                ));
                                                                            } else {
                                                                                ToastHelper.showCenterToast(
                                                                                        getApplicationContext(),
                                                                                        R.string.activity_share_housing_detail_set_schedule_unsuccessfully_message,
                                                                                        Toast.LENGTH_LONG
                                                                                );
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onPostFailure(Throwable t) {
                                                                            ToastHelper.showCenterToast(
                                                                                    getApplicationContext(),
                                                                                    R.string.activity_share_housing_detail_set_schedule_unsuccessfully_message,
                                                                                    Toast.LENGTH_LONG
                                                                            );
                                                                            RetrofitClient.showShareSpaceServerConnectionErrorDialog(ShareHousingDetailActivity.this, t);
                                                                        }
                                                                    }
                                                            );
                                                        }
                                                    });
                                            mScheduleNotesDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.activity_housing_detail_schedule_note_dialog_cancel),
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            new AlertDialog.Builder(ShareHousingDetailActivity.this)
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
                            Drawable drawable = new BitmapDrawable(ShareHousingDetailActivity.this.getResources(), resource);
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
                            Drawable drawable = new BitmapDrawable(ShareHousingDetailActivity.this.getResources(), resource);
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
                Intent intent = new Intent(ShareHousingDetailActivity.this, ImageViewerActivity.class);
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
                HousingClient.deletePhotoURL(mShareHousing.getHousing().getID(), itemIndex, new IPhotoDeletingCallback() {
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
                                ShareSpaceApplication.BUS.post(new DeleteShareHousingPhotoEvent(
                                        new HistoryShareHousingPhoto(mShareHousing, new Date())
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
                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(ShareHousingDetailActivity.this, t);
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
