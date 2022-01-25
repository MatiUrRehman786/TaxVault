package com.sar.taxvault.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.sar.taxvault.Model.UserModel;
import com.sar.taxvault.MyApplication;
import com.sar.taxvault.classes.SessionManager;
import com.sar.taxvault.databinding.ActivityPhoneVerificationBinding;
import com.sar.taxvault.utils.UIUpdate;
import com.sar.taxvault.vm.AuthViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PhoneVerification extends BaseActivity {

    ActivityPhoneVerificationBinding binding;

    AuthViewModel model;

    String mVerificationId;

    PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityPhoneVerificationBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        setUpUIUpdate();

        setViewModel();

        setCallbacks();

        setListeners();
    }

    private void setListeners() {

        binding.includeView.backIV.setOnClickListener(v -> onBackPressed());

        binding.submitBt.setOnClickListener(view -> verify());
    }

    @Override
    protected void onResume() {

        MyApplication.enteredBackground = false;

        super.onResume();
    }

    private boolean validate() {

        if (Objects.requireNonNull(binding.codeET.getText()).toString().isEmpty()) {

            showErrorAlert("Code Field Must Not be Empty.");

            return false;

        }

        return true;
    }

    private void verify() {

        if (validate()) {

            String code = Objects.requireNonNull(binding.codeET.getText()).toString();

            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

            signInWithPhoneAuthCredential(credential);
        }

    }

    @Override
    public void onBackPressed() {

        FirebaseAuth.getInstance().signOut();

        SessionManager.getInstance().setUser(null, this);

        super.onBackPressed();
    }

    public void setCallbacks() {

        SessionManager.getInstance().getBiometricCredentials(user -> {

            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                            .setPhoneNumber(user.getPhoneNumber())       // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(PhoneVerification.this)                 // Activity (for callback binding)
                            .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);

        }, PhoneVerification.this);


    }


    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NotNull PhoneAuthCredential credential) {

            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.

            signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(@NotNull FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
//            Log.w(TAG, "onVerificationFailed", e);

//            if (e instanceof FirebaseAuthInvalidCredentialsException) {
            // Invalid request
            showErrorAlert(e.getLocalizedMessage());

//            } else if (e instanceof FirebaseTooManyRequestsException) {
            // The SMS quota for the project has been exceeded
            showErrorAlert(e.getLocalizedMessage());

//            }

            // Show a message and update the UI
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {

            Toast.makeText(PhoneVerification.this, "Code Sent", Toast.LENGTH_SHORT).show();
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            // Log.d(TAG, "onCodeSent:" + verificationId);

            // Save verification ID and resending token so we can use them later
            mVerificationId = verificationId;
            mResendToken = token;
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        SessionManager.getInstance()
                                .getBiometricCredentials(user -> {

                                    model.authenticateWithEmailAndPassword(user.getEmail(), user.getPassword(), PhoneVerification.this);

                                }, PhoneVerification.this);

                    } else {

                        if (task.getException() != null) {

                            showErrorAlert(task.getException().getLocalizedMessage());

                        }
                    }
                });
    }

    private void setUpUIUpdate() {

        UIUpdate.GetUIUpdate(this)
                .destroy();

    }

    private void setViewModel() {

        model = new ViewModelProvider(this).get(AuthViewModel.class);

        model.getLiveData().observe(PhoneVerification.this, genericModelLiveData -> {

            switch (genericModelLiveData.status) {

                case error:

                    hideLoader();

                    showErrorAlert(genericModelLiveData.errorMsg);

                    break;

                case loading:

                    showLoader();

                    break;

                case success:

                    onAuthSucceeded();

                    break;
            }

        });
    }

    private void onAuthSucceeded() {

        SessionManager.getInstance().getBiometricCredentials(user -> {

            user.twoFactorAuthenticated = true;

            SessionManager.getInstance().setUser(user, PhoneVerification.this);

            startActivity(new Intent(PhoneVerification.this, Main.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));

            finish();

        }, PhoneVerification.this);

    }

}
