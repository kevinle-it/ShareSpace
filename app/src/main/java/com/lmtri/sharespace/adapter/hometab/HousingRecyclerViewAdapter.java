package com.lmtri.sharespace.adapter.hometab;

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
import com.lmtri.sharespace.activity.profiletab.historypost.HistoryPostActivity;
import com.lmtri.sharespace.api.model.Housing;
import com.lmtri.sharespace.customview.SquareImageView;
import com.lmtri.sharespace.helper.HousePriceHelper;
import com.lmtri.sharespace.listener.OnHousingListInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Housing} and makes a call to the
 * specified {@link OnHousingListInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class HousingRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String TAG = HousingRecyclerViewAdapter.class.getSimpleName();

    private final int VIEW_TYPE_HISTORY_HOUSING_SECTION = 0;
    private final int VIEW_TYPE_POSTED_HOUSINGS = 1;
    private final int VIEW_TYPE_HOUSING_SECTION = 2;
    private final int VIEW_TYPE_HOUSINGS = 3;

    private final Context mContext;
    private final List<Housing> mPostedHousings;
    private final List<Housing> mHousings;
    private final OnHousingListInteractionListener mListener;

    public HousingRecyclerViewAdapter(Context context,
                                      List<Housing> postedHousings,
                                      List<Housing> housings,
                                      OnHousingListInteractionListener listener) {
        mContext = context;
        mPostedHousings = postedHousings;
        mHousings = housings;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HISTORY_HOUSING_SECTION) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_housing_list_history_housing_section_item, parent, false);
            return new HistoryHousingSectionViewHolder(itemView);
        } else if (viewType == VIEW_TYPE_POSTED_HOUSINGS) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_housing_item, parent, false);
            return new HistoryHousingViewHolder(itemView);
        } else if (viewType == VIEW_TYPE_HOUSING_SECTION) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_housing_list_housing_section_item, parent, false);
            return new HousingSectionViewHolder(itemView);
        }  else if (viewType == VIEW_TYPE_HOUSINGS) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_housing_item, parent, false);
            return new HousingViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HistoryHousingViewHolder) {
            final HistoryHousingViewHolder historyHousingViewHolder = (HistoryHousingViewHolder) holder;
            historyHousingViewHolder.mItem = mPostedHousings.get(position - 1);  // position - 1: minus History Housing Section Item.
            if (!mPostedHousings.get(position - 1).getPhotoURLs().isEmpty()) {
                String profileImageUrl = mPostedHousings.get(position - 1).getPhotoURLs().get(0);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Glide.with(mContext)
                            .load(R.drawable.ic_home)
                            .asBitmap()
                            .fitCenter()
                            .into(new BitmapImageViewTarget(historyHousingViewHolder.mPlaceHolderProfileImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    historyHousingViewHolder.mPlaceHolderProfileImage.setImageDrawable(new BitmapDrawable(mContext.getResources(), resource));
                                }
                            });
                    Glide.with(mContext)
                            .load(profileImageUrl)
                            .asBitmap()
                            .into(new BitmapImageViewTarget(historyHousingViewHolder.mProfileImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    historyHousingViewHolder.mProfileImage.setImageDrawable(new BitmapDrawable(mContext.getResources(), resource));
                                }
                            });
                } else {
                    Glide.with(mContext)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.ic_home)
                            .crossFade()
                            .into(historyHousingViewHolder.mProfileImage);
                }
            } else {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Glide.with(mContext)
                            .load(R.drawable.ic_home)
                            .asBitmap()
                            .fitCenter()
                            .into(new BitmapImageViewTarget(historyHousingViewHolder.mPlaceHolderProfileImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    historyHousingViewHolder.mPlaceHolderProfileImage.setImageDrawable(new BitmapDrawable(mContext.getResources(), resource));
                                }
                            });
                } else {
                    Glide.with(mContext)
                            .load(R.drawable.ic_home)
                            .crossFade()
                            .into(historyHousingViewHolder.mProfileImage);
                }
            }
            if (!TextUtils.isEmpty(mPostedHousings.get(position - 1).getTitle())) {
                historyHousingViewHolder.mHouseTitle.setText(mPostedHousings.get(position - 1).getTitle());
            }
            if (!TextUtils.isEmpty(mPostedHousings.get(position - 1).getHouseType())) {
                historyHousingViewHolder.mHouseType.setText(mPostedHousings.get(position - 1).getHouseType());
            }
            if (!TextUtils.isEmpty(mPostedHousings.get(position - 1).getAddressDistrict())
                    && !TextUtils.isEmpty(mPostedHousings.get(position - 1).getAddressCity())) {
                historyHousingViewHolder.mHouseLocation.setText(
//                        mContext.getString(R.string.housing_recycler_view_adapter_house_location) +
                        mPostedHousings.get(position - 1).getAddressDistrict() + ", "
                        + mPostedHousings.get(position - 1).getAddressCity()
                );
            }

