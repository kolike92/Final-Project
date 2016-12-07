/**
 * Class: InnerActivity
 * @author NOGE
 * Superclass: AppCompatActivity
 *
 * Base class for all activites except login to display menu on every page
 */


package com.BUddy.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public abstract class InnerActivity extends AppCompatActivity {

    CallbackManager callbackManager;

    //all activites must have a user object
    BuddyUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set up facebook SDK and callback manager
        callbackManager = CallbackManager.Factory.create();
        FacebookSdk.sdkInitialize(getApplicationContext());

        //when manager is called, log user in on FB
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        //successful login, get FB id and add to DB
                        AccessToken at = loginResult.getAccessToken();
                        String fbid = at.getUserId();
                        if(user != null) user.setFbId(fbid);
                        DatabaseReference dbUser =
                                FirebaseDatabase.getInstance().getReference("users/" + user.getFirebaseId()).child("fbId");
                        dbUser.setValue(fbid);

                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {

                    }
                });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.buddy_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.facebookConnect)
        {
            //menu option: facebook connect (call login manager)
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
        }
        if(id == R.id.homeButton) {
            //menu option: home
            Intent home = new Intent(getApplicationContext(), HomeActivity.class);
            home.putExtra(StaticConstants.USER_KEY, user);
            startActivity(home);
        }
        if(id == R.id.topLikes){
            //menu option: stats page
            Intent likes = new Intent(getApplicationContext(), EventLikes.class);
            likes.putExtra(StaticConstants.USER_KEY, user);
            startActivity(likes);
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //forward on activity result to FB callback manager in case it was an FB login
        super.onActivityResult(requestCode,resultCode,data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }


}

