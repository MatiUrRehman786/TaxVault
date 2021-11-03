package com.sar.taxvault.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sar.taxvault.Model.UserModel;
import com.sar.taxvault.R;
import com.sar.taxvault.databinding.ActivityMainBinding;
import com.sar.taxvault.fragments.FilesFragment;
import com.sar.taxvault.fragments.NotificationsFragment;
import com.sar.taxvault.fragments.PremiumFeatureFragment;
import com.sar.taxvault.fragments.TaxVaultFragment;

import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class Main extends AppCompatActivity {

    private ActivityMainBinding binding;

    private FirebaseAuth mAuth;

    public FirebaseDatabase rootNode;

    public DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        initFireBase();

        setView();

        loadFragment(new TaxVaultFragment());

        setDrawer();

        setListeners();

    }

    private void initFireBase() {

        rootNode = FirebaseDatabase.getInstance();

        mDatabase = rootNode.getReference("User");

        mAuth = FirebaseAuth.getInstance();

    }


    private void setView() {

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        binding.includeView.titleTV.setText("Tax Vault");

        getUserData();
    }

    private void setDrawer() {

        DuoDrawerToggle drawerToggle = new DuoDrawerToggle(this, binding.drawer,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        binding.drawer.setDrawerListener(drawerToggle);

        drawerToggle.syncState();

    }

    private void setListeners() {

        binding.includeView.menuIV.setOnClickListener(v -> {

            checkOpenOrCloseDrawer();

        });

        binding.navView.drawerMenuIV.setOnClickListener(v -> {

            checkOpenOrCloseDrawer();

        });

        binding.navView.homeTV.setOnClickListener(v -> {

            binding.includeView.titleTV.setText("Tax Vault");

            checkOpenOrCloseDrawer();

            startActivity(new Intent(this, VaultTypeActivity.class));

            binding.includeView.yearSpinner.setVisibility(View.INVISIBLE);

        });

        binding.navView.filesTV.setOnClickListener(v -> {

//            binding.includeView.titleTV.setText("");

            checkOpenOrCloseDrawer();

            Intent intent = new Intent(Main.this, VaultTypeActivity.class);

            startActivity(intent);

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


            binding.includeView.yearSpinner.setVisibility(View.VISIBLE);

        });

        binding.navView.notificationTV.setOnClickListener(v -> {

            binding.includeView.titleTV.setText("Notifications");

            checkOpenOrCloseDrawer();

            loadFragment(new NotificationsFragment());

            binding.includeView.yearSpinner.setVisibility(View.INVISIBLE);

        });


        binding.navView.upgradeTV.setOnClickListener(v -> {

            binding.includeView.titleTV.setText("Premium Feature");

            checkOpenOrCloseDrawer();

            loadFragment(new PremiumFeatureFragment());

            binding.includeView.yearSpinner.setVisibility(View.INVISIBLE);

        });

        binding.navView.settingsTV.setOnClickListener(v -> {

            checkOpenOrCloseDrawer();

            Intent intent = new Intent(this, SettingsActivity.class);

            startActivity(intent);

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });

    }

    private void checkOpenOrCloseDrawer() {

        if (binding.drawer.isDrawerOpen()) {

            binding.drawer.closeDrawer(Gravity.LEFT);

        } else {

            binding.drawer.openDrawer(Gravity.LEFT);

        }
    }

    private void loadFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(binding.mainFragmentContainer.getId(), fragment);

        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();

    }

    private void getUserData() {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        DatabaseReference usersRef = rootRef.child("User").child(mAuth.getCurrentUser().getUid());

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserModel user = dataSnapshot.getValue(UserModel.class);

                binding.navView.userNameTV.setText(user.getFirstName()+" "+user.getLastName());

                binding.navView.userEmailTV.setText(user.getEmail());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("userDataResponse", databaseError.getMessage());
            }

        };

        usersRef.addListenerForSingleValueEvent(valueEventListener);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (getSupportFragmentManager().getBackStackEntryCount() < 1) {

            finish();

        } else {

            super.onBackPressed();

        }
    }
}