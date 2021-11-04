package com.sar.taxvault.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sar.taxvault.Model.Document;
import com.sar.taxvault.Model.UserModel;
import com.sar.taxvault.activity.NewsActivity;
import com.sar.taxvault.activity.RemindersActivity;
import com.sar.taxvault.activity.SettingsActivity;
import com.sar.taxvault.activity.VaultActivity;
import com.sar.taxvault.activity.VaultTypeActivity;
import com.sar.taxvault.databinding.FragmentTaxVaultBinding;
import com.sar.taxvault.utils.UIUpdate;

import java.util.ArrayList;
import java.util.List;

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

        getMyProfile();

        return binding.getRoot();

    }

    private void getData(UserModel user) {

        UIUpdate.GetUIUpdate(getActivity()).setProgressDialog();

        FirebaseDatabase.getInstance().getReference("Files")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        UIUpdate.GetUIUpdate(getActivity()).dismissProgressDialog();

                        if (snapshot.getValue() != null)
                            parseSnapshot(snapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                        UIUpdate.GetUIUpdate(getActivity()).dismissProgressDialog();

                        UIUpdate.GetUIUpdate(getActivity()).showAlertDialog("Alert", error.getMessage());
                    }
                });
    }


    private void parseSnapshot(DataSnapshot snapshot) {

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Long bytes = 0l;

        for (DataSnapshot child : snapshot.getChildren()) {

            for (DataSnapshot documentSnapshot : child.getChildren()) {

                Document document = documentSnapshot.getValue(Document.class);

                if (document.getUserId().equalsIgnoreCase(userId)) {

                    bytes = bytes + document.getSize();
                }
            }
        }

        long GB = 1073741824;

        if (bytes < GB) {

            Long percentUsed = (bytes * 100) / GB;

            double sizeInMB = new Long(bytes).doubleValue()/1048576;
            double sizeInGB = new Long(GB).doubleValue()/1048576;

            binding.circularProgressBar.setProgress(new Long(percentUsed).intValue());

            binding.usedTV.setText("Used " + percentUsed.intValue() + "%");
            binding.percentTV.setText(percentUsed.intValue() + "%");
            binding.pointsTV.setText(round(sizeInMB,5) + "MB / " + round(sizeInGB,5)+"MB");
        }
        else
        {
            binding.circularProgressBar.setProgress(100);

            binding.usedTV.setText("Used " + 100 + "%");
            binding.percentTV.setText(100 + "%");
            binding.pointsTV.setText(1024 + "MB / " + 1024+"MB");
        }
    }

    public double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    private void getMyProfile() {

        UIUpdate.GetUIUpdate(getActivity()).setProgressDialog();

        FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        UIUpdate.GetUIUpdate(getActivity()).dismissProgressDialog();

                        if (snapshot.getValue() != null) {
                            UserModel user = snapshot.getValue(UserModel.class);

                            getData(user);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                        UIUpdate.GetUIUpdate(getActivity()).dismissProgressDialog();

                        UIUpdate.GetUIUpdate(getActivity()).showAlertDialog("Alert", error.getMessage());
                    }
                });
    }

    private void setData(UserModel user) {

        binding.circularProgressBar.setProgress(new Float(user.getPercentShared()));

        binding.usedTV.setText("Used " + user.getPercentShared() + "%");
        binding.percentTV.setText(user.getPercentShared() + "%");
        binding.pointsTV.setText(user.getPostCount() + " / " + user.getMaxPost());
    }


    private void setListeners() {

        binding.vaultCL.setOnClickListener(view -> {

            Intent intent = new Intent(getActivity(), VaultTypeActivity.class);

            startActivity(intent);

            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });

        binding.newsCL.setOnClickListener(view -> {

            Intent intent = new Intent(getActivity(), NewsActivity.class);

            startActivity(intent);

            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });

        binding.remindersCL.setOnClickListener(view -> {

            RemindersActivity.startActivity(getActivity());

            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });

        binding.settingsCL.setOnClickListener(view -> {

            Intent intent = new Intent(getActivity(), SettingsActivity.class);

            startActivity(intent);

            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });
    }
}