package com.lmtri.sharespace.activity.posthouse;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lmtri.sharespace.R;
import com.lmtri.sharespace.customview.CustomEditText;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.KeyboardHelper;

public class PostHouseActivity extends AppCompatActivity {

    public static final String TAG = PostHouseActivity.class.getSimpleName();

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

    private LinearLayout mHouseDirectionLayout;
    private TextView mHouseDirectionText;

    private LinearLayout mPriceLayout;
    private TextView mPriceText;

    private LinearLayout mContactLayout;
    private TextView mContactText;
    private String mContactName = Constants.CONTACT_NAME;
    private String mContactNumber = Constants.CONTACT_NUMBER;
    private String mContactEmail = Constants.CONTACT_EMAIL;

    private LinearLayout mDetailedInfoLayout;
    private TextView mDetailedInfoText;

    private RelativeLayout mResetInputLayout;

    private RelativeLayout mPostBarLayout;
    private TextView mCancelText;
    private TextView mPostText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_house);

        mHouseTypeLayout = (LinearLayout) findViewById(R.id.activity_post_house_house_type_field);
        mHouseTypeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PostHouseDetailedItemActivity.class);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_TOOLBAR_TITLE_EXTRA, Constants.ACTIVITY_HOUSE_TYPE_TOOLBAR_TITLE);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_DETAILS_EXTRA, mHouseTypeText.getText());
                startActivityForResult(intent, Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST);
                overridePendingTransition(R.anim.push_left_in, R.anim.scale_down_out);

                KeyboardHelper.hideSoftKeyboard(PostHouseActivity.this);
            }
        });
        mHouseTypeText = (TextView) findViewById(R.id.activity_post_house_house_type_text);

        mAreaEditText = (CustomEditText) findViewById(R.id.activity_post_house_area_edit_text);
        mAreaEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mPostBarLayout.setVisibility(View.GONE);
                } else {
                    mPostBarLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        mAreaEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    KeyboardHelper.hideSoftKeyboard(PostHouseActivity.this);
                }
                return false;
            }
        });

        mAddressLayout = (LinearLayout) findViewById(R.id.activity_post_house_address_field);
        mAddressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PostHouseDetailedItemActivity.class);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_TOOLBAR_TITLE_EXTRA, Constants.ACTIVITY_ADDRESS_TOOLBAR_TITLE);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_ADDRESS_HOUSE_NUMBER_EXTRA, mAddressHouseNumber);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_ADDRESS_STREET_EXTRA, mAddressStreet);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_ADDRESS_WARD_EXTRA, mAddressWard);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_ADDRESS_DISTRICT_EXTRA, mAddressDistrict);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_ADDRESS_CITY_EXTRA, mAddressCity);
                startActivityForResult(intent, Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST);
                overridePendingTransition(R.anim.push_left_in, R.anim.scale_down_out);

                KeyboardHelper.hideSoftKeyboard(PostHouseActivity.this);
            }
        });
        mAddressText = (TextView) findViewById(R.id.activity_post_house_address_text);

        mHouseDirectionLayout = (LinearLayout) findViewById(R.id.activity_post_house_house_direction_field);
        mHouseDirectionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PostHouseDetailedItemActivity.class);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_TOOLBAR_TITLE_EXTRA, Constants.ACTIVITY_HOUSE_DIRECTION_TOOLBAR_TITLE);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_DETAILS_EXTRA, mHouseDirectionText.getText());
                startActivityForResult(intent, Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST);
                overridePendingTransition(R.anim.push_left_in, R.anim.scale_down_out);

                KeyboardHelper.hideSoftKeyboard(PostHouseActivity.this);
            }
        });
        mHouseDirectionText = (TextView) findViewById(R.id.activity_post_house_house_direction_text);

        mPriceLayout = (LinearLayout) findViewById(R.id.activity_post_house_price_field);
        mPriceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PostHouseDetailedItemActivity.class);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_TOOLBAR_TITLE_EXTRA, Constants.ACTIVITY_PRICE_TOOLBAR_TITLE);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_DETAILS_EXTRA, mPriceText.getText());
                startActivityForResult(intent, Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST);
                overridePendingTransition(R.anim.push_left_in, R.anim.scale_down_out);

                KeyboardHelper.hideSoftKeyboard(PostHouseActivity.this);
            }
        });
        mPriceText = (TextView) findViewById(R.id.activity_post_house_price_text);

        mContactLayout = (LinearLayout) findViewById(R.id.activity_post_house_contact_field);
        mContactLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PostHouseDetailedItemActivity.class);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_TOOLBAR_TITLE_EXTRA, Constants.ACTIVITY_CONTACT_TOOLBAR_TITLE);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_CONTACT_NAME_EXTRA, Constants.CONTACT_NAME);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_CONTACT_NUMBER_EXTRA, Constants.CONTACT_NUMBER);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_CONTACT_EMAIL_EXTRA, Constants.CONTACT_EMAIL);
                startActivityForResult(intent, Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST);
                overridePendingTransition(R.anim.push_left_in, R.anim.scale_down_out);

                KeyboardHelper.hideSoftKeyboard(PostHouseActivity.this);
            }
        });
        mContactText = (TextView) findViewById(R.id.activity_post_house_contact_text);

        mDetailedInfoLayout = (LinearLayout) findViewById(R.id.activity_post_house_details_field);
        mDetailedInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PostHouseDetailedItemActivity.class);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_TOOLBAR_TITLE_EXTRA, Constants.ACTIVITY_DETAILED_INFO_TOOLBAR_TITLE);
                intent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_DETAILS_EXTRA, mDetailedInfoText.getText());
                startActivityForResult(intent, Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST);
                overridePendingTransition(R.anim.push_left_in, R.anim.scale_down_out);

                KeyboardHelper.hideSoftKeyboard(PostHouseActivity.this);
            }
        });
        mDetailedInfoText = (TextView) findViewById(R.id.activity_post_house_details_text);

        mResetInputLayout = (RelativeLayout) findViewById(R.id.activity_post_house_reset_input);
        mResetInputLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHouseTypeText.setText(getString(R.string.activity_post_house_detailed_item_house_type_arbitrary));

                mAreaEditText.setText("");

                mAddressText.setText("");
                mAddressHouseNumber = "";
                mAddressStreet = "";
                mAddressWard = "";
                mAddressDistrict = "";
                mAddressCity = "";

                mHouseDirectionText.setText(getString(R.string.activity_post_house_detailed_item_house_direction_arbitrary));

                mPriceText.setText("");

                mContactText.setText(Constants.CONTACT_NAME + "-" + Constants.CONTACT_NUMBER + "-" + Constants.CONTACT_EMAIL);
                mContactName = Constants.CONTACT_NAME;
                mContactNumber = Constants.CONTACT_NUMBER;
                mContactEmail = Constants.CONTACT_EMAIL;

                mDetailedInfoText.setText("");

                KeyboardHelper.hideSoftKeyboard(PostHouseActivity.this);
            }
        });

        mPostBarLayout = (RelativeLayout) findViewById(R.id.activity_post_house_post_bar_layout);
        mCancelText = (TextView) mPostBarLayout.findViewById(R.id.activity_post_house_post_bar_cancel);
        mCancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(PostHouseActivity.this)
                        .setTitle(getString(R.string.activity_post_house_cancel_alert_title))
                        .setMessage(getString(R.string.activity_post_house_cancel_alert_message))
                        .setPositiveButton(getString(R.string.activity_post_house_cancel_alert_confirm),
                                new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton(getString(R.string.activity_post_house_cancel_alert_cancel),
                                new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();
            }
        });
        mPostText = (TextView) mPostBarLayout.findViewById(R.id.activity_post_house_post_bar_post);
        mPostText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHouseTypeText.getText().toString().equalsIgnoreCase(Constants.HOUSE_TYPES.get(0))
                        || mAreaEditText.getText().toString().isEmpty()
                        || mAddressDistrict.isEmpty()
                        || mAddressCity.isEmpty()
                        || mPriceText.getText().toString().isEmpty()
                        || mContactName.isEmpty()) {
                    String errorMessage = getString(R.string.activity_post_house_post_error_message_missing);
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
                    if (mPriceText.getText().toString().isEmpty()) {
                        errorMessage += getString(R.string.activity_post_house_post_error_message_missing_price);
                    }
                    if (mContactName.isEmpty()) {
                        errorMessage += getString(R.string.activity_post_house_post_error_message_missing_owner_name);
                    }
                    errorMessage = errorMessage.substring(0, errorMessage.length() - 2);

                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                } else {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(Constants.START_ACTIVITY_POST_HOUSE_REQUEST_HOUSE_TYPE_RESULT, mHouseTypeText.getText().toString());
                    returnIntent.putExtra(Constants.START_ACTIVITY_POST_HOUSE_REQUEST_AREA_RESULT, mAreaEditText.getText().toString());
                    returnIntent.putExtra(Constants.START_ACTIVITY_POST_HOUSE_REQUEST_ADDRESS_HOUSE_NUMBER_RESULT, mAddressHouseNumber);
                    returnIntent.putExtra(Constants.START_ACTIVITY_POST_HOUSE_REQUEST_ADDRESS_STREET_RESULT, mAddressStreet);
                    returnIntent.putExtra(Constants.START_ACTIVITY_POST_HOUSE_REQUEST_ADDRESS_WARD_RESULT, mAddressWard);
                    returnIntent.putExtra(Constants.START_ACTIVITY_POST_HOUSE_REQUEST_ADDRESS_DISTRICT_RESULT, mAddressDistrict);
                    returnIntent.putExtra(Constants.START_ACTIVITY_POST_HOUSE_REQUEST_ADDRESS_CITY_RESULT, mAddressCity);
                    returnIntent.putExtra(Constants.START_ACTIVITY_POST_HOUSE_REQUEST_HOUSE_DIRECTION_RESULT, mHouseDirectionText.getText().toString());
                    returnIntent.putExtra(Constants.START_ACTIVITY_POST_HOUSE_REQUEST_PRICE_RESULT, mPriceText.getText().toString());
                    returnIntent.putExtra(Constants.START_ACTIVITY_POST_HOUSE_REQUEST_CONTACT_NAME_RESULT, mContactName);
                    returnIntent.putExtra(Constants.START_ACTIVITY_POST_HOUSE_REQUEST_CONTACT_NUMBER_RESULT, mContactNumber);
                    returnIntent.putExtra(Constants.START_ACTIVITY_POST_HOUSE_REQUEST_CONTACT_EMAIL_RESULT, mContactEmail);
                    returnIntent.putExtra(Constants.START_ACTIVITY_POST_HOUSE_REQUEST_DETAILS_INFO_RESULT, mDetailedInfoText.getText().toString());
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST) {
            if (resultCode == RESULT_OK) {
                String activityType = data.getStringExtra(Constants.ACTIVITY_DETAILED_ITEM_RESULT_TYPE);
                String result = data.getStringExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_RESULT);
                if (activityType.equalsIgnoreCase(Constants.ACTIVITY_HOUSE_TYPE_TOOLBAR_TITLE)) {
                    mHouseTypeText.setText(result);
                } else if (activityType.equalsIgnoreCase(Constants.ACTIVITY_ADDRESS_TOOLBAR_TITLE)) {
                    mAddressHouseNumber = data.getStringExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_HOUSE_NUMBER_RESULT);
                    mAddressStreet = data.getStringExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_STREET_RESULT);
                    mAddressWard = data.getStringExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_WARD_RESULT);
                    mAddressDistrict = data.getStringExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_DISTRICT_RESULT);
                    mAddressCity = data.getStringExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_CITY_RESULT);
                    mAddressText.setText(
                            mAddressHouseNumber + ", "
                                    + mAddressStreet + ", "
                                    + getString(R.string.activity_post_house_detailed_item_address_ward_acronym) + mAddressWard + ", "
                                    + getString(R.string.activity_post_house_detailed_item_address_district_acronym) + mAddressDistrict + ", "
                                    + mAddressCity
                    );
                } else if (activityType.equalsIgnoreCase(Constants.ACTIVITY_HOUSE_DIRECTION_TOOLBAR_TITLE)) {
                    mHouseDirectionText.setText(result);
                } else if (activityType.equalsIgnoreCase(Constants.ACTIVITY_PRICE_TOOLBAR_TITLE)) {
                    mPriceText.setText(result);
                } else if (activityType.equalsIgnoreCase(Constants.ACTIVITY_CONTACT_TOOLBAR_TITLE)) {
                    mContactName = data.getStringExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_CONTACT_NAME_RESULT);
                    mContactNumber = data.getStringExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_CONTACT_NUMBER_RESULT);
                    mContactEmail = data.getStringExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_CONTACT_EMAIL_RESULT);
                    mContactText.setText(mContactName + "-" + mContactNumber + "-" + mContactEmail);
                } else if (activityType.equalsIgnoreCase(Constants.ACTIVITY_DETAILED_INFO_TOOLBAR_TITLE)) {
                    mDetailedInfoText.setText(result);
                }
            }
        }
    }
}
