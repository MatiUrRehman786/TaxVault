package com.sar.taxvault.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class VaultTypeActivity extends BaseActivity {

    ActivityVaultTypeBinding binding;

    UserModel user;

    String year;

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

//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

    private void initView() {

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(new Date());

        year = String.valueOf(calendar.get(Calendar.YEAR));

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        binding.includeView.titleTV.setText("Categories");

        String[] dates = {"2018","2019","2020","2021","2022","2023","2024","2025","2026","2027","2027"};

        binding.includeView.yearSpinner.setVisibility(View.VISIBLE);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,dates);

        binding.includeView.yearSpinner.setAdapter(adapter);
        binding.includeView.yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    year = binding.includeView.yearSpinner.getSelectedItem().toString();

                    if(categoriesAdapter != null) {

                        categoriesAdapter.setYear(year);

                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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

    RecyclerViewAdapterCategories categoriesAdapter;
    private void setAdapter(List<String> categories) {

        binding.vaultRV.setLayoutManager(new LinearLayoutManager(this));

        categoriesAdapter = new RecyclerViewAdapterCategories(this, categories,year, false);
        binding.vaultRV.setAdapter(categoriesAdapter);

    }

}