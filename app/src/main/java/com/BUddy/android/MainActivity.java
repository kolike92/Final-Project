/**
 * Class: MainActivity
 * @author NOGE
 * Superclass: AppCompatActivity
 * Implements: GoogleApiClient.OnConnectionFailedListener
 */

package com.BUddy.android;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity implements  GoogleApiClient.OnConnectionFailedListener{
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private Button btnBULogin;
    private static final int RC_SIGN_IN = 9001; //Google activity request code
    private TextView tvError;
    private ImageView logo;

    private String email;
    private BuddyUser user;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbRef;
    //test
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //if we somehow got shut down in the middle, restore the user and the email
        if(savedInstanceState != null)
        {
            email = savedInstanceState.getString(StaticConstants.EMAIL_KEY);
            user = (BuddyUser) savedInstanceState.getParcelable(StaticConstants.USER_KEY);
        }

        //initialize Facebook SDK (required)
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        //make sure app has a valid signature
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.BUddy.android",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        //set up UI
        logo = (ImageView) findViewById(R.id.logo);
        logo.setImageResource(R.drawable.my_logo);

        callbackManager = CallbackManager.Factory.create();

        //Facebook button. For now, we only need the user email
        loginButton = (LoginButton) findViewById((R.id.login_button));
        loginButton.setReadPermissions("email");

        //Login with google/Kerberos
        btnBULogin = (Button) findViewById(R.id.btnLogin);

        tvError = (TextView) findViewById(R.id.tvError);

        //Like FB, set up permissions. Only  need email
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //get DB handle to user table
        firebaseDatabase = FirebaseDatabase.getInstance();
        dbRef = firebaseDatabase.getReference("users");

        //Sign in using google
        btnBULogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        //Sign in using Facebook
        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        //check if user has connected with Facebook (i.e. has an FbId in the db)
                        AccessToken at = loginResult.getAccessToken();
                        String fbid = at.getUserId();
                        Query q = dbRef.orderByChild("fbId").equalTo(fbid);
                        q.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(!dataSnapshot.exists())
                                {
                                    //No matching entry. Don't log in
                                    tvError.setText(getString(R.string.noFacebook));
                                    LoginManager.getInstance().logOut();
                                }
                                else {
                                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                                    for(DataSnapshot d : children) {
                                        //There should only be one matching child user
                                        user = d.getValue(BuddyUser.class);
                                        //Older users may not have firebase ids set. Set them here
                                        if(user.getFirebaseId() == null || user.getFirebaseId().equals(""))
                                        {
                                            user.setFirebaseId(d.getKey());
                                            DatabaseReference userRef = dbRef.child(d.getKey());
                                            //save updated user to DB
                                            userRef.setValue(user, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                    Log.e(StaticConstants.TAG, "Error writing to DB" + databaseError.getMessage());
                                                }
                                            });

                                        }
                                    }
                                    Intent home = new Intent(getBaseContext(), HomeActivity.class);
                                    Bundle b = new Bundle();
                                    //pass the user to the activity
                                    b.putParcelable(StaticConstants.USER_KEY,user);

                                    home.putExtras(b);
                                    startActivity(home);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                                tvError.setText(getString(R.string.unknownSignInError) + databaseError.getMessage());
                            }
                        }); //end update user
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        tvError.setText(getString(R.string.unknownSignInError) + exception.getMessage());
                    }
                }); //end register callback
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        else //check to see if result is from Facebook, if so, use facebook login
        {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void handleSignInResult(GoogleSignInResult result) {
        if(!result.isSuccess())
        {
            tvError.setText(getString(R.string.unknownSignInError));
        }
        else {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            email = acct.getEmail();
            if (!email.endsWith("@bu.edu")) {
                //Not a BU email --> didn't go through Kerberos
                tvError.setText(getString(R.string.errorNotBU));
                if (mGoogleApiClient.isConnected()) {
                    //if user connected to google with a non-BU address, sign them out again
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                            new ResultCallback<Status>() {
                                @Override
                                public void onResult(Status status) {
                                    Log.d("BUDDY", "User signed out successfully");
                                }
                            });
                }
                return;
            }

            //Query DB to see if returning or new user
            Query q = dbRef.orderByChild("email").equalTo(email);
            q.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists())
                    {
                        //new user
                        user = new BuddyUser();
                        user.setEmail(email);
                        DatabaseReference eventRef = dbRef.push();
                        user.setFirebaseId(eventRef.getKey());
                        //add new user to db
                        eventRef.setValue(user);
                        Intent home = new Intent(getApplicationContext(), HomeActivity.class);
                        home.putExtra(StaticConstants.USER_KEY,user);
                        //go to home page
                        startActivity(home);
                    }
                    else
                    {
                        Iterable<DataSnapshot> childSnap = dataSnapshot.getChildren();
                        for(DataSnapshot d: childSnap) {
                            //there should only be one data item that matches, so this loop should
                            //only go through once
                            user = d.getValue(BuddyUser.class);
                            if(user.getFirebaseId() == null || user.getFirebaseId().equals(""))
                            {
                                //set firebase id if not already set
                                user.setFirebaseId(d.getKey());
                                DatabaseReference userRef = dbRef.child(d.getKey());
                                userRef.setValue(user, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    }
                                });

                            }

                        }
                        Intent home = new Intent(getApplicationContext(), HomeActivity.class);
                        home.putExtra(StaticConstants.USER_KEY,user);

                        //go to home page
                        startActivity(home);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    tvError.setText(getString(R.string.unknownSignInError));
                }
            });

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        tvError.setText(getString(R.string.unknownSignInError));
    }

    //If we get stopped in the middle, save current state of email and user
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putString(StaticConstants.EMAIL_KEY,email);
        savedInstanceState.putParcelable(StaticConstants.USER_KEY,user);
    }
}






























