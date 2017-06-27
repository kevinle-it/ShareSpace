package com.lmtri.sharespace.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lmtri.sharespace.R;
import com.lmtri.sharespace.fragment.HousingFragment.OnListFragmentInteractionListener;
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
        Glide.with(mContext).load(profileImageUrl).placeholder(R.drawable.ic_home).into(holder.mProfileImage);

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
        public final ImageView mProfileImage;
        public Housing mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mProfileImage = (ImageView) view.findViewById(R.id.house_profile_image);
        }
    }
}
