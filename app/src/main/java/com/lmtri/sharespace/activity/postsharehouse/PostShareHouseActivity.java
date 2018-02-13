package com.lmtri.sharespace.activity.postsharehouse;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.lmtri.sharespace.activity.posthouse.PostHouseAddressActivity;
import com.lmtri.sharespace.activity.posthouse.PostHouseDetailedItemActivity;
import com.lmtri.sharespace.api.model.Housing;
import com.lmtri.sharespace.api.model.ShareHousing;
import com.lmtri.sharespace.api.service.RetrofitClient;
import com.lmtri.sharespace.customview.CustomEditText;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.FileHelper;
import com.lmtri.sharespace.helper.HousePriceHelper;
import com.lmtri.sharespace.helper.KeyboardHelper;
import com.lmtri.sharespace.helper.ListHelper;
import com.lmtri.sharespace.helper.PermissionHelper;
import com.lmtri.sharespace.helper.ToastHelper;
import com.lmtri.sharespace.helper.busevent.sharehousing.post.PostShareHousingEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.post.UpdateShareHousingEvent;
import com.lmtri.sharespace.helper.firebasestorage.FirebaseStorageHelper;
import com.lmtri.sharespace.helper.firebasestorage.sharehousing.OnShareHousingPostingListener;
import com.lmtri.sharespace.helper.firebasestorage.sharehousing.OnShareHousingUpdatingListener;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.FishBunCreator;
import com.sangcomz.fishbun.define.Define;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PostShareHouseActivity extends AppCompatActivity {
    public static final String TAG = PostShareHouseActivity.class.getSimpleName();

    private GridLayout mPhotoGridContainer;
    private GridLayout.LayoutParams mPhotoGridItemLayoutParams;
    private LayoutInflater mLayoutInflater;
    private View.OnClickListener mProfilePhotoGridItemClickListener;
    private View.OnClickListener mReplaceNormalPhotoGridItemClickListener;
    private View.OnClickListener mAddMoreNormalPhotoGridItemClickListener;
    private int mCurrentSelectedGridItemIndex;
    private boolean mIsChooseProfilePhoto = false;
    private boolean mIsReplaceNormalPhoto = false;
    private ArrayList<Uri> mProfilePhotoUri = new ArrayList<>();
    private ArrayList<Uri> mReplaceNormalPhotoUri = new ArrayList<>();
    private ArrayList<Uri> mAllCurrentSelectedPhotoUris = new ArrayList<>();
    private ArrayList<Uri> mPreviousAllCurrentSelectedPhotoUris = new ArrayList<>();
    private ArrayList<Integer> mDifferentIndexes;
    private static int mIndexOfDifferentIndexesList = 0;
    private static int mLoadPhotoIndex = 0;
    private int mGridContainerWidthPixels;
    private int mGridItemMarginPixels;
    private int mGridItemWidthPixels;

    private LinearLayout mHouseTitleLayout;
    private TextView mHouseTitleText;

    private View.OnFocusChangeListener mFocusChangeListener;
    private TextView.OnEditorActionListener mEditorActionListener;
    private CustomEditText mNumPeopleEditText;
    private CustomEditText mNumRoomEditText;
    private CustomEditText mNumBedEditText;
    private CustomEditText mNumBathEditText;

    private LinearLayout mHouseTypeLayout;
    private TextView mHouseTypeText;

    private CustomEditText mAreaEditText;

    private LinearLayout mAddressLayout;
    private TextView mAddressText;
    private String mAddressHouseNumber = "";
    private String mAddressStreet = "";
    private String mAddressWard = "";
    private String mAddressDistrict = "";
    private String mAddressCity = "";
    private String mLatitude = "";
    private String mLongitude = "";

    private LinearLayout mHouseDirectionLayout;
    private TextView mHouseDirectionText;

    private LinearLayout mPriceLayout;
    private TextView mPriceText;
    private float mPrice = 0;
    private String mPriceUnit = "";

    private LinearLayout mContactLayout;
    private TextView mContactText;
    private String mContactName = Constants.CONTACT_NAME;
    private String mContactNumber = Constants.CONTACT_NUMBER;
    private String mContactEmail = Constants.CONTACT_EMAIL;

    private LinearLayout mDescriptionLayout;
    private TextView mDescriptionText;

    private CustomEditText mTimeRestrictionEditText;
    private Calendar mCalendar;

    private LinearLayout mAllowPetLayout;
    private ImageView mAllowPetCheckmark;
    private boolean mAllowPet = false;

    private LinearLayout mHasWifiLayout;
    private ImageView mHasWifiCheckmark;
    private boolean mHasWifi = false;

    private LinearLayout mHasACLayout;
    private ImageView mHasACCheckmark;
    private boolean mHasAC = false;

    private LinearLayout mHasParkingLayout;
    private ImageView mHasParkingCheckmark;
    private boolean mHasParking = false;

    private boolean mIsFindRoommate = false;
    private boolean mIsPostNewShareHousing = false;
    private boolean mIsEditShareHousing = false;
    private View mDisableEditView;

    private LinearLayout mPricePerMonthOfOneLayout;
    private TextView mPricePerMonthOfOneText;
    private float mPricePerMonthOfOne = 0;
    private String mPricePerMonthOfOneUnit = "";

    private LinearLayout mDescriptionShareHouseLayout;
    private TextView mDescriptionShareHouseText;

    private CustomEditText mShareHouseRequiredNumPeopleEditText;
    private Spinner mShareHouseRequiredGenderSpinner;
    private Spinner mShareHouseRequiredWorkTypeSpinner;

    private LinearLayout mAllowSmokingLayout;
    private ImageView mAllowSmokingCheckmark;
    private boolean mAllowSmoking = false;

    private LinearLayout mAllowAlcoholLayout;
    private ImageView mAllowAlcoholCheckmark;
    private boolean mAllowAlcohol = false;

    private LinearLayout mHasPrivateKeyLayout;
    private ImageView mHasPrivateKeyCheckmark;
    private boolean mHasPrivateKey = false;

    private RelativeLayout mResetInputLayout;

    private RelativeLayout mPostBarLayout;
    private TextView mCancelText;
    private TextView mPostText;

    private Housing mToBePostedHousing = null;
    private ShareHousing mToBePostedShareHousing = null;

    private View mDimmedView;
    private boolean mIsBottomSlidingOverlayMenuAnimationRunning;
    private LinearLayout mBottomSlidingOverlayMenu;
    private int mBottomSlidingOverlayMenuHeight;
    private PermissionHelper mPermissionHelper = new PermissionHelper(this);
    private TextView mBottomSlidingOverlayMenuCamera;
    private TextView mBottomSlidingOverlayMenuGallery;
    private TextView mBottomSlidingOverlayMenuCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_share_house);

        mIsFindRoommate = getIntent().getBooleanExtra(Constants.IS_FIND_ROOMMATE_EXTRA, false);
        mIsPostNewShareHousing = getIntent().getBooleanExtra(Constants.IS_POST_NEW_SHARE_HOUSING_EXTRA, false);
        mIsEditShareHousing = getIntent().getBooleanExtra(Constants.IS_EDIT_SHARE_HOUSING_EXTRA, false);
        mDisableEditView = findViewById(R.id.activity_post_share_house_disable_edit_view);
        mDisableEditView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        if (mIsPostNewShareHousing) {
            mDisableEditView.setVisibility(View.GONE);
        } else if (mIsFindRoommate) {
            Gson gson = new Gson();
            try {
                mToBePostedHousing = gson.fromJson(
                        getIntent().getStringExtra(Constants.HOUSING_INFO_FOR_FINDING_ROOMMATE_EXTRA),
                        Housing.class
                );
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        } else if (mIsEditShareHousing) {
            Gson gson = new Gson();
            try {
                mToBePostedShareHousing = gson.fromJson(
                        getIntent().getStringExtra(Constants.SHARE_HOUSING_INFO_FOR_EDITING_POST_EXTRA),
                        ShareHousing.class
                );
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
            if (Constants.CURRENT_USER.getUserID() == mToBePostedShareHousing.getCreator().getUserID()
                    && Constants.CURRENT_USER.getUserID() == mToBePostedShareHousing.getHousing().getOwner().getUserID()) {
                mDisableEditView.setVisibility(View.GONE);
            }
        }
//        if (mIsPostNewShareHousing) {
//            for (int i = 0; i < mPostShareHouseFieldsContainer.getChildCount(); i++) {
//                if (mPostShareHouseFieldsContainer.getChildAt(i).getVisibility()
//                        == View.GONE) {
//                    mPostShareHouseFieldsContainer.getChildAt(i).setVisibility(View.VISIBLE);
//                }
//            }
//        }

        mPhotoGridContainer = (GridLayout) findViewById(R.id.activity_post_house_add_photo_grid_container);
        mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mProfilePhotoGridItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentSelectedGridItemIndex = (int) v.getTag();
                mIsChooseProfilePhoto = true;
                mIsReplaceNormalPhoto = false;
                showBottomSlidingOverlayMenu();
            }
        };
        mReplaceNormalPhotoGridItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentSelectedGridItemIndex = (int) v.getTag();
                mIsChooseProfilePhoto = false;
                mIsReplaceNormalPhoto = true;
                showBottomSlidingOverlayMenu();
            }
        };
        mAddMoreNormalPhotoGridItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentSelectedGridItemIndex = (int) v.getTag();
                mIsChooseProfilePhoto = false;
                mIsReplaceNormalPhoto = false;
                showBottomSlidingOverlayMenu();
            }
        };

        mPhotoGridContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mPhotoGridContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mPhotoGridContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                mGridContainerWidthPixels = mPhotoGridContainer.getWidth();
                mGridItemMarginPixels = PostShareHouseActivity.this.getResources().getDimensionPixelSize(R.dimen.activity_post_house_add_photo_box_margin);
                mGridItemWidthPixels = (mGridContainerWidthPixels - mGridItemMarginPixels * 8) / 4;

                if (mIsPostNewShareHousing) {
                    inflateInitialAddProfilePhotoBox();
                } else if (mIsFindRoommate) {
                    for (String url : mToBePostedHousing.getPhotoURLs()) {
                        mAllCurrentSelectedPhotoUris.add(Uri.parse(url));
                    }
                    mProfilePhotoUri.clear();
                    mProfilePhotoUri.add(mAllCurrentSelectedPhotoUris.get(0));
                    mLoadPhotoIndex = 0;
                    loadAllSelectedPhoto();
                } else if (mIsEditShareHousing) {
                    for (String url : mToBePostedShareHousing.getHousing().getPhotoURLs()) {
                        mAllCurrentSelectedPhotoUris.add(Uri.parse(url));
                    }
                    mProfilePhotoUri.clear();
                    mProfilePhotoUri.add(mAllCurrentSelectedPhotoUris.get(0));
                    mLoadPhotoIndex = 0;
                    loadAllSelectedPhoto();
                }
            }
        });

        mHouseTitleLayout = (LinearLayout) findViewById(R.id.activity_post_house_house_title_field);
        mHouseTitleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PostHouseDetailedItemActivity.class);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_TOOLBAR_TITLE_EXTRA, Constants.ACTIVITY_HOUSE_TITLE_TOOLBAR_TITLE);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_DETAILS_EXTRA, mHouseTitleText.getText());
                startActivityForResult(intent, Constants.START_ACTIVITY_DETAILED_ITEM_HOUSE_TITLE_REQUEST);
                overridePendingTransition(R.anim.push_left_in, R.anim.scale_down_out);

                KeyboardHelper.hideSoftKeyboard(PostShareHouseActivity.this, true);
            }
        });
        mHouseTitleText = (TextView) findViewById(R.id.activity_post_house_title_text);
        mHouseTitleText.setHorizontallyScrolling(true);
        mHouseTitleText.setSelected(true);

        mFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mPostBarLayout.setVisibility(View.GONE);
                } else {
                    mPostBarLayout.setVisibility(View.VISIBLE);
                }
            }
        };
        mEditorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    KeyboardHelper.hideSoftKeyboard(PostShareHouseActivity.this, true);
                }
                return false;
            }
        };

        mNumPeopleEditText = (CustomEditText) findViewById(R.id.activity_post_house_num_people_edit_text);
        mNumPeopleEditText.setOnFocusChangeListener(mFocusChangeListener);
        mNumPeopleEditText.setOnEditorActionListener(mEditorActionListener);

        mNumRoomEditText = (CustomEditText) findViewById(R.id.activity_post_house_num_room_edit_text);
        mNumRoomEditText.setOnFocusChangeListener(mFocusChangeListener);
        mNumRoomEditText.setOnEditorActionListener(mEditorActionListener);

        mNumBedEditText = (CustomEditText) findViewById(R.id.activity_post_house_num_bed_edit_text);
        mNumBedEditText.setOnFocusChangeListener(mFocusChangeListener);
        mNumBedEditText.setOnEditorActionListener(mEditorActionListener);

        mNumBathEditText = (CustomEditText) findViewById(R.id.activity_post_house_num_bath_edit_text);
        mNumBathEditText.setOnFocusChangeListener(mFocusChangeListener);
        mNumBathEditText.setOnEditorActionListener(mEditorActionListener);

        mHouseTypeLayout = (LinearLayout) findViewById(R.id.activity_post_house_house_type_field);
        mHouseTypeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostShareHouseActivity.this, PostHouseDetailedItemActivity.class);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_TOOLBAR_TITLE_EXTRA, Constants.ACTIVITY_HOUSE_TYPE_TOOLBAR_TITLE);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_DETAILS_EXTRA, mHouseTypeText.getText());
                startActivityForResult(intent, Constants.START_ACTIVITY_DETAILED_ITEM_HOUSE_TYPE_REQUEST);
                overridePendingTransition(R.anim.push_left_in, R.anim.scale_down_out);

                KeyboardHelper.hideSoftKeyboard(PostShareHouseActivity.this, true);
            }
        });
        mHouseTypeText = (TextView) findViewById(R.id.activity_post_house_house_type_text);
        mHouseTypeText.setHorizontallyScrolling(true);
        mHouseTypeText.setSelected(true);

        mAreaEditText = (CustomEditText) findViewById(R.id.activity_post_house_area_edit_text);
        mAreaEditText.setOnFocusChangeListener(mFocusChangeListener);
        mAreaEditText.setOnEditorActionListener(mEditorActionListener);

        mAddressLayout = (LinearLayout) findViewById(R.id.activity_post_house_address_field);
        mAddressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(PostShareHouseActivity.this, PostHouseDetailedItemActivity.class);
