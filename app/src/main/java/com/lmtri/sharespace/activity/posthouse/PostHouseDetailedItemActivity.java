package com.lmtri.sharespace.activity.posthouse;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lmtri.sharespace.R;
import com.lmtri.sharespace.customview.CustomEditText;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.KeyboardHelper;
import com.lmtri.sharespace.helper.ToastHelper;

import java.util.ArrayList;
import java.util.Locale;

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
        setContentView(R.layout.activity_post_house_detailed);

        mToolbarTitle = getIntent().getStringExtra(Constants.ACTIVITY_DETAILED_ITEM_TOOLBAR_TITLE_EXTRA);
        mDetailsExtra = getIntent().getStringExtra(Constants.ACTIVITY_DETAILED_ITEM_DETAILS_EXTRA);

        mToolbar = (Toolbar) findViewById(R.id.activity_post_house_detail_type_toolbar);
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

        mToolbarTitleTextView = (TextView) findViewById(R.id.activity_post_house_detailed_item_toolbar_title);
        mToolbarTitleTextView.setText(mToolbarTitle);

        mToolbarDoneTextView = (TextView) findViewById(R.id.activity_post_house_detailed_item_toolbar_done);

        mDetailedItemContainer = (LinearLayout) findViewById(R.id.activity_post_house_detailed_item_container);

        mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (mToolbarTitle.equalsIgnoreCase(Constants.ACTIVITY_HOUSE_TITLE_TOOLBAR_TITLE)) {
            buildHouseTitleActivityLayout();
        } else if (mToolbarTitle.equalsIgnoreCase(Constants.ACTIVITY_HOUSE_TYPE_TOOLBAR_TITLE)) {
            buildHouseTypeActivityLayout();
        } else if (mToolbarTitle.equalsIgnoreCase(Constants.ACTIVITY_ADDRESS_TOOLBAR_TITLE)) {
            buildAddressActivityLayout();
        } else if (mToolbarTitle.equalsIgnoreCase(Constants.ACTIVITY_HOUSE_DIRECTION_TOOLBAR_TITLE)) {
            buildHouseDirectionActivityLayout();
        } else if (mToolbarTitle.equalsIgnoreCase(Constants.ACTIVITY_PRICE_TOOLBAR_TITLE)) {
            buildPriceActivityLayout();
        } else if (mToolbarTitle.equalsIgnoreCase(Constants.ACTIVITY_CONTACT_TOOLBAR_TITLE)) {
            buildContactActivityLayout();
        } else if (mToolbarTitle.equalsIgnoreCase(Constants.ACTIVITY_DESCRIPTION_TOOLBAR_TITLE)) {
            buildDescriptionActivityLayout();
        }
    }

    private void buildHouseTitleActivityLayout() {
        final CustomEditText inputHouseTitle = new CustomEditText(this);
        inputHouseTitle.setHint(getString(R.string.activity_post_house_detailed_item_house_title_hint));
        inputHouseTitle.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        inputHouseTitle.setFilters(
                new InputFilter[] {
                        new InputFilter.LengthFilter(
                                Constants.ACTIVITY_DETAILED_ITEM_HOUSE_TITLE_NUM_CHARACTERS_LIMIT
                        )
                }
        );
        if (!TextUtils.isEmpty(mDetailsExtra)) {
            inputHouseTitle.setText(mDetailsExtra);
        }
        inputHouseTitle.setBackground(ContextCompat.getDrawable(this, R.drawable.edittext_notes));
        LinearLayout.LayoutParams inputNotesLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        inputHouseTitle.setPadding(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics())
        );
        inputHouseTitle.setGravity(Gravity.TOP);
        inputHouseTitle.setLayoutParams(inputNotesLayoutParams);

        mToolbarDoneTextView.setVisibility(View.VISIBLE);
        mToolbarDoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_RESULT, inputHouseTitle.getText().toString());
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });

        mDetailedItemContainer.addView(inputHouseTitle);
    }

    private void buildHouseTypeActivityLayout() {
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
                    returnIntent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_RESULT, houseType.getText());
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
    }

    private void buildAddressActivityLayout() {
        mDetailedItemContainer.setFocusable(true);
        mDetailedItemContainer.setFocusableInTouchMode(true);
        mDetailedItemContainer.requestFocus();
        View customSpinner = mLayoutInflater.inflate(R.layout.custom_spinner, null);
        final Spinner citySpinner = (Spinner) customSpinner
                .findViewById(R.id.activity_post_house_detailed_item_city_spinner);
        citySpinner.setSelection(0);
        citySpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                KeyboardHelper.hideSoftKeyboard(PostHouseDetailedItemActivity.this, false);
                citySpinner.performClick();
                return true;
            }
        });
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mDetailedItemText.setText(Constants.ADDRESS_FIELDS.get(Constants.ADDRESS_FIELDS.size() - 1));
                for (int i = 0; i < mAddressEditTexts.size() - 1; ++i) {
                    if (!mAddressEditTexts.get(i).getText().toString().isEmpty()) {
                        if (i == 0) {
                            mDetailedItemText.setText(mDetailedItemText.getText()
                                    + mAddressEditTexts.get(i).getText().toString() + ", ");
                        } else if (i == 2) {
                            mDetailedItemText.setText(mDetailedItemText.getText()
//                                    + getString(R.string.activity_post_house_detailed_item_address_ward_acronym)
                                    + mAddressEditTexts.get(i).getText().toString() + ", ");
                        } else if (i == 3) {
                            mDetailedItemText.setText(mDetailedItemText.getText()
//                                    + getString(R.string.activity_post_house_detailed_item_address_district_acronym)
                                    + mAddressEditTexts.get(i).getText().toString() + ", ");
                        } else {
                            mDetailedItemText.setText(mDetailedItemText.getText()
                                    + mAddressEditTexts.get(i).getText().toString() + ", ");
                        }
                    }
                }
                mDetailedItemText.setText(
                        mDetailedItemText.getText().toString()
                        + parent.getItemAtPosition(position).toString()
                );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mToolbarDoneTextView.setVisibility(View.VISIBLE);
        mToolbarDoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_HOUSE_NUMBER_RESULT,
                                mAddressEditTexts.get(0).getText().toString());
                returnIntent.putExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_STREET_RESULT,
                                mAddressEditTexts.get(1).getText().toString());
                returnIntent.putExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_WARD_RESULT,
                                mAddressEditTexts.get(2).getText().toString());
                returnIntent.putExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_DISTRICT_RESULT,
                                mAddressEditTexts.get(3).getText().toString());
                returnIntent.putExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_CITY_RESULT,
                                citySpinner.getSelectedItem().toString());
                setResult(RESULT_OK, returnIntent);

                if (!mAddressEditTexts.get(3).getText().toString().isEmpty()) {      // District exists.
                    if (mAddressEditTexts.get(0).getText().toString().isEmpty()) {          // House Number not exist.
                        finish();
                    } else {    // House Number exists.
                        if (!mAddressEditTexts.get(1).getText().toString().isEmpty()) {     // Street exists.
                            finish();
                        } else {        // Street don't exist.
                            String errorMessage = getString(R.string.activity_post_house_detailed_item_address_error_message_missing) + getString(R.string.activity_post_house_detailed_item_address_error_message_missing_street);
                            errorMessage = errorMessage.substring(0, errorMessage.length() - 2);

                            ToastHelper.showCenterToast(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
                        }
                    }
                } else {
                    String errorMessage = getString(R.string.activity_post_house_detailed_item_address_error_message_missing);
                    if (mAddressEditTexts.get(3).getText().toString().isEmpty()) {  // District not exist.
                        errorMessage += getString(R.string.activity_post_house_detailed_item_address_error_message_missing_district);
                    }
                    errorMessage = errorMessage.substring(0, errorMessage.length() - 2);

                    ToastHelper.showCenterToast(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
                }
            }
        });

        String houseNumber = getIntent().getStringExtra(Constants.ACTIVITY_DETAILED_ITEM_ADDRESS_HOUSE_NUMBER_EXTRA);
        String street = getIntent().getStringExtra(Constants.ACTIVITY_DETAILED_ITEM_ADDRESS_STREET_EXTRA);
        String ward = getIntent().getStringExtra(Constants.ACTIVITY_DETAILED_ITEM_ADDRESS_WARD_EXTRA);
        String district = getIntent().getStringExtra(Constants.ACTIVITY_DETAILED_ITEM_ADDRESS_DISTRICT_EXTRA);
        String city = getIntent().getStringExtra(Constants.ACTIVITY_DETAILED_ITEM_ADDRESS_CITY_EXTRA);

        for (CharSequence rowData : Constants.ADDRESS_FIELDS) {
            View item = mLayoutInflater.inflate(R.layout.activity_post_house_detailed_item, null);
            mDetailedItemText = (TextView) item.findViewById(R.id.activity_post_house_detailed_item_name);

            CustomEditText inputAddress = new CustomEditText(this);
            if (rowData.toString()
                    .equalsIgnoreCase(Constants.ADDRESS_FIELDS.get(0).toString())) {    // House Number Field.
//                inputAddress.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE);
                inputAddress.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                inputAddress.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            } else if (rowData.toString()
                    .equalsIgnoreCase(Constants.ADDRESS_FIELDS.get(Constants.ADDRESS_FIELDS.size() - 3).toString())) {
                // District Field.
                inputAddress.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                inputAddress.setImeOptions(EditorInfo.IME_ACTION_DONE);
                inputAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            mDetailedItemContainer.setSelected(true);
                            return true;
                        }
                        return false;
                    }
                });
            } else {
                inputAddress.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
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
                        if (!mAddressEditTexts.get(i).getText().toString().isEmpty()) {
                            if (i == 0) {
                                mDetailedItemText.setText(mDetailedItemText.getText()
                                        + mAddressEditTexts.get(i).getText().toString() + ", ");
                            } else if (i == 2) {
                                mDetailedItemText.setText(mDetailedItemText.getText()
//                                        + getString(R.string.activity_post_house_detailed_item_address_ward_acronym)
                                        + mAddressEditTexts.get(i).getText().toString() + ", ");
                            } else if (i == 3) {
                                mDetailedItemText.setText(mDetailedItemText.getText()
//                                        + getString(R.string.activity_post_house_detailed_item_address_district_acronym)
                                        + mAddressEditTexts.get(i).getText().toString() + ", ");
                            } else {
                                mDetailedItemText.setText(mDetailedItemText.getText()
                                        + mAddressEditTexts.get(i).getText().toString() + ", ");
                            }
                        }
                    }
                    mDetailedItemText.setText(
                            mDetailedItemText.getText().toString()
                            + citySpinner.getSelectedItem().toString()
                    );
                }
            });
            if (rowData.toString().equalsIgnoreCase(Constants.ADDRESS_FIELDS.get(0).toString())
                    && !TextUtils.isEmpty(houseNumber)) {
                inputAddress.setText(houseNumber);
            } else if (rowData.toString().equalsIgnoreCase(Constants.ADDRESS_FIELDS.get(1).toString())
                    && !TextUtils.isEmpty(street)) {
                inputAddress.setText(street);
            } else if (rowData.toString().equalsIgnoreCase(Constants.ADDRESS_FIELDS.get(2).toString())
                    && !TextUtils.isEmpty(ward)) {
                inputAddress.setText(ward);
            } else if (rowData.toString().equalsIgnoreCase(Constants.ADDRESS_FIELDS.get(3).toString())
                    && !TextUtils.isEmpty(district)) {
                inputAddress.setText(district);
            } else if (rowData.toString().equalsIgnoreCase(Constants.ADDRESS_FIELDS.get(4).toString())
                    && !TextUtils.isEmpty(district)) {
                inputAddress.setText(city);
            }

            mDetailedItemContainer.addView(item);
            if (!rowData.toString()
                    .equalsIgnoreCase(Constants.ADDRESS_FIELDS.get(Constants.ADDRESS_FIELDS.size() - 1).toString())) {
                mDetailedItemText.setText(rowData);

                mAddressEditTexts.add(inputAddress);
                if (rowData.toString()
                        .equalsIgnoreCase(Constants.ADDRESS_FIELDS.get(Constants.ADDRESS_FIELDS.size() - 2).toString())) {
                    mDetailedItemContainer.addView(customSpinner);
                } else {
                    mDetailedItemContainer.addView(inputAddress);
                }
            } else {
                mDetailedItemText.setText(Constants.ADDRESS_FIELDS.get(Constants.ADDRESS_FIELDS.size() - 1));
                if (!TextUtils.isEmpty(district) && !TextUtils.isEmpty(city)) {
                    mDetailedItemText.setText(Constants.ADDRESS_FIELDS.get(Constants.ADDRESS_FIELDS.size() - 1)
//                            + getString(R.string.activity_post_house_detailed_item_address_district_acronym)
                            + district + ", "
                            + city
                    );
                    if (!TextUtils.isEmpty(houseNumber) && !TextUtils.isEmpty(street)) {
                        mDetailedItemText.setText(Constants.ADDRESS_FIELDS.get(Constants.ADDRESS_FIELDS.size() - 1)
                                + houseNumber + ", "
                                + street + ", "
//                                + getString(R.string.activity_post_house_detailed_item_address_district_acronym)
                                + district + ", "
                                + city
                        );
                    }
                    if (!TextUtils.isEmpty(ward)) {
                        mDetailedItemText.setText(Constants.ADDRESS_FIELDS.get(Constants.ADDRESS_FIELDS.size() - 1)
                                + houseNumber + ", "
                                + street + ", "
//                                + getString(R.string.activity_post_house_detailed_item_address_ward_acronym)
                                + ward + ", "
//                                + getString(R.string.activity_post_house_detailed_item_address_district_acronym)
                                + district + ", "
                                + city
                        );
                    }
                }
            }
        }
    }

    private void buildHouseDirectionActivityLayout() {
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
                    returnIntent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_RESULT, houseDirection.getText());
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
    }

    private void buildPriceActivityLayout() {
        final TextView shownPriceSection = new TextView(this);
        shownPriceSection.setBackgroundColor(ContextCompat.getColor(this, R.color.clouds));
        shownPriceSection.setPadding(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()),
                0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics())
        );
        shownPriceSection.setText(getString(R.string.activity_post_house_detailed_item_price_shown));

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
                for (View priceUnitField : mPriceUnitFields) {
                    if (priceUnitField.findViewById(R.id.activity_post_house_detailed_item_check_mark).getVisibility() == View.VISIBLE) {
                        TextView priceUnit = (TextView) priceUnitField.findViewById(R.id.activity_post_house_detailed_item_name);
                        if (!priceUnit.getText().toString().equalsIgnoreCase(Constants.PRICE_UNITS.get(Constants.PRICE_UNITS.size() - 1))) {
                            shownPriceSection.setText(
                                    getString(R.string.activity_post_house_detailed_item_price_shown) + s.toString() + " " + priceUnit.getText());
                        } else {    // Price Unit is 'Deal'.
                            shownPriceSection.setText(
                                    getString(R.string.activity_post_house_detailed_item_price_shown) + priceUnit.getText());
                        }
                    }
                }
            }
        });
        float price = getIntent().getFloatExtra(Constants.ACTIVITY_DETAILED_ITEM_PRICE_EXTRA, 0);
        String priceUnit = getIntent().getStringExtra(Constants.ACTIVITY_DETAILED_ITEM_PRICE_UNIT_EXTRA);
        if (!TextUtils.isEmpty(priceUnit)) {
            if (!priceUnit.equalsIgnoreCase(Constants.PRICE_UNITS.get(Constants.PRICE_UNITS.size() - 1))) {
                shownPriceSection.setText(getString(R.string.activity_post_house_detailed_item_price_shown) + price + " " + priceUnit);
                inputPrice.setText(String.format(Locale.US, "%.2f", price));
            } else {    // Price Unit is 'Deal'.
                shownPriceSection.setText(getString(R.string.activity_post_house_detailed_item_price_shown) + priceUnit);
            }
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
                String priceUnit = "";
                for (View priceUnitField : mPriceUnitFields) {
                    if (priceUnitField.findViewById(R.id.activity_post_house_detailed_item_check_mark)
                            .getVisibility() == View.VISIBLE) {
                        priceUnit = ((TextView) priceUnitField
                                .findViewById(R.id.activity_post_house_detailed_item_name))
                                .getText().toString();
                    }
                }
                Intent returnIntent = new Intent();
                if (!TextUtils.isEmpty(priceUnit)) {
                    if (priceUnit.equalsIgnoreCase(Constants.PRICE_UNITS.get(Constants.PRICE_UNITS.size() - 1))) {
                        // Price Unit is 'Deal'.
                        returnIntent.putExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_PRICE_RESULT, 0f);
                        returnIntent.putExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_PRICE_UNIT_RESULT, priceUnit);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    } else if (!inputPrice.getText().toString().isEmpty()) {
                        returnIntent.putExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_PRICE_RESULT, Float.parseFloat(inputPrice.getText().toString()));
                        returnIntent.putExtra(Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_PRICE_UNIT_RESULT, priceUnit);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    } else {
                        ToastHelper.showCenterToast(
                                getApplicationContext(),
                                getString(R.string.activity_post_house_price_not_input_price_alert),
                                Toast.LENGTH_SHORT
                        );
                    }
                } else {
                    ToastHelper.showCenterToast(
                            getApplicationContext(),
                            getString(R.string.activity_post_house_price_not_input_price_unit_alert),
                            Toast.LENGTH_SHORT
                    );
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
                    for (int i = 0; i < mPriceUnitFields.size(); ++i) {
                        View priceUnitField = mPriceUnitFields.get(i);
                        ImageView childCheckMark = (ImageView) priceUnitField.findViewById(R.id.activity_post_house_detailed_item_check_mark);
                        childCheckMark.setVisibility(View.INVISIBLE);
                    }

                    if (!priceUnit.getText().toString().equalsIgnoreCase(Constants.PRICE_UNITS.get(Constants.PRICE_UNITS.size() - 1))) {
                        if (!TextUtils.isEmpty(inputPrice.getText().toString())) {
                            shownPriceSection.setText(
                                    getString(R.string.activity_post_house_detailed_item_price_shown) + inputPrice.getText().toString() + " " + priceUnit.getText());

                            checkMark.setVisibility(View.VISIBLE);
                        } else {
                            ToastHelper.showCenterToast(
                                    getApplicationContext(),
                                    getString(R.string.activity_post_house_price_not_input_price_alert),
                                    Toast.LENGTH_SHORT);
                        }
                    } else {    // Price Unit is 'Deal'.
                        shownPriceSection.setText(
                                getString(R.string.activity_post_house_detailed_item_price_shown) + priceUnit.getText());

                        checkMark.setVisibility(View.VISIBLE);
                    }
                }
            });

            mDetailedItemText = (TextView) item.findViewById(R.id.activity_post_house_detailed_item_name);
            mDetailedItemText.setText(rowData);

            mDetailedItemCheckMark = (ImageView) item.findViewById(R.id.activity_post_house_detailed_item_check_mark);
            if (!TextUtils.isEmpty(priceUnit)) {
                if (rowData.equalsIgnoreCase(priceUnit)) {
                    mDetailedItemCheckMark.setVisibility(View.VISIBLE);
                }
            }
            mPriceUnitFields.add(item);
            mDetailedItemContainer.addView(item);
        }
    }

    private void buildContactActivityLayout() {
        mToolbarDoneTextView.setVisibility(View.VISIBLE);
        mToolbarDoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(
                        Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_CONTACT_NAME_RESULT,
                        mContactEditTexts.get(0).getText().toString()
                );
                returnIntent.putExtra(
                        Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_CONTACT_NUMBER_RESULT,
                        mContactEditTexts.get(1).getText().toString()
                );
                returnIntent.putExtra(
                        Constants.START_ACTIVITY_DETAILED_ITEM_REQUEST_CONTACT_EMAIL_RESULT,
                        mContactEditTexts.get(2).getText().toString()
                );
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
    }

    private void buildDescriptionActivityLayout() {
        final CustomEditText inputNotes = new CustomEditText(this);
        inputNotes.setHint(getString(R.string.activity_post_house_detailed_item_description_hint));
        inputNotes.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        inputNotes.setFilters(
                new InputFilter[] {
                        new InputFilter.LengthFilter(
                                Constants.ACTIVITY_DETAILED_ITEM_DESCRIPTION_NUM_CHARACTERS_LIMIT
                        )
                }
        );
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
                returnIntent.putExtra(Constants.ACTIVITY_DETAILED_ITEM_RESULT, inputNotes.getText().toString());
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });

        mDetailedItemContainer.addView(inputNotes);
    }

    @Override
    protected void onPause() {
        overridePendingTransition(R.anim.scale_up_in, R.anim.push_right_out);
        super.onPause();
    }
}
