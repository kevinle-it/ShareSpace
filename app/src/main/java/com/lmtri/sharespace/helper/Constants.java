package com.lmtri.sharespace.helper;

import com.lmtri.sharespace.ApplicationContextSingleton;
import com.lmtri.sharespace.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by lmtri on 6/13/2017.
 */

public class Constants {
    
    // Constants related to login and signup.
    public static final int REQUIRED_PASSWORD_LENGTH = 8;
    public static final int START_ACTIVITY_SIGNUP_REQUEST = 1;
    public static final int START_ACTIVITY_LOGIN_REQUEST = 2;
    public static final int REQUIRED_SIGNUP_AGE = 18;

    // Constants related to View Pager and Bottom Navigation View.
    public static final int VIEW_PAGER_OFF_SCREEN_PAGE_LIMIT = 4;
    public static final int VIEW_PAGER_INDEX_HOME = 0;
    public static final int VIEW_PAGER_INDEX_SAVED = 1;
    public static final int VIEW_PAGER_INDEX_SHARE = 2;
    public static final int VIEW_PAGER_INDEX_INBOX = 3;
    public static final int VIEW_PAGER_INDEX_PROFILE = 4;
    public static final int NAVIGATION_INDEX_HOME = R.id.navigation_home;
    public static final int NAVIGATION_INDEX_SAVED = R.id.navigation_saved;
    public static final int NAVIGATION_INDEX_SHARE = R.id.navigation_share;
    public static final int NAVIGATION_INDEX_INBOX = R.id.navigation_inbox;
    public static final int NAVIGATION_INDEX_PROFILE = R.id.navigation_profile;
    public static final String ROOT_FRAGMENT_VIEW_PAGER_INDEX_PARAM = "ROOT_FRAGMENT_VIEW_PAGER_INDEX_PARAM";

    // Constants related to Housing Fragment.
    public static final String STORAGE_REFERENCE_URL = "gs://sharespace-a1fbe.appspot.com/";

    // Constants related to Housing Detail Activity.
    public static final String ACTIVITY_HOUSING_DETAIL_PROFILE_IMAGE_URL_EXTRA = "com.lmtri.sharespace.ACTIVITY_HOUSING_DETAIL_PROFILE_IMAGE_URL_EXTRA";
    public static final String ACTIVITY_HOUSING_DETAIL_HOUSE_TITLE_EXTRA = "com.lmtri.sharespace.ACTIVITY_HOUSING_DETAIL_HOUSE_TITLE_EXTRA";
    public static final String ACTIVITY_HOUSING_DETAIL_PRICE_EXTRA = "com.lmtri.sharespace.ACTIVITY_HOUSING_DETAIL_PRICE_EXTRA";
    public static final String ACTIVITY_HOUSING_DETAIL_DETAILS_EXTRA = "com.lmtri.sharespace.ACTIVITY_HOUSING_DETAIL_DETAILS_EXTRA";
    public static final String ACTIVITY_HOUSING_DETAIL_HOUSE_NUMBER_EXTRA = "com.lmtri.sharespace.ACTIVITY_HOUSING_DETAIL_HOUSE_NUMBER_EXTRA";
    public static final String ACTIVITY_HOUSING_DETAIL_STREET_EXTRA = "com.lmtri.sharespace.ACTIVITY_HOUSING_DETAIL_STREET_EXTRA";
    public static final String ACTIVITY_HOUSING_DETAIL_WARD_EXTRA = "com.lmtri.sharespace.ACTIVITY_HOUSING_DETAIL_WARD_EXTRA";
    public static final String ACTIVITY_HOUSING_DETAIL_DISTRICT_EXTRA = "com.lmtri.sharespace.ACTIVITY_HOUSING_DETAIL_DISTRICT_EXTRA";
    public static final String ACTIVITY_HOUSING_DETAIL_CITY_EXTRA = "com.lmtri.sharespace.ACTIVITY_HOUSING_DETAIL_CITY_EXTRA";
    public static final String ACTIVITY_HOUSING_DETAIL_HOUSE_TYPE_EXTRA = "com.lmtri.sharespace.ACTIVITY_HOUSING_DETAIL_HOUSE_TYPE_EXTRA";
    public static final String ACTIVITY_HOUSING_DETAIL_OWNER_NAME_EXTRA = "com.lmtri.sharespace.ACTIVITY_HOUSING_DETAIL_OWNER_NAME_EXTRA";

