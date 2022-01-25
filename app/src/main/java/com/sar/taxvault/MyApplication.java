package com.sar.taxvault;

import android.app.Application;
import android.util.Log;

import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDex;

import com.google.firebase.auth.FirebaseAuth;
import com.sar.taxvault.utils.AppLifeCycleHandler;

import org.greenrobot.eventbus.EventBus;

public class MyApplication extends Application implements LifecycleObserver {

    public static Boolean enteredBackground = false;

    String TAG = "MyApplication";
    @Override
    public void onCreate() {

        super.onCreate();

        MultiDex.install(this);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        //App in background
        enteredBackground = true;
        Log.d(TAG, "onAppBackgrounded: ");

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        // App in foreground
        Log.d(TAG, "onAppForegrounded: ");
    }
}