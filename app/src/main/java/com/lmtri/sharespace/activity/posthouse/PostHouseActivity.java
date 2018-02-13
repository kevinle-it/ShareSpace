package com.lmtri.sharespace.activity.posthouse;

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
import com.lmtri.sharespace.api.model.Housing;
import com.lmtri.sharespace.api.service.RetrofitClient;
import com.lmtri.sharespace.customview.CustomEditText;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.FileHelper;
import com.lmtri.sharespace.helper.HousePriceHelper;
import com.lmtri.sharespace.helper.KeyboardHelper;
import com.lmtri.sharespace.helper.ListHelper;
import com.lmtri.sharespace.helper.PermissionHelper;
import com.lmtri.sharespace.helper.ToastHelper;
import com.lmtri.sharespace.helper.busevent.housing.post.PostHousingEvent;
import com.lmtri.sharespace.helper.busevent.housing.post.UpdateHousingEvent;
import com.lmtri.sharespace.helper.firebasestorage.FirebaseStorageHelper;
import com.lmtri.sharespace.helper.firebasestorage.housing.OnHousingPostingListener;
import com.lmtri.sharespace.helper.firebasestorage.housing.OnHousingUpdatingListener;
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

public class PostHouseActivity extends AppCompatActivity {
    public static final String TAG = PostHouseActivity.class.getSimpleName();

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

    private boolean mIsPostNewHousing = false;
    private boolean mIsEditHousing = false;

    private RelativeLayout mResetInputLayout;

    private RelativeLayout mPostBarLayout;
    private TextView mCancelText;
    private TextView mPostText;

    private Housing mToBePostedHousing = null;

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
        setContentView(R.layout.activity_post_house);