    // Constants related to Post House and Detailed Item Activity.
    public static final int START_ACTIVITY_POST_HOUSE_REQUEST = 3;
    public static final String START_ACTIVITY_POST_HOUSE_REQUEST_HOUSE_TYPE_RESULT = "com.lmtri.sharespace.START_ACTIVITY_POST_HOUSE_REQUEST_HOUSE_TYPE_RESULT";
    public static final String START_ACTIVITY_POST_HOUSE_REQUEST_AREA_RESULT = "com.lmtri.sharespace.START_ACTIVITY_POST_HOUSE_REQUEST_AREA_RESULT";
    public static final String START_ACTIVITY_POST_HOUSE_REQUEST_ADDRESS_HOUSE_NUMBER_RESULT = "com.lmtri.sharespace.START_ACTIVITY_POST_HOUSE_REQUEST_ADDRESS_HOUSE_NUMBER_RESULT";
    public static final String START_ACTIVITY_POST_HOUSE_REQUEST_ADDRESS_STREET_RESULT = "com.lmtri.sharespace.START_ACTIVITY_POST_HOUSE_REQUEST_ADDRESS_STREET_RESULT";
    public static final String START_ACTIVITY_POST_HOUSE_REQUEST_ADDRESS_WARD_RESULT = "com.lmtri.sharespace.START_ACTIVITY_POST_HOUSE_REQUEST_ADDRESS_WARD_RESULT";
    public static final String START_ACTIVITY_POST_HOUSE_REQUEST_ADDRESS_DISTRICT_RESULT = "com.lmtri.sharespace.START_ACTIVITY_POST_HOUSE_REQUEST_ADDRESS_DISTRICT_RESULT";
    public static final String START_ACTIVITY_POST_HOUSE_REQUEST_ADDRESS_CITY_RESULT = "com.lmtri.sharespace.START_ACTIVITY_POST_HOUSE_REQUEST_ADDRESS_CITY_RESULT";
    public static final String START_ACTIVITY_POST_HOUSE_REQUEST_HOUSE_DIRECTION_RESULT = "com.lmtri.sharespace.START_ACTIVITY_POST_HOUSE_REQUEST_HOUSE_DIRECTION_RESULT";
    public static final String START_ACTIVITY_POST_HOUSE_REQUEST_PRICE_RESULT = "com.lmtri.sharespace.START_ACTIVITY_POST_HOUSE_REQUEST_PRICE_RESULT";
    public static final String START_ACTIVITY_POST_HOUSE_REQUEST_CONTACT_NAME_RESULT = "com.lmtri.sharespace.START_ACTIVITY_POST_HOUSE_REQUEST_CONTACT_NAME_RESULT";
    public static final String START_ACTIVITY_POST_HOUSE_REQUEST_CONTACT_NUMBER_RESULT = "com.lmtri.sharespace.START_ACTIVITY_POST_HOUSE_REQUEST_CONTACT_NUMBER_RESULT";
    public static final String START_ACTIVITY_POST_HOUSE_REQUEST_CONTACT_EMAIL_RESULT = "com.lmtri.sharespace.START_ACTIVITY_POST_HOUSE_REQUEST_CONTACT_EMAIL_RESULT";
    public static final String START_ACTIVITY_POST_HOUSE_REQUEST_DETAILS_INFO_RESULT = "com.lmtri.sharespace.START_ACTIVITY_POST_HOUSE_REQUEST_DETAILS_INFO_RESULT";

