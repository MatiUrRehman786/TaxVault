package com.sar.taxvault.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sar.taxvault.Facebook.FacebookHelper;
import com.sar.taxvault.Facebook.FacebookResponse;
import com.sar.taxvault.Facebook.FacebookUser;
import com.sar.taxvault.Model.UserModel;
import com.sar.taxvault.R;
import com.sar.taxvault.classes.SessionManager;
import com.sar.taxvault.databinding.ActivityLoginBinding;
import com.sar.taxvault.databinding.DialogForgotPasswordBinding;
import com.sar.taxvault.utils.UIUpdate;
import com.sar.taxvault.vm.AuthViewModel;
import com.williammora.snackbar.Snackbar;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;

public class Login extends BaseActivity {

    AuthViewModel model;

    Boolean shouldShowTwoFactor = false;

    private ActivityLoginBinding binding;

    private FirebaseAuth mAuth;

    public FirebaseDatabase rootNode;

    public DatabaseReference mDatabase;

    //Google

    private GoogleSignInClient mGoogleSignInClient;

    private final static int RC_SIGN_IN = 123;

    //bio metrics
    private Executor executor;

    private BiometricPrompt biometricPrompt;

    private BiometricPrompt.PromptInfo promptInfo;

    //faccebook

    FacebookHelper facebookHelper;

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();

        setContentView(view);

        initExecutor();

        setLiveData();

        printHashKey(Login.this);

        initFireBase();

        checkLogin();

        setView();

        createRequest();

        setListeners();

