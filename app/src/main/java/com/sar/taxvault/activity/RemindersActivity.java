package com.sar.taxvault.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.sar.taxvault.adapters.RecyclerViewAdapterFiles;
import com.sar.taxvault.adapters.RecyclerViewAdapterNotifications;
import com.sar.taxvault.databinding.ActivityRemidersBinding;
import com.sar.taxvault.databinding.ActivityVaultBinding;

public class RemindersActivity extends AppCompatActivity {

    ActivityRemidersBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRemidersBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setView();
        setListeners();
        initRecyclerViewNotifications();
    }

    private void setView() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding.includeView.titleTV.setText("Reminders");
    }

    private void setListeners() {
        binding.includeView.backIV.setOnClickListener(view -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    private void initRecyclerViewNotifications(){
        LinearLayoutManager layoutManager5 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.notificationsRV.setLayoutManager(layoutManager5);

        RecyclerViewAdapterNotifications adapter = new RecyclerViewAdapterNotifications(this,null);
        binding.notificationsRV.setItemAnimator( new DefaultItemAnimator());
        binding.notificationsRV.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}