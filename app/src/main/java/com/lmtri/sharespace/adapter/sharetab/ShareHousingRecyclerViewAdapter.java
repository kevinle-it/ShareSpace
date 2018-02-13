package com.lmtri.sharespace.adapter.sharetab;

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
import com.lmtri.sharespace.api.model.ShareHousing;
import com.lmtri.sharespace.customview.SquareImageView;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.HousePriceHelper;
import com.lmtri.sharespace.listener.OnShareHousingListInteractionListener;

import java.util.List;

/**
 * Created by lmtri on 8/22/2017.
 */

public class ShareHousingRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String TAG = ShareHousingRecyclerViewAdapter.class.getSimpleName();

    private final int VIEW_TYPE_HISTORY_SHARE_HOUSING_SECTION = 0;
    private final int VIEW_TYPE_POSTED_SHARE_HOUSINGS = 1;
    private final int VIEW_TYPE_SHARE_HOUSING_SECTION = 2;
    private final int VIEW_TYPE_SHARE_HOUSINGS = 3;

    private final Context mContext;
    private final List<ShareHousing> mPostedShareHousings;
    private final List<ShareHousing> mShareHousings;
    private final OnShareHousingListInteractionListener mListener;

    public ShareHousingRecyclerViewAdapter(Context context,
                                           List<ShareHousing> postedShareHousings,
                                           List<ShareHousing> shareHousings,
                                           OnShareHousingListInteractionListener listener) {
        mContext = context;
        mPostedShareHousings = postedShareHousings;
        mShareHousings = shareHousings;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HISTORY_SHARE_HOUSING_SECTION) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_share_housing_list_history_share_housing_section_item, parent, false);
            return new HistoryShareHousingSectionViewHolder(itemView);
        } else if (viewType == VIEW_TYPE_POSTED_SHARE_HOUSINGS) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_share_housing_item, parent, false);
            return new HistoryShareHousingViewHolder(itemView);
        } else if (viewType == VIEW_TYPE_SHARE_HOUSING_SECTION) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_share_housing_list_share_housing_section_item, parent, false);
            return new ShareHousingSectionViewHolder(itemView);
        }  else if (viewType == VIEW_TYPE_SHARE_HOUSINGS) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_share_housing_item, parent, false);
            return new ShareHousingViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HistoryShareHousingViewHolder) {
            final HistoryShareHousingViewHolder historyShareHousingViewHolder = (HistoryShareHousingViewHolder) holder;
            historyShareHousingViewHolder.mItem = mPostedShareHousings.get(position - 1);  // position - 1: minus History Share Housing Section Item.
            if (!mPostedShareHousings.get(position - 1).getHousing().getPhotoURLs().isEmpty()) {
                String profileImageUrl = mPostedShareHousings.get(position - 1).getHousing().getPhotoURLs().get(0);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Glide.with(mContext)
                            .load(R.drawable.ic_home)
                            .asBitmap()
                            .fitCenter()
                            .into(new BitmapImageViewTarget(historyShareHousingViewHolder.mPlaceHolderProfileImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    historyShareHousingViewHolder.mPlaceHolderProfileImage.setImageDrawable(new BitmapDrawable(mContext.getResources(), resource));
                                }
                            });
                    Glide.with(mContext)
                            .load(profileImageUrl)
                            .asBitmap()
                            .into(new BitmapImageViewTarget(historyShareHousingViewHolder.mProfileImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    historyShareHousingViewHolder.mProfileImage.setImageDrawable(new BitmapDrawable(mContext.getResources(), resource));
                                }
                            });
                } else {
                    Glide.with(mContext)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.ic_home)
                            .crossFade()
                            .into(historyShareHousingViewHolder.mProfileImage);
                }
            } else {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Glide.with(mContext)
                            .load(R.drawable.ic_home)
                            .asBitmap()
                            .fitCenter()
                            .into(new BitmapImageViewTarget(historyShareHousingViewHolder.mPlaceHolderProfileImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    historyShareHousingViewHolder.mPlaceHolderProfileImage.setImageDrawable(new BitmapDrawable(mContext.getResources(), resource));
                                }
                            });
                } else {
                    Glide.with(mContext)
                            .load(R.drawable.ic_home)
                            .crossFade()
                            .into(historyShareHousingViewHolder.mProfileImage);
                }
            }
            if (!TextUtils.isEmpty(mPostedShareHousings.get(position - 1).getHousing().getTitle())) {
                historyShareHousingViewHolder.mHouseTitle.setText(mPostedShareHousings.get(position - 1).getHousing().getTitle());
            }
            if (!TextUtils.isEmpty(mPostedShareHousings.get(position - 1).getHousing().getHouseType())) {
                historyShareHousingViewHolder.mHouseType.setText(mPostedShareHousings.get(position - 1).getHousing().getHouseType());
            }
            if (!TextUtils.isEmpty(mPostedShareHousings.get(position - 1).getHousing().getAddressDistrict())
                    && !TextUtils.isEmpty(mPostedShareHousings.get(position - 1).getHousing().getAddressCity())) {
                historyShareHousingViewHolder.mHouseLocation.setText(
//                        mContext.getString(R.string.housing_recycler_view_adapter_house_location) +
                        mPostedShareHousings.get(position - 1).getHousing().getAddressDistrict() + ", "
                        + mPostedShareHousings.get(position - 1).getHousing().getAddressCity()
                );
            }

