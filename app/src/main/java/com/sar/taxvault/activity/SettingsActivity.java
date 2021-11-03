package com.sar.taxvault.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sar.taxvault.Model.UserModel;
import com.sar.taxvault.databinding.ActivityRemidersBinding;
import com.sar.taxvault.databinding.ActivitySettingsBinding;
import com.sar.taxvault.databinding.ActivitySignupBinding;
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


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        DatabaseReference usersRef = rootRef.child("User").child(mAuth.getCurrentUser().getUid());

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserModel user = dataSnapshot.getValue(UserModel.class);

                setUSetData(user);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("userDataResponse", databaseError.getMessage());
            }
        };
        usersRef.addListenerForSingleValueEvent(valueEventListener);

    }

    private void setUSetData(UserModel user) {

        binding.userNameTV.setText(user.getFirstName() + " " + user.getLastName());

        binding.emailTV.setText(user.getEmail());

        binding.userFullNameTV.setText(user.getFirstName() + " " + user.getLastName());

        binding.userEmailAddressTV.setText(user.getEmail());

        binding.userPhoneNumberTV.setText(user.getPhoneNumber());

    }

    private void setView() {

        binding.includeView.titleTV.setText("Profile");

    }

    private void setListeners() {

        binding.includeView.backIV.setOnClickListener(view -> {

            finish();

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });

        binding.includeView.backIV.setOnClickListener(v -> finish());
        binding.logoutBtn.setOnClickListener(v -> logOut());

    }

    private void logOut() {

        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(SettingsActivity.this, Login.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

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

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }
}