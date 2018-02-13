package com.lmtri.sharespace.fragment.appointment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.lmtri.sharespace.R;
import com.lmtri.sharespace.ShareSpaceApplication;
import com.lmtri.sharespace.adapter.appointmenttab.HousingAppointmentRecyclerViewAdapter;
import com.lmtri.sharespace.api.model.AppointmentNotification;
import com.lmtri.sharespace.api.model.AppointmentNotificationData;
import com.lmtri.sharespace.api.model.FCMResponse;
import com.lmtri.sharespace.api.model.HousingAppointment;
import com.lmtri.sharespace.api.service.RetrofitClient;
import com.lmtri.sharespace.api.service.user.UserClient;
import com.lmtri.sharespace.api.service.user.appointment.housing.IGetMoreNewerHousingAppointmentsCallback;
import com.lmtri.sharespace.api.service.user.appointment.housing.IGetMoreOlderHousingAppointmentsCallback;
import com.lmtri.sharespace.api.service.user.appointment.housing.IGetNumOfHousingAppointmentsCallback;
import com.lmtri.sharespace.api.service.user.appointment.housing.IHousingAppointmentGettingCallback;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.ToastHelper;
import com.lmtri.sharespace.helper.busevent.SigninEvent;
import com.lmtri.sharespace.helper.busevent.SignoutEvent;
import com.lmtri.sharespace.helper.busevent.appointment.housing.DeleteHousingAppointmentEvent;
import com.lmtri.sharespace.helper.busevent.appointment.housing.OpenHousingAppointmentNotificationEvent;
import com.lmtri.sharespace.helper.busevent.appointment.housing.SetNewHousingAppointmentEvent;
import com.lmtri.sharespace.helper.busevent.appointment.housing.UpdateHousingAppointmentEvent;
import com.lmtri.sharespace.helper.busevent.housing.save.SaveHousingEvent;
import com.lmtri.sharespace.helper.busevent.housing.save.UnsaveHousingEvent;
import com.lmtri.sharespace.helper.firebasecloudmessaging.FCMClient;
import com.lmtri.sharespace.helper.firebasecloudmessaging.IFCMSendNotificationCallback;
import com.lmtri.sharespace.listener.EndlessRecyclerViewScrollListener;
import com.lmtri.sharespace.listener.OnHousingAppointmentListInteractionListener;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HousingAppointmentTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HousingAppointmentTabFragment extends Fragment {

    public static final String TAG = HousingAppointmentTabFragment.class.getSimpleName();

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private LinearLayout mConnectionErrorLayout;
    private TextView mConnectionErrorMessage;
    private ViewSwitcher mViewSwitcherRetryConnect;
    private FrameLayout mRetryConnectButton;

//    private SwipeRefreshLayout mSwipeRefreshContainer;
    private RecyclerView mRecyclerView;
    private OnHousingAppointmentListInteractionListener mListener;
    private EndlessRecyclerViewScrollListener mRecyclerViewScrollListener;

    private int mNumOfHousingAppointments = 0;
    private List<HousingAppointment> mHousingAppointments = new ArrayList<>();
    private HousingAppointmentRecyclerViewAdapter mHousingAppointmentRecyclerViewAdapter;

    public HousingAppointmentTabFragment() {
        // Required empty public constructor
    }


    public static HousingAppointmentTabFragment newInstance(int columnCount) {
        HousingAppointmentTabFragment fragment = new HousingAppointmentTabFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        ShareSpaceApplication.BUS.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_housing_appointment_tab, container, false);

        mConnectionErrorLayout = (LinearLayout) view.findViewById(R.id.fragment_housing_appointment_tab_connection_error_layout);
        mConnectionErrorMessage = (TextView) view.findViewById(R.id.fragment_housing_appointment_tab_connection_error_message);
        mViewSwitcherRetryConnect = (ViewSwitcher) view.findViewById(R.id.fragment_housing_appointment_tab_view_switcher_retry_connect_to_server);
        mRetryConnectButton = (FrameLayout) view.findViewById(R.id.fragment_housing_appointment_tab_retry_button);
        mRetryConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewSwitcherRetryConnect.showNext();
                loadMoreOlderHousingAppointmentsFromApi(0);
            }
        });
        if (Constants.CURRENT_USER == null) {
            mConnectionErrorLayout.setVisibility(View.GONE);
        }

