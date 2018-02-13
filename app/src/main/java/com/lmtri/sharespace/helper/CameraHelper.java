package com.lmtri.sharespace.helper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.lmtri.sharespace.R;
import com.lmtri.sharespace.listener.OnActivityResultListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * Created by lmtri on 8/11/2017.
 */

public class CameraHelper implements OnActivityResultListener {

    public static final String TAG = CameraHelper.class.getSimpleName();

    private Activity mActivity;
    private static String mImageDirectory;
    private static String mCurrentImagePath;

    public CameraHelper(@NonNull Activity activity) {
        mActivity = Objects.requireNonNull(activity, "Cannot Start Camera from NULL Activity/Context!");
    }

    /**
     * Start camera intent
     * Create a temporary file and pass file Uri to camera intent
     */
    public void captureImage(String imageDirectory) {
        if (!TextUtils.isEmpty(imageDirectory)) {
            mImageDirectory = imageDirectory;
        } else {
            mImageDirectory = mActivity.getString(R.string.activity_post_house_camera_image_directory);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
            File imageFile = createImageFile(mImageDirectory);
            if (imageFile != null) {
                String authority = mActivity.getPackageName() + ".provider";
                Uri uri = FileProvider.getUriForFile(mActivity, authority, imageFile);
                mCurrentImagePath = "file:" + imageFile.getAbsolutePath();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                mActivity.startActivityForResult(intent, Constants.START_CAMERA_REQUEST);
            } else {
                ToastHelper.showCenterToast(mActivity, mActivity.getString(R.string.activity_post_house_camera_create_image_file_error), Toast.LENGTH_LONG);
            }
        } else {
            ToastHelper.showCenterToast(mActivity, mActivity.getString(R.string.activity_post_house_no_camera_found_error), Toast.LENGTH_LONG);
        }
    }

    private File createImageFile(String directory) {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), directory);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create " + directory + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp;

        File imageFile = null;
        try {
            imageFile = File.createTempFile(imageFileName, ".jpg", mediaStorageDir);
        } catch (IOException e) {
            Log.d(TAG, "Oops! Failed create " + imageFileName + " file");
        }
        return imageFile;
    }

    public String getCurrentImagePath() {
        return mCurrentImagePath;
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.START_CAMERA_REQUEST) {
            if (resultCode == RESULT_OK && !TextUtils.isEmpty(mCurrentImagePath)) {
//                Uri imageUri = Uri.parse(mCurrentImagePath);
//                if (imageUri != null) {
//                    MediaScannerConnection.scanFile(
//                            mActivity,
//                            new String[]{imageUri.getPath()},
//                            null,
//                            new MediaScannerConnection.OnScanCompletedListener() {
//                                @Override
//                                public void onScanCompleted(String path, Uri uri) {
//                                    if (mListener != null) {
//                                        mListener.onScanCompleted(path, uri);
//                                    }
//                                }
//                            }
//                    );
//                }
                return true;
            }
        }
        return false;
    }
}
