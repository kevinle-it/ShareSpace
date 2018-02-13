package com.lmtri.sharespace.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lmtri.sharespace.R;
import com.lmtri.sharespace.ShareSpaceApplication;
import com.lmtri.sharespace.activity.hometab.HousingDetailActivity;
import com.lmtri.sharespace.activity.sharetab.ShareHousingDetailActivity;
import com.lmtri.sharespace.adapter.ViewPagerAdapter;
import com.lmtri.sharespace.api.model.AppointmentNotificationData;
import com.lmtri.sharespace.api.model.Housing;
import com.lmtri.sharespace.api.model.HousingAppointment;
import com.lmtri.sharespace.api.model.ShareHousing;
import com.lmtri.sharespace.api.model.ShareHousingAppointment;
import com.lmtri.sharespace.api.model.User;
import com.lmtri.sharespace.api.service.RetrofitClient;
import com.lmtri.sharespace.api.service.user.IGetUserInfoCallback;
import com.lmtri.sharespace.api.service.user.UserClient;
import com.lmtri.sharespace.customview.CustomViewPager;
import com.lmtri.sharespace.fragment.RootFragment;
import com.lmtri.sharespace.fragment.appointment.AppointmentFragment;
import com.lmtri.sharespace.fragment.home.HousingFragment;
import com.lmtri.sharespace.fragment.interested.InterestedFragment;
import com.lmtri.sharespace.fragment.profile.ProfileFragment;
import com.lmtri.sharespace.fragment.share.ShareHousingFragment;
import com.lmtri.sharespace.helper.BottomNavigationViewHelper;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.busevent.SigninEvent;
import com.lmtri.sharespace.helper.busevent.SignoutEvent;
import com.lmtri.sharespace.helper.busevent.appointment.housing.SwitchToHousingAppointmentTabEvent;
import com.lmtri.sharespace.helper.busevent.appointment.sharehousing.OpenShareHousingAppointmentNotificationEvent;
import com.lmtri.sharespace.helper.busevent.appointment.sharehousing.SwitchToShareHousingAppointmentTabEvent;
import com.lmtri.sharespace.helper.busevent.housing.HistoryPostSaveNotePhotoActivityPostShareOfExistHousingEvent;
import com.lmtri.sharespace.helper.busevent.appointment.housing.OpenHousingAppointmentNotificationEvent;
import com.lmtri.sharespace.helper.busevent.housing.search.SearchHousingPostShareOfExistHousingEvent;
import com.lmtri.sharespace.listener.OnHousingAppointmentListInteractionListener;
import com.lmtri.sharespace.listener.OnHousingListInteractionListener;
import com.lmtri.sharespace.listener.OnShareHousingAppointmentListInteractionListener;
import com.lmtri.sharespace.listener.OnShareHousingListInteractionListener;
import com.squareup.otto.Subscribe;

public class MainActivity extends AppCompatActivity implements
        ProfileFragment.OnProfileFragmentInteractionListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    // Firebase Authentication.
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private BottomNavigationView mBottomNavigationView;
    private CustomViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private OnHousingListInteractionListener mHousingListInteractionListener
            = new OnHousingListInteractionListener() {
        @Override
        public void onHousingListInteraction(Housing item) {
            Intent intent = new Intent(MainActivity.this, HousingDetailActivity.class);

            Gson gson = new Gson();
            intent.putExtra(Constants.ACTIVITY_HOUSING_DETAIL_HOUSING_EXTRA, gson.toJson(item));

            startActivityForResult(intent, Constants.START_ACTIVITY_HOUSING_DETAIL);
        }
    };
