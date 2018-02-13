package com.lmtri.sharespace.adapter.interestedtab;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
import com.lmtri.sharespace.api.model.HistoryHousingNote;
import com.lmtri.sharespace.api.model.HistoryHousingPhoto;
import com.lmtri.sharespace.api.model.Housing;
import com.lmtri.sharespace.api.model.SavedHousing;
import com.lmtri.sharespace.customview.SquareImageView;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.HousePriceHelper;
import com.lmtri.sharespace.listener.OnHousingListInteractionListener;

import java.util.List;

/**
 * Created by lmtri on 12/3/2017.
 */

public class InterestedHousingRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String TAG = InterestedHousingRecyclerViewAdapter.class.getSimpleName();

    private final int VIEW_TYPE_SAVED_HOUSING_SECTION = 0;
    private final int VIEW_TYPE_SAVED_HOUSINGS = 1;
    private final int VIEW_TYPE_HOUSING_NOTE_SECTION = 2;
    private final int VIEW_TYPE_HOUSING_NOTES = 3;
    private final int VIEW_TYPE_HOUSING_PHOTO_SECTION = 4;
    private final int VIEW_TYPE_HOUSING_PHOTOS = 5;

    private final AppCompatActivity mActivity;
    private final List<SavedHousing> mSavedHousings;
    private final List<HistoryHousingNote> mHistoryHousingNotes;
    private final List<HistoryHousingPhoto> mHistoryHousingPhotos;
    private final OnHousingListInteractionListener mListener;

    public InterestedHousingRecyclerViewAdapter(AppCompatActivity activity,
                                                List<SavedHousing> savedHousings,
                                                List<HistoryHousingNote> historyHousingNotes,
                                                List<HistoryHousingPhoto> historyHousingPhotos,
                                                OnHousingListInteractionListener mListener) {
        this.mActivity = activity;
        this.mSavedHousings = savedHousings;
        this.mHistoryHousingNotes = historyHousingNotes;
        this.mHistoryHousingPhotos = historyHousingPhotos;
        this.mListener = mListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: viewType = " + viewType);
         if (viewType == VIEW_TYPE_SAVED_HOUSING_SECTION) {
             Log.d(TAG, "onCreateViewHolder: VIEW_TYPE_SAVED_HOUSING_SECTION");
             View itemView = LayoutInflater.from(parent.getContext())
                     .inflate(R.layout.fragment_interested_housing_tab_saved_housing_section_item, parent, false);
             return new SavedHousingSectionViewHolder(itemView);
        } else if (viewType == VIEW_TYPE_SAVED_HOUSINGS) {
             Log.d(TAG, "onCreateViewHolder: VIEW_TYPE_SAVED_HOUSINGS");
             View itemView = LayoutInflater.from(parent.getContext())
                     .inflate(R.layout.fragment_housing_item, parent, false);
             return new SavedHousingViewHolder(itemView);
        } else if (viewType == VIEW_TYPE_HOUSING_NOTE_SECTION) {
             Log.d(TAG, "onCreateViewHolder: VIEW_TYPE_HOUSING_NOTE_SECTION");
             View itemView = LayoutInflater.from(parent.getContext())
                     .inflate(R.layout.fragment_interested_housing_tab_housing_note_section_item, parent, false);
             return new HistoryHousingNoteSectionViewHolder(itemView);
        } else if (viewType == VIEW_TYPE_HOUSING_NOTES) {
             Log.d(TAG, "onCreateViewHolder: VIEW_TYPE_HOUSING_NOTES");
             View itemView = LayoutInflater.from(parent.getContext())
                     .inflate(R.layout.fragment_housing_item, parent, false);
             return new HistoryHousingNoteViewHolder(itemView);
        } else if (viewType == VIEW_TYPE_HOUSING_PHOTO_SECTION) {
             Log.d(TAG, "onCreateViewHolder: VIEW_TYPE_HOUSING_PHOTO_SECTION");
             View itemView = LayoutInflater.from(parent.getContext())
                     .inflate(R.layout.fragment_interested_housing_tab_housing_photo_section_item, parent, false);
             return new HistoryHousingPhotoSectionViewHolder(itemView);
        } else if (viewType == VIEW_TYPE_HOUSING_PHOTOS) {
             Log.d(TAG, "onCreateViewHolder: VIEW_TYPE_HOUSING_PHOTOS");
             View itemView = LayoutInflater.from(parent.getContext())
                     .inflate(R.layout.fragment_housing_item, parent, false);
             return new HistoryHousingPhotoViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: position = " + position);
        if (holder instanceof SavedHousingViewHolder) {
            Log.d(TAG, "onBindViewHolder: SavedHousingViewHolder");
            final SavedHousingViewHolder savedHousingViewHolder = (SavedHousingViewHolder) holder;
            savedHousingViewHolder.mItem = mSavedHousings.get(position - 1).getSavedHousing();  // position - 1: minus Saved Housing Section Item.
            if (!mSavedHousings.get(position - 1).getSavedHousing().getPhotoURLs().isEmpty()) {
                String profileImageUrl = mSavedHousings.get(position - 1).getSavedHousing().getPhotoURLs().get(0);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Glide.with(mActivity)
                            .load(R.drawable.ic_home)
                            .asBitmap()
                            .fitCenter()
                            .into(new BitmapImageViewTarget(savedHousingViewHolder.mPlaceHolderProfileImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    savedHousingViewHolder.mPlaceHolderProfileImage.setImageDrawable(new BitmapDrawable(mActivity.getResources(), resource));
                                }
                            });
                    Glide.with(mActivity)
                            .load(profileImageUrl)
                            .asBitmap()
                            .into(new BitmapImageViewTarget(savedHousingViewHolder.mProfileImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    savedHousingViewHolder.mProfileImage.setImageDrawable(new BitmapDrawable(mActivity.getResources(), resource));
                                }
                            });
                } else {
                    Glide.with(mActivity)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.ic_home)
                            .crossFade()
                            .into(savedHousingViewHolder.mProfileImage);
                }
            } else {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Glide.with(mActivity)
                            .load(R.drawable.ic_home)
                            .asBitmap()
                            .fitCenter()
                            .into(new BitmapImageViewTarget(savedHousingViewHolder.mPlaceHolderProfileImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    savedHousingViewHolder.mPlaceHolderProfileImage.setImageDrawable(new BitmapDrawable(mActivity.getResources(), resource));
                                }
                            });
                } else {
                    Glide.with(mActivity)
                            .load(R.drawable.ic_home)
                            .crossFade()
                            .into(savedHousingViewHolder.mProfileImage);
                }
            }
            if (!TextUtils.isEmpty(mSavedHousings.get(position - 1).getSavedHousing().getTitle())) {
                savedHousingViewHolder.mHouseTitle.setText(mSavedHousings.get(position - 1).getSavedHousing().getTitle());
            }
            if (!TextUtils.isEmpty(mSavedHousings.get(position - 1).getSavedHousing().getHouseType())) {
                savedHousingViewHolder.mHouseType.setText(mSavedHousings.get(position - 1).getSavedHousing().getHouseType());
            }
            if (!TextUtils.isEmpty(mSavedHousings.get(position - 1).getSavedHousing().getAddressDistrict())
                    && !TextUtils.isEmpty(mSavedHousings.get(position - 1).getSavedHousing().getAddressCity())) {
                savedHousingViewHolder.mHouseLocation.setText(
//                        mActivity.getString(R.string.housing_recycler_view_adapter_house_location) +
                        mSavedHousings.get(position - 1).getSavedHousing().getAddressDistrict() + ", "
                        + mSavedHousings.get(position - 1).getSavedHousing().getAddressCity()
                );
            }

