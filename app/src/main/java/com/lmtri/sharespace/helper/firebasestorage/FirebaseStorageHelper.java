package com.lmtri.sharespace.helper.firebasestorage;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lmtri.sharespace.R;
import com.lmtri.sharespace.api.model.Housing;
import com.lmtri.sharespace.api.model.ShareHousing;
import com.lmtri.sharespace.api.service.housing.HousingClient;
import com.lmtri.sharespace.api.service.housing.photo.IPhotoPostingCallback;
import com.lmtri.sharespace.api.service.housing.post.IHousingDeletingCallback;
import com.lmtri.sharespace.api.service.housing.post.IHousingPostingCallback;
import com.lmtri.sharespace.api.service.housing.post.IHousingUpdatingCallback;
import com.lmtri.sharespace.api.service.sharehousing.ShareHousingClient;
import com.lmtri.sharespace.api.service.sharehousing.post.IShareHousingDeletingCallback;
import com.lmtri.sharespace.api.service.sharehousing.post.IShareHousingPostingCallback;
import com.lmtri.sharespace.api.service.sharehousing.post.IShareHousingUpdatingCallback;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.FileHelper;
import com.lmtri.sharespace.helper.ToastHelper;
import com.lmtri.sharespace.helper.firebasestorage.housing.OnHousingDeletingListener;
import com.lmtri.sharespace.helper.firebasestorage.housing.OnHousingPostingListener;
import com.lmtri.sharespace.helper.firebasestorage.housing.OnHousingUpdatingListener;
import com.lmtri.sharespace.helper.firebasestorage.sharehousing.OnShareHousingDeletingListener;
import com.lmtri.sharespace.helper.firebasestorage.sharehousing.OnShareHousingPostingListener;
import com.lmtri.sharespace.helper.firebasestorage.sharehousing.OnShareHousingUpdatingListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * Created by lmtri on 8/13/2017.
 */

public class FirebaseStorageHelper {
    private static int mNumOfPhotosUploaded = 0;
    
