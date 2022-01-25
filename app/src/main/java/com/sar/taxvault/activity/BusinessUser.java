package com.sar.taxvault.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sar.taxvault.Model.CustomUserModel;
import com.sar.taxvault.Model.SelectedManager;
import com.sar.taxvault.adapters.ManagerAdapter;
import com.sar.taxvault.adapters.RecyclerViewAdapterBusiness;
import com.sar.taxvault.databinding.ActivityBusinessUsersBinding;
import com.sar.taxvault.interfaces.BusinessIdCallback;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BusinessUser extends BaseActivity {

    ActivityBusinessUsersBinding binding;

    CustomUserModel user;

    List<CustomUserModel> businesses;
    List<SelectedManager> selectedManagers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityBusinessUsersBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.includeView.backIV.setOnClickListener(v -> finish());

        getBusinessUsers();

    }

    private void getBusinessUsers() {

        FirebaseDatabase.getInstance().getReference("User")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                        onDataChanged(snapshot);

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

    }

    private void onDataChanged(DataSnapshot snapshot) {

        businesses = new ArrayList<>();

        if (snapshot.exists()) {

            for (DataSnapshot child : snapshot.getChildren()) {

                try {

                    CustomUserModel userModel = child.getValue(CustomUserModel.class);

                    if (userModel != null) {

                        userModel.setUserId(child.getKey());

                        if (child.getKey().equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                            BusinessUser.this.user = userModel;

                        } else if (userModel.getUserType().equalsIgnoreCase("bussiness")) {

                            businesses.add(userModel);

                        }

                    }

                    setAdapter();

                } catch (Exception e) {

                    Log.d(TAG, "onDataChange: " + child.getKey());

                }

            }

        }

    }

    private void setAdapter() {

        binding.vaultRV.setLayoutManager(new LinearLayoutManager(BusinessUser.this));

        binding.vaultRV.setAdapter(new RecyclerViewAdapterBusiness(BusinessUser.this, businesses, new BusinessIdCallback() {
            @Override
            public void onItemClick(String businessId, String businessName) {

            }

            @Override
            public void onUserSelected(CustomUserModel user) {

                selectBusinessUser(user);

            }
        }));

        setMyBusiness();

    }

    private void selectBusinessUser(CustomUserModel manager) {

        SelectedManager selectedManager = new SelectedManager();
        selectedManager.businessName = manager.firstName+" "+manager.lastName;
        selectedManager.businessStatus = false;

        String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String managerId = manager.getUserId();

        FirebaseDatabase.getInstance().getReference("userBusiness")
                .child(myId).child(managerId)
                .setValue(selectedManager);

        FirebaseDatabase.getInstance().getReference("userBusiness")
                .child(managerId).child(myId)
                .setValue(selectedManager);

    }

    private void setMyBusiness() {

        FirebaseDatabase.getInstance().getReference("userBusiness")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                        selectedManagers = new ArrayList<>();

                        if(snapshot.exists()) {

                            for (DataSnapshot child: snapshot.getChildren()) {

                                SelectedManager selectedManager = child.getValue(SelectedManager.class);

                                if(selectedManager != null) {

                                    selectedManager.id = child.getKey();

                                    selectedManagers.add(selectedManager);
                                }

                            }



                        }

                        setSelectedManagers();

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

//        binding.managerCL.setVisibility(View.GONE);
//
//        for (CustomUserModel user : businesses) {
//
//            if (this.user.getBusinessId().equalsIgnoreCase(user.getUserId())) {
//
//                binding.managerCL.setVisibility(View.VISIBLE);
//
//                binding.managerNameTV.setText(user.getFirstName() + " " + user.getLastName());
//
//                setListener();
//
//                return;
//            }
//
//        }

    }

    private void setSelectedManagers() {

        binding.selectedManagersRV.setLayoutManager(new LinearLayoutManager(this));

        binding.selectedManagersRV.setAdapter(new ManagerAdapter(BusinessUser.this, selectedManagers));
    }

    private void setListener() {

//        binding.deleteIV.setOnClickListener(v -> deleteBusiness());

    }

    private void deleteBusiness() {

        user.setBusinessId("NULL");

        user.businessStatus = "NULL";

        FirebaseDatabase.getInstance().getReference("User")
                .child(user.getUserId())
                .setValue(user);

        setMyBusiness();
    }

}
