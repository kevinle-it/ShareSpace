package com.lmtri.sharespace.fragment.appointment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.lmtri.sharespace.adapter.appointmenttab.ShareHousingAppointmentRecyclerViewAdapter;
import com.lmtri.sharespace.api.model.AppointmentNotification;
import com.lmtri.sharespace.api.model.AppointmentNotificationData;
import com.lmtri.sharespace.api.model.FCMResponse;
import com.lmtri.sharespace.api.model.ShareHousingAppointment;
import com.lmtri.sharespace.api.service.RetrofitClient;
import com.lmtri.sharespace.api.service.user.UserClient;
import com.lmtri.sharespace.api.service.user.appointment.sharehousing.IGetMoreNewerShareHousingAppointmentsCallback;
import com.lmtri.sharespace.api.service.user.appointment.sharehousing.IGetMoreOlderShareHousingAppointmentsCallback;
import com.lmtri.sharespace.api.service.user.appointment.sharehousing.IGetNumOfShareHousingAppointmentsCallback;
import com.lmtri.sharespace.api.service.user.appointment.sharehousing.IShareHousingAppointmentGettingCallback;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.ToastHelper;
import com.lmtri.sharespace.helper.busevent.SigninEvent;
import com.lmtri.sharespace.helper.busevent.SignoutEvent;
import com.lmtri.sharespace.helper.busevent.appointment.housing.OpenHousingAppointmentNotificationEvent;
import com.lmtri.sharespace.helper.busevent.appointment.sharehousing.DeleteShareHousingAppointmentEvent;
import com.lmtri.sharespace.helper.busevent.appointment.sharehousing.OpenShareHousingAppointmentNotificationEvent;
import com.lmtri.sharespace.helper.busevent.appointment.sharehousing.SetNewShareHousingAppointmentEvent;
import com.lmtri.sharespace.helper.busevent.appointment.sharehousing.UpdateShareHousingAppointmentEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.save.SaveShareHousingEvent;
import com.lmtri.sharespace.helper.busevent.sharehousing.save.UnsaveShareHousingEvent;
import com.lmtri.sharespace.helper.firebasecloudmessaging.FCMClient;
import com.lmtri.sharespace.helper.firebasecloudmessaging.IFCMSendNotificationCallback;
import com.lmtri.sharespace.listener.EndlessRecyclerViewScrollListener;
import com.lmtri.sharespace.listener.OnShareHousingAppointmentListInteractionListener;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShareHousingAppointmentTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShareHousingAppointmentTabFragment extends Fragment {

    public static final String TAG = ShareHousingAppointmentTabFragment.class.getSimpleName();

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private LinearLayout mConnectionErrorLayout;
    private TextView mConnectionErrorMessage;
    private ViewSwitcher mViewSwitcherRetryConnect;
    private FrameLayout mRetryConnectButton;

//    private SwipeRefreshLayout mSwipeRefreshContainer;
    private RecyclerView mRecyclerView;
    private OnShareHousingAppointmentListInteractionListener mListener;
    private EndlessRecyclerViewScrollListener mRecyclerViewScrollListener;

    private int mNumOfShareHousingAppointments = 0;
    private List<ShareHousingAppointment> mShareHousingAppointments = new ArrayList<>();
    private ShareHousingAppointmentRecyclerViewAdapter mShareHousingAppointmentRecyclerViewAdapter;

    public ShareHousingAppointmentTabFragment() {
        // Required empty public constructor
    }


    public static ShareHousingAppointmentTabFragment newInstance(int columnCount) {
        ShareHousingAppointmentTabFragment fragment = new ShareHousingAppointmentTabFragment();
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
        View view = inflater.inflate(R.layout.fragment_share_housing_appointment_tab, container, false);

        mConnectionErrorLayout = (LinearLayout) view.findViewById(R.id.fragment_share_housing_appointment_tab_connection_error_layout);
        mConnectionErrorMessage = (TextView) view.findViewById(R.id.fragment_share_housing_appointment_tab_connection_error_message);
        mViewSwitcherRetryConnect = (ViewSwitcher) view.findViewById(R.id.fragment_share_housing_appointment_tab_view_switcher_retry_connect_to_server);
        mRetryConnectButton = (FrameLayout) view.findViewById(R.id.fragment_share_housing_appointment_tab_retry_button);
        mRetryConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewSwitcherRetryConnect.showNext();
                loadMoreOlderShareHousingAppointmentsFromApi(0);
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

//        mSwipeRefreshContainer = (SwipeRefreshLayout) view.findViewById(R.id.fragment_share_housing_appointment_tab_swipe_refresh_container);
//        mSwipeRefreshContainer.setColorSchemeResources(
//                R.color.colorPrimary,
//                R.color.colorPrimaryLight,
//                R.color.colorSecondary
//        );
//        mSwipeRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                if (mShareHousingAppointments != null && mShareHousingAppointments.size() > 0) {
//                    if (Constants.CURRENT_USER != null) {
//                        String s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(mShareHousingAppointments.get(0).getDateTimeCreated());
//                        s = s.substring(0, s.length() - 2) + ":" + s.substring(s.length() - 2, s.length());
//                        loadMoreNewerShareHousingAppointmentsFromApi(s);
//                    }
//                } else {
//                    if (Constants.CURRENT_USER != null) {
//                        loadMoreOlderShareHousingAppointmentsFromApi(0);
//                    }
//                }
//            }
//        });
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_share_housing_appointment_tab_share_list);
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
                        loadMoreOlderShareHousingAppointmentsFromApi(page);
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
                        loadMoreOlderShareHousingAppointmentsFromApi(page);
                    }
                }
            };
        }

        mShareHousingAppointmentRecyclerViewAdapter = new ShareHousingAppointmentRecyclerViewAdapter(getContext(), mShareHousingAppointments, mListener);
        mRecyclerView.setAdapter(mShareHousingAppointmentRecyclerViewAdapter);
        mRecyclerView.addOnScrollListener(mRecyclerViewScrollListener);

        return view;
    }

    @Subscribe
    public void userSignin(SigninEvent event) {
        if (mNumOfShareHousingAppointments == 0) {
            mConnectionErrorLayout.setVisibility(View.VISIBLE);
            UserClient.getNumOfShareHousingAppointments(new IGetNumOfShareHousingAppointmentsCallback() {
                @Override
                public void onGetComplete(Integer numOfShareHousingAppointments) {
                    if (numOfShareHousingAppointments != null && numOfShareHousingAppointments > 0) {
                        mNumOfShareHousingAppointments = numOfShareHousingAppointments;
                        loadMoreOlderShareHousingAppointmentsFromApi(0);
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
        mNumOfShareHousingAppointments = 0;
        mShareHousingAppointments.clear();
        mShareHousingAppointmentRecyclerViewAdapter.notifyDataSetChanged();
        mShareHousingAppointments = new ArrayList<>();
        mShareHousingAppointmentRecyclerViewAdapter = new ShareHousingAppointmentRecyclerViewAdapter(getContext(), mShareHousingAppointments, mListener);
        mRecyclerView.setAdapter(mShareHousingAppointmentRecyclerViewAdapter);
    }

    @Subscribe
    public void saveShareHousing(SaveShareHousingEvent event) {
        if (event.getSavedShareHousing() != null) {
            for (int i = 0; i < mShareHousingAppointments.size(); ++i) {
                if (event.getSavedShareHousing().getSavedShareHousing().getID()
                        == mShareHousingAppointments.get(i).getShareHousing().getID()) {
                    mShareHousingAppointments.get(i).getShareHousing().setNumOfSaved(
                            mShareHousingAppointments.get(i).getShareHousing().getNumOfSaved() + 1
                    );
                    break;
                }
            }
        }
    }

    @Subscribe
    public void unsaveShareHousing(UnsaveShareHousingEvent event) {
        if (event.getSavedShareHousing() != null) {
            for (int i = 0; i < mShareHousingAppointments.size(); ++i) {
                if (event.getSavedShareHousing().getSavedShareHousing().getID()
                        == mShareHousingAppointments.get(i).getShareHousing().getID()) {
                    mShareHousingAppointments.get(i).getShareHousing().setNumOfSaved(
                            mShareHousingAppointments.get(i).getShareHousing().getNumOfSaved() + 1
                    );
                    break;
                }
            }
        }
    }

    @Subscribe
    public void setNewShareHousingAppointment(SetNewShareHousingAppointmentEvent event) {
        if (event.getShareHousingAppointment() != null
                && event.getShareHousingAppointment().getShareHousing()
                .getCreator().getDeviceTokens() != null) {
            FCMClient.sendNotification(
                    new AppointmentNotification(
                            new AppointmentNotificationData(
                                    Constants.SHARE_HOUSING_APPOINTMENT_TYPE,
                                    Constants.SET_NEW_APPOINTMENT_NOTIFICATION,
                                    event.getShareHousingAppointment().getShareHousing().getID(),
                                    event.getShareHousingAppointment().getSender().getUserID(),
                                    event.getShareHousingAppointment().getShareHousing().getCreator().getUserID()
                            ),
                            event.getShareHousingAppointment().getShareHousing()
                                    .getCreator().getDeviceTokens().split(";")
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
            mShareHousingAppointments.add(0, event.getShareHousingAppointment());
            mShareHousingAppointmentRecyclerViewAdapter.notifyItemInserted(0);
        }
    }

    @Subscribe
    public void updateShareHousingAppointment(UpdateShareHousingAppointmentEvent event) {
        if (event.getShareHousingAppointment() != null
                && event.getShareHousingAppointment().getSender().getDeviceTokens() != null
                && event.getShareHousingAppointment().getShareHousing().getCreator().getDeviceTokens() != null) {
            FCMClient.sendNotification(
                    new AppointmentNotification(
                            new AppointmentNotificationData(
                                    Constants.SHARE_HOUSING_APPOINTMENT_TYPE,
                                    Constants.UPDATE_APPOINTMENT_NOTIFICATION,
                                    event.getShareHousingAppointment().getShareHousing().getID(),
                                    event.getShareHousingAppointment().getSender().getUserID(),
                                    event.getShareHousingAppointment().getShareHousing().getCreator().getUserID()
                            ),
                            Constants.CURRENT_USER.getUserID()
                                    == event.getShareHousingAppointment().getShareHousing().getCreator().getUserID()
                                    ? event.getShareHousingAppointment().getSender().getDeviceTokens().split(";")
                                    : event.getShareHousingAppointment().getShareHousing().getCreator().getDeviceTokens().split(";")
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
            for (int i = 0; i < mShareHousingAppointments.size(); ++i) {
                if (event.getShareHousingAppointment().getShareHousing().getID()
                        == mShareHousingAppointments.get(i).getShareHousing().getID()) {
                    mShareHousingAppointments.remove(i);
                    mShareHousingAppointments.add(0, event.getShareHousingAppointment());
                    mShareHousingAppointmentRecyclerViewAdapter.notifyItemRemoved(i);
                    mShareHousingAppointmentRecyclerViewAdapter.notifyItemInserted(0);
                    break;
                }
            }
        }
    }

    @Subscribe
    public void deleteShareHousingAppointment(DeleteShareHousingAppointmentEvent event) {
        if (event.getShareHousingAppointment() != null
                && event.getShareHousingAppointment().getSender().getDeviceTokens() != null
                && event.getShareHousingAppointment().getShareHousing().getCreator().getDeviceTokens() != null) {
            FCMClient.sendNotification(
                    new AppointmentNotification(
                            new AppointmentNotificationData(
                                    Constants.SHARE_HOUSING_APPOINTMENT_TYPE,
                                    Constants.DELETE_APPOINTMENT_NOTIFICATION,
                                    event.getShareHousingAppointment().getShareHousing().getID(),
                                    event.getShareHousingAppointment().getSender().getUserID(),
                                    event.getShareHousingAppointment().getShareHousing().getCreator().getUserID()
                            ),
                            Constants.CURRENT_USER.getUserID()
                                    == event.getShareHousingAppointment().getShareHousing().getCreator().getUserID()
                                    ? event.getShareHousingAppointment().getSender().getDeviceTokens().split(";")
                                    : event.getShareHousingAppointment().getShareHousing().getCreator().getDeviceTokens().split(";")
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
            for (int i = 0; i < mShareHousingAppointments.size(); ++i) {
                if (event.getShareHousingAppointment().getShareHousing().getID()
                        == mShareHousingAppointments.get(i).getShareHousing().getID()) {
                    mShareHousingAppointments.remove(i);
                    mShareHousingAppointmentRecyclerViewAdapter.notifyItemRemoved(i);
                    break;
                }
            }
            if (mShareHousingAppointments.isEmpty()) {
                mConnectionErrorLayout.setVisibility(View.VISIBLE);
                if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                    mViewSwitcherRetryConnect.showNext();
                }
            }
        }
    }

    @Subscribe
    public void openShareHousingAppointmentNotificationEvent(final OpenShareHousingAppointmentNotificationEvent event) {
        if (event.getData() != null) {
            final AppointmentNotificationData data = event.getData();
            UserClient.getSpecificShareHousingAppointment(
                    event.getData().getHousingOrShareHousingID(),
                    event.getData().getSenderID(),
                    event.getData().getOwnerOrCreatorID(),
                    new IShareHousingAppointmentGettingCallback() {
                        @Override
                        public void onGetComplete(ShareHousingAppointment shareHousingAppointment) {
                            if (shareHousingAppointment != null) {
                                if (data.getAppointmentType() == Constants.SET_NEW_APPOINTMENT_NOTIFICATION) {
                                    if (mConnectionErrorLayout.getVisibility() == View.VISIBLE) {
                                        mConnectionErrorLayout.setVisibility(View.GONE);
                                    }
                                    mShareHousingAppointments.add(0, shareHousingAppointment);
                                    mShareHousingAppointmentRecyclerViewAdapter.notifyItemInserted(0);
                                } else if (data.getAppointmentType() == Constants.UPDATE_APPOINTMENT_NOTIFICATION) {
                                    for (int i = 0; i < mShareHousingAppointments.size(); ++i) {
                                        if (shareHousingAppointment.getShareHousing().getID()
                                                == mShareHousingAppointments.get(i).getShareHousing().getID()) {
                                            mShareHousingAppointments.remove(i);
                                            mShareHousingAppointments.add(0, shareHousingAppointment);
                                            mShareHousingAppointmentRecyclerViewAdapter.notifyItemRemoved(i);
                                            mShareHousingAppointmentRecyclerViewAdapter.notifyItemInserted(0);
                                            break;
                                        }
                                    }
                                } else if (data.getAppointmentType() == Constants.ACCEPT_APPOINTMENT_NOTIFICATION) {
                                    for (int i = 0; i < mShareHousingAppointments.size(); ++i) {
                                        if (shareHousingAppointment.getShareHousing().getID()
                                                == mShareHousingAppointments.get(i).getShareHousing().getID()) {
                                            mShareHousingAppointments.remove(i);
                                            mShareHousingAppointments.add(0, shareHousingAppointment);
                                            mShareHousingAppointmentRecyclerViewAdapter.notifyItemRemoved(i);
                                            mShareHousingAppointmentRecyclerViewAdapter.notifyItemInserted(0);
                                            break;
                                        }
                                    }
                                }
                            }
                            if (data.getAppointmentType() == Constants.DELETE_APPOINTMENT_NOTIFICATION) {
                                for (int i = 0; i < mShareHousingAppointments.size(); ++i) {
                                    if (data.getHousingOrShareHousingID()
                                            == mShareHousingAppointments.get(i).getShareHousing().getID()) {
                                        mShareHousingAppointments.remove(i);
                                        mShareHousingAppointmentRecyclerViewAdapter.notifyItemRemoved(i);
                                        break;
                                    }
                                }
                                if (mShareHousingAppointments.isEmpty()) {
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

//    private void loadMoreNewerShareHousingAppointmentsFromApi(String currentTopHousingAppointmentDateTimeCreated) {
//        UserClient.getMoreNewerShareHousingAppointments(
//                currentTopHousingAppointmentDateTimeCreated,
//                new IGetMoreNewerShareHousingAppointmentsCallback() {
//                    @Override
//                    public void onGetComplete(List<ShareHousingAppointment> newShareHousingAppointments) {
////                        mSwipeRefreshContainer.setRefreshing(false);
//                        if (mShareHousingAppointments.size() == 0) {
//                            if (newShareHousingAppointments != null && newShareHousingAppointments.size() > 0) {
//                                mShareHousingAppointments.addAll(0, newShareHousingAppointments);
//
//                                mShareHousingAppointmentRecyclerViewAdapter.notifyItemRangeInserted(0, newShareHousingAppointments.size());
//                            } else {
////                                RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext());
//                            }
//                        } else if (mShareHousingAppointments.size() > 0) {
//                            if (newShareHousingAppointments != null && newShareHousingAppointments.size() > 0) {
//                                mShareHousingAppointments.addAll(0, newShareHousingAppointments);
//
//                                mShareHousingAppointmentRecyclerViewAdapter.notifyItemRangeInserted(0, newShareHousingAppointments.size());
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onGetFailure(Throwable t) {
////                        mSwipeRefreshContainer.setRefreshing(false);
//                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext(), t);
//                    }
//                }
//        );
//    }


    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadMoreOlderShareHousingAppointmentsFromApi(final int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
        String currentBottomShareHousingAppointmentDateTimeCreated
                = !mShareHousingAppointments.isEmpty()
                ? new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(mShareHousingAppointments.get(mShareHousingAppointments.size() - 1).getDateTimeCreated())
                : null;
        if (currentBottomShareHousingAppointmentDateTimeCreated != null) {
            currentBottomShareHousingAppointmentDateTimeCreated
                    = currentBottomShareHousingAppointmentDateTimeCreated.substring(0, currentBottomShareHousingAppointmentDateTimeCreated.length() - 2)
                    + ":" + currentBottomShareHousingAppointmentDateTimeCreated.substring(currentBottomShareHousingAppointmentDateTimeCreated.length() - 2, currentBottomShareHousingAppointmentDateTimeCreated.length());
        }
        UserClient.getMoreOlderShareHousingAppointments(currentBottomShareHousingAppointmentDateTimeCreated,
                new IGetMoreOlderShareHousingAppointmentsCallback() {
                    @Override
                    public void onGetComplete(List<ShareHousingAppointment> olderShareHousingAppointments) {
//                        mSwipeRefreshContainer.setRefreshing(false);
                        if (mShareHousingAppointments.size() == 0) {
                            if (olderShareHousingAppointments != null && olderShareHousingAppointments.size() > 0) {
                                if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                                    mViewSwitcherRetryConnect.showNext();
                                }
                                mConnectionErrorLayout.setVisibility(View.GONE);

                                int positionStart = mShareHousingAppointments.size();

                                mShareHousingAppointments.addAll(olderShareHousingAppointments);

                                mShareHousingAppointmentRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mShareHousingAppointments.size() - positionStart);

                                if (mShareHousingAppointments.size() < mNumOfShareHousingAppointments) {
                                    loadMoreOlderShareHousingAppointmentsFromApi(1);
                                }
                            } else {
//                                mConnectionErrorMessage.setVisibility(View.VISIBLE);
                                if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                                    mViewSwitcherRetryConnect.showNext();
                                }
//                                RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext());
                            }
                        } else if (mShareHousingAppointments.size() > 0) {
                            if (olderShareHousingAppointments != null && olderShareHousingAppointments.size() > 0) {
                                int positionStart = mShareHousingAppointments.size();

                                mShareHousingAppointments.addAll(olderShareHousingAppointments);

                                mShareHousingAppointmentRecyclerViewAdapter.notifyItemRangeInserted(positionStart, mShareHousingAppointments.size() - positionStart);

                                if (mShareHousingAppointments.size() < mNumOfShareHousingAppointments) {
                                    loadMoreOlderShareHousingAppointmentsFromApi(1);
                                }
                            }
//                            else {
//                                RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext());
//                            }
                        }
                    }

                    @Override
                    public void onGetFailure(Throwable t) {
//                        mSwipeRefreshContainer.setRefreshing(false);

//                        mConnectionErrorMessage.setVisibility(View.VISIBLE);
                        if (mViewSwitcherRetryConnect.getCurrentView() != mRetryConnectButton) {
                            mViewSwitcherRetryConnect.showNext();
                        }
                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(getContext(), t);
                    }
                });
    }

    public void setListener(OnShareHousingAppointmentListInteractionListener listener) {
        mListener = listener;
    }
}
