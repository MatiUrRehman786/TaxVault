package com.sar.taxvault.utils;

import android.content.Context;
import android.util.Log;

import com.androidnetworking.error.ANError;

public class Utils {

    public static String getRootDirPath(Context context) {
//        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//            File file = ContextCompat.getExternalFilesDirs(context.getApplicationContext(), null)[0];
//            return file.getAbsolutePath();
//        } else {
            return context.getExternalFilesDir(null) + "/";
//        }
    }

    public static void logError(String TAG, Throwable e) {
        if (e instanceof ANError) {
            ANError anError = (ANError) e;
            if (anError.getErrorCode() != 0) {
                // received ANError from server
                // error.getErrorCode() - the ANError code from server
                // error.getErrorBody() - the ANError body from server
                // error.getErrorDetail() - just a ANError detail
                Log.d(TAG, "onError errorCode : " + anError.getErrorCode());
                Log.d(TAG, "onError errorBody : " + anError.getErrorBody());
                Log.d(TAG, "onError errorDetail : " + anError.getErrorDetail());
            } else {
                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                Log.d(TAG, "onError errorDetail : " + anError.getErrorDetail());
            }
        } else {
            Log.d(TAG, "onError errorMessage : " + e.getMessage());
        }
    }

}