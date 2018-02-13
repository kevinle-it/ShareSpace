package com.lmtri.sharespace.adapter.appointmenttab;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.lmtri.sharespace.R;
import com.lmtri.sharespace.ShareSpaceApplication;
import com.lmtri.sharespace.api.model.AppointmentNotification;
import com.lmtri.sharespace.api.model.AppointmentNotificationData;
import com.lmtri.sharespace.api.model.FCMResponse;
import com.lmtri.sharespace.api.model.ShareHousingAppointment;
import com.lmtri.sharespace.api.service.RetrofitClient;
import com.lmtri.sharespace.api.service.user.UserClient;
import com.lmtri.sharespace.api.service.user.appointment.sharehousing.IShareHousingAppointmentAcceptingCallback;
import com.lmtri.sharespace.api.service.user.appointment.sharehousing.IShareHousingAppointmentDeletingCallback;
import com.lmtri.sharespace.api.service.user.appointment.sharehousing.IShareHousingAppointmentUpdatingCallback;
import com.lmtri.sharespace.customview.CustomEditText;
import com.lmtri.sharespace.customview.SquareImageView;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.HousePriceHelper;
import com.lmtri.sharespace.helper.ToastHelper;
import com.lmtri.sharespace.helper.busevent.appointment.sharehousing.AcceptShareHousingAppointmentEvent;
import com.lmtri.sharespace.helper.busevent.appointment.sharehousing.DeleteShareHousingAppointmentEvent;
import com.lmtri.sharespace.helper.busevent.appointment.sharehousing.UpdateShareHousingAppointmentEvent;
import com.lmtri.sharespace.helper.firebasecloudmessaging.FCMClient;
import com.lmtri.sharespace.helper.firebasecloudmessaging.IFCMSendNotificationCallback;
import com.lmtri.sharespace.listener.OnShareHousingAppointmentListInteractionListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by lmtri on 12/4/2017.
 */

public class ShareHousingAppointmentRecyclerViewAdapter extends RecyclerView.Adapter<ShareHousingAppointmentRecyclerViewAdapter.ViewHolder> {

    public static final String TAG = ShareHousingAppointmentRecyclerViewAdapter.class.getSimpleName();

    private final Context mContext;
    private final List<ShareHousingAppointment> mShareHousingAppointments;
    private final OnShareHousingAppointmentListInteractionListener mListener;

    private AlertDialog mScheduleDatePickerDialogJellyBean;
    private AlertDialog mScheduleTimePickerDialogJellyBean;
    private DatePickerDialog mScheduleDatePickerDialog;
    private TimePickerDialog mScheduleTimePickerDialog;
    private AlertDialog mScheduleNotesDialog;
    private CustomEditText mInputScheduleNotes;