//        holder.mNumSaved.setText(String.valueOf(mInterestedHousings.get(position - 1).getHistoryHousingPhoto().getNumOfSaved()) + mActivity.getString(R.string.housing_recycler_view_adapter_saved));

            Pair<String, String> pair = HousePriceHelper.parseForHousing(mSavedHousings.get(position - 1).getSavedHousing().getPrice(), mActivity);
            if (pair.first == null) {
                savedHousingViewHolder.mHousePrice.setText(pair.second);
            } else {
                savedHousingViewHolder.mHousePrice.setText(pair.first + " " + pair.second);
            }

//        holder.mProfileImage.setImageBitmap(
//                BitmapLoader.decodeSampledBitmapFromFile(localFile, 56, 56));

            savedHousingViewHolder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onHousingListInteraction(savedHousingViewHolder.mItem);
                    }
                }
            });
        } else if (holder instanceof HistoryHousingNoteViewHolder) {
            Log.d(TAG, "onBindViewHolder: HistoryHousingNoteViewHolder");
            final HistoryHousingNoteViewHolder historyHousingNoteViewHolder = (HistoryHousingNoteViewHolder) holder;
            // position - 1 - mSavedHousings.size() - 1:
            // minus History Housing Note Section Item
            // AND Num Of Saved Housings
            // AND Saved Housing Section Item.
            int shiftedPosition = 0;
            if (mSavedHousings.isEmpty()) {
                shiftedPosition = position - 1;
            } else {
                shiftedPosition = position - 1 - mSavedHousings.size() - 1;
            }
            historyHousingNoteViewHolder.mItem = mHistoryHousingNotes.get(shiftedPosition).getHousing();
            if (!mHistoryHousingNotes.get(shiftedPosition).getHousing().getPhotoURLs().isEmpty()) {
                String profileImageUrl = mHistoryHousingNotes.get(shiftedPosition).getHousing().getPhotoURLs().get(0);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Glide.with(mActivity)
                            .load(R.drawable.ic_home)
                            .asBitmap()
                            .fitCenter()
                            .into(new BitmapImageViewTarget(historyHousingNoteViewHolder.mPlaceHolderProfileImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    historyHousingNoteViewHolder.mPlaceHolderProfileImage.setImageDrawable(new BitmapDrawable(mActivity.getResources(), resource));
                                }
                            });
                    Glide.with(mActivity)
                            .load(profileImageUrl)
                            .asBitmap()
                            .into(new BitmapImageViewTarget(historyHousingNoteViewHolder.mProfileImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    historyHousingNoteViewHolder.mProfileImage.setImageDrawable(new BitmapDrawable(mActivity.getResources(), resource));
                                }
                            });
                } else {
                    Glide.with(mActivity)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.ic_home)
                            .crossFade()
                            .into(historyHousingNoteViewHolder.mProfileImage);
                }
            } else {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Glide.with(mActivity)
                            .load(R.drawable.ic_home)
                            .asBitmap()
                            .fitCenter()
                            .into(new BitmapImageViewTarget(historyHousingNoteViewHolder.mPlaceHolderProfileImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    historyHousingNoteViewHolder.mPlaceHolderProfileImage.setImageDrawable(new BitmapDrawable(mActivity.getResources(), resource));
                                }
                            });
                } else {
                    Glide.with(mActivity)
                            .load(R.drawable.ic_home)
                            .crossFade()
                            .into(historyHousingNoteViewHolder.mProfileImage);
                }
            }
            if (!TextUtils.isEmpty(mHistoryHousingNotes.get(shiftedPosition).getHousing().getTitle())) {
                historyHousingNoteViewHolder.mHouseTitle.setText(mHistoryHousingNotes.get(shiftedPosition).getHousing().getTitle());
            }
            if (!TextUtils.isEmpty(mHistoryHousingNotes.get(shiftedPosition).getHousing().getHouseType())) {
                historyHousingNoteViewHolder.mHouseType.setText(mHistoryHousingNotes.get(shiftedPosition).getHousing().getHouseType());
            }
            if (!TextUtils.isEmpty(mHistoryHousingNotes.get(shiftedPosition).getHousing().getAddressDistrict())
                    && !TextUtils.isEmpty(mHistoryHousingNotes.get(shiftedPosition).getHousing().getAddressCity())) {
                historyHousingNoteViewHolder.mHouseLocation.setText(
//                        mActivity.getString(R.string.housing_recycler_view_adapter_house_location) +
                        mHistoryHousingNotes.get(shiftedPosition).getHousing().getAddressDistrict() + ", "
                        + mHistoryHousingNotes.get(shiftedPosition).getHousing().getAddressCity()
                );
            }

