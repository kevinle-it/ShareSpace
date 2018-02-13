package com.lmtri.sharespace.helper;

import com.lmtri.sharespace.ApplicationContextSingleton;
import com.lmtri.sharespace.R;
import com.lmtri.sharespace.api.model.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by lmtri on 6/13/2017.
 */

public class Constants {
    // Share Space Server's Base URL.
    public static final String SHARE_SPACE_SERVER_BASE_URL = "http://lmtri.somee.com/api/";

    // Share Space User's Firebase Authorization Token.
    public static String SHARE_SPACE_USER_FIREBASE_TOKEN = "";

    // Current User.
    public static User CURRENT_USER;
    public static final String CURRENT_USER_SHARED_PREFERENCES = "com.lmtri.sharespace.CURRENT_USER_SHARED_PREFERENCES_";

    // Share Space's Shared Preferences.
    public static final String SHARE_SPACE_SHARED_PREFERENCES = "com.lmtri.sharespace.SHARE_SPACE_SHARED_PREFERENCES";

    // Share Space's Appointment Notification.
    public static final String FCM_BASE_URL = "https://fcm.googleapis.com/fcm/";
    public static final String FCM_SERVER_KEY = "AAAATkvzdPo:APA91bGpCe8awqbPR5LRdDPnd8J3oCGwVoK9cniYHyVa72Z-CcckyBOqo702R9Z_zjjTbmym2lUKo_ZjpMVgng5vvFIVlOejpIxk_0_oOAQlzRdDKOafmIr0DtHPvFptuNnBLdp3rciZ";
    public static final String ACTION_OPEN_APPOINTMENT_NOTIFICATION = "com.lmtri.sharespace.ACTION_OPEN_APPOINTMENT_NOTIFICATION";
    public static final int SET_NEW_APPOINTMENT_NOTIFICATION = 1;
    public static final int UPDATE_APPOINTMENT_NOTIFICATION = 2;
    public static final int ACCEPT_APPOINTMENT_NOTIFICATION = 3;
    public static final int DELETE_APPOINTMENT_NOTIFICATION = 4;
    public static final String APPOINTMENT_NOTIFICATION_DATA = "com.lmtri.sharespace.APPOINTMENT_NOTIFICATION_DATA";
    public static final String HOUSING_APPOINTMENT_TYPE = "com.lmtri.sharespace.HOUSING_APPOINTMENT_TYPE";
    public static final String SHARE_HOUSING_APPOINTMENT_TYPE = "com.lmtri.sharespace.SHARE_HOUSING_APPOINTMENT_TYPE";

    // Android request unique ID.
    public static int REQUEST_ID = 0;

    // Constants related to Main Activity.
    public static final int START_ACTIVITY_HOUSING_DETAIL = ++REQUEST_ID;

    // Constants related to login and signup.
    public static final int REQUIRED_MIN_PASSWORD_LENGTH = 8;
    public static final int START_ACTIVITY_SIGNUP_REQUEST = ++REQUEST_ID;
    public static final int START_ACTIVITY_LOGIN_REQUEST = ++REQUEST_ID;
    public static final int REQUIRED_SIGNUP_AGE = 18;

    // Constants related to View Pager and Bottom Navigation View.
    public static final int VIEW_PAGER_OFF_SCREEN_PAGE_LIMIT = 4;
    public static final int VIEW_PAGER_INDEX_HOME = 0;
    public static final int VIEW_PAGER_INDEX_SAVED = 1;
    public static final int VIEW_PAGER_INDEX_SHARE = 2;
    public static final int VIEW_PAGER_INDEX_SCHEDULE = 3;
    public static final int VIEW_PAGER_INDEX_PROFILE = 4;
    public static final int NAVIGATION_INDEX_HOME = R.id.navigation_home;
    public static final int NAVIGATION_INDEX_INTERESTED = R.id.navigation_interested;
    public static final int NAVIGATION_INDEX_SHARE = R.id.navigation_share;
    public static final int NAVIGATION_INDEX_SCHEDULE = R.id.navigation_schedule;
    public static final int NAVIGATION_INDEX_PROFILE = R.id.navigation_profile;
    public static final String ROOT_FRAGMENT_VIEW_PAGER_INDEX_PARAM = "ROOT_FRAGMENT_VIEW_PAGER_INDEX_PARAM";

