package com.lmtri.sharespace.adapter.interestedtab;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.lmtri.sharespace.R;
import com.lmtri.sharespace.activity.profiletab.historynote.HistoryNoteActivity;
import com.lmtri.sharespace.activity.profiletab.historyphoto.HistoryPhotoActivity;
import com.lmtri.sharespace.activity.profiletab.historysave.HistorySaveActivity;
import com.lmtri.sharespace.api.model.HistoryShareHousingNote;
import com.lmtri.sharespace.api.model.HistoryShareHousingPhoto;
import com.lmtri.sharespace.api.model.SavedShareHousing;
import com.lmtri.sharespace.api.model.ShareHousing;
import com.lmtri.sharespace.customview.SquareImageView;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.HousePriceHelper;
import com.lmtri.sharespace.listener.OnShareHousingListInteractionListener;

import java.util.List;

/**
 * Created by lmtri on 12/3/2017.
 */

public class InterestedShareHousingRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String TAG = InterestedShareHousingRecyclerViewAdapter.class.getSimpleName();

    private final int VIEW_TYPE_SAVED_SHARE_HOUSINGS_SECTION = 0;
    private final int VIEW_TYPE_SAVED_SHARE_HOUSINGS = 1;
    private final int VIEW_TYPE_SHARE_HOUSING_NOTES_SECTION = 2;
    private final int VIEW_TYPE_SHARE_HOUSING_NOTES = 3;
    private final int VIEW_TYPE_SHARE_HOUSING_PHOTOS_SECTION = 4;
    private final int VIEW_TYPE_SHARE_HOUSING_PHOTOS = 5;

    private final Context mContext;
    private final List<SavedShareHousing> mSavedShareHousings;
    private final List<HistoryShareHousingNote> mHistoryShareHousingNotes;
    private final List<HistoryShareHousingPhoto> mHistoryShareHousingPhotos;
    private final OnShareHousingListInteractionListener mListener;

    public InterestedShareHousingRecyclerViewAdapter(Context context,
                                                     List<SavedShareHousing> savedShareHousings,
                                                     List<HistoryShareHousingNote> historyShareHousingNotes,
                                                     List<HistoryShareHousingPhoto> historyShareHousingPhotos,
                                                     OnShareHousingListInteractionListener listener) {
        this.mContext = context;
        this.mSavedShareHousings = savedShareHousings;
        this.mHistoryShareHousingNotes = historyShareHousingNotes;
        this.mHistoryShareHousingPhotos = historyShareHousingPhotos;
        this.mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SAVED_SHARE_HOUSINGS_SECTION) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_interested_share_housing_tab_saved_share_housing_section_item, parent, false);
            return new SavedShareHousingSectionViewHolder(itemView);
        } else if (viewType == VIEW_TYPE_SAVED_SHARE_HOUSINGS) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_share_housing_item, parent, false);
            return new SavedShareHousingViewHolder(itemView);
        } else if (viewType == VIEW_TYPE_SHARE_HOUSING_NOTES_SECTION) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_interested_share_housing_tab_share_housing_note_section_item, parent, false);
            return new HistoryShareHousingNoteSectionViewHolder(itemView);
        } else if (viewType == VIEW_TYPE_SHARE_HOUSING_NOTES) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_share_housing_item, parent, false);
            return new HistoryShareHousingNoteViewHolder(itemView);
        } else if (viewType == VIEW_TYPE_SHARE_HOUSING_PHOTOS_SECTION) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_interested_share_housing_tab_share_housing_photo_section_item, parent, false);
            return new HistoryShareHousingPhotoSectionViewHolder(itemView);
        } else if (viewType == VIEW_TYPE_SHARE_HOUSING_PHOTOS) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_share_housing_item, parent, false);
            return new HistoryShareHousingPhotoViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SavedShareHousingViewHolder) {
            final SavedShareHousingViewHolder savedShareHousingViewHolder = (SavedShareHousingViewHolder) holder;
            savedShareHousingViewHolder.mItem = mSavedShareHousings.get(position - 1).getSavedShareHousing();  // position - 1: minus Saved Share Housing Section Item.
            if (!mSavedShareHousings.get(position - 1).getSavedShareHousing().getHousing().getPhotoURLs().isEmpty()) {
                String profileImageUrl = mSavedShareHousings.get(position - 1).getSavedShareHousing().getHousing().getPhotoURLs().get(0);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Glide.with(mContext)
                            .load(R.drawable.ic_home)
                            .asBitmap()
                            .fitCenter()
                            .into(new BitmapImageViewTarget(savedShareHousingViewHolder.mPlaceHolderProfileImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    savedShareHousingViewHolder.mPlaceHolderProfileImage.setImageDrawable(new BitmapDrawable(mContext.getResources(), resource));
                                }
                            });
                    Glide.with(mContext)
                            .load(profileImageUrl)
                            .asBitmap()
                            .into(new BitmapImageViewTarget(savedShareHousingViewHolder.mProfileImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    savedShareHousingViewHolder.mProfileImage.setImageDrawable(new BitmapDrawable(mContext.getResources(), resource));
                                }
                            });
                } else {
                    Glide.with(mContext)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.ic_home)
                            .crossFade()
                            .into(savedShareHousingViewHolder.mProfileImage);
                }
            } else {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Glide.with(mContext)
                            .load(R.drawable.ic_home)
                            .asBitmap()
                            .fitCenter()
                            .into(new BitmapImageViewTarget(savedShareHousingViewHolder.mPlaceHolderProfileImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    savedShareHousingViewHolder.mPlaceHolderProfileImage.setImageDrawable(new BitmapDrawable(mContext.getResources(), resource));
                                }
                            });
                } else {
                    Glide.with(mContext)
                            .load(R.drawable.ic_home)
                            .crossFade()
                            .into(savedShareHousingViewHolder.mProfileImage);
                }
            }
            if (!TextUtils.isEmpty(mSavedShareHousings.get(position - 1).getSavedShareHousing().getHousing().getTitle())) {
                savedShareHousingViewHolder.mHouseTitle.setText(mSavedShareHousings.get(position - 1).getSavedShareHousing().getHousing().getTitle());
            }
            if (!TextUtils.isEmpty(mSavedShareHousings.get(position - 1).getSavedShareHousing().getHousing().getHouseType())) {
                savedShareHousingViewHolder.mHouseType.setText(mSavedShareHousings.get(position - 1).getSavedShareHousing().getHousing().getHouseType());
            }
            if (!TextUtils.isEmpty(mSavedShareHousings.get(position - 1).getSavedShareHousing().getHousing().getAddressDistrict())
                    && !TextUtils.isEmpty(mSavedShareHousings.get(position - 1).getSavedShareHousing().getHousing().getAddressCity())) {
                savedShareHousingViewHolder.mHouseLocation.setText(
//                        mContext.getString(R.string.housing_recycler_view_adapter_house_location) +
                        mSavedShareHousings.get(position - 1).getSavedShareHousing().getHousing().getAddressDistrict() + ", "
                        + mSavedShareHousings.get(position - 1).getSavedShareHousing().getHousing().getAddressCity()
                );
            }

