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
import com.lmtri.sharespace.api.model.Housing;
import com.lmtri.sharespace.api.model.SavedHousing;
import com.lmtri.sharespace.customview.SquareImageView;
import com.lmtri.sharespace.helper.HousePriceHelper;
import com.lmtri.sharespace.listener.OnHousingListInteractionListener;

import java.util.List;

/**
 * Created by lmtri on 12/10/2017.
 */

public class HistorySavedHousingRecyclerViewAdapter extends RecyclerView.Adapter<HistorySavedHousingRecyclerViewAdapter.ViewHolder> {

    public static final String TAG = HistorySavedHousingRecyclerViewAdapter.class.getSimpleName();

    private final Context mContext;
    private final List<SavedHousing> mSavedHousings;
    private final OnHousingListInteractionListener mListener;

    public HistorySavedHousingRecyclerViewAdapter(Context context, List<SavedHousing> items, OnHousingListInteractionListener listener) {
        mContext = context;
        mSavedHousings = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_housing_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mSavedHousings.get(position).getSavedHousing();
        if (!mSavedHousings.get(position).getSavedHousing().getPhotoURLs().isEmpty()) {
            String profileImageUrl = mSavedHousings.get(position).getSavedHousing().getPhotoURLs().get(0);
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
        if (!TextUtils.isEmpty(mSavedHousings.get(position).getSavedHousing().getTitle())) {
            holder.mHouseTitle.setText(mSavedHousings.get(position).getSavedHousing().getTitle());
        }
        if (!TextUtils.isEmpty(mSavedHousings.get(position).getSavedHousing().getHouseType())) {
            holder.mHouseType.setText(mSavedHousings.get(position).getSavedHousing().getHouseType());
        }
        if (!TextUtils.isEmpty(mSavedHousings.get(position).getSavedHousing().getAddressDistrict())
                && !TextUtils.isEmpty(mSavedHousings.get(position).getSavedHousing().getAddressCity())) {
            holder.mHouseLocation.setText(
//                    mContext.getString(R.string.housing_recycler_view_adapter_house_location) +
                    mSavedHousings.get(position).getSavedHousing().getAddressDistrict() + ", "
                    + mSavedHousings.get(position).getSavedHousing().getAddressCity()
            );
        }

//        holder.mNumSaved.setText(String.valueOf(mSavedHousings.get(position).getNumOfSaved()) + mContext.getString(R.string.housing_recycler_view_adapter_saved));

        Pair<String, String> pair = HousePriceHelper.parseForHousing(mSavedHousings.get(position).getSavedHousing().getPrice(), mContext);
        if (pair.first == null) {
            holder.mHousePrice.setText(pair.second);
        } else {
            holder.mHousePrice.setText(pair.first + " " + pair.second);
        }

//        holder.mProfileImage.setImageBitmap(
//                BitmapLoader.decodeSampledBitmapFromFile(localFile, 56, 56));

        holder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onHousingListInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSavedHousings != null ? mSavedHousings.size() : 0;
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
        public Housing mItem;

        public ViewHolder(View itemView) {
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