//        holder.mNumSaved.setText(String.valueOf(mInterestedHousings.get(shiftedPosition).getHistoryHousingPhoto().getNumOfSaved()) + mActivity.getString(R.string.housing_recycler_view_adapter_saved));

            Pair<String, String> pair = HousePriceHelper.parseForHousing(mHistoryHousingNotes.get(shiftedPosition).getHousing().getPrice(), mActivity);
            if (pair.first == null) {
                historyHousingNoteViewHolder.mHousePrice.setText(pair.second);
            } else {
                historyHousingNoteViewHolder.mHousePrice.setText(pair.first + " " + pair.second);
            }

//        holder.mProfileImage.setImageBitmap(
//                BitmapLoader.decodeSampledBitmapFromFile(localFile, 56, 56));

            historyHousingNoteViewHolder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onHousingListInteraction(historyHousingNoteViewHolder.mItem);
                    }
                }
            });
        } else if (holder instanceof HistoryHousingPhotoViewHolder) {
            Log.d(TAG, "onBindViewHolder: HistoryHousingPhotoViewHolder");
            final HistoryHousingPhotoViewHolder historyHousingPhotoViewHolder = (HistoryHousingPhotoViewHolder) holder;
            // position - 1 - mHistoryHousingNotes.size() - 1 - mSavedHousings.size() - 1:
            // minus History Housing Photo Section Item
            // AND Num Of History Housing Notes
            // AND History Housing Note Section Item
            // AND Num Of Saved Housings
            // AND Saved Housing Section Item.
            int shiftedPosition = 0;
            if (mSavedHousings.isEmpty()) {
                if (mHistoryHousingNotes.isEmpty()) {
                    shiftedPosition = position - 1;
                } else {
                    shiftedPosition = position - 1 - mHistoryHousingNotes.size() - 1;
                }
            } else {
                if (mHistoryHousingNotes.isEmpty()) {
                    shiftedPosition = position - 1 - mSavedHousings.size() - 1;
                } else {
                    shiftedPosition = position - 1 - mHistoryHousingNotes.size() - 1 - mSavedHousings.size() - 1;
                }
            }
            historyHousingPhotoViewHolder.mItem = mHistoryHousingPhotos.get(shiftedPosition).getHousing();
            if (!mHistoryHousingPhotos.get(shiftedPosition).getHousing().getPhotoURLs().isEmpty()) {
                String profileImageUrl = mHistoryHousingPhotos.get(shiftedPosition).getHousing().getPhotoURLs().get(0);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Glide.with(mActivity)
                            .load(R.drawable.ic_home)
                            .asBitmap()
                            .fitCenter()
                            .into(new BitmapImageViewTarget(historyHousingPhotoViewHolder.mPlaceHolderProfileImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    historyHousingPhotoViewHolder.mPlaceHolderProfileImage.setImageDrawable(new BitmapDrawable(mActivity.getResources(), resource));
                                }
                            });
                    Glide.with(mActivity)
                            .load(profileImageUrl)
                            .asBitmap()
                            .into(new BitmapImageViewTarget(historyHousingPhotoViewHolder.mProfileImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    historyHousingPhotoViewHolder.mProfileImage.setImageDrawable(new BitmapDrawable(mActivity.getResources(), resource));
                                }
                            });
                } else {
                    Glide.with(mActivity)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.ic_home)
                            .crossFade()
                            .into(historyHousingPhotoViewHolder.mProfileImage);
                }
            } else {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Glide.with(mActivity)
                            .load(R.drawable.ic_home)
                            .asBitmap()
                            .fitCenter()
                            .into(new BitmapImageViewTarget(historyHousingPhotoViewHolder.mPlaceHolderProfileImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    historyHousingPhotoViewHolder.mPlaceHolderProfileImage.setImageDrawable(new BitmapDrawable(mActivity.getResources(), resource));
                                }
                            });
                } else {
                    Glide.with(mActivity)
                            .load(R.drawable.ic_home)
                            .crossFade()
                            .into(historyHousingPhotoViewHolder.mProfileImage);
                }
            }
            if (!TextUtils.isEmpty(mHistoryHousingPhotos.get(shiftedPosition).getHousing().getTitle())) {
                historyHousingPhotoViewHolder.mHouseTitle.setText(mHistoryHousingPhotos.get(shiftedPosition).getHousing().getTitle());
            }
            if (!TextUtils.isEmpty(mHistoryHousingPhotos.get(shiftedPosition).getHousing().getHouseType())) {
                historyHousingPhotoViewHolder.mHouseType.setText(mHistoryHousingPhotos.get(shiftedPosition).getHousing().getHouseType());
            }
            if (!TextUtils.isEmpty(mHistoryHousingPhotos.get(shiftedPosition).getHousing().getAddressDistrict())
                    && !TextUtils.isEmpty(mHistoryHousingPhotos.get(shiftedPosition).getHousing().getAddressCity())) {
                historyHousingPhotoViewHolder.mHouseLocation.setText(
//                        mActivity.getString(R.string.housing_recycler_view_adapter_house_location) +
                        mHistoryHousingPhotos.get(shiftedPosition).getHousing().getAddressDistrict() + ", "
                        + mHistoryHousingPhotos.get(shiftedPosition).getHousing().getAddressCity()
                );
            }

