package com.lmtri.sharespace.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.lmtri.sharespace.R;
import com.lmtri.sharespace.ShareSpaceApplication;
import com.lmtri.sharespace.api.model.User;
import com.lmtri.sharespace.api.service.RetrofitClient;
import com.lmtri.sharespace.api.service.user.IGetUserInfoCallback;
import com.lmtri.sharespace.api.service.user.UserClient;
import com.lmtri.sharespace.customview.CustomEditText;
import com.lmtri.sharespace.helper.BitmapLoader;
import com.lmtri.sharespace.helper.BlurBuilder;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.KeyboardHelper;
import com.lmtri.sharespace.helper.busevent.SigninEvent;
import com.lmtri.sharespace.helper.busevent.SignoutEvent;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();

    // Firebase Authentication.
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // UI references.
    private ScrollView mScrollViewScreen;

    private View mDummyView;
    private CustomEditText mEmailView;
    private CustomEditText mPasswordView;

    private Button mSigninButton;
    private TextView mSignupLink;

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

                    try {
                        UserClient.getUserInfo(
                                FirebaseInstanceId.getInstance().getToken(),
                                new IGetUserInfoCallback() {
                                    @Override
                                    public void onGetUserInfoSuccess(User user) {
                                        mProgressDialog.dismiss();
                                        mSigninButton.setEnabled(true);

                                        if (user != null) {
                                            Constants.CURRENT_USER = user;
                                            Constants.CONTACT_NAME
                                                    = !TextUtils.isEmpty(user.getLastName())
                                                    ? user.getLastName() + " " + user.getFirstName()
                                                    : user.getFirstName();
                                            Constants.CONTACT_NUMBER = user.getPhoneNumber();
                                            Constants.CONTACT_EMAIL = user.getEmail();

                                            ShareSpaceApplication.BUS.post(new SigninEvent(user));

                                            setResult(RESULT_OK);
                                            finish();
                                        } else {
                                            mAuth.signOut();
                                            ShareSpaceApplication.BUS.post(new SignoutEvent());
                                            RetrofitClient.showShareSpaceServerConnectionErrorDialog(LoginActivity.this);
                                        }
                                    }

                                    @Override
                                    public void onGetUserInfoFailure(Throwable t) {
                                        RetrofitClient.showShareSpaceServerConnectionErrorDialog(LoginActivity.this, t);
                                    }
                                }
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // getIdToken(boolean b) -> Whether forcing User's Token
                    // to be refreshed (even if it hasn't expired yet) or not.
                    user.getIdToken(false).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                        @Override
                        public void onSuccess(GetTokenResult getTokenResult) {
                            String idToken = getTokenResult.getToken();
                            Log.d(TAG, "onAuthStateChanged: ID Token: " + idToken);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    // User is signed out.
                    Log.d(TAG, "onAuthStateChanged:signed_out");

                    mProgressDialog.dismiss();
                    mSigninButton.setEnabled(true);
                    Constants.CURRENT_USER = null;
                }
            }
        };

        setContentView(R.layout.activity_login);

        // Set up the login screen.
        mScrollViewScreen = (ScrollView) findViewById(R.id.login_screen);
        mScrollViewScreen.post(new Runnable() {
            @Override
            public void run() {
                Bitmap blurredLoginBackground = BlurBuilder.blur(LoginActivity.this,
                        BitmapLoader.decodeSampledBitmapFromResource(getResources(), R.drawable.login_background_2,
                                mScrollViewScreen.getMeasuredWidth()/3, mScrollViewScreen.getMeasuredHeight()/3));
                mScrollViewScreen.setBackground(new BitmapDrawable(getResources(), blurredLoginBackground));
            }
        });
        // Remove auto-focus from all Edit Texts.
        mScrollViewScreen.requestFocus();

        mDummyView = findViewById(R.id.dummy_view);

        // Set up the login form.
        mEmailView = (CustomEditText) findViewById(R.id.email);
        mEmailView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (mEmailView.getError() != null) {
                        mEmailView.setError(null);
                    }
                    mDummyView.requestFocus();
                }
            }
        });
        mPasswordView = (CustomEditText) findViewById(R.id.password);
        mPasswordView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (mPasswordView.getError() != null) {
                        mPasswordView.setError(null);
                    }
                    mDummyView.requestFocus();
                }
            }
        });
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mSigninButton = (Button) findViewById(R.id.signin_button);
        mSigninButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mSignupLink = (TextView) findViewById(R.id.signup_link);
        mSignupLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset errors.
                mEmailView.setError(null);
                mPasswordView.setError(null);

                // Start Signup activity.
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, Constants.START_ACTIVITY_SIGNUP_REQUEST);
                overridePendingTransition(R.anim.push_left_in, R.anim.stay_still);
            }
        });

        mProgressDialog = new ProgressDialog(LoginActivity.this,
                R.style.LoginProgressDialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getString(R.string.sign_in_authenticating));
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

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Hide Soft Keyboard.
        KeyboardHelper.hideSoftKeyboard(this, true);

        // Disable Signin Button to perform Authentication.
        mSigninButton.setEnabled(false);

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        else if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
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
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            // Re-enable Signin Button.
            mSigninButton.setEnabled(true);
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mProgressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                            // This listener will execute after mAuthListener.

                            // Login failed.
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

                                mProgressDialog.dismiss();
                                mSigninButton.setEnabled(true);
                                return;
                            }
                        }
                    });
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= Constants.REQUIRED_MIN_PASSWORD_LENGTH;
    }

    @Override
    public void onBackPressed() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        // Disable going back to the MainActivity.
//        moveTaskToBack(true);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.START_ACTIVITY_SIGNUP_REQUEST) {
            if (resultCode == RESULT_OK) {
                // User signed up successfully and logged in.
                setResult(RESULT_OK);
                finish();
            }
        }
    }
}