//        setHasOptionsMenu(true);
//
//        mMaterialSearchView = (MaterialSearchView) view.findViewById(R.id.fragment_housing_list_search_view);
//        mMaterialSearchView.setSuggestions(new String[] {
//                "Android",
//                "iOS",
//                "Ajax",
//                "SCALA",
//                "Ruby",
//                "Arduino",
//                "JavaScript",
//                "Application"
//        });   // Test Search View.

//        mSwipeRefreshContainer = (SwipeRefreshLayout) view.findViewById(R.id.fragment_housing_appointment_tab_swipe_refresh_container);
//        mSwipeRefreshContainer.setColorSchemeResources(
//                R.color.colorPrimary,
//                R.color.colorPrimaryLight,
//                R.color.colorSecondary
//        );
//        mSwipeRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                if (mHousingAppointments != null && mHousingAppointments.size() > 0) {
//                    if (Constants.CURRENT_USER != null) {
//                        String s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(mHousingAppointments.get(0).getDateTimeCreated());
//                        s = s.substring(0, s.length() - 2) + ":" + s.substring(s.length() - 2, s.length());
//                        loadMoreNewerHousingAppointmentsFromApi(s);
//                    }
//                } else {
//                    if (Constants.CURRENT_USER != null) {
//                        loadMoreOlderHousingAppointmentsFromApi(0);
//                    }
//                }
//            }
//        });
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_housing_appointment_tab_home_list);
        // Set the adapter
        if (mColumnCount <= 1) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                    linearLayoutManager.getOrientation());

            mRecyclerView.setLayoutManager(linearLayoutManager);
//            mRecyclerView.addItemDecoration(dividerItemDecoration);
            // Retain an instance so that you can call `resetState()` for fresh searches
            mRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager, false) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    // Triggered only when new data needs to be appended to the list
                    // Add whatever code is needed to append new items to the bottom of the list
                    if (Constants.CURRENT_USER != null) {
                        loadMoreOlderHousingAppointmentsFromApi(page);
                    }
                }
            };
        } else {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), mColumnCount);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                    gridLayoutManager.getOrientation());

            mRecyclerView.setLayoutManager(gridLayoutManager);