    // Constants related to Image Viewer Activity.
    public static final String ACTIVITY_IMAGE_VIEWER_PHOTO_URLS = "com.lmtri.sharespace.ACTIVITY_IMAGE_VIEWER_PHOTO_URLS";
    public static final String ACTIVITY_IMAGE_VIEWER_CURRENT_PHOTO_INDEX = "com.lmtri.sharespace.ACTIVITY_IMAGE_VIEWER_CURRENT_PHOTO_INDEX";

    // Constants related to Housing Fragment.
    public static final String STORAGE_REFERENCE_URL = "gs://sharespace-a1fbe.appspot.com/";
    public static final String IS_POST_NEW_HOUSING_EXTRA = "com.lmtri.sharespace.IS_POST_NEW_HOUSING_EXTRA";
    public static final int START_ACTIVITY_POST_HOUSE_REQUEST = ++REQUEST_ID;

    // Constants related to Search Housing Activity.
    public static final int SEARCH_HOUSING_VIEW_PAGER_OFF_SCREEN_PAGE_LIMIT = 2;
    public static final int SEARCH_HOUSING_VIEW_PAGER_INDEX_INPUT_SEARCHING_DATA = 0;
    public static final int SEARCH_HOUSING_VIEW_PAGER_INDEX_LIST_RESULT = 1;
    public static final int SEARCH_HOUSING_VIEW_PAGER_INDEX_MAP_RESULT = 2;
    public static final int SEARCH_HOUSING_MAX_RADIUS_IN_KM = 15;
    public static final int SEARCH_HOUSING_INITIAL_RADIUS_IN_KM = 5;
    public static final int START_ACTIVITY_SEARCH_HOUSING_REQUEST = ++REQUEST_ID;
    public static final int ACCESS_FINE_LOCATION_PERMISSION_REQUEST = ++REQUEST_ID;
    public static final BigDecimal SEARCH_HOUSING_MAP_VIEW_VN_SOUTH_WEST_MAP_VIEW_BOUND_LATITUDE = new BigDecimal("5.154261");
    public static final BigDecimal SEARCH_HOUSING_MAP_VIEW_VN_SOUTH_WEST_MAP_VIEW_BOUND_LONGITUDE = new BigDecimal("88.33233");
    public static final BigDecimal SEARCH_HOUSING_MAP_VIEW_VN_NORTH_EAST_MAP_VIEW_BOUND_LATITUDE = new BigDecimal("26.173052");
    public static final BigDecimal SEARCH_HOUSING_MAP_VIEW_VN_NORTH_EAST_MAP_VIEW_BOUND_LONGITUDE = new BigDecimal("123.48858");
    public static final BigDecimal SEARCH_HOUSING_MAP_VIEW_VN_SOUTH_WEST_BOUND_LATITUDE = new BigDecimal("8.1952");
    public static final BigDecimal SEARCH_HOUSING_MAP_VIEW_VN_SOUTH_WEST_BOUND_LONGITUDE = new BigDecimal("102.14441");
    public static final BigDecimal SEARCH_HOUSING_MAP_VIEW_VN_NORTH_EAST_BOUND_LATITUDE = new BigDecimal("23.393395");
    public static final BigDecimal SEARCH_HOUSING_MAP_VIEW_VN_NORTH_EAST_BOUND_LONGITUDE = new BigDecimal("109.6765");

    // Constants related to Housing Detail Activity.
    public static final String ACTIVITY_HOUSING_DETAIL_HOUSING_EXTRA = "com.lmtri.sharespace.ACTIVITY_HOUSING_DETAIL_HOUSING_EXTRA";

