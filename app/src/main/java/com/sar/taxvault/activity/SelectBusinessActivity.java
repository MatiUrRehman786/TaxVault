package com.sar.taxvault.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sar.taxvault.Model.NewsModel;
import com.sar.taxvault.Model.UserModel;
import com.sar.taxvault.adapters.RecyclerViewAdapterBusiness;
import com.sar.taxvault.adapters.RecyclerViewAdapterNews;
import com.sar.taxvault.databinding.ActivitySelectBusinessBinding;
import com.sar.taxvault.interfaces.BusinessIdCallback;

import org.bouncycastle.jcajce.provider.symmetric.ARC4;

import java.util.ArrayList;
import java.util.List;

public class SelectBusinessActivity extends BaseActivity implements BusinessIdCallback {

    ActivitySelectBusinessBinding binding;

    List<UserModel> businessList;

    private FirebaseAuth mAuth;

    public FirebaseDatabase rootNode;

    public DatabaseReference mDatabase;

    String type="normal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySelectBusinessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initFireBase();

        setView();

        getDataFromIntent();

        getBusinessList();

        setListener();

    }

    private void setListener() {

        binding.includeView.backIV.setOnClickListener(v -> finish());

    }

    private void getDataFromIntent() {

        Intent intent=getIntent();

        type=intent.getStringExtra("type");

        if(!type.equals("social")){

            binding.userTypeLabel.setVisibility(View.GONE);

            binding.userTypeSpinner.setVisibility(View.GONE);

            binding.arrowBtn.setVisibility(View.GONE);

        }

    }

    private void initFireBase() {

        rootNode = FirebaseDatabase.getInstance();

        mDatabase = rootNode.getReference("User");

        mAuth = FirebaseAuth.getInstance();

    }


    private void setView() {

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        binding.includeView.titleTV.setText("Business");

    }

    private void getBusinessList() {

        if (isOnline()) {

            businessList = new ArrayList<>();


            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    businessList.clear();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        try{

                            UserModel user = ds.getValue(UserModel.class);

                            user.setUserId(ds.getKey());

                            if(user.getUserType().equals("bussiness"))
                                businessList.add(user);
                        }catch (Exception e){

                        }


                    }

                    setRecyclerViewNews();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    System.out.println("GetNews" + databaseError.getCode());

                }
            });
        }
    }

    private void setRecyclerViewNews() {

        LinearLayoutManager layoutManager5 = new LinearLayoutManager(this);
        binding.businessRV.setLayoutManager(layoutManager5);

        RecyclerViewAdapterBusiness adapter = new RecyclerViewAdapterBusiness(this, businessList,this);

        binding.businessRV.setItemAnimator(new DefaultItemAnimator());

        binding.businessRV.setAdapter(adapter);

    }

    @Override
    public void onItemClick(String businessId,String businessName) {

        if(type.equals("social")){

            mDatabase.child(mAuth.getCurrentUser().getUid()).child("businessId").setValue(businessId);
            mDatabase.child(mAuth.getCurrentUser().getUid()).child("userType")
                    .setValue(binding.userTypeSpinner.getSelectedItem().toString());

            startActivity(new Intent(SelectBusinessActivity.this, Main.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));

            finish();

        }else{

            Signup.businessId=businessId;

            Signup.businessName=businessName;

            finish();

        }

    }
}