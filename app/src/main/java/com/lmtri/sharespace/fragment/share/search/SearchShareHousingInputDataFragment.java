package com.lmtri.sharespace.fragment.share.search;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.lmtri.sharespace.R;
import com.lmtri.sharespace.ShareSpaceApplication;
import com.lmtri.sharespace.api.model.SearchShareHousingData;
import com.lmtri.sharespace.api.model.ShareHousing;
import com.lmtri.sharespace.api.service.RetrofitClient;
import com.lmtri.sharespace.api.service.sharehousing.ShareHousingClient;
import com.lmtri.sharespace.api.service.sharehousing.search.ISearchShareHousingCallback;
import com.lmtri.sharespace.customview.CustomEditText;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.KeyboardHelper;
import com.lmtri.sharespace.helper.ToastHelper;
import com.lmtri.sharespace.helper.busevent.sharehousing.search.ReturnSearchShareHousingResultEvent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchShareHousingInputDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchShareHousingInputDataFragment extends Fragment {

    public static final String TAG = SearchShareHousingInputDataFragment.class.getSimpleName();

    private Toolbar mToolbar;

    private SupportPlaceAutocompleteFragment mPlaceAutocompleteFragment;
    private Place mSelectedPlace;

    private TextView mRadiusText;
    private SeekBar mRadiusBar;
    private int mRadiusInKm = Constants.SEARCH_HOUSING_INITIAL_RADIUS_IN_KM;

    private CustomEditText mInputKeywords;

    private LinearLayout mHouseTypeOption;
    private String[] mHouseTypeArray;
    private boolean[] mIsSelectedHouseTypeArray;
    private TextView mHouseTypesText;

    private List<Integer> mFullIntPriceList;
    private List<Integer> mMaxIntPriceList;
    private Spinner mMinPriceSpinner;
    private List<String> mMinStringPriceList;
    private ArrayAdapter<String> mMinPriceSpinnerAdapter;
    private Spinner mMaxPriceSpinner;
    private List<String> mMaxStringPriceList;
    private ArrayAdapter<String> mMaxPriceSpinnerAdapter;

    private List<Integer> mFullIntAreaList;
    private List<Integer> mMaxIntAreaList;
    private Spinner mMinAreaSpinner;
    private List<CharSequence> mMinStringAreaList;
    private ArrayAdapter<CharSequence> mMinAreaSpinnerAdapter;
    private Spinner mMaxAreaSpinner;
    private List<CharSequence> mMaxStringAreaList;
    private ArrayAdapter<CharSequence> mMaxAreaSpinnerAdapter;

    private LinearLayout mNumPeople;
    private int mSelectedNumPeople = 0;
    private LinearLayout mNumRoom;
    private int mSelectedNumRoom = 0;
    private LinearLayout mNumBed;
    private int mSelectedNumBed = 0;
    private LinearLayout mNumBath;
    private int mSelectedNumBath = 0;

    private LinearLayout mAmenitiesOption;
    private String[] mAmenityArray;
    private boolean[] mIsSelectedAmenityArray;
    private TextView mAmenitiesText;

    private List<Integer> mFullIntTimeRestrictionList;
    private List<Integer> mMaxIntTimeRestrictionList;
    private Spinner mMinTimeRestrictionSpinner;
    private List<String> mMinStringTimeRestrictionList;
    private ArrayAdapter<String> mMinTimeRestrictionSpinnerAdapter;
    private Spinner mMaxTimeRestrictionSpinner;
    private List<String> mMaxStringTimeRestrictionList;
    private ArrayAdapter<String> mMaxTimeRestrictionSpinnerAdapter;

    private LinearLayout mNumRoommate;
    private int mSelectedNumRoommate = 1;

    private Spinner mRequiredGenderSpinner;
    private Spinner mRequiredWorkTypeSpinner;

    private LinearLayout mOtherInfoOption;
    private String[] mOtherInfoArray;
    private boolean[] mIsSelectedOtherInfoArray;
    private TextView mOtherInfoText;

    private TextView mResetSearchingDataButton;
    private TextView mSearchButton;

    public SearchShareHousingInputDataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchShareHousingInputDataFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchShareHousingInputDataFragment newInstance() {
        SearchShareHousingInputDataFragment fragment = new SearchShareHousingInputDataFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_share_housing_input_data, container, false);

        mToolbar = (Toolbar) view.findViewById(R.id.fragment_search_housing_input_data_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        if (mToolbar.getNavigationIcon() != null) {
            mToolbar.getNavigationIcon().setColorFilter(
                    ContextCompat.getColor(getContext(), android.R.color.white),
                    PorterDuff.Mode.SRC_ATOP
            );
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.stay_still, R.anim.slide_down_out);
            }
        });

        mPlaceAutocompleteFragment = (SupportPlaceAutocompleteFragment) getChildFragmentManager()
                .findFragmentById(R.id.fragment_search_housing_input_data_places_autocomplete_fragment);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            ((EditText) mPlaceAutocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setTextAppearance(getContext(), android.R.style.TextAppearance_Medium);
        } else {
            ((EditText) mPlaceAutocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setTextAppearance(android.R.style.TextAppearance_Medium);
        }
        mPlaceAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mSelectedPlace = place;
            }

            @Override
            public void onError(Status status) {

            }
        });
        AutocompleteFilter filter = new AutocompleteFilter.Builder()
                .setCountry("VN")
                .build();
        mPlaceAutocompleteFragment.setFilter(filter);

        mRadiusText = (TextView) view.findViewById(R.id.fragment_search_housing_input_data_radius);
        mRadiusText.setText(getString(R.string.fragment_search_housing_input_data_radius_section, Constants.SEARCH_HOUSING_INITIAL_RADIUS_IN_KM));
        mRadiusBar = (SeekBar) view.findViewById(R.id.fragment_search_housing_input_data_radius_bar);
        mRadiusBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mRadiusInKm = progress;
                    mRadiusText.setText(getString(R.string.fragment_search_housing_input_data_radius_section, progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                KeyboardHelper.hideSoftKeyboard(getActivity(), false);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mRadiusBar.setMax(Constants.SEARCH_HOUSING_MAX_RADIUS_IN_KM);
        mRadiusBar.setProgress(Constants.SEARCH_HOUSING_INITIAL_RADIUS_IN_KM);

        mInputKeywords = (CustomEditText) view.findViewById(R.id.fragment_search_housing_input_data_key_word_input);

        mHouseTypeOption = (LinearLayout) view.findViewById(R.id.fragment_search_housing_input_data_house_type_layout);
        mHouseTypeOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardHelper.hideSoftKeyboard(getActivity(), false);
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.fragment_search_housing_input_data_house_type_option_dialog_title)
                        .setMultiChoiceItems(mHouseTypeArray, mIsSelectedHouseTypeArray,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                        if (which != 0) {
                                            if (mIsSelectedHouseTypeArray[0]) {
                                                mIsSelectedHouseTypeArray[0] = false;
                                                ((AlertDialog) dialog).getListView().setItemChecked(0, false);
                                            }
                                        } else {
                                            for (int i = 0; i < mIsSelectedHouseTypeArray.length; ++i) {
                                                if (i != 0) {
                                                    mIsSelectedHouseTypeArray[i] = false;
                                                    ((AlertDialog) dialog).getListView().setItemChecked(i, false);
                                                }
                                            }
                                        }
                                    }
                                })
                        .setPositiveButton(R.string.fragment_search_housing_input_data_house_type_option_dialog_positive, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String houseTypes = "";
                                for (int i = 0; i < mIsSelectedHouseTypeArray.length; ++i) {
                                    if (mIsSelectedHouseTypeArray[i] == true) {
                                        houseTypes += mHouseTypeArray[i] + ", ";
                                    }
                                }
                                mHouseTypesText.setText(houseTypes.substring(0, houseTypes.length() - 2));
                            }
                        })
                        .setNegativeButton(R.string.fragment_search_housing_input_data_house_type_option_dialog_negative, null)
                        .show();
            }
        });
        mHouseTypeArray = getResources().getStringArray(R.array.fragment_search_housing_input_data_house_type_array);
        mIsSelectedHouseTypeArray = new boolean[mHouseTypeArray.length];
        for (int i = 0; i < mIsSelectedHouseTypeArray.length; ++i) {
            mIsSelectedHouseTypeArray[i] = (i == 0) ? true : false;
        }
        mHouseTypesText = (TextView) view.findViewById(R.id.fragment_search_housing_input_data_house_types);

        mFullIntPriceList = new ArrayList<>();
        mMaxIntPriceList = new ArrayList<>();
        int[] fullIntPriceArray = getResources().getIntArray(R.array.fragment_search_housing_input_data_price_integer_array);
        for (int i = 0; i < fullIntPriceArray.length; ++i) {
            mFullIntPriceList.add(fullIntPriceArray[i]);
            mMaxIntPriceList.add(fullIntPriceArray[i]);
        }
        mMinPriceSpinner = (Spinner) view.findViewById(R.id.fragment_search_housing_input_data_min_price_per_month_of_one_spinner);
        mMinPriceSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                KeyboardHelper.hideSoftKeyboard(getActivity(), false);
                return false;
            }
        });
        mMinPriceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 1) {
                    int previousMaxPrice = mMaxIntPriceList.get(mMaxPriceSpinner.getSelectedItemPosition());
                    mMaxStringPriceList.clear();
                    mMaxStringPriceList.add(mMinStringPriceList.get(0));
                    mMaxStringPriceList.add(mMinStringPriceList.get(1));
                    mMaxIntPriceList.clear();
                    mMaxIntPriceList.add(mFullIntPriceList.get(0));
                    mMaxIntPriceList.add(mFullIntPriceList.get(1));
                    for (int i = position; i < mMinStringPriceList.size(); ++i) {
                        mMaxStringPriceList.add(mMinStringPriceList.get(i));
                        mMaxIntPriceList.add(mFullIntPriceList.get(i));
                    }
                    if (mFullIntPriceList.get(position) > previousMaxPrice) {
                        mMaxPriceSpinnerAdapter.notifyDataSetChanged();
                        mMaxPriceSpinner.setSelection(0);
                    } else {
                        mMaxPriceSpinnerAdapter.notifyDataSetChanged();
                    }
                } else if (position == 1) {
                    mMaxStringPriceList.clear();
                    mMaxStringPriceList.add(mMinStringPriceList.get(1));
                    mMaxIntPriceList.clear();
                    mMaxIntPriceList.add(mFullIntPriceList.get(1));

                    mMaxPriceSpinnerAdapter.notifyDataSetChanged();

                    mMaxPriceSpinner.setSelection(0);
                } else if (position == 0) {
                    mMaxStringPriceList.clear();
                    mMaxIntPriceList.clear();
                    for (int i = 0; i < mMinStringPriceList.size(); ++i) {
                        mMaxStringPriceList.add(mMinStringPriceList.get(i));
                        mMaxIntPriceList.add(mFullIntPriceList.get(i));
                    }
                    mMaxPriceSpinnerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mMinStringPriceList = new ArrayList<>(
                Arrays.asList(getResources()
                        .getStringArray(R.array.fragment_search_housing_input_data_price_array)
                )
        );
        mMinPriceSpinnerAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, mMinStringPriceList
        );
        mMinPriceSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMinPriceSpinner.setAdapter(mMinPriceSpinnerAdapter);

        mMaxPriceSpinner = (Spinner) view.findViewById(R.id.fragment_search_housing_input_data_max_price_per_month_of_one_spinner);
        mMaxPriceSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                KeyboardHelper.hideSoftKeyboard(getActivity(), false);
                return false;
            }
        });
        mMaxPriceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mMaxStringPriceList.size() > 1) {
                    if (position == 1) {
                        mMinPriceSpinner.setSelection(1);

                        mMaxStringPriceList.clear();
                        mMaxStringPriceList.add(mMinStringPriceList.get(1));
                        mMaxIntPriceList.clear();
                        mMaxIntPriceList.add(mFullIntPriceList.get(1));

                        mMaxPriceSpinnerAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mMaxStringPriceList = new ArrayList<>(
                Arrays.asList(getResources()
                        .getStringArray(R.array.fragment_search_housing_input_data_price_array)
                )
        );
        mMaxPriceSpinnerAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, mMaxStringPriceList
        );
        mMaxPriceSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMaxPriceSpinner.setAdapter(mMaxPriceSpinnerAdapter);

        mFullIntAreaList = new ArrayList<>();
        mMaxIntAreaList = new ArrayList<>();
        int[] fullIntAreaArray = getResources().getIntArray(R.array.fragment_search_housing_input_data_area_integer_array);
        for (int i = 0; i < fullIntAreaArray.length; ++i) {
            mFullIntAreaList.add(fullIntAreaArray[i]);
            mMaxIntAreaList.add(fullIntAreaArray[i]);
        }
        mMinAreaSpinner = (Spinner) view.findViewById(R.id.fragment_search_housing_input_data_min_area_spinner);
        mMinAreaSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                KeyboardHelper.hideSoftKeyboard(getActivity(), false);
                return false;
            }
        });
        mMinAreaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    int previousMaxArea = mMaxIntAreaList.get(mMaxAreaSpinner.getSelectedItemPosition());
                    mMaxStringAreaList.clear();
                    mMaxStringAreaList.add(mMinStringAreaList.get(0));
                    mMaxIntAreaList.clear();
                    mMaxIntAreaList.add(mFullIntAreaList.get(0));
                    for (int i = position; i < mMinStringAreaList.size(); ++i) {
                        mMaxStringAreaList.add(mMinStringAreaList.get(i));
                        mMaxIntAreaList.add(mFullIntAreaList.get(i));
                    }
                    if (mFullIntAreaList.get(position) > previousMaxArea) {
                        mMaxAreaSpinnerAdapter.notifyDataSetChanged();
                        mMaxAreaSpinner.setSelection(0);
                    } else {
                        mMaxAreaSpinnerAdapter.notifyDataSetChanged();
                    }
                } else if (position == 0) {
                    mMaxStringAreaList.clear();
                    mMaxIntAreaList.clear();
                    for (int i = 0; i < mMinStringAreaList.size(); ++i) {
                        mMaxStringAreaList.add(mMinStringAreaList.get(i));
                        mMaxIntAreaList.add(mFullIntAreaList.get(i));
                    }
                    mMaxAreaSpinnerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mMinStringAreaList = new ArrayList<>(Arrays.asList(
                getResources()
                        .getTextArray(R.array.fragment_search_housing_input_data_area_array)
        ));
        mMinAreaSpinnerAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, mMinStringAreaList
        );
        mMinAreaSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMinAreaSpinner.setAdapter(mMinAreaSpinnerAdapter);

        mMaxAreaSpinner = (Spinner) view.findViewById(R.id.fragment_search_housing_input_data_max_area_spinner);
        mMaxAreaSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                KeyboardHelper.hideSoftKeyboard(getActivity(), false);
                return false;
            }
        });
        mMaxStringAreaList = new ArrayList<>(Arrays.asList(
                getResources()
                        .getTextArray(R.array.fragment_search_housing_input_data_area_array)
        ));
        mMaxAreaSpinnerAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, mMaxStringAreaList
        );
        mMaxAreaSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMaxAreaSpinner.setAdapter(mMaxAreaSpinnerAdapter);

        mNumPeople = (LinearLayout) view.findViewById(R.id.fragment_search_housing_input_data_number_picker_num_people_layout);
        for (int i = 0; i < mNumPeople.getChildCount(); ++i) {
            mNumPeople.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    KeyboardHelper.hideSoftKeyboard(getActivity(), false);
                    ((TextView) v).setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    v.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.search_housing_square_option_border_primary_color_background));
                    int tag = Integer.parseInt((String) v.getTag());
                    mSelectedNumPeople = tag;
                    for (int j = 0; j < mNumPeople.getChildCount(); ++j) {
                        if (j != tag) {
                            ((TextView) mNumPeople.getChildAt(j)).setTextColor(ContextCompat.getColor(getContext(), R.color.textColorSecondary));
                            mNumPeople.getChildAt(j).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.search_housing_square_option_border));
                        }
                    }
                }
            });
        }
        mNumRoom = (LinearLayout) view.findViewById(R.id.fragment_search_housing_input_data_number_picker_num_room_layout);
        for (int i = 0; i < mNumRoom.getChildCount(); ++i) {
            mNumRoom.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    KeyboardHelper.hideSoftKeyboard(getActivity(), false);
                    ((TextView) v).setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    v.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.search_housing_square_option_border_primary_color_background));
                    int tag = Integer.parseInt((String) v.getTag());
                    mSelectedNumRoom = tag;
                    for (int j = 0; j < mNumRoom.getChildCount(); ++j) {
                        if (j != tag) {
                            ((TextView) mNumRoom.getChildAt(j)).setTextColor(ContextCompat.getColor(getContext(), R.color.textColorSecondary));;
                            mNumRoom.getChildAt(j).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.search_housing_square_option_border));
                        }
                    }
                }
            });
        }
        mNumBed = (LinearLayout) view.findViewById(R.id.fragment_search_housing_input_data_number_picker_num_bed_layout);
        for (int i = 0; i < mNumBed.getChildCount(); ++i) {
            mNumBed.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    KeyboardHelper.hideSoftKeyboard(getActivity(), false);
                    ((TextView) v).setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    v.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.search_housing_square_option_border_primary_color_background));
                    int tag = Integer.parseInt((String) v.getTag());
                    mSelectedNumBed = tag;
                    for (int j = 0; j < mNumBed.getChildCount(); ++j) {
                        if (j != tag) {
                            ((TextView) mNumBed.getChildAt(j)).setTextColor(ContextCompat.getColor(getContext(), R.color.textColorSecondary));;
                            mNumBed.getChildAt(j).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.search_housing_square_option_border));
                        }
                    }
                }
            });
        }
        mNumBath = (LinearLayout) view.findViewById(R.id.fragment_search_housing_input_data_number_picker_num_bath_layout);
        for (int i = 0; i < mNumBath.getChildCount(); ++i) {
            mNumBath.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    KeyboardHelper.hideSoftKeyboard(getActivity(), false);
                    ((TextView) v).setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    v.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.search_housing_square_option_border_primary_color_background));
                    int tag = Integer.parseInt((String) v.getTag());
                    mSelectedNumBath = tag;
                    for (int j = 0; j < mNumBath.getChildCount(); ++j) {
                        if (j != tag) {
                            ((TextView) mNumBath.getChildAt(j)).setTextColor(ContextCompat.getColor(getContext(), R.color.textColorSecondary));;
                            mNumBath.getChildAt(j).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.search_housing_square_option_border));
                        }
                    }
                }
            });
        }

        mAmenitiesOption = (LinearLayout) view.findViewById(R.id.fragment_search_housing_input_data_amenities_layout);
        mAmenitiesOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardHelper.hideSoftKeyboard(getActivity(), false);
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.fragment_search_housing_input_data_amenities_option_dialog_title)
                        .setMultiChoiceItems(mAmenityArray, mIsSelectedAmenityArray,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                        if (which != 0) {
                                            if (mIsSelectedAmenityArray[0]) {
                                                mIsSelectedAmenityArray[0] = false;
                                                ((AlertDialog) dialog).getListView().setItemChecked(0, false);
                                            }
                                        } else {
                                            for (int i = 0; i < mIsSelectedAmenityArray.length; ++i) {
                                                if (i != 0) {
                                                    mIsSelectedAmenityArray[i] = false;
                                                    ((AlertDialog) dialog).getListView().setItemChecked(i, false);
                                                }
                                            }
                                        }
                                    }
                                })
                        .setPositiveButton(R.string.fragment_search_housing_input_data_amenities_option_dialog_positive, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String amenities = "";
                                for (int i = 0; i < mIsSelectedAmenityArray.length; ++i) {
                                    if (mIsSelectedAmenityArray[i] == true) {
                                        amenities += mAmenityArray[i] + ", ";
                                    }
                                }
                                mAmenitiesText.setText(amenities.substring(0, amenities.length() - 2));
                            }
                        })
                        .setNegativeButton(R.string.fragment_search_housing_input_data_amenities_option_dialog_negative, null)
                        .show();
            }
        });
        mAmenityArray = getResources().getStringArray(R.array.fragment_search_housing_input_data_amenity_array);
        mIsSelectedAmenityArray = new boolean[mAmenityArray.length];
        for (int i = 0; i < mIsSelectedAmenityArray.length; ++i) {
            mIsSelectedAmenityArray[i] = (i == 0) ? true : false;
        }
        mAmenitiesText = (TextView) view.findViewById(R.id.fragment_search_housing_input_data_amenities);

        mFullIntTimeRestrictionList = new ArrayList<>();
        mMaxIntTimeRestrictionList = new ArrayList<>();
        int[] fullIntTimeRestrictionArray = getResources().getIntArray(R.array.fragment_search_housing_input_data_time_restriction_integer_array);
        for (int i = 0; i < fullIntTimeRestrictionArray.length; ++i) {
            mFullIntTimeRestrictionList.add(fullIntTimeRestrictionArray[i]);
            mMaxIntTimeRestrictionList.add(fullIntTimeRestrictionArray[i]);
        }
        mMinTimeRestrictionSpinner = (Spinner) view.findViewById(R.id.fragment_search_housing_input_data_min_time_restriction_spinner);
        mMinTimeRestrictionSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                KeyboardHelper.hideSoftKeyboard(getActivity(), false);
                return false;
            }
        });
        mMinTimeRestrictionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    int previousMaxTimeRestriction = mMaxIntTimeRestrictionList.get(mMaxTimeRestrictionSpinner.getSelectedItemPosition());
                    mMaxStringTimeRestrictionList.clear();
                    mMaxStringTimeRestrictionList.add(mMinStringTimeRestrictionList.get(0));
                    mMaxIntTimeRestrictionList.clear();
                    mMaxIntTimeRestrictionList.add(mFullIntTimeRestrictionList.get(0));
                    for (int i = position; i < mMinStringTimeRestrictionList.size(); ++i) {
                        mMaxStringTimeRestrictionList.add(mMinStringTimeRestrictionList.get(i));
                        mMaxIntTimeRestrictionList.add(mFullIntTimeRestrictionList.get(i));
                    }
                    if (mFullIntTimeRestrictionList.get(position) > previousMaxTimeRestriction) {
                        mMaxTimeRestrictionSpinnerAdapter.notifyDataSetChanged();
                        mMaxTimeRestrictionSpinner.setSelection(0);
                    } else {
                        mMaxTimeRestrictionSpinnerAdapter.notifyDataSetChanged();
                    }
                } else if (position == 0) {
                    mMaxStringTimeRestrictionList.clear();
                    mMaxIntTimeRestrictionList.clear();
                    for (int i = 0; i < mMinStringTimeRestrictionList.size(); ++i) {
                        mMaxStringTimeRestrictionList.add(mMinStringTimeRestrictionList.get(i));
                        mMaxIntTimeRestrictionList.add(mFullIntTimeRestrictionList.get(i));
                    }
                    mMaxTimeRestrictionSpinnerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mMinStringTimeRestrictionList = new ArrayList<>(
                Arrays.asList(getResources()
                        .getStringArray(R.array.fragment_search_housing_input_data_time_restriction_array)
                )
        );
        mMinTimeRestrictionSpinnerAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, mMinStringTimeRestrictionList
        );
        mMinTimeRestrictionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMinTimeRestrictionSpinner.setAdapter(mMinTimeRestrictionSpinnerAdapter);

        mMaxTimeRestrictionSpinner = (Spinner) view.findViewById(R.id.fragment_search_housing_input_data_max_time_restriction_spinner);
        mMaxTimeRestrictionSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                KeyboardHelper.hideSoftKeyboard(getActivity(), false);
                return false;
            }
        });
        mMaxStringTimeRestrictionList = new ArrayList<>(
                Arrays.asList(getResources()
                        .getStringArray(R.array.fragment_search_housing_input_data_time_restriction_array)
                )
        );
        mMaxTimeRestrictionSpinnerAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, mMaxStringTimeRestrictionList
        );
        mMaxTimeRestrictionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMaxTimeRestrictionSpinner.setAdapter(mMaxTimeRestrictionSpinnerAdapter);

        mNumRoommate = (LinearLayout) view.findViewById(R.id.fragment_search_share_housing_input_data_number_picker_num_roommate_layout);
        for (int i = 0; i < mNumRoommate.getChildCount(); ++i) {
            mNumRoommate.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    KeyboardHelper.hideSoftKeyboard(getActivity(), false);
                    ((TextView) v).setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    v.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.search_housing_square_option_border_primary_color_background));
                    int tag = Integer.parseInt((String) v.getTag());
                    mSelectedNumRoommate = tag;
                    for (int j = 0; j < mNumRoommate.getChildCount(); ++j) {
                        if (j != tag - 1) {     // tag = [1, 5]
                            ((TextView) mNumRoommate.getChildAt(j)).setTextColor(ContextCompat.getColor(getContext(), R.color.textColorSecondary));;
                            mNumRoommate.getChildAt(j).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.search_housing_square_option_border));
                        }
                    }
                }
            });
        }

        mRequiredGenderSpinner = (Spinner) view.findViewById(R.id.fragment_search_share_housing_input_data_required_gender_spinner);
        mRequiredGenderSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                KeyboardHelper.hideSoftKeyboard(getActivity(), false);
                return false;
            }
        });
        mRequiredWorkTypeSpinner = (Spinner) view.findViewById(R.id.fragment_search_share_housing_input_data_required_work_type_spinner);
        mRequiredWorkTypeSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                KeyboardHelper.hideSoftKeyboard(getActivity(), false);
                return false;
            }
        });

        mOtherInfoOption = (LinearLayout) view.findViewById(R.id.fragment_search_housing_input_data_other_info_layout);
        mOtherInfoOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardHelper.hideSoftKeyboard(getActivity(), false);
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.fragment_search_housing_input_data_house_type_option_dialog_title)
                        .setMultiChoiceItems(mOtherInfoArray, mIsSelectedOtherInfoArray,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                        if (which != 0) {
                                            if (mIsSelectedOtherInfoArray[0]) {
                                                mIsSelectedOtherInfoArray[0] = false;
                                                ((AlertDialog) dialog).getListView().setItemChecked(0, false);
                                            }
                                        } else {
                                            for (int i = 0; i < mIsSelectedOtherInfoArray.length; ++i) {
                                                if (i != 0) {
                                                    mIsSelectedOtherInfoArray[i] = false;
                                                    ((AlertDialog) dialog).getListView().setItemChecked(i, false);
                                                }
                                            }
                                        }
                                    }
                                })
                        .setPositiveButton(R.string.fragment_search_housing_input_data_house_type_option_dialog_positive, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String otherInfo = "";
                                for (int i = 0; i < mIsSelectedOtherInfoArray.length; ++i) {
                                    if (mIsSelectedOtherInfoArray[i] == true) {
                                        otherInfo += mOtherInfoArray[i] + ", ";
                                    }
                                }
                                mOtherInfoText.setText(otherInfo.substring(0, otherInfo.length() - 2));
                            }
                        })
                        .setNegativeButton(R.string.fragment_search_housing_input_data_house_type_option_dialog_negative, null)
                        .show();
            }
        });
        mOtherInfoArray = getResources().getStringArray(R.array.fragment_search_share_housing_input_data_other_info_array);
        mIsSelectedOtherInfoArray = new boolean[mOtherInfoArray.length];
        for (int i = 0; i < mIsSelectedOtherInfoArray.length; ++i) {
            mIsSelectedOtherInfoArray[i] = (i == 0) ? true : false;
        }
        mOtherInfoText = (TextView) view.findViewById(R.id.fragment_search_housing_input_data_other_info);

        mResetSearchingDataButton = (TextView) view.findViewById(R.id.fragment_search_housing_input_data_search_bar_reset_searching_data);
        mResetSearchingDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardHelper.hideSoftKeyboard(getActivity(), false);
                ((EditText) mPlaceAutocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setText("");
                mSelectedPlace = null;

                mRadiusBar.setProgress(Constants.SEARCH_HOUSING_INITIAL_RADIUS_IN_KM);
                mRadiusInKm = Constants.SEARCH_HOUSING_INITIAL_RADIUS_IN_KM;
                mRadiusText.setText(getString(R.string.fragment_search_housing_input_data_radius_section, mRadiusInKm));

                mInputKeywords.setText("");

                for (int i = 0; i < mIsSelectedHouseTypeArray.length; ++i) {
                    mIsSelectedHouseTypeArray[i] = (i == 0) ? true : false;
                }
                mHouseTypesText.setText(mHouseTypeArray[0]);

                mMinPriceSpinner.setSelection(0);

                mMaxStringPriceList.clear();
                mMaxIntPriceList.clear();
                for (int i = 0; i < mMinStringPriceList.size(); ++i) {
                    mMaxStringPriceList.add(mMinStringPriceList.get(i));
                    mMaxIntPriceList.add(mFullIntPriceList.get(i));
                }
                mMaxPriceSpinnerAdapter.notifyDataSetChanged();
                mMaxPriceSpinner.setSelection(0);

                mMinAreaSpinner.setSelection(0);

                mMaxStringAreaList.clear();
                mMaxIntAreaList.clear();
                for (int i = 0; i < mMinStringAreaList.size(); ++i) {
                    mMaxStringAreaList.add(mMinStringAreaList.get(i));
                    mMaxIntAreaList.add(mFullIntAreaList.get(i));
                }
                mMaxAreaSpinnerAdapter.notifyDataSetChanged();
                mMaxAreaSpinner.setSelection(0);

                mSelectedNumPeople = 0;
                for (int i = 0; i < mNumPeople.getChildCount(); ++i) {
                    if (i == 0) {
                        ((TextView) mNumPeople.getChildAt(i)).setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                        mNumPeople.getChildAt(i).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.search_housing_square_option_border_primary_color_background));
                    } else {
                        ((TextView) mNumPeople.getChildAt(i)).setTextColor(ContextCompat.getColor(getContext(), R.color.textColorSecondary));
                        mNumPeople.getChildAt(i).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.search_housing_square_option_border));
                    }
                }
                mSelectedNumRoom = 0;
                for (int i = 0; i < mNumRoom.getChildCount(); ++i) {
                    if (i == 0) {
                        ((TextView) mNumRoom.getChildAt(i)).setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                        mNumRoom.getChildAt(i).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.search_housing_square_option_border_primary_color_background));
                    } else {
                        ((TextView) mNumRoom.getChildAt(i)).setTextColor(ContextCompat.getColor(getContext(), R.color.textColorSecondary));
                        mNumRoom.getChildAt(i).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.search_housing_square_option_border));
                    }
                }
                mSelectedNumBed = 0;
                for (int i = 0; i < mNumBed.getChildCount(); ++i) {
                    if (i == 0) {
                        ((TextView) mNumBed.getChildAt(i)).setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                        mNumBed.getChildAt(i).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.search_housing_square_option_border_primary_color_background));
                    } else {
                        ((TextView) mNumBed.getChildAt(i)).setTextColor(ContextCompat.getColor(getContext(), R.color.textColorSecondary));
                        mNumBed.getChildAt(i).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.search_housing_square_option_border));
                    }
                }
                mSelectedNumBath = 0;
                for (int i = 0; i < mNumBath.getChildCount(); ++i) {
                    if (i == 0) {
                        ((TextView) mNumBath.getChildAt(i)).setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                        mNumBath.getChildAt(i).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.search_housing_square_option_border_primary_color_background));
                    } else {
                        ((TextView) mNumBath.getChildAt(i)).setTextColor(ContextCompat.getColor(getContext(), R.color.textColorSecondary));
                        mNumBath.getChildAt(i).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.search_housing_square_option_border));
                    }
                }

                for (int i = 0; i < mIsSelectedAmenityArray.length; ++i) {
                    mIsSelectedAmenityArray[i] = (i == 0) ? true : false;
                }
                mAmenitiesText.setText(mAmenityArray[0]);

                mMinTimeRestrictionSpinner.setSelection(0);

                mMaxStringTimeRestrictionList.clear();
                mMaxIntTimeRestrictionList.clear();
                for (int i = 0; i < mMinStringTimeRestrictionList.size(); ++i) {
                    mMaxStringTimeRestrictionList.add(mMinStringTimeRestrictionList.get(i));
                    mMaxIntTimeRestrictionList.add(mFullIntTimeRestrictionList.get(i));
                }
                mMaxTimeRestrictionSpinnerAdapter.notifyDataSetChanged();
                mMaxTimeRestrictionSpinner.setSelection(0);

                mSelectedNumRoommate = 1;
                for (int i = 0; i < mNumRoommate.getChildCount(); ++i) {
                    if (i == 0) {
                        ((TextView) mNumRoommate.getChildAt(i)).setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                        mNumRoommate.getChildAt(i).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.search_housing_square_option_border_primary_color_background));
                    } else {
                        ((TextView) mNumRoommate.getChildAt(i)).setTextColor(ContextCompat.getColor(getContext(), R.color.textColorSecondary));
                        mNumRoommate.getChildAt(i).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.search_housing_square_option_border));
                    }
                }
            }
        });

        mSearchButton = (TextView) view.findViewById(R.id.fragment_search_housing_input_data_search_bar_search);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardHelper.hideSoftKeyboard(getActivity(), false);
                if (mSelectedPlace != null) {
                    BigDecimal latitude = BigDecimal.valueOf(mSelectedPlace.getLatLng().latitude);
                    BigDecimal longitude = BigDecimal.valueOf(mSelectedPlace.getLatLng().longitude);

                    int minPricePerMonthOfOne = mFullIntPriceList.get(mMinPriceSpinner.getSelectedItemPosition());
                    int maxPricePerMonthOfOne = mMaxIntPriceList.get(mMaxPriceSpinner.getSelectedItemPosition());

                    int minArea = mFullIntAreaList.get(mMinAreaSpinner.getSelectedItemPosition());
                    int maxArea = mMaxIntAreaList.get(mMaxAreaSpinner.getSelectedItemPosition());

                    String minTimeRestriction = mMinStringTimeRestrictionList
                            .get(mMinTimeRestrictionSpinner.getSelectedItemPosition());
                    String maxTimeRestriction = mMaxStringTimeRestrictionList
                            .get(mMaxTimeRestrictionSpinner.getSelectedItemPosition());

                    SearchShareHousingData searchHousingData = new SearchShareHousingData(
                            latitude, longitude,
                            mRadiusInKm * 1000, mInputKeywords.getText().toString(),
                            mIsSelectedHouseTypeArray, minPricePerMonthOfOne, maxPricePerMonthOfOne,
                            minArea, maxArea,
                            mSelectedNumPeople == 0 ? -1 : mSelectedNumPeople,
                            mSelectedNumRoom == 0 ? -1 : mSelectedNumRoom,
                            mSelectedNumBed == 0 ? -1 : mSelectedNumBed,
                            mSelectedNumBath == 0 ? -1 : mSelectedNumBath,
                            mIsSelectedAmenityArray,
                            minTimeRestriction
                                    .equalsIgnoreCase(getString(R.string.fragment_search_housing_input_data_time_restriction_any))
                                    ? "" : minTimeRestriction,
                            maxTimeRestriction
                                    .equalsIgnoreCase(getString(R.string.fragment_search_housing_input_data_time_restriction_any))
                                    ? "" : maxTimeRestriction,
                            mSelectedNumRoommate, mRequiredGenderSpinner.getSelectedItem().toString(),
                            mRequiredWorkTypeSpinner.getSelectedItem().toString(), mIsSelectedOtherInfoArray
                    );
                    final ProgressDialog progressDialog = new ProgressDialog(getContext(),
                            R.style.LoginProgressDialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage(getString(R.string.fragment_search_housing_input_data_searching));
                    progressDialog.setCanceledOnTouchOutside(false);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    }
                    progressDialog.show();
                    ShareHousingClient.searchShareHousing(
                            searchHousingData, new ISearchShareHousingCallback() {
                                @Override
                                public void onSearchComplete(List<ShareHousing> shareHousingResults) {
                                    progressDialog.dismiss();
                                    if (shareHousingResults != null && shareHousingResults.size() > 0) {
                                        ShareSpaceApplication.BUS.post(new ReturnSearchShareHousingResultEvent(shareHousingResults));
                                    } else {
                                        ToastHelper.showCenterToast(
                                                getContext(),
                                                R.string.fragment_search_housing_input_data_not_found_matched_result,
                                                Toast.LENGTH_LONG
                                        );
                                    }
                                }

                                @Override
                                public void onSearchFailure(Throwable t) {
                                    RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext(), t);
                                }
                            }
                    );
                } else {
                    ToastHelper.showCenterToast(
                            getContext(),
                            R.string.fragment_search_housing_input_data_search_error_missing_location,
                            Toast.LENGTH_LONG
                    );
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPlaceAutocompleteFragment.onActivityResult(requestCode, resultCode, data);     // For Place Auto Complete Fragment' onPlaceSelected Callback to be called after User selected a Place
                                                                                        // and Place Auto Complete Edit Text to be filled in.
    }

}
