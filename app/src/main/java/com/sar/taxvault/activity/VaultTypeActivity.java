package com.sar.taxvault.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sar.taxvault.Model.UserModel;
import com.sar.taxvault.adapters.RecyclerViewAdapterCategories;
import com.sar.taxvault.databinding.ActivityVaultTypeBinding;
import com.sar.taxvault.utils.UIUpdate;

import java.util.ArrayList;
import java.util.List;

public class VaultTypeActivity extends AppCompatActivity {

    ActivityVaultTypeBinding binding;

    UserModel user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityVaultTypeBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        initView();

        getCurrentUser();

        setListeners();
    }


    private void setListeners() {

        binding.includeView.backIV.setOnClickListener(view -> onBackPressed());

    }


    @Override
    public void onBackPressed() {

        super.onBackPressed();

        finish();

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

    private void initView() {

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        binding.includeView.titleTV.setText("Categories");

    }

    private void getCurrentUser() {

        UIUpdate.GetUIUpdate(this).destroy();
        UIUpdate.GetUIUpdate(this).setProgressDialog();

        FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid())

                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        UIUpdate.GetUIUpdate(VaultTypeActivity.this).dismissProgressDialog();

                        if (snapshot.getValue() != null) {

                            user = snapshot.getValue(UserModel.class);

                            getCategories();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                        UIUpdate.GetUIUpdate(VaultTypeActivity.this).dismissProgressDialog();

                        UIUpdate.GetUIUpdate(VaultTypeActivity.this).showAlertDialog("Alert", error.getMessage());
                    }
                });
    }

    private void getCategories() {

        String userType = user.getUserType();

        String selectedCategory;

        if (userType.equalsIgnoreCase("individual")) {

            selectedCategory = "user-categories";

        } else if (userType.equalsIgnoreCase("business")) {

            selectedCategory = "business-categories";

        } else {

            selectedCategory = "trusts-categories";

        }

        FirebaseDatabase.getInstance().getReference(selectedCategory)

                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        if (snapshot.getValue() != null)

                            parseSnapshot(snapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
    }

    private void parseSnapshot(DataSnapshot snapshot) {

        List<String> categories = new ArrayList<>();

        for (DataSnapshot child : snapshot.getChildren()) {

            if (child.getValue() != null)

                categories.add(child.getValue().toString());
        }

        setAdapter(categories);
    }

    private void setAdapter(List<String> categories) {

        binding.vaultRV.setLayoutManager(new LinearLayoutManager(this));

        binding.vaultRV.setAdapter(new RecyclerViewAdapterCategories(this, categories));

    }

}