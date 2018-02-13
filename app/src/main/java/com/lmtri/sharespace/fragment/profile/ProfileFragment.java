package com.lmtri.sharespace.fragment.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.lmtri.sharespace.R;
import com.lmtri.sharespace.ShareSpaceApplication;
import com.lmtri.sharespace.activity.LoginActivity;
import com.lmtri.sharespace.activity.profiletab.historynote.HistoryNoteActivity;
import com.lmtri.sharespace.activity.profiletab.historyphoto.HistoryPhotoActivity;
import com.lmtri.sharespace.activity.profiletab.historypost.HistoryPostActivity;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.busevent.SigninEvent;
import com.lmtri.sharespace.helper.busevent.SignoutEvent;
import com.lmtri.sharespace.helper.busevent.housing.HistoryPostSaveNotePhotoActivityPostShareOfExistHousingEvent;
import com.squareup.otto.Subscribe;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnProfileFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    public static final String TAG = ProfileFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView mUsername;
    private TextView mEmail;

    private LinearLayout mHistoryPost;
    private LinearLayout mHistoryNote;
    private LinearLayout mHistoryPhoto;

    private LinearLayout mSigninSignoutLayout;
    private ImageView mSigninSignoutImage;
    private TextView mSigninSignoutText;

    private OnProfileFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        ShareSpaceApplication.BUS.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mUsername = (TextView) view.findViewById(R.id.fragment_profile_username);
        mEmail = (TextView) view.findViewById(R.id.fragment_profile_email);

        mHistoryPost = (LinearLayout) view.findViewById(R.id.fragment_profile_history_post_layout);
        mHistoryPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.CURRENT_USER != null) {
                    Intent intent = new Intent(getContext(), HistoryPostActivity.class);
                    startActivityForResult(intent, Constants.START_ACTIVITY_HISTORY_POST_SAVE_NOTE_PHOTO_REQUEST);
                } else {
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.activity_housing_detail_login_required_feature_dialog_title)
                            .setMessage(R.string.activity_housing_detail_login_required_feature_dialog_message)
                            .setPositiveButton(R.string.activity_housing_detail_login_required_feature_dialog_positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getContext(), LoginActivity.class);
                                    startActivityForResult(intent, Constants.START_ACTIVITY_LOGIN_REQUEST);
                                }
                            })
                            .setNegativeButton(R.string.activity_housing_detail_login_required_feature_dialog_negative, null)
                            .show();
                }
            }
        });

        mHistoryNote = (LinearLayout) view.findViewById(R.id.fragment_profile_history_note_layout);
        mHistoryNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.CURRENT_USER != null) {
                    Intent intent = new Intent(getContext(), HistoryNoteActivity.class);
                    startActivityForResult(intent, Constants.START_ACTIVITY_HISTORY_POST_SAVE_NOTE_PHOTO_REQUEST);
                } else {
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.activity_housing_detail_login_required_feature_dialog_title)
                            .setMessage(R.string.activity_housing_detail_login_required_feature_dialog_message)
                            .setPositiveButton(R.string.activity_housing_detail_login_required_feature_dialog_positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getContext(), LoginActivity.class);
                                    startActivityForResult(intent, Constants.START_ACTIVITY_LOGIN_REQUEST);
                                }
                            })
                            .setNegativeButton(R.string.activity_housing_detail_login_required_feature_dialog_negative, null)
                            .show();
                }
            }
        });

        mHistoryPhoto = (LinearLayout) view.findViewById(R.id.fragment_profile_history_photo_layout);
        mHistoryPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.CURRENT_USER != null) {
                    Intent intent = new Intent(getContext(), HistoryPhotoActivity.class);
                    startActivityForResult(intent, Constants.START_ACTIVITY_HISTORY_POST_SAVE_NOTE_PHOTO_REQUEST);
                } else {
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.activity_housing_detail_login_required_feature_dialog_title)
                            .setMessage(R.string.activity_housing_detail_login_required_feature_dialog_message)
                            .setPositiveButton(R.string.activity_housing_detail_login_required_feature_dialog_positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getContext(), LoginActivity.class);
                                    startActivityForResult(intent, Constants.START_ACTIVITY_LOGIN_REQUEST);
                                }
                            })
                            .setNegativeButton(R.string.activity_housing_detail_login_required_feature_dialog_negative, null)
                            .show();
                }
            }
        });

        mSigninSignoutLayout = (LinearLayout) view.findViewById(R.id.fragment_profile_signin_signout_layout);
        mSigninSignoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.fragment_profile_signout_dialog_title)
                            .setMessage(R.string.fragment_profile_signout_dialog_message)
                            .setPositiveButton(R.string.fragment_profile_signout_dialog_positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseAuth.getInstance().signOut();
                                    Constants.CURRENT_USER = null;
                                    ShareSpaceApplication.BUS.post(new SignoutEvent());
                                }
                            })
                            .setNegativeButton(R.string.fragment_profile_signout_dialog_negative, null)
                            .show();
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, Constants.START_ACTIVITY_LOGIN_REQUEST);
                }
            }
        });
        mSigninSignoutImage = (ImageView) view.findViewById(R.id.fragment_profile_signin_signout_image);
        mSigninSignoutText = (TextView) view.findViewById(R.id.fragment_profile_signin_signout_text);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnProfileFragmentInteractionListener) {
            mListener = (OnProfileFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSavedHousePostListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.START_ACTIVITY_HISTORY_POST_SAVE_NOTE_PHOTO_REQUEST) {
            if (resultCode == getActivity().RESULT_OK) {
                ShareSpaceApplication.BUS.post(new HistoryPostSaveNotePhotoActivityPostShareOfExistHousingEvent());
            }
        }
    }

    @Subscribe
    public void userSignin(SigninEvent event) {
        mSigninSignoutImage.setImageResource(R.drawable.ic_signout);
        mSigninSignoutText.setText(R.string.fragment_profile_signout);

        if (!TextUtils.isEmpty(Constants.CURRENT_USER.getLastName())) {
            mUsername.setText(
                    Constants.CURRENT_USER.getLastName() + " " +
                            Constants.CURRENT_USER.getFirstName()
            );
        } else {
            mUsername.setText(
                    getString(R.string.activity_housing_detail_house_owner)
                            + Constants.CURRENT_USER.getFirstName()
            );
        }
        if (!TextUtils.isEmpty(Constants.CURRENT_USER.getEmail())) {
            mEmail.setText(Constants.CURRENT_USER.getEmail());
        }
    }

    @Subscribe
    public void userSignout(SignoutEvent event) {
        mSigninSignoutImage.setImageResource(R.drawable.ic_signin);
        mSigninSignoutText.setText(R.string.fragment_profile_signin);

        mUsername.setText("User's Name");
        mEmail.setText("User's Email");
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnProfileFragmentInteractionListener {
        // TODO: Update argument type and name
        void onProfileFragmentInteraction(Uri uri);
    }
}