    public static byte[] getBytesFrom(Context context, Uri uri) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int bufferSize = 1024;
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            byte[] buffer = new byte[bufferSize];
            int length = 0;
            while ((length = inputStream.read(buffer)) != -1)
                outputStream.write(buffer, 0, length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    public static void postPhoto(final Context context, final int housingID,
                                 Uri photoURI, final int shareHousingID, String housingTitle,
                                 final OnPhotoPostingListener photoPostingListener) {
        ProgressDialog getImageInfoProgressDialog = new ProgressDialog(context, R.style.LoginProgressDialog);
        getImageInfoProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        getImageInfoProgressDialog.setIndeterminate(true);
        getImageInfoProgressDialog.setTitle(context.getString(R.string.activity_post_house_uploading));
        getImageInfoProgressDialog.setMessage(context.getString(R.string.activity_post_house_get_image_info));
        getImageInfoProgressDialog.setCanceledOnTouchOutside(false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getImageInfoProgressDialog.getWindow()
                    .setBackgroundDrawableResource(android.R.color.transparent);
        }
        getImageInfoProgressDialog.show();

        String path = FileHelper.getPathFromUri(context, photoURI);

        getImageInfoProgressDialog.dismiss();

        final ProgressDialog uploadImageProgressDialog = new ProgressDialog(context, R.style.LoginProgressDialog);
        uploadImageProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        uploadImageProgressDialog.setIndeterminate(true);
        uploadImageProgressDialog.setTitle(context.getString(R.string.activity_post_house_uploading));
        uploadImageProgressDialog.setMessage(context.getString(
                R.string.activity_housing_detail_syncing_photo_message
        ));
        uploadImageProgressDialog.setCanceledOnTouchOutside(false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            uploadImageProgressDialog.getWindow()
                    .setBackgroundDrawableResource(android.R.color.transparent);
        }
        uploadImageProgressDialog.show();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "IMG_" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "_"
                + timeStamp + "_";
        String storagePath = FirebaseAuth.getInstance().getCurrentUser().getUid() + "/"
                + housingTitle + "/" + imageName;

        StorageReference mStorageReference = FirebaseStorage.getInstance()
                .getReferenceFromUrl(Constants.STORAGE_REFERENCE_URL);

        File imageFile = new File(path);

        String extension = "";
        int j = imageFile.getAbsolutePath().lastIndexOf('.');
        if (j > 0) {
            extension = imageFile.getAbsolutePath().substring(j + 1);
        }
        StorageReference imageStorageReferencePath;
        imageStorageReferencePath = mStorageReference.child(storagePath + "." + extension);

        try {
            InputStream inputStream = new FileInputStream(imageFile);
            UploadTask uploadTask = imageStorageReferencePath.putStream(inputStream);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    uploadPhotoInfoToShareSpaceServer(
                            context,
                            uploadImageProgressDialog,
                            housingID, taskSnapshot.getDownloadUrl().toString(),
                            shareHousingID, photoPostingListener
                    );
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    uploadImageProgressDialog.setMessage(context.getString(R.string.activity_post_house_uploading_error, e.getMessage()));
                }
            });
        } catch (FileNotFoundException e) {
            uploadImageProgressDialog.setMessage(context.getString(R.string.activity_post_house_uploading_error, e.getMessage()));
            e.printStackTrace();
        }
    }

    private static void uploadPhotoInfoToShareSpaceServer(final Context context,
            final ProgressDialog uploadImageProgressDialog, int housingID,
            final String photoURL, int shareHousingID, final OnPhotoPostingListener photoPostingListener) {
        HousingClient.postPhotoURL(
                housingID, photoURL, shareHousingID, new IPhotoPostingCallback() {
                    @Override
                    public void onPostComplete(Boolean isSuccess) {
                        uploadImageProgressDialog.dismiss();
                        if (isSuccess) {
                            photoPostingListener.onPostSuccess(photoURL);
                        } else {
                            ToastHelper.showCenterToast(
                                    context,
                                    R.string.activity_housing_detail_photos_not_synced_message,
                                    Toast.LENGTH_LONG
                            );
                        }
                    }

                    @Override
                    public void onPostFailure(Throwable t) {
                        photoPostingListener.onPostFailure(t);
                    }
                }
        );
    }

    public static void postNewHousing(final Context context, final Housing toBePostedHousing,
                                      final ArrayList<Uri> photoURIs,
                                      final OnHousingPostingListener housingPostingListener) {
//        String filePath = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera";
//        String fileName = "testImage.png";

//        File imageFile = new File(filePath, fileName);

        ProgressDialog getImageInfoProgressDialog = new ProgressDialog(context, R.style.LoginProgressDialog);
        getImageInfoProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        getImageInfoProgressDialog.setIndeterminate(true);
        getImageInfoProgressDialog.setTitle(context.getString(R.string.activity_post_house_uploading));
        getImageInfoProgressDialog.setMessage(context.getString(R.string.activity_post_house_get_image_info));
        getImageInfoProgressDialog.setCanceledOnTouchOutside(false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getImageInfoProgressDialog.getWindow()
                    .setBackgroundDrawableResource(android.R.color.transparent);
        }
        getImageInfoProgressDialog.show();

        final ArrayList<String> imageDiskDirectory = new ArrayList<>();
        for (int i = 0; i < photoURIs.size(); ++i) {
//            byte[] imageData = getBytesFrom(context, photoURIs.get(i));

            String path = FileHelper.getPathFromUri(context, photoURIs.get(i));

            if (!TextUtils.isEmpty(path)) {
                imageDiskDirectory.add(path);
            }
        }
        getImageInfoProgressDialog.dismiss();

        final ProgressDialog uploadImageProgressDialog = new ProgressDialog(context, R.style.LoginProgressDialog);
        uploadImageProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        uploadImageProgressDialog.setProgress(0);
        uploadImageProgressDialog.setMax(imageDiskDirectory.size());
        uploadImageProgressDialog.setTitle(context.getString(R.string.activity_post_house_uploading));
        uploadImageProgressDialog.setMessage(context.getString(
                R.string.activity_post_house_uploading_success_on_each_image,
                0, imageDiskDirectory.size()
        ));
        uploadImageProgressDialog.setCanceledOnTouchOutside(false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            uploadImageProgressDialog.getWindow()
                    .setBackgroundDrawableResource(android.R.color.transparent);
        }
        uploadImageProgressDialog.show();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "IMG_" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "_"
                + timeStamp + "_";
        String storagePath = FirebaseAuth.getInstance().getCurrentUser().getUid() + "/"
                + toBePostedHousing.getTitle() + "_" + timeStamp + "/" + imageName;

        StorageReference mStorageReference = FirebaseStorage.getInstance()
                .getReferenceFromUrl(Constants.STORAGE_REFERENCE_URL);

        mNumOfPhotosUploaded = 0;
        final ArrayList<String> photoURLs = new ArrayList<>();
        for (int i = 0; i < imageDiskDirectory.size(); ++i) {
            File imageFile = new File(imageDiskDirectory.get(i));

            String extension = "";
            int j = imageFile.getAbsolutePath().lastIndexOf('.');
            if (j > 0) {
                extension = imageFile.getAbsolutePath().substring(j + 1);
            }
            StorageReference imageStorageReferencePath;
            if (i == 0) {
                imageStorageReferencePath = mStorageReference.child(storagePath + "Profile." + extension);
            } else {
                imageStorageReferencePath = mStorageReference.child(storagePath + i + "." + extension);
            }

            try {
                InputStream inputStream = new FileInputStream(imageFile);
                UploadTask uploadTask = imageStorageReferencePath.putStream(inputStream);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        photoURLs.add(taskSnapshot.getDownloadUrl().toString());

                        uploadImageProgressDialog.setMessage(context.getString(
                                R.string.activity_post_house_uploading_success_on_each_image,
                                ++mNumOfPhotosUploaded,
                                imageDiskDirectory.size()
                        ));
                        uploadImageProgressDialog.setProgress(mNumOfPhotosUploaded);

                        if (uploadImageProgressDialog.getProgress() == uploadImageProgressDialog.getMax()) {
                            uploadImageProgressDialog.dismiss();
                            ProgressDialog uploadHousingInfoProgressDialog = new ProgressDialog(context, R.style.LoginProgressDialog);
                            uploadHousingInfoProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            uploadHousingInfoProgressDialog.setIndeterminate(true);
                            uploadHousingInfoProgressDialog.setTitle(context.getString(R.string.activity_post_house_uploading));
                            uploadHousingInfoProgressDialog.setMessage(context.getString(R.string.activity_post_house_uploading_house_info));
                            uploadHousingInfoProgressDialog.setCanceledOnTouchOutside(false);
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                                uploadHousingInfoProgressDialog.getWindow()
                                        .setBackgroundDrawableResource(android.R.color.transparent);
                            }
                            uploadHousingInfoProgressDialog.show();

                            Collections.sort(photoURLs, new Comparator<String>() {
                                @Override
                                public int compare(String a, String b) {
                                    return a.toLowerCase(Locale.US).substring(a.lastIndexOf(".") - 1, a.lastIndexOf("."))
                                            .compareToIgnoreCase(b.toLowerCase(Locale.US).substring(b.lastIndexOf(".") - 1, b.lastIndexOf(".")));
                                }
                            });
                            photoURLs.add(0, photoURLs.get(photoURLs.size() - 1));
                            photoURLs.remove(photoURLs.size() - 1);

                            uploadHousingInfoToShareSpaceServer(
                                    uploadHousingInfoProgressDialog,
                                    toBePostedHousing, photoURLs,
                                    housingPostingListener
                            );
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        uploadImageProgressDialog.setMessage(context.getString(R.string.activity_post_house_uploading_error, e.getMessage()));
                    }
                });
            } catch (FileNotFoundException e) {
                uploadImageProgressDialog.setMessage(context.getString(R.string.activity_post_house_uploading_error, e.getMessage()));
                e.printStackTrace();
            }
        }
    }

    public static void updateHousing(final Context context, final Housing toBeUpdatedHousing,
                                     final ArrayList<Uri> photoURIs,
                                     final OnHousingUpdatingListener housingUpdatingListener) {
        ProgressDialog getImageInfoProgressDialog = new ProgressDialog(context, R.style.LoginProgressDialog);
        getImageInfoProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        getImageInfoProgressDialog.setIndeterminate(true);
        getImageInfoProgressDialog.setTitle(context.getString(R.string.activity_post_house_uploading));
        getImageInfoProgressDialog.setMessage(context.getString(R.string.activity_post_house_get_image_info));
        getImageInfoProgressDialog.setCanceledOnTouchOutside(false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getImageInfoProgressDialog.getWindow()
                    .setBackgroundDrawableResource(android.R.color.transparent);
        }
        getImageInfoProgressDialog.show();

        int numNewPhotosNeedToUpload = 0;
        final ArrayList<String> imageDiskDirectory = new ArrayList<>();
        for (int i = 0; i < photoURIs.size(); ++i) {
            String path;
            if (!photoURIs.get(i).toString().contains("https://firebasestorage.googleapis.com/")) {
                path = FileHelper.getPathFromUri(context, photoURIs.get(i));
                ++numNewPhotosNeedToUpload;
            } else {
                path = photoURIs.get(i).toString();
            }
            if (!TextUtils.isEmpty(path)) {
                imageDiskDirectory.add(path);
            }
        }
        getImageInfoProgressDialog.dismiss();

        final ProgressDialog uploadImageProgressDialog = new ProgressDialog(context, R.style.LoginProgressDialog);
        uploadImageProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        uploadImageProgressDialog.setProgress(0);
        uploadImageProgressDialog.setMax(numNewPhotosNeedToUpload);
        uploadImageProgressDialog.setTitle(context.getString(R.string.activity_post_house_uploading));
        uploadImageProgressDialog.setMessage(context.getString(
                R.string.activity_post_house_uploading_success_on_each_image,
                0, numNewPhotosNeedToUpload
        ));
        uploadImageProgressDialog.setCanceledOnTouchOutside(false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            uploadImageProgressDialog.getWindow()
                    .setBackgroundDrawableResource(android.R.color.transparent);
        }
        uploadImageProgressDialog.show();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "IMG_" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "_"
                + timeStamp + "_";
        String storagePath = FirebaseAuth.getInstance().getCurrentUser().getUid() + "/"
                + toBeUpdatedHousing.getTitle() + "_" + timeStamp + "/" + imageName;

        StorageReference mStorageReference = FirebaseStorage.getInstance()
                .getReferenceFromUrl(Constants.STORAGE_REFERENCE_URL);

        boolean isExistPhotosNeedToUpload = false;
        for (int i = 0; i < imageDiskDirectory.size(); ++i) {
            if (!imageDiskDirectory.get(i).contains("https://firebasestorage.googleapis.com/")) {
                isExistPhotosNeedToUpload = true;
                break;
            }
        }
        if (isExistPhotosNeedToUpload) {
            mNumOfPhotosUploaded = 0;
            final ArrayList<String> photoURLs = new ArrayList<>();
            for (int i = 0; i < imageDiskDirectory.size(); ++i) {
                if (!imageDiskDirectory.get(i).contains("https://firebasestorage.googleapis.com/")) {
                    File imageFile = new File(imageDiskDirectory.get(i));

                    String extension = "";
                    int j = imageFile.getAbsolutePath().lastIndexOf('.');
                    if (j > 0) {
                        extension = imageFile.getAbsolutePath().substring(j + 1);
                    }
                    StorageReference imageStorageReferencePath;
                    if (i == 0) {
                        imageStorageReferencePath = mStorageReference.child(storagePath + "Profile." + extension);
                    } else {
                        imageStorageReferencePath = mStorageReference.child(storagePath + i + "." + extension);
                    }

                    try {
                        InputStream inputStream = new FileInputStream(imageFile);
                        UploadTask uploadTask = imageStorageReferencePath.putStream(inputStream);
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                photoURLs.add(taskSnapshot.getDownloadUrl().toString());

                                uploadImageProgressDialog.setMessage(context.getString(
                                        R.string.activity_post_house_uploading_success_on_each_image,
                                        ++mNumOfPhotosUploaded,
                                        uploadImageProgressDialog.getMax()
                                ));
                                uploadImageProgressDialog.setProgress(mNumOfPhotosUploaded);

                                if (uploadImageProgressDialog.getProgress() == uploadImageProgressDialog.getMax()) {
                                    uploadImageProgressDialog.dismiss();
                                    ProgressDialog uploadHousingInfoProgressDialog = new ProgressDialog(context, R.style.LoginProgressDialog);
                                    uploadHousingInfoProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                    uploadHousingInfoProgressDialog.setIndeterminate(true);
                                    uploadHousingInfoProgressDialog.setTitle(context.getString(R.string.activity_post_house_uploading));
                                    uploadHousingInfoProgressDialog.setMessage(context.getString(R.string.activity_post_house_uploading_house_info));
                                    uploadHousingInfoProgressDialog.setCanceledOnTouchOutside(false);
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                                        uploadHousingInfoProgressDialog.getWindow()
                                                .setBackgroundDrawableResource(android.R.color.transparent);
                                    }
                                    uploadHousingInfoProgressDialog.show();

                                    Collections.sort(photoURLs, new Comparator<String>() {
                                        @Override
                                        public int compare(String a, String b) {
                                            return a.toLowerCase(Locale.US).substring(a.lastIndexOf(".") - 1, a.lastIndexOf("."))
                                                    .compareToIgnoreCase(b.toLowerCase(Locale.US).substring(b.lastIndexOf(".") - 1, b.lastIndexOf(".")));
                                        }
                                    });
                                    photoURLs.add(0, photoURLs.get(photoURLs.size() - 1));
                                    photoURLs.remove(photoURLs.size() - 1);

                                    updateHousingInfoOnShareSpaceServer(
                                            uploadHousingInfoProgressDialog,
                                            toBeUpdatedHousing, photoURLs,
                                            housingUpdatingListener
                                    );
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                uploadImageProgressDialog.setMessage(context.getString(R.string.activity_post_house_uploading_error, e.getMessage()));
                            }
                        });
                    } catch (FileNotFoundException e) {
                        uploadImageProgressDialog.setMessage(context.getString(R.string.activity_post_house_uploading_error, e.getMessage()));
                        e.printStackTrace();
                    }
                } else {
                    photoURLs.add(imageDiskDirectory.get(i));
                }
            }
        } else {
            ProgressDialog uploadHousingInfoProgressDialog = new ProgressDialog(context, R.style.LoginProgressDialog);
            uploadHousingInfoProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            uploadHousingInfoProgressDialog.setIndeterminate(true);
            uploadHousingInfoProgressDialog.setTitle(context.getString(R.string.activity_post_house_uploading));
            uploadHousingInfoProgressDialog.setMessage(context.getString(R.string.activity_post_house_uploading_house_info));
            uploadHousingInfoProgressDialog.setCanceledOnTouchOutside(false);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                uploadHousingInfoProgressDialog.getWindow()
                        .setBackgroundDrawableResource(android.R.color.transparent);
            }
            uploadHousingInfoProgressDialog.show();

            updateHousingInfoOnShareSpaceServer(
                    uploadHousingInfoProgressDialog,
                    toBeUpdatedHousing, imageDiskDirectory,
                    housingUpdatingListener
            );
        }
    }

    private static void uploadHousingInfoToShareSpaceServer(
            final ProgressDialog progressDialog,
            final Housing toBePostedHousing, final ArrayList<String> photoURLs,
            final OnHousingPostingListener housingPostingListener) {

        toBePostedHousing.setPhotoURLs(photoURLs);

        HousingClient.postHousing(
                toBePostedHousing,
                new IHousingPostingCallback() {
                    @Override
                    public void onPostComplete(Housing housing) {
                        progressDialog.dismiss();
                        housingPostingListener.onPostSuccess(housing);
                    }

                    @Override
                    public void onPostFailure(Throwable t) {
                        housingPostingListener.onPostFailure(t);
                    }
                }
        );
    }

    private static void updateHousingInfoOnShareSpaceServer(
            final ProgressDialog progressDialog,
            final Housing toBeUpdatedHousing, final ArrayList<String> photoURLs,
            final OnHousingUpdatingListener housingUpdatingListener) {

        toBeUpdatedHousing.setPhotoURLs(photoURLs);

        HousingClient.updateHousing(
                toBeUpdatedHousing,
                new IHousingUpdatingCallback() {
                    @Override
                    public void onUpdateComplete(Boolean isUpdated) {
                        progressDialog.dismiss();
                        housingUpdatingListener.onUpdateComplete(isUpdated);
                    }

                    @Override
                    public void onUpdateFailure(Throwable t) {
                        housingUpdatingListener.onUpdateFailure(t);
                    }
                }
        );
    }

    public static void deleteHousing(final Context context, final int housingID,
                                     final OnHousingDeletingListener housingDeletingListener) {
        final ProgressDialog deleteHousingProgressDialog = new ProgressDialog(context, R.style.LoginProgressDialog);
        deleteHousingProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        deleteHousingProgressDialog.setIndeterminate(true);
        deleteHousingProgressDialog.setMessage(context.getString(R.string.activity_housing_detail_delete_housing_progress_dialog_message));
        deleteHousingProgressDialog.setCanceledOnTouchOutside(false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            deleteHousingProgressDialog.getWindow()
                    .setBackgroundDrawableResource(android.R.color.transparent);
        }
        deleteHousingProgressDialog.show();

        HousingClient.deleteHousing(housingID, new IHousingDeletingCallback() {
            @Override
            public void onDeleteComplete(Boolean isDeleted) {
                deleteHousingProgressDialog.dismiss();
                if (isDeleted) {
                    housingDeletingListener.onDeleteComplete(isDeleted);
                } else {
                    ToastHelper.showCenterToast(
                            context,
                            R.string.activity_housing_detail_delete_housing_unsuccessfully_toast_message,
                            Toast.LENGTH_LONG
                    );
                }
            }

            @Override
            public void onDeleteFailure(Throwable t) {
                housingDeletingListener.onDeleteFailure(t);
            }
        });
    }

    public static void postNewShareHousing(final Context context,
                                           final ShareHousing toBePostedShareHousing,
                                           final ArrayList<Uri> photoURIs,
                                           final OnShareHousingPostingListener shareHousingPostingListener) {
//        String filePath = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera";
//        String fileName = "testImage.png";

//        File imageFile = new File(filePath, fileName);
        ProgressDialog getImageInfoProgressDialog = new ProgressDialog(context, R.style.LoginProgressDialog);
        getImageInfoProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        getImageInfoProgressDialog.setIndeterminate(true);
        getImageInfoProgressDialog.setTitle(context.getString(R.string.activity_post_house_uploading));
        getImageInfoProgressDialog.setMessage(context.getString(R.string.activity_post_house_get_image_info));
        getImageInfoProgressDialog.setCanceledOnTouchOutside(false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getImageInfoProgressDialog.getWindow()
                    .setBackgroundDrawableResource(android.R.color.transparent);
        }
        getImageInfoProgressDialog.show();

        final ArrayList<String> imageDiskDirectory = new ArrayList<>();
        for (int i = 0; i < photoURIs.size(); ++i) {
//            byte[] imageData = getBytesFrom(context, photoURIs.get(i));

            String path = FileHelper.getPathFromUri(context, photoURIs.get(i));

            if (!TextUtils.isEmpty(path)) {
                imageDiskDirectory.add(path);
            }
        }
        getImageInfoProgressDialog.dismiss();

        final ProgressDialog uploadImageProgressDialog = new ProgressDialog(context, R.style.LoginProgressDialog);
        uploadImageProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        uploadImageProgressDialog.setProgress(0);
        uploadImageProgressDialog.setMax(imageDiskDirectory.size());
        uploadImageProgressDialog.setTitle(context.getString(R.string.activity_post_house_uploading));
        uploadImageProgressDialog.setMessage(context.getString(
                R.string.activity_post_house_uploading_success_on_each_image,
                0, imageDiskDirectory.size()
        ));
        uploadImageProgressDialog.setCanceledOnTouchOutside(false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            uploadImageProgressDialog.getWindow()
                    .setBackgroundDrawableResource(android.R.color.transparent);
        }
        uploadImageProgressDialog.show();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "IMG_" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "_"
                + timeStamp + "_";
        String storagePath = FirebaseAuth.getInstance().getCurrentUser().getUid() + "/"
                + toBePostedShareHousing.getHousing().getTitle() + "_" + timeStamp + "/" + imageName;

        StorageReference mStorageReference = FirebaseStorage.getInstance()
                .getReferenceFromUrl(Constants.STORAGE_REFERENCE_URL);

        mNumOfPhotosUploaded = 0;
        final ArrayList<String> photoURLs = new ArrayList<>();
        for (int i = 0; i < imageDiskDirectory.size(); ++i) {
            File imageFile = new File(imageDiskDirectory.get(i));

            String extension = "";
            int j = imageFile.getAbsolutePath().lastIndexOf('.');
            if (j > 0) {
                extension = imageFile.getAbsolutePath().substring(j + 1);
            }
            StorageReference imageStorageReferencePath;
            if (i == 0) {
                imageStorageReferencePath = mStorageReference.child(storagePath + "Profile." + extension);
            } else {
                imageStorageReferencePath = mStorageReference.child(storagePath + i + "." + extension);
            }

            try {
                InputStream inputStream = new FileInputStream(imageFile);
                UploadTask uploadTask = imageStorageReferencePath.putStream(inputStream);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        photoURLs.add(taskSnapshot.getDownloadUrl().toString());

                        uploadImageProgressDialog.setMessage(context.getString(
                                R.string.activity_post_house_uploading_success_on_each_image,
                                ++mNumOfPhotosUploaded,
                                imageDiskDirectory.size()
                        ));
                        uploadImageProgressDialog.setProgress(mNumOfPhotosUploaded);

                        if (uploadImageProgressDialog.getProgress() == uploadImageProgressDialog.getMax()) {
                            uploadImageProgressDialog.dismiss();
                            ProgressDialog uploadHousingInfoProgressDialog = new ProgressDialog(context, R.style.LoginProgressDialog);
                            uploadHousingInfoProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            uploadHousingInfoProgressDialog.setIndeterminate(true);
                            uploadHousingInfoProgressDialog.setTitle(context.getString(R.string.activity_post_house_uploading));
                            uploadHousingInfoProgressDialog.setMessage(context.getString(R.string.activity_post_house_uploading_house_info));
                            uploadHousingInfoProgressDialog.setCanceledOnTouchOutside(false);
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                                uploadHousingInfoProgressDialog.getWindow()
                                        .setBackgroundDrawableResource(android.R.color.transparent);
                            }
                            uploadHousingInfoProgressDialog.show();
                            uploadShareHousingInfoToShareSpaceServer(
                                    uploadHousingInfoProgressDialog,
                                    toBePostedShareHousing, photoURLs,
                                    shareHousingPostingListener
                            );
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        uploadImageProgressDialog.setMessage(context.getString(R.string.activity_post_house_uploading_error, e.getMessage()));
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void postShareOfExistHousing(final Context context,
                                               final ShareHousing toBePostedShareOfExistHousing,
                                               final OnShareHousingPostingListener shareHousingPostingListener) {
        ProgressDialog uploadShareHousingInfoProgressDialog = new ProgressDialog(context, R.style.LoginProgressDialog);
        uploadShareHousingInfoProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        uploadShareHousingInfoProgressDialog.setIndeterminate(true);
        uploadShareHousingInfoProgressDialog.setTitle(context.getString(R.string.activity_post_house_uploading));
        uploadShareHousingInfoProgressDialog.setMessage(context.getString(R.string.activity_post_share_house_uploading_share_house_info));
        uploadShareHousingInfoProgressDialog.setCanceledOnTouchOutside(false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            uploadShareHousingInfoProgressDialog.getWindow()
                    .setBackgroundDrawableResource(android.R.color.transparent);
        }
        uploadShareHousingInfoProgressDialog.show();
        uploadShareHousingInfoToShareSpaceServer(
                uploadShareHousingInfoProgressDialog,
                toBePostedShareOfExistHousing, null, shareHousingPostingListener
        );
    }

    public static void updateNewShareHousing(final Context context,
                                             final ShareHousing toBeUpdatedNewShareHousing,
                                             final ArrayList<Uri> photoURIs,
                                             final OnShareHousingUpdatingListener shareHousingUpdatingListener) {
        ProgressDialog getImageInfoProgressDialog = new ProgressDialog(context, R.style.LoginProgressDialog);
        getImageInfoProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        getImageInfoProgressDialog.setIndeterminate(true);
        getImageInfoProgressDialog.setTitle(context.getString(R.string.activity_post_house_uploading));
        getImageInfoProgressDialog.setMessage(context.getString(R.string.activity_post_house_get_image_info));
        getImageInfoProgressDialog.setCanceledOnTouchOutside(false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getImageInfoProgressDialog.getWindow()
                    .setBackgroundDrawableResource(android.R.color.transparent);
        }
        getImageInfoProgressDialog.show();

        int numNewPhotosNeedToUpload = 0;
        final ArrayList<String> imageDiskDirectory = new ArrayList<>();
        for (int i = 0; i < photoURIs.size(); ++i) {
            String path;
            if (!photoURIs.get(i).toString().contains("https://firebasestorage.googleapis.com/")) {
                path = FileHelper.getPathFromUri(context, photoURIs.get(i));
                ++numNewPhotosNeedToUpload;
            } else {
                path = photoURIs.get(i).toString();
            }
            if (!TextUtils.isEmpty(path)) {
                imageDiskDirectory.add(path);
            }
        }
        getImageInfoProgressDialog.dismiss();

        final ProgressDialog uploadImageProgressDialog = new ProgressDialog(context, R.style.LoginProgressDialog);
        uploadImageProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        uploadImageProgressDialog.setProgress(0);
        uploadImageProgressDialog.setMax(numNewPhotosNeedToUpload);
        uploadImageProgressDialog.setTitle(context.getString(R.string.activity_post_house_uploading));
        uploadImageProgressDialog.setMessage(context.getString(
                R.string.activity_post_house_uploading_success_on_each_image,
                0, numNewPhotosNeedToUpload
        ));
        uploadImageProgressDialog.setCanceledOnTouchOutside(false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            uploadImageProgressDialog.getWindow()
                    .setBackgroundDrawableResource(android.R.color.transparent);
        }
        uploadImageProgressDialog.show();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "IMG_" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "_"
                + timeStamp + "_";
        String storagePath = FirebaseAuth.getInstance().getCurrentUser().getUid() + "/"
                + toBeUpdatedNewShareHousing.getHousing().getTitle() + "_" + timeStamp + "/" + imageName;

        StorageReference mStorageReference = FirebaseStorage.getInstance()
                .getReferenceFromUrl(Constants.STORAGE_REFERENCE_URL);

        boolean isExistPhotosNeedToUpload = false;
        for (int i = 0; i < imageDiskDirectory.size(); ++i) {
            if (!imageDiskDirectory.get(i).contains("https://firebasestorage.googleapis.com/")) {
                isExistPhotosNeedToUpload = true;
                break;
            }
        }
        if (isExistPhotosNeedToUpload) {
            mNumOfPhotosUploaded = 0;
            final ArrayList<String> photoURLs = new ArrayList<>();
            for (int i = 0; i < imageDiskDirectory.size(); ++i) {
                if (!imageDiskDirectory.get(i).contains("https://firebasestorage.googleapis.com/")) {
                    File imageFile = new File(imageDiskDirectory.get(i));

                    String extension = "";
                    int j = imageFile.getAbsolutePath().lastIndexOf('.');
                    if (j > 0) {
                        extension = imageFile.getAbsolutePath().substring(j + 1);
                    }
                    StorageReference imageStorageReferencePath;
                    if (i == 0) {
                        imageStorageReferencePath = mStorageReference.child(storagePath + "Profile." + extension);
                    } else {
                        imageStorageReferencePath = mStorageReference.child(storagePath + i + "." + extension);
                    }

                    try {
                        InputStream inputStream = new FileInputStream(imageFile);
                        UploadTask uploadTask = imageStorageReferencePath.putStream(inputStream);
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                photoURLs.add(taskSnapshot.getDownloadUrl().toString());

                                uploadImageProgressDialog.setMessage(context.getString(
                                        R.string.activity_post_house_uploading_success_on_each_image,
                                        ++mNumOfPhotosUploaded,
                                        uploadImageProgressDialog.getMax()
                                ));
                                uploadImageProgressDialog.setProgress(mNumOfPhotosUploaded);

                                if (uploadImageProgressDialog.getProgress() == uploadImageProgressDialog.getMax()) {
                                    uploadImageProgressDialog.dismiss();
                                    ProgressDialog uploadHousingInfoProgressDialog = new ProgressDialog(context, R.style.LoginProgressDialog);
                                    uploadHousingInfoProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                    uploadHousingInfoProgressDialog.setIndeterminate(true);
                                    uploadHousingInfoProgressDialog.setTitle(context.getString(R.string.activity_post_house_uploading));
                                    uploadHousingInfoProgressDialog.setMessage(context.getString(R.string.activity_post_share_house_uploading_share_house_info));
                                    uploadHousingInfoProgressDialog.setCanceledOnTouchOutside(false);
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                                        uploadHousingInfoProgressDialog.getWindow()
                                                .setBackgroundDrawableResource(android.R.color.transparent);
                                    }
                                    uploadHousingInfoProgressDialog.show();

                                    Collections.sort(photoURLs, new Comparator<String>() {
                                        @Override
                                        public int compare(String a, String b) {
                                            return a.toLowerCase(Locale.US).substring(a.lastIndexOf(".") - 1, a.lastIndexOf("."))
                                                    .compareToIgnoreCase(b.toLowerCase(Locale.US).substring(b.lastIndexOf(".") - 1, b.lastIndexOf(".")));
                                        }
                                    });
                                    photoURLs.add(0, photoURLs.get(photoURLs.size() - 1));
                                    photoURLs.remove(photoURLs.size() - 1);

                                    updateShareHousingInfoOnShareSpaceServer(
                                            uploadHousingInfoProgressDialog,
                                            toBeUpdatedNewShareHousing, photoURLs,
                                            shareHousingUpdatingListener
                                    );
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                uploadImageProgressDialog.setMessage(context.getString(R.string.activity_post_house_uploading_error, e.getMessage()));
                            }
                        });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    photoURLs.add(imageDiskDirectory.get(i));
                }
            }
        } else {
            ProgressDialog uploadHousingInfoProgressDialog = new ProgressDialog(context, R.style.LoginProgressDialog);
            uploadHousingInfoProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            uploadHousingInfoProgressDialog.setIndeterminate(true);
            uploadHousingInfoProgressDialog.setTitle(context.getString(R.string.activity_post_house_uploading));
            uploadHousingInfoProgressDialog.setMessage(context.getString(R.string.activity_post_share_house_uploading_share_house_info));
            uploadHousingInfoProgressDialog.setCanceledOnTouchOutside(false);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                uploadHousingInfoProgressDialog.getWindow()
                        .setBackgroundDrawableResource(android.R.color.transparent);
            }
            uploadHousingInfoProgressDialog.show();

            updateShareHousingInfoOnShareSpaceServer(
                    uploadHousingInfoProgressDialog,
                    toBeUpdatedNewShareHousing, imageDiskDirectory,
                    shareHousingUpdatingListener
            );
        }
    }

    public static void updateShareHousing(final Context context,
                                          final ShareHousing toBeUpdatedShareHousing,
                                          final OnShareHousingUpdatingListener shareHousingUpdatingListener) {
        ProgressDialog uploadShareHousingInfoProgressDialog = new ProgressDialog(context, R.style.LoginProgressDialog);
        uploadShareHousingInfoProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        uploadShareHousingInfoProgressDialog.setIndeterminate(true);
        uploadShareHousingInfoProgressDialog.setTitle(context.getString(R.string.activity_post_house_uploading));
        uploadShareHousingInfoProgressDialog.setMessage(context.getString(R.string.activity_post_share_house_uploading_share_house_info));
        uploadShareHousingInfoProgressDialog.setCanceledOnTouchOutside(false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            uploadShareHousingInfoProgressDialog.getWindow()
                    .setBackgroundDrawableResource(android.R.color.transparent);
        }
        uploadShareHousingInfoProgressDialog.show();

        updateShareHousingInfoOnShareSpaceServer(
                uploadShareHousingInfoProgressDialog,
                toBeUpdatedShareHousing, null, shareHousingUpdatingListener
        );
    }

    private static void uploadShareHousingInfoToShareSpaceServer(
            final ProgressDialog progressDialog,
            final ShareHousing toBePostedShareHousing,
            final ArrayList<String> photoURLs,
            final OnShareHousingPostingListener shareHousingPostingListener) {

        if (photoURLs != null) {
            toBePostedShareHousing.getHousing().setPhotoURLs(photoURLs);
        }

        ShareHousingClient.postShareHousing(
                toBePostedShareHousing,
                new IShareHousingPostingCallback() {
                    @Override
                    public void onPostComplete(ShareHousing shareHousing) {
                        progressDialog.dismiss();
                        shareHousingPostingListener.onPostSuccess(shareHousing);
                    }

                    @Override
                    public void onPostFailure(Throwable t) {
                        shareHousingPostingListener.onPostFailure(t);
                    }
                }
        );
    }

    private static void updateShareHousingInfoOnShareSpaceServer(
            final ProgressDialog progressDialog,
            final ShareHousing toBeUpdatedShareHousing,
            final ArrayList<String> photoURLs,
            final OnShareHousingUpdatingListener shareHousingUpdatingListener) {

        if (photoURLs != null) {
            toBeUpdatedShareHousing.getHousing().setPhotoURLs(photoURLs);
        }

        ShareHousingClient.updateShareHousing(
                toBeUpdatedShareHousing,
                new IShareHousingUpdatingCallback() {
                    @Override
                    public void onUpdateComplete(Boolean isUpdated) {
                        progressDialog.dismiss();
                        shareHousingUpdatingListener.onUpdateComplete(isUpdated);
                    }

                    @Override
                    public void onUpdateFailure(Throwable t) {
                        shareHousingUpdatingListener.onUpdateFailure(t);
                    }
                }
        );
    }

    public static void deleteShareHousing(final Context context, final int shareHousingID,
                                          final OnShareHousingDeletingListener shareHousingDeletingListener) {
        final ProgressDialog deleteHousingProgressDialog = new ProgressDialog(context, R.style.LoginProgressDialog);
        deleteHousingProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        deleteHousingProgressDialog.setIndeterminate(true);
        deleteHousingProgressDialog.setMessage(context.getString(R.string.activity_share_housing_detail_delete_share_housing_progress_dialog_message));
        deleteHousingProgressDialog.setCanceledOnTouchOutside(false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            deleteHousingProgressDialog.getWindow()
                    .setBackgroundDrawableResource(android.R.color.transparent);
        }
        deleteHousingProgressDialog.show();

        ShareHousingClient.deleteShareHousing(shareHousingID, new IShareHousingDeletingCallback() {
            @Override
            public void onDeleteComplete(Boolean isDeleted) {
                deleteHousingProgressDialog.dismiss();
                if (isDeleted) {
                    shareHousingDeletingListener.onDeleteComplete(isDeleted);
                } else {
                    ToastHelper.showCenterToast(
                            context,
                            R.string.activity_share_housing_detail_delete_share_housing_unsuccessfully_toast_message,
                            Toast.LENGTH_LONG
                    );
                }
            }

            @Override
            public void onDeleteFailure(Throwable t) {
                shareHousingDeletingListener.onDeleteFailure(t);
            }
        });
    }
}
