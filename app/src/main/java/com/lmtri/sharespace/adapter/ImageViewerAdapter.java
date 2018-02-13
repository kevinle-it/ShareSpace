package com.lmtri.sharespace.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.lmtri.sharespace.R;
import com.lmtri.sharespace.activity.ImageViewerActivity;
import com.lmtri.sharespace.helper.Constants;

import java.util.ArrayList;

/**
 * Created by lmtri on 9/21/2017.
 */

public class ImageViewerAdapter extends PagerAdapter {
    private ArrayList<String> mPhotoURLs;
    private int mCurrentPhotoIndex;
    private boolean mIsFullscreen = false;

    public ImageViewerAdapter(ArrayList<String> photoURLs) {
        mPhotoURLs = photoURLs;
    }

    public ImageViewerAdapter(ArrayList<String> photoURLs, boolean isFullscreen) {
        mPhotoURLs = photoURLs;
        mIsFullscreen = isFullscreen;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final Context context = container.getContext();
        PhotoView photoView = new PhotoView(context);

        if (mIsFullscreen) {
            photoView.setBackgroundColor(ContextCompat.getColor(container.getContext(), android.R.color.black));
            photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        else {
            photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ImageViewerActivity.class);
                    intent.putStringArrayListExtra(Constants.ACTIVITY_IMAGE_VIEWER_PHOTO_URLS, mPhotoURLs);
                    intent.putExtra(Constants.ACTIVITY_IMAGE_VIEWER_CURRENT_PHOTO_INDEX, mCurrentPhotoIndex);
                    context.startActivity(intent);
                }
            });
            photoView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        Glide.with(container.getContext())
                .load(mPhotoURLs.get(position))
                .placeholder(R.drawable.ic_home)
                .crossFade()
                .into(photoView);

        container.addView(photoView,
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT)
        );
        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((PhotoView) object);
    }

    @Override
    public int getCount() {
        return mPhotoURLs.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setCurrentPhotoIndex(int currentPhotoIndex) {
        mCurrentPhotoIndex = currentPhotoIndex;
    }
}
