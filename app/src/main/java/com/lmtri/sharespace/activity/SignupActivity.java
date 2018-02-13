package com.lmtri.sharespace.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.lmtri.sharespace.R;
import com.lmtri.sharespace.ShareSpaceApplication;
import com.lmtri.sharespace.api.model.User;
import com.lmtri.sharespace.api.service.RetrofitClient;
import com.lmtri.sharespace.api.service.user.IRegisterCallback;
import com.lmtri.sharespace.api.service.user.UserClient;
import com.lmtri.sharespace.customview.CustomEditText;
import com.lmtri.sharespace.helper.BitmapLoader;
import com.lmtri.sharespace.helper.BlurBuilder;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.KeyboardHelper;
import com.lmtri.sharespace.helper.busevent.SigninEvent;
import com.lmtri.sharespace.helper.busevent.SignoutEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SignupActivity extends AppCompatActivity {

    public static final String TAG = SignupActivity.class.getSimpleName();

    // Firebase Authentication.
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // UI references.
    private ScrollView mScrollViewScreen;
    private CustomEditText mLastNameView;
    private CustomEditText mFirstNameView;
    private CustomEditText mEmailView;
    private CustomEditText mPasswordView;
    private CustomEditText mConfirmPasswordView;
    private CustomEditText mDOBView;
    private View mDOBDummyView;
    private Spinner mGenderSpinner;
    private CustomEditText mPhoneNumberView;
    private Button mSignupButton;
    private TextView mLoginLink;

    private Calendar mCalendar;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Firebase Authentication.
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in.
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    User currentUser = new User(
                            mFirstNameView.getText().toString(),
                            mLastNameView.getText().toString(),
                            mEmailView.getText().toString(),
                            mDOBView.getText().toString(),
                            mPhoneNumberView.getText().toString(),
                            mGenderSpinner.getSelectedItem().toString(), 0,
                            FirebaseInstanceId.getInstance().getToken()
                    );

                    // Register with Share Space Server.
                    UserClient.register(
                            currentUser,
                            new IRegisterCallback() {
                                @Override
                                public void onRegisterSuccess(User registeredUser) {
                                    mProgressDialog.dismiss();
                                    mSignupButton.setEnabled(true);

                                    if (registeredUser != null) {
                                        Constants.CURRENT_USER = registeredUser;
                                        Constants.CONTACT_NAME
                                                = !TextUtils.isEmpty(registeredUser.getLastName())
                                                ? registeredUser.getLastName() + " " + registeredUser.getFirstName()
                                                : registeredUser.getFirstName();
                                        Constants.CONTACT_NUMBER = registeredUser.getPhoneNumber();
                                        Constants.CONTACT_EMAIL = registeredUser.getEmail();

                                        ShareSpaceApplication.BUS.post(new SigninEvent(registeredUser));

                                        // User signed up successfully and logged in.
                                        setResult(RESULT_OK);
                                        finish();
                                    } else {
                                        mAuth.signOut();
                                        ShareSpaceApplication.BUS.post(new SignoutEvent());
                                        FirebaseAuth.getInstance().getCurrentUser().delete()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        // TODO: 11/28/2017 Add onCompleteListener after user's account deleted.
                                                    }
                                                });
                                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(SignupActivity.this);
                                    }
                                }

                                @Override
                                public void onRegisterFailure(Throwable t) {
                                    RetrofitClient.showShareSpaceServerConnectionErrorDialog(SignupActivity.this, t);
                                }
                            }
                    );
                } else {
                    // User is signed out.
                    Log.d(TAG, "onAuthStateChanged:signed_out");

                    Constants.CURRENT_USER = null;
                }
            }
        };

        setContentView(R.layout.activity_signup);

        // Set up the signup screen.
        mScrollViewScreen = (ScrollView) findViewById(R.id.signup_screen);
        mScrollViewScreen.post(new Runnable() {
            @Override
            public void run() {
                Bitmap blurredLoginBackground = BlurBuilder.blur(SignupActivity.this,
                        BitmapLoader.decodeSampledBitmapFromResource(getResources(), R.drawable.login_background_2,
                                mScrollViewScreen.getMeasuredWidth()/3, mScrollViewScreen.getMeasuredHeight()/3));
                mScrollViewScreen.setBackground(new BitmapDrawable(getResources(), blurredLoginBackground));
            }
        });
        // Remove auto-focus from all Edit Texts.
        mScrollViewScreen.requestFocus();

        // Set up the signup form.
        mLastNameView = (CustomEditText) findViewById(R.id.last_name);
        mLastNameView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && mLastNameView.getError() != null) {
                    mLastNameView.setError(null);
                }
            }
        });
        mFirstNameView = (CustomEditText) findViewById(R.id.first_name);
        mFirstNameView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && mFirstNameView.getError() != null) {
                    mFirstNameView.setError(null);
                }
            }
        });
        mEmailView = (CustomEditText) findViewById(R.id.email);
        mEmailView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && mEmailView.getError() != null) {
                    mEmailView.setError(null);
                }
            }
        });
        mPasswordView = (CustomEditText) findViewById(R.id.password);
        mPasswordView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && mPasswordView.getError() != null) {
                    mPasswordView.setError(null);
                }
            }
        });
        mConfirmPasswordView = (CustomEditText) findViewById(R.id.confirm_password);
        mConfirmPasswordView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && mConfirmPasswordView.getError() != null) {
                    mConfirmPasswordView.setError(null);
                }
            }
        });
        mPhoneNumberView = (CustomEditText) findViewById(R.id.phone_number);
        mPhoneNumberView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && mPhoneNumberView.getError() != null) {
                    mPhoneNumberView.setError(null);
                } else {
                    if (!TextUtils.isEmpty(mDOBView.getText().toString())) {
                        mPhoneNumberView.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    }
                }
            }
        });
        mPhoneNumberView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    KeyboardHelper.hideSoftKeyboard(SignupActivity.this, false);
                    mDOBDummyView.requestFocus();
                    return true;
                }
                return false;
            }
        });

        // Set up DOB picker.
        mDOBView = (CustomEditText) findViewById(R.id.date_of_birth);
        mCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, month);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDOBView();
            }
        };
        final DatePickerDialog datePickerDialog =
                new DatePickerDialog(this, onDateSetListener,
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setCancelable(false);
        datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mDOBDummyView.requestFocus();
            }
        });
        mDOBView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDOBView.setError(null);
                if (!datePickerDialog.isShowing()) {
                    datePickerDialog.show();
                }
            }
        });
        mDOBView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && mDOBView.getError() == null) {
                    KeyboardHelper.hideSoftKeyboard(SignupActivity.this, false);
                    mDOBView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mDOBView.callOnClick();
                        }
                    }, 900);
                }
            }
        });
        mDOBView.setInputType(InputType.TYPE_NULL);

        mDOBDummyView = findViewById(R.id.dob_dummy_view);

        // Set up Gender Spinner.
        mGenderSpinner = (Spinner) findViewById(R.id.gender_spinner);
        mGenderSpinner.setSelection(0);

        mSignupButton = (Button) findViewById(R.id.signup_button);
        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attempSignup();
            }
        });

        mLoginLink = (TextView) findViewById(R.id.login_link);
        mLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.stay_still, R.anim.push_right_out);
            }
        });

        mProgressDialog = new ProgressDialog(SignupActivity.this,
                R.style.LoginProgressDialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getString(R.string.creating_account));
        mProgressDialog.setCanceledOnTouchOutside(false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mProgressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onPause() {
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        super.onStop();
    }

    private void updateDOBView() {
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.CANADA);

        mDOBView.setText(simpleDateFormat.format(mCalendar.getTime()));
    }

    private void attempSignup() {
        // Hide Soft Keyboard.
        KeyboardHelper.hideSoftKeyboard(this, true);

        // Disable Signup Button to perform Authentication.
        mSignupButton.setEnabled(false);

        // Reset errors.
        mLastNameView.setError(null);
        mFirstNameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mConfirmPasswordView.setError(null);
        mDOBView.setError(null);
        mPhoneNumberView.setError(null);

        // Store values at the time of the signup attempt.
        final String firstName = mFirstNameView.getText().toString();
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();
        final String phoneNumber = mPhoneNumberView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid date of birth.
        if (Calendar.getInstance().get(Calendar.YEAR) - mCalendar.get(Calendar.YEAR) < Constants.REQUIRED_SIGNUP_AGE) {
            mDOBView.setError(getString(R.string.error_not_enough_age));
            focusView = mDOBView;
            cancel = true;
        }

        // Check if Phone Number is Empty.
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberView.setError(getString(R.string.error_field_required));
            focusView = mPhoneNumberView;
            cancel = true;
        }

        // Check if Confirm Password match Password.
        if (!mConfirmPasswordView.getText().toString().equals(mPasswordView.getText().toString())) {
            mConfirmPasswordView.setError(getString(R.string.error_not_match_password));
            focusView = mConfirmPasswordView;
            cancel = true;
        }

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_signup_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid first name.
        if (TextUtils.isEmpty(firstName)) {
            mFirstNameView.setError(getString(R.string.error_field_required));
            focusView = mFirstNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt signup and focus the first
            // form field with an error.
            focusView.requestFocus();
            // Re-enable Signup Button.
            mSignupButton.setEnabled(true);
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user signup attempt.
            mProgressDialog.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                            // This listener will execute after mAuthListener.

                            // Signup failed.
                            if (!task.isSuccessful()) {
                                mProgressDialog.dismiss();
                                mSignupButton.setEnabled(true);

                                Log.w(TAG, "createUserWithEmail:failed", task.getException());
                                Toast toast = Toast.makeText(
                                        getApplicationContext(),
                                        getString(R.string.error_signup_failed) + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT
                                );
                                TextView message = (TextView) toast.getView()
                                        .findViewById(android.R.id.message);
                                if (message != null) {
                                    message.setGravity(Gravity.CENTER);
                                    message.setLineSpacing(
                                            getResources().getDimensionPixelSize(R.dimen.activity_login_error_login_signup_failed_message_line_spacing),
                                            1.0f
                                    );
                                }
                                toast.show();
                                return;
                            } else {
                                // Signed up successfully.
                                // Don't need to attemp to log in
                                // due to auto logging in feature of Firebase
                                // after a successful account creation.
//                                attempLogin(email, password);
                            }
                        }
                    });
        }
    }

    private void attempLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // Log in failed.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast toast = Toast.makeText(
                                    getApplicationContext(),
                                    getString(R.string.error_login_failed),
                                    Toast.LENGTH_SHORT
                            );
                            TextView message = (TextView) toast.getView()
                                    .findViewById(android.R.id.message);
                            if (message != null) {
                                message.setGravity(Gravity.CENTER);
                                message.setLineSpacing(
                                        getResources().getDimensionPixelSize(R.dimen.activity_login_error_login_signup_failed_message_line_spacing),
                                        1.0f
                                );
                            }
                            toast.show();
                            return;
                        }
                    }
                });
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= Constants.REQUIRED_MIN_PASSWORD_LENGTH;
    }

    @Override
    public void onBackPressed() {
        // Reset errors.
        mLastNameView.setError(null);
        mFirstNameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mConfirmPasswordView.setError(null);
        mDOBView.setError(null);
        mPhoneNumberView.setError(null);

        super.onBackPressed();
        overridePendingTransition(R.anim.stay_still, R.anim.push_right_out);
    }
}