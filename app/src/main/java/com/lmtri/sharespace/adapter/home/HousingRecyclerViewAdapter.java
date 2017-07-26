package com.lmtri.sharespace.adapter.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.lmtri.sharespace.R;
import com.lmtri.sharespace.customview.SquareImageView;
import com.lmtri.sharespace.fragment.home.HousingFragment.OnListFragmentInteractionListener;
import com.lmtri.sharespace.model.Housing;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Housing} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class HousingRecyclerViewAdapter extends RecyclerView.Adapter<HousingRecyclerViewAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Housing> mValues;
    private final OnListFragmentInteractionListener mListener;

    public HousingRecyclerViewAdapter(Context context, List<Housing> items, OnListFragmentInteractionListener listener) {
        mContext = context;
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_housing_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        Uri profileImageUrl = mValues.get(position).getProfileImageUrl();
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
        if (mValues.get(position).getTitle() != null && !mValues.get(position).getTitle().isEmpty()) {
            holder.mHouseTitle.setText(mValues.get(position).getTitle());
        }
        if (mValues.get(position).getHouseType() != null && !mValues.get(position).getHouseType().isEmpty()
                && mValues.get(position).getAddressDistrict() != null && !mValues.get(position).getAddressDistrict().isEmpty()
                && mValues.get(position).getAddressCity() != null && !mValues.get(position).getAddressCity().isEmpty()) {
            holder.mHouseTypeAndLocation.setText(
                    mValues.get(position).getHouseType()
                    + mContext.getString(R.string.housing_recycler_view_adapter_house_type_in_district) + mValues.get(position).getAddressDistrict() + ", "
                    + mValues.get(position).getAddressCity()
            );
        }
        if (mValues.get(position).getPrice() != null && !mValues.get(position).getPrice().isEmpty()) {
            holder.mHousePrice.setText(mValues.get(position).getPrice());
        }
//        holder.mProfileImage.setImageBitmap(
//                BitmapLoader.decodeSampledBitmapFromFile(localFile, 56, 56));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final SquareImageView mPlaceHolderProfileImage;
        public final SquareImageView mProfileImage;
        public final TextView mHouseTitle;
        public final TextView mHouseTypeAndLocation;
        public final TextView mHousePrice;
        public Housing mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mPlaceHolderProfileImage = (SquareImageView) view.findViewById(R.id.fragment_housing_item_placeholder_house_profile_image);
            mProfileImage = (SquareImageView) view.findViewById(R.id.fragment_housing_item_house_profile_image);
            mHouseTitle = (TextView) view.findViewById(R.id.fragment_housing_item_house_title);
            mHouseTypeAndLocation = (TextView) view.findViewById(R.id.fragment_housing_item_house_type_and_location_title);
            mHousePrice = (TextView) view.findViewById(R.id.fragment_housing_item_price);
        }
    }
}
