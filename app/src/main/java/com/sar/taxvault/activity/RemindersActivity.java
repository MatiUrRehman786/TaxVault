package com.sar.taxvault.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sar.taxvault.Model.Remainder;
import com.sar.taxvault.Model.UserModel;
import com.sar.taxvault.adapters.RecyclerViewAdapterFiles;
import com.sar.taxvault.adapters.RecyclerViewAdapterNotifications;
import com.sar.taxvault.databinding.ActivityRemidersBinding;
import com.sar.taxvault.databinding.ActivityVaultBinding;
import com.sar.taxvault.utils.UIUpdate;

import java.util.ArrayList;
import java.util.List;

public class RemindersActivity extends AppCompatActivity {

    ActivityRemidersBinding binding;

    ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRemidersBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();

        setContentView(view);

        setView();
        setListeners();
        getData();
    }

    String getCurrentUserId() {

        return FirebaseAuth.getInstance().getCurrentUser().getUid();

    }

    private void getData() {

        UIUpdate.GetUIUpdate(this).destroy();
        UIUpdate.GetUIUpdate(this).setProgressDialog();

        valueEventListener = FirebaseDatabase.getInstance().getReference("Remainders").child(getCurrentUserId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        UIUpdate.GetUIUpdate(RemindersActivity.this).dismissProgressDialog();

                        if (snapshot.getValue() != null)

                            parseSnapshot(snapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                        UIUpdate.GetUIUpdate(RemindersActivity.this).dismissProgressDialog();

                        UIUpdate.GetUIUpdate(RemindersActivity.this).showAlertDialog("Alert", error.getMessage());
                    }
                });
    }

    private void parseSnapshot(DataSnapshot snapshot) {

        List<Remainder> list = new ArrayList<Remainder>();

        for (DataSnapshot remainderSnapshot : snapshot.getChildren()) {

            Remainder remainder = remainderSnapshot.getValue(Remainder.class);

            list.add(remainder);

        }

        setAdapter(list);

    }

    private void setView() {

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        binding.includeView.titleTV.setText("Reminders");
    }

    private void setListeners() {

        binding.includeView.backIV.setOnClickListener(view -> {

            finish();

//            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });
    }

    private void setAdapter(List<Remainder> remainders) {

        binding.notificationsRV.setLayoutManager(new LinearLayoutManager(this));

        RecyclerViewAdapterNotifications adapter = new RecyclerViewAdapterNotifications(this, remainders);

        binding.notificationsRV.setItemAnimator(new DefaultItemAnimator());

        binding.notificationsRV.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();

//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        valueEventListener = null;
    }

    public static void startActivity(Context context) {

        context.startActivity(new Intent(context, RemindersActivity.class));

    }
}