        mIsPostNewHousing = getIntent().getBooleanExtra(Constants.IS_POST_NEW_HOUSING_EXTRA, false);
        mIsEditHousing = getIntent().getBooleanExtra(Constants.IS_EDIT_HOUSING_EXTRA, false);
        if (mIsEditHousing) {
            Gson gson = new Gson();
            try {
                mToBePostedHousing = gson.fromJson(
                        getIntent().getStringExtra(Constants.HOUSING_INFO_FOR_EDITING_POST_EXTRA),
                        Housing.class
                );
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }

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
                mGridItemMarginPixels = PostHouseActivity.this.getResources().getDimensionPixelSize(R.dimen.activity_post_house_add_photo_box_margin);
                mGridItemWidthPixels = (mGridContainerWidthPixels - mGridItemMarginPixels * 8) / 4;

                if (mIsPostNewHousing) {
                    inflateInitialAddProfilePhotoBox();
                } else {
                    for (String url : mToBePostedHousing.getPhotoURLs()) {
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
                Intent intent = new Intent(PostHouseActivity.this, PostHouseDetailedItemActivity.class);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_TOOLBAR_TITLE_EXTRA, Constants.ACTIVITY_HOUSE_TITLE_TOOLBAR_TITLE);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_DETAILS_EXTRA, mHouseTitleText.getText());
                startActivityForResult(intent, Constants.START_ACTIVITY_DETAILED_ITEM_HOUSE_TITLE_REQUEST);
                overridePendingTransition(R.anim.push_left_in, R.anim.scale_down_out);

                KeyboardHelper.hideSoftKeyboard(PostHouseActivity.this, true);
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
                    KeyboardHelper.hideSoftKeyboard(PostHouseActivity.this, true);
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
                Intent intent = new Intent(PostHouseActivity.this, PostHouseDetailedItemActivity.class);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_TOOLBAR_TITLE_EXTRA, Constants.ACTIVITY_HOUSE_TYPE_TOOLBAR_TITLE);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_DETAILS_EXTRA, mHouseTypeText.getText());
                startActivityForResult(intent, Constants.START_ACTIVITY_DETAILED_ITEM_HOUSE_TYPE_REQUEST);
                overridePendingTransition(R.anim.push_left_in, R.anim.scale_down_out);

                KeyboardHelper.hideSoftKeyboard(PostHouseActivity.this, true);
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
//                Intent intent = new Intent(PostHouseActivity.this, PostHouseDetailedItemActivity.class);
//                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_TOOLBAR_TITLE_EXTRA, Constants.ACTIVITY_ADDRESS_TOOLBAR_TITLE);
//                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_ADDRESS_HOUSE_NUMBER_EXTRA, mAddressHouseNumber);
//                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_ADDRESS_STREET_EXTRA, mAddressStreet);
//                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_ADDRESS_WARD_EXTRA, mAddressWard);
//                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_ADDRESS_DISTRICT_EXTRA, mAddressDistrict);
//                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_ADDRESS_CITY_EXTRA, mAddressCity);
//                startActivityForResult(intent, Constants.START_ACTIVITY_DETAILED_ITEM_ADDRESS_REQUEST);
                Intent intent = new Intent(PostHouseActivity.this, PostHouseAddressActivity.class);
                intent.putExtra(Constants.ACTIVITY_POST_HOUSE_ADDRESS_HOUSE_NUMBER_EXTRA, mAddressHouseNumber);
                intent.putExtra(Constants.ACTIVITY_POST_HOUSE_ADDRESS_STREET_EXTRA, mAddressStreet);
                intent.putExtra(Constants.ACTIVITY_POST_HOUSE_ADDRESS_WARD_EXTRA, mAddressWard);
                intent.putExtra(Constants.ACTIVITY_POST_HOUSE_ADDRESS_DISTRICT_EXTRA, mAddressDistrict);
                intent.putExtra(Constants.ACTIVITY_POST_HOUSE_ADDRESS_CITY_EXTRA, mAddressCity);
                intent.putExtra(Constants.ACTIVITY_POST_HOUSE_ADDRESS_LATITUDE_EXTRA, mLatitude);
                intent.putExtra(Constants.ACTIVITY_POST_HOUSE_ADDRESS_LONGITUDE_EXTRA, mLongitude);
                startActivityForResult(intent, Constants.START_ACTIVITY_POST_HOUSE_ADDRESS_REQUEST);
                overridePendingTransition(R.anim.push_left_in, R.anim.scale_down_out);

                KeyboardHelper.hideSoftKeyboard(PostHouseActivity.this, true);
            }
        });
        mAddressText = (TextView) findViewById(R.id.activity_post_house_address_text);
        mAddressText.setHorizontallyScrolling(true);
        mAddressText.setSelected(true);

        mHouseDirectionLayout = (LinearLayout) findViewById(R.id.activity_post_house_house_direction_field);
        mHouseDirectionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostHouseActivity.this, PostHouseDetailedItemActivity.class);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_TOOLBAR_TITLE_EXTRA, Constants.ACTIVITY_HOUSE_DIRECTION_TOOLBAR_TITLE);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_DETAILS_EXTRA, mHouseDirectionText.getText());
                startActivityForResult(intent, Constants.START_ACTIVITY_DETAILED_ITEM_HOUSE_DIRECTION_REQUEST);
                overridePendingTransition(R.anim.push_left_in, R.anim.scale_down_out);

                KeyboardHelper.hideSoftKeyboard(PostHouseActivity.this, true);
            }
        });
        mHouseDirectionText = (TextView) findViewById(R.id.activity_post_house_house_direction_text);
        mHouseDirectionText.setHorizontallyScrolling(true);
        mHouseDirectionText.setSelected(true);

        mPriceLayout = (LinearLayout) findViewById(R.id.activity_post_house_price_field);
        mPriceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostHouseActivity.this, PostHouseDetailedItemActivity.class);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_TOOLBAR_TITLE_EXTRA, Constants.ACTIVITY_PRICE_TOOLBAR_TITLE);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_PRICE_EXTRA, mPrice);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_PRICE_UNIT_EXTRA, mPriceUnit);
                startActivityForResult(intent, Constants.START_ACTIVITY_DETAILED_ITEM_PRICE_REQUEST);
                overridePendingTransition(R.anim.push_left_in, R.anim.scale_down_out);

                KeyboardHelper.hideSoftKeyboard(PostHouseActivity.this, true);
            }
        });
        mPriceText = (TextView) findViewById(R.id.activity_post_house_price_text);
        mPriceText.setHorizontallyScrolling(true);
        mPriceText.setSelected(true);

        mContactLayout = (LinearLayout) findViewById(R.id.activity_post_house_contact_field);
        mContactLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostHouseActivity.this, PostHouseDetailedItemActivity.class);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_TOOLBAR_TITLE_EXTRA, Constants.ACTIVITY_CONTACT_TOOLBAR_TITLE);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_CONTACT_NAME_EXTRA, Constants.CONTACT_NAME);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_CONTACT_NUMBER_EXTRA, Constants.CONTACT_NUMBER);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_CONTACT_EMAIL_EXTRA, Constants.CONTACT_EMAIL);
                startActivityForResult(intent, Constants.START_ACTIVITY_DETAILED_ITEM_CONTACT_REQUEST);
                overridePendingTransition(R.anim.push_left_in, R.anim.scale_down_out);

                KeyboardHelper.hideSoftKeyboard(PostHouseActivity.this, true);
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
                Intent intent = new Intent(PostHouseActivity.this, PostHouseDetailedItemActivity.class);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_TOOLBAR_TITLE_EXTRA, Constants.ACTIVITY_DESCRIPTION_TOOLBAR_TITLE);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_DETAILS_EXTRA, mDescriptionText.getText());
                startActivityForResult(intent, Constants.START_ACTIVITY_DETAILED_ITEM_DESCRIPTION_REQUEST);
                overridePendingTransition(R.anim.push_left_in, R.anim.scale_down_out);

                KeyboardHelper.hideSoftKeyboard(PostHouseActivity.this, true);
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
                        PostHouseActivity.this,
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
                            ContextCompat.getColor(PostHouseActivity.this, R.color.colorPrimary),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mAllowPet = true;
                } else {
                    mAllowPetCheckmark.setColorFilter(
                            ContextCompat.getColor(PostHouseActivity.this, R.color.silver_gray),
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
                ContextCompat.getColor(PostHouseActivity.this, R.color.silver_gray),
                PorterDuff.Mode.SRC_ATOP
        );
        mAllowPet = false;

        View.OnClickListener hasWifiClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mHasWifi) {
                    mHasWifiCheckmark.setColorFilter(
                            ContextCompat.getColor(PostHouseActivity.this, R.color.colorPrimary),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mHasWifi = true;
                } else {
                    mHasWifiCheckmark.setColorFilter(
                            ContextCompat.getColor(PostHouseActivity.this, R.color.silver_gray),
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
                ContextCompat.getColor(PostHouseActivity.this, R.color.silver_gray),
                PorterDuff.Mode.SRC_ATOP
        );
        mHasWifi = false;

        View.OnClickListener hasACClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mHasAC) {
                    mHasACCheckmark.setColorFilter(
                            ContextCompat.getColor(PostHouseActivity.this, R.color.colorPrimary),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mHasAC = true;
                } else {
                    mHasACCheckmark.setColorFilter(
                            ContextCompat.getColor(PostHouseActivity.this, R.color.silver_gray),
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
                ContextCompat.getColor(PostHouseActivity.this, R.color.silver_gray),
                PorterDuff.Mode.SRC_ATOP
        );
        mHasAC = false;

        View.OnClickListener hasParkingClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mHasParking) {
                    mHasParkingCheckmark.setColorFilter(
                            ContextCompat.getColor(PostHouseActivity.this, R.color.colorPrimary),
                            PorterDuff.Mode.SRC_ATOP
                    );
                    mHasParking = true;
                } else {
                    mHasParkingCheckmark.setColorFilter(
                            ContextCompat.getColor(PostHouseActivity.this, R.color.silver_gray),
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
                ContextCompat.getColor(PostHouseActivity.this, R.color.silver_gray),
                PorterDuff.Mode.SRC_ATOP
        );
        mHasParking = false;

        mResetInputLayout = (RelativeLayout) findViewById(R.id.activity_post_house_reset_input);
        mResetInputLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        ContextCompat.getColor(PostHouseActivity.this, R.color.silver_gray),
                        PorterDuff.Mode.SRC_ATOP
                );
                mAllowPet = false;
                mHasWifiCheckmark.setColorFilter(
                        ContextCompat.getColor(PostHouseActivity.this, R.color.silver_gray),
                        PorterDuff.Mode.SRC_ATOP
                );
                mHasWifi = false;
                mHasACCheckmark.setColorFilter(
                        ContextCompat.getColor(PostHouseActivity.this, R.color.silver_gray),
                        PorterDuff.Mode.SRC_ATOP
                );
                mHasAC = false;
                mHasParkingCheckmark.setColorFilter(
                        ContextCompat.getColor(PostHouseActivity.this, R.color.silver_gray),
                        PorterDuff.Mode.SRC_ATOP
                );
                mHasParking = false;

                KeyboardHelper.hideSoftKeyboard(PostHouseActivity.this, true);
            }
        });

        mPostBarLayout = (RelativeLayout) findViewById(R.id.activity_post_house_post_bar);
        mCancelText = (TextView) mPostBarLayout.findViewById(R.id.activity_post_house_post_bar_cancel);
        mCancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(PostHouseActivity.this)
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
                if (mAllCurrentSelectedPhotoUris.size() == 0
                        || mHouseTypeText.getText().toString().equalsIgnoreCase(Constants.HOUSE_TYPES.get(0))
                        || mAreaEditText.getText().toString().isEmpty()
                        || mAddressDistrict.isEmpty()
                        || mAddressCity.isEmpty()
                        || mLatitude.isEmpty()
                        || mLongitude.isEmpty()
                        || mPriceText.getText().toString().isEmpty()
                        || mContactName.isEmpty()) {
                    String errorMessage = getString(R.string.activity_post_house_post_error_message_missing);
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
                    errorMessage = errorMessage.substring(0, errorMessage.length() - 2);

                    ToastHelper.showCenterToast(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
                } else if (mPrice == 0) {
                    ToastHelper.showCenterToast(
                            getApplicationContext(),
                            getString(R.string.activity_post_house_post_error_message_zero_price),
                            Toast.LENGTH_LONG
                    );
                } else {
                    if (mIsPostNewHousing) {
                        uploadNewHousingData();
                    } else {
                        updateHousingData();
                    }
                }
            }
        });
        if (mIsEditHousing) {
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

                FishBunCreator fishBunCreator = FishBun.with(PostHouseActivity.this)
                        .MultiPageMode()
                        .setCamera(true)
                        .setPickerSpanCount(Constants.ACTIVITY_POST_HOUSE_PHOTO_PICKER_NUM_COLUMN_COUNT)
                        .setActionBarColor(ContextCompat.getColor(PostHouseActivity.this, R.color.default_white),
                                ContextCompat.getColor(PostHouseActivity.this, R.color.default_white), true)
                        .setActionBarTitleColor(ContextCompat.getColor(PostHouseActivity.this, R.color.colorPrimary))
                        .setAlbumSpanCount(Constants.ACTIVITY_POST_HOUSE_PORTRAIT_ALBUM_PICKER_NUM_COLUMN_COUNT,
                                Constants.ACTIVITY_POST_HOUSE_LANDSCAPE_ALBUM_PICKER_NUM_COLUMN_COUNT)
                        .setButtonInAlbumActivity(false)
                        .exceptGif(true)
                        .setReachLimitAutomaticClose(false)
                        .setHomeAsUpIndicatorDrawable(ContextCompat.getDrawable(PostHouseActivity.this, R.drawable.ic_left_arrow))
                        .setOkButtonDrawable(ContextCompat.getDrawable(PostHouseActivity.this, R.drawable.ic_check_mark_thicker))
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
                mGridItemMarginPixels = PostHouseActivity.this.getResources().getDimensionPixelSize(R.dimen.activity_post_house_add_photo_box_margin);
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
                            Drawable drawable = new BitmapDrawable(PostHouseActivity.this.getResources(), resource);
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
                        Drawable drawable = new BitmapDrawable(PostHouseActivity.this.getResources(), resource);
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
                            Drawable drawable = new BitmapDrawable(PostHouseActivity.this.getResources(), resource);
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

    private void uploadNewHousingData() {
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
            FirebaseStorageHelper.postNewHousing(
                    this, mToBePostedHousing,
                    mAllCurrentSelectedPhotoUris,
                    new OnHousingPostingListener() {
                        @Override
                        public void onPostSuccess(Housing housing) {
//                            mToBePostedHousing = housing;
//                            Gson gson = new Gson();
//                            Intent returnIntent = new Intent();
//                            returnIntent.putExtra(
//                                    Constants.START_ACTIVITY_POST_HOUSE_REQUEST_POSTED_HOUSING_RESULT,
//                                    gson.toJson(mToBePostedHousing)
//                            );
//                            setResult(RESULT_OK, returnIntent);
                            if (housing != null) {
                                ShareSpaceApplication.BUS.post(new PostHousingEvent(housing));
                                finish();
                            } else {
                                ToastHelper.showCenterToast(
                                        getApplicationContext(),
                                        R.string.activity_post_house_cannot_upload_house_info,
                                        Toast.LENGTH_LONG
                                );
                            }
                        }

                        @Override
                        public void onPostFailure(Throwable t) {
                            ToastHelper.showCenterToast(
                                    getApplicationContext(),
                                    R.string.activity_post_house_cannot_upload_house_info,
                                    Toast.LENGTH_LONG
                            );
                            RetrofitClient.showShareSpaceServerConnectionErrorDialog(PostHouseActivity.this, t);
                        }
                    }
            );
        }
    }

    private void updateHousingData() {
        if (Constants.CURRENT_USER != null) {
            final ArrayList<String> photoURIStrings = new ArrayList<>();
            for (Uri photoUri : mAllCurrentSelectedPhotoUris) {
                photoURIStrings.add(photoUri.toString());
            }
            int price = HousePriceHelper.convertForHousing(mPrice, mPriceUnit, this);
            mToBePostedHousing = new Housing(
                    mToBePostedHousing.getID(), photoURIStrings, mHouseTitleText.getText().toString(),
                    Constants.CURRENT_USER, price, true,
                    mHouseTypeText.getText().toString(), new Date(), mToBePostedHousing.getNumOfView(),
                    mToBePostedHousing.getNumOfSaved(),
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
            FirebaseStorageHelper.updateHousing(
                    this, mToBePostedHousing,
                    mAllCurrentSelectedPhotoUris,
                    new OnHousingUpdatingListener() {
                        @Override
                        public void onUpdateComplete(Boolean isUpdated) {
//                            mToBePostedHousing = housing;
//                            Gson gson = new Gson();
//                            Intent returnIntent = new Intent();
//                            returnIntent.putExtra(
//                                    Constants.START_ACTIVITY_POST_HOUSE_REQUEST_POSTED_HOUSING_RESULT,
//                                    gson.toJson(mToBePostedHousing)
//                            );
//                            setResult(RESULT_OK, returnIntent);
                            if (isUpdated) {
                                ShareSpaceApplication.BUS.post(new UpdateHousingEvent(mToBePostedHousing));
                                Intent returnIntent = new Intent();
                                Gson gson = new Gson();
                                returnIntent.putExtra(
                                        Constants.ACTIVITY_HOUSING_DETAIL_HOUSING_EXTRA,
                                        gson.toJson(mToBePostedHousing)
                                );
                                setResult(RESULT_OK, returnIntent);
                                finish();
                            } else {
                                ToastHelper.showCenterToast(
                                        getApplicationContext(),
                                        R.string.activity_post_house_cannot_update_house_info,
                                        Toast.LENGTH_LONG
                                );
                            }
                        }

                        @Override
                        public void onUpdateFailure(Throwable t) {
                            ToastHelper.showCenterToast(
                                    getApplicationContext(),
                                    R.string.activity_post_house_cannot_update_house_info,
                                    Toast.LENGTH_LONG
                            );
                            RetrofitClient.showShareSpaceServerConnectionErrorDialog(PostHouseActivity.this, t);
                        }
                    }
            );
        }
    }
}
