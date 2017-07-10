package com.lmtri.sharespace.helper;

import com.lmtri.sharespace.R;

/**
 * Created by lmtri on 6/13/2017.
 */

public class Constants {
    // Constants related to login and signup.
    public static final int REQUIRED_PASSWORD_LENGTH = 8;
    public static final int SIGNUP_REQUEST = 1;
    public static final int LOGIN_REQUEST = 2;
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
}