//        holder.mNumSaved.setText(String.valueOf(mInterestedShareHousings.get(position - 1).getHistoryShareHousingNote().getNumOfSaved()) + mContext.getString(R.string.housing_recycler_view_adapter_saved));

            Pair<String, String> pair = HousePriceHelper.parseForShareHousing(mSavedShareHousings.get(position - 1).getSavedShareHousing().getPricePerMonthOfOne(), mContext);
            if (pair.first == null) {
                savedShareHousingViewHolder.mHousePrice.setText(pair.second);
            } else {
                savedShareHousingViewHolder.mHousePrice.setText(pair.first + " " + pair.second);
            }

            if (mSavedShareHousings.get(position - 1).getSavedShareHousing().getRequiredNumOfPeople() <= 0) {
                savedShareHousingViewHolder.mRequiredNumOfPeople.setText(1 + mContext.getString(R.string.activity_housing_detail_people));
            } else {
                savedShareHousingViewHolder.mRequiredNumOfPeople.setText(mSavedShareHousings.get(position - 1).getSavedShareHousing().getRequiredNumOfPeople() + mContext.getString(R.string.activity_housing_detail_people));
            }

            if (!TextUtils.isEmpty(mSavedShareHousings.get(position - 1).getSavedShareHousing().getRequiredGender())) {
                savedShareHousingViewHolder.mRequiredGender.setText(mSavedShareHousings.get(position - 1).getSavedShareHousing().getRequiredGender());
            } else {
                savedShareHousingViewHolder.mRequiredGender.setText(mContext.getString(R.string.hint_signup_gender_female));
            }

            if (!TextUtils.isEmpty(mSavedShareHousings.get(position - 1).getSavedShareHousing().getRequiredWorkType())) {
                savedShareHousingViewHolder.mRequiredWorkType.setText(mSavedShareHousings.get(position - 1).getSavedShareHousing().getRequiredWorkType());
            } else {
                savedShareHousingViewHolder.mRequiredWorkType.setText(mContext.getString(R.string.share_housing_recycler_view_adapter_work_type_student));
            }