    public static final int START_ACTIVITY_DETAILED_ITEM_REQUEST = 4;
    public static final String ACTIVITY_DETAILED_ITEM_TOOLBAR_TITLE_EXTRA = "com.lmtri.sharespace.ACTIVITY_DETAILED_ITEM_TOOLBAR_TITLE_EXTRA";
    public static final String ACTIVITY_DETAILED_ITEM_DETAILS_EXTRA = "com.lmtri.sharespace.ACTIVITY_DETAILED_ITEM_DETAILS_EXTRA";
    public static final String ACTIVITY_DETAILED_ITEM_RESULT_TYPE = "com.lmtri.sharespace.ACTIVITY_DETAILED_ITEM_RESULT_TYPE";
    public static final String START_ACTIVITY_DETAILED_ITEM_REQUEST_RESULT = "com.lmtri.sharespace.START_ACTIVITY_DETAILED_ITEM_REQUEST_RESULT";

    public static final String ACTIVITY_HOUSE_TYPE_TOOLBAR_TITLE =
            ApplicationContextSingleton
                    .getInstance()
                    .getApplicationContext()
                    .getResources()
                    .getString(R.string.activity_post_house_house_type);
    public static final ArrayList<String> HOUSE_TYPES = new ArrayList<>(
            Arrays.asList(
                    ApplicationContextSingleton
                            .getInstance()
                            .getApplicationContext()
                            .getResources()
                            .getStringArray(R.array.activity_post_house_detailed_item_house_type_array)
            )
    );

    public static final String ACTIVITY_ADDRESS_TOOLBAR_TITLE =
            ApplicationContextSingleton
                    .getInstance()
                    .getApplicationContext()
                    .getResources()
                    .getString(R.string.activity_post_house_address);
    public static final ArrayList<String> ADDRESS_FIELDS = new ArrayList<>(
            Arrays.asList(
                    ApplicationContextSingleton
                            .getInstance()
                            .getApplicationContext()
                            .getResources()
                            .getStringArray(R.array.activity_post_house_detailed_item_address_array)
            )
    );
    public static final String ACTIVITY_DETAILED_ITEM_ADDRESS_HOUSE_NUMBER_EXTRA = "com.lmtri.sharespace.ACTIVITY_DETAILED_ITEM_ADDRESS_HOUSE_NUMBER_EXTRA";
    public static final String ACTIVITY_DETAILED_ITEM_ADDRESS_STREET_EXTRA = "com.lmtri.sharespace.ACTIVITY_DETAILED_ITEM_ADDRESS_STREET_EXTRA";
    public static final String ACTIVITY_DETAILED_ITEM_ADDRESS_WARD_EXTRA = "com.lmtri.sharespace.ACTIVITY_DETAILED_ITEM_ADDRESS_WARD_EXTRA";
    public static final String ACTIVITY_DETAILED_ITEM_ADDRESS_DISTRICT_EXTRA = "com.lmtri.sharespace.ACTIVITY_DETAILED_ITEM_ADDRESS_DISTRICT_EXTRA";
    public static final String ACTIVITY_DETAILED_ITEM_ADDRESS_CITY_EXTRA = "com.lmtri.sharespace.ACTIVITY_DETAILED_ITEM_ADDRESS_CITY_EXTRA";
    public static final String START_ACTIVITY_DETAILED_ITEM_REQUEST_HOUSE_NUMBER_RESULT = "com.lmtri.sharespace.START_ACTIVITY_DETAILED_ITEM_REQUEST_HOUSE_NUMBER_RESULT";
    public static final String START_ACTIVITY_DETAILED_ITEM_REQUEST_STREET_RESULT = "com.lmtri.sharespace.START_ACTIVITY_DETAILED_ITEM_REQUEST_STREET_RESULT";
    public static final String START_ACTIVITY_DETAILED_ITEM_REQUEST_WARD_RESULT = "com.lmtri.sharespace.START_ACTIVITY_DETAILED_ITEM_REQUEST_WARD_RESULT";
    public static final String START_ACTIVITY_DETAILED_ITEM_REQUEST_DISTRICT_RESULT = "com.lmtri.sharespace.START_ACTIVITY_DETAILED_ITEM_REQUEST_DISTRICT_RESULT";
    public static final String START_ACTIVITY_DETAILED_ITEM_REQUEST_CITY_RESULT = "com.lmtri.sharespace.START_ACTIVITY_DETAILED_ITEM_REQUEST_CITY_RESULT";