//        historyHousingViewHolder.mNumSaved.setText(String.valueOf(mHousings.get(position).getNumOfSaved()) + mContext.getString(R.string.housing_recycler_view_adapter_saved));

            Pair<String, String> pair = HousePriceHelper.parseForHousing(mPostedHousings.get(position - 1).getPrice(), mContext);
            if (pair.first == null) {
                historyHousingViewHolder.mHousePrice.setText(pair.second);
            } else {
                historyHousingViewHolder.mHousePrice.setText(pair.first + " " + pair.second);
            }

//        historyHousingViewHolder.mProfileImage.setImageBitmap(
//                BitmapLoader.decodeSampledBitmapFromFile(localFile, 56, 56));

            historyHousingViewHolder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onHousingListInteraction(historyHousingViewHolder.mItem);
                    }
                }
            });
        } else if (holder instanceof HousingViewHolder) {
            final HousingViewHolder housingViewHolder = (HousingViewHolder) holder;
            // position - 1 - mPostedHousings.size() - 1:
            // minus Housing Section Item
            // AND Num Of Posted Housings
            // AND History Housing Section Item.
            int shiftedPosition = 0;
            if (mPostedHousings.isEmpty()) {
                shiftedPosition = position - 1;
            } else {
                shiftedPosition = position - 1 - mPostedHousings.size() - 1;
            }
            housingViewHolder.mItem = mHousings.get(shiftedPosition);
            if (!mHousings.get(shiftedPosition).getPhotoURLs().isEmpty()) {
                String profileImageUrl = mHousings.get(shiftedPosition).getPhotoURLs().get(0);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Glide.with(mContext)
                            .load(R.drawable.ic_home)
                            .asBitmap()
                            .fitCenter()
                            .into(new BitmapImageViewTarget(housingViewHolder.mPlaceHolderProfileImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    housingViewHolder.mPlaceHolderProfileImage.setImageDrawable(new BitmapDrawable(mContext.getResources(), resource));
                                }
                            });
                    Glide.with(mContext)
                            .load(profileImageUrl)
                            .asBitmap()
                            .into(new BitmapImageViewTarget(housingViewHolder.mProfileImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    housingViewHolder.mProfileImage.setImageDrawable(new BitmapDrawable(mContext.getResources(), resource));
                                }
                            });
                } else {
                    Glide.with(mContext)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.ic_home)
                            .crossFade()
                            .into(housingViewHolder.mProfileImage);
                }
            } else {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Glide.with(mContext)
                            .load(R.drawable.ic_home)
                            .asBitmap()
                            .fitCenter()
                            .into(new BitmapImageViewTarget(housingViewHolder.mPlaceHolderProfileImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    housingViewHolder.mPlaceHolderProfileImage.setImageDrawable(new BitmapDrawable(mContext.getResources(), resource));
                                }
                            });
                } else {
                    Glide.with(mContext)
                            .load(R.drawable.ic_home)
                            .crossFade()
                            .into(housingViewHolder.mProfileImage);
                }
            }
            if (!TextUtils.isEmpty(mHousings.get(shiftedPosition).getTitle())) {
                housingViewHolder.mHouseTitle.setText(mHousings.get(shiftedPosition).getTitle());
            }
            if (!TextUtils.isEmpty(mHousings.get(shiftedPosition).getHouseType())) {
                housingViewHolder.mHouseType.setText(mHousings.get(shiftedPosition).getHouseType());
            }
            if (!TextUtils.isEmpty(mHousings.get(shiftedPosition).getAddressDistrict())
                    && !TextUtils.isEmpty(mHousings.get(shiftedPosition).getAddressCity())) {
                housingViewHolder.mHouseLocation.setText(
//                        mContext.getString(R.string.housing_recycler_view_adapter_house_location) +
                        mHousings.get(shiftedPosition).getAddressDistrict() + ", "
                        + mHousings.get(shiftedPosition).getAddressCity()
                );
            }

