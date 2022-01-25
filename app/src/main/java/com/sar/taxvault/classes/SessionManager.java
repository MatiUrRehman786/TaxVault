package com.sar.taxvault.classes;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.sar.taxvault.Model.UserModel;

public class SessionManager {

    private static SessionManager sessionManager;

    Gson gson = new Gson();

    SharedPreferences sharedpreferences;

//    public static RxDataStore<Preferences> dataStore = null;

//    public final static Preferences.Key AUTH_CREDENTIALS_BIOMETRICS = new Preferences.Key("AUTH_CREDENTIALS_BIOMETRICS");

    public static final String AUTH_CREDENTIALS_BIOMETRICS = "AUTH_CREDENTIALS_BIOMETRICS";

    public static SessionManager getInstance() {

        if (sessionManager == null)

            sessionManager = new SessionManager();

        return sessionManager;

    }

    public void setUser(UserModel userModel, Context context) {

        sharedpreferences = context.getSharedPreferences("Prefs", Context.MODE_PRIVATE);

        String json = gson.toJson(userModel);

        initDataStore(context);

        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(AUTH_CREDENTIALS_BIOMETRICS, json);

        editor.commit();

    }

    public void getBiometricCredentials(UserDataCallback callback, Context c) {

        initDataStore(c);

        String data = sharedpreferences.getString(AUTH_CREDENTIALS_BIOMETRICS, null);

        if(data == null) {

            callback.onUserFound(null);

            return;
        }

        UserModel userModel  = new Gson().fromJson(data, UserModel.class);

        callback.onUserFound(userModel);
    }

    private void initDataStore(Context context) {

        sharedpreferences = context.getSharedPreferences("Prefs", Context.MODE_PRIVATE);

    }

    public interface UserDataCallback {

        void onUserFound(UserModel user);
    }
}