//        historyShareHousingViewHolder.mNumSaved.setText(String.valueOf(mShareHousings.get(position).getNumOfSaved()) + mContext.getString(R.string.housing_recycler_view_adapter_saved));

            Pair<String, String> pair = HousePriceHelper.parseForShareHousing(mPostedShareHousings.get(position - 1).getPricePerMonthOfOne(), mContext);
            if (pair.first == null) {
                historyShareHousingViewHolder.mPricePerMonthOfOne.setText(pair.second);
            } else {
                historyShareHousingViewHolder.mPricePerMonthOfOne.setText(pair.first + " " + pair.second);
            }

            if (!TextUtils.isEmpty(mPostedShareHousings.get(position - 1).getRequiredGender())) {
                historyShareHousingViewHolder.mRequiredGender.setText(mPostedShareHousings.get(position - 1).getRequiredGender());
            } else {
                historyShareHousingViewHolder.mRequiredGender.setText(mContext.getString(R.string.hint_signup_gender_female));
            }

            if (mPostedShareHousings.get(position - 1).getRequiredNumOfPeople() <= 0) {
                historyShareHousingViewHolder.mRequiredNumOfPeople.setText(1 + mContext.getString(R.string.activity_housing_detail_people));
            } else {
                historyShareHousingViewHolder.mRequiredNumOfPeople.setText(mPostedShareHousings.get(position - 1).getRequiredNumOfPeople() + mContext.getString(R.string.activity_housing_detail_people));
            }

            if (!TextUtils.isEmpty(mPostedShareHousings.get(position - 1).getRequiredWorkType())) {
                historyShareHousingViewHolder.mRequiredWorkType.setText(mPostedShareHousings.get(position - 1).getRequiredWorkType());
            } else {
                historyShareHousingViewHolder.mRequiredWorkType.setText(mContext.getString(R.string.share_housing_recycler_view_adapter_work_type_student));
            }

