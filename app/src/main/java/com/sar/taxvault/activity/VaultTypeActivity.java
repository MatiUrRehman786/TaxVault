package com.sar.taxvault.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.sar.taxvault.R;
import com.sar.taxvault.databinding.ActivityVaultTypeBinding;

public class VaultTypeActivity extends AppCompatActivity {

    ActivityVaultTypeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityVaultTypeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setView();

    }

    private void setView() {

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        binding.includeView.titleTV.setText("Vault Category");

        binding.includeView.yearSpinner.setVisibility(View.INVISIBLE);

    }

}