//        holder.mProfileImage.setImageBitmap(
//                BitmapLoader.decodeSampledBitmapFromFile(localFile, 56, 56));

            savedShareHousingViewHolder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onShareHousingListInteraction(savedShareHousingViewHolder.mItem);
                    }
                }
            });
        } else if (holder instanceof HistoryShareHousingNoteViewHolder) {
            final HistoryShareHousingNoteViewHolder historyShareHousingNoteViewHolder = (HistoryShareHousingNoteViewHolder) holder;
            // position - 1 - mSavedShareHousings.size() - 1:
            // minus History Share Housing Note Section Item
            // AND Num Of Saved Share Housings
            // AND Saved Share Housing Section Item.
            int shiftedPosition = 0;
            if (mSavedShareHousings.isEmpty()) {
                shiftedPosition = position - 1;
            } else {
                shiftedPosition = position - 1 - mSavedShareHousings.size() - 1;
            }
            historyShareHousingNoteViewHolder.mItem = mHistoryShareHousingNotes.get(shiftedPosition).getShareHousing();
            if (!mHistoryShareHousingNotes.get(shiftedPosition).getShareHousing().getHousing().getPhotoURLs().isEmpty()) {
                String profileImageUrl = mHistoryShareHousingNotes.get(shiftedPosition).getShareHousing().getHousing().getPhotoURLs().get(0);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Glide.with(mContext)
                            .load(R.drawable.ic_home)
                            .asBitmap()
                            .fitCenter()
                            .into(new BitmapImageViewTarget(historyShareHousingNoteViewHolder.mPlaceHolderProfileImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    historyShareHousingNoteViewHolder.mPlaceHolderProfileImage.setImageDrawable(new BitmapDrawable(mContext.getResources(), resource));
                                }
                            });
                    Glide.with(mContext)
                            .load(profileImageUrl)
                            .asBitmap()
                            .into(new BitmapImageViewTarget(historyShareHousingNoteViewHolder.mProfileImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    historyShareHousingNoteViewHolder.mProfileImage.setImageDrawable(new BitmapDrawable(mContext.getResources(), resource));
                                }
                            });
                } else {
                    Glide.with(mContext)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.ic_home)
                            .crossFade()
                            .into(historyShareHousingNoteViewHolder.mProfileImage);
                }
            } else {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Glide.with(mContext)
                            .load(R.drawable.ic_home)
                            .asBitmap()
                            .fitCenter()
                            .into(new BitmapImageViewTarget(historyShareHousingNoteViewHolder.mPlaceHolderProfileImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    historyShareHousingNoteViewHolder.mPlaceHolderProfileImage.setImageDrawable(new BitmapDrawable(mContext.getResources(), resource));
                                }
                            });
                } else {
                    Glide.with(mContext)
                            .load(R.drawable.ic_home)
                            .crossFade()
                            .into(historyShareHousingNoteViewHolder.mProfileImage);
                }
            }
            if (!TextUtils.isEmpty(mHistoryShareHousingNotes.get(shiftedPosition).getShareHousing().getHousing().getTitle())) {
                historyShareHousingNoteViewHolder.mHouseTitle.setText(mHistoryShareHousingNotes.get(shiftedPosition).getShareHousing().getHousing().getTitle());
            }
            if (!TextUtils.isEmpty(mHistoryShareHousingNotes.get(shiftedPosition).getShareHousing().getHousing().getHouseType())) {
                historyShareHousingNoteViewHolder.mHouseType.setText(mHistoryShareHousingNotes.get(shiftedPosition).getShareHousing().getHousing().getHouseType());
            }
            if (!TextUtils.isEmpty(mHistoryShareHousingNotes.get(shiftedPosition).getShareHousing().getHousing().getAddressDistrict())
                    && !TextUtils.isEmpty(mHistoryShareHousingNotes.get(shiftedPosition).getShareHousing().getHousing().getAddressCity())) {
                historyShareHousingNoteViewHolder.mHouseLocation.setText(
//                        mContext.getString(R.string.housing_recycler_view_adapter_house_location) +
                        mHistoryShareHousingNotes.get(shiftedPosition).getShareHousing().getHousing().getAddressDistrict() + ", "
                        + mHistoryShareHousingNotes.get(shiftedPosition).getShareHousing().getHousing().getAddressCity()
                );
            }

