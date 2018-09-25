package com.mycontacts.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.mycontacts.R;

import org.json.JSONObject;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class SocialActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 154;

    private GoogleApiClient mGoogleApiClient;
    private SignInButton btnSignIn;
    private static final String TAG = MainActivity.class.getSimpleName();

    private Button btnSignOut;
    private LinearLayout llProfileLayout;
    private ImageView imgProfilePic;
    private TextView txtName, txtEmail;
    AccessToken accessToken;
    CallbackManager callbackManager;
    LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        try {
            btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
            btnSignOut = (Button) findViewById(R.id.btn_sign_out);

            llProfileLayout = (LinearLayout) findViewById(R.id.llProfile);
            imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
            txtName = (TextView) findViewById(R.id.txtName);
            txtEmail = (TextView) findViewById(R.id.txtEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // accessToken of facebook
            accessToken = AccessToken.getCurrentAccessToken();
            callbackManager = CallbackManager.Factory.create();

            // implement GoogleSignInOptions
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, SocialActivity.this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Customizing G+ button
        btnSignIn.setSize(SignInButton.SIZE_STANDARD);
        // onClick on Button
        btnSignIn.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);

        try {
            // Declare facebook button
            loginButton = (LoginButton) findViewById(R.id.activity_main_btn_login);
            // read permission
            loginButton.setReadPermissions(Arrays.asList(new String[]{"email", "user_birthday", "user_hometown"}));
            loginButton.setTextSize(15);
            // check token
            if (accessToken != null) {
                // Load Data
                getProfileData();
            } else {
                llProfileLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "User login successfully");
                try {
                    // declare bliank
                    txtName.setText("");
                    txtEmail.setText("");
                    btnSignIn.setVisibility(View.GONE);
                    // Load Data
                    getProfileData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancel() {
                // App code
                Log.d(TAG, "User cancel login");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d(TAG, "Problem for login");
            }
        });

        // facebook AccessTokenTracker
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                try {
                    if (currentAccessToken == null) {
                        Log.d(TAG, "User logged out successfully");
                        llProfileLayout.setVisibility(View.GONE);
                        btnSignIn.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };


//         printHashKey(this); //generate  hash key for facebook
    }

    // Google Sign In Method
    private void signIn() {
        try {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btn_sign_in:
                txtName.setText("");
                txtEmail.setText("");
                signIn();
                break;

            case R.id.btn_sign_out:
                signOut();
                break;

        }
    }

    // Google Sign out Method
    private void signOut() {
        try {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            updateUI(false);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            // fecebook callback method
            callbackManager.onActivityResult(requestCode, resultCode, data);

            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // google handle
    private void updateUI(boolean isSignedIn) {
        try {
            if (isSignedIn) {
                btnSignIn.setVisibility(View.GONE);
                loginButton.setVisibility(View.GONE);
                btnSignOut.setVisibility(View.VISIBLE);
                llProfileLayout.setVisibility(View.VISIBLE);
            } else {
                btnSignIn.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.VISIBLE);
                btnSignOut.setVisibility(View.GONE);
                llProfileLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Google Account details
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        try {
            if (result.isSuccess()) {
                // Signed in successfully, show authenticated UI.
                GoogleSignInAccount acct = result.getSignInAccount();

                Log.e(TAG, "display name: " + acct.getDisplayName());
                // Get data from google
                String personName = acct.getDisplayName();
                String personPhotoUrl = acct.getPhotoUrl().toString();
                String email = acct.getEmail();

                Log.e(TAG, "Name: " + personName + ", email: " + email
                        + ", Image: " + personPhotoUrl);
                // set data to view
                txtName.setText(personName);
                txtEmail.setText(email);
                Glide.with(getApplicationContext()).load(personPhotoUrl)
                        .thumbnail(0.5f)
                        .crossFade()
                        //  .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imgProfilePic);

                updateUI(true);
            } else {
                // Signed out, show unauthenticated UI.
                updateUI(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // get facebook data
    public void getProfileData() {
        try {
            accessToken = AccessToken.getCurrentAccessToken();
            llProfileLayout.setVisibility(View.VISIBLE);
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            Log.d(TAG, "Graph Object :" + object);
                            try {
                                txtName.setVisibility(View.VISIBLE);
                                txtEmail.setVisibility(View.VISIBLE);
                                imgProfilePic.setVisibility(View.VISIBLE);
                                // get facebook data
                                String name = object.getString("name");
                                String email = object.getString("email");
                                String userID = object.getString("id");

                                // set data
                                txtName.setText(name);
                                txtEmail.setText(email);

                                btnSignOut.setVisibility(View.GONE);
                                btnSignIn.setVisibility(View.GONE);
                                llProfileLayout.setVisibility(View.VISIBLE);
                                //Load facebook image
                                URL imageURL = new URL("https://graph.facebook.com/" + userID + "/picture?type=large");
                                Log.d(TAG, "Graph Object1 :" + imageURL);

                                Glide.with(getApplicationContext()).load("https://graph.facebook.com/" + userID + "/picture?type=large")
                                        .thumbnail(0.5f)
                                        .crossFade()
                                        //  .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(imgProfilePic);

                                Log.d(TAG, "Name :" + name);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,link,birthday,gender,email");
            request.setParameters(parameters);
            request.executeAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void printHashKey(Context pContext) {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("Hash", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("Hash", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("Hash", "printHashKey()", e);
        }
    }
}
