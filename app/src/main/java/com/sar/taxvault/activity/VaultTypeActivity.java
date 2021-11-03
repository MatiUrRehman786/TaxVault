package com.sar.taxvault.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sar.taxvault.R;
import com.sar.taxvault.adapters.RecyclerViewAdapterCategories;
import com.sar.taxvault.databinding.ActivityVaultTypeBinding;

import java.util.ArrayList;
import java.util.List;

public class VaultTypeActivity extends AppCompatActivity {

    ActivityVaultTypeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityVaultTypeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        getCategories();
    }

    private void initView() {
        binding.includeView.titleTV.setText("Categories");
    }

    private void getCategories() {
        FirebaseDatabase.getInstance().getReference("user-categories")
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
        binding.vaultRV.setAdapter(new RecyclerViewAdapterCategories(this,categories));
    }
}