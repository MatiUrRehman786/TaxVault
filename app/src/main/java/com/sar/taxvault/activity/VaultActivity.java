package com.sar.taxvault.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.sar.taxvault.adapters.RecyclerViewAdapterFiles;
import com.sar.taxvault.adapters.RecyclerViewAdapterNews;
import com.sar.taxvault.databinding.ActivityNewsBinding;
import com.sar.taxvault.databinding.ActivityVaultBinding;

public class VaultActivity extends AppCompatActivity {

    ActivityVaultBinding binding;

    String category = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityVaultBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        getExtrasFromIntent();

        setView();

        setListeners();

        initRecyclerViewFiles();

    }

    private void getExtrasFromIntent() {

        if (getIntent().getExtras() == null)
            return;

        Bundle b = getIntent().getExtras();

        category = b.getString("category");

    }

    private void setView() {
        binding.includeView.titleTV.setText("Vault");
        binding.includeView.yearSpinner.setVisibility(View.VISIBLE);
    }

    private void setListeners() {
        binding.includeView.backIV.setOnClickListener(view -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    private void initRecyclerViewFiles(){
        LinearLayoutManager layoutManager5 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.filesRV.setLayoutManager(layoutManager5);

        RecyclerViewAdapterFiles adapter = new RecyclerViewAdapterFiles(this,null, true);
        binding.filesRV.setItemAnimator( new DefaultItemAnimator());
        binding.filesRV.setAdapter(adapter);
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}