//    private OnSavedHousingListInteractionListener mSavedHousingListInteractionListener
//            = new OnSavedHousingListInteractionListener() {
//        @Override
//        public void onSavedHousingListInteraction(SavedHousing item) {
//            Intent intent = new Intent(MainActivity.this, HousingDetailActivity.class);
//
//            Gson gson = new Gson();
//            intent.putExtra(Constants.ACTIVITY_HOUSING_DETAIL_HOUSING_EXTRA, gson.toJson(item.getHistoryHousingPhoto()));
//
//            startActivityForResult(intent, Constants.START_ACTIVITY_HOUSING_DETAIL);
//        }
//    };
//    private OnSavedShareHousingListInteractionListener mSavedShareHousingListInteractionListener
//            = new OnSavedShareHousingListInteractionListener() {
//        @Override
//        public void onSavedShareHousingListInteraction(SavedShareHousing item) {
//            Intent intent = new Intent(MainActivity.this, ShareHousingDetailActivity.class);
//
//            Gson gson = new Gson();
//            intent.putExtra(Constants.ACTIVITY_SHARE_HOUSING_DETAIL_SHARE_HOUSING_EXTRA, gson.toJson(item.getHistoryShareHousingNote()));
//
//            startActivity(intent);
//        }
//    };
    private OnShareHousingListInteractionListener mShareHousingListInteractionListener
            = new OnShareHousingListInteractionListener() {
        @Override
        public void onShareHousingListInteraction(ShareHousing item) {
            Intent intent = new Intent(MainActivity.this, ShareHousingDetailActivity.class);

            Gson gson = new Gson();
            intent.putExtra(Constants.ACTIVITY_SHARE_HOUSING_DETAIL_SHARE_HOUSING_EXTRA, gson.toJson(item));

            startActivity(intent);
        }
    };
    private OnHousingAppointmentListInteractionListener mHousingAppointmentListInteractionListener
            = new OnHousingAppointmentListInteractionListener() {
        @Override
        public void onHousingAppointmentListInteraction(HousingAppointment item) {
            Intent intent = new Intent(MainActivity.this, HousingDetailActivity.class);

            Gson gson = new Gson();
            intent.putExtra(Constants.ACTIVITY_HOUSING_DETAIL_HOUSING_EXTRA, gson.toJson(item.getHousing()));

            startActivityForResult(intent, Constants.START_ACTIVITY_HOUSING_DETAIL);
        }
    };
    private OnShareHousingAppointmentListInteractionListener mShareHousingAppointmentListInteractionListener
            = new OnShareHousingAppointmentListInteractionListener() {
        @Override
        public void onShareHousingAppointmentListInteraction(ShareHousingAppointment item) {
            Intent intent = new Intent(MainActivity.this, ShareHousingDetailActivity.class);

            Gson gson = new Gson();
            intent.putExtra(Constants.ACTIVITY_SHARE_HOUSING_DETAIL_SHARE_HOUSING_EXTRA, gson.toJson(item.getShareHousing()));

            startActivity(intent);
        }
    };

    private MenuItem mPrevMenuItem;

    private ViewPager.SimpleOnPageChangeListener mViewPagerOnPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            if (mPrevMenuItem != null) {
                mPrevMenuItem.setChecked(false);
            } else {
                mBottomNavigationView.getMenu().getItem(0).setChecked(false);
            }
            mBottomNavigationView.getMenu().getItem(position).setChecked(true);
            mPrevMenuItem = mBottomNavigationView.getMenu().getItem(position);
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case Constants.NAVIGATION_INDEX_HOME:
                    if (mViewPager.getCurrentItem() == Constants.VIEW_PAGER_INDEX_HOME) {
                        return false;
                    } else {
                        mViewPager.setCurrentItem(Constants.VIEW_PAGER_INDEX_HOME, false);
                    }
                    break;
                case Constants.NAVIGATION_INDEX_INTERESTED:
                    if (mViewPager.getCurrentItem() == Constants.VIEW_PAGER_INDEX_SAVED) {
                        return false;
                    } else {
                        mViewPager.setCurrentItem(Constants.VIEW_PAGER_INDEX_SAVED, false);
                    }
                    break;
                case Constants.NAVIGATION_INDEX_SHARE:
                    if (mViewPager.getCurrentItem() == Constants.VIEW_PAGER_INDEX_SHARE) {
                        return false;
                    } else {
                        mViewPager.setCurrentItem(Constants.VIEW_PAGER_INDEX_SHARE, false);
                    }
                    break;
                case Constants.NAVIGATION_INDEX_SCHEDULE:
                    if (mViewPager.getCurrentItem() == Constants.VIEW_PAGER_INDEX_SCHEDULE) {
                        return false;
                    } else {
                        mViewPager.setCurrentItem(Constants.VIEW_PAGER_INDEX_SCHEDULE, false);
                    }
                    break;
                case Constants.NAVIGATION_INDEX_PROFILE:
                    if (mViewPager.getCurrentItem() == Constants.VIEW_PAGER_INDEX_PROFILE) {
                        return false;
                    } else {
                        mViewPager.setCurrentItem(Constants.VIEW_PAGER_INDEX_PROFILE, false);
                    }
                    break;
            }
            return true;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ShareSpaceApplication.BUS.register(this);

        //Firebase Authentication.
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in.
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    try {
                        UserClient.getUserInfo(
                                FirebaseInstanceId.getInstance().getToken(),
                                new IGetUserInfoCallback() {
                                    @Override
                                    public void onGetUserInfoSuccess(User user) {
                                        if (user != null) {
                                            Constants.CURRENT_USER = user;
                                            Constants.CONTACT_NAME
                                                    = !TextUtils.isEmpty(user.getLastName())
                                                    ? user.getLastName() + " " + user.getFirstName()
                                                    : user.getFirstName();
                                            Constants.CONTACT_NUMBER = user.getPhoneNumber();
                                            Constants.CONTACT_EMAIL = user.getEmail();

                                            ShareSpaceApplication.BUS.post(new SigninEvent(user));
                                        } else {
                                            mAuth.signOut();
                                            ShareSpaceApplication.BUS.post(new SignoutEvent());
                                            RetrofitClient.showShareSpaceServerConnectionErrorDialog(MainActivity.this);
                                        }
                                    }

                                    @Override
                                    public void onGetUserInfoFailure(Throwable t) {
                                        mAuth.signOut();
                                        ShareSpaceApplication.BUS.post(new SignoutEvent());
                                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(MainActivity.this, t);
                                    }
                                }
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // getIdToken(boolean b) -> Whether forcing User's Token
                    // to be refreshed (even if it hasn't expired yet) or not.
                    user.getIdToken(false).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                        @Override
                        public void onSuccess(GetTokenResult getTokenResult) {
                            String idToken = getTokenResult.getToken();
                            Log.d(TAG, "onAuthStateChanged: ID Token: " + idToken);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    // User is signed out.
                    Log.d(TAG, "onAuthStateChanged:signed_out");

                    Constants.CURRENT_USER = null;
                }
            }
        };

        // Initialize View Pager.
        mViewPager = (CustomViewPager) findViewById(R.id.view_pager);
        mViewPager.setPagingEnabled(false);
        mViewPager.setOffscreenPageLimit(Constants.VIEW_PAGER_OFF_SCREEN_PAGE_LIMIT);

        // Initialize Bottom Navigation View
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(mBottomNavigationView);

        setupViewPager(mViewPager);

        mAuth.addAuthStateListener(mAuthListener);
