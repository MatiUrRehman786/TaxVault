package com.sar.taxvault.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.sar.taxvault.R;

/**
 * Created by Sajeel on 12/24/2018.
 */

public class UIUpdate {

    private ProgressDialog progressDialog;

    Dialog customDialog;
    Context context;
    Dialog myDialog;

    public static UIUpdate uiUpdate;

    private static final String CHANNEL_NAME = "General";
    private static final String CHANNEL_DESCRIPTION = "General Notification";

    public static UIUpdate GetUIUpdate(Context context) {

        if (uiUpdate == null) {

            uiUpdate = new UIUpdate(context);

        }

        return uiUpdate;
    }

    public UIUpdate(Context context) {
        this.context = context;
        customDialog = new Dialog(context);
        myDialog = new Dialog(context);
    }

    public void fullScreenView(Window window) {
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle(title);
        builder1.setMessage(message);
        builder1.setCancelable(false);
        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    public void setProgressDialog() {
        progressDialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
        progressDialog.setProgressStyle(R.style.ProgressStyle);
        progressDialog.setTitle("");
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void dismissProgressDialog() {

        if (progressDialog != null)

            if (progressDialog.isShowing())

                progressDialog.dismiss();

        progressDialog = null;
    }

    public void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void ShowKeyBoard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public void destroy() {

        destroyProgressDialog();

        destroyCustomDialog();

        destroyMyDialog();

        uiUpdate = null;

    }

    private void destroyProgressDialog() {

        if (progressDialog != null) {

            if (progressDialog.isShowing())
                progressDialog.hide();

            progressDialog = null;
        }
    }

    private void destroyMyDialog() {

        if (myDialog != null) {

            if (myDialog.isShowing())
                myDialog.hide();

            myDialog = null;
        }
    }

    private void destroyCustomDialog() {

        if (customDialog != null) {

            if (customDialog.isShowing())
                customDialog.hide();

            customDialog = null;
        }
    }
}
