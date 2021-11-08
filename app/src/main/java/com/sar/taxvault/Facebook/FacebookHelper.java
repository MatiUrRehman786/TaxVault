package com.sar.taxvault.Facebook;

import android.app.Activity;
import android.content.Intent;
import android.media.FaceDetector;
import android.os.Bundle;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.sar.taxvault.utils.UIUpdate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class FacebookHelper {

    public String TAG = FacebookHelper.class.getSimpleName();

    private FacebookUser user;

    private FacebookResponse mListener;

    private String mFieldString;

    private CallbackManager mCallBackManager;

    Activity context;


    public FacebookHelper( FacebookResponse responseListener,
                           String fieldString,
                           Activity context) {
        this.context=context;
        FacebookSdk.sdkInitialize(context.getApplicationContext());

        if (responseListener == null)

            throw new IllegalArgumentException("FacebookResponse listener cannot be null.");


        //noinspection ConstantConditions

        if (fieldString == null)
            throw new IllegalArgumentException("field string cannot be null.");



        mListener = responseListener;

        mFieldString = fieldString;

        mCallBackManager = CallbackManager.Factory.create();


        //get access token

        FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {

            @Override

            public void onSuccess(LoginResult loginResult) {

                mListener.onFbSignInSuccess();


                //get the user profile

                getUserProfile(loginResult);
            }

            @Override

            public void onCancel() {

                mListener.onFbSignTnFail();

            }

            @Override

            public void onError(FacebookException e) {

                Log.d(TAG, "onError: FB Exception"+e.getLocalizedMessage());

                UIUpdate.GetUIUpdate(context).destroy();

                UIUpdate.GetUIUpdate(context).showAlertDialog("Alert",e.getLocalizedMessage());

                mListener.onFbSignTnFail();

            }

        };

        LoginManager.getInstance().registerCallback(mCallBackManager, mCallBack);

    }

    private void getUserProfile(LoginResult loginResult) {

        // App code

        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {


                        Log.e("response: ", response + "");

                        try {

                            mListener.onFbProfileRecieved(parseResponse(object));

                        } catch (Exception e) {

                            e.printStackTrace();


                            mListener.onFbSignTnFail();

                        }
                    }

                });


        Bundle parameters = new Bundle();

        parameters.putString("fields", mFieldString);

        request.setParameters(parameters);

        request.executeAsync();

    }


    public CallbackManager getCallbackManager() {

        return mCallBackManager;

    }

    private FacebookUser parseResponse(JSONObject object) throws JSONException {
        object.toString();
        user = new FacebookUser();

        user.response = object;


        if (object.has("id"))
            user.facebookID = object.getString("id");

        if (object.has("email")) user.email = object.getString("email");

        if (object.has("name")) user.name = object.getString("name");

        if (object.has("gender")) user.gender = object.getString("gender");

        if (object.has("about")) user.about = object.getString("about");

        if (object.has("bio")) user.bio = object.getString("bio");

        if (object.has("cover"))user.coverPicUrl = object.getJSONObject("cover").getString("source");

        if (object.has("picture"))user.profilePic = object.getJSONObject("picture").getJSONObject("data").getString("url");

        return user;

    }

    public void performSignIn(Activity activity)
    {
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "email"));

    }
    public String getFacebookId(){
        return user.facebookID;
    }
//    public void performSignIn(Fragment fragment) {


//        LoginManager.getInstance().logInWithReadPermissions(fragment, Arrays.asList("public_profile", "user_friends", "email"));


//    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        mCallBackManager.onActivityResult(requestCode, resultCode, data);

    }


    public void performSignOut() {

        LoginManager.getInstance().logOut();

        mListener.onFbSignTnFail();

    }
}