    public ShareHousingAppointmentRecyclerViewAdapter(Context context, List<ShareHousingAppointment> shareHousingAppointments, OnShareHousingAppointmentListInteractionListener listener) {
        mContext = context;
        mShareHousingAppointments = shareHousingAppointments;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mInputScheduleNotes == null) {
            mInputScheduleNotes = new CustomEditText(mContext);
            mInputScheduleNotes.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            mInputScheduleNotes.setFilters(
                    new InputFilter[] {
                            new InputFilter.LengthFilter(
                                    Constants.ACTIVITY_HOUSING_DETAIL_SCHEDULE_NOTE_NUM_CHARACTERS_LIMIT
                            )
                    }
            );
        }
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_share_housing_appointment_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mShareHousingAppointments.get(position);
        if (!mShareHousingAppointments.get(position).getShareHousing().getHousing().getPhotoURLs().isEmpty()) {
            String profileImageUrl = mShareHousingAppointments.get(position).getShareHousing().getHousing().getPhotoURLs().get(0);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                Glide.with(mContext)
                        .load(R.drawable.ic_home)
                        .asBitmap()
                        .fitCenter()
                        .into(new BitmapImageViewTarget(holder.mPlaceHolderProfileImage) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                holder.mPlaceHolderProfileImage.setImageDrawable(new BitmapDrawable(mContext.getResources(), resource));
                            }
                        });
                Glide.with(mContext)
                        .load(profileImageUrl)
                        .asBitmap()
                        .into(new BitmapImageViewTarget(holder.mProfileImage) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                holder.mProfileImage.setImageDrawable(new BitmapDrawable(mContext.getResources(), resource));
                            }
                        });
            } else {
                Glide.with(mContext)
                        .load(profileImageUrl)
                        .placeholder(R.drawable.ic_home)
                        .crossFade()
                        .into(holder.mProfileImage);
            }
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                Glide.with(mContext)
                        .load(R.drawable.ic_home)
                        .asBitmap()
                        .fitCenter()
                        .into(new BitmapImageViewTarget(holder.mPlaceHolderProfileImage) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                holder.mPlaceHolderProfileImage.setImageDrawable(new BitmapDrawable(mContext.getResources(), resource));
                            }
                        });
            } else {
                Glide.with(mContext)
                        .load(R.drawable.ic_home)
                        .crossFade()
                        .into(holder.mProfileImage);
            }
        }
        if (!TextUtils.isEmpty(mShareHousingAppointments.get(position).getShareHousing().getHousing().getTitle())) {
            holder.mHouseTitle.setText(mShareHousingAppointments.get(position).getShareHousing().getHousing().getTitle());
        }
        if (mShareHousingAppointments.get(position).getAppointmentDateTime() != null) {
            holder.mShareHousingAppointmentDateTime.setText(
                    new SimpleDateFormat("dd-MM-yyyy'T'HH:mm'Z'")
                            .format(mShareHousingAppointments.get(position).getAppointmentDateTime())
            );
        }

        Pair<String, String> pair = HousePriceHelper.parseForShareHousing(mShareHousingAppointments.get(position).getShareHousing().getPricePerMonthOfOne(), mContext);
        if (pair.first == null) {
            holder.mHousePrice.setText(pair.second);
        } else {
            holder.mHousePrice.setText(pair.first + " " + pair.second);
        }

        if (!TextUtils.isEmpty(mShareHousingAppointments.get(position).getShareHousing().getRequiredGender())) {
            holder.mRequiredGender.setText(mShareHousingAppointments.get(position).getShareHousing().getRequiredGender());
        } else {
            holder.mRequiredGender.setText(mContext.getString(R.string.hint_signup_gender_female));
        }
        if (mShareHousingAppointments.get(position).getAppointmentDateTime() != null) {
            holder.mShareHousingAppointmentDateTime.setText(
                    new SimpleDateFormat("dd-MM-yyyy HH:mm")
                            .format(mShareHousingAppointments.get(position).getAppointmentDateTime())
            );
        }
        if (mShareHousingAppointments.get(position).getShareHousing().getRequiredNumOfPeople() <= 0) {
            holder.mRequiredNumOfPeople.setText(1 + mContext.getString(R.string.activity_housing_detail_people));
        } else {
            holder.mRequiredNumOfPeople.setText(mShareHousingAppointments.get(position).getShareHousing().getRequiredNumOfPeople() + mContext.getString(R.string.activity_housing_detail_people));
        }
        if (!TextUtils.isEmpty(mShareHousingAppointments.get(position).getContent())) {
            holder.mHousingAppointmentNotes.setText(mShareHousingAppointments.get(position).getContent());
        } else {
            holder.mHousingAppointmentNotes.setText(R.string.fragment_housing_appointment_empty_content);
        }

        if (Constants.CURRENT_USER != null) {
            if (Constants.CURRENT_USER.getUserID() == mShareHousingAppointments.get(position).getShareHousing().getCreator().getUserID()) {     // Share Housing Creator
                if (mShareHousingAppointments.get(position).isCreatorConfirmed()) {
                    holder.mHousingAppointmentAcceptButton.setVisibility(View.GONE);
                    holder.mHousingAppointmentAcceptButtonLeftVerticalBorderLine.setVisibility(View.GONE);
                }
                if (mShareHousingAppointments.get(position).isUserConfirmed()) {
                    holder.mSendReceiveHousingAppointmentSign.setImageResource(R.drawable.ic_receive);
                    holder.mSendReceiveHousingAppointmentSign.setColorFilter(ContextCompat.getColor(mContext, R.color.light_blue));
                } else {
                    holder.mSendReceiveHousingAppointmentSign.setImageResource(R.drawable.ic_wait_confirm);
                    holder.mSendReceiveHousingAppointmentSign.setColorFilter(ContextCompat.getColor(mContext, R.color.light_red));
                }
            } else {    // User
                if (mShareHousingAppointments.get(position).getNumOfRequests() < 2) {
                    holder.mHousingAppointmentAcceptButton.setVisibility(View.GONE);
                    holder.mHousingAppointmentAcceptButtonLeftVerticalBorderLine.setVisibility(View.GONE);
                } else {
                    if (mShareHousingAppointments.get(position).isUserConfirmed()) {
                        holder.mHousingAppointmentAcceptButton.setVisibility(View.GONE);
                        holder.mHousingAppointmentAcceptButtonLeftVerticalBorderLine.setVisibility(View.GONE);
                    }
                }
                if (!mShareHousingAppointments.get(position).isCreatorConfirmed()) {
                    holder.mSendReceiveHousingAppointmentSign.setImageResource(R.drawable.ic_wait_confirm);
                    holder.mSendReceiveHousingAppointmentSign.setColorFilter(ContextCompat.getColor(mContext, R.color.light_red));
                } else {
                    holder.mSendReceiveHousingAppointmentSign.setImageResource(R.drawable.ic_send);
                    holder.mSendReceiveHousingAppointmentSign.setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary));
                }
            }
        }

        holder.mHousingAppointmentAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserClient.acceptShareHousingAppointment(
                        mShareHousingAppointments.get(position).getShareHousing().getID(),
                        Constants.CURRENT_USER.getUserID()
                                == mShareHousingAppointments.get(position).getShareHousing().getCreator().getUserID()
                                ? mShareHousingAppointments.get(position).getSender().getUserID()
                                : mShareHousingAppointments.get(position).getRecipientID(),
                        new IShareHousingAppointmentAcceptingCallback() {
                            @Override
                            public void onAcceptComplete(ShareHousingAppointment shareHousingAppointment) {
                                if (shareHousingAppointment != null) {
                                    mShareHousingAppointments.set(position, shareHousingAppointment);
                                    notifyItemChanged(position);
                                    ToastHelper.showCenterToast(
                                            mContext,
                                            Constants.CURRENT_USER.getUserID()
                                                    == mShareHousingAppointments.get(position).getShareHousing().getCreator().getUserID()
                                                    ? R.string.fragment_housing_appointment_tab_share_housing_creator_accept_successfully_message
                                                    : R.string.fragment_housing_appointment_tab_user_accept_share_housing_creator_successfully_message,
                                            Toast.LENGTH_LONG
                                    );
                                    ShareSpaceApplication.BUS.post(new AcceptShareHousingAppointmentEvent(mShareHousingAppointments.get(position)));

                                    if (shareHousingAppointment.getSender().getDeviceTokens() != null
                                            && shareHousingAppointment.getShareHousing().getCreator().getDeviceTokens() != null) {
                                        FCMClient.sendNotification(
                                                new AppointmentNotification(
                                                        new AppointmentNotificationData(
                                                                Constants.SHARE_HOUSING_APPOINTMENT_TYPE,
                                                                Constants.ACCEPT_APPOINTMENT_NOTIFICATION,
                                                                shareHousingAppointment.getShareHousing().getID(),
                                                                shareHousingAppointment.getSender().getUserID(),
                                                                shareHousingAppointment.getShareHousing().getCreator().getUserID()
                                                        ),
                                                        Constants.CURRENT_USER.getUserID()
                                                                == shareHousingAppointment.getShareHousing().getCreator().getUserID()
                                                                ? shareHousingAppointment.getSender().getDeviceTokens().split(";")
                                                                : shareHousingAppointment.getShareHousing().getCreator().getDeviceTokens().split(";")
                                                ),
                                                new IFCMSendNotificationCallback() {
                                                    @Override
                                                    public void onSendComplete(FCMResponse fcmResponse) {
                                                        if (fcmResponse != null) {
                                                            if (fcmResponse.getNumSuccess() == 0) {
                                                                ToastHelper.showCenterToast(
                                                                        mContext,
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
                                    }
                                } else {
                                    ToastHelper.showCenterToast(
                                            mContext,
                                            Constants.CURRENT_USER.getUserID()
                                                    == mShareHousingAppointments.get(position).getShareHousing().getCreator().getUserID()
                                                    ? R.string.fragment_housing_appointment_tab_share_housing_creator_accept_unsuccessfully_message
                                                    : R.string.fragment_housing_appointment_tab_user_accept_share_housing_creator_unsuccessfully_message,
                                            Toast.LENGTH_LONG
                                    );
                                }
                            }

                            @Override
                            public void onAcceptFailure(Throwable t) {

                                ToastHelper.showCenterToast(
                                        mContext,
                                        Constants.CURRENT_USER.getUserID()
                                                == mShareHousingAppointments.get(position).getShareHousing().getCreator().getUserID()
                                                ? R.string.fragment_housing_appointment_tab_share_housing_creator_accept_unsuccessfully_message
                                                : R.string.fragment_housing_appointment_tab_user_accept_share_housing_creator_unsuccessfully_message,
                                        Toast.LENGTH_LONG
                                );
                                RetrofitClient.showShareSpaceServerConnectionErrorDialog(mContext, t);
                            }
                        }
                );
            }
        });
        holder.mHousingAppointmentEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.CURRENT_USER != null) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        handleShareHousingAppointment(0, position);
                    } else {
                        handleShareHousingAppointmentJellyBean(0, position);
                    }
                }
            }
        });
        holder.mHousingAppointmentRejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.CURRENT_USER != null) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        handleShareHousingAppointment(1, position);
                    } else {
                        handleShareHousingAppointmentJellyBean(1, position);
                    }
                }
            }
        });
        holder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onShareHousingAppointmentListInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mShareHousingAppointments != null ? mShareHousingAppointments.size() : 0;
    }

    private void handleShareHousingAppointment(int selectedOption, final int position) {
        final Calendar calendar = Calendar.getInstance();
        if (selectedOption == 0) {
            DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendar.set(Calendar.MINUTE, minute);

                            if (!TextUtils.isEmpty(mShareHousingAppointments.get(position).getContent())) {
                                mInputScheduleNotes.setText(mShareHousingAppointments.get(position).getContent());
                            }
                            FrameLayout container = new FrameLayout(mContext);
                            FrameLayout.LayoutParams inputScheduleNotesLayoutParams = new FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            inputScheduleNotesLayoutParams.setMargins(
                                    mContext.getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_user_note_dialog_edittext_left_right_margin),
                                    0,
                                    mContext.getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_user_note_dialog_edittext_left_right_margin),
                                    0
                            );
                            mInputScheduleNotes.setLayoutParams(inputScheduleNotesLayoutParams);
                            if (mInputScheduleNotes.getParent() != null) {
                                ((ViewGroup) mInputScheduleNotes.getParent()).removeView(mInputScheduleNotes);
                            }
                            container.addView(mInputScheduleNotes);

                            mScheduleNotesDialog = new AlertDialog.Builder(mContext).create();
                            mScheduleNotesDialog.setTitle(R.string.activity_housing_detail_schedule_note_dialog_title);
                            mScheduleNotesDialog.setMessage(mContext.getString(R.string.activity_housing_detail_schedule_note_dialog_message));
                            mScheduleNotesDialog.setView(container);
                            mScheduleNotesDialog.setButton(DialogInterface.BUTTON_POSITIVE, mContext.getString(R.string.activity_housing_detail_schedule_note_dialog_save),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    String s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(calendar.getTime());
                                                    s = s.substring(0, s.length() - 2) + ":" + s.substring(s.length() - 2, s.length());
                                                    UserClient.updateShareHousingAppointment(
                                                            mShareHousingAppointments.get(position).getShareHousing().getID(),
                                                            Constants.CURRENT_USER.getUserID()
                                                                    == mShareHousingAppointments.get(position).getShareHousing().getCreator().getUserID()
                                                                    ? mShareHousingAppointments.get(position).getSender().getUserID()
                                                                    : mShareHousingAppointments.get(position).getRecipientID(),
                                                            s, mInputScheduleNotes.getText().toString(),
                                                            new IShareHousingAppointmentUpdatingCallback() {
                                                                @Override
                                                                public void onUpdateComplete(ShareHousingAppointment shareHousingAppointment) {
                                                                    if (shareHousingAppointment != null) {
                                                                        ToastHelper.showCenterToast(
                                                                                mContext,
                                                                                Constants.CURRENT_USER.getUserID()
                                                                                        == mShareHousingAppointments.get(position).getShareHousing().getCreator().getUserID()
                                                                                        ? R.string.fragment_housing_appointment_tab_share_housing_creator_set_schedule_successfully_message
                                                                                        : R.string.fragment_housing_appointment_tab_user_set_share_housing_creator_schedule_successfully_message,
                                                                                Toast.LENGTH_LONG
                                                                        );
                                                                        ShareSpaceApplication.BUS.post(new UpdateShareHousingAppointmentEvent(
                                                                                shareHousingAppointment
                                                                        ));
                                                                    } else {
                                                                        ToastHelper.showCenterToast(
                                                                                mContext,
                                                                                Constants.CURRENT_USER.getUserID()
                                                                                        == mShareHousingAppointments.get(position).getShareHousing().getCreator().getUserID()
                                                                                        ? R.string.fragment_housing_appointment_tab_share_housing_creator_set_schedule_unsuccessfully_message
                                                                                        : R.string.fragment_housing_appointment_tab_user_set_share_housing_creator_schedule_unsuccessfully_message,
                                                                                Toast.LENGTH_LONG
                                                                        );
                                                                    }
                                                                }

                                                                @Override
                                                                public void onUpdateFailure(Throwable t) {
                                                                    ToastHelper.showCenterToast(
                                                                            mContext,
                                                                            Constants.CURRENT_USER.getUserID()
                                                                                    == mShareHousingAppointments.get(position).getShareHousing().getCreator().getUserID()
                                                                                    ? R.string.fragment_housing_appointment_tab_share_housing_creator_set_schedule_unsuccessfully_message
                                                                                    : R.string.fragment_housing_appointment_tab_user_set_share_housing_creator_schedule_unsuccessfully_message,
                                                                            Toast.LENGTH_LONG
                                                                    );
                                                                    RetrofitClient.showShareSpaceServerConnectionErrorDialog(mContext, t);
                                                                }
                                                            }
                                                    );
                                                }
                                            });
                            mScheduleNotesDialog.setButton(DialogInterface.BUTTON_POSITIVE, mContext.getString(R.string.activity_housing_detail_schedule_note_dialog_cancel),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    new AlertDialog.Builder(mContext)
                                                            .setTitle(R.string.activity_housing_detail_update_schedule_note_confirm_dialog_title)
                                                            .setMessage(R.string.activity_housing_detail_update_schedule_note_confirm_dialog_message)
                                                            .setPositiveButton(R.string.activity_housing_detail_update_schedule_note_confirm_dialog_positive,
                                                                    new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            mScheduleNotesDialog.show();
                                                                        }
                                                                    })
                                                            .setNegativeButton(R.string.activity_housing_detail_update_schedule_note_confirm_dialog_negative, null)
                                                            .show();
                                                }
                                            });
                            mScheduleNotesDialog.setCanceledOnTouchOutside(false);
                            mScheduleNotesDialog.show();
                        }
                    };
                    mScheduleTimePickerDialog =
                            new TimePickerDialog(mContext, onTimeSetListener,
                                    Integer.parseInt(new SimpleDateFormat("HH").format(mShareHousingAppointments.get(position).getAppointmentDateTime())),
                                    Integer.parseInt(new SimpleDateFormat("mm").format(mShareHousingAppointments.get(position).getAppointmentDateTime())),
                                    true
                            );
                    mScheduleTimePickerDialog.setCanceledOnTouchOutside(false);
                    mScheduleTimePickerDialog.show();
                }
            };
            mScheduleDatePickerDialog =
                    new DatePickerDialog(mContext, onDateSetListener,
                            Integer.parseInt(new SimpleDateFormat("yyyy").format(mShareHousingAppointments.get(position).getAppointmentDateTime())),
                            Integer.parseInt(new SimpleDateFormat("MM").format(mShareHousingAppointments.get(position).getAppointmentDateTime())) - 1,
                            Integer.parseInt(new SimpleDateFormat("dd").format(mShareHousingAppointments.get(position).getAppointmentDateTime())));
            mScheduleDatePickerDialog.setTitle(Constants.CURRENT_USER.getUserID()
                    == mShareHousingAppointments.get(position).getShareHousing().getCreator().getUserID()
                    ? R.string.fragment_housing_appointment_tab_share_housing_creator_update_schedule_dialog_title
                    : R.string.fragment_housing_appointment_tab_user_update_share_housing_creator_schedule_dialog_title);
            mScheduleDatePickerDialog.setCanceledOnTouchOutside(false);
            mScheduleDatePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            mScheduleDatePickerDialog.show();
        } else if (selectedOption == 1) {
            new AlertDialog.Builder(mContext)
                    .setTitle(Constants.CURRENT_USER.getUserID()
                            == mShareHousingAppointments.get(position).getShareHousing().getCreator().getUserID()
                            ? R.string.fragment_housing_appointment_tab_housing_owner_delete_schedule_note_confirm_dialog_title
                            : R.string.fragment_housing_appointment_tab_user_delete_share_housing_creator_schedule_note_confirm_dialog_title)
                    .setMessage(R.string.activity_housing_detail_delete_schedule_note_confirm_dialog_message)
                    .setPositiveButton(R.string.activity_housing_detail_delete_schedule_note_confirm_dialog_positive,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    UserClient.deleteShareHousingAppointment(
                                            mShareHousingAppointments.get(position).getShareHousing().getID(),
                                            Constants.CURRENT_USER.getUserID()
                                                    == mShareHousingAppointments.get(position).getShareHousing().getCreator().getUserID()
                                                    ? mShareHousingAppointments.get(position).getSender().getUserID()
                                                    : mShareHousingAppointments.get(position).getRecipientID(),
                                            new IShareHousingAppointmentDeletingCallback() {
                                                @Override
                                                public void onDeleteComplete(ShareHousingAppointment shareHousingAppointment) {
                                                    if (shareHousingAppointment != null) {
                                                        ToastHelper.showCenterToast(
                                                                mContext,
                                                                Constants.CURRENT_USER.getUserID()
                                                                        == mShareHousingAppointments.get(position).getShareHousing().getCreator().getUserID()
                                                                        ? R.string.fragment_housing_appointment_tab_share_housing_creator_delete_schedule_successfully_message
                                                                        : R.string.fragment_housing_appointment_tab_user_delete_share_housing_creator_housing_owner_schedule_successfully_message,
                                                                Toast.LENGTH_LONG
                                                        );
                                                        ShareSpaceApplication.BUS.post(new DeleteShareHousingAppointmentEvent(
                                                                shareHousingAppointment
                                                        ));
                                                    } else {
                                                        ToastHelper.showCenterToast(
                                                                mContext,
                                                                Constants.CURRENT_USER.getUserID()
                                                                        == mShareHousingAppointments.get(position).getShareHousing().getCreator().getUserID()
                                                                        ? R.string.fragment_housing_appointment_tab_share_housing_craetor_delete_schedule_unsuccessfully_message
                                                                        : R.string.fragment_housing_appointment_tab_user_delete_share_housing_creator_schedule_unsuccessfully_message,
                                                                Toast.LENGTH_LONG
                                                        );
                                                    }
                                                }

                                                @Override
                                                public void onDeleteFailure(Throwable t) {
                                                    ToastHelper.showCenterToast(
                                                            mContext,
                                                            Constants.CURRENT_USER.getUserID()
                                                                    == mShareHousingAppointments.get(position).getShareHousing().getCreator().getUserID()
                                                                    ? R.string.fragment_housing_appointment_tab_share_housing_craetor_delete_schedule_unsuccessfully_message
                                                                    : R.string.fragment_housing_appointment_tab_user_delete_share_housing_creator_schedule_unsuccessfully_message,
                                                            Toast.LENGTH_LONG
                                                    );
                                                    RetrofitClient.showShareSpaceServerConnectionErrorDialog(mContext, t);
                                                }
                                            }
                                    );
                                }
                            })
                    .setNegativeButton(R.string.activity_housing_detail_delete_schedule_note_confirm_dialog_negative, null)
                    .show();
        }
    }

    private void handleShareHousingAppointmentJellyBean(int selectedOption, final int position) {
        final Calendar calendar = Calendar.getInstance();
        if (selectedOption == 0) {
            final DatePicker datePicker = new DatePicker(mContext);
            datePicker.init(
                    Integer.parseInt(new SimpleDateFormat("yyyy").format(mShareHousingAppointments.get(position).getAppointmentDateTime())),
                    Integer.parseInt(new SimpleDateFormat("MM").format(mShareHousingAppointments.get(position).getAppointmentDateTime())) - 1,
                    Integer.parseInt(new SimpleDateFormat("dd").format(mShareHousingAppointments.get(position).getAppointmentDateTime())),
                    null
            );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                datePicker.setCalendarViewShown(false);
            }
            datePicker.setMinDate(calendar.getTimeInMillis());

            mScheduleDatePickerDialogJellyBean = new AlertDialog.Builder(mContext).create();
            mScheduleDatePickerDialogJellyBean.setTitle(R.string.activity_share_housing_detail_update_schedule_dialog_title);
            mScheduleDatePickerDialogJellyBean.setView(datePicker);
            mScheduleDatePickerDialogJellyBean.setButton(DialogInterface.BUTTON_POSITIVE, mContext.getString(R.string.activity_housing_detail_set_schedule_dialog_positive_jelly_bean),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            calendar.set(Calendar.YEAR, datePicker.getYear());
                            calendar.set(Calendar.MONTH, datePicker.getMonth());
                            calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());

                            final TimePicker timePicker = new TimePicker(mContext);
                            timePicker.setIs24HourView(true);
                            timePicker.setCurrentHour(Integer.parseInt(new SimpleDateFormat("HH").format(mShareHousingAppointments.get(position).getAppointmentDateTime())));
                            timePicker.setCurrentMinute(Integer.parseInt(new SimpleDateFormat("mm").format(mShareHousingAppointments.get(position).getAppointmentDateTime())));

                            mScheduleTimePickerDialogJellyBean = new AlertDialog.Builder(mContext).create();
                            mScheduleTimePickerDialogJellyBean.setTitle(R.string.activity_housing_detail_set_time_schedule_dialog_title_jelly_bean);
                            mScheduleTimePickerDialogJellyBean.setView(timePicker);
                            mScheduleTimePickerDialogJellyBean.setButton(DialogInterface.BUTTON_POSITIVE, mContext.getString(R.string.activity_housing_detail_set_schedule_dialog_positive_jelly_bean),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                                            calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());

                                            if (!TextUtils.isEmpty(mShareHousingAppointments.get(position).getContent())) {
                                                mInputScheduleNotes.setText(mShareHousingAppointments.get(position).getContent());
                                            } else {
                                                mInputScheduleNotes.setText("");
                                            }
                                            FrameLayout container = new FrameLayout(mContext);
                                            FrameLayout.LayoutParams inputScheduleNotesLayoutParams = new FrameLayout.LayoutParams(
                                                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                            inputScheduleNotesLayoutParams.setMargins(
                                                    mContext.getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_user_note_dialog_edittext_left_right_margin),
                                                    0,
                                                    mContext.getResources().getDimensionPixelSize(R.dimen.activity_housing_detail_user_note_dialog_edittext_left_right_margin),
                                                    0
                                            );
                                            mInputScheduleNotes.setLayoutParams(inputScheduleNotesLayoutParams);
                                            if (mInputScheduleNotes.getParent() != null) {
                                                ((ViewGroup) mInputScheduleNotes.getParent()).removeView(mInputScheduleNotes);
                                            }
                                            container.addView(mInputScheduleNotes);

                                            mScheduleNotesDialog = new AlertDialog.Builder(mContext).create();
                                            mScheduleNotesDialog.setTitle(R.string.activity_housing_detail_schedule_note_dialog_title);
                                            mScheduleNotesDialog.setMessage(mContext.getString(R.string.activity_housing_detail_schedule_note_dialog_message));
                                            mScheduleNotesDialog.setView(container);
                                            mScheduleNotesDialog.setButton(DialogInterface.BUTTON_POSITIVE, mContext.getString(R.string.activity_housing_detail_schedule_note_dialog_save),
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            String s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(calendar.getTime());
                                                            s = s.substring(0, s.length() - 2) + ":" + s.substring(s.length() - 2, s.length());
                                                            UserClient.updateShareHousingAppointment(
                                                                    mShareHousingAppointments.get(position).getShareHousing().getID(),
                                                                    Constants.CURRENT_USER.getUserID()
                                                                            == mShareHousingAppointments.get(position).getShareHousing().getCreator().getUserID()
                                                                            ? mShareHousingAppointments.get(position).getSender().getUserID()
                                                                            : mShareHousingAppointments.get(position).getRecipientID(),
                                                                    s, mInputScheduleNotes.getText().toString(),
                                                                    new IShareHousingAppointmentUpdatingCallback() {
                                                                        @Override
                                                                        public void onUpdateComplete(ShareHousingAppointment shareHousingAppointment) {
                                                                            if (shareHousingAppointment != null) {
                                                                                ToastHelper.showCenterToast(
                                                                                        mContext,
                                                                                        Constants.CURRENT_USER.getUserID()
                                                                                                == mShareHousingAppointments.get(position).getShareHousing().getCreator().getUserID()
                                                                                                ? R.string.fragment_housing_appointment_tab_share_housing_creator_set_schedule_successfully_message
                                                                                                : R.string.fragment_housing_appointment_tab_user_set_share_housing_creator_schedule_successfully_message,
                                                                                        Toast.LENGTH_LONG
                                                                                );
                                                                                ShareSpaceApplication.BUS.post(new UpdateShareHousingAppointmentEvent(
                                                                                        shareHousingAppointment
                                                                                ));
                                                                            } else {
                                                                                ToastHelper.showCenterToast(
                                                                                        mContext,
                                                                                        Constants.CURRENT_USER.getUserID()
                                                                                                == mShareHousingAppointments.get(position).getShareHousing().getCreator().getUserID()
                                                                                                ? R.string.fragment_housing_appointment_tab_share_housing_creator_set_schedule_unsuccessfully_message
                                                                                                : R.string.fragment_housing_appointment_tab_user_set_share_housing_creator_schedule_unsuccessfully_message,
                                                                                        Toast.LENGTH_LONG
                                                                                );
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onUpdateFailure(Throwable t) {
                                                                            ToastHelper.showCenterToast(
                                                                                    mContext,
                                                                                    Constants.CURRENT_USER.getUserID()
                                                                                            == mShareHousingAppointments.get(position).getShareHousing().getCreator().getUserID()
                                                                                            ? R.string.fragment_housing_appointment_tab_share_housing_creator_set_schedule_unsuccessfully_message
                                                                                            : R.string.fragment_housing_appointment_tab_user_set_share_housing_creator_schedule_unsuccessfully_message,
                                                                                    Toast.LENGTH_LONG
                                                                            );
                                                                            RetrofitClient.showShareSpaceServerConnectionErrorDialog(mContext, t);
                                                                        }
                                                                    }
                                                            );
                                                        }
                                                    });
                                            mScheduleNotesDialog.setButton(DialogInterface.BUTTON_POSITIVE, mContext.getString(R.string.activity_housing_detail_schedule_note_dialog_cancel),
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            new AlertDialog.Builder(mContext)
                                                                    .setTitle(R.string.activity_housing_detail_update_schedule_note_confirm_dialog_title)
                                                                    .setMessage(R.string.activity_housing_detail_update_schedule_note_confirm_dialog_message)
                                                                    .setPositiveButton(R.string.activity_housing_detail_update_schedule_note_confirm_dialog_positive,
                                                                            new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    mScheduleNotesDialog.show();
                                                                                }
                                                                            })
                                                                    .setNegativeButton(R.string.activity_housing_detail_update_schedule_note_confirm_dialog_negative, null)
                                                                    .show();
                                                        }
                                                    });
                                            mScheduleNotesDialog.setCanceledOnTouchOutside(false);
                                            mScheduleNotesDialog.show();
                                        }
                                    });
                            mScheduleTimePickerDialogJellyBean.setButton(DialogInterface.BUTTON_NEGATIVE, mContext.getString(R.string.activity_housing_detail_set_schedule_dialog_negative_jelly_bean),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            mScheduleTimePickerDialogJellyBean.setCanceledOnTouchOutside(false);
                            mScheduleTimePickerDialogJellyBean.show();
                        }
                    });
            mScheduleDatePickerDialogJellyBean.setButton(DialogInterface.BUTTON_NEGATIVE, mContext.getString(R.string.activity_housing_detail_set_schedule_dialog_negative_jelly_bean),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            mScheduleDatePickerDialogJellyBean.setCanceledOnTouchOutside(false);
            mScheduleDatePickerDialogJellyBean.show();
        } else if (selectedOption == 1) {
            new AlertDialog.Builder(mContext)
                    .setTitle(Constants.CURRENT_USER.getUserID()
                            == mShareHousingAppointments.get(position).getShareHousing().getCreator().getUserID()
                            ? R.string.fragment_housing_appointment_tab_housing_owner_delete_schedule_note_confirm_dialog_title
                            : R.string.fragment_housing_appointment_tab_user_delete_share_housing_creator_schedule_note_confirm_dialog_title)
                    .setMessage(R.string.activity_housing_detail_delete_schedule_note_confirm_dialog_message)
                    .setPositiveButton(R.string.activity_housing_detail_delete_schedule_note_confirm_dialog_positive,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    UserClient.deleteShareHousingAppointment(
                                            mShareHousingAppointments.get(position).getShareHousing().getID(),
                                            Constants.CURRENT_USER.getUserID()
                                                    == mShareHousingAppointments.get(position).getShareHousing().getCreator().getUserID()
                                                    ? mShareHousingAppointments.get(position).getSender().getUserID()
                                                    : mShareHousingAppointments.get(position).getRecipientID(),
                                            new IShareHousingAppointmentDeletingCallback() {
                                                @Override
                                                public void onDeleteComplete(ShareHousingAppointment shareHousingAppointment) {
                                                    if (shareHousingAppointment != null) {
                                                        ToastHelper.showCenterToast(
                                                                mContext,
                                                                Constants.CURRENT_USER.getUserID()
                                                                        == mShareHousingAppointments.get(position).getShareHousing().getCreator().getUserID()
                                                                        ? R.string.fragment_housing_appointment_tab_share_housing_creator_delete_schedule_successfully_message
                                                                        : R.string.fragment_housing_appointment_tab_user_delete_share_housing_creator_housing_owner_schedule_successfully_message,
                                                                Toast.LENGTH_LONG
                                                        );
                                                        ShareSpaceApplication.BUS.post(new DeleteShareHousingAppointmentEvent(
                                                                shareHousingAppointment
                                                        ));
                                                    } else {
                                                        ToastHelper.showCenterToast(
                                                                mContext,
                                                                Constants.CURRENT_USER.getUserID()
                                                                        == mShareHousingAppointments.get(position).getShareHousing().getCreator().getUserID()
                                                                        ? R.string.fragment_housing_appointment_tab_share_housing_craetor_delete_schedule_unsuccessfully_message
                                                                        : R.string.fragment_housing_appointment_tab_user_delete_share_housing_creator_schedule_unsuccessfully_message,
                                                                Toast.LENGTH_LONG
                                                        );
                                                    }
                                                }

                                                @Override
                                                public void onDeleteFailure(Throwable t) {
                                                    ToastHelper.showCenterToast(
                                                            mContext,
                                                            Constants.CURRENT_USER.getUserID()
                                                                    == mShareHousingAppointments.get(position).getShareHousing().getCreator().getUserID()
                                                                    ? R.string.fragment_housing_appointment_tab_share_housing_craetor_delete_schedule_unsuccessfully_message
                                                                    : R.string.fragment_housing_appointment_tab_user_delete_share_housing_creator_schedule_unsuccessfully_message,
                                                            Toast.LENGTH_LONG
                                                    );
                                                    RetrofitClient.showShareSpaceServerConnectionErrorDialog(mContext, t);
                                                }
                                            }
                                    );
                                }
                            })
                    .setNegativeButton(R.string.activity_housing_detail_delete_schedule_note_confirm_dialog_negative, null)
                    .show();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mItemView;
        public final SquareImageView mPlaceHolderProfileImage;
        public final SquareImageView mProfileImage;
        public final TextView mHouseTitle;
        public final TextView mShareHousingAppointmentDateTime;
        public final TextView mHousePrice;
        public final TextView mRequiredGender;
        public final TextView mRequiredNumOfPeople;
        public final TextView mHousingAppointmentNotes;
        public final View mHousingAppointmentAcceptButtonLeftVerticalBorderLine;
        public final ImageView mHousingAppointmentAcceptButton;
        public final ImageView mHousingAppointmentEditButton;
        public final ImageView mHousingAppointmentRejectButton;
        public final ImageView mSendReceiveHousingAppointmentSign;
        public ShareHousingAppointment mItem;

        public ViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mPlaceHolderProfileImage = (SquareImageView) itemView.findViewById(R.id.fragment_share_housing_appointment_item_house_profile_image_placeholder);
            mProfileImage = (SquareImageView) itemView.findViewById(R.id.fragment_share_housing_appointment_item_house_profile_image);
            mHouseTitle = (TextView) itemView.findViewById(R.id.fragment_share_housing_appointment_item_house_title);
            mShareHousingAppointmentDateTime = (TextView) itemView.findViewById(R.id.fragment_share_housing_appointment_item_share_housing_appointment_date_time);
            mHousePrice = (TextView) itemView.findViewById(R.id.fragment_share_housing_appointment_item_price);
            mRequiredGender = (TextView) itemView.findViewById(R.id.fragment_share_housing_appointment_item_gender);
            mRequiredNumOfPeople = (TextView) itemView.findViewById(R.id.fragment_share_housing_appointment_item_num_people);
            mHousingAppointmentNotes = (TextView) itemView.findViewById(R.id.fragment_share_housing_appointment_item_appointment_notes);
            mHousingAppointmentAcceptButtonLeftVerticalBorderLine = itemView.findViewById(R.id.fragment_share_housing_appointment_item_accept_button_left_vertical_border_line);
            mHousingAppointmentAcceptButton = (ImageView) itemView.findViewById(R.id.fragment_share_housing_appointment_item_accept_button);
            mHousingAppointmentEditButton = (ImageView) itemView.findViewById(R.id.fragment_share_housing_appointment_item_edit_button);
            mHousingAppointmentRejectButton = (ImageView) itemView.findViewById(R.id.fragment_share_housing_appointment_item_reject_button);
            mSendReceiveHousingAppointmentSign = (ImageView) itemView.findViewById(R.id.fragment_share_housing_appointment_item_send_receive_sign);
        }
    }
}
