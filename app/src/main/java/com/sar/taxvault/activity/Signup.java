package com.sar.taxvault.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sar.taxvault.Model.UserModel;
import com.sar.taxvault.R;
import com.sar.taxvault.databinding.ActivitySignupBinding;
import com.sar.taxvault.utils.UIUpdate;
import com.williammora.snackbar.Snackbar;

public class Signup extends BaseActivity {

    private ActivitySignupBinding binding;

    private FirebaseAuth mAuth;

    public FirebaseDatabase rootNode;

    public DatabaseReference mDatabase;

    public static String businessName = "", businessId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);

        binding = ActivitySignupBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();

        initFireBase();

        setContentView(view);

        setView();

        setListeners();

    }

    private void initFireBase() {

        UIUpdate.GetUIUpdate(this).destroy();

        rootNode = FirebaseDatabase.getInstance();

        mDatabase = rootNode.getReference("User");

        mAuth = FirebaseAuth.getInstance();

    }

    private void setView() {

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        binding.includeView.titleTV.setText("Sign up");

        binding.includeView.menuIV.setVisibility(View.INVISIBLE);

        binding.includeView.yearSpinner.setVisibility(View.INVISIBLE);

    }

    private void setListeners() {

        binding.signupBtn.setOnClickListener(view -> {

            if (isValid()) {

                if (isOnline()) {

                    signUpUserNow();

                } else {

                    showMessage("Check your internet Connection");

                }

            }

        });

        binding.selectBusinessBtn.setOnClickListener(v -> {

            startActivity(new Intent(Signup.this, SelectBusinessActivity.class)
                    .putExtra("type", "normal"));

        });

    }

    private boolean isValid() {

        boolean chceck = false;

        if (binding.firstNameET.getText().toString().trim().isEmpty()) {

            showMessage("Enter User First Name!");

            return chceck;

        }

        if (binding.lastNameET.getText().toString().trim().isEmpty()) {

            showMessage("Enter Last Name!");

            return chceck;

        }

        if (binding.phoneNumberET.getText().toString().trim().isEmpty()) {

            showMessage("Enter Phone Number!");

            return chceck;

        }

        if (binding.emailET.getText().toString().trim().isEmpty()) {

            showMessage("Enter Email!");

            return chceck;

        }

        if (binding.passwordET.getText().toString().trim().isEmpty()) {

            showMessage("Enter Password!");

            return chceck;

        }
        if (binding.businessNameTV.getText().toString().trim().isEmpty()) {

            showMessage("Select Business!");

            return chceck;

        }

        chceck = true;

        return chceck;

    }

    private void showMessage(String message) {

        Snackbar.with(Signup.this)
                .text(message)
                .show(Signup.this);

    }

    private void signUpUserNow() {

        String email = binding.emailET.getText().toString();

        String password = binding.passwordET.getText().toString();

        UIUpdate.GetUIUpdate(this).setProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                UIUpdate.GetUIUpdate(Signup.this).dismissProgressDialog();

                if (task.isSuccessful()) {

                    String currentID = mAuth.getCurrentUser().getUid();

                    UserModel user = new UserModel();

                    user.setFirstName(binding.firstNameET.getText().toString());
                    user.setLastName(binding.lastNameET.getText().toString());
                    user.setPhoneNumber(binding.phoneNumberET.getText().toString());
                    user.setEmail(binding.emailET.getText().toString());
                    user.setPassword(binding.passwordET.getText().toString());
                    user.setUserType(binding.userTypeSpinner.getSelectedItem().toString());
                    user.setRememberMe(binding.rememberMeCBSignup.isChecked());
                    user.setBusinessId(businessId);

                    mDatabase.child(currentID).setValue(user);

                    finish();

                    startActivity(new Intent(Signup.this, Main.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));

                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


                } else {

                    if (task.getException() != null)

                        UIUpdate.GetUIUpdate(Signup.this).showAlertDialog("Alert", task.getException().getLocalizedMessage());

                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (businessName != "") {

            binding.businessNameTV.setText(businessName);

            businessName = "";

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();

    }
}