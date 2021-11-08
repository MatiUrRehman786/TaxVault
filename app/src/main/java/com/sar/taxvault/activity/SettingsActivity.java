package com.sar.taxvault.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sar.taxvault.Model.UserModel;
import com.sar.taxvault.databinding.ActivitySettingsBinding;
import com.sar.taxvault.utils.UIUpdate;
import com.williammora.snackbar.Snackbar;

public class SettingsActivity extends BaseActivity {

    ActivitySettingsBinding binding;

    private FirebaseAuth mAuth;

    public FirebaseDatabase rootNode;

    public DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        initFireBase();

        setView();

        setListeners();

        if (isOnline()) {

            getUserData();

        } else {

            showMessage("Check your internet connection!");

        }

    }

    private void initFireBase() {

        rootNode = FirebaseDatabase.getInstance();

        mDatabase = rootNode.getReference("User");

        mAuth = FirebaseAuth.getInstance();

    }


    private void getUserData() {


        UIUpdate.GetUIUpdate(this).destroy();
        UIUpdate.GetUIUpdate(this).setProgressDialog();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        DatabaseReference usersRef = rootRef.child("User").child(mAuth.getCurrentUser().getUid());

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UIUpdate.GetUIUpdate(SettingsActivity.this).dismissProgressDialog();

                UserModel user = dataSnapshot.getValue(UserModel.class);

                setUserData(user);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("userDataResponse", databaseError.getMessage());
            }
        };
        usersRef.addListenerForSingleValueEvent(valueEventListener);

    }

    private void setUserData(UserModel user) {

        binding.userNameTV.setText(user.getFirstName() + " " + user.getLastName());

        binding.emailTV.setText(user.getEmail());

        binding.userFullNameTV.setText(user.getFirstName() + " " + user.getLastName());

        binding.userEmailAddressTV.setText(user.getEmail());

        binding.userPhoneNumberTV.setText(user.getPhoneNumber());

    }

    private void setView() {

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        binding.includeView.titleTV.setText("Profile");

    }

    private void setListeners() {

        binding.includeView.backIV.setOnClickListener(view -> {

            finish();

//            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });

        binding.includeView.backIV.setOnClickListener(v -> finish());
        binding.logoutBtn.setOnClickListener(v -> logOut());
        binding.updateBtn.setOnClickListener(v -> updateProfile());

    }

    private void updateProfile() {

        String name = binding.userFullNameTV.getText().toString();

        if (name.contains(" ")) {

            String[] data = name.split(" ");

            mDatabase.child(mAuth.getCurrentUser().getUid()).child("firstName").setValue(data[0]);

            mDatabase.child(mAuth.getCurrentUser().getUid()).child("lastName").setValue(data[1]);

        } else {

            mDatabase.child(mAuth.getCurrentUser().getUid()).child("firstName").setValue(name);

        }

        mDatabase.child(mAuth.getCurrentUser().getUid()).child("phoneNumber").setValue(binding.userPhoneNumberTV.getText().toString());

        Toast.makeText(SettingsActivity.this, "Profile Updated!", Toast.LENGTH_SHORT).show();

        getUserData();

    }

    private void logOut() {

        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(SettingsActivity.this, Login.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

    private void showMessage(String message) {

        Snackbar.with(SettingsActivity.this)
                .text(message)
                .show(SettingsActivity.this);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();

//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }
}