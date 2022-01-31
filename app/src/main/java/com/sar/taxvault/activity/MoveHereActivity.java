package com.sar.taxvault.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sar.taxvault.Model.Document;
import com.sar.taxvault.Model.UserModel;
import com.sar.taxvault.adapters.RecyclerViewAdapterCategories;
import com.sar.taxvault.databinding.ActivityVaultTypeBinding;
import com.sar.taxvault.utils.UIUpdate;

import java.util.ArrayList;
import java.util.List;

public class MoveHereActivity extends BaseActivity {

    ActivityVaultTypeBinding binding;

    UserModel user;

    Document document;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityVaultTypeBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        document = getIntent().getParcelableExtra("document");

        initView();

        getCurrentUser();

        setListeners();
    }

    public static void start(Context c, Document document) {

        c.startActivity(new Intent(c, MoveHereActivity.class)
        .putExtra("document",document));

    }


    private void setListeners() {

        binding.includeView.backIV.setOnClickListener(view -> onBackPressed());

    }


    @Override
    public void onBackPressed() {

        super.onBackPressed();

        finish();

    }

    private void initView() {

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        binding.includeView.titleTV.setText("Move File");

        binding.moveHereDesc.setVisibility(View.VISIBLE);

        binding.moveHereDesc.setText("Move "+document.getName()+" to another folder. Select folder to move.");

    }

    private void getCurrentUser() {

        UIUpdate.GetUIUpdate(this).destroy();
        showLoader();

        FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid())

                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        hideLoader();

                        if (snapshot.getValue() != null) {

                            user = snapshot.getValue(UserModel.class);

                            getCategories();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                        hideLoader();

                        UIUpdate.GetUIUpdate(MoveHereActivity.this).showAlertDialog("Alert", error.getMessage());
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

       RecyclerViewAdapterCategories adapterCategories = new RecyclerViewAdapterCategories(this, categories, "2022", true);

       adapterCategories.setListener(category -> {

           if(document != null) {

               document.setType(category);

               FirebaseDatabase.getInstance().getReference("ManagerFiles").child(user.getUserId())
                       .child(document.getId()).setValue(document);

           }

           finish();

       });

        binding.vaultRV.setAdapter(adapterCategories);

    }

}