    public static final String ACTIVITY_HOUSE_DIRECTION_TOOLBAR_TITLE =
            ApplicationContextSingleton
                    .getInstance()
                    .getApplicationContext()
                    .getResources()
                    .getString(R.string.activity_post_house_house_direction);
    public static final ArrayList<String> HOUSE_DIRECTIONS = new ArrayList<>(
            Arrays.asList(
                    ApplicationContextSingleton
                            .getInstance()
                            .getApplicationContext()
                            .getResources()
                            .getStringArray(R.array.activity_post_house_detailed_item_house_direction_array)
            )
    );

    public static final String ACTIVITY_PRICE_TOOLBAR_TITLE =
            ApplicationContextSingleton
                    .getInstance()
                    .getApplicationContext()
                    .getResources()
                    .getString(R.string.activity_post_house_price);
    public static final ArrayList<String> PRICE_UNITS = new ArrayList<>(
            Arrays.asList(
                    ApplicationContextSingleton
                            .getInstance()
                            .getApplicationContext()
                            .getResources()
                            .getStringArray(R.array.activity_post_house_detailed_item_price_unit_array)
            )
    );

    public static final String ACTIVITY_CONTACT_TOOLBAR_TITLE =
            ApplicationContextSingleton
                    .getInstance()
                    .getApplicationContext()
                    .getResources()
                    .getString(R.string.activity_post_house_contact);
    public static final ArrayList<String> CONTACT_FIELDS = new ArrayList<>(
            Arrays.asList(
                    ApplicationContextSingleton
                            .getInstance()
                            .getApplicationContext()
                            .getResources()
                            .getStringArray(R.array.activity_post_house_detailed_item_contact_array)
            )
    );
    public static final String ACTIVITY_DETAILED_ITEM_CONTACT_NAME_EXTRA = "com.lmtri.sharespace.ACTIVITY_DETAILED_ITEM_CONTACT_NAME_EXTRA";
    public static final String ACTIVITY_DETAILED_ITEM_CONTACT_NUMBER_EXTRA = "com.lmtri.sharespace.ACTIVITY_DETAILED_ITEM_CONTACT_NUMBER_EXTRA";
    public static final String ACTIVITY_DETAILED_ITEM_CONTACT_EMAIL_EXTRA = "com.lmtri.sharespace.ACTIVITY_DETAILED_ITEM_CONTACT_EMAIL_EXTRA";
    public static final String START_ACTIVITY_DETAILED_ITEM_REQUEST_CONTACT_NAME_RESULT = "com.lmtri.sharespace.START_ACTIVITY_DETAILED_ITEM_REQUEST_CONTACT_NAME_RESULT";
    public static final String START_ACTIVITY_DETAILED_ITEM_REQUEST_CONTACT_NUMBER_RESULT = "com.lmtri.sharespace.START_ACTIVITY_DETAILED_ITEM_REQUEST_CONTACT_NUMBER_RESULT";
    public static final String START_ACTIVITY_DETAILED_ITEM_REQUEST_CONTACT_EMAIL_RESULT = "com.lmtri.sharespace.START_ACTIVITY_DETAILED_ITEM_REQUEST_CONTACT_EMAIL_RESULT";
    public static final String CONTACT_NAME = "Minh-Tri Le";
    public static final String CONTACT_NUMBER = "01265636832";
    public static final String CONTACT_EMAIL = "lmtri1995@gmail.com";

    public static final String ACTIVITY_DETAILED_INFO_TOOLBAR_TITLE =
            ApplicationContextSingleton
                    .getInstance()
                    .getApplicationContext()
                    .getResources()
                    .getString(R.string.activity_post_house_details);
}