//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivityForResult(intent, Constants.START_ACTIVITY_LOGIN_REQUEST);

        if (getIntent() != null) {
            String action = getIntent().getAction();
            if (Constants.ACTION_OPEN_APPOINTMENT_NOTIFICATION.equalsIgnoreCase(action)) {
                Gson gson = new Gson();
                try {
                    AppointmentNotificationData data = gson.fromJson(
                            getIntent().getStringExtra(Constants.APPOINTMENT_NOTIFICATION_DATA),
                            AppointmentNotificationData.class
                    );
                    if (data != null) {
                        if (data.getHousingOrShareHousingType().equalsIgnoreCase(Constants.HOUSING_APPOINTMENT_TYPE)) {
                            ShareSpaceApplication.BUS.post(new SwitchToHousingAppointmentTabEvent());
                        } else if (data.getHousingOrShareHousingType().equalsIgnoreCase(Constants.SHARE_HOUSING_APPOINTMENT_TYPE)) {
                            ShareSpaceApplication.BUS.post(new SwitchToShareHousingAppointmentTabEvent());
                        }
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                // If you want to cancel the notification: NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mViewPager.addOnPageChangeListener(mViewPagerOnPageChangeListener);
    }

    @Override
    protected void onPause() {
//        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
//        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        mViewPager.removeOnPageChangeListener(mViewPagerOnPageChangeListener);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = ((RootFragment) mViewPagerAdapter.getItem(mViewPager.getCurrentItem())).getCurrentFragment();
        switch (mViewPager.getCurrentItem()) {
            case Constants.VIEW_PAGER_INDEX_HOME:
                if (!((HousingFragment) currentFragment).onBackPressed()) {
                    super.onBackPressed();
                }
                break;
            case Constants.VIEW_PAGER_INDEX_SAVED:
                if (!((InterestedFragment) currentFragment).onBackPressed()) {
                    super.onBackPressed();
                }
                break;
            case Constants.VIEW_PAGER_INDEX_SHARE:
                if (!((ShareHousingFragment) currentFragment).onBackPressed()) {
                    super.onBackPressed();
                }
                break;
            case Constants.VIEW_PAGER_INDEX_SCHEDULE:
                if (!((AppointmentFragment) currentFragment).onBackPressed()) {
                    super.onBackPressed();
                }
                break;
            case Constants.VIEW_PAGER_INDEX_PROFILE:
                if (!((ProfileFragment) currentFragment).onBackPressed()) {
                    super.onBackPressed();
                }
                break;
            default:
                super.onBackPressed();
                break;
        }
    }

    private void setupViewPager(CustomViewPager viewPager) {
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        Fragment fragment1 = RootFragment.newInstance(Constants.VIEW_PAGER_INDEX_HOME);
        ((RootFragment) fragment1).setHousingListInteractionListener(mHousingListInteractionListener);
        mViewPagerAdapter.addFragment(fragment1);

        Fragment fragment2 = RootFragment.newInstance(Constants.VIEW_PAGER_INDEX_SAVED);
        ((RootFragment) fragment2).setHousingListInteractionListener(mHousingListInteractionListener);
        ((RootFragment) fragment2).setShareHousingListInteractionListener(mShareHousingListInteractionListener);
        mViewPagerAdapter.addFragment(fragment2);

        Fragment fragment3 = RootFragment.newInstance(Constants.VIEW_PAGER_INDEX_SHARE);
        ((RootFragment) fragment3).setShareHousingListInteractionListener(mShareHousingListInteractionListener);
        mViewPagerAdapter.addFragment(fragment3);

        Fragment fragment4 = RootFragment.newInstance(Constants.VIEW_PAGER_INDEX_SCHEDULE);
        ((RootFragment) fragment4).setHousingAppointmentListInteractionListener(mHousingAppointmentListInteractionListener);
        ((RootFragment) fragment4).setShareHousingAppointmentListInteractionListener(mShareHousingAppointmentListInteractionListener);
        mViewPagerAdapter.addFragment(fragment4);

        mViewPagerAdapter.addFragment(RootFragment.newInstance(Constants.VIEW_PAGER_INDEX_PROFILE));
        viewPager.setAdapter(mViewPagerAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);  // For Fragments to consume onActivityResult
                                                                // (of Activities start by Fragments) before MainActivity.
        if (requestCode == Constants.START_ACTIVITY_LOGIN_REQUEST) {
            if (resultCode == RESULT_OK) {
//                setupViewPager(mViewPager);
            }
        } else if (requestCode == Constants.START_ACTIVITY_HOUSING_DETAIL) {
            if (resultCode == RESULT_OK) {
                mViewPager.setCurrentItem(Constants.VIEW_PAGER_INDEX_SHARE, false);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            String action = intent.getAction();
            if (Constants.ACTION_OPEN_APPOINTMENT_NOTIFICATION.equalsIgnoreCase(action)) {
                Gson gson = new Gson();
                try {
                    AppointmentNotificationData data = gson.fromJson(
                            intent.getStringExtra(Constants.APPOINTMENT_NOTIFICATION_DATA),
                            AppointmentNotificationData.class
                    );
                    if (data != null) {
                        if (data.getHousingOrShareHousingType().equalsIgnoreCase(Constants.HOUSING_APPOINTMENT_TYPE)) {
                            ShareSpaceApplication.BUS.post(new SwitchToHousingAppointmentTabEvent());
                            ShareSpaceApplication.BUS.post(new OpenHousingAppointmentNotificationEvent(data));
                        } else if (data.getHousingOrShareHousingType().equalsIgnoreCase(Constants.SHARE_HOUSING_APPOINTMENT_TYPE)) {
                            ShareSpaceApplication.BUS.post(new SwitchToShareHousingAppointmentTabEvent());
                            ShareSpaceApplication.BUS.post(new OpenShareHousingAppointmentNotificationEvent(data));
                        }
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                // If you want to cancel the notification: NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);
            }
        }
    }

    @Subscribe
    public void searchHousingPostShareOfExistHousingEvent(SearchHousingPostShareOfExistHousingEvent event) {
        mViewPager.setCurrentItem(Constants.VIEW_PAGER_INDEX_SHARE, false);
    }

    @Subscribe
    public void historyPostSaveNotePhotoActivityPostShareOfExistHousingEvent(HistoryPostSaveNotePhotoActivityPostShareOfExistHousingEvent event) {
        mViewPager.setCurrentItem(Constants.VIEW_PAGER_INDEX_SHARE, false);
    }

    @Subscribe
    public void switchToHousingAppointmentTabEvent(SwitchToHousingAppointmentTabEvent event) {
        mViewPager.setCurrentItem(Constants.VIEW_PAGER_INDEX_SCHEDULE, false);
        mViewPager.post(new Runnable(){
            @Override
            public void run() {
                mViewPagerOnPageChangeListener.onPageSelected(mViewPager.getCurrentItem());
            }
        });
    }

    @Subscribe
    public void switchToShareHousingAppointmentTabEvent(SwitchToShareHousingAppointmentTabEvent event) {
        mViewPager.setCurrentItem(Constants.VIEW_PAGER_INDEX_SCHEDULE, false);
        mViewPager.post(new Runnable(){
            @Override
            public void run() {
                mViewPagerOnPageChangeListener.onPageSelected(mViewPager.getCurrentItem());
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//    }

    //    @Override
//    public void onHousingListInteraction(Housing item) {
//        Intent intent = new Intent(this, HousingDetailActivity.class);
//
//        Gson gson = new Gson();
//        intent.putExtra(Constants.ACTIVITY_HOUSING_DETAIL_HOUSING_EXTRA, gson.toJson(item));
//
//        startActivity(intent);
//    }

//    @Override
//    public void onShareHousingListInteraction(ShareHousing item) {
//        Intent intent = new Intent(this, ShareHousingDetailActivity.class);
//
//        Gson gson = new Gson();
//        intent.putExtra(Constants.ACTIVITY_SHARE_HOUSING_DETAIL_SHARE_HOUSING_EXTRA, gson.toJson(item));
//
//        startActivity(intent);
//    }

    @Override
    public void onProfileFragmentInteraction(Uri uri) {

    }
}