//        holder.mProfileImage.setImageBitmap(
//                BitmapLoader.decodeSampledBitmapFromFile(localFile, 56, 56));

            historyShareHousingViewHolder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onShareHousingListInteraction(historyShareHousingViewHolder.mItem);
                    }
                }
            });
        } else if (holder instanceof ShareHousingViewHolder) {
            final ShareHousingViewHolder shareHousingViewHolder = (ShareHousingViewHolder) holder;
            // position - 1 - mPostedShareHousings.size() - 1:
            // minus Share Housing Section Item
            // AND Num Of Posted Share Housings
            // AND History Share Housing Section Item.
            int shiftedPosition = 0;
            if (mPostedShareHousings.isEmpty()) {
                shiftedPosition = position - 1;
            } else {
                shiftedPosition = position - 1 - mPostedShareHousings.size() - 1;
            }
            shareHousingViewHolder.mItem = mShareHousings.get(shiftedPosition);
            if (!mShareHousings.get(shiftedPosition).getHousing().getPhotoURLs().isEmpty()) {
                String profileImageUrl = mShareHousings.get(shiftedPosition).getHousing().getPhotoURLs().get(0);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Glide.with(mContext)
                            .load(R.drawable.ic_home)
                            .asBitmap()
                            .fitCenter()
                            .into(new BitmapImageViewTarget(shareHousingViewHolder.mPlaceHolderProfileImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    shareHousingViewHolder.mPlaceHolderProfileImage.setImageDrawable(new BitmapDrawable(mContext.getResources(), resource));
                                }
                            });
                    Glide.with(mContext)
                            .load(profileImageUrl)
                            .asBitmap()
                            .into(new BitmapImageViewTarget(shareHousingViewHolder.mProfileImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    shareHousingViewHolder.mProfileImage.setImageDrawable(new BitmapDrawable(mContext.getResources(), resource));
                                }
                            });
                } else {
                    Glide.with(mContext)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.ic_home)
                            .crossFade()
                            .into(shareHousingViewHolder.mProfileImage);
                }
            } else {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Glide.with(mContext)
                            .load(R.drawable.ic_home)
                            .asBitmap()
                            .fitCenter()
                            .into(new BitmapImageViewTarget(shareHousingViewHolder.mPlaceHolderProfileImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    shareHousingViewHolder.mPlaceHolderProfileImage.setImageDrawable(new BitmapDrawable(mContext.getResources(), resource));
                                }
                            });
                } else {
                    Glide.with(mContext)
                            .load(R.drawable.ic_home)
                            .crossFade()
                            .into(shareHousingViewHolder.mProfileImage);
                }
            }
            if (!TextUtils.isEmpty(mShareHousings.get(shiftedPosition).getHousing().getTitle())) {
                shareHousingViewHolder.mHouseTitle.setText(mShareHousings.get(shiftedPosition).getHousing().getTitle());
            }
            if (!TextUtils.isEmpty(mShareHousings.get(shiftedPosition).getHousing().getHouseType())) {
                shareHousingViewHolder.mHouseType.setText(mShareHousings.get(shiftedPosition).getHousing().getHouseType());
            }
            if (!TextUtils.isEmpty(mShareHousings.get(shiftedPosition).getHousing().getAddressDistrict())
                    && !TextUtils.isEmpty(mShareHousings.get(shiftedPosition).getHousing().getAddressCity())) {
                shareHousingViewHolder.mHouseLocation.setText(
//                        mContext.getString(R.string.housing_recycler_view_adapter_house_location) +
                        mShareHousings.get(shiftedPosition).getHousing().getAddressDistrict() + ", "
                        + mShareHousings.get(shiftedPosition).getHousing().getAddressCity()
                );
            }

//        shareHousingViewHolder.mNumSaved.setText(String.valueOf(mShareHousings.get(position).getNumOfSaved()) + mContext.getString(R.string.housing_recycler_view_adapter_saved));

            Pair<String, String> pair = HousePriceHelper.parseForShareHousing(mShareHousings.get(shiftedPosition).getPricePerMonthOfOne(), mContext);
            if (pair.first == null) {
                shareHousingViewHolder.mPricePerMonthOfOne.setText(pair.second);
            } else {
                shareHousingViewHolder.mPricePerMonthOfOne.setText(pair.first + " " + pair.second);
            }

            if (!TextUtils.isEmpty(mShareHousings.get(shiftedPosition).getRequiredGender())) {
                shareHousingViewHolder.mRequiredGender.setText(mShareHousings.get(shiftedPosition).getRequiredGender());
            } else {
                shareHousingViewHolder.mRequiredGender.setText(mContext.getString(R.string.hint_signup_gender_female));
            }

            if (mShareHousings.get(shiftedPosition).getRequiredNumOfPeople() <= 0) {
                shareHousingViewHolder.mRequiredNumOfPeople.setText(1 + mContext.getString(R.string.activity_housing_detail_people));
            } else {
                shareHousingViewHolder.mRequiredNumOfPeople.setText(mShareHousings.get(shiftedPosition).getRequiredNumOfPeople() + mContext.getString(R.string.activity_housing_detail_people));
            }

            if (!TextUtils.isEmpty(mShareHousings.get(shiftedPosition).getRequiredWorkType())) {
                shareHousingViewHolder.mRequiredWorkType.setText(mShareHousings.get(shiftedPosition).getRequiredWorkType());
            } else {
                shareHousingViewHolder.mRequiredWorkType.setText(mContext.getString(R.string.share_housing_recycler_view_adapter_work_type_student));
            }

