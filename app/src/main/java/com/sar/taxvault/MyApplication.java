package com.sar.taxvault;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDex;

import com.sar.taxvault.utils.OfficeToPDFTest;
import com.sar.taxvault.utils.PDFNetSample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyApplication extends Application implements LifecycleObserver {

    private ArrayList<PDFNetSample> mListSamples = new ArrayList<PDFNetSample>();
    private static MyApplication singleton;
    private Context m_context;

    public static Boolean enteredBackground = false;

    String TAG = "MyApplication";
    @Override
    public void onCreate() {

        super.onCreate();

        MultiDex.install(this);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        singleton = this;

        mListSamples.add(new OfficeToPDFTest(getApplicationContext()));

        m_context = getApplicationContext();

        Collections.sort(mListSamples, (o1, o2) -> o1.getTitle().compareTo(o2.getTitle()));
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


    public List<PDFNetSample> getContent() {
        return this.mListSamples;
    }

    public static MyApplication getInstance() {
        return singleton;
    }

    public Context getContext() {
        return m_context;
    }
}