package com.sar.taxvault.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sar.taxvault.Model.Document;
import com.sar.taxvault.Model.UserModel;
import com.sar.taxvault.Stripe.MyEphemeralKeyProvider;
import com.sar.taxvault.activity.NewsActivity;
import com.sar.taxvault.activity.RemindersActivity;
import com.sar.taxvault.activity.SettingsActivity;
import com.sar.taxvault.activity.UpgradeToPremiumActivity;
import com.sar.taxvault.activity.VaultTypeActivity;
import com.sar.taxvault.databinding.FragmentTaxVaultBinding;
import com.sar.taxvault.retrofit.Controller;
import com.sar.taxvault.utils.UIUpdate;

import org.json.JSONObject;

import java.util.Date;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaxVaultFragment extends Fragment {

    private FragmentTaxVaultBinding binding;

    UserModel user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTaxVaultBinding.inflate(inflater, container, false);

        UIUpdate.GetUIUpdate(getActivity()).destroy();

        setListeners();

        getMyProfile();

        return binding.getRoot();

    }

    private void getData(UserModel user) {

        UIUpdate.GetUIUpdate(getActivity()).setProgressDialog();

        FirebaseDatabase.getInstance().getReference("Files")
                .child(user.getBusinessId())
                .child(user.getUserId())
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

            Document document = child.getValue(Document.class);

            bytes = bytes + document.getSize();

        }

        long GB = 1073741824;
        int maxSizeGB = 1;

        String currentPackage = user.getCurrentPackage();

        if (currentPackage != null) {

            if (user.getPurchasedTSp() != null) {

                Long ts = user.getPurchasedTSp();

                int diffInDays = (int) ((ts - new Date().getTime())
                        / (1000 * 60 * 60 * 24));

                if (user.getCurrentPackage().equalsIgnoreCase("monthly")) {

                    if (diffInDays < 30) {

                        maxSizeGB = 3;
                    }

                }


                if (user.getCurrentPackage().equalsIgnoreCase("yearly")) {

                    if (diffInDays < 365) {

                        maxSizeGB = 3;
                    }

                }
            }
        }

        if (bytes < (GB * maxSizeGB)) {

            Long percentUsed = (bytes * 100) / GB;

            double sizeInMB = new Long(bytes).doubleValue() / 1048576;

            binding.circularProgressBar.setProgress(new Long(percentUsed).intValue());

            binding.usedTV.setText("Used " + percentUsed.intValue() + "%");
            binding.percentTV.setText(percentUsed.intValue() + "%");

            binding.pointsTV.setText(roundAndConvertInGB(sizeInMB, 5) + "GB / " + maxSizeGB + "GB");

        } else {
            binding.circularProgressBar.setProgress(100);

            binding.usedTV.setText("Used " + 100 + "%");
            binding.percentTV.setText(100 + "%");
            if (user.getCurrentPackage() == null)
                binding.pointsTV.setText(1 + "GB / " + 1 + "GB");
            else
                binding.pointsTV.setText("Unlimited");
        }
    }

    public double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    public String roundAndConvertInGB(double value, int precision) {

        double gb = value / 1024;

        return String.format("%.5f", gb);

    }

    private void getMyProfile() {

        UIUpdate.GetUIUpdate(getActivity()).setProgressDialog();

        FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        UIUpdate.GetUIUpdate(getActivity()).dismissProgressDialog();

                        if (snapshot.getValue() != null) {

                            user = snapshot.getValue(UserModel.class);
                            user.setUserId(snapshot.getKey());

                            getData(user);

                            setVisibilities();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                        UIUpdate.GetUIUpdate(getActivity()).dismissProgressDialog();

                        UIUpdate.GetUIUpdate(getActivity()).showAlertDialog("Alert", error.getMessage());
                    }
                });
    }

    private void setVisibilities() {

        if (user.getCurrentPackage() != null) {

            Long ts = user.getPurchasedTSp();

            int diffInDays = (int) ((ts - new Date().getTime())
                    / (1000 * 60 * 60 * 24));

            if (user.getCurrentPackage().equalsIgnoreCase("monthly")) {

                if (diffInDays < 30) {

                    binding.upgradeIV.setVisibility(View.INVISIBLE);
//                    binding.percentTV.setVisibility(View.INVISIBLE);
//                    binding.circularProgressBar.setVisibility(View.INVISIBLE);
                    binding.upgradeTV.setVisibility(View.INVISIBLE);
//                    binding.pointsTV.setText("Unlimited");
                    binding.freeMembershipTV.setText("Monthly");
                }

            }


            if (user.getCurrentPackage().equalsIgnoreCase("yearly")) {

                if (diffInDays < 365) {

                    binding.upgradeIV.setVisibility(View.INVISIBLE);
//                    binding.percentTV.setVisibility(View.INVISIBLE);
                    binding.upgradeTV.setVisibility(View.INVISIBLE);
//                    binding.pointsTV.setText("Unlimited");
                    binding.freeMembershipTV.setText("Yearly");
                }

            }

        }

    }

    private void setListeners() {

        binding.vaultCL.setOnClickListener(view -> {

            Intent intent = new Intent(getActivity(), VaultTypeActivity.class);

            startActivity(intent);

//            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });

        binding.newsCL.setOnClickListener(view -> {

            Intent intent = new Intent(getActivity(), NewsActivity.class);

            startActivity(intent);

//            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });

        binding.remindersCL.setOnClickListener(view -> {

            RemindersActivity.startActivity(getActivity());

//            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });

        binding.settingsCL.setOnClickListener(view -> {

            Intent intent = new Intent(getActivity(), SettingsActivity.class);

            startActivity(intent);

//            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });

        binding.upgradeIV.setOnClickListener(view -> checkPackage());
    }

    void checkPackage() {

        if (user.getCustomerId() == null) {

            createCustomer();

        } else {

            startActivity(new Intent(getActivity(),
                    UpgradeToPremiumActivity.class)
                    .putExtra("secret", user.getClientSecret())
                    .putExtra("user", user)
                    .putExtra("cus_id", user.getCustomerId())
            );

        }
    }

    private void createCustomer() {

        MyEphemeralKeyProvider.cusId = "1";

        UIUpdate.GetUIUpdate(getActivity()).destroy();
        UIUpdate.GetUIUpdate(getActivity()).setProgressDialog();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", user.getFirstName() + user.getLastName())
                .addFormDataPart("email", user.getEmail())
                .addFormDataPart("cus_id", "")
                .build();
        Controller.getApi().createCustomer(requestBody)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        UIUpdate.GetUIUpdate(getActivity()).dismissProgressDialog();

                        if (response.body() != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body());
                                String customerId = jsonObject.getString("cus_id");
                                String clientSecret = jsonObject.getString("key");

                                MyEphemeralKeyProvider.cusId = customerId;

                                String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                user.setClientSecret(clientSecret);
                                user.setCustomerId(customerId);
                                FirebaseDatabase.getInstance().getReference("User").child(myId)
                                        .setValue(user);

                                startActivity(new Intent(getActivity(),
                                        UpgradeToPremiumActivity.class)
                                        .putExtra("secret", clientSecret)
                                        .putExtra("cus_id", customerId)
                                        .putExtra("user", user)
                                );

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                    }
                });
    }
}