//        holder.mNumSaved.setText(String.valueOf(mInterestedHousings.get(shiftedPosition).getHistoryHousingPhoto().getNumOfSaved()) + mActivity.getString(R.string.housing_recycler_view_adapter_saved));

            Pair<String, String> pair = HousePriceHelper.parseForHousing(mHistoryHousingPhotos.get(shiftedPosition).getHousing().getPrice(), mActivity);
            if (pair.first == null) {
                historyHousingPhotoViewHolder.mHousePrice.setText(pair.second);
            } else {
                historyHousingPhotoViewHolder.mHousePrice.setText(pair.first + " " + pair.second);
            }

//        holder.mProfileImage.setImageBitmap(
//                BitmapLoader.decodeSampledBitmapFromFile(localFile, 56, 56));

            historyHousingPhotoViewHolder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onHousingListInteraction(historyHousingPhotoViewHolder.mItem);
                    }
                }
            });
        } else if (holder instanceof SavedHousingSectionViewHolder) {
            Log.d(TAG, "onBindViewHolder: SavedHousingSectionViewHolder");
            SavedHousingSectionViewHolder savedHousingSectionViewHolder = (SavedHousingSectionViewHolder) holder;
            savedHousingSectionViewHolder.mShowMoreButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mActivity, HistorySaveActivity.class);
                            mActivity.startActivityForResult(intent, Constants.START_ACTIVITY_HISTORY_POST_SAVE_NOTE_PHOTO_REQUEST);
                        }
                    }
            );
        } else if (holder instanceof HistoryHousingNoteSectionViewHolder) {
            Log.d(TAG, "onBindViewHolder: HistoryHousingNoteSectionViewHolder");
            HistoryHousingNoteSectionViewHolder historyHousingNoteSectionViewHolder = (HistoryHousingNoteSectionViewHolder) holder;
            historyHousingNoteSectionViewHolder.mShowMoreButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mActivity, HistoryNoteActivity.class);
                            mActivity.startActivityForResult(intent, Constants.START_ACTIVITY_HISTORY_POST_SAVE_NOTE_PHOTO_REQUEST);
                        }
                    }
            );
        } else if (holder instanceof HistoryHousingPhotoSectionViewHolder) {
            Log.d(TAG, "onBindViewHolder: HistoryHousingPhotoSectionViewHolder");
            HistoryHousingPhotoSectionViewHolder historyHousingPhotoSectionViewHolder = (HistoryHousingPhotoSectionViewHolder) holder;
            historyHousingPhotoSectionViewHolder.mShowMoreButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mActivity, HistoryPhotoActivity.class);
                            mActivity.startActivityForResult(intent, Constants.START_ACTIVITY_HISTORY_POST_SAVE_NOTE_PHOTO_REQUEST);
                        }
                    }
            );
        }
    }

    @Override
    public int getItemViewType(int position) {
        Log.d(TAG, "getItemViewType: position = " + position);
        if (mSavedHousings.isEmpty()) {
            if (mHistoryHousingNotes.isEmpty()) {
                if (mHistoryHousingPhotos.isEmpty()) {  // All EMPTY.
                    Log.d(TAG, "getItemViewType: ALL EMPTY: return -1");
                    return -1;
                } else {    // Only Exist Photo.
                    if (position == 0) {
                        Log.d(TAG, "getItemViewType: Only Photo: VIEW_TYPE_HOUSING_PHOTO_SECTION");
                        return VIEW_TYPE_HOUSING_PHOTO_SECTION;
                    } else {
                        Log.d(TAG, "getItemViewType: Only Photo: VIEW_TYPE_HOUSING_PHOTOS");
                        return VIEW_TYPE_HOUSING_PHOTOS;
                    }
                }
            } else {    // mHistoryHousingPhotos NOT EMPTY.
                if (mHistoryHousingPhotos.isEmpty()) {  // Only Exist Note.
                    if (position == 0) {
                        Log.d(TAG, "getItemViewType: Only Note: VIEW_TYPE_HOUSING_NOTE_SECTION");
                        return VIEW_TYPE_HOUSING_NOTE_SECTION;
                    } else {
                        Log.d(TAG, "getItemViewType: Only Note: VIEW_TYPE_HOUSING_NOTES");
                        return VIEW_TYPE_HOUSING_NOTES;
                    }
                } else {    // Exist Note AND Photo.
                    if (position == 0) {
                        Log.d(TAG, "getItemViewType: Note AND Photo: VIEW_TYPE_HOUSING_NOTE_SECTION");
                        return VIEW_TYPE_HOUSING_NOTE_SECTION;
                    } else if (position > 0 && position < mHistoryHousingNotes.size() + 1) {
                        Log.d(TAG, "getItemViewType: Note AND Photo: VIEW_TYPE_HOUSING_NOTES");
                        return VIEW_TYPE_HOUSING_NOTES;
                    } else if (position == mHistoryHousingNotes.size() + 1) {
                        Log.d(TAG, "getItemViewType: Note AND Photo: VIEW_TYPE_HOUSING_PHOTO_SECTION");
                        return VIEW_TYPE_HOUSING_PHOTO_SECTION;
                    } else {    // position > mHistoryHousingNotes.size() + 1
                        Log.d(TAG, "getItemViewType: Note AND Photo: VIEW_TYPE_HOUSING_PHOTOS");
                        return VIEW_TYPE_HOUSING_PHOTOS;
                    }
                }
            }
        } else {    // mSavedHousings NOT EMPTY.
            if (mHistoryHousingNotes.isEmpty()) {
                if (mHistoryHousingPhotos.isEmpty()) {  // Only Exist Saved.
                    if (position == 0) {
                        Log.d(TAG, "getItemViewType: Only Saved: VIEW_TYPE_SAVED_HOUSING_SECTION");
                        return VIEW_TYPE_SAVED_HOUSING_SECTION;
                    } else {
                        Log.d(TAG, "getItemViewType: Only Saved: VIEW_TYPE_SAVED_HOUSINGS");
                        return VIEW_TYPE_SAVED_HOUSINGS;
                    }
                } else {    // Exist Saved AND Photo.
                    if (position == 0) {
                        Log.d(TAG, "getItemViewType: Saved AND Photo: VIEW_TYPE_SAVED_HOUSING_SECTION");
                        return VIEW_TYPE_SAVED_HOUSING_SECTION;
                    } else if (position > 0 && position < mSavedHousings.size() + 1) {
                        Log.d(TAG, "getItemViewType: Saved AND Photo: VIEW_TYPE_SAVED_HOUSINGS");
                        return VIEW_TYPE_SAVED_HOUSINGS;
                    } else if (position == mSavedHousings.size() + 1) {
                        Log.d(TAG, "getItemViewType: Saved AND Photo: VIEW_TYPE_HOUSING_PHOTO_SECTION");
                        return VIEW_TYPE_HOUSING_PHOTO_SECTION;
                    } else {    // position > mSavedHousings.size() + 1
                        Log.d(TAG, "getItemViewType: Saved AND Photo: VIEW_TYPE_HOUSING_PHOTOS");
                        return VIEW_TYPE_HOUSING_PHOTOS;
                    }
                }
            } else {    // mHistoryHousingNotes NOT EMPTY.
                if (mHistoryHousingPhotos.isEmpty()) {  // Exist Saved AND Note.
                    if (position == 0) {
                        Log.d(TAG, "getItemViewType: Saved AND Note: VIEW_TYPE_SAVED_HOUSING_SECTION");
                        return VIEW_TYPE_SAVED_HOUSING_SECTION;
                    } else if (position > 0 && position < mSavedHousings.size() + 1) {
                        Log.d(TAG, "getItemViewType: Saved AND Note: VIEW_TYPE_SAVED_HOUSINGS");
                        return VIEW_TYPE_SAVED_HOUSINGS;
                    } else if (position == mSavedHousings.size() + 1) {
                        Log.d(TAG, "getItemViewType: Saved AND Note: VIEW_TYPE_HOUSING_NOTE_SECTION");
                        return VIEW_TYPE_HOUSING_NOTE_SECTION;
                    } else {    // position > mSavedHousings.size() + 1
                        Log.d(TAG, "getItemViewType: Saved AND Note: VIEW_TYPE_HOUSING_NOTES");
                        return VIEW_TYPE_HOUSING_NOTES;
                    }
                } else {    // Exist Saved AND Note AND Photo.
                    if (position == 0) {
                        Log.d(TAG, "getItemViewType: Saved AND Note AND Photo: VIEW_TYPE_SAVED_HOUSING_SECTION");
                        return VIEW_TYPE_SAVED_HOUSING_SECTION;
                    } else if (position > 0 && position < mSavedHousings.size() + 1) {
                        Log.d(TAG, "getItemViewType: Saved AND Note AND Photo: VIEW_TYPE_SAVED_HOUSINGS");
                        return VIEW_TYPE_SAVED_HOUSINGS;
                    } else if (position == mSavedHousings.size() + 1) {
                        Log.d(TAG, "getItemViewType: Saved AND Note AND Photo: VIEW_TYPE_HOUSING_NOTE_SECTION");
                        return VIEW_TYPE_HOUSING_NOTE_SECTION;
                    } else if (position > mSavedHousings.size() + 1
                            && position < mSavedHousings.size() + 1 + mHistoryHousingNotes.size() + 1) {
                        Log.d(TAG, "getItemViewType: Saved AND Note AND Photo: VIEW_TYPE_HOUSING_NOTES");
                        return VIEW_TYPE_HOUSING_NOTES;
                    } else if (position == mSavedHousings.size() + 1 + mHistoryHousingNotes.size() + 1) {
                        Log.d(TAG, "getItemViewType: Saved AND Note AND Photo: VIEW_TYPE_HOUSING_PHOTO_SECTION");
                        return VIEW_TYPE_HOUSING_PHOTO_SECTION;
                    } else {    // position > mSavedHousings.size() + 1 + mHistoryHousingNotes.size() + 1
                        Log.d(TAG, "getItemViewType: Saved AND Note AND Photo: VIEW_TYPE_HOUSING_PHOTOS");
                        return VIEW_TYPE_HOUSING_PHOTOS;
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mSavedHousings.isEmpty()) {
            if (mHistoryHousingNotes.isEmpty()) {
                if (mHistoryHousingPhotos.isEmpty()) {  // All EMPTY.
                    Log.d(TAG, "getItemCount: ALL EMPTY: 0");
                    return 0;
                } else {    // Only Exist Photo.
                    Log.d(TAG, "getItemCount: Only Photo: " + (mHistoryHousingPhotos.size() + 1));
                    return mHistoryHousingPhotos.size() + 1;
                }
            } else {    // mHistoryHousingPhotos NOT EMPTY.
                if (mHistoryHousingPhotos.isEmpty()) {  // Only Exist Note.
                    Log.d(TAG, "getItemCount: Only Note: " + (mHistoryHousingNotes.size() + 1));
                    return mHistoryHousingNotes.size() + 1;
                } else {    // Exist Note AND Photo.
                    Log.d(TAG, "getItemCount: Note AND Photo: " + (mHistoryHousingNotes.size() + 1
                            + mHistoryHousingPhotos.size() + 1));
                    return mHistoryHousingNotes.size() + 1
                         + mHistoryHousingPhotos.size() + 1;
                }
            }
        } else {    // mSavedHousings NOT EMPTY.
            if (mHistoryHousingNotes.isEmpty()) {
                if (mHistoryHousingPhotos.isEmpty()) {  // Only Exist Saved.
                    Log.d(TAG, "getItemCount: Only Saved: " + (mSavedHousings.size() + 1));
                    return mSavedHousings.size() + 1;
                } else {    // Exist Saved AND Photo.
                    Log.d(TAG, "getItemCount: Saved AND Photo: " + (mSavedHousings.size() + 1
                            + mHistoryHousingPhotos.size() + 1));
                    return mSavedHousings.size() + 1
                         + mHistoryHousingPhotos.size() + 1;
                }
            } else {    // mHistoryHousingNotes NOT EMPTY.
                if (mHistoryHousingPhotos.isEmpty()) {  // Exist Saved AND Note.
                    Log.d(TAG, "getItemCount: Saved AND Note: " + (mSavedHousings.size() + 1
                            + mHistoryHousingNotes.size() + 1));
                    return mSavedHousings.size() + 1
                         + mHistoryHousingNotes.size() + 1;
                } else {    // Exist Saved AND Note AND Photo.
                    Log.d(TAG, "getItemCount: Saved AND Note AND Photo " + mSavedHousings.size() + 1
                            + mHistoryHousingNotes.size() + 1
                            + mHistoryHousingPhotos.size() + 1);
                    return mSavedHousings.size() + 1
                         + mHistoryHousingNotes.size() + 1
                         + mHistoryHousingPhotos.size() + 1;
                }
            }
        }
    }

    public class SavedHousingSectionViewHolder extends RecyclerView.ViewHolder {
        public final LinearLayout mShowMoreButton;

        public SavedHousingSectionViewHolder(View itemView) {
            super(itemView);
            mShowMoreButton = (LinearLayout) itemView.findViewById(R.id.fragment_interested_housing_tab_saved_housing_section_item_show_more);
        }
    }

    public class SavedHousingViewHolder extends RecyclerView.ViewHolder {
        public final View mItemView;
        public final SquareImageView mPlaceHolderProfileImage;
        public final SquareImageView mProfileImage;
        public final TextView mHouseTitle;
        public final TextView mHouseType;
        public final TextView mHouseLocation;
        //        public final TextView mNumSaved;
        public final TextView mHousePrice;
        public Housing mItem;

        public SavedHousingViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mPlaceHolderProfileImage = (SquareImageView) itemView.findViewById(R.id.fragment_housing_item_house_profile_image_placeholder);
            mProfileImage = (SquareImageView) itemView.findViewById(R.id.fragment_housing_item_house_profile_image);
            mHouseTitle = (TextView) itemView.findViewById(R.id.fragment_housing_item_house_title);
            mHouseType = (TextView) itemView.findViewById(R.id.fragment_housing_item_house_type);
            mHouseLocation = (TextView) itemView.findViewById(R.id.fragment_housing_item_house_location);
//            mNumSaved = (TextView) itemView.findViewById(R.id.fragment_housing_item_num_saved_text);
            mHousePrice = (TextView) itemView.findViewById(R.id.fragment_housing_item_price);
        }
    }

    public class HistoryHousingNoteSectionViewHolder extends RecyclerView.ViewHolder {
        public final LinearLayout mShowMoreButton;

        public HistoryHousingNoteSectionViewHolder(View itemView) {
            super(itemView);
            mShowMoreButton = (LinearLayout) itemView.findViewById(R.id.fragment_interested_housing_tab_housing_note_section_item_show_more);
        }
    }

    public class HistoryHousingNoteViewHolder extends RecyclerView.ViewHolder {
        public final View mItemView;
        public final SquareImageView mPlaceHolderProfileImage;
        public final SquareImageView mProfileImage;
        public final TextView mHouseTitle;
        public final TextView mHouseType;
        public final TextView mHouseLocation;
        //        public final TextView mNumSaved;
        public final TextView mHousePrice;
        public Housing mItem;

        public HistoryHousingNoteViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mPlaceHolderProfileImage = (SquareImageView) itemView.findViewById(R.id.fragment_housing_item_house_profile_image_placeholder);
            mProfileImage = (SquareImageView) itemView.findViewById(R.id.fragment_housing_item_house_profile_image);
            mHouseTitle = (TextView) itemView.findViewById(R.id.fragment_housing_item_house_title);
            mHouseType = (TextView) itemView.findViewById(R.id.fragment_housing_item_house_type);
            mHouseLocation = (TextView) itemView.findViewById(R.id.fragment_housing_item_house_location);
//            mNumSaved = (TextView) itemView.findViewById(R.id.fragment_housing_item_num_saved_text);
            mHousePrice = (TextView) itemView.findViewById(R.id.fragment_housing_item_price);
        }
    }

    public class HistoryHousingPhotoSectionViewHolder extends RecyclerView.ViewHolder {
        public final LinearLayout mShowMoreButton;

        public HistoryHousingPhotoSectionViewHolder(View itemView) {
            super(itemView);
            mShowMoreButton = (LinearLayout) itemView.findViewById(R.id.fragment_interested_housing_tab_housing_photo_section_item_show_more);
        }
    }

    public class HistoryHousingPhotoViewHolder extends RecyclerView.ViewHolder {
        public final View mItemView;
        public final SquareImageView mPlaceHolderProfileImage;
        public final SquareImageView mProfileImage;
        public final TextView mHouseTitle;
        public final TextView mHouseType;
        public final TextView mHouseLocation;
        //        public final TextView mNumSaved;
        public final TextView mHousePrice;
        public Housing mItem;

        public HistoryHousingPhotoViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mPlaceHolderProfileImage = (SquareImageView) itemView.findViewById(R.id.fragment_housing_item_house_profile_image_placeholder);
            mProfileImage = (SquareImageView) itemView.findViewById(R.id.fragment_housing_item_house_profile_image);
            mHouseTitle = (TextView) itemView.findViewById(R.id.fragment_housing_item_house_title);
            mHouseType = (TextView) itemView.findViewById(R.id.fragment_housing_item_house_type);
            mHouseLocation = (TextView) itemView.findViewById(R.id.fragment_housing_item_house_location);
//            mNumSaved = (TextView) itemView.findViewById(R.id.fragment_housing_item_num_saved_text);
            mHousePrice = (TextView) itemView.findViewById(R.id.fragment_housing_item_price);
        }
    }
}
