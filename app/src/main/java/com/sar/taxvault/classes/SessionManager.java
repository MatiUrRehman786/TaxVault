package com.sar.taxvault.classes;

import android.content.Context;

import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import com.google.gson.Gson;
import com.sar.taxvault.Model.UserModel;

import org.reactivestreams.Subscription;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableSubscriber;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SessionManager {

    private static SessionManager sessionManager;

    Gson gson = new Gson();

    RxDataStore<Preferences> dataStore;

    Preferences.Key AUTH_CREDENTIALS_BIOMETRICS = new Preferences.Key("AUTH_CREDENTIALS_BIOMETRICS");

    public static SessionManager getInstance() {

        if (sessionManager == null)

            sessionManager = new SessionManager();


        return sessionManager;

    }

    public void setUser(UserModel userModel, Context context) {

        String json = gson.toJson(userModel);

        initDataStore(context);

        Single<Preferences> updateResult = dataStore.updateDataAsync(prefsIn -> {

            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();

            mutablePreferences.set(AUTH_CREDENTIALS_BIOMETRICS,
                    json);

            return Single.just(mutablePreferences);
        });

        updateResult.subscribe();

    }

    public void getBiometricCredentials(UserDataCallback callback, Context c) {

        initDataStore(c);

        Flowable<Object> objectFlowable =
                dataStore.data().map(prefs -> prefs.get(AUTH_CREDENTIALS_BIOMETRICS));

        objectFlowable.subscribeOn(Schedulers.newThread());

        objectFlowable.subscribe(new FlowableSubscriber<Object>() {
            @Override
            public void onSubscribe(@NonNull Subscription s) {

            }

            @Override
            public void onNext(Object o) {

                UserModel userModel = gson.fromJson(o.toString(), UserModel.class);

                callback.onUserFound(userModel);

            }

            @Override
            public void onError(Throwable t) {

                callback.onUserFound(null);

            }

            @Override
            public void onComplete() {

            }
        });

    }

    private void initDataStore(Context context) {

        dataStore =
                new RxPreferenceDataStoreBuilder(context, /*name=*/ "settings").build();

    }

    public interface UserDataCallback {

        void onUserFound(UserModel user);
    }
}
