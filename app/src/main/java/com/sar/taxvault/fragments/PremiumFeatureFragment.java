package com.sar.taxvault.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sar.taxvault.R;
import com.sar.taxvault.activity.Login;
import com.sar.taxvault.databinding.FragmentPremiumFeatureBinding;
import com.sar.taxvault.databinding.FragmentProfileBinding;

public class PremiumFeatureFragment extends Fragment {
    private FragmentPremiumFeatureBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPremiumFeatureBinding.inflate(inflater, container, false);

        setListeners();
        return binding.getRoot();
    }

    private void setListeners() {
//        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), Login.class);
//                startActivity(intent);
//                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//            }
//        });
    }
}