    public static final String ACTIVITY_HOUSING_DETAIL_FIND_ROOMMATE_CURRENT_HOUSING_INFO_EXTRA = "com.lmtri.sharespace.ACTIVITY_HOUSING_DETAIL_FIND_ROOMMATE_CURRENT_HOUSING_INFO_EXTRA";
    public static final String IS_FIND_ROOMMATE_EXTRA = "com.lmtri.sharespace.IS_FIND_ROOMMATE_EXTRA";
    public static final String HOUSING_INFO_FOR_FINDING_ROOMMATE_EXTRA = "com.lmtri.sharespace.HOUSING_INFO_FOR_FINDING_ROOMMATE_EXTRA";
    public static final int START_ACTIVITY_SHOW_EXIST_SHARE_POSTS_OF_CURRENT_HOUSING_REQUEST = ++REQUEST_ID;
    public static final int START_ACTIVITY_POST_SHARE_HOUSE_REQUEST = ++REQUEST_ID;

    public static final int ACTIVITY_HOUSING_DETAIL_NUM_PHOTOS_LIMIT = 20;
    public static final int ACTIVITY_HOUSING_DETAIL_SCHEDULE_NOTE_NUM_CHARACTERS_LIMIT = 50;
    public static final int ACTIVITY_HOUSING_DETAIL_NOTE_NUM_CHARACTERS_LIMIT = 500;

    public static final String IS_EDIT_HOUSING_EXTRA = "com.lmtri.sharespace.IS_EDIT_HOUSING_EXTRA";
    public static final String HOUSING_INFO_FOR_EDITING_POST_EXTRA = "com.lmtri.sharespace.HOUSING_INFO_FOR_EDITING_POST_EXTRA";

    // Constants related to Post House and Detailed Item Activity.

    public static final String LIST_OF_POSTED_HOUSINGS_SHARED_PREFERENCES = "com.lmtri.sharespace.LIST_OF_POSTED_HOUSINGS_SHARED_PREFERENCES_";
    public static final int ACTIVITY_POST_HOUSE_NUM_PHOTOS_LIMIT = 20;
    public static final int ACTIVITY_POST_HOUSE_PORTRAIT_ALBUM_PICKER_NUM_COLUMN_COUNT = 2;
    public static final int ACTIVITY_POST_HOUSE_LANDSCAPE_ALBUM_PICKER_NUM_COLUMN_COUNT = 2;
    public static final int ACTIVITY_POST_HOUSE_PHOTO_PICKER_NUM_COLUMN_COUNT = 4;

    public static final int CAMERA_AND_WRITE_EXTERNAL_STORAGE_PERMISSIONS_REQUEST = ++REQUEST_ID;
    public static final int CAMERA_PERMISSION_REQUEST = ++REQUEST_ID;
    public static final int WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST = ++REQUEST_ID;
    public static final int START_CAMERA_REQUEST = ++REQUEST_ID;
    public static final int START_ACTIVITY_DETAILED_ITEM_HOUSE_TITLE_REQUEST = ++REQUEST_ID;
    public static final int START_ACTIVITY_DETAILED_ITEM_HOUSE_TYPE_REQUEST = ++REQUEST_ID;
    public static final int START_ACTIVITY_DETAILED_ITEM_ADDRESS_REQUEST = ++REQUEST_ID;
    public static final int START_ACTIVITY_DETAILED_ITEM_HOUSE_DIRECTION_REQUEST = ++REQUEST_ID;
    public static final int START_ACTIVITY_DETAILED_ITEM_PRICE_REQUEST = ++REQUEST_ID;
    public static final int START_ACTIVITY_DETAILED_ITEM_CONTACT_REQUEST = ++REQUEST_ID;
    public static final int START_ACTIVITY_DETAILED_ITEM_DESCRIPTION_REQUEST = ++REQUEST_ID;

    public static final int ACTIVITY_DETAILED_ITEM_HOUSE_TITLE_NUM_CHARACTERS_LIMIT = 50;
    public static final int ACTIVITY_DETAILED_ITEM_DESCRIPTION_NUM_CHARACTERS_LIMIT = 1000;

    public static final String ACTIVITY_DETAILED_ITEM_TOOLBAR_TITLE_EXTRA = "com.lmtri.sharespace.ACTIVITY_DETAILED_ITEM_TOOLBAR_TITLE_EXTRA";
    public static final String ACTIVITY_DETAILED_ITEM_DETAILS_EXTRA = "com.lmtri.sharespace.ACTIVITY_DETAILED_ITEM_DETAILS_EXTRA";
    public static final String ACTIVITY_DETAILED_ITEM_RESULT = "com.lmtri.sharespace.ACTIVITY_DETAILED_ITEM_RESULT";

