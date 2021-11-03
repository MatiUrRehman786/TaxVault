package com.sar.taxvault.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sar.taxvault.activity.NewsActivity;
import com.sar.taxvault.activity.RemindersActivity;
import com.sar.taxvault.activity.SettingsActivity;
import com.sar.taxvault.activity.VaultActivity;
import com.sar.taxvault.databinding.FragmentTaxVaultBinding;

public class TaxVaultFragment extends Fragment {

    private FragmentTaxVaultBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTaxVaultBinding.inflate(inflater, container, false);

        setListeners();

        return binding.getRoot();

    }

    private void setListeners() {

        binding.vaultCL.setOnClickListener(view -> {

            Intent intent = new Intent(getActivity(), VaultActivity.class);

            startActivity(intent);

            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });

        binding.newsCL.setOnClickListener(view -> {

            Intent intent = new Intent(getActivity(), NewsActivity.class);

            startActivity(intent);

            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });

        binding.remindersCL.setOnClickListener(view -> {

            Intent intent = new Intent(getActivity(), RemindersActivity.class);

            startActivity(intent);

            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });

        binding.settingsCL.setOnClickListener(view -> {

            Intent intent = new Intent(getActivity(), SettingsActivity.class);

            startActivity(intent);

            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });
    }
}