//                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_TOOLBAR_TITLE_EXTRA, Constants.ACTIVITY_ADDRESS_TOOLBAR_TITLE);
//                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_ADDRESS_HOUSE_NUMBER_EXTRA, mAddressHouseNumber);
//                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_ADDRESS_STREET_EXTRA, mAddressStreet);
//                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_ADDRESS_WARD_EXTRA, mAddressWard);
//                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_ADDRESS_DISTRICT_EXTRA, mAddressDistrict);
//                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_ADDRESS_CITY_EXTRA, mAddressCity);
//                startActivityForResult(intent, Constants.START_ACTIVITY_DETAILED_ITEM_ADDRESS_REQUEST);
                Intent intent = new Intent(PostShareHouseActivity.this, PostHouseAddressActivity.class);
                intent.putExtra(Constants.ACTIVITY_POST_HOUSE_ADDRESS_HOUSE_NUMBER_EXTRA, mAddressHouseNumber);
                intent.putExtra(Constants.ACTIVITY_POST_HOUSE_ADDRESS_STREET_EXTRA, mAddressStreet);
                intent.putExtra(Constants.ACTIVITY_POST_HOUSE_ADDRESS_WARD_EXTRA, mAddressWard);
                intent.putExtra(Constants.ACTIVITY_POST_HOUSE_ADDRESS_DISTRICT_EXTRA, mAddressDistrict);
                intent.putExtra(Constants.ACTIVITY_POST_HOUSE_ADDRESS_CITY_EXTRA, mAddressCity);
                intent.putExtra(Constants.ACTIVITY_POST_HOUSE_ADDRESS_LATITUDE_EXTRA, mLatitude);
                intent.putExtra(Constants.ACTIVITY_POST_HOUSE_ADDRESS_LONGITUDE_EXTRA, mLongitude);
                startActivityForResult(intent, Constants.START_ACTIVITY_POST_HOUSE_ADDRESS_REQUEST);
                overridePendingTransition(R.anim.push_left_in, R.anim.scale_down_out);

                KeyboardHelper.hideSoftKeyboard(PostShareHouseActivity.this, true);
            }
        });
        mAddressText = (TextView) findViewById(R.id.activity_post_house_address_text);
        mAddressText.setHorizontallyScrolling(true);
        mAddressText.setSelected(true);

        mHouseDirectionLayout = (LinearLayout) findViewById(R.id.activity_post_house_house_direction_field);
        mHouseDirectionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostShareHouseActivity.this, PostHouseDetailedItemActivity.class);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_TOOLBAR_TITLE_EXTRA, Constants.ACTIVITY_HOUSE_DIRECTION_TOOLBAR_TITLE);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_DETAILS_EXTRA, mHouseDirectionText.getText());
                startActivityForResult(intent, Constants.START_ACTIVITY_DETAILED_ITEM_HOUSE_DIRECTION_REQUEST);
                overridePendingTransition(R.anim.push_left_in, R.anim.scale_down_out);

                KeyboardHelper.hideSoftKeyboard(PostShareHouseActivity.this, true);
            }
        });
        mHouseDirectionText = (TextView) findViewById(R.id.activity_post_house_house_direction_text);
        mHouseDirectionText.setHorizontallyScrolling(true);
        mHouseDirectionText.setSelected(true);

        mPriceLayout = (LinearLayout) findViewById(R.id.activity_post_house_price_field);
        mPriceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostShareHouseActivity.this, PostHouseDetailedItemActivity.class);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_TOOLBAR_TITLE_EXTRA, Constants.ACTIVITY_PRICE_TOOLBAR_TITLE);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_PRICE_EXTRA, mPrice);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_PRICE_UNIT_EXTRA, mPriceUnit);
                startActivityForResult(intent, Constants.START_ACTIVITY_DETAILED_ITEM_PRICE_REQUEST);
                overridePendingTransition(R.anim.push_left_in, R.anim.scale_down_out);

                KeyboardHelper.hideSoftKeyboard(PostShareHouseActivity.this, true);
            }
        });
        mPriceText = (TextView) findViewById(R.id.activity_post_house_price_text);
        mPriceText.setHorizontallyScrolling(true);
        mPriceText.setSelected(true);

        mContactLayout = (LinearLayout) findViewById(R.id.activity_post_house_contact_field);
        mContactLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostShareHouseActivity.this, PostHouseDetailedItemActivity.class);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_TOOLBAR_TITLE_EXTRA, Constants.ACTIVITY_CONTACT_TOOLBAR_TITLE);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_CONTACT_NAME_EXTRA, Constants.CONTACT_NAME);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_CONTACT_NUMBER_EXTRA, Constants.CONTACT_NUMBER);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_CONTACT_EMAIL_EXTRA, Constants.CONTACT_EMAIL);
                startActivityForResult(intent, Constants.START_ACTIVITY_DETAILED_ITEM_CONTACT_REQUEST);
                overridePendingTransition(R.anim.push_left_in, R.anim.scale_down_out);

                KeyboardHelper.hideSoftKeyboard(PostShareHouseActivity.this, true);
            }
        });
        mContactText = (TextView) findViewById(R.id.activity_post_house_contact_text);
        mContactText.setHorizontallyScrolling(true);
        mContactText.setSelected(true);
        mContactText.setText(Constants.CONTACT_NAME + "-" + Constants.CONTACT_NUMBER + "-" + Constants.CONTACT_EMAIL);

        mDescriptionLayout = (LinearLayout) findViewById(R.id.activity_post_house_description_field);
        mDescriptionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostShareHouseActivity.this, PostHouseDetailedItemActivity.class);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_TOOLBAR_TITLE_EXTRA, Constants.ACTIVITY_DESCRIPTION_TOOLBAR_TITLE);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_DETAILS_EXTRA, mDescriptionText.getText());
                startActivityForResult(intent, Constants.START_ACTIVITY_DETAILED_ITEM_DESCRIPTION_REQUEST);
                overridePendingTransition(R.anim.push_left_in, R.anim.scale_down_out);

                KeyboardHelper.hideSoftKeyboard(PostShareHouseActivity.this, true);
            }
        });
        mDescriptionText = (TextView) findViewById(R.id.activity_post_house_description_text);
        mDescriptionText.setHorizontallyScrolling(true);
        mDescriptionText.setSelected(true);

        mTimeRestrictionEditText = (CustomEditText) findViewById(R.id.activity_post_house_time_restriction_edit_text);
        mCalendar = Calendar.getInstance();
        final TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mCalendar.set(Calendar.MINUTE, minute);
                String timeFormat = "HH:mm";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeFormat, Locale.CANADA);

                mTimeRestrictionEditText.setText(simpleDateFormat.format(mCalendar.getTime()));
            }
        };
        mTimeRestrictionEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(
                        PostShareHouseActivity.this,
                        onTimeSetListener,
                        mCalendar.get(Calendar.HOUR_OF_DAY),
                        mCalendar.get(Calendar.MINUTE),
                        true
                ).show();
            }
        });
        mTimeRestrictionEditText.setInputType(InputType.TYPE_NULL);

        View.OnClickListener allowPetClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mAllowPet) {
                    mAllowPetCheckmark.setColorFilter(
                            ContextCompat.getColor(PostShareHouseActivity.this, R.color.colorPrimary),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mAllowPet = true;
                } else {
                    mAllowPetCheckmark.setColorFilter(
                            ContextCompat.getColor(PostShareHouseActivity.this, R.color.silver_gray),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mAllowPet = false;
                }
            }
        };
        mAllowPetLayout = (LinearLayout) findViewById(R.id.activity_post_house_allow_pet_field);
        mAllowPetLayout.setOnClickListener(allowPetClickListener);
        mAllowPetCheckmark = (ImageView) findViewById(R.id.activity_post_house_allow_pet_check_mark);
        mAllowPetCheckmark.setOnClickListener(allowPetClickListener);
        mAllowPetCheckmark.setColorFilter(
                ContextCompat.getColor(PostShareHouseActivity.this, R.color.silver_gray),
                PorterDuff.Mode.SRC_ATOP
        );
        mAllowPet = false;

        View.OnClickListener hasWifiClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mHasWifi) {
                    mHasWifiCheckmark.setColorFilter(
                            ContextCompat.getColor(PostShareHouseActivity.this, R.color.colorPrimary),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mHasWifi = true;
                } else {
                    mHasWifiCheckmark.setColorFilter(
                            ContextCompat.getColor(PostShareHouseActivity.this, R.color.silver_gray),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mHasWifi = false;
                }
            }
        };
        mHasWifiLayout = (LinearLayout) findViewById(R.id.activity_post_house_has_wifi_field);
        mHasWifiLayout.setOnClickListener(hasWifiClickListener);
        mHasWifiCheckmark = (ImageView) findViewById(R.id.activity_post_house_has_wifi_check_mark);
        mHasWifiCheckmark.setOnClickListener(hasWifiClickListener);
        mHasWifiCheckmark.setColorFilter(
                ContextCompat.getColor(PostShareHouseActivity.this, R.color.silver_gray),
                PorterDuff.Mode.SRC_ATOP
        );
        mHasWifi = false;

        View.OnClickListener hasACClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mHasAC) {
                    mHasACCheckmark.setColorFilter(
                            ContextCompat.getColor(PostShareHouseActivity.this, R.color.colorPrimary),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mHasAC = true;
                } else {
                    mHasACCheckmark.setColorFilter(
                            ContextCompat.getColor(PostShareHouseActivity.this, R.color.silver_gray),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mHasAC = false;
                }
            }
        };
        mHasACLayout = (LinearLayout) findViewById(R.id.activity_post_house_has_ac_field);
        mHasACLayout.setOnClickListener(hasACClickListener);
        mHasACCheckmark = (ImageView) findViewById(R.id.activity_post_house_has_ac_check_mark);
        mHasACCheckmark.setOnClickListener(hasACClickListener);
        mHasACCheckmark.setColorFilter(
                ContextCompat.getColor(PostShareHouseActivity.this, R.color.silver_gray),
                PorterDuff.Mode.SRC_ATOP
        );
        mHasAC = false;

        View.OnClickListener hasParkingClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mHasParking) {
                    mHasParkingCheckmark.setColorFilter(
                            ContextCompat.getColor(PostShareHouseActivity.this, R.color.colorPrimary),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mHasParking = true;
                } else {
                    mHasParkingCheckmark.setColorFilter(
                            ContextCompat.getColor(PostShareHouseActivity.this, R.color.silver_gray),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mHasParking = false;
                }
            }
        };
        mHasParkingLayout = (LinearLayout) findViewById(R.id.activity_post_house_has_parking_field);
        mHasParkingLayout.setOnClickListener(hasParkingClickListener);
        mHasParkingCheckmark = (ImageView) findViewById(R.id.activity_post_house_has_parking_check_mark);
        mHasParkingCheckmark.setOnClickListener(hasParkingClickListener);
        mHasParkingCheckmark.setColorFilter(
                ContextCompat.getColor(PostShareHouseActivity.this, R.color.silver_gray),
                PorterDuff.Mode.SRC_ATOP
        );
        mHasParking = false;

        mPricePerMonthOfOneLayout = (LinearLayout) findViewById(R.id.activity_post_share_house_price_field);
        mPricePerMonthOfOneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostShareHouseActivity.this, PostShareHouseDetailedItemActivity.class);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_TOOLBAR_TITLE_EXTRA, Constants.ACTIVITY_PRICE_PER_MONTH_OF_ONE_TOOLBAR_TITLE);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_PRICE_EXTRA, mPricePerMonthOfOne);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_PRICE_UNIT_EXTRA, mPricePerMonthOfOneUnit);
                startActivityForResult(intent, Constants.START_ACTIVITY_DETAILED_ITEM_PRICE_PER_MONTH_OF_ONE_REQUEST);
                overridePendingTransition(R.anim.push_left_in, R.anim.scale_down_out);

                KeyboardHelper.hideSoftKeyboard(PostShareHouseActivity.this, true);
            }
        });
        mPricePerMonthOfOneText = (TextView) findViewById(R.id.activity_post_share_house_price_text);
        mPricePerMonthOfOneText.setHorizontallyScrolling(true);
        mPricePerMonthOfOneText.setSelected(true);

        mDescriptionShareHouseLayout = (LinearLayout) findViewById(R.id.activity_post_share_house_description_field);
        mDescriptionShareHouseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostShareHouseActivity.this, PostShareHouseDetailedItemActivity.class);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_TOOLBAR_TITLE_EXTRA, Constants.ACTIVITY_DESCRIPTION_TOOLBAR_TITLE);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_DETAILS_EXTRA, mDescriptionShareHouseText.getText());
                startActivityForResult(intent, Constants.START_ACTIVITY_DETAILED_ITEM_SHARE_DESCRIPTION_REQUEST);
                overridePendingTransition(R.anim.push_left_in, R.anim.scale_down_out);

                KeyboardHelper.hideSoftKeyboard(PostShareHouseActivity.this, true);
            }
        });
        mDescriptionShareHouseText = (TextView) findViewById(R.id.activity_post_share_house_description_text);
        mDescriptionShareHouseText.setHorizontallyScrolling(true);
        mDescriptionShareHouseText.setSelected(true);

        mShareHouseRequiredNumPeopleEditText = (CustomEditText) findViewById(R.id.activity_post_share_house_required_num_people_edit_text);
        mShareHouseRequiredNumPeopleEditText.setOnFocusChangeListener(mFocusChangeListener);
        mShareHouseRequiredNumPeopleEditText.setOnEditorActionListener(mEditorActionListener);

        mShareHouseRequiredGenderSpinner = (Spinner) findViewById(R.id.activity_post_share_house_required_gender_spinner);
        mShareHouseRequiredGenderSpinner.setSelection(0);

        mShareHouseRequiredWorkTypeSpinner = (Spinner) findViewById(R.id.activity_post_share_house_required_work_type_spinner);
        mShareHouseRequiredWorkTypeSpinner.setSelection(0);

        View.OnClickListener allowSmokingClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mAllowSmoking) {
                    mAllowSmokingCheckmark.setColorFilter(
                            ContextCompat.getColor(PostShareHouseActivity.this, R.color.colorPrimary),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mAllowSmoking = true;
                } else {
                    mAllowSmokingCheckmark.setColorFilter(
                            ContextCompat.getColor(PostShareHouseActivity.this, R.color.silver_gray),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mAllowSmoking = false;
                }
            }
        };
        mAllowSmokingLayout = (LinearLayout) findViewById(R.id.activity_post_share_house_allow_smoking_field);
        mAllowSmokingLayout.setOnClickListener(allowSmokingClickListener);
        mAllowSmokingCheckmark = (ImageView) findViewById(R.id.activity_post_share_house_allow_smoking_check_mark);
        mAllowSmokingCheckmark.setOnClickListener(hasParkingClickListener);
        mAllowSmokingCheckmark.setColorFilter(
                ContextCompat.getColor(PostShareHouseActivity.this, R.color.silver_gray),
                PorterDuff.Mode.SRC_ATOP
        );
        mAllowSmoking = false;

        View.OnClickListener allowAlcoholClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mAllowAlcohol) {
                    mAllowAlcoholCheckmark.setColorFilter(
                            ContextCompat.getColor(PostShareHouseActivity.this, R.color.colorPrimary),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mAllowAlcohol = true;
                } else {
                    mAllowAlcoholCheckmark.setColorFilter(
                            ContextCompat.getColor(PostShareHouseActivity.this, R.color.silver_gray),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mAllowAlcohol = false;
                }
            }
        };
        mAllowAlcoholLayout = (LinearLayout) findViewById(R.id.activity_post_share_house_allow_alcohol_field);
        mAllowAlcoholLayout.setOnClickListener(allowAlcoholClickListener);
        mAllowAlcoholCheckmark = (ImageView) findViewById(R.id.activity_post_share_house_allow_alcohol_check_mark);
        mAllowAlcoholCheckmark.setOnClickListener(hasParkingClickListener);
        mAllowAlcoholCheckmark.setColorFilter(
                ContextCompat.getColor(PostShareHouseActivity.this, R.color.silver_gray),
                PorterDuff.Mode.SRC_ATOP
        );
        mAllowAlcohol = false;

        View.OnClickListener hasPrivateKeyClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mHasPrivateKey) {
                    mHasPrivateKeyCheckmark.setColorFilter(
                            ContextCompat.getColor(PostShareHouseActivity.this, R.color.colorPrimary),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mHasPrivateKey = true;
                } else {
                    mHasPrivateKeyCheckmark.setColorFilter(
                            ContextCompat.getColor(PostShareHouseActivity.this, R.color.silver_gray),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mHasPrivateKey = false;
                }
            }
        };
        mHasPrivateKeyLayout = (LinearLayout) findViewById(R.id.activity_post_house_has_private_key_field);
        mHasPrivateKeyLayout.setOnClickListener(hasPrivateKeyClickListener);
        mHasPrivateKeyCheckmark = (ImageView) findViewById(R.id.activity_post_house_has_private_key_check_mark);
        mHasPrivateKeyCheckmark.setOnClickListener(hasPrivateKeyClickListener);
        mHasPrivateKeyCheckmark.setColorFilter(
                ContextCompat.getColor(PostShareHouseActivity.this, R.color.silver_gray),
                PorterDuff.Mode.SRC_ATOP
        );
        mHasPrivateKey = false;

        mResetInputLayout = (RelativeLayout) findViewById(R.id.activity_post_house_reset_input);
        mResetInputLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsPostNewShareHousing || (mIsEditShareHousing && Constants.CURRENT_USER.getUserID() == mToBePostedShareHousing.getCreator().getUserID()
                        && Constants.CURRENT_USER.getUserID() == mToBePostedShareHousing.getHousing().getOwner().getUserID())) {
                    mPhotoGridContainer.removeAllViews();
                    mAllCurrentSelectedPhotoUris.clear();
                    inflateInitialAddProfilePhotoBox();

                    mHouseTitleText.setText("");

                    mNumPeopleEditText.setText("");
                    mNumRoomEditText.setText("");
                    mNumBedEditText.setText("");
                    mNumBathEditText.setText("");

                    mHouseTypeText.setText(getString(R.string.activity_post_house_detailed_item_house_type_any));

                    mAreaEditText.setText("");

                    mAddressText.setText("");
                    mAddressHouseNumber = "";
                    mAddressStreet = "";
                    mAddressWard = "";
                    mAddressDistrict = "";
                    mAddressCity = "";
                    mLatitude = "";
                    mLongitude = "";

                    mHouseDirectionText.setText(getString(R.string.activity_post_house_detailed_item_house_direction_any));

                    mPriceText.setText("");
                    mPrice = 0;
                    mPriceUnit = "";

                    mContactText.setText(Constants.CONTACT_NAME + "-" + Constants.CONTACT_NUMBER + "-" + Constants.CONTACT_EMAIL);
                    mContactName = Constants.CONTACT_NAME;
                    mContactNumber = Constants.CONTACT_NUMBER;
                    mContactEmail = Constants.CONTACT_EMAIL;

                    mDescriptionText.setText("");

                    mTimeRestrictionEditText.setText("");

                    mAllowPetCheckmark.setColorFilter(
                            ContextCompat.getColor(PostShareHouseActivity.this, R.color.silver_gray),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mAllowPet = false;
                    mHasWifiCheckmark.setColorFilter(
                            ContextCompat.getColor(PostShareHouseActivity.this, R.color.silver_gray),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mHasWifi = false;
                    mHasACCheckmark.setColorFilter(
                            ContextCompat.getColor(PostShareHouseActivity.this, R.color.silver_gray),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mHasAC = false;
                    mHasParkingCheckmark.setColorFilter(
                            ContextCompat.getColor(PostShareHouseActivity.this, R.color.silver_gray),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mHasParking = false;
                }
                mPricePerMonthOfOneText.setText("");
                mPricePerMonthOfOne = 0;
                mPricePerMonthOfOneUnit = "";

                mDescriptionShareHouseText.setText("");

                mShareHouseRequiredNumPeopleEditText.setText("");

                mShareHouseRequiredGenderSpinner.setSelection(0);

                mShareHouseRequiredWorkTypeSpinner.setSelection(0);

                mAllowSmokingCheckmark.setColorFilter(
                        ContextCompat.getColor(PostShareHouseActivity.this, R.color.silver_gray),
                        PorterDuff.Mode.SRC_ATOP
                );
                mAllowSmoking = false;

                mAllowAlcoholCheckmark.setColorFilter(
                        ContextCompat.getColor(PostShareHouseActivity.this, R.color.silver_gray),
                        PorterDuff.Mode.SRC_ATOP
                );
                mAllowAlcohol = false;

                mHasPrivateKeyCheckmark.setColorFilter(
                        ContextCompat.getColor(PostShareHouseActivity.this, R.color.silver_gray),
                        PorterDuff.Mode.SRC_ATOP
                );
                mHasPrivateKey = false;

                KeyboardHelper.hideSoftKeyboard(PostShareHouseActivity.this, true);
            }
        });

        mPostBarLayout = (RelativeLayout) findViewById(R.id.activity_post_house_post_bar);
        mCancelText = (TextView) mPostBarLayout.findViewById(R.id.activity_post_house_post_bar_cancel);
        mCancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(PostShareHouseActivity.this)
                        .setTitle(getString(R.string.activity_post_house_cancel_alert_title))
                        .setMessage(getString(R.string.activity_post_house_cancel_alert_message))
                        .setPositiveButton(getString(R.string.activity_post_house_cancel_alert_confirm),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                        .setNegativeButton(getString(R.string.activity_post_house_cancel_alert_cancel), null)
                        .show();
            }
        });
        mPostText = (TextView) mPostBarLayout.findViewById(R.id.activity_post_house_post_bar_post);
        mPostText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String errorMessage = getString(R.string.activity_post_house_post_error_message_missing);
                if (mIsPostNewShareHousing || (mIsEditShareHousing && Constants.CURRENT_USER.getUserID() == mToBePostedShareHousing.getCreator().getUserID()
                        && Constants.CURRENT_USER.getUserID() == mToBePostedShareHousing.getHousing().getOwner().getUserID())) {
                    if (mAllCurrentSelectedPhotoUris.size() == 0
                            || mHouseTypeText.getText().toString().equalsIgnoreCase(Constants.HOUSE_TYPES.get(0))
                            || mAreaEditText.getText().toString().isEmpty()
                            || mAddressDistrict.isEmpty()
                            || mAddressCity.isEmpty()
                            || mLatitude.isEmpty()
                            || mLongitude.isEmpty()
                            || mPriceText.getText().toString().isEmpty()
                            || mContactName.isEmpty()) {
                        if (mAllCurrentSelectedPhotoUris.size() == 0) {
                            errorMessage += getString(R.string.activity_post_house_post_error_message_missing_photo);
                        }
                        if (mHouseTitleText.getText().toString().isEmpty()) {
                            errorMessage += getString(R.string.activity_post_house_post_error_message_missing_house_title);
                        }
                        if (mHouseTypeText.getText().toString().equalsIgnoreCase(Constants.HOUSE_TYPES.get(0))) {
                            errorMessage += getString(R.string.activity_post_house_post_error_message_missing_house_type);
                        }
                        if (mAreaEditText.getText().toString().isEmpty()) {
                            errorMessage += getString(R.string.activity_post_house_post_error_message_missing_area);
                        }
                        if (mAddressDistrict.isEmpty()) {
                            errorMessage += getString(R.string.activity_post_house_post_error_message_missing_district);
                        }
                        if (mAddressCity.isEmpty()) {
                            errorMessage += getString(R.string.activity_post_house_post_error_message_missing_city);
                        }
                        if (mLatitude.isEmpty() || mLongitude.isEmpty()) {
                            errorMessage += getString(R.string.activity_post_house_address_error_message_missing_latitude_longitude) + ", ";
                        }
                        if (mPriceText.getText().toString().isEmpty()) {
                            errorMessage += getString(R.string.activity_post_house_post_error_message_missing_price);
                        }
                        if (mContactName.isEmpty()) {
                            errorMessage += getString(R.string.activity_post_house_post_error_message_missing_contact_name);
                        }
                        if (mDescriptionText.getText().toString().isEmpty()) {
                            errorMessage += getString(R.string.activity_post_house_post_error_message_missing_description_info);
                        }
                    }
                }
                if (mPricePerMonthOfOneText.getText().toString().isEmpty()
                        || mDescriptionShareHouseText.getText().toString().isEmpty()
                        || mShareHouseRequiredNumPeopleEditText.getText().toString().isEmpty()) {
                    if (mPricePerMonthOfOneText.getText().toString().isEmpty()) {
                        errorMessage += getString(R.string.activity_post_share_house_post_error_message_missing_price_per_month_of_one);
                    }
                    if (mDescriptionShareHouseText.getText().toString().isEmpty()) {
                        errorMessage += getString(R.string.activity_post_share_house_post_error_message_missing_description_info);
                    }
                    if (mShareHouseRequiredNumPeopleEditText.getText().toString().isEmpty()) {
                        errorMessage += getString(R.string.activity_post_share_house_post_error_message_missing_required_num_people);
                    }
                    errorMessage = errorMessage.substring(0, errorMessage.length() - 2);
                }
                if (!errorMessage.equalsIgnoreCase(getString(R.string.activity_post_house_post_error_message_missing))) {
                    ToastHelper.showCenterToast(getApplicationContext(), errorMessage, Toast.LENGTH_LONG);
                }
                if (mPrice == 0) {
                    errorMessage = getString(R.string.activity_post_house_post_error_message_zero_price);
                    ToastHelper.showCenterToast(getApplicationContext(), errorMessage, Toast.LENGTH_LONG);
                }
                if (mPricePerMonthOfOne == 0) {
                    errorMessage = getString(R.string.activity_post_house_post_error_message_zero_price_per_month_of_one);
                    ToastHelper.showCenterToast(getApplicationContext(), errorMessage, Toast.LENGTH_LONG);
                }
                if (errorMessage.equalsIgnoreCase(getString(R.string.activity_post_house_post_error_message_missing))) {
                    if (mIsPostNewShareHousing) {
                        uploadNewShareHousingData();
                    } else if (mIsFindRoommate) {
                        uploadFindingRoommateData();
                    } else if (mIsEditShareHousing) {
                        updateShareHousingData();
                    }
                }
            }
        });
        if (mIsFindRoommate) {
            if (mToBePostedHousing != null) {
                mHouseTitleText.setText(mToBePostedHousing.getTitle());
                mNumPeopleEditText.setText(String.valueOf(mToBePostedHousing.getNumOfPeople()));
                mNumRoomEditText.setText(String.valueOf(mToBePostedHousing.getNumOfRoom()));
                mNumBedEditText.setText(String.valueOf(mToBePostedHousing.getNumOfBed()));
                mNumBathEditText.setText(String.valueOf(mToBePostedHousing.getNumOfBath()));
                mHouseTypeText.setText(mToBePostedHousing.getHouseType());
                mAreaEditText.setText(Float.toString(mToBePostedHousing.getArea()));
                if (!TextUtils.isEmpty(mToBePostedHousing.getAddressHouseNumber())) {
                    mAddressHouseNumber = mToBePostedHousing.getAddressHouseNumber();
                    mAddressText.setText(mToBePostedHousing.getAddressHouseNumber() + ", ");
                }
                if (!TextUtils.isEmpty(mToBePostedHousing.getAddressStreet())) {
                    mAddressStreet = mToBePostedHousing.getAddressStreet();
                    mAddressText.setText(mAddressText.getText() + mAddressStreet + ", ");
                }
                if (!TextUtils.isEmpty(mToBePostedHousing.getAddressWard())) {
                    mAddressWard = mToBePostedHousing.getAddressWard();
                    mAddressText.setText(
                            mAddressText.getText()
//                            + getString(R.string.activity_post_house_detailed_item_address_ward_acronym)
                            + mToBePostedHousing.getAddressWard() + ", "
                    );
                }
                if (!TextUtils.isEmpty(mToBePostedHousing.getAddressDistrict())) {
                    mAddressDistrict = mToBePostedHousing.getAddressDistrict();
                    mAddressText.setText(
                            mAddressText.getText()
//                            + getString(R.string.activity_post_house_detailed_item_address_district_acronym)
                            + mToBePostedHousing.getAddressDistrict() + ", "
                    );
                }
                if (!TextUtils.isEmpty(mToBePostedHousing.getAddressCity())) {
                    mAddressCity = mToBePostedHousing.getAddressCity();
                    mAddressText.setText(mAddressText.getText() + mToBePostedHousing.getAddressCity());
                }
                if (mToBePostedHousing.getLatitude() != null && mToBePostedHousing.getLongitude() != null) {
                    mLatitude = mToBePostedHousing.getLatitude().toString();
                    mLongitude = mToBePostedHousing.getLongitude().toString();
                } else {
                    mLatitude = "";
                    mLongitude = "";
                }
                Pair<String, String> pair = HousePriceHelper.parseForHousing(mToBePostedHousing.getPrice(), this);
                if (pair.first == null) {
                    mPrice = 0;
                    mPriceUnit = pair.second;
                    mPriceText.setText(pair.second);
                } else {
                    mPrice = Float.valueOf(pair.first);
                    mPriceUnit = pair.second;
                    mPriceText.setText(pair.first + " " + pair.second);
                }
                mContactText.setText(
                        mToBePostedHousing.getOwner().getFirstName() + "-" +
                                mToBePostedHousing.getOwner().getPhoneNumber() + "-" +
                                mToBePostedHousing.getOwner().getEmail()
                );
                mDescriptionText.setText(mToBePostedHousing.getDescription());
                if (!TextUtils.isEmpty(mToBePostedHousing.getTimeRestriction())) {
                    mTimeRestrictionEditText.setText(mToBePostedHousing.getTimeRestriction());
                }
                if (mToBePostedHousing.isAllowPet()) {
                    mAllowPetCheckmark.setColorFilter(
                            ContextCompat.getColor(this, R.color.colorPrimary),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mAllowPet = true;
                } else {
                    mAllowPetCheckmark.setColorFilter(
                            ContextCompat.getColor(this, R.color.silver_gray),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mAllowPet = false;
                }
                if (mToBePostedHousing.hasWifi()) {
                    mHasWifiCheckmark.setColorFilter(
                            ContextCompat.getColor(this, R.color.colorPrimary),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mHasWifi = true;
                } else {
                    mHasWifiCheckmark.setColorFilter(
                            ContextCompat.getColor(this, R.color.silver_gray),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mHasWifi = false;
                }
                if (mToBePostedHousing.hasAC()) {
                    mHasACCheckmark.setColorFilter(
                            ContextCompat.getColor(this, R.color.colorPrimary),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mHasAC = true;
                } else {
                    mHasACCheckmark.setColorFilter(
                            ContextCompat.getColor(this, R.color.silver_gray),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mHasAC = false;
                }
                if (mToBePostedHousing.hasParking()) {
                    mHasParkingCheckmark.setColorFilter(
                            ContextCompat.getColor(this, R.color.colorPrimary),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mHasParking = true;
                } else {
                    mHasParkingCheckmark.setColorFilter(
                            ContextCompat.getColor(this, R.color.silver_gray),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mHasParking = false;
                }
                mPostText.setText(R.string.activity_post_house_find_roommate);
            }
        } else if (mIsEditShareHousing) {
            if (mToBePostedShareHousing != null) {
                mHouseTitleText.setText(mToBePostedShareHousing.getHousing().getTitle());
                mNumPeopleEditText.setText(String.valueOf(mToBePostedShareHousing.getHousing().getNumOfPeople()));
                mNumRoomEditText.setText(String.valueOf(mToBePostedShareHousing.getHousing().getNumOfRoom()));
                mNumBedEditText.setText(String.valueOf(mToBePostedShareHousing.getHousing().getNumOfBed()));
                mNumBathEditText.setText(String.valueOf(mToBePostedShareHousing.getHousing().getNumOfBath()));
                mHouseTypeText.setText(mToBePostedShareHousing.getHousing().getHouseType());
                mAreaEditText.setText(Float.toString(mToBePostedShareHousing.getHousing().getArea()));
                if (!TextUtils.isEmpty(mToBePostedShareHousing.getHousing().getAddressHouseNumber())) {
                    mAddressHouseNumber = mToBePostedShareHousing.getHousing().getAddressHouseNumber();
                    mAddressText.setText(mToBePostedShareHousing.getHousing().getAddressHouseNumber() + ", ");
                }
                if (!TextUtils.isEmpty(mToBePostedShareHousing.getHousing().getAddressStreet())) {
                    mAddressStreet = mToBePostedShareHousing.getHousing().getAddressStreet();
                    mAddressText.setText(mAddressText.getText() + mAddressStreet + ", ");
                }
                if (!TextUtils.isEmpty(mToBePostedShareHousing.getHousing().getAddressWard())) {
                    mAddressWard = mToBePostedShareHousing.getHousing().getAddressWard();
                    mAddressText.setText(
                            mAddressText.getText()
//                            + getString(R.string.activity_post_house_detailed_item_address_ward_acronym)
                            + mToBePostedShareHousing.getHousing().getAddressWard() + ", "
                    );
                }
                if (!TextUtils.isEmpty(mToBePostedShareHousing.getHousing().getAddressDistrict())) {
                    mAddressDistrict = mToBePostedShareHousing.getHousing().getAddressDistrict();
                    mAddressText.setText(
                            mAddressText.getText()
//                            + getString(R.string.activity_post_house_detailed_item_address_district_acronym)
                            + mToBePostedShareHousing.getHousing().getAddressDistrict() + ", "
                    );
                }
                if (!TextUtils.isEmpty(mToBePostedShareHousing.getHousing().getAddressCity())) {
                    mAddressCity = mToBePostedShareHousing.getHousing().getAddressCity();
                    mAddressText.setText(mAddressText.getText() + mToBePostedShareHousing.getHousing().getAddressCity());
                }
                Pair<String, String> pair = HousePriceHelper.parseForHousing(mToBePostedShareHousing.getHousing().getPrice(), this);
                if (pair.first == null) {
                    mPrice = 0;
                    mPriceUnit = pair.second;
                    mPriceText.setText(pair.second);
                } else {
                    mPrice = Float.valueOf(pair.first);
                    mPriceUnit = pair.second;
                    mPriceText.setText(pair.first + " " + pair.second);
                }
                mContactText.setText(
                        mToBePostedShareHousing.getHousing().getOwner().getFirstName() + "-" +
                                mToBePostedShareHousing.getHousing().getOwner().getPhoneNumber() + "-" +
                                mToBePostedShareHousing.getHousing().getOwner().getEmail()
                );
                mDescriptionText.setText(mToBePostedShareHousing.getHousing().getDescription());
                if (!TextUtils.isEmpty(mToBePostedShareHousing.getHousing().getTimeRestriction())) {
                    mTimeRestrictionEditText.setText(mToBePostedShareHousing.getHousing().getTimeRestriction());
                }
                if (mToBePostedShareHousing.getHousing().isAllowPet()) {
                    mAllowPetCheckmark.setColorFilter(
                            ContextCompat.getColor(this, R.color.colorPrimary),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mAllowPet = true;
                } else {
                    mAllowPetCheckmark.setColorFilter(
                            ContextCompat.getColor(this, R.color.silver_gray),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mAllowPet = false;
                }
                if (mToBePostedShareHousing.getHousing().hasWifi()) {
                    mHasWifiCheckmark.setColorFilter(
                            ContextCompat.getColor(this, R.color.colorPrimary),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mHasWifi = true;
                } else {
                    mHasWifiCheckmark.setColorFilter(
                            ContextCompat.getColor(this, R.color.silver_gray),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mHasWifi = false;
                }
                if (mToBePostedShareHousing.getHousing().hasAC()) {
                    mHasACCheckmark.setColorFilter(
                            ContextCompat.getColor(this, R.color.colorPrimary),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mHasAC = true;
                } else {
                    mHasACCheckmark.setColorFilter(
                            ContextCompat.getColor(this, R.color.silver_gray),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mHasAC = false;
                }
                if (mToBePostedShareHousing.getHousing().hasParking()) {
                    mHasParkingCheckmark.setColorFilter(
                            ContextCompat.getColor(this, R.color.colorPrimary),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mHasParking = true;
                } else {
                    mHasParkingCheckmark.setColorFilter(
                            ContextCompat.getColor(this, R.color.silver_gray),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mHasParking = false;
                }

                pair = HousePriceHelper.parseForShareHousing(mToBePostedShareHousing.getPricePerMonthOfOne(), this);
                if (pair.first == null) {
                    mPricePerMonthOfOne = 0;
                    mPricePerMonthOfOneUnit = pair.second;
                    mPricePerMonthOfOneText.setText(pair.second);
                } else {
                    mPricePerMonthOfOne = Float.valueOf(pair.first);
                    mPricePerMonthOfOneUnit = pair.second;
                    mPricePerMonthOfOneText.setText(pair.first + " " + pair.second);
                }
                mDescriptionShareHouseText.setText(mToBePostedShareHousing.getDescription());
                mShareHouseRequiredNumPeopleEditText.setText(String.valueOf(mToBePostedShareHousing.getRequiredNumOfPeople()));

                mShareHouseRequiredGenderSpinner.setSelection(
                        ((ArrayAdapter<String>) mShareHouseRequiredGenderSpinner.getAdapter())
                                .getPosition(mToBePostedShareHousing.getRequiredGender())
                );
                mShareHouseRequiredWorkTypeSpinner.setSelection(
                        ((ArrayAdapter<String>) mShareHouseRequiredWorkTypeSpinner.getAdapter())
                                .getPosition(mToBePostedShareHousing.getRequiredWorkType())
                );
                if (mToBePostedShareHousing.isAllowSmoking()) {
                    mAllowSmokingCheckmark.setColorFilter(
                            ContextCompat.getColor(this, R.color.colorPrimary),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mAllowSmoking = true;
                } else {
                    mAllowSmokingCheckmark.setColorFilter(
                            ContextCompat.getColor(this, R.color.silver_gray),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mAllowSmoking = false;
                }
                if (mToBePostedShareHousing.isAllowAlcohol()) {
                    mAllowAlcoholCheckmark.setColorFilter(
                            ContextCompat.getColor(this, R.color.colorPrimary),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mAllowAlcohol = true;
                } else {
                    mAllowAlcoholCheckmark.setColorFilter(
                            ContextCompat.getColor(this, R.color.silver_gray),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mAllowAlcohol = false;
                }
                if (mToBePostedShareHousing.hasPrivateKey()) {
                    mHasPrivateKeyCheckmark.setColorFilter(
                            ContextCompat.getColor(this, R.color.colorPrimary),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mHasPrivateKey = true;
                } else {
                    mHasPrivateKeyCheckmark.setColorFilter(
                            ContextCompat.getColor(this, R.color.silver_gray),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mHasPrivateKey = false;
                }
                mPostText.setText(R.string.activity_post_house_update);
            }
        }
        mDimmedView = findViewById(R.id.activity_post_house_dimmed_view);
        mDimmedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideBottomSlidingOverlayMenu();
            }
        });
        mBottomSlidingOverlayMenu = (LinearLayout) findViewById(R.id.activity_post_house_bottom_sliding_overlay_menu);
        mBottomSlidingOverlayMenu.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mBottomSlidingOverlayMenu.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mBottomSlidingOverlayMenu.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                mBottomSlidingOverlayMenuHeight = mBottomSlidingOverlayMenu.getHeight();
                mBottomSlidingOverlayMenu.setTranslationY(mBottomSlidingOverlayMenuHeight);
                mBottomSlidingOverlayMenu.setVisibility(View.GONE);
            }
        });
        mBottomSlidingOverlayMenuCamera = (TextView) findViewById(R.id.activity_post_house_bottom_sliding_overlay_menu_camera_option);
        mBottomSlidingOverlayMenuCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideBottomSlidingOverlayMenu();
                mPermissionHelper.capturePhotoWithCheckPermissions(getString(R.string.activity_post_house_camera_image_directory));
            }
        });
        mBottomSlidingOverlayMenuGallery = (TextView) findViewById(R.id.activity_post_house_bottom_sliding_overlay_menu_gallery_option);
        mBottomSlidingOverlayMenuGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideBottomSlidingOverlayMenu();

                FishBunCreator fishBunCreator = FishBun.with(PostShareHouseActivity.this)
                        .MultiPageMode()
                        .setCamera(true)
                        .setPickerSpanCount(Constants.ACTIVITY_POST_HOUSE_PHOTO_PICKER_NUM_COLUMN_COUNT)
                        .setActionBarColor(ContextCompat.getColor(PostShareHouseActivity.this, R.color.default_white),
                                ContextCompat.getColor(PostShareHouseActivity.this, R.color.default_white), true)
                        .setActionBarTitleColor(ContextCompat.getColor(PostShareHouseActivity.this, R.color.colorPrimary))
                        .setAlbumSpanCount(Constants.ACTIVITY_POST_HOUSE_PORTRAIT_ALBUM_PICKER_NUM_COLUMN_COUNT,
                                Constants.ACTIVITY_POST_HOUSE_LANDSCAPE_ALBUM_PICKER_NUM_COLUMN_COUNT)
                        .setButtonInAlbumActivity(false)
                        .exceptGif(true)
                        .setReachLimitAutomaticClose(false)
                        .setHomeAsUpIndicatorDrawable(ContextCompat.getDrawable(PostShareHouseActivity.this, R.drawable.ic_left_arrow))
                        .setOkButtonDrawable(ContextCompat.getDrawable(PostShareHouseActivity.this, R.drawable.ic_check_mark_thicker))
                        .setAllViewTitle(getString(R.string.activity_post_house_select_photo_all_photos))
                        .textOnImagesSelectionLimitReached(getString(R.string.activity_post_house_select_photo_limit_reached))
                        .textOnNothingSelected(getString(R.string.activity_post_house_select_photo_please_select_at_least_one));

                if (mIsChooseProfilePhoto) {
                    if (mAllCurrentSelectedPhotoUris.size() > 0) {
                        mProfilePhotoUri.clear();   // For User to select another profile photo
                        // to replace the current one easily
                        // without deselecting the current profile photo.
                        fishBunCreator
                                .setActionBarTitle(getString(R.string.activity_post_house_replace_profile_photo))
                                .setMaxCount(1)
                                .setArrayPaths(mProfilePhotoUri)
                                .startAlbum();
                    } else {
                        fishBunCreator
                                .setActionBarTitle(getString(R.string.activity_post_house_select_photo))
                                .setMaxCount(Constants.ACTIVITY_POST_HOUSE_NUM_PHOTOS_LIMIT)
                                .setArrayPaths(mAllCurrentSelectedPhotoUris)
                                .startAlbum();
                    }
                } else if (mIsReplaceNormalPhoto) {
                    mReplaceNormalPhotoUri.clear(); // For User to select another photo
                    // to replace the current one easily.
                    // without deselecting the current photo.
                    fishBunCreator
                            .setActionBarTitle(getString(R.string.activity_post_house_replace_photo))
                            .setMaxCount(1)
                            .setArrayPaths(mReplaceNormalPhotoUri)
                            .startAlbum();
                } else {
                    mPreviousAllCurrentSelectedPhotoUris.clear();
                    mPreviousAllCurrentSelectedPhotoUris = new ArrayList<>(mAllCurrentSelectedPhotoUris);
                    fishBunCreator
                            .setActionBarTitle(getString(R.string.activity_post_house_select_photo))
                            .setMaxCount(Constants.ACTIVITY_POST_HOUSE_NUM_PHOTOS_LIMIT)
                            .setArrayPaths(mAllCurrentSelectedPhotoUris)
                            .startAlbum();
                }
            }
        });
        mBottomSlidingOverlayMenuCancel = (TextView) findViewById(R.id.activity_post_house_bottom_sliding_overlay_menu_cancel_option);
        mBottomSlidingOverlayMenuCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideBottomSlidingOverlayMenu();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPhotoGridContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mPhotoGridContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mPhotoGridContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                mGridContainerWidthPixels = mPhotoGridContainer.getWidth();
                mGridItemMarginPixels = PostShareHouseActivity.this.getResources().getDimensionPixelSize(R.dimen.activity_post_house_add_photo_box_margin);
                mGridItemWidthPixels = (mGridContainerWidthPixels - mGridItemMarginPixels * 8) / 4;
            }
        });
    }

    @Override
    public void onBackPressed() {
        mCancelText.callOnClick();
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
//            Uri imageUri = Uri.parse(mPermissionHelper.getCameraHelper().getCurrentImagePath());
            Uri imageContentUri = FileHelper.getImageContentUriFromFile(
                    this, new File(
                            mPermissionHelper.getCameraHelper().getCurrentImagePath()
                                    .substring(5, mPermissionHelper.getCameraHelper().getCurrentImagePath().length())   // Remove "file:"
                    )
            );
            if (imageContentUri != null) {
                if (mAllCurrentSelectedPhotoUris.size() > 0) {
                    if (mCurrentSelectedGridItemIndex == 0) {
                        mProfilePhotoUri.set(0, mAllCurrentSelectedPhotoUris.get(0));
                    }
                    if (mCurrentSelectedGridItemIndex < mAllCurrentSelectedPhotoUris.size()) {
                        mAllCurrentSelectedPhotoUris.set(mCurrentSelectedGridItemIndex, imageContentUri);
                        replaceSelectedPhoto();
                    } else {
                        mAllCurrentSelectedPhotoUris.add(imageContentUri);
                        replaceSelectedPhoto();
                        inflateSelectMorePhotoBox();
                    }
                } else {
                    mProfilePhotoUri.add(imageContentUri);
                    mAllCurrentSelectedPhotoUris.add(imageContentUri);
                    replaceSelectedPhoto();
                    inflateSelectMorePhotoBox();
                }
            }
        }
        if (requestCode == Define.ALBUM_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (mIsChooseProfilePhoto) {
                    mProfilePhotoUri = data.getParcelableArrayListExtra(Define.INTENT_PATH);
                    if (mProfilePhotoUri.size() > 1) {
                        // User select Profile Photo for the 1st time (Not replacing).
                        // User could select multiple photos.
                        // -> Add all selected photo to all current selected photo list.
                        mAllCurrentSelectedPhotoUris = new ArrayList<>(mProfilePhotoUri);
                        mProfilePhotoUri.clear();
                        mProfilePhotoUri.add(mAllCurrentSelectedPhotoUris.get(0));

                        mPhotoGridContainer.removeViewAt(0);
                        mLoadPhotoIndex = 0;
                        loadAllSelectedPhoto();
                    } else if (mProfilePhotoUri.size() == 1) {
                        // User is replacing current Profile Photo.
                        if (mAllCurrentSelectedPhotoUris.size() > 0) {
                            mAllCurrentSelectedPhotoUris.set(0, mProfilePhotoUri.get(0));

                            replaceSelectedPhoto();
                        } else {    // User is selecting photo for the 1st time with only 1 photo.
                            mAllCurrentSelectedPhotoUris = new ArrayList<>(mProfilePhotoUri);

                            mPhotoGridContainer.removeViewAt(0);
                            mLoadPhotoIndex = 0;
                            loadAllSelectedPhoto();
                        }
                    }
                } else if (mIsReplaceNormalPhoto) {
                    mReplaceNormalPhotoUri = data.getParcelableArrayListExtra(Define.INTENT_PATH);
                    if (mReplaceNormalPhotoUri.size() > 0) {
                        mAllCurrentSelectedPhotoUris.set(mCurrentSelectedGridItemIndex, mReplaceNormalPhotoUri.get(0));
                        replaceSelectedPhoto();
                    }
                } else {    // Select More Normal Photo Option Selected.
                    mAllCurrentSelectedPhotoUris = data.getParcelableArrayListExtra(Define.INTENT_PATH);

                    if (mAllCurrentSelectedPhotoUris.size() > 0) {
                        mDifferentIndexes =
                                ListHelper.compare(mAllCurrentSelectedPhotoUris, mPreviousAllCurrentSelectedPhotoUris);

                        mProfilePhotoUri.set(0, mAllCurrentSelectedPhotoUris.get(0));

                        if (mDifferentIndexes.size() > 0) {
                            mIndexOfDifferentIndexesList = 0;
                            mLoadPhotoIndex = mDifferentIndexes.get(mIndexOfDifferentIndexesList);
                            loadPhotoOnAddMoreNormalPhotoOptionSelected();
                        }

                        if (mAllCurrentSelectedPhotoUris.size() < mPreviousAllCurrentSelectedPhotoUris.size()) {
                            for (int i = mAllCurrentSelectedPhotoUris.size();
                                 i < mPreviousAllCurrentSelectedPhotoUris.size(); ++i) {
                                mPhotoGridContainer.removeViewAt(i);
                            }
                            if (mPreviousAllCurrentSelectedPhotoUris.size()
                                    == Constants.ACTIVITY_POST_HOUSE_NUM_PHOTOS_LIMIT) {
                                // Add a 'select more normal photo box'
                                inflateSelectMorePhotoBox();
                            }
                            mPreviousAllCurrentSelectedPhotoUris.clear();
                            mPreviousAllCurrentSelectedPhotoUris = new ArrayList<>(mAllCurrentSelectedPhotoUris);
                        } else if (mAllCurrentSelectedPhotoUris.size() > mPreviousAllCurrentSelectedPhotoUris.size()) {
                            // Remove last item (Add more normal photo box).
                            mPhotoGridContainer.removeViewAt(mPhotoGridContainer.getChildCount() - 1);
                            mLoadPhotoIndex = mPreviousAllCurrentSelectedPhotoUris.size();
                            loadAllSelectedPhoto();
                        }
                    }
                }
            }
        } else if (requestCode == Constants.START_ACTIVITY_DETAILED_ITEM_HOUSE_TITLE_REQUEST) {
            if (resultCode == RESULT_OK) {
                mHouseTitleText.setText(data.getStringExtra(Constants.ACTIVITY_DETAILED_ITEM_RESULT));
            }
        } else if (requestCode == Constants.START_ACTIVITY_DETAILED_ITEM_HOUSE_TYPE_REQUEST) {
            if (resultCode == RESULT_OK) {
                mHouseTypeText.setText(data.getStringExtra(Constants.ACTIVITY_DETAILED_ITEM_RESULT));
            }
        } else if (requestCode == Constants.START_ACTIVITY_POST_HOUSE_ADDRESS_REQUEST) {
            if (resultCode == RESULT_OK) {
                mAddressHouseNumber = data.getStringExtra(Constants.START_ACTIVITY_POST_HOUSE_ADDRESS_REQUEST_HOUSE_NUMBER_RESULT);
                mAddressStreet = data.getStringExtra(Constants.START_ACTIVITY_POST_HOUSE_ADDRESS_REQUEST_STREET_RESULT);
                mAddressWard = data.getStringExtra(Constants.START_ACTIVITY_POST_HOUSE_ADDRESS_REQUEST_WARD_RESULT);
                mAddressDistrict = data.getStringExtra(Constants.START_ACTIVITY_POST_HOUSE_ADDRESS_REQUEST_DISTRICT_RESULT);
                mAddressCity = data.getStringExtra(Constants.START_ACTIVITY_POST_HOUSE_ADDRESS_REQUEST_CITY_RESULT);
                mLatitude = data.getStringExtra(Constants.START_ACTIVITY_POST_HOUSE_ADDRESS_REQUEST_LATITUDE_RESULT);
                mLongitude = data.getStringExtra(Constants.START_ACTIVITY_POST_HOUSE_ADDRESS_REQUEST_LONGITUDE_RESULT);
                mAddressText.setText("");
                if (!TextUtils.isEmpty(mAddressHouseNumber)) {
                    mAddressText.setText(mAddressHouseNumber + ", ");
                }
                if (!TextUtils.isEmpty(mAddressStreet)) {
                    mAddressText.setText(mAddressText.getText() + mAddressStreet + ", ");
                }
                if (!TextUtils.isEmpty(mAddressWard)) {
                    mAddressText.setText(
                            mAddressText.getText()
//                            + getString(R.string.activity_post_house_detailed_item_address_ward_acronym)
                            + mAddressWard + ", "
                    );
                }
                if (!TextUtils.isEmpty(mAddressDistrict)) {
                    mAddressText.setText(
                            mAddressText.getText()
//                            + getString(R.string.activity_post_house_detailed_item_address_district_acronym)
                            + mAddressDistrict + ", "
                    );
                }
                if (!TextUtils.isEmpty(mAddressCity)) {
                    mAddressText.setText(mAddressText.getText() + mAddressCity);
                }
            }
        } else if (requestCode == Constants.START_ACTIVITY_DETAILED_ITEM_HOUSE_DIRECTION_REQUEST) {
            if (resultCode == RESULT_OK) {
                mHouseDirectionText.setText(data.getStringExtra(Constants.ACTIVITY_DETAILED_ITEM_RESULT));
            }
        } else if (requestCode == Constants.START_ACTIVITY_DETAILED_ITEM_PRICE_REQUEST) {
            if (resultCode == RESULT_OK) {
                mPrice = data.getFloatExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_PRICE_RESULT, 0);
                mPriceUnit = data.getStringExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_PRICE_UNIT_RESULT);
                if (mPrice != 0) {
                    mPriceText.setText(mPrice + " " + mPriceUnit);
                } else {
                    mPriceText.setText(mPriceUnit);
                }
            }
        } else if (requestCode == Constants.START_ACTIVITY_DETAILED_ITEM_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                mContactName = data.getStringExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_CONTACT_NAME_RESULT);
                mContactNumber = data.getStringExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_CONTACT_NUMBER_RESULT);
                mContactEmail = data.getStringExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_CONTACT_EMAIL_RESULT);
                mContactText.setText(mContactName + "-" + mContactNumber + "-" + mContactEmail);
            }
        } else if (requestCode == Constants.START_ACTIVITY_DETAILED_ITEM_DESCRIPTION_REQUEST) {
            if (resultCode == RESULT_OK) {
                mDescriptionText.setText(data.getStringExtra(Constants.ACTIVITY_DETAILED_ITEM_RESULT));
            }
        } else if (requestCode == Constants.START_ACTIVITY_DETAILED_ITEM_PRICE_PER_MONTH_OF_ONE_REQUEST) {
            if (resultCode == RESULT_OK) {
                mPricePerMonthOfOne = data.getFloatExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_PRICE_RESULT, 0);
                mPricePerMonthOfOneUnit = data.getStringExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_PRICE_UNIT_RESULT);
                if (mPricePerMonthOfOne != 0) {
                    mPricePerMonthOfOneText.setText(mPricePerMonthOfOne + " " + mPricePerMonthOfOneUnit);
                } else {
                    mPricePerMonthOfOneText.setText(mPricePerMonthOfOneUnit);
                }
            }
        } else if (requestCode == Constants.START_ACTIVITY_DETAILED_ITEM_SHARE_DESCRIPTION_REQUEST) {
            if (resultCode == RESULT_OK) {
                mDescriptionShareHouseText.setText(data.getStringExtra(Constants.ACTIVITY_DETAILED_ITEM_RESULT));
            }
        }
    }

    private void loadAllSelectedPhoto() {
        if (mLoadPhotoIndex < mAllCurrentSelectedPhotoUris.size()) {
            Glide.with(this)
                    .load(mAllCurrentSelectedPhotoUris.get(mLoadPhotoIndex))
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(new SimpleTarget<Bitmap>(mGridItemWidthPixels, mGridItemWidthPixels) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            Drawable drawable = new BitmapDrawable(PostShareHouseActivity.this.getResources(), resource);
                            if (mLoadPhotoIndex == 0) {
                                inflateProfilePhoto(drawable);
                            } else {
                                // Inflate Normal Photo.
                                inflateNormalPhoto(drawable);

                                if (mLoadPhotoIndex < mAllCurrentSelectedPhotoUris.size() - 1) {
                                    ++mLoadPhotoIndex;
                                    loadAllSelectedPhoto();
                                } else if (mLoadPhotoIndex < Constants.ACTIVITY_POST_HOUSE_NUM_PHOTOS_LIMIT - 1) {
                                    // Add a 'select more normal photo box'.
                                    inflateSelectMorePhotoBox();
                                }
                            }
                        }
                    });
        } else {
            // User chose only 1 photo for the 1st time.
            // -> Add a 'select more normal photo box'.
            inflateSelectMorePhotoBox();
        }
    }

    private void replaceSelectedPhoto() {
        Glide.with(this)
                .load(mAllCurrentSelectedPhotoUris.get(mCurrentSelectedGridItemIndex))
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(new SimpleTarget<Bitmap>(mGridItemWidthPixels, mGridItemWidthPixels) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Drawable drawable = new BitmapDrawable(PostShareHouseActivity.this.getResources(), resource);
                        View item = mPhotoGridContainer.getChildAt(mCurrentSelectedGridItemIndex);
                        item.setBackground(drawable);
                        if (mCurrentSelectedGridItemIndex == 0) {
                            ImageView plusSign = (ImageView) item
                                    .findViewById(R.id.activity_post_house_add_profile_photo_plus_sign);
                            if (plusSign.getVisibility() == View.VISIBLE) {
                                plusSign.setVisibility(View.GONE);
                            }
                        } else {
                            ImageView plusSign = (ImageView) item
                                    .findViewById(R.id.activity_post_house_add_normal_photo_plus_sign);
                            if (plusSign.getVisibility() == View.VISIBLE) {
                                plusSign.setVisibility(View.GONE);
                            }
                            ImageView removeButton = (ImageView) item
                                            .findViewById(R.id.activity_post_house_add_normal_photo_remove_button);
                            if (removeButton.getVisibility() == View.GONE) {
                                removeButton.setVisibility(View.VISIBLE);
                            }
                            if (!removeButton.hasOnClickListeners()) {
                                removeButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int itemIndex = (int) ((RelativeLayout) v.getParent()).getTag();
                                        mPhotoGridContainer.removeViewAt(itemIndex);
                                        mAllCurrentSelectedPhotoUris.remove(itemIndex);

                                        for (int i = 0; i < mPhotoGridContainer.getChildCount(); ++i) {
                                            mPhotoGridContainer.getChildAt(i).setTag(i);
                                        }

                                        if (mAllCurrentSelectedPhotoUris.size()
                                                == Constants.ACTIVITY_POST_HOUSE_NUM_PHOTOS_LIMIT - 1) {
                                            RelativeLayout addMoreNormalPhotoGridItem =
                                                    (RelativeLayout) mLayoutInflater.inflate(
                                                            R.layout.activity_post_house_add_normal_photo, null);
                                            addMoreNormalPhotoGridItem
                                                    .setOnClickListener(mAddMoreNormalPhotoGridItemClickListener);

                                            mPhotoGridItemLayoutParams = new GridLayout.LayoutParams();
                                            mPhotoGridItemLayoutParams.setMargins(
                                                    mGridItemMarginPixels,
                                                    mGridItemMarginPixels,
                                                    mGridItemMarginPixels,
                                                    mGridItemMarginPixels
                                            );
                                            mPhotoGridItemLayoutParams.width = mGridItemWidthPixels;
                                            mPhotoGridItemLayoutParams.height = mGridItemWidthPixels;

                                            addMoreNormalPhotoGridItem.setLayoutParams(mPhotoGridItemLayoutParams);

                                            // Set Tag as Current Index of it in Array List of Uris and PhotoGridContainer.
                                            addMoreNormalPhotoGridItem.setTag(mPhotoGridContainer.getChildCount());
                                            mPhotoGridContainer.addView(addMoreNormalPhotoGridItem);
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
    }

    private void loadPhotoOnAddMoreNormalPhotoOptionSelected() {
        if (mLoadPhotoIndex < mAllCurrentSelectedPhotoUris.size()) {
            Glide.with(this)
                    .load(mAllCurrentSelectedPhotoUris.get(mLoadPhotoIndex))
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(new SimpleTarget<Bitmap>(mGridItemWidthPixels, mGridItemWidthPixels) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            Drawable drawable = new BitmapDrawable(PostShareHouseActivity.this.getResources(), resource);
                            mPhotoGridContainer.getChildAt(mLoadPhotoIndex).setBackground(drawable);

                            if (++mIndexOfDifferentIndexesList < mDifferentIndexes.size()) {
                                mLoadPhotoIndex = mDifferentIndexes.get(mIndexOfDifferentIndexesList);
                                loadPhotoOnAddMoreNormalPhotoOptionSelected();
                            }
                        }
                    });
        }
    }

    private void inflateInitialAddProfilePhotoBox() {
        RelativeLayout addProfilePhotoGridItem =
                (RelativeLayout) mLayoutInflater.inflate(
                        R.layout.activity_post_house_add_profile_photo, null);
        addProfilePhotoGridItem.setOnClickListener(mProfilePhotoGridItemClickListener);

        mPhotoGridItemLayoutParams = new GridLayout.LayoutParams();
        mPhotoGridItemLayoutParams.setMargins(
                mGridItemMarginPixels,
                mGridItemMarginPixels,
                mGridItemMarginPixels,
                mGridItemMarginPixels
        );
        mPhotoGridItemLayoutParams.width = mGridItemWidthPixels;
        mPhotoGridItemLayoutParams.height = mGridItemWidthPixels;

        addProfilePhotoGridItem.setLayoutParams(mPhotoGridItemLayoutParams);

        // Set Tag as Current Index of it in Array List of Uris and PhotoGridContainer.
        addProfilePhotoGridItem.setTag(mPhotoGridContainer.getChildCount());
        mPhotoGridContainer.addView(addProfilePhotoGridItem);
    }

    private void inflateProfilePhoto(Drawable drawable) {
        RelativeLayout profilePhotoGridItem =
                (RelativeLayout) mLayoutInflater.inflate(
                        R.layout.activity_post_house_add_profile_photo, null);
        profilePhotoGridItem.setOnClickListener(mProfilePhotoGridItemClickListener);
        profilePhotoGridItem.setBackground(drawable);
        profilePhotoGridItem
                .findViewById(R.id.activity_post_house_add_profile_photo_plus_sign)
                .setVisibility(View.GONE);

        mPhotoGridItemLayoutParams = new GridLayout.LayoutParams();
        mPhotoGridItemLayoutParams.setMargins(
                mGridItemMarginPixels,
                mGridItemMarginPixels,
                mGridItemMarginPixels,
                mGridItemMarginPixels
        );
        mPhotoGridItemLayoutParams.width = mGridItemWidthPixels;
        mPhotoGridItemLayoutParams.height = mGridItemWidthPixels;

        profilePhotoGridItem.setLayoutParams(mPhotoGridItemLayoutParams);

        // Set Tag as Current Index of it in Array List of Uris and PhotoGridContainer.
        profilePhotoGridItem.setTag(mPhotoGridContainer.getChildCount());
        mPhotoGridContainer.addView(profilePhotoGridItem);

        ++mLoadPhotoIndex;
        loadAllSelectedPhoto();
    }

    private void inflateNormalPhoto(Drawable drawable) {
        RelativeLayout normalPhotoGridItem =
                (RelativeLayout) mLayoutInflater
                        .inflate(R.layout.activity_post_house_add_normal_photo, null);
        normalPhotoGridItem.setOnClickListener(mReplaceNormalPhotoGridItemClickListener);
        normalPhotoGridItem.setBackground(drawable);
        normalPhotoGridItem
                .findViewById(R.id.activity_post_house_add_normal_photo_plus_sign)
                .setVisibility(View.GONE);
        ImageView removeButton =
                (ImageView) normalPhotoGridItem
                        .findViewById(R.id.activity_post_house_add_normal_photo_remove_button);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemIndex = (int) ((RelativeLayout) v.getParent()).getTag();
                mPhotoGridContainer.removeViewAt(itemIndex);
                mAllCurrentSelectedPhotoUris.remove(itemIndex);

                for (int i = 0; i < mPhotoGridContainer.getChildCount(); ++i) {
                    mPhotoGridContainer.getChildAt(i).setTag(i);
                }

                if (mAllCurrentSelectedPhotoUris.size()
                        == Constants.ACTIVITY_POST_HOUSE_NUM_PHOTOS_LIMIT - 1) {
                    RelativeLayout addMoreNormalPhotoGridItem =
                            (RelativeLayout) mLayoutInflater.inflate(
                                    R.layout.activity_post_house_add_normal_photo, null);
                    addMoreNormalPhotoGridItem
                            .setOnClickListener(mAddMoreNormalPhotoGridItemClickListener);

                    mPhotoGridItemLayoutParams = new GridLayout.LayoutParams();
                    mPhotoGridItemLayoutParams.setMargins(
                            mGridItemMarginPixels,
                            mGridItemMarginPixels,
                            mGridItemMarginPixels,
                            mGridItemMarginPixels
                    );
                    mPhotoGridItemLayoutParams.width = mGridItemWidthPixels;
                    mPhotoGridItemLayoutParams.height = mGridItemWidthPixels;

                    addMoreNormalPhotoGridItem.setLayoutParams(mPhotoGridItemLayoutParams);

                    // Set Tag as Current Index of it in Array List of Uris and PhotoGridContainer.
                    addMoreNormalPhotoGridItem.setTag(mPhotoGridContainer.getChildCount());
                    mPhotoGridContainer.addView(addMoreNormalPhotoGridItem);
                }
            }
        });
        removeButton.setVisibility(View.VISIBLE);

        mPhotoGridItemLayoutParams = new GridLayout.LayoutParams();
        mPhotoGridItemLayoutParams.setMargins(
                mGridItemMarginPixels,
                mGridItemMarginPixels,
                mGridItemMarginPixels,
                mGridItemMarginPixels
        );
        mPhotoGridItemLayoutParams.width = mGridItemWidthPixels;
        mPhotoGridItemLayoutParams.height = mGridItemWidthPixels;

        normalPhotoGridItem.setLayoutParams(mPhotoGridItemLayoutParams);

        // Set Tag as Current Index of it in Array List of Uris and PhotoGridContainer.
        normalPhotoGridItem.setTag(mPhotoGridContainer.getChildCount());
        mPhotoGridContainer.addView(normalPhotoGridItem);
    }

    private void inflateSelectMorePhotoBox() {
        RelativeLayout addMoreNormalPhotoGridItem =
                (RelativeLayout) mLayoutInflater.inflate(
                        R.layout.activity_post_house_add_normal_photo, null);
        addMoreNormalPhotoGridItem
                .setOnClickListener(mAddMoreNormalPhotoGridItemClickListener);

        mPhotoGridItemLayoutParams = new GridLayout.LayoutParams();
        mPhotoGridItemLayoutParams.setMargins(
                mGridItemMarginPixels,
                mGridItemMarginPixels,
                mGridItemMarginPixels,
                mGridItemMarginPixels
        );
        mPhotoGridItemLayoutParams.width = mGridItemWidthPixels;
        mPhotoGridItemLayoutParams.height = mGridItemWidthPixels;

        addMoreNormalPhotoGridItem.setLayoutParams(mPhotoGridItemLayoutParams);

        // Set Tag as Current Index of it in Array List of Uris and PhotoGridContainer.
        addMoreNormalPhotoGridItem.setTag(mPhotoGridContainer.getChildCount());
        mPhotoGridContainer.addView(addMoreNormalPhotoGridItem);
    }

    private void showBottomSlidingOverlayMenu() {
        if (!mIsBottomSlidingOverlayMenuAnimationRunning) {
            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator translateAnimY = ObjectAnimator.ofFloat(mBottomSlidingOverlayMenu, View.TRANSLATION_Y, mBottomSlidingOverlayMenuHeight, 0.0f);
            translateAnimY.setDuration(300);
            translateAnimY.setInterpolator(new DecelerateInterpolator());
            translateAnimY.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mIsBottomSlidingOverlayMenuAnimationRunning = true;
                    mDimmedView.setVisibility(View.VISIBLE);
                    mBottomSlidingOverlayMenu.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mIsBottomSlidingOverlayMenuAnimationRunning = false;
                }
            });
            translateAnimY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Float newValue = (Float) animation.getAnimatedValue();

                    mDimmedView.setAlpha(((mBottomSlidingOverlayMenuHeight - newValue) / mBottomSlidingOverlayMenuHeight) * 0.5f);
                }
            });
            animatorSet.play(translateAnimY);
            animatorSet.start();
        }
    }

    private void hideBottomSlidingOverlayMenu() {
        if (!mIsBottomSlidingOverlayMenuAnimationRunning) {
            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator translateAnimY = ObjectAnimator.ofFloat(mBottomSlidingOverlayMenu, View.TRANSLATION_Y, 0.0f, mBottomSlidingOverlayMenuHeight);
            translateAnimY.setDuration(300);
            translateAnimY.setInterpolator(new DecelerateInterpolator());
            translateAnimY.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mIsBottomSlidingOverlayMenuAnimationRunning = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mBottomSlidingOverlayMenu.setVisibility(View.GONE);
                    mDimmedView.setVisibility(View.GONE);
                    mIsBottomSlidingOverlayMenuAnimationRunning = false;
                }
            });
            translateAnimY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Float newValue = (Float) animation.getAnimatedValue();

                    mDimmedView.setAlpha(((mBottomSlidingOverlayMenuHeight - newValue) / mBottomSlidingOverlayMenuHeight) * 0.5f);
                }
            });
            animatorSet.play(translateAnimY);
            animatorSet.start();
        }
    }

    private void uploadNewShareHousingData() {
        if (Constants.CURRENT_USER != null) {
            final ArrayList<String> photoURIStrings = new ArrayList<>();
            for (Uri photoUri : mAllCurrentSelectedPhotoUris) {
                photoURIStrings.add(photoUri.toString());
            }
            int price = HousePriceHelper.convertForHousing(mPrice, mPriceUnit, this);
            mToBePostedHousing = new Housing(
                    -1, photoURIStrings, mHouseTitleText.getText().toString(),
                    Constants.CURRENT_USER, price, true,
                    mHouseTypeText.getText().toString(), new Date(), 0, 0,
                    mNumPeopleEditText.getText().toString().isEmpty()
                            ? -1 : Integer.parseInt(mNumPeopleEditText.getText().toString()),
                    mNumRoomEditText.getText().toString().isEmpty()
                            ? -1 : Integer.parseInt(mNumRoomEditText.getText().toString()),
                    mNumBedEditText.getText().toString().isEmpty()
                            ? -1 : Integer.parseInt(mNumBedEditText.getText().toString()),
                    mNumBathEditText.getText().toString().isEmpty()
                            ? -1 : Integer.parseInt(mNumBathEditText.getText().toString()),
                    mAllowPet, mHasWifi, mHasAC, mHasParking,
                    mTimeRestrictionEditText.getText().toString(),
                    Float.parseFloat(mAreaEditText.getText().toString()),
                    new BigDecimal(mLatitude), new BigDecimal(mLongitude),
                    mAddressHouseNumber, mAddressStreet, mAddressWard, mAddressDistrict, mAddressCity,
                    mDescriptionText.getText().toString(), 0
            );
            int pricePerMonthOfOne = HousePriceHelper
                    .convertForShareHousing(mPricePerMonthOfOne, mPricePerMonthOfOneUnit, this);
            mToBePostedShareHousing = new ShareHousing(
                    -1, mToBePostedHousing, Constants.CURRENT_USER, true, pricePerMonthOfOne,
                    mDescriptionShareHouseText.getText().toString(), 0, 0,
                    Integer.parseInt(mShareHouseRequiredNumPeopleEditText.getText().toString()),
                    mShareHouseRequiredGenderSpinner.getSelectedItem().toString(),
                    mShareHouseRequiredWorkTypeSpinner.getSelectedItem().toString(),
                    mAllowSmoking, mAllowAlcohol, mHasPrivateKey, new Date()
            );
            FirebaseStorageHelper.postNewShareHousing(
                    this, mToBePostedShareHousing,
                    mAllCurrentSelectedPhotoUris,
                    new OnShareHousingPostingListener() {
                        @Override
                        public void onPostSuccess(ShareHousing shareHousing) {
//                            mToBePostedShareHousing = shareHousing;
//                            Gson gson = new Gson();
//                            Intent returnIntent = new Intent();
//                            returnIntent.putExtra(
//                                    Constants.START_ACTIVITY_POST_SHARE_HOUSE_REQUEST_POSTED_SHARE_HOUSING_RESULT,
//                                    gson.toJson(mToBePostedShareHousing)
//                            );
//                            setResult(RESULT_OK, returnIntent);
                            if (shareHousing != null) {
                                ShareSpaceApplication.BUS.post(new PostShareHousingEvent(shareHousing));
                                finish();
                            } else {
                                ToastHelper.showCenterToast(
                                        getApplicationContext(),
                                        R.string.activity_post_share_house_cannot_upload_share_house_info,
                                        Toast.LENGTH_LONG
                                );
                            }
                        }

                        @Override
                        public void onPostFailure(Throwable t) {
                            ToastHelper.showCenterToast(
                                    getApplicationContext(),
                                    R.string.activity_post_share_house_cannot_upload_share_house_info,
                                    Toast.LENGTH_LONG
                            );
                            RetrofitClient.showShareSpaceServerConnectionErrorDialog(PostShareHouseActivity.this, t);
                        }
                    }
            );
        }
    }

    private void uploadFindingRoommateData() {
        if (Constants.CURRENT_USER != null) {
            int pricePerMonthOfOne = HousePriceHelper
                    .convertForShareHousing(mPricePerMonthOfOne, mPricePerMonthOfOneUnit, this);
            mToBePostedShareHousing = new ShareHousing(
                    -1, mToBePostedHousing, Constants.CURRENT_USER, true, pricePerMonthOfOne,
                    mDescriptionShareHouseText.getText().toString(), 0, 0,
                    Integer.parseInt(mShareHouseRequiredNumPeopleEditText.getText().toString()),
                    mShareHouseRequiredGenderSpinner.getSelectedItem().toString(),
                    mShareHouseRequiredWorkTypeSpinner.getSelectedItem().toString(),
                    mAllowSmoking, mAllowAlcohol, mHasPrivateKey, new Date()
            );
            FirebaseStorageHelper.postShareOfExistHousing(
                    this, mToBePostedShareHousing,
                    new OnShareHousingPostingListener() {
                        @Override
                        public void onPostSuccess(ShareHousing shareHousing) {
//                            mToBePostedShareHousing = shareHousing;
//                            Gson gson = new Gson();
//                            Intent returnIntent = new Intent();
//                            returnIntent.putExtra(
//                                    Constants.START_ACTIVITY_POST_SHARE_HOUSE_REQUEST_POSTED_SHARE_HOUSING_RESULT,
//                                    gson.toJson(mToBePostedShareHousing)
//                            );
//                            setResult(RESULT_OK, returnIntent);
                            if (shareHousing != null) {
                                ShareSpaceApplication.BUS.post(new PostShareHousingEvent(shareHousing));
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                ToastHelper.showCenterToast(
                                        getApplicationContext(),
                                        R.string.activity_post_share_house_cannot_upload_share_house_info,
                                        Toast.LENGTH_LONG
                                );
                            }
                        }

                        @Override
                        public void onPostFailure(Throwable t) {
                            ToastHelper.showCenterToast(
                                    getApplicationContext(),
                                    R.string.activity_post_share_house_cannot_upload_share_house_info,
                                    Toast.LENGTH_LONG
                            );
                            RetrofitClient.showShareSpaceServerConnectionErrorDialog(PostShareHouseActivity.this, t);
                        }
                    }
            );
        }
    }

    private void updateShareHousingData() {
        if (Constants.CURRENT_USER != null) {
            if (Constants.CURRENT_USER.getUserID() == mToBePostedShareHousing.getCreator().getUserID()
                    && Constants.CURRENT_USER.getUserID() == mToBePostedShareHousing.getHousing().getOwner().getUserID()) {
                final ArrayList<String> photoURIStrings = new ArrayList<>();
                for (Uri photoUri : mAllCurrentSelectedPhotoUris) {
                    photoURIStrings.add(photoUri.toString());
                }
                int price = HousePriceHelper.convertForHousing(mPrice, mPriceUnit, this);
                mToBePostedHousing = new Housing(
                        mToBePostedShareHousing.getHousing().getID(), photoURIStrings, mHouseTitleText.getText().toString(),
                        Constants.CURRENT_USER, price, true,
                        mHouseTypeText.getText().toString(), new Date(), mToBePostedShareHousing.getHousing().getNumOfView(),
                        mToBePostedShareHousing.getHousing().getNumOfSaved(),
                        mNumPeopleEditText.getText().toString().isEmpty()
                                ? -1 : Integer.parseInt(mNumPeopleEditText.getText().toString()),
                        mNumRoomEditText.getText().toString().isEmpty()
                                ? -1 : Integer.parseInt(mNumRoomEditText.getText().toString()),
                        mNumBedEditText.getText().toString().isEmpty()
                                ? -1 : Integer.parseInt(mNumBedEditText.getText().toString()),
                        mNumBathEditText.getText().toString().isEmpty()
                                ? -1 : Integer.parseInt(mNumBathEditText.getText().toString()),
                        mAllowPet, mHasWifi, mHasAC, mHasParking,
                        mTimeRestrictionEditText.getText().toString(),
                        Float.parseFloat(mAreaEditText.getText().toString()),
                        new BigDecimal(mLatitude), new BigDecimal(mLongitude),
                        mAddressHouseNumber, mAddressStreet, mAddressWard, mAddressDistrict, mAddressCity,
                        mDescriptionText.getText().toString(), 0
                );
                int pricePerMonthOfOne = HousePriceHelper
                        .convertForShareHousing(mPricePerMonthOfOne, mPricePerMonthOfOneUnit, this);
                mToBePostedShareHousing = new ShareHousing(
                        mToBePostedShareHousing.getID(), mToBePostedHousing, Constants.CURRENT_USER, true, pricePerMonthOfOne,
                        mDescriptionShareHouseText.getText().toString(), mToBePostedShareHousing.getNumOfView(),
                        mToBePostedShareHousing.getNumOfSaved(),
                        Integer.parseInt(mShareHouseRequiredNumPeopleEditText.getText().toString()),
                        mShareHouseRequiredGenderSpinner.getSelectedItem().toString(),
                        mShareHouseRequiredWorkTypeSpinner.getSelectedItem().toString(),
                        mAllowSmoking, mAllowAlcohol, mHasPrivateKey, new Date()
                );
                FirebaseStorageHelper.updateNewShareHousing(
                        this, mToBePostedShareHousing,
                        mAllCurrentSelectedPhotoUris,
                        new OnShareHousingUpdatingListener() {
                            @Override
                            public void onUpdateComplete(Boolean isUpdated) {
//                            mToBePostedShareHousing = shareHousing;
//                            Gson gson = new Gson();
//                            Intent returnIntent = new Intent();
//                            returnIntent.putExtra(
//                                    Constants.START_ACTIVITY_POST_SHARE_HOUSE_REQUEST_POSTED_SHARE_HOUSING_RESULT,
//                                    gson.toJson(mToBePostedShareHousing)
//                            );
//                            setResult(RESULT_OK, returnIntent);
                                if (isUpdated) {
                                    ShareSpaceApplication.BUS.post(new UpdateShareHousingEvent(mToBePostedShareHousing));
                                    Intent returnIntent = new Intent();
                                    Gson gson = new Gson();
                                    returnIntent.putExtra(
                                            Constants.ACTIVITY_SHARE_HOUSING_DETAIL_SHARE_HOUSING_EXTRA,
                                            gson.toJson(mToBePostedShareHousing)
                                    );
                                    setResult(RESULT_OK, returnIntent);
                                    finish();
                                } else {
                                    ToastHelper.showCenterToast(
                                            getApplicationContext(),
                                            R.string.activity_post_share_house_cannot_update_share_house_info,
                                            Toast.LENGTH_LONG
                                    );
                                }
                            }

                            @Override
                            public void onUpdateFailure(Throwable t) {
                                ToastHelper.showCenterToast(
                                        getApplicationContext(),
                                        R.string.activity_post_share_house_cannot_update_share_house_info,
                                        Toast.LENGTH_LONG
                                );
                                RetrofitClient.showShareSpaceServerConnectionErrorDialog(PostShareHouseActivity.this, t);
                            }
                        }
                );
            } else {
                int pricePerMonthOfOne = HousePriceHelper
                        .convertForShareHousing(mPricePerMonthOfOne, mPricePerMonthOfOneUnit, this);
                mToBePostedShareHousing = new ShareHousing(
                        mToBePostedShareHousing.getID(), mToBePostedShareHousing.getHousing(), Constants.CURRENT_USER, true, pricePerMonthOfOne,
                        mDescriptionShareHouseText.getText().toString(), mToBePostedShareHousing.getNumOfView(),
                        mToBePostedShareHousing.getNumOfSaved(),
                        Integer.parseInt(mShareHouseRequiredNumPeopleEditText.getText().toString()),
                        mShareHouseRequiredGenderSpinner.getSelectedItem().toString(),
                        mShareHouseRequiredWorkTypeSpinner.getSelectedItem().toString(),
                        mAllowSmoking, mAllowAlcohol, mHasPrivateKey, new Date()
                );
                FirebaseStorageHelper.updateShareHousing(
                        this, mToBePostedShareHousing,
                        new OnShareHousingUpdatingListener() {
                            @Override
                            public void onUpdateComplete(Boolean isUpdated) {
//                            mToBePostedShareHousing = shareHousing;
//                            Gson gson = new Gson();
//                            Intent returnIntent = new Intent();
//                            returnIntent.putExtra(
//                                    Constants.START_ACTIVITY_POST_SHARE_HOUSE_REQUEST_POSTED_SHARE_HOUSING_RESULT,
//                                    gson.toJson(mToBePostedShareHousing)
//                            );
//                            setResult(RESULT_OK, returnIntent);
                                if (isUpdated) {
                                    ShareSpaceApplication.BUS.post(new UpdateShareHousingEvent(mToBePostedShareHousing));
                                    Intent returnIntent = new Intent();
                                    Gson gson = new Gson();
                                    returnIntent.putExtra(
                                            Constants.ACTIVITY_SHARE_HOUSING_DETAIL_SHARE_HOUSING_EXTRA,
                                            gson.toJson(mToBePostedShareHousing)
                                    );
                                    setResult(RESULT_OK, returnIntent);
                                    finish();
                                } else {
                                    ToastHelper.showCenterToast(
                                            getApplicationContext(),
                                            R.string.activity_post_share_house_cannot_update_share_house_info,
                                            Toast.LENGTH_LONG
                                    );
                                }
                            }

                            @Override
                            public void onUpdateFailure(Throwable t) {
                                ToastHelper.showCenterToast(
                                        getApplicationContext(),
                                        R.string.activity_post_share_house_cannot_update_share_house_info,
                                        Toast.LENGTH_LONG
                                );
                                RetrofitClient.showShareSpaceServerConnectionErrorDialog(PostShareHouseActivity.this, t);
                            }
                        }
                );
            }
        }
    }
}