//        holder.mNumSaved.setText(String.valueOf(mInterestedShareHousings.get(shiftedPosition).getHistoryShareHousingNote().getNumOfSaved()) + mContext.getString(R.string.housing_recycler_view_adapter_saved));

            Pair<String, String> pair = HousePriceHelper.parseForShareHousing(mHistoryShareHousingNotes.get(shiftedPosition).getShareHousing().getPricePerMonthOfOne(), mContext);
            if (pair.first == null) {
                historyShareHousingNoteViewHolder.mHousePrice.setText(pair.second);
            } else {
                historyShareHousingNoteViewHolder.mHousePrice.setText(pair.first + " " + pair.second);
            }

            if (mHistoryShareHousingNotes.get(shiftedPosition).getShareHousing().getRequiredNumOfPeople() <= 0) {
                historyShareHousingNoteViewHolder.mRequiredNumOfPeople.setText(1 + mContext.getString(R.string.activity_housing_detail_people));
            } else {
                historyShareHousingNoteViewHolder.mRequiredNumOfPeople.setText(mHistoryShareHousingNotes.get(shiftedPosition).getShareHousing().getRequiredNumOfPeople() + mContext.getString(R.string.activity_housing_detail_people));
            }

            if (!TextUtils.isEmpty(mHistoryShareHousingNotes.get(shiftedPosition).getShareHousing().getRequiredGender())) {
                historyShareHousingNoteViewHolder.mRequiredGender.setText(mHistoryShareHousingNotes.get(shiftedPosition).getShareHousing().getRequiredGender());
            } else {
                historyShareHousingNoteViewHolder.mRequiredGender.setText(mContext.getString(R.string.hint_signup_gender_female));
            }

            if (!TextUtils.isEmpty(mHistoryShareHousingNotes.get(shiftedPosition).getShareHousing().getRequiredWorkType())) {
                historyShareHousingNoteViewHolder.mRequiredWorkType.setText(mHistoryShareHousingNotes.get(shiftedPosition).getShareHousing().getRequiredWorkType());
            } else {
                historyShareHousingNoteViewHolder.mRequiredWorkType.setText(mContext.getString(R.string.share_housing_recycler_view_adapter_work_type_student));
            }

