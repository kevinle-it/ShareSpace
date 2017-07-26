package com.lmtri.sharespace.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lmtri.sharespace.R;
import com.lmtri.sharespace.customview.CustomEditText;
import com.lmtri.sharespace.helper.BitmapLoader;
import com.lmtri.sharespace.helper.BlurBuilder;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.KeyboardHelper;

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
    private CustomEditText mEmailView;
    private CustomEditText mPasswordView;
    private CustomEditText mConfirmPasswordView;
    private CustomEditText mDOBView;
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

                    // User signed up successfully and logged in.
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    // User is signed out.
                    Log.d(TAG, "onAuthStateChanged:signed_out");
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

        // Set up the signup form.
        mEmailView = (CustomEditText) findViewById(R.id.email);
        mPasswordView = (CustomEditText) findViewById(R.id.password);
        mConfirmPasswordView = (CustomEditText) findViewById(R.id.confirm_password);

        // Set up DOB picker.
        mDOBView = (CustomEditText) findViewById(R.id.date_of_birth);
        mCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, month);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDOBView();
            }
        };
        mDOBView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(SignupActivity.this, onDateSetListener,
                        mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        mDOBView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    KeyboardHelper.hideSoftKeyboard(SignupActivity.this);
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
        mProgressDialog.setMessage("Creating account...");
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
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void updateDOBView() {
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.CANADA);

        mDOBView.setText(simpleDateFormat.format(mCalendar.getTime()));;
    }

    private void attempSignup() {
        // Hide Soft Keyboard.
        KeyboardHelper.hideSoftKeyboard(this);

        // Disable Signup Button to perform Authentication.
        mSignupButton.setEnabled(false);

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mConfirmPasswordView.setError(null);
        mDOBView.setError(null);

        // Store values at the time of the signup attempt.
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid date of birth.
        if (Calendar.getInstance().get(Calendar.YEAR) - mCalendar.get(Calendar.YEAR) < Constants.REQUIRED_SIGNUP_AGE) {
            mDOBView.setError(getString(R.string.error_not_enough_age));
            focusView = mDOBView;
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
        }
        else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
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

                            mProgressDialog.dismiss();
                            mSignupButton.setEnabled(true);

                            // Signup failed.
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "createUserWithEmail:failed", task.getException());
                                Toast.makeText(getApplicationContext(), getString(R.string.error_signup_failed), Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                // Signed up successfully.
                                attempLogin(email, password);
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
                            Toast.makeText(getApplicationContext(), getString(R.string.error_login_failed), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= Constants.REQUIRED_PASSWORD_LENGTH;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stay_still, R.anim.push_right_out);
    }
}