package com.lmtri.sharespace.activity.postsharehouse;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lmtri.sharespace.R;
import com.lmtri.sharespace.activity.posthouse.PostHouseDetailedItemActivity;
import com.lmtri.sharespace.customview.CustomEditText;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.ToastHelper;

import java.util.ArrayList;
import java.util.Locale;

public class PostShareHouseDetailedItemActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_post_share_house_detailed);

        mToolbarTitle = getIntent().getStringExtra(Constants.ACTIVITY_DETAILED_ITEM_TOOLBAR_TITLE_EXTRA);
        mDetailsExtra = getIntent().getStringExtra(Constants.ACTIVITY_DETAILED_ITEM_DETAILS_EXTRA);

        mToolbar = (Toolbar) findViewById(R.id.activity_post_share_house_detail_type_toolbar);
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

        mToolbarTitleTextView = (TextView) findViewById(R.id.activity_post_share_house_detailed_item_toolbar_title);
        mToolbarTitleTextView.setText(mToolbarTitle);

        mToolbarDoneTextView = (TextView) findViewById(R.id.activity_post_share_house_detailed_item_toolbar_done);

        mDetailedItemContainer = (LinearLayout) findViewById(R.id.activity_post_share_house_detailed_item_container);

        mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (mToolbarTitle.equalsIgnoreCase(Constants.ACTIVITY_PRICE_PER_MONTH_OF_ONE_TOOLBAR_TITLE)) {
            buildPriceActivityLayout();
        } else if (mToolbarTitle.equalsIgnoreCase(Constants.ACTIVITY_DESCRIPTION_TOOLBAR_TITLE)) {
            buildDescriptionActivityLayout();
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

        for (final String rowData : Constants.SHARE_PRICE_UNITS) {
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

    private void buildDescriptionActivityLayout() {
        final CustomEditText inputNotes = new CustomEditText(this);
        inputNotes.setHint(getString(R.string.activity_post_share_house_detailed_item_description_hint));
        inputNotes.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        inputNotes.setFilters(
                new InputFilter[] {
                        new InputFilter.LengthFilter(
                                Constants.ACTIVITY_DETAILED_ITEM_DESCRIPTION_NUM_CHARACTERS_LIMIT
                        )
                }
        );
        if (!TextUtils.isEmpty(mDetailsExtra)) {
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