//        holder.mProfileImage.setImageBitmap(
//                BitmapLoader.decodeSampledBitmapFromFile(localFile, 56, 56));

            historyShareHousingNoteViewHolder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onShareHousingListInteraction(historyShareHousingNoteViewHolder.mItem);
                    }
                }
            });
        } else if (holder instanceof HistoryShareHousingPhotoViewHolder) {
            final HistoryShareHousingPhotoViewHolder historyShareHousingPhotoViewHolder = (HistoryShareHousingPhotoViewHolder) holder;
            // position - 1 - mHistoryShareHousingNotes.size() - 1 - mSavedShareHousings.size() - 1:
            // minus History Share Housing Photo Section Item
            // AND Num Of History Share Housing Notes
            // AND History Share Housing Note Section Item
            // AND Num Of Saved Share Housings
            // AND Saved Share Housing Section Item.
            int shiftedPosition = 0;
            if (mSavedShareHousings.isEmpty()) {
                if (mHistoryShareHousingNotes.isEmpty()) {
                    shiftedPosition = position - 1;
                } else {
                    shiftedPosition = position - 1 - mHistoryShareHousingNotes.size() - 1;
                }
            } else {
                if (mHistoryShareHousingNotes.isEmpty()) {
                    shiftedPosition = position - 1 - mSavedShareHousings.size() - 1;
                } else {
                    shiftedPosition = position - 1 - mHistoryShareHousingNotes.size() - 1 - mSavedShareHousings.size() - 1;
                }
            }
            historyShareHousingPhotoViewHolder.mItem = mHistoryShareHousingPhotos.get(shiftedPosition).getShareHousing();
            if (!mHistoryShareHousingPhotos.get(shiftedPosition).getShareHousing().getHousing().getPhotoURLs().isEmpty()) {
                String profileImageUrl = mHistoryShareHousingPhotos.get(shiftedPosition).getShareHousing().getHousing().getPhotoURLs().get(0);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Glide.with(mContext)
                            .load(R.drawable.ic_home)
                            .asBitmap()
                            .fitCenter()
                            .into(new BitmapImageViewTarget(historyShareHousingPhotoViewHolder.mPlaceHolderProfileImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    historyShareHousingPhotoViewHolder.mPlaceHolderProfileImage.setImageDrawable(new BitmapDrawable(mContext.getResources(), resource));
                                }
                            });
                    Glide.with(mContext)
                            .load(profileImageUrl)
                            .asBitmap()
                            .into(new BitmapImageViewTarget(historyShareHousingPhotoViewHolder.mProfileImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    historyShareHousingPhotoViewHolder.mProfileImage.setImageDrawable(new BitmapDrawable(mContext.getResources(), resource));
                                }
                            });
                } else {
                    Glide.with(mContext)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.ic_home)
                            .crossFade()
                            .into(historyShareHousingPhotoViewHolder.mProfileImage);
                }
            } else {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Glide.with(mContext)
                            .load(R.drawable.ic_home)
                            .asBitmap()
                            .fitCenter()
                            .into(new BitmapImageViewTarget(historyShareHousingPhotoViewHolder.mPlaceHolderProfileImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    historyShareHousingPhotoViewHolder.mPlaceHolderProfileImage.setImageDrawable(new BitmapDrawable(mContext.getResources(), resource));
                                }
                            });
                } else {
                    Glide.with(mContext)
                            .load(R.drawable.ic_home)
                            .crossFade()
                            .into(historyShareHousingPhotoViewHolder.mProfileImage);
                }
            }
            if (!TextUtils.isEmpty(mHistoryShareHousingPhotos.get(shiftedPosition).getShareHousing().getHousing().getTitle())) {
                historyShareHousingPhotoViewHolder.mHouseTitle.setText(mHistoryShareHousingPhotos.get(shiftedPosition).getShareHousing().getHousing().getTitle());
            }
            if (!TextUtils.isEmpty(mHistoryShareHousingPhotos.get(shiftedPosition).getShareHousing().getHousing().getHouseType())) {
                historyShareHousingPhotoViewHolder.mHouseType.setText(mHistoryShareHousingPhotos.get(shiftedPosition).getShareHousing().getHousing().getHouseType());
            }
            if (!TextUtils.isEmpty(mHistoryShareHousingPhotos.get(shiftedPosition).getShareHousing().getHousing().getAddressDistrict())
                    && !TextUtils.isEmpty(mHistoryShareHousingPhotos.get(shiftedPosition).getShareHousing().getHousing().getAddressCity())) {
                historyShareHousingPhotoViewHolder.mHouseLocation.setText(
//                        mContext.getString(R.string.housing_recycler_view_adapter_house_location) +
                        mHistoryShareHousingPhotos.get(shiftedPosition).getShareHousing().getHousing().getAddressDistrict() + ", "
                        + mHistoryShareHousingPhotos.get(shiftedPosition).getShareHousing().getHousing().getAddressCity()
                );
            }

//        holder.mNumSaved.setText(String.valueOf(mInterestedShareHousings.get(shiftedPosition).getHistoryShareHousingNote().getNumOfSaved()) + mContext.getString(R.string.housing_recycler_view_adapter_saved));

            Pair<String, String> pair = HousePriceHelper.parseForShareHousing(mHistoryShareHousingPhotos.get(shiftedPosition).getShareHousing().getPricePerMonthOfOne(), mContext);
            if (pair.first == null) {
                historyShareHousingPhotoViewHolder.mHousePrice.setText(pair.second);
            } else {
                historyShareHousingPhotoViewHolder.mHousePrice.setText(pair.first + " " + pair.second);
            }

            if (mHistoryShareHousingPhotos.get(shiftedPosition).getShareHousing().getRequiredNumOfPeople() <= 0) {
                historyShareHousingPhotoViewHolder.mRequiredNumOfPeople.setText(1 + mContext.getString(R.string.activity_housing_detail_people));
            } else {
                historyShareHousingPhotoViewHolder.mRequiredNumOfPeople.setText(mHistoryShareHousingPhotos.get(shiftedPosition).getShareHousing().getRequiredNumOfPeople() + mContext.getString(R.string.activity_housing_detail_people));
            }

            if (!TextUtils.isEmpty(mHistoryShareHousingPhotos.get(shiftedPosition).getShareHousing().getRequiredGender())) {
                historyShareHousingPhotoViewHolder.mRequiredGender.setText(mHistoryShareHousingPhotos.get(shiftedPosition).getShareHousing().getRequiredGender());
            } else {
                historyShareHousingPhotoViewHolder.mRequiredGender.setText(mContext.getString(R.string.hint_signup_gender_female));
            }

            if (!TextUtils.isEmpty(mHistoryShareHousingPhotos.get(shiftedPosition).getShareHousing().getRequiredWorkType())) {
                historyShareHousingPhotoViewHolder.mRequiredWorkType.setText(mHistoryShareHousingPhotos.get(shiftedPosition).getShareHousing().getRequiredWorkType());
            } else {
                historyShareHousingPhotoViewHolder.mRequiredWorkType.setText(mContext.getString(R.string.share_housing_recycler_view_adapter_work_type_student));
            }