//            mRecyclerView.addItemDecoration(dividerItemDecoration);
            // Retain an instance so that you can call `resetState()` for fresh searches
            mRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager, false) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    // Triggered only when new data needs to be appended to the list
                    // Add whatever code is needed to append new items to the bottom of the list
                    if (Constants.CURRENT_USER != null) {
                        loadMoreOlderHousingAppointmentsFromApi(page);
                    }
                }
            };
        }

        mHousingAppointmentRecyclerViewAdapter = new HousingAppointmentRecyclerViewAdapter(getContext(), mHousingAppointments, mListener);
        mRecyclerView.setAdapter(mHousingAppointmentRecyclerViewAdapter);
        mRecyclerView.addOnScrollListener(mRecyclerViewScrollListener);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Subscribe
    public void userSignin(SigninEvent event) {
        if (mNumOfHousingAppointments == 0) {
            mConnectionErrorLayout.setVisibility(View.VISIBLE);
            UserClient.getNumOfHousingAppointments(new IGetNumOfHousingAppointmentsCallback() {
                @Override
                public void onGetComplete(Integer numOfHousingAppointments) {
                    if (numOfHousingAppointments != null && numOfHousingAppointments > 0) {
                        mNumOfHousingAppointments = numOfHousingAppointments;
                        loadMoreOlderHousingAppointmentsFromApi(0);
                    } else {
                        if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                            mViewSwitcherRetryConnect.showNext();
                        }
                    }
                }

                @Override
                public void onGetFailure(Throwable t) {

                }
            });
        }
    }

    @Subscribe
    public void userSignout(SignoutEvent event) {
        mConnectionErrorLayout.setVisibility(View.GONE);
        mNumOfHousingAppointments = 0;
        mHousingAppointments.clear();
        mHousingAppointmentRecyclerViewAdapter.notifyDataSetChanged();
        mHousingAppointments = new ArrayList<>();
        mHousingAppointmentRecyclerViewAdapter = new HousingAppointmentRecyclerViewAdapter(
                getContext(), mHousingAppointments, mListener
        );
        mRecyclerView.setAdapter(mHousingAppointmentRecyclerViewAdapter);
    }

    @Subscribe
    public void saveHousing(SaveHousingEvent event) {
        if (event.getSavedHousing() != null) {
            for (int i = 0; i < mHousingAppointments.size(); ++i) {
                if (event.getSavedHousing().getSavedHousing().getID()
                        == mHousingAppointments.get(i).getHousing().getID()) {
                    mHousingAppointments.get(i).getHousing().setNumOfSaved(
                            mHousingAppointments.get(i).getHousing().getNumOfSaved() + 1
                    );
                    break;
                }
            }
        }
    }

    @Subscribe
    public void unsaveHousing(UnsaveHousingEvent event) {
        if (event.getSavedHousing() != null) {
            for (int i = 0; i < mHousingAppointments.size(); ++i) {
                if (event.getSavedHousing().getSavedHousing().getID()
                        == mHousingAppointments.get(i).getHousing().getID()) {
                    mHousingAppointments.get(i).getHousing().setNumOfSaved(
                            mHousingAppointments.get(i).getHousing().getNumOfSaved() + 1
                    );
                    break;
                }
            }
        }
    }

    @Subscribe
    public void setNewHousingAppointment(SetNewHousingAppointmentEvent event) {
        if (event.getHousingAppointment() != null
                && event.getHousingAppointment().getHousing()
                .getOwner().getDeviceTokens() != null) {
            FCMClient.sendNotification(
                    new AppointmentNotification(
                            new AppointmentNotificationData(
                                    Constants.HOUSING_APPOINTMENT_TYPE,
                                    Constants.SET_NEW_APPOINTMENT_NOTIFICATION,
                                    event.getHousingAppointment().getHousing().getID(),
                                    event.getHousingAppointment().getSender().getUserID(),
                                    event.getHousingAppointment().getHousing().getOwner().getUserID()
                            ),
                            event.getHousingAppointment().getHousing()
                                    .getOwner().getDeviceTokens().split(";")
                    ),
                    new IFCMSendNotificationCallback() {
                        @Override
                        public void onSendComplete(FCMResponse fcmResponse) {
                            if (fcmResponse != null) {
                                if (fcmResponse.getNumSuccess() == 0) {
                                    ToastHelper.showCenterToast(
                                            getContext(),
                                            "Không gửi được thông báo đến người nhận.",
                                            Toast.LENGTH_SHORT
                                    );
                                }
                            }
                        }

                        @Override
                        public void onSendFailure(Throwable t) {

                        }
                    }
            );
            if (mConnectionErrorLayout.getVisibility() == View.VISIBLE) {
                mConnectionErrorLayout.setVisibility(View.GONE);
            }
            mHousingAppointments.add(0, event.getHousingAppointment());
            mHousingAppointmentRecyclerViewAdapter.notifyItemInserted(0);
        }
    }

    @Subscribe
    public void updateHousingAppointment(UpdateHousingAppointmentEvent event) {
        if (event.getHousingAppointment() != null
                && event.getHousingAppointment().getSender().getDeviceTokens() != null
                && event.getHousingAppointment().getHousing().getOwner().getDeviceTokens() != null) {
            FCMClient.sendNotification(
                    new AppointmentNotification(
                            new AppointmentNotificationData(
                                    Constants.HOUSING_APPOINTMENT_TYPE,
                                    Constants.UPDATE_APPOINTMENT_NOTIFICATION,
                                    event.getHousingAppointment().getHousing().getID(),
                                    event.getHousingAppointment().getSender().getUserID(),
                                    event.getHousingAppointment().getHousing().getOwner().getUserID()
                            ),
                            Constants.CURRENT_USER.getUserID()
                                    == event.getHousingAppointment().getHousing().getOwner().getUserID()
                                    ? event.getHousingAppointment().getSender().getDeviceTokens().split(";")
                                    : event.getHousingAppointment().getHousing().getOwner().getDeviceTokens().split(";")
                    ),
                    new IFCMSendNotificationCallback() {
                        @Override
                        public void onSendComplete(FCMResponse fcmResponse) {
                            if (fcmResponse != null) {
                                if (fcmResponse.getNumSuccess() == 0) {
                                    ToastHelper.showCenterToast(
                                            getContext(),
                                            "Không gửi được thông báo đến người nhận.",
                                            Toast.LENGTH_SHORT
                                    );
                                }
                            }
                        }

                        @Override
                        public void onSendFailure(Throwable t) {

                        }
                    }
            );
            for (int i = 0; i < mHousingAppointments.size(); ++i) {
                if (event.getHousingAppointment().getHousing().getID()
                        == mHousingAppointments.get(i).getHousing().getID()) {
                    mHousingAppointments.remove(i);
                    mHousingAppointments.add(0, event.getHousingAppointment());
                    mHousingAppointmentRecyclerViewAdapter.notifyItemRemoved(i);
                    mHousingAppointmentRecyclerViewAdapter.notifyItemInserted(0);
                    break;
                }
            }
        }
    }

    @Subscribe
    public void deleteHousingAppointment(DeleteHousingAppointmentEvent event) {
        if (event.getHousingAppointment() != null
                && event.getHousingAppointment().getSender().getDeviceTokens() != null
                && event.getHousingAppointment().getHousing().getOwner().getDeviceTokens() != null) {
            FCMClient.sendNotification(
                    new AppointmentNotification(
                            new AppointmentNotificationData(
                                    Constants.HOUSING_APPOINTMENT_TYPE,
                                    Constants.DELETE_APPOINTMENT_NOTIFICATION,
                                    event.getHousingAppointment().getHousing().getID(),
                                    event.getHousingAppointment().getSender().getUserID(),
                                    event.getHousingAppointment().getHousing().getOwner().getUserID()
                            ),
                            Constants.CURRENT_USER.getUserID()
                                    == event.getHousingAppointment().getHousing().getOwner().getUserID()
                                    ? event.getHousingAppointment().getSender().getDeviceTokens().split(";")
                                    : event.getHousingAppointment().getHousing().getOwner().getDeviceTokens().split(";")
                    ),
                    new IFCMSendNotificationCallback() {
                        @Override
                        public void onSendComplete(FCMResponse fcmResponse) {
                            if (fcmResponse != null) {
                                if (fcmResponse.getNumSuccess() == 0) {
                                    ToastHelper.showCenterToast(
                                            getContext(),
                                            "Không gửi được thông báo đến người nhận.",
                                            Toast.LENGTH_SHORT
                                    );
                                }
                            }
                        }

                        @Override
                        public void onSendFailure(Throwable t) {

                        }
                    }
            );
            for (int i = 0; i < mHousingAppointments.size(); ++i) {
                if (event.getHousingAppointment().getHousing().getID()
                        == mHousingAppointments.get(i).getHousing().getID()) {
                    mHousingAppointments.remove(i);
                    mHousingAppointmentRecyclerViewAdapter.notifyItemRemoved(i);
                    break;
                }
            }
            if (mHousingAppointments.isEmpty()) {
                mConnectionErrorLayout.setVisibility(View.VISIBLE);
                if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                    mViewSwitcherRetryConnect.showNext();
                }
            }
        }
    }

    @Subscribe
    public void openHousingAppointmentNotificationEvent(OpenHousingAppointmentNotificationEvent event) {
        if (event.getData() != null) {
            final AppointmentNotificationData data = event.getData();
            UserClient.getSpecificHousingAppointment(
                    event.getData().getHousingOrShareHousingID(),
                    event.getData().getSenderID(),
                    event.getData().getOwnerOrCreatorID(),
                    new IHousingAppointmentGettingCallback() {
                        @Override
                        public void onGetComplete(HousingAppointment housingAppointment) {
                            if (housingAppointment != null) {
                                if (data.getAppointmentType() == Constants.SET_NEW_APPOINTMENT_NOTIFICATION) {
                                    if (mConnectionErrorLayout.getVisibility() == View.VISIBLE) {
                                        mConnectionErrorLayout.setVisibility(View.GONE);
                                    }
                                    mHousingAppointments.add(0, housingAppointment);
                                    mHousingAppointmentRecyclerViewAdapter.notifyItemInserted(0);
                                } else if (data.getAppointmentType() == Constants.UPDATE_APPOINTMENT_NOTIFICATION) {
                                    for (int i = 0; i < mHousingAppointments.size(); ++i) {
                                        if (housingAppointment.getHousing().getID()
                                                == mHousingAppointments.get(i).getHousing().getID()) {
                                            mHousingAppointments.remove(i);
                                            mHousingAppointments.add(0, housingAppointment);
                                            mHousingAppointmentRecyclerViewAdapter.notifyItemRemoved(i);
                                            mHousingAppointmentRecyclerViewAdapter.notifyItemInserted(0);
                                            break;
                                        }
                                    }
                                } else if (data.getAppointmentType() == Constants.ACCEPT_APPOINTMENT_NOTIFICATION) {
                                    for (int i = 0; i < mHousingAppointments.size(); ++i) {
                                        if (housingAppointment.getHousing().getID()
                                                == mHousingAppointments.get(i).getHousing().getID()) {
                                            mHousingAppointments.remove(i);
                                            mHousingAppointments.add(0, housingAppointment);
                                            mHousingAppointmentRecyclerViewAdapter.notifyItemRemoved(i);
                                            mHousingAppointmentRecyclerViewAdapter.notifyItemInserted(0);
                                            break;
                                        }
                                    }
                                }
                            }
                            if (data.getAppointmentType() == Constants.DELETE_APPOINTMENT_NOTIFICATION) {
                                for (int i = 0; i < mHousingAppointments.size(); ++i) {
                                    if (data.getHousingOrShareHousingID()
                                            == mHousingAppointments.get(i).getHousing().getID()) {
                                        mHousingAppointments.remove(i);
                                        mHousingAppointmentRecyclerViewAdapter.notifyItemRemoved(i);
                                        break;
                                    }
                                }
                                if (mHousingAppointments.isEmpty()) {
                                    mConnectionErrorLayout.setVisibility(View.VISIBLE);
                                    if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                                        mViewSwitcherRetryConnect.showNext();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onGetFailure(Throwable t) {

                        }
                    }
            );
        }
    }


//    private void loadMoreNewerHousingAppointmentsFromApi(String currentTopHousingAppointmentDateTimeCreated) {
//        UserClient.getMoreNewerHousingAppointments(
//                currentTopHousingAppointmentDateTimeCreated,
//                new IGetMoreNewerHousingAppointmentsCallback() {
//                    @Override
//                    public void onGetComplete(List<HousingAppointment> newHousingAppointments) {
//                        mSwipeRefreshContainer.setRefreshing(false);
//                        if (mHousingAppointments.size() == 0) {
//                            if (newHousingAppointments != null && newHousingAppointments.size() > 0) {
//                                mHousingAppointments.addAll(0, newHousingAppointments);
//
//                                mHousingAppointmentRecyclerViewAdapter.notifyItemRangeInserted(0, newHousingAppointments.size());
//                            } else {
////                                RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext());
//                            }
//                        } else if (mHousingAppointments.size() > 0) {
//                            if (newHousingAppointments != null && newHousingAppointments.size() > 0) {
//                                mHousingAppointments.addAll(0, newHousingAppointments);
//
//                                mHousingAppointmentRecyclerViewAdapter.notifyItemRangeInserted(0, newHousingAppointments.size());
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onGetFailure(Throwable t) {
//                        mSwipeRefreshContainer.setRefreshing(false);
//                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext(), t);
//                    }
//                }
//        );
//    }


    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadMoreOlderHousingAppointmentsFromApi(final int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
        String currentBottomHousingAppointmentDateTimeCreated
                = !mHousingAppointments.isEmpty()
                ? new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(mHousingAppointments.get(mHousingAppointments.size() - 1).getDateTimeCreated())
                : null;
        if (currentBottomHousingAppointmentDateTimeCreated != null) {
            currentBottomHousingAppointmentDateTimeCreated
                    = currentBottomHousingAppointmentDateTimeCreated.substring(0, currentBottomHousingAppointmentDateTimeCreated.length() - 2)
                    + ":" + currentBottomHousingAppointmentDateTimeCreated.substring(currentBottomHousingAppointmentDateTimeCreated.length() - 2, currentBottomHousingAppointmentDateTimeCreated.length());
        }
        UserClient.getMoreOlderHousingAppointments(currentBottomHousingAppointmentDateTimeCreated,
                new IGetMoreOlderHousingAppointmentsCallback() {
            @Override
            public void onGetComplete(List<HousingAppointment> olderHousingAppointments) {
//                mSwipeRefreshContainer.setRefreshing(false);
                if (mHousingAppointments.size() == 0) {
                    if (olderHousingAppointments != null && olderHousingAppointments.size() > 0) {
                        if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                            mViewSwitcherRetryConnect.showNext();
                        }
                        mConnectionErrorLayout.setVisibility(View.GONE);

                        int positionStart = mHousingAppointments.size();

                        mHousingAppointments.addAll(olderHousingAppointments);

                        mHousingAppointmentRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mHousingAppointments.size() - positionStart);

                        if (mHousingAppointments.size() < mNumOfHousingAppointments) {
                            loadMoreOlderHousingAppointmentsFromApi(1);
                        }
                    } else {
//                        mConnectionErrorMessage.setVisibility(View.VISIBLE);
                        if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                            mViewSwitcherRetryConnect.showNext();
                        }
//                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext());
                    }
                } else if (mHousingAppointments.size() > 0) {
                    if (olderHousingAppointments != null && olderHousingAppointments.size() > 0) {
                        int positionStart = mHousingAppointments.size();

                        mHousingAppointments.addAll(olderHousingAppointments);

                        mHousingAppointmentRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mHousingAppointments.size() - positionStart);

                        if (mHousingAppointments.size() < mNumOfHousingAppointments) {
                            loadMoreOlderHousingAppointmentsFromApi(1);
                        }
                    }
//                    else {
//                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext());
//                    }
                }
            }

            @Override
            public void onGetFailure(Throwable t) {
//                mSwipeRefreshContainer.setRefreshing(false);

//                mConnectionErrorMessage.setVisibility(View.VISIBLE);
                if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                    mViewSwitcherRetryConnect.showNext();
                }
                RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext(), t);
            }
        });
    }

    public void setListener(OnHousingAppointmentListInteractionListener listener) {
        mListener = listener;
    }
}
