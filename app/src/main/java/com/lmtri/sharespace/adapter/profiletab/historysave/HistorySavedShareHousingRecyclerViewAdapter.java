package com.lmtri.sharespace.adapter.profiletab.historysave;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.lmtri.sharespace.R;
import com.lmtri.sharespace.api.model.SavedShareHousing;
import com.lmtri.sharespace.api.model.ShareHousing;
import com.lmtri.sharespace.customview.SquareImageView;
import com.lmtri.sharespace.helper.HousePriceHelper;
import com.lmtri.sharespace.listener.OnShareHousingListInteractionListener;

import java.util.List;

/**
 * Created by lmtri on 12/10/2017.
 */

public class HistorySavedShareHousingRecyclerViewAdapter extends RecyclerView.Adapter<HistorySavedShareHousingRecyclerViewAdapter.ViewHolder> {

    public static final String TAG = HistorySavedShareHousingRecyclerViewAdapter.class.getSimpleName();

    private final Context mContext;
    private final List<SavedShareHousing> mSavedShareHousings;
    private final OnShareHousingListInteractionListener mListener;

    public HistorySavedShareHousingRecyclerViewAdapter(Context context, List<SavedShareHousing> items, OnShareHousingListInteractionListener listener) {
        mContext = context;
        mSavedShareHousings = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_share_housing_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mSavedShareHousings.get(position).getSavedShareHousing();
        if (!mSavedShareHousings.get(position).getSavedShareHousing().getHousing().getPhotoURLs().isEmpty()) {
            String profileImageUrl = mSavedShareHousings.get(position).getSavedShareHousing().getHousing().getPhotoURLs().get(0);
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
        if (!TextUtils.isEmpty(mSavedShareHousings.get(position).getSavedShareHousing().getHousing().getTitle())) {
            holder.mHouseTitle.setText(mSavedShareHousings.get(position).getSavedShareHousing().getHousing().getTitle());
        }
        if (!TextUtils.isEmpty(mSavedShareHousings.get(position).getSavedShareHousing().getHousing().getHouseType())) {
            holder.mHouseType.setText(mSavedShareHousings.get(position).getSavedShareHousing().getHousing().getHouseType());
        }
        if (!TextUtils.isEmpty(mSavedShareHousings.get(position).getSavedShareHousing().getHousing().getAddressDistrict())
                && !TextUtils.isEmpty(mSavedShareHousings.get(position).getSavedShareHousing().getHousing().getAddressCity())) {
            holder.mHouseLocation.setText(
//                    mContext.getString(R.string.housing_recycler_view_adapter_house_location) +
                    mSavedShareHousings.get(position).getSavedShareHousing().getHousing().getAddressDistrict() + ", "
                    + mSavedShareHousings.get(position).getSavedShareHousing().getHousing().getAddressCity()
            );
        }

//        holder.mNumSaved.setText(String.valueOf(mSavedShareHousings.get(position).getNumOfSaved()) + mContext.getString(R.string.housing_recycler_view_adapter_saved));

        Pair<String, String> pair = HousePriceHelper.parseForShareHousing(mSavedShareHousings.get(position).getSavedShareHousing().getPricePerMonthOfOne(), mContext);
        if (pair.first == null) {
            holder.mHousePrice.setText(pair.second);
        } else {
            holder.mHousePrice.setText(pair.first + " " + pair.second);
        }

        if (!TextUtils.isEmpty(mSavedShareHousings.get(position).getSavedShareHousing().getRequiredGender())) {
            holder.mRequiredGender.setText(mSavedShareHousings.get(position).getSavedShareHousing().getRequiredGender());
        } else {
            holder.mRequiredGender.setText(mContext.getString(R.string.hint_signup_gender_female));
        }

        if (mSavedShareHousings.get(position).getSavedShareHousing().getRequiredNumOfPeople() <= 0) {
            holder.mRequiredNumOfPeople.setText(1 + mContext.getString(R.string.activity_housing_detail_people));
        } else {
            holder.mRequiredNumOfPeople.setText(mSavedShareHousings.get(position).getSavedShareHousing().getRequiredNumOfPeople() + mContext.getString(R.string.activity_housing_detail_people));
        }

        if (!TextUtils.isEmpty(mSavedShareHousings.get(position).getSavedShareHousing().getRequiredWorkType())) {
            holder.mRequiredWorkType.setText(mSavedShareHousings.get(position).getSavedShareHousing().getRequiredWorkType());
        } else {
            holder.mRequiredWorkType.setText(mContext.getString(R.string.share_housing_recycler_view_adapter_work_type_student));
        }

//        holder.mProfileImage.setImageBitmap(
//                BitmapLoader.decodeSampledBitmapFromFile(localFile, 56, 56));

        holder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onShareHousingListInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSavedShareHousings != null ? mSavedShareHousings.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mItemView;
        public final SquareImageView mPlaceHolderProfileImage;
        public final SquareImageView mProfileImage;
        public final TextView mHouseTitle;
        public final TextView mHouseType;
        public final TextView mHouseLocation;
        //        public final TextView mNumSaved;
        public final TextView mHousePrice;
        public final TextView mRequiredGender;
        public final TextView mRequiredNumOfPeople;
        public final TextView mRequiredWorkType;
        public ShareHousing mItem;

        public ViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mPlaceHolderProfileImage = (SquareImageView) itemView.findViewById(R.id.fragment_share_housing_item_house_profile_image_placeholder);
            mProfileImage = (SquareImageView) itemView.findViewById(R.id.fragment_share_housing_item_house_profile_image);
            mHouseTitle = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_house_title);
            mHouseType = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_house_type);
            mHouseLocation = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_house_location);
//            mNumSaved = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_num_saved);
            mHousePrice = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_price_per_month_of_one);
            mRequiredGender = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_gender);
            mRequiredNumOfPeople = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_num_people);
            mRequiredWorkType = (TextView) itemView.findViewById(R.id.fragment_share_housing_item_work_type);
        }
    }
}
