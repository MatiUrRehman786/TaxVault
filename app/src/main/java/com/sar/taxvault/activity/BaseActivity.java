package com.sar.taxvault.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.williammora.snackbar.Snackbar;

public class BaseActivity extends AppCompatActivity {

    static final String TAG = "FireBase";
    ProgressDialog mDialog;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    public AlertDialog errorDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDialog = new ProgressDialog(BaseActivity.this);
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

}