//        housingViewHolder.mNumSaved.setText(String.valueOf(mHousings.get(position).getNumOfSaved()) + mContext.getString(R.string.housing_recycler_view_adapter_saved));

            Pair<String, String> pair = HousePriceHelper.parseForHousing(mHousings.get(shiftedPosition).getPrice(), mContext);
            if (pair.first == null) {
                housingViewHolder.mHousePrice.setText(pair.second);
            } else {
                housingViewHolder.mHousePrice.setText(pair.first + " " + pair.second);
            }

//        housingViewHolder.mProfileImage.setImageBitmap(
//                BitmapLoader.decodeSampledBitmapFromFile(localFile, 56, 56));

            housingViewHolder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onHousingListInteraction(housingViewHolder.mItem);
                    }
                }
            });
        } else if (holder instanceof HistoryHousingSectionViewHolder) {
            HistoryHousingSectionViewHolder historyHousingSectionViewHolder = (HistoryHousingSectionViewHolder) holder;
            historyHousingSectionViewHolder.mShowMoreButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, HistoryPostActivity.class);
                            mContext.startActivity(intent);
                        }
                    }
            );
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mPostedHousings.isEmpty()) {
            if (mHousings.isEmpty()) {
                return -1;
            } else {
                if (position == 0) {
                    return VIEW_TYPE_HOUSING_SECTION;
                } else {
                    return VIEW_TYPE_HOUSINGS;
                }
            }
        } else {
            if (mHousings.isEmpty()) {
                if (position == 0) {
                    return VIEW_TYPE_HISTORY_HOUSING_SECTION;
                } else {
                    return VIEW_TYPE_POSTED_HOUSINGS;
                }
            } else {
                if (position == 0) {
                    return VIEW_TYPE_HISTORY_HOUSING_SECTION;
                } else if (position > 0 && position < mPostedHousings.size() + 1) {
                    return VIEW_TYPE_POSTED_HOUSINGS;
                } else if (position == mPostedHousings.size() + 1) {
                    return VIEW_TYPE_HOUSING_SECTION;
                } else {
                    return VIEW_TYPE_HOUSINGS;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mPostedHousings.isEmpty()) {
            if (mHousings.isEmpty()) {
                return 0;
            } else {
                return mHousings.size() + 1;
            }
        } else {
            if (mHousings.isEmpty()) {
                return mPostedHousings.size() + 1;
            } else {
                return mPostedHousings.size() + 1
                        + mHousings.size() + 1;
            }
        }
    }

    public class HistoryHousingSectionViewHolder extends RecyclerView.ViewHolder {
        public final LinearLayout mShowMoreButton;

        public HistoryHousingSectionViewHolder(View itemView) {
            super(itemView);
            mShowMoreButton = (LinearLayout) itemView.findViewById(R.id.fragment_housing_list_history_housing_section_item_show_more);
        }
    }

    public class HistoryHousingViewHolder extends RecyclerView.ViewHolder {
        public final View mItemView;
        public final SquareImageView mPlaceHolderProfileImage;
        public final SquareImageView mProfileImage;
        public final TextView mHouseTitle;
        public final TextView mHouseType;
        public final TextView mHouseLocation;
//        public final TextView mNumSaved;
        public final TextView mHousePrice;
        public Housing mItem;

        public HistoryHousingViewHolder(View itemView) {
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

    public class HousingSectionViewHolder extends RecyclerView.ViewHolder {
        public HousingSectionViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class HousingViewHolder extends RecyclerView.ViewHolder {
        public final View mItemView;
        public final SquareImageView mPlaceHolderProfileImage;
        public final SquareImageView mProfileImage;
        public final TextView mHouseTitle;
        public final TextView mHouseType;
        public final TextView mHouseLocation;
        //        public final TextView mNumSaved;
        public final TextView mHousePrice;
        public Housing mItem;

        public HousingViewHolder(View itemView) {
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