//        holder.mProfileImage.setImageBitmap(
//                BitmapLoader.decodeSampledBitmapFromFile(localFile, 56, 56));

            historyShareHousingPhotoViewHolder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onShareHousingListInteraction(historyShareHousingPhotoViewHolder.mItem);
                    }
                }
            });
        } else if (holder instanceof SavedShareHousingSectionViewHolder) {
            SavedShareHousingSectionViewHolder savedShareHousingSectionViewHolder = (SavedShareHousingSectionViewHolder) holder;
            savedShareHousingSectionViewHolder.mShowMoreButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, HistorySaveActivity.class);
                            intent.putExtra(Constants.SELECTED_TAB_INDEX_INTERESTED_SHARE_HOUSING, 1);
                            mContext.startActivity(intent);
                        }
                    }
            );
        } else if (holder instanceof HistoryShareHousingNoteSectionViewHolder) {
            HistoryShareHousingNoteSectionViewHolder historyShareHousingNoteSectionViewHolder = (HistoryShareHousingNoteSectionViewHolder) holder;
            historyShareHousingNoteSectionViewHolder.mShowMoreButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, HistoryNoteActivity.class);
                            intent.putExtra(Constants.SELECTED_TAB_INDEX_INTERESTED_SHARE_HOUSING, 1);
                            mContext.startActivity(intent);
                        }
                    }
            );
        } else if (holder instanceof HistoryShareHousingPhotoSectionViewHolder) {
            HistoryShareHousingPhotoSectionViewHolder historyShareHousingPhotoSectionViewHolder = (HistoryShareHousingPhotoSectionViewHolder) holder;
            historyShareHousingPhotoSectionViewHolder.mShowMoreButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, HistoryPhotoActivity.class);
                            intent.putExtra(Constants.SELECTED_TAB_INDEX_INTERESTED_SHARE_HOUSING, 1);
                            mContext.startActivity(intent);
                        }
                    }
            );
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mSavedShareHousings.isEmpty()) {
            if (mHistoryShareHousingNotes.isEmpty()) {
                if (mHistoryShareHousingPhotos.isEmpty()) {  // All EMPTY.
                    return -1;
                } else {    // Only Exist Photo.
                    if (position == 0) {
                        return VIEW_TYPE_SHARE_HOUSING_PHOTOS_SECTION;
                    } else {
                        return VIEW_TYPE_SHARE_HOUSING_PHOTOS;
                    }
                }
            } else {    // mHistoryShareHousingPhotos NOT EMPTY.
                if (mHistoryShareHousingPhotos.isEmpty()) {  // Only Exist Note.
                    if (position == 0) {
                        return VIEW_TYPE_SHARE_HOUSING_NOTES_SECTION;
                    } else {
                        return VIEW_TYPE_SHARE_HOUSING_NOTES;
                    }
                } else {    // Exist Note AND Photo.
                    if (position == 0) {
                        return VIEW_TYPE_SHARE_HOUSING_NOTES_SECTION;
                    } else if (position > 0 && position < mHistoryShareHousingNotes.size() + 1) {
                        return VIEW_TYPE_SHARE_HOUSING_NOTES;
                    } else if (position == mHistoryShareHousingNotes.size() + 1) {
                        return VIEW_TYPE_SHARE_HOUSING_PHOTOS_SECTION;
                    } else {    // position > mHistoryShareHousingNotes.size() + 1
                        return VIEW_TYPE_SHARE_HOUSING_PHOTOS;
                    }
                }
            }
        } else {    // mSavedShareHousings NOT EMPTY.
            if (mHistoryShareHousingNotes.isEmpty()) {
                if (mHistoryShareHousingPhotos.isEmpty()) {  // Only Exist Saved.
                    if (position == 0) {
                        return VIEW_TYPE_SAVED_SHARE_HOUSINGS_SECTION;
                    } else {
                        return VIEW_TYPE_SAVED_SHARE_HOUSINGS;
                    }
                } else {    // Exist Saved AND Photo.
                    if (position == 0) {
                        return VIEW_TYPE_SAVED_SHARE_HOUSINGS_SECTION;
                    } else if (position > 0 && position < mSavedShareHousings.size() + 1) {
                        return VIEW_TYPE_SAVED_SHARE_HOUSINGS;
                    } else if (position == mSavedShareHousings.size() + 1) {
                        return VIEW_TYPE_SHARE_HOUSING_PHOTOS_SECTION;
                    } else {    // position > mSavedShareHousings.size() + 1
                        return VIEW_TYPE_SHARE_HOUSING_PHOTOS;
                    }
                }
            } else {    // mHistoryShareHousingNotes NOT EMPTY.
                if (mHistoryShareHousingPhotos.isEmpty()) {  // Exist Saved AND Note.
                    if (position == 0) {
                        return VIEW_TYPE_SAVED_SHARE_HOUSINGS_SECTION;
                    } else if (position > 0 && position < mSavedShareHousings.size() + 1) {
                        return VIEW_TYPE_SAVED_SHARE_HOUSINGS;
                    } else if (position == mSavedShareHousings.size() + 1) {
                        return VIEW_TYPE_SHARE_HOUSING_NOTES_SECTION;
                    } else {    // position > mSavedShareHousings.size() + 1
                        return VIEW_TYPE_SHARE_HOUSING_NOTES;
                    }
                } else {    // Exist Saved AND Note AND Photo.
                    if (position == 0) {
                        return VIEW_TYPE_SAVED_SHARE_HOUSINGS_SECTION;
                    } else if (position > 0 && position < mSavedShareHousings.size() + 1) {
                        return VIEW_TYPE_SAVED_SHARE_HOUSINGS;
                    } else if (position == mSavedShareHousings.size() + 1) {
                        return VIEW_TYPE_SHARE_HOUSING_NOTES_SECTION;
                    } else if (position > mSavedShareHousings.size() + 1
                            && position < mSavedShareHousings.size() + 1 + mHistoryShareHousingNotes.size() + 1) {
                        return VIEW_TYPE_SHARE_HOUSING_NOTES;
                    } else if (position == mSavedShareHousings.size() + 1 + mHistoryShareHousingNotes.size() + 1) {
                        return VIEW_TYPE_SHARE_HOUSING_PHOTOS_SECTION;
                    } else {    // position > mSavedShareHousings.size() + 1 + mHistoryShareHousingNotes.size() + 1
                        return VIEW_TYPE_SHARE_HOUSING_PHOTOS;
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mSavedShareHousings.isEmpty()) {
            if (mHistoryShareHousingNotes.isEmpty()) {
                if (mHistoryShareHousingPhotos.isEmpty()) {  // All EMPTY.
                    return 0;
                } else {    // Only Exist Photo.
                    return mHistoryShareHousingPhotos.size() + 1;
                }
            } else {    // mHistoryShareHousingPhotos NOT EMPTY.
                if (mHistoryShareHousingPhotos.isEmpty()) {  // Only Exist Note.
                    return mHistoryShareHousingNotes.size() + 1;
                } else {    // Exist Note AND Photo.
                    return mHistoryShareHousingNotes.size() + 1
                            + mHistoryShareHousingPhotos.size() + 1;
                }
            }
        } else {    // mSavedShareHousings NOT EMPTY.
            if (mHistoryShareHousingNotes.isEmpty()) {
                if (mHistoryShareHousingPhotos.isEmpty()) {  // Only Exist Saved.
                    return mSavedShareHousings.size() + 1;
                } else {    // Exist Saved AND Photo.
                    return mSavedShareHousings.size() + 1
                            + mHistoryShareHousingPhotos.size() + 1;
                }
            } else {    // mHistoryShareHousingNotes NOT EMPTY.
                if (mHistoryShareHousingPhotos.isEmpty()) {  // Exist Saved AND Note.
                    return mSavedShareHousings.size() + 1
                            + mHistoryShareHousingNotes.size() + 1;
                } else {    // Exist Saved AND Note AND Photo.
                    return mSavedShareHousings.size() + 1
                            + mHistoryShareHousingNotes.size() + 1
                            + mHistoryShareHousingPhotos.size() + 1;
                }
            }
        }
    }

    public class SavedShareHousingSectionViewHolder extends RecyclerView.ViewHolder {
        public final LinearLayout mShowMoreButton;

        public SavedShareHousingSectionViewHolder(View itemView) {
            super(itemView);
            mShowMoreButton = (LinearLayout) itemView.findViewById(R.id.fragment_interested_share_housing_tab_saved_share_housing_section_item_show_more);
        }
    }

    public class SavedShareHousingViewHolder extends RecyclerView.ViewHolder {
        public final View mItemView;
        public final SquareImageView mPlaceHolderProfileImage;
        public final SquareImageView mProfileImage;
        public final TextView mHouseTitle;
        public final TextView mHouseType;
        public final TextView mHouseLocation;
//        public final TextView mNumSaved;
        public final TextView mHousePrice;
        public final TextView mRequiredNumOfPeople;
        public final TextView mRequiredGender;
        public final TextView mRequiredWorkType;
        public ShareHousing mItem;

        public SavedShareHousingViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mPlaceHolderProfileImage = (SquareImageView) itemView.findViewById(R.id.fragment_share_housing_item_house_profile_image_placeholder);
            mProfileImage = (SquareImageView) itemView.findViewById(R.id.fragment_share_housing_item_house_profile_image);
            mHouseTitle = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_house_title);
            mHouseType = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_house_type);
            mHouseLocation = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_house_location);