        setupFacebook();

    }

    private void setLiveData() {

        model = new ViewModelProvider(this).get(AuthViewModel.class);

        model.getLiveData().observe(Login.this, genericModelLiveData -> {

            switch (genericModelLiveData.status) {

                case error:

                    hideLoader();

                    showErrorAlert(genericModelLiveData.errorMsg);

                    break;

                case loading:

                    try {

                        showLoader();

                    } catch (Exception e) {


                    }

                    break;

                case success:

                    UserModel userModel = (UserModel) genericModelLiveData.object;

                    onAuthSucceeded(userModel);

                    shouldShowTwoFactor = false;

                    break;
            }


        });
    }

    private void onAuthSucceeded(UserModel userModel) {

        hideLoader();

        if (userModel != null) {

            if (binding.passwordET.getText().toString().length() >= 6) {

                userModel.setPassword(binding.passwordET.getText().toString());

            }

            if (shouldShowTwoFactor)

                userModel.twoFactorAuthenticated = false;

            SessionManager.getInstance().setUser(userModel, Login.this);

        }

        if (shouldShowTwoFactor && (userModel != null && !userModel.twoFactorAuthenticated))

            handleTwoFactor(userModel);

        else {

            setRemember();

            startMainActivity();
        }

    }

    private void initExecutor() {

        executor = ContextCompat.getMainExecutor(this);

        setBiometricPrompt();
    }

    private void setBiometricPrompt() {

        biometricPrompt = new BiometricPrompt(Login.this,
                executor,
                biometricAuthCallback
        );

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for TAX VAULT")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();

        binding.biometricBtn
                .setOnClickListener(view -> loginWithBiometric());

    }

    BiometricPrompt.AuthenticationCallback biometricAuthCallback = new BiometricPrompt.AuthenticationCallback() {
        @Override
        public void onAuthenticationError(int errorCode,
                                          @NonNull CharSequence errString) {

            super.onAuthenticationError(errorCode, errString);

            showErrorAlert(errString.toString());

        }

        @Override
        public void onAuthenticationSucceeded(
                @NonNull BiometricPrompt.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);

            SessionManager.getInstance().getBiometricCredentials(user -> performLoginWithCredentials(user), Login.this);

        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();

            showErrorAlert("Authentication Failed");
        }
    };

    private void performLoginWithCredentials(UserModel user) {

        if (user == null) {

            showErrorAlert("Please Sign in using password first.");

            return;
        }

        shouldShowTwoFactor = false;

        if (!user.getEmail().isEmpty() && !user.getPassword().isEmpty()) {

            model.authenticateWithEmailAndPassword(user.getEmail(), user.getPassword(), Login.this);

        }

    }

    private void performLoginUsingUniqueCode(String email, String password) {

        if (email != null && password != null) {

            model = new ViewModelProvider(this).get(AuthViewModel.class);

//            model.loginWithUniqueCode(email, password, this);

        }

    }

    private void handleTwoFactor(UserModel userModel) {

        if (userModel != null) {

            userModel.twoFactorAuthenticated = false;

            FirebaseAuth.getInstance().signOut();

            startActivity(new Intent(Login.this, PhoneVerification.class));

        }

    }

    void loginWithBiometric() {

        biometricPrompt.authenticate(promptInfo);

    }


    public static void printHashKey(Context pContext) {
        try {

            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {

                MessageDigest md = MessageDigest.getInstance("SHA");

                md.update(signature.toByteArray());

                String hashKey = new String(Base64.encode(md.digest(), 0));

                Log.i("keyHash", "printHashKey() Hash Key: " + hashKey);

            }

        } catch (NoSuchAlgorithmException e) {

            Log.e("keyHash", "printHashKey()", e);

        } catch (Exception e) {

            Log.e("keyHash", "printHashKey()", e);

        }

    }


    private void initFireBase() {

        rootNode = FirebaseDatabase.getInstance();

        mDatabase = rootNode.getReference("User");

        mAuth = FirebaseAuth.getInstance();

    }

    private void checkLogin() {

//        if (mAuth.getCurrentUser() != null) {

//            startMainActivity();

//        }

    }

    private void setListeners() {

        binding.signUpTV.setOnClickListener(v -> {

            Intent intent = new Intent(Login.this, Signup.class);

            startActivity(intent);

        });

        binding.loginBtn.setOnClickListener(v -> {

            if (isValid()) {

                if (isOnline()) {

                    shouldShowTwoFactor = true;

                    model.authenticateWithEmailAndPassword(binding.emailET.getText().toString(), binding.passwordET.getText().toString(), Login.this);

                } else {

                    showMessage("Check your internet Connection");

                }

            }

        });

        binding.forgotPasswordTV.setOnClickListener(v -> showForgotDialog());

        binding.googleBtn.setOnClickListener(v -> googleSgnIn());

        binding.facebookBtn.setOnClickListener(view -> facebookHelper.performSignIn(Login.this));

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

        if (binding.emailET.getText().toString().trim().isEmpty()) {

            showMessage("Enter Email");

            return false;

        }

        if (binding.passwordET.getText().toString().trim().isEmpty()) {

            showMessage("Enter Password");

            return false;

        }

        return true;

    }

    private void showMessage(String message) {

        Snackbar.with(Login.this)
                .text(message)
                .show(Login.this);

    }

    private void loginUserNow() {

        UIUpdate.GetUIUpdate(this).destroy();

        showLoader();

        String email = binding.emailET.getText().toString();

        String password = binding.passwordET.getText().toString();

        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(email, password)

                .addOnCompleteListener(Login.this, task -> {

                    hideLoader();

                    if (!task.isSuccessful()) {

                        if (task.getException() != null) {

                            UIUpdate.GetUIUpdate(Login.this).showAlertDialog("Alert",
                                    task.getException().getLocalizedMessage());

                        }

                    } else {


                    }
                });

    }

    public void startMainActivity() {

        finish();

        Intent intent = new Intent(Login.this, Main.class);

        startActivity(intent);

//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void setRemember() {


    }

    private void sendForgotEmail() {

        FirebaseAuth
                .getInstance()
                .sendPasswordResetEmail(binding.emailET.getText().toString())
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        finish();

                        Toast.makeText(Login.this, "Email has been sent on your Email!", Toast.LENGTH_SHORT).show();

                    }
                });
    }


    private void createRequest() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }


    private void googleSgnIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            handleSignInResult(task);

        } else {

            facebookHelper.onActivityResult(requestCode, resultCode, data);

        }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {

        try {

            GoogleSignInAccount account = completedTask.getResult(ApiException.class);


            Log.w("GoogleUserInfo", "info=" + account.getEmail());

            try {

                firebaseAuthWithGoogle(account.getIdToken(), account);

            } catch (Exception e) {

                Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        } catch (ApiException e) {

            Toast.makeText(Login.this, "Try Again With Google#" + e.getMessage() + "" + completedTask.getException(), Toast.LENGTH_LONG).show();

        }
    }

    private void firebaseAuthWithGoogle(String idToken, GoogleSignInAccount account) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        FirebaseUser currentUser = mAuth.getCurrentUser();

                        checkAlreadyExist(account);

                    } else {

                        Log.d("googleExcwption", task.getException().toString());

                        Toast.makeText(Login.this, "Try Again With Google" + task.getException().toString(), Toast.LENGTH_LONG).show();

                    }

                });
    }

    private void checkAlreadyExist(GoogleSignInAccount account) {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        mDatabase
                .child(currentUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        if (snapshot.exists()) {

                            UserModel user = snapshot.getValue(UserModel.class);

                            if (user.getBusinessType() == null || user.getBusinessType().isEmpty() || user.getBusinessType().equalsIgnoreCase("null")) {

                                Intent intent = new Intent(Login.this, SelectBusinessActivity.class);

                                intent.putExtra("type", "social");

                                startActivity(intent);

                            } else {

                                Intent intent = new Intent(Login.this, Main.class);

                                startActivity(intent);

                                finish();

                            }


                        } else {

                            UserModel user = new UserModel();

                            user.setEmail(account.getEmail());

                            user.setFirstName(account.getDisplayName());

                            user.setLastName("");

                            user.setPassword("");

                            user.setToken("");

                            user.setPhoneNumber("");

                            mDatabase
                                    .child(currentUser.getUid())
                                    .setValue(user);

                            finish();

                            Intent intent = new Intent(Login.this, SelectBusinessActivity.class);

                            intent.putExtra("type", "social");

                            startActivity(intent);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });

    }

    private void setupFacebook() {

        FacebookSdk.setApplicationId(getResources().getString(R.string.facebook_app_id));

        FacebookSdk.sdkInitialize(this);

        callbackManager = CallbackManager.Factory.create();

        facebookHelper = new FacebookHelper(response, "id,name", this);

    }

    FacebookResponse response = new FacebookResponse() {

        @Override
        public void onFbSignTnFail() {

            Toast.makeText(getApplicationContext(), "Try Again With Facebook", Toast.LENGTH_LONG).show();

        }

        @Override
        public void onFbSignInSuccess() {

        }

        @Override
        public void onFbSignOut() {

            Toast.makeText(getApplicationContext(), "Sign out Successful", Toast.LENGTH_LONG).show();

        }

        @Override
        public void onFbProfileRecieved(FacebookUser facebookUser) {

            AccessToken accessToken = AccessToken.getCurrentAccessToken();

            facebookUser.token = accessToken;

            accessToken.getDeclinedPermissions();

            handleFacebookAccessToken(facebookUser);

        }

    };

    private void handleFacebookAccessToken(final FacebookUser user) {

        AuthCredential credential = FacebookAuthProvider.getCredential(user.token.getToken());

        FirebaseAuth.getInstance()
                .signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        checkFacebookUserExist(user);

                    } else {

                        Toast.makeText(getApplicationContext(), "Try Again With Facebook", Toast.LENGTH_LONG).show();

                    }

                });

    }

    private void checkFacebookUserExist(FacebookUser facebookUser) {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        mDatabase
                .child(currentUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        if (snapshot.exists()) {

                            finish();

                            UserModel user = snapshot.getValue(UserModel.class);

                            if (user.getBusinessId().isEmpty()) {

                                Intent intent = new Intent(Login.this, SelectBusinessActivity.class);

                                intent.putExtra("type", "social");

                                startActivity(intent);

                            } else {

                                Intent intent = new Intent(Login.this, Main.class);

                                startActivity(intent);

                            }


                        } else {


                            UserModel user = new UserModel();

                            user.setEmail(facebookUser.email);

                            user.setFirstName(facebookUser.name);

                            user.setLastName("");

                            user.setPassword("");

                            user.setToken("");

                            user.setPhoneNumber("");

                            mDatabase
                                    .child(currentUser.getUid())
                                    .setValue(user);


                            finish();

                            Intent intent = new Intent(Login.this, Main.class);

                            startActivity(intent);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        UIUpdate.GetUIUpdate(this).destroy();

    }
}