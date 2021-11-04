package com.sar.taxvault.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sar.taxvault.R;
import com.sar.taxvault.databinding.ActivityLoginBinding;
import com.sar.taxvault.databinding.DialogForgotPasswordBinding;
import com.sar.taxvault.utils.UIUpdate;
import com.williammora.snackbar.Snackbar;

public class Login extends BaseActivity {

    private ActivityLoginBinding binding;

    private FirebaseAuth mAuth;

    public FirebaseDatabase rootNode;

    public DatabaseReference mDatabase;

    private GoogleSignInClient mGoogleSignInClient;

    private final static int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();

        initFireBase();

        checkLogin();

        setContentView(view);

        setView();

        setListeners();

    }

    private void initFireBase() {

        rootNode = FirebaseDatabase.getInstance();

        mDatabase = rootNode.getReference("User");

        mAuth = FirebaseAuth.getInstance();

    }

    private void checkLogin() {

        if (mAuth.getCurrentUser() != null) {

            startMainActivity();

        }

    }


    private void setListeners() {

        binding.signUpTV.setOnClickListener(v -> {

            Intent intent = new Intent(Login.this, Signup.class);

            startActivity(intent);

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });

        binding.loginBtn.setOnClickListener(v -> {

            if (isValid()) {

                if (isOnline()) {

                    loginUserNow();

                } else {

                    showMessage("Check your internet Connection");

                }

            }

        });

        binding.forgotPasswordTV.setOnClickListener(v -> showForgotDialog());

        //  binding.googleBtn.setOnClickListener(v -> googleSgnIn());

    }

    private void showForgotDialog() {

        Dialog dialog = new Dialog(Login.this, android.R.style.Theme_NoTitleBar_Fullscreen);

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setCancelable(true);

        DialogForgotPasswordBinding dialogBinding = DialogForgotPasswordBinding.inflate((Login.this).getLayoutInflater());
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.doneBtn.setOnClickListener(v -> {

            if (!dialogBinding.emailET.getText().toString().isEmpty()) {

                sendForgotEmail();

            }

            dialog.dismiss();

        });

        dialog.show();
    }

    private void setView() {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        binding.includeView.titleTV.setText("Login");

        binding.includeView.menuIV.setVisibility(View.INVISIBLE);

        binding.includeView.yearSpinner.setVisibility(View.INVISIBLE);

    }

    private boolean isValid() {

        boolean chceck = false;

        if (binding.emailET.getText().toString().trim().isEmpty()) {

            showMessage("Enter Email!");

            return chceck;

        }

        if (binding.passwordET.getText().toString().trim().isEmpty()) {

            showMessage("Enter Password!");

            return chceck;

        }

        chceck = true;

        return chceck;

    }

    private void showMessage(String message) {

        Snackbar.with(Login.this)
                .text(message)
                .show(Login.this);

    }

    private void loginUserNow() {

        UIUpdate.GetUIUpdate(this).destroy();

        UIUpdate.GetUIUpdate(this).setProgressDialog();

        String email = binding.emailET.getText().toString();

        String password = binding.passwordET.getText().toString();

        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(email, password)

                .addOnCompleteListener(Login.this, task -> {

                    UIUpdate.GetUIUpdate(Login.this).dismissProgressDialog();

                    if (!task.isSuccessful()) {

                        if (task.getException() != null) {

                            UIUpdate.GetUIUpdate(Login.this).showAlertDialog("Alert",
                                    task.getException().getLocalizedMessage());

                        }

                    } else {


                        setRemember();

                        startMainActivity();

                    }
                });

    }

    public void startMainActivity() {

        finish();

        Intent intent = new Intent(Login.this, Main.class);

        startActivity(intent);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void setRemember() {


    }

    private void sendForgotEmail() {

        FirebaseAuth.getInstance().sendPasswordResetEmail(binding.emailET.getText().toString())

                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        finish();

                        Toast.makeText(Login.this, "Email has been sent on your Email!", Toast.LENGTH_SHORT).show();

                    }
                });
    }

}