//            mNumSaved = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_num_saved);
            mHousePrice = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_price_per_month_of_one);
            mRequiredNumOfPeople = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_num_people);
            mRequiredGender = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_gender);
            mRequiredWorkType = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_work_type);
        }
    }

    public class HistoryShareHousingNoteSectionViewHolder extends RecyclerView.ViewHolder {
        public final LinearLayout mShowMoreButton;

        public HistoryShareHousingNoteSectionViewHolder(View itemView) {
            super(itemView);
            mShowMoreButton = (LinearLayout) itemView.findViewById(R.id.fragment_interested_share_housing_tab_share_housing_note_section_item_show_more);
        }
    }

    public class HistoryShareHousingNoteViewHolder extends RecyclerView.ViewHolder {
        public final View mItemView;
        public final SquareImageView mPlaceHolderProfileImage;
        public final SquareImageView mProfileImage;
        public final TextView mHouseTitle;
        public final TextView mHouseType;
        public final TextView mHouseLocation;
        //        public final TextView mNumSaved;
        public final TextView mHousePrice;
        public final TextView mRequiredNumOfPeople;
        public final TextView mRequiredGender;
        public final TextView mRequiredWorkType;
        public ShareHousing mItem;

        public HistoryShareHousingNoteViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mPlaceHolderProfileImage = (SquareImageView) itemView.findViewById(R.id.fragment_share_housing_item_house_profile_image_placeholder);
            mProfileImage = (SquareImageView) itemView.findViewById(R.id.fragment_share_housing_item_house_profile_image);
            mHouseTitle = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_house_title);
            mHouseType = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_house_type);
            mHouseLocation = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_house_location);