    public static final String ACTIVITY_HOUSE_TITLE_TOOLBAR_TITLE =
            ApplicationContextSingleton
                    .getInstance()
                    .getApplicationContext()
                    .getResources()
                    .getString(R.string.activity_post_house_house_title);

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
    public static final ArrayList<CharSequence> ADDRESS_FIELDS = new ArrayList<>(
            Arrays.asList(
                     ApplicationContextSingleton
                            .getInstance()
                            .getApplicationContext()
                            .getResources()
                            .getTextArray(R.array.activity_post_house_detailed_item_address_field_array)
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

    public static final int START_ACTIVITY_POST_HOUSE_ADDRESS_REQUEST = ++REQUEST_ID;
    public static final String ACTIVITY_POST_HOUSE_ADDRESS_HOUSE_NUMBER_EXTRA = "com.lmtri.sharespace.ACTIVITY_POST_HOUSE_ADDRESS_HOUSE_NUMBER_EXTRA";
    public static final String ACTIVITY_POST_HOUSE_ADDRESS_STREET_EXTRA = "com.lmtri.sharespace.ACTIVITY_POST_HOUSE_ADDRESS_STREET_EXTRA";
    public static final String ACTIVITY_POST_HOUSE_ADDRESS_WARD_EXTRA = "com.lmtri.sharespace.ACTIVITY_POST_HOUSE_ADDRESS_WARD_EXTRA";
    public static final String ACTIVITY_POST_HOUSE_ADDRESS_DISTRICT_EXTRA = "com.lmtri.sharespace.ACTIVITY_POST_HOUSE_ADDRESS_DISTRICT_EXTRA";
    public static final String ACTIVITY_POST_HOUSE_ADDRESS_CITY_EXTRA = "com.lmtri.sharespace.ACTIVITY_POST_HOUSE_ADDRESS_CITY_EXTRA";
    public static final String ACTIVITY_POST_HOUSE_ADDRESS_LATITUDE_EXTRA = "com.lmtri.sharespace.ACTIVITY_POST_HOUSE_ADDRESS_LATITUDE_EXTRA";
    public static final String ACTIVITY_POST_HOUSE_ADDRESS_LONGITUDE_EXTRA = "com.lmtri.sharespace.ACTIVITY_POST_HOUSE_ADDRESS_LONGITUDE_EXTRA";
    public static final String START_ACTIVITY_POST_HOUSE_ADDRESS_REQUEST_HOUSE_NUMBER_RESULT = "com.lmtri.sharespace.START_ACTIVITY_POST_HOUSE_ADDRESS_REQUEST_HOUSE_NUMBER_RESULT";
    public static final String START_ACTIVITY_POST_HOUSE_ADDRESS_REQUEST_STREET_RESULT = "com.lmtri.sharespace.START_ACTIVITY_POST_HOUSE_ADDRESS_REQUEST_STREET_RESULT";
    public static final String START_ACTIVITY_POST_HOUSE_ADDRESS_REQUEST_WARD_RESULT = "com.lmtri.sharespace.START_ACTIVITY_POST_HOUSE_ADDRESS_REQUEST_WARD_RESULT";
    public static final String START_ACTIVITY_POST_HOUSE_ADDRESS_REQUEST_DISTRICT_RESULT = "com.lmtri.sharespace.START_ACTIVITY_POST_HOUSE_ADDRESS_REQUEST_DISTRICT_RESULT";
    public static final String START_ACTIVITY_POST_HOUSE_ADDRESS_REQUEST_CITY_RESULT = "com.lmtri.sharespace.START_ACTIVITY_POST_HOUSE_ADDRESS_REQUEST_CITY_RESULT";
    public static final String START_ACTIVITY_POST_HOUSE_ADDRESS_REQUEST_LATITUDE_RESULT = "com.lmtri.sharespace.START_ACTIVITY_POST_HOUSE_ADDRESS_REQUEST_LATITUDE_RESULT";
    public static final String START_ACTIVITY_POST_HOUSE_ADDRESS_REQUEST_LONGITUDE_RESULT = "com.lmtri.sharespace.START_ACTIVITY_POST_HOUSE_ADDRESS_REQUEST_LONGITUDE_RESULT";


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
    public static final String ACTIVITY_DETAILED_ITEM_PRICE_EXTRA = "com.lmtri.sharespace.ACTIVITY_DETAILED_ITEM_PRICE_EXTRA";
    public static final String ACTIVITY_DETAILED_ITEM_PRICE_UNIT_EXTRA = "com.lmtri.sharespace.ACTIVITY_DETAILED_ITEM_PRICE_UNIT_EXTRA";
    public static final String START_ACTIVITY_DETAILED_ITEM_REQUEST_PRICE_RESULT = "com.lmtri.sharespace.START_ACTIVITY_DETAILED_ITEM_REQUEST_PRICE_RESULT";
    public static final String START_ACTIVITY_DETAILED_ITEM_REQUEST_PRICE_UNIT_RESULT = "com.lmtri.sharespace.START_ACTIVITY_DETAILED_ITEM_REQUEST_PRICE_UNIT_RESULT";

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
    public static String CONTACT_NAME = "Minh-Tri Le";
    public static String CONTACT_NUMBER = "01265636832";
    public static String CONTACT_EMAIL = "lmtri1995@gmail.com";

    public static final String ACTIVITY_DESCRIPTION_TOOLBAR_TITLE =
            ApplicationContextSingleton
                    .getInstance()
                    .getApplicationContext()
                    .getResources()
                    .getString(R.string.activity_post_house_description);

    // Constants related to Interested Share Housing Fragment.
    public static final String SELECTED_TAB_INDEX_INTERESTED_SHARE_HOUSING = "com.lmtri.sharespace.SELECTED_TAB_INDEX_INTERESTED_SHARE_HOUSING";
    public static final int START_ACTIVITY_HISTORY_POST_SAVE_NOTE_PHOTO_REQUEST = ++REQUEST_ID;

    // Constants related to Share Housing Fragment.
    public static final String IS_POST_NEW_SHARE_HOUSING_EXTRA = "com.lmtri.sharespace.IS_POST_NEW_SHARE_HOUSING_EXTRA";
    public static final String SELECTED_TAB_INDEX_POSTED_SHARE_HOUSING = "com.lmtri.sharespace.SELECTED_TAB_INDEX_POSTED_SHARE_HOUSING";

    // Constants related to Share Housing Detail Activity.
    public static final String ACTIVITY_SHARE_HOUSING_DETAIL_SHARE_HOUSING_EXTRA = "com.lmtri.sharespace.ACTIVITY_SHARE_HOUSING_DETAIL_SHARE_HOUSING_EXTRA";
    public static final String IS_EDIT_SHARE_HOUSING_EXTRA = "com.lmtri.sharespace.IS_EDIT_SHARE_HOUSING_EXTRA";
    public static final String SHARE_HOUSING_INFO_FOR_EDITING_POST_EXTRA = "com.lmtri.sharespace.SHARE_HOUSING_INFO_FOR_EDITING_POST_EXTRA";

    public static final String ACTIVITY_PRICE_PER_MONTH_OF_ONE_TOOLBAR_TITLE =
            ApplicationContextSingleton
                    .getInstance()
                    .getApplicationContext()
                    .getResources()
                    .getString(R.string.activity_post_share_house_price_toolbar_title);
    public static final ArrayList<String> SHARE_PRICE_UNITS = new ArrayList<>(
            Arrays.asList(
                    ApplicationContextSingleton
                            .getInstance()
                            .getApplicationContext()
                            .getResources()
                            .getStringArray(R.array.activity_post_share_house_detailed_item_price_unit_array)
            )
    );
    public static final int START_ACTIVITY_DETAILED_ITEM_PRICE_PER_MONTH_OF_ONE_REQUEST = ++REQUEST_ID;
    public static final int START_ACTIVITY_DETAILED_ITEM_SHARE_DESCRIPTION_REQUEST = ++REQUEST_ID;
}
