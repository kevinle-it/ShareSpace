package com.lmtri.sharespace.activity.posthouse;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lmtri.sharespace.R;
import com.lmtri.sharespace.customview.CustomEditText;
import com.lmtri.sharespace.helper.Constants;

import java.util.ArrayList;

public class PostHouseDetailedItemActivity extends AppCompatActivity {

    public static final String TAG = PostHouseDetailedItemActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private TextView mToolbarTitleTextView;
    private TextView mToolbarDoneTextView;
    private LinearLayout mDetailedItemContainer;

    private LayoutInflater mLayoutInflater;
    private TextView mDetailedItemText;
    private ImageView mDetailedItemCheckMark;

    private String mToolbarTitle;
    private String mDetailsExtra;

    private ArrayList<CustomEditText> mAddressEditTexts = new ArrayList<>();
    private ArrayList<View> mPriceUnitFields = new ArrayList<>();
    private ArrayList<CustomEditText> mContactEditTexts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_house_detailed_activity);

        mToolbarTitle = getIntent().getStringExtra(Constants.ACTIVITY_DETAILED_ITEM_TOOLBAR_TITLE_EXTRA);
        mDetailsExtra = getIntent().getStringExtra(Constants.ACTIVITY_DETAILED_ITEM_DETAILS_EXTRA);

        mToolbar = (Toolbar) findViewById(R.id.activity_post_house_house_type_toolbar);
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
                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        mToolbarTitleTextView = (TextView) findViewById(R.id.activity_post_house_detailed_item_toolbar_title);
        mToolbarTitleTextView.setText(mToolbarTitle);

        mToolbarDoneTextView = (TextView) findViewById(R.id.activity_post_house_detailed_item_toolbar_done);

        mDetailedItemContainer = (LinearLayout) findViewById(R.id.activity_post_house_detailed_item_container);

        mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (mToolbarTitle.equalsIgnoreCase(Constants.ACTIVITY_HOUSE_TYPE_TOOLBAR_TITLE)) {
            for (String rowData : Constants.HOUSE_TYPES) {
                final View item = mLayoutInflater.inflate(R.layout.activity_post_house_detailed_item, null);
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView houseType = (TextView) item.findViewById(R.id.activity_post_house_detailed_item_name);
                        ImageView checkMark = (ImageView) item.findViewById(R.id.activity_post_house_detailed_item_check_mark);
                        for (int i = 0; i < mDetailedItemContainer.getChildCount(); ++i) {
                            View child = mDetailedItemContainer.getChildAt(i);
                            ImageView childCheckMark = (ImageView) child.findViewById(R.id.activity_post_house_detailed_item_check_mark);
                            childCheckMark.setVisibility(View.INVISIBLE);
                        }
                        checkMark.setVisibility(View.VISIBLE);

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_RESULT_TYPE, Constants.ACTIVITY_HOUSE_TYPE_TOOLBAR_TITLE);
                        returnIntent.putExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_RESULT, houseType.getText());
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                });

                mDetailedItemText = (TextView) item.findViewById(R.id.activity_post_house_detailed_item_name);
                mDetailedItemText.setText(rowData);

                mDetailedItemCheckMark = (ImageView) item.findViewById(R.id.activity_post_house_detailed_item_check_mark);
                if (rowData.equalsIgnoreCase(mDetailsExtra)) {
                    mDetailedItemCheckMark.setVisibility(View.VISIBLE);
                }

                mDetailedItemContainer.addView(item);
            }
        } else if (mToolbarTitle.equalsIgnoreCase(Constants.ACTIVITY_ADDRESS_TOOLBAR_TITLE)) {
            mToolbarDoneTextView.setVisibility(View.VISIBLE);
            mToolbarDoneTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_RESULT_TYPE, Constants.ACTIVITY_ADDRESS_TOOLBAR_TITLE);
                    returnIntent.putExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_HOUSE_NUMBER_RESULT, mAddressEditTexts.get(0).getText().toString());
                    returnIntent.putExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_STREET_RESULT, mAddressEditTexts.get(1).getText().toString());
                    returnIntent.putExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_WARD_RESULT, mAddressEditTexts.get(2).getText().toString());
                    returnIntent.putExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_DISTRICT_RESULT, mAddressEditTexts.get(3).getText().toString());
                    returnIntent.putExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_CITY_RESULT, mAddressEditTexts.get(4).getText().toString());
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            });

            String houseNumber = getIntent().getStringExtra(Constants.ACTIVITY_DETAILED_ITEM_ADDRESS_HOUSE_NUMBER_EXTRA);
            String street = getIntent().getStringExtra(Constants.ACTIVITY_DETAILED_ITEM_ADDRESS_STREET_EXTRA);
            String ward = getIntent().getStringExtra(Constants.ACTIVITY_DETAILED_ITEM_ADDRESS_WARD_EXTRA);
            String district = getIntent().getStringExtra(Constants.ACTIVITY_DETAILED_ITEM_ADDRESS_DISTRICT_EXTRA);
            String city = getIntent().getStringExtra(Constants.ACTIVITY_DETAILED_ITEM_ADDRESS_CITY_EXTRA);

            for (String rowData : Constants.ADDRESS_FIELDS) {
                View item = mLayoutInflater.inflate(R.layout.activity_post_house_detailed_item, null);
                mDetailedItemText = (TextView) item.findViewById(R.id.activity_post_house_detailed_item_name);
                CustomEditText inputAddress = new CustomEditText(this);
                if (rowData.equalsIgnoreCase(Constants.ADDRESS_FIELDS.get(0))) {
                    inputAddress.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE);
                }
                if (rowData.equalsIgnoreCase(Constants.ADDRESS_FIELDS.get(Constants.ADDRESS_FIELDS.size() - 2))) {
                    inputAddress.setImeOptions(EditorInfo.IME_ACTION_DONE);
                } else {
                    inputAddress.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                }
                inputAddress.setSingleLine();
                inputAddress.setMaxLines(1);
                inputAddress.setBackground(ContextCompat.getDrawable(this, R.drawable.edittext_field));
                inputAddress.setPadding(
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()),
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()),
                        0,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics())
                );
                inputAddress.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        mDetailedItemText.setText(Constants.ADDRESS_FIELDS.get(Constants.ADDRESS_FIELDS.size() - 1));
                        for (int i = 0; i < mAddressEditTexts.size() - 1; ++i) {
                            if (i == 0) {
                                mDetailedItemText.setText(mDetailedItemText.getText()
                                        + mAddressEditTexts.get(i).getText().toString());
                            } else if (i == 2) {
                                mDetailedItemText.setText(mDetailedItemText.getText() + ", "
                                        + getString(R.string.activity_post_house_detailed_item_address_ward_acronym)
                                        + mAddressEditTexts.get(i).getText().toString());
                            } else if (i == 3) {
                                mDetailedItemText.setText(mDetailedItemText.getText() + ", "
                                        + getString(R.string.activity_post_house_detailed_item_address_district_acronym)
                                        + mAddressEditTexts.get(i).getText().toString());
                            } else {
                                mDetailedItemText.setText(mDetailedItemText.getText() + ", "
                                        + mAddressEditTexts.get(i).getText().toString());
                            }
                        }
                    }
                });
                if (rowData.equalsIgnoreCase(Constants.ADDRESS_FIELDS.get(0)) && !houseNumber.isEmpty()) {
                    inputAddress.setText(houseNumber);
                } else if (rowData.equalsIgnoreCase(Constants.ADDRESS_FIELDS.get(1)) && !street.isEmpty()) {
                    inputAddress.setText(street);
                } else if (rowData.equalsIgnoreCase(Constants.ADDRESS_FIELDS.get(2)) && !ward.isEmpty()) {
                    inputAddress.setText(ward);
                } else if (rowData.equalsIgnoreCase(Constants.ADDRESS_FIELDS.get(3)) && !district.isEmpty()) {
                    inputAddress.setText(district);
                } else if (rowData.equalsIgnoreCase(Constants.ADDRESS_FIELDS.get(4)) && !city.isEmpty()) {
                    inputAddress.setText(city);
                }

                mDetailedItemContainer.addView(item);
                if (!rowData.equalsIgnoreCase(Constants.ADDRESS_FIELDS.get(Constants.ADDRESS_FIELDS.size() - 1))) {
                    mDetailedItemText.setText(rowData);

                    mAddressEditTexts.add(inputAddress);
                    mDetailedItemContainer.addView(inputAddress);
                } else {
                    mDetailedItemText.setText(Constants.ADDRESS_FIELDS.get(Constants.ADDRESS_FIELDS.size() - 1)
                            + houseNumber + ", "
                            + street + ", "
                            + getString(R.string.activity_post_house_detailed_item_address_ward_acronym) + ward + ", "
                            + getString(R.string.activity_post_house_detailed_item_address_district_acronym) + district + ", "
                            + city
                    );
                }
            }
        } else if (mToolbarTitle.equalsIgnoreCase(Constants.ACTIVITY_HOUSE_DIRECTION_TOOLBAR_TITLE)) {
            for (String rowData : Constants.HOUSE_DIRECTIONS) {
                final View item = mLayoutInflater.inflate(R.layout.activity_post_house_detailed_item, null);
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView houseDirection = (TextView) item.findViewById(R.id.activity_post_house_detailed_item_name);
                        ImageView checkMark = (ImageView) item.findViewById(R.id.activity_post_house_detailed_item_check_mark);
                        for (int i = 0; i < mDetailedItemContainer.getChildCount(); ++i) {
                            View child = mDetailedItemContainer.getChildAt(i);
                            ImageView childCheckMark = (ImageView) child.findViewById(R.id.activity_post_house_detailed_item_check_mark);
                            childCheckMark.setVisibility(View.INVISIBLE);
                        }
                        checkMark.setVisibility(View.VISIBLE);
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_RESULT_TYPE, Constants.ACTIVITY_HOUSE_DIRECTION_TOOLBAR_TITLE);
                        returnIntent.putExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_RESULT, houseDirection.getText());
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                });

                mDetailedItemText = (TextView) item.findViewById(R.id.activity_post_house_detailed_item_name);
                mDetailedItemText.setText(rowData);

                mDetailedItemCheckMark = (ImageView) item.findViewById(R.id.activity_post_house_detailed_item_check_mark);
                if (rowData.equalsIgnoreCase(mDetailsExtra)) {
                    mDetailedItemCheckMark.setVisibility(View.VISIBLE);
                }

                mDetailedItemContainer.addView(item);
            }
        } else if (mToolbarTitle.equalsIgnoreCase(Constants.ACTIVITY_PRICE_TOOLBAR_TITLE)) {
            final TextView shownPriceSection = new TextView(this);
            shownPriceSection.setBackgroundColor(ContextCompat.getColor(this, R.color.clouds));
            shownPriceSection.setPadding(
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()),
                    0,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics())
            );
            shownPriceSection.setText(getString(R.string.activity_post_house_detailed_item_price_shown) + mDetailsExtra);

            final CustomEditText inputPrice = new CustomEditText(this);
            inputPrice.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            inputPrice.setSingleLine();
            inputPrice.setMaxLines(1);
            inputPrice.setBackground(ContextCompat.getDrawable(this, R.drawable.edittext_field));
            inputPrice.setPadding(
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()),
                    0,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics())
            );
            inputPrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    shownPriceSection.setText(getString(R.string.activity_post_house_detailed_item_price_shown) + s.toString());
                    for (View priceUnitField : mPriceUnitFields) {
                        if (priceUnitField.findViewById(R.id.activity_post_house_detailed_item_check_mark).getVisibility() == View.VISIBLE) {
                            TextView unit = (TextView) priceUnitField.findViewById(R.id.activity_post_house_detailed_item_name);
                            shownPriceSection.setText(
                                    getString(R.string.activity_post_house_detailed_item_price_shown) + s.toString() + " " + unit.getText());
                        }
                    }
                }
            });
            if (!mDetailsExtra.equalsIgnoreCase(Constants.PRICE_UNITS.get(Constants.PRICE_UNITS.size() - 1))) {
                inputPrice.setText(mDetailsExtra.split("[ ]", 2)[0]);
            }

            TextView priceUnitSection = new TextView(this);
            priceUnitSection.setBackgroundColor(ContextCompat.getColor(this, R.color.clouds));
            priceUnitSection.setPadding(
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()),
                    0,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics())
            );
            priceUnitSection.setText(getString(R.string.activity_post_house_detailed_item_price_unit_section));

            mToolbarDoneTextView.setVisibility(View.VISIBLE);
            mToolbarDoneTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!inputPrice.getText().toString().isEmpty()) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_RESULT_TYPE, Constants.ACTIVITY_PRICE_TOOLBAR_TITLE);
                        for (View priceUnitField : mPriceUnitFields) {
                            if (priceUnitField.findViewById(R.id.activity_post_house_detailed_item_check_mark).getVisibility() == View.VISIBLE) {
                                TextView unit = (TextView) priceUnitField.findViewById(R.id.activity_post_house_detailed_item_name);
                                if (!unit.getText().toString().equalsIgnoreCase(Constants.PRICE_UNITS.get(Constants.PRICE_UNITS.size() - 1))) {
                                    returnIntent.putExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_RESULT,
                                            shownPriceSection.getText().toString().split(":[ ]")[1]);
                                    break;
                                } else {
                                    if (!inputPrice.getText().toString().isEmpty()) {
                                        returnIntent.putExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_RESULT,
                                                shownPriceSection.getText().toString().split(":[ ]")[1].split("[ ]", 2)[1]);
                                    } else {
                                        returnIntent.putExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_RESULT,
                                                shownPriceSection.getText().toString().split(":[ ]")[1]);
                                    }
                                    break;
                                }
                            }
                        }
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                getString(R.string.activity_post_house_price_not_input_alert),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
            });

            mDetailedItemContainer.addView(shownPriceSection);
            mDetailedItemContainer.addView(inputPrice);
            mDetailedItemContainer.addView(priceUnitSection);

            for (final String rowData : Constants.PRICE_UNITS) {
                final View item = mLayoutInflater.inflate(R.layout.activity_post_house_detailed_item, null);
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView priceUnit = (TextView) item.findViewById(R.id.activity_post_house_detailed_item_name);
                        ImageView checkMark = (ImageView) item.findViewById(R.id.activity_post_house_detailed_item_check_mark);
                        for (int i = 3; i < mDetailedItemContainer.getChildCount(); ++i) {
                            View child = mDetailedItemContainer.getChildAt(i);
                            ImageView childCheckMark = (ImageView) child.findViewById(R.id.activity_post_house_detailed_item_check_mark);
                            childCheckMark.setVisibility(View.INVISIBLE);
                        }
                        checkMark.setVisibility(View.VISIBLE);

                        if (!inputPrice.getText().toString().isEmpty()) {
                            shownPriceSection.setText(
                                    getString(R.string.activity_post_house_detailed_item_price_shown) + inputPrice.getText().toString() + " " + priceUnit.getText());
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    getString(R.string.activity_post_house_price_not_input_alert),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                mDetailedItemText = (TextView) item.findViewById(R.id.activity_post_house_detailed_item_name);
                mDetailedItemText.setText(rowData);

                mDetailedItemCheckMark = (ImageView) item.findViewById(R.id.activity_post_house_detailed_item_check_mark);
                if (!mDetailsExtra.isEmpty()) {
                    if (mDetailsExtra.equalsIgnoreCase(Constants.PRICE_UNITS.get(Constants.PRICE_UNITS.size() - 1))) {
                        if (rowData.equalsIgnoreCase(mDetailsExtra)) {
                            mDetailedItemCheckMark.setVisibility(View.VISIBLE);
                        }
                    } else if (rowData.equalsIgnoreCase(mDetailsExtra.split("[ ]", 2)[1])) {
                        mDetailedItemCheckMark.setVisibility(View.VISIBLE);
                    }
                }

                mPriceUnitFields.add(item);
                mDetailedItemContainer.addView(item);
            }
        } else if (mToolbarTitle.equalsIgnoreCase(Constants.ACTIVITY_CONTACT_TOOLBAR_TITLE)) {
            mToolbarDoneTextView.setVisibility(View.VISIBLE);
            mToolbarDoneTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_RESULT_TYPE, Constants.ACTIVITY_CONTACT_TOOLBAR_TITLE);
                    returnIntent.putExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_CONTACT_NAME_RESULT, mContactEditTexts.get(0).getText().toString());
                    returnIntent.putExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_CONTACT_NUMBER_RESULT, mContactEditTexts.get(1).getText().toString());
                    returnIntent.putExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_CONTACT_EMAIL_RESULT, mContactEditTexts.get(2).getText().toString());
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            });

            final TextView contactSection = new TextView(this);
            contactSection.setBackgroundColor(ContextCompat.getColor(this, R.color.clouds));
            contactSection.setPadding(
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()),
                    0,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics())
            );
            contactSection.setText(getString(R.string.activity_post_house_detailed_item_contact_section));

            mDetailedItemContainer.addView(contactSection);

            for (final String rowData : Constants.CONTACT_FIELDS) {
                final View item = mLayoutInflater.inflate(R.layout.activity_post_house_detailed_item, null);
                CustomEditText inputContact = (CustomEditText) item.findViewById(R.id.activity_post_house_detailed_item_contact_input);
                if (rowData.equalsIgnoreCase(Constants.CONTACT_FIELDS.get(0))) {
                    inputContact.setText(getIntent().getStringExtra(Constants.ACTIVITY_DETAILED_ITEM_CONTACT_NAME_EXTRA));
                } else if (rowData.equalsIgnoreCase(Constants.CONTACT_FIELDS.get(1))) {
                    inputContact.setText(getIntent().getStringExtra(Constants.ACTIVITY_DETAILED_ITEM_CONTACT_NUMBER_EXTRA));
                } else if (rowData.equalsIgnoreCase(Constants.CONTACT_FIELDS.get(2))) {
                    inputContact.setText(getIntent().getStringExtra(Constants.ACTIVITY_DETAILED_ITEM_CONTACT_EMAIL_EXTRA));
                }
                mContactEditTexts.add(inputContact);
                inputContact.setVisibility(View.VISIBLE);

                mDetailedItemText = (TextView) item.findViewById(R.id.activity_post_house_detailed_item_name);
                mDetailedItemText.setText(rowData);

                mDetailedItemContainer.addView(item);
            }
        } else if (mToolbarTitle.equalsIgnoreCase(Constants.ACTIVITY_DETAILED_INFO_TOOLBAR_TITLE)) {
            final CustomEditText inputNotes = new CustomEditText(this);
            inputNotes.setHint(getString(R.string.activity_post_house_detailed_item_details_hint));
            if (!mDetailsExtra.isEmpty()) {
                inputNotes.setText(mDetailsExtra);
            }
            inputNotes.setBackground(ContextCompat.getDrawable(this, R.drawable.edittext_notes));
            LinearLayout.LayoutParams inputNotesLayoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            inputNotes.setPadding(
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics())
            );
            inputNotes.setGravity(Gravity.TOP);
            inputNotes.setLayoutParams(inputNotesLayoutParams);

            mToolbarDoneTextView.setVisibility(View.VISIBLE);
            mToolbarDoneTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_RESULT_TYPE, Constants.ACTIVITY_DETAILED_INFO_TOOLBAR_TITLE);
                    returnIntent.putExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_RESULT, inputNotes.getText().toString());
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            });

            mDetailedItemContainer.addView(inputNotes);
        }
    }

    @Override
    protected void onPause() {
        overridePendingTransition(R.anim.scale_up_in, R.anim.push_right_out);
        super.onPause();
    }
}