//            mNumSaved = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_num_saved);
            mHousePrice = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_price_per_month_of_one);
            mRequiredNumOfPeople = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_num_people);
            mRequiredGender = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_gender);
            mRequiredWorkType = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_work_type);
        }
    }

    public class HistoryShareHousingPhotoSectionViewHolder extends RecyclerView.ViewHolder {
        public final LinearLayout mShowMoreButton;

        public HistoryShareHousingPhotoSectionViewHolder(View itemView) {
            super(itemView);
            mShowMoreButton = (LinearLayout) itemView.findViewById(R.id.fragment_interested_share_housing_tab_share_housing_photo_section_item_show_more);
        }
    }

    public class HistoryShareHousingPhotoViewHolder extends RecyclerView.ViewHolder {
        public final View mItemView;
        public final SquareImageView mPlaceHolderProfileImage;
        public final SquareImageView mProfileImage;
        public final TextView mHouseTitle;
        public final TextView mHouseType;
        public final TextView mHouseLocation;
        //        public final TextView mNumSaved;
        public final TextView mHousePrice;
        public final TextView mRequiredNumOfPeople;
        public final TextView mRequiredGender;
        public final TextView mRequiredWorkType;
        public ShareHousing mItem;

        public HistoryShareHousingPhotoViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mPlaceHolderProfileImage = (SquareImageView) itemView.findViewById(R.id.fragment_share_housing_item_house_profile_image_placeholder);
            mProfileImage = (SquareImageView) itemView.findViewById(R.id.fragment_share_housing_item_house_profile_image);
            mHouseTitle = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_house_title);
            mHouseType = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_house_type);
            mHouseLocation = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_house_location);
//            mNumSaved = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_num_saved);
            mHousePrice = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_price_per_month_of_one);
            mRequiredNumOfPeople = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_num_people);
            mRequiredGender = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_gender);
            mRequiredWorkType = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_work_type);
        }
    }
}
