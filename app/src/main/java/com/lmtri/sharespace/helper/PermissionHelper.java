package com.lmtri.sharespace.helper;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.lmtri.sharespace.R;
import com.lmtri.sharespace.listener.OnRequestPermissionsResultCallback;

import java.util.ArrayList;

/**
 * Created by lmtri on 8/11/2017.
 */

public class PermissionHelper implements OnRequestPermissionsResultCallback {

    public static final String TAG = PermissionHelper.class.getSimpleName();

    private Activity mActivity;
    private String mImageDirectory;
    private CameraHelper mCameraHelper;

    public PermissionHelper(@NonNull Activity activity) {
        mActivity = Objects.requireNonNull(activity, "Cannot Check Permissions with NULL Activity/Context!");
        this.mCameraHelper = new CameraHelper(activity);
    }

    public CameraHelper getCameraHelper() {
        return mCameraHelper;
    }

    /**
     * Request for camera permission
     */
    public void capturePhotoWithCheckPermissions(String imageDirectory) {
        if (!TextUtils.isEmpty(imageDirectory)) {
            mImageDirectory = imageDirectory;
        } else {
            mImageDirectory = mActivity.getString(R.string.activity_post_house_camera_image_directory);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> notGrantedPermissions = getNotGrantedPermissions(
                    mActivity,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            );
            if (notGrantedPermissions.size() == 0) {
                // Camera & Write External Storage Permissions are granted.
                // Do the stuff that requires the permissions...
                mCameraHelper.captureImage(mImageDirectory);
            } else {
                if (notGrantedPermissions.size() == 2) {
                    // Camera & Write External Storage Permissions are not granted. Requesting permissions...
                    ActivityCompat.requestPermissions(
                            mActivity,
                            new String[]{
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            },
                            Constants.CAMERA_AND_WRITE_EXTERNAL_STORAGE_PERMISSIONS_REQUEST
                    );
                } else if (notGrantedPermissions.contains(Manifest.permission.CAMERA)) {
                    // Camera Permission is not granted. Requesting permission...
                    ActivityCompat.requestPermissions(
                            mActivity,
                            new String[]{ Manifest.permission.CAMERA },
                            Constants.CAMERA_PERMISSION_REQUEST
                    );
                } else {
                    // Write External Storage Permission is not granted. Requesting permission...
                    ActivityCompat.requestPermissions(
                            mActivity,
                            new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE },
                            Constants.WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST
                    );
                }
            }
        } else {
            mCameraHelper.captureImage(mImageDirectory);
        }
    }

    public ArrayList<String> getNotGrantedPermissions(@NonNull Activity activity,
                                              @NonNull String... permissions) {
        Objects.requireNonNull(activity, "Cannot Check Permissions with NULL Activity/Context!");
        Objects.requireNonNull(permissions, "No Permission to check!");
        ArrayList<String> notGrantedPermissions = new ArrayList<>();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(activity, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    notGrantedPermissions.add(permission);
                }
            }
        }
        return notGrantedPermissions;
    }

    /**
     * Open app settings screen
     */
    private void openAppSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", activity.getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.CAMERA_AND_WRITE_EXTERNAL_STORAGE_PERMISSIONS_REQUEST) {
            if (grantResults.length > 0) {
                boolean hasCameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean hasWriteExternalStoragePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                boolean shouldShowCameraRequestPermissionRationale = ActivityCompat
                        .shouldShowRequestPermissionRationale(mActivity, Manifest.permission.CAMERA);
                boolean shouldShowWriteExternalStorageRequestPermissionRationale = ActivityCompat
                        .shouldShowRequestPermissionRationale(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (hasCameraPermission && hasWriteExternalStoragePermission) {
                    // Has Camera AND Write External Storage Permissions.
                    // Do the stuff that requires the permissions...
                    mCameraHelper.captureImage(mImageDirectory);
                } else if (!hasCameraPermission && !hasWriteExternalStoragePermission) {
                    // No Camera AND Write External Storage Permissions.
                    // Should we show an explanation?
                    if (shouldShowCameraRequestPermissionRationale
                            && shouldShowWriteExternalStorageRequestPermissionRationale) {
                        // Show permission explanation dialog...
                        showDialogCameraAndWriteExternalStoragePermissionsRationale();
                    } else if (!shouldShowCameraRequestPermissionRationale
                            && !shouldShowWriteExternalStorageRequestPermissionRationale) {
                        // Never ask again of Camera & Write External Storage Permissions selected,
                        // or device policy prohibits the app from having those permissions.
                        // So, disable those features, or fall back to another situation...
                        showSnackBarPermissionRequest(
                                R.string.activity_post_house_camera_and_write_external_storage_permission_denied_dont_show_again_rationale,
                                R.string.activity_post_house_permission_denied_dont_show_again_rationale_ok,
                                Snackbar.LENGTH_LONG
                        );
                    } else if (!shouldShowCameraRequestPermissionRationale) {
                        // Never ask again of Camera Permission Request selected,
                        // or device policy prohibits the app from having that permission.
                        // So, disable that feature, or fall back to another situation...
                        showSnackBarPermissionRequest(
                                R.string.activity_post_house_camera_permission_denied_dont_show_again_rationale,
                                R.string.activity_post_house_permission_denied_dont_show_again_rationale_ok,
                                Snackbar.LENGTH_INDEFINITE
                        );
                        showDialogWriteExternalStoragePermissionRationale();
                    } else {
                        // Never ask again of Write External Storage Permission Request selected,
                        // or device policy prohibits the app from having that permission.
                        // So, disable that feature, or fall back to another situation...
                        showSnackBarPermissionRequest(
                                R.string.activity_post_house_write_external_storage_permission_denied_dont_show_again_rationale,
                                R.string.activity_post_house_permission_denied_dont_show_again_rationale_ok,
                                Snackbar.LENGTH_INDEFINITE
                        );
                        showDialogCameraPermissionRationale();
                    }
                } else if (!hasCameraPermission) {  // No Camera Permission & Has Write External Storage Permission.
                    // Should we show an explanation?
                    if (shouldShowCameraRequestPermissionRationale) {
                        // Show permission explanation dialog...
                        showDialogCameraPermissionRationale();
                    } else {
                        // Never ask again of Camera Permission Request selected,
                        // or device policy prohibits the app from having that permission.
                        // So, disable that feature, or fall back to another situation...
                        showSnackBarPermissionRequest(
                                R.string.activity_post_house_camera_permission_denied_dont_show_again_rationale,
                                R.string.activity_post_house_permission_denied_dont_show_again_rationale_ok,
                                Snackbar.LENGTH_LONG
                        );
                    }
                } else {    // No Write External Storage Permission & Has Camera Permission.
                    // Should we show an explanation?
                    if (shouldShowWriteExternalStorageRequestPermissionRationale) {
                        // Show permission explanation dialog...
                        showDialogWriteExternalStoragePermissionRationale();
                    } else {
                        // Never ask again of Write External Storage Permission Request selected,
                        // or device policy prohibits the app from having that permission.
                        // So, disable that feature, or fall back to another situation...
                        showSnackBarPermissionRequest(
                                R.string.activity_post_house_write_external_storage_permission_denied_dont_show_again_rationale,
                                R.string.activity_post_house_permission_denied_dont_show_again_rationale_ok,
                                Snackbar.LENGTH_LONG
                        );
                    }
                }
            }
        } else if (requestCode == Constants.CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Camera & Write External Storage Permissions are granted.
                    // Do the stuff that requires the permissions...
                    mCameraHelper.captureImage(mImageDirectory);
                } else {
                    // Should we show an explanation?
                    if (ActivityCompat
                            .shouldShowRequestPermissionRationale(mActivity, Manifest.permission.CAMERA)) {
                        // Show permission explanation dialog...
                        showDialogCameraPermissionRationale();
                    } else {
                        // Never ask again of Camera Permission Request selected,
                        // or device policy prohibits the app from having that permission.
                        // So, disable that feature, or fall back to another situation...
                        showSnackBarPermissionRequest(
                                R.string.activity_post_house_camera_permission_denied_dont_show_again_rationale,
                                R.string.activity_post_house_permission_denied_dont_show_again_rationale_ok,
                                Snackbar.LENGTH_LONG
                        );
                    }
                }
            }
        } else if (requestCode == Constants.WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Camera & Write External Storage Permissions are granted.
                    // Do the stuff that requires the permissions...
                    mCameraHelper.captureImage(mImageDirectory);
                } else {
                    // Should we show an explanation?
                    if (ActivityCompat
                            .shouldShowRequestPermissionRationale(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // Show permission explanation dialog...
                        showDialogWriteExternalStoragePermissionRationale();
                    } else {
                        // Never ask again of Write External Storage Permission Request selected,
                        // or device policy prohibits the app from having that permission.
                        // So, disable that feature, or fall back to another situation...
                        showSnackBarPermissionRequest(
                                R.string.activity_post_house_write_external_storage_permission_denied_dont_show_again_rationale,
                                R.string.activity_post_house_permission_denied_dont_show_again_rationale_ok,
                                Snackbar.LENGTH_LONG
                        );
                    }
                }
            }
        } else {
            Log.d(TAG, "Got an unexpected permission request: " + requestCode);
        }
    }

    private void showDialogCameraAndWriteExternalStoragePermissionsRationale() {
        new AlertDialog.Builder(mActivity)
                .setTitle(R.string.activity_post_house_camera_and_write_external_storage_permission_denied_rationale_title)
                .setMessage(R.string.activity_post_house_camera_and_write_external_storage_permissions_denied_rationale)
                .setPositiveButton(R.string.activity_post_house_permission_denied_rationale_retry,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(
                                        mActivity,
                                        new String[]{
                                                Manifest.permission.CAMERA,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                                        },
                                        Constants.CAMERA_AND_WRITE_EXTERNAL_STORAGE_PERMISSIONS_REQUEST
                                );
                            }
                        }
                )
                .setNegativeButton(R.string.activity_post_house_permission_denied_rationale_cancel, null)
                .show();
    }

    private void showDialogCameraPermissionRationale() {
        new AlertDialog.Builder(mActivity)
                .setTitle(R.string.activity_post_house_camera_permission_denied_rationale_title)
                .setMessage(R.string.activity_post_house_camera_permission_denied_rationale)
                .setPositiveButton(R.string.activity_post_house_permission_denied_rationale_retry,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(
                                        mActivity,
                                        new String[]{ Manifest.permission.CAMERA },
                                        Constants.CAMERA_PERMISSION_REQUEST
                                );
                            }
                        }
                )
                .setNegativeButton(R.string.activity_post_house_permission_denied_rationale_cancel, null)
                .show();
    }

    private void showDialogWriteExternalStoragePermissionRationale() {
        new AlertDialog.Builder(mActivity)
                .setTitle(R.string.activity_post_house_write_external_storage_permission_denied_rationale_title)
                .setMessage(R.string.activity_post_house_write_external_storage_permission_denied_rationale)
                .setPositiveButton(R.string.activity_post_house_permission_denied_rationale_retry,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(
                                        mActivity,
                                        new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE },
                                        Constants.WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST
                                );
                            }
                        }
                )
                .setNegativeButton(R.string.activity_post_house_permission_denied_rationale_cancel, null)
                .show();
    }

    private void showSnackBarPermissionRequest(int messageResId,
                                               int actionResId,
                                               int duration) {
        Snackbar.make(
                    mActivity.findViewById(android.R.id.content)
                            .getRootView(),
                    messageResId,
                    duration
                )
                .setAction(
                    actionResId,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openAppSettings(mActivity);
                        }
                    }
                )
                .show();
    }
}
