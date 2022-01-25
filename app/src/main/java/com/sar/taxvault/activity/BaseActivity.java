package com.sar.taxvault.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sar.taxvault.MessageEvent;
import com.sar.taxvault.MyApplication;
import com.williammora.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class BaseActivity extends AppCompatActivity {

    static final String TAG = "FireBase";

    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    public AlertDialog errorDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EventBus.getDefault().register(this);
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void showLoader() {

        if(BaseActivity.this.isFinishing() || BaseActivity.this.isDestroyed())

            return;

        if (progressDialog != null && progressDialog.isShowing())

            progressDialog.dismiss();

        progressDialog = new ProgressDialog(BaseActivity.this);
        progressDialog.setTitle("");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);

        progressDialog.show();
    }

    public void hideKeyboard(View view) {

        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);

        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(MyApplication.enteredBackground) {

            showSessionTimeoutAlert();

        }
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(!(networkInfo != null && networkInfo.isConnected())){
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        return (networkInfo != null && networkInfo.isConnected());
    }

    public void showErrorAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this)
                .setTitle("Alert")
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        try {
            errorDialog = builder.create();
            errorDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSessionTimeoutAlert() {

        if(errorDialog != null && errorDialog.isShowing())
            errorDialog.dismiss();

        AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this)
                .setTitle("Alert")
                .setMessage("Your session has been ended please login again")
                .setCancelable(false)
                .setPositiveButton("Ok", (dialog, which) -> {

                    MyApplication.enteredBackground = false;
                    FirebaseAuth.getInstance().signOut();

                    dialog.dismiss();

                    startActivity(new Intent(BaseActivity.this, Login.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));

                    finish();

                });
        try {
            errorDialog = builder.create();
            errorDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideLoader() {

        if(BaseActivity.this.isDestroyed() || BaseActivity.this.isFinishing())

            return;

        if (progressDialog != null && progressDialog.isShowing())

            progressDialog.dismiss();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

//        EventBus.getDefault().unregister(this);
    }
}