//        holder.mProfileImage.setImageBitmap(
//                BitmapLoader.decodeSampledBitmapFromFile(localFile, 56, 56));

            shareHousingViewHolder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onShareHousingListInteraction(shareHousingViewHolder.mItem);
                    }
                }
            });
        } else if (holder instanceof HistoryShareHousingSectionViewHolder) {
            HistoryShareHousingSectionViewHolder historyShareHousingSectionViewHolder = (HistoryShareHousingSectionViewHolder) holder;
            historyShareHousingSectionViewHolder.mShowMoreButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, HistoryPostActivity.class);
                            intent.putExtra(Constants.SELECTED_TAB_INDEX_POSTED_SHARE_HOUSING, 1);
                            mContext.startActivity(intent);
                        }
                    }
            );
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mPostedShareHousings.isEmpty()) {
            if (mShareHousings.isEmpty()) {
                return -1;
            } else {
                if (position == 0) {
                    return VIEW_TYPE_SHARE_HOUSING_SECTION;
                } else {
                    return VIEW_TYPE_SHARE_HOUSINGS;
                }
            }
        } else {
            if (mShareHousings.isEmpty()) {
                if (position == 0) {
                    return VIEW_TYPE_HISTORY_SHARE_HOUSING_SECTION;
                } else {
                    return VIEW_TYPE_POSTED_SHARE_HOUSINGS;
                }
            } else {
                if (position == 0) {
                    return VIEW_TYPE_HISTORY_SHARE_HOUSING_SECTION;
                } else if (position > 0 && position < mPostedShareHousings.size() + 1) {
                    return VIEW_TYPE_POSTED_SHARE_HOUSINGS;
                } else if (position == mPostedShareHousings.size() + 1) {
                    return VIEW_TYPE_SHARE_HOUSING_SECTION;
                } else {
                    return VIEW_TYPE_SHARE_HOUSINGS;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mPostedShareHousings.isEmpty()) {
            if (mShareHousings.isEmpty()) {
                return 0;
            } else {
                return mShareHousings.size() + 1;
            }
        } else {
            if (mShareHousings.isEmpty()) {
                return mPostedShareHousings.size() + 1;
            } else {
                return mPostedShareHousings.size() + 1
                        + mShareHousings.size() + 1;
            }
        }
    }

    public class HistoryShareHousingSectionViewHolder extends RecyclerView.ViewHolder {
        public final LinearLayout mShowMoreButton;

        public HistoryShareHousingSectionViewHolder(View itemView) {
            super(itemView);
            mShowMoreButton = (LinearLayout) itemView.findViewById(R.id.fragment_share_housing_list_history_share_housing_section_item_show_more);
        }
    }

    public class HistoryShareHousingViewHolder extends RecyclerView.ViewHolder {
        public final View mItemView;
        public final SquareImageView mPlaceHolderProfileImage;
        public final SquareImageView mProfileImage;
        public final TextView mHouseTitle;
        public final TextView mHouseType;
        public final TextView mHouseLocation;
//        public final TextView mNumSaved;
        public final TextView mPricePerMonthOfOne;
        public final TextView mRequiredGender;
        public final TextView mRequiredNumOfPeople;
        public final TextView mRequiredWorkType;
        public ShareHousing mItem;

        public HistoryShareHousingViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mPlaceHolderProfileImage = (SquareImageView) itemView.findViewById(R.id.fragment_share_housing_item_house_profile_image_placeholder);
            mProfileImage = (SquareImageView) itemView.findViewById(R.id.fragment_share_housing_item_house_profile_image);
            mHouseTitle = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_house_title);
            mHouseType = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_house_type);
            mHouseLocation = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_house_location);
//            mNumSaved = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_num_saved);
            mPricePerMonthOfOne = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_price_per_month_of_one);
            mRequiredGender = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_gender);
            mRequiredNumOfPeople = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_num_people);
            mRequiredWorkType = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_work_type);
        }
    }

    public class ShareHousingSectionViewHolder extends RecyclerView.ViewHolder {
        public ShareHousingSectionViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class ShareHousingViewHolder extends RecyclerView.ViewHolder {
        public final View mItemView;
        public final SquareImageView mPlaceHolderProfileImage;
        public final SquareImageView mProfileImage;
        public final TextView mHouseTitle;
        public final TextView mHouseType;
        public final TextView mHouseLocation;
        //        public final TextView mNumSaved;
        public final TextView mPricePerMonthOfOne;
        public final TextView mRequiredGender;
        public final TextView mRequiredNumOfPeople;
        public final TextView mRequiredWorkType;
        public ShareHousing mItem;

        public ShareHousingViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mPlaceHolderProfileImage = (SquareImageView) itemView.findViewById(R.id.fragment_share_housing_item_house_profile_image_placeholder);
            mProfileImage = (SquareImageView) itemView.findViewById(R.id.fragment_share_housing_item_house_profile_image);
            mHouseTitle = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_house_title);
            mHouseType = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_house_type);
            mHouseLocation = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_house_location);
//            mNumSaved = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_num_saved);
            mPricePerMonthOfOne = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_price_per_month_of_one);
            mRequiredGender = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_gender);
            mRequiredNumOfPeople = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_num_people);
            mRequiredWorkType = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_work_type);
        }
    }
}
