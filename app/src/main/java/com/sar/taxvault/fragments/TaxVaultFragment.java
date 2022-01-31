package com.sar.taxvault.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import com.sar.taxvault.Model.SelectedManager;
import com.sar.taxvault.Model.UserModel;
import com.sar.taxvault.Stripe.MyEphemeralKeyProvider;
import com.sar.taxvault.activity.ActivitySendMessage;
import com.sar.taxvault.activity.BusinessUser;
import com.sar.taxvault.activity.ChatActivity;
import com.sar.taxvault.activity.NewsActivity;
import com.sar.taxvault.activity.RemindersActivity;
import com.sar.taxvault.activity.SelectBusinessActivity;
import com.sar.taxvault.activity.SettingsActivity;
import com.sar.taxvault.activity.UpgradeToPremiumActivity;
import com.sar.taxvault.activity.VaultTypeActivity;
import com.sar.taxvault.databinding.FragmentTaxVaultBinding;
import com.sar.taxvault.retrofit.Controller;
import com.sar.taxvault.utils.UIUpdate;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaxVaultFragment extends BaseFragment {

    private FragmentTaxVaultBinding binding;

    UserModel user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTaxVaultBinding.inflate(inflater, container, false);

        UIUpdate.GetUIUpdate(getActivity()).destroy();

        setListeners();

//        getMyProfile();

        return binding.getRoot();

    }

    ValueEventListener dataEventListener;

    private void getData(UserModel user) {

        UIUpdate.GetUIUpdate(getActivity()).destroy();
        showLoader();

        dataEventListener = FirebaseDatabase.getInstance().getReference("ManagerFiles")
                .child(user.getUserId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot snapshot) {

                        hideLoader();

                        if (snapshot.getValue() != null)

                            parseSnapshot(snapshot);
                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError error) {

                        hideLoader();

                        UIUpdate.GetUIUpdate(getActivity()).showAlertDialog("Alert", error.getMessage());
                    }
                });
    }


    @SuppressLint("SetTextI18n")
    private void parseSnapshot(DataSnapshot snapshot) {

        long bytes = 0L;

        for (DataSnapshot child : snapshot.getChildren()) {

            Document document = child.getValue(Document.class);

            if (document != null) {
                bytes = bytes + document.getSize();
            }

        }

        long GB = 1073741824;
        int maxSizeGB = 1;

        if (bytes < (GB * maxSizeGB)) {

            long percentUsed = (bytes * 100) / GB;

            double sizeInMB = Long.valueOf(bytes).doubleValue() / 1048576;

            binding.circularProgressBar.setProgress(Long.valueOf(percentUsed).intValue());

            binding.usedTV.setText("Used " + (int) percentUsed + "%");

            binding.pointsTV.setText(roundAndConvertInGB(sizeInMB, 5) + "GB");

        } else {

            binding.circularProgressBar.setProgress(100);

            binding.pointsTV.setText("Used " + 100 + "%");
        }
    }

    public double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    @SuppressLint("DefaultLocale")
    public String roundAndConvertInGB(double value, int precision) {

        double gb = value / 1024;

        return String.format("%.5f", gb);

    }

    @Override
    public void onPause() {
        super.onPause();

        profileEventListener = null;
        dataEventListener = null;

    }

    @Override
    public void onResume() {
        super.onResume();

        getMyProfile();
    }

    ValueEventListener profileEventListener;

    private void getMyProfile() {

        showLoader();

        profileEventListener = FirebaseDatabase.getInstance().getReference("User").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot snapshot) {

                        hideLoader();

                        if (snapshot.getValue() != null) {

                            user = snapshot.getValue(UserModel.class);
                            if (user != null) {
                                user.setUserId(snapshot.getKey());
                            }

                            getData(user);

                            setVisibilities();

                        }
                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError error) {

                        hideLoader();

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
//                    binding.freeMembershipTV.setText("Monthly");
                }

            }


            if (user.getCurrentPackage().equalsIgnoreCase("yearly")) {

                if (diffInDays < 365) {

                    binding.upgradeIV.setVisibility(View.INVISIBLE);
//                    binding.percentTV.setVisibility(View.INVISIBLE);
                    binding.upgradeTV.setVisibility(View.INVISIBLE);
//                    binding.pointsTV.setText("Unlimited");
//                    binding.freeMembershipTV.setText("Yearly");
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

            RemindersActivity.startActivity(requireActivity());

//            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });

        binding.settingsCL.setOnClickListener(view -> {

            Intent intent = new Intent(getActivity(), SettingsActivity.class);

            startActivity(intent);

//            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });

        binding.businessUserCL.setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(), BusinessUser.class);

            startActivity(intent);

        });

        binding.upgradeIV.setOnClickListener(view -> checkPackage());

        binding.messagesCL.setOnClickListener(v -> {

            checkManager();

        });
    }

    ArrayList<SelectedManager> managersList;
    List<String> names;


    private void checkManager() {

        showLoader();

        FirebaseDatabase.getInstance().getReference("userBusiness").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot snapshot) {

                        managersList = new ArrayList<>();
                        names = new ArrayList<>();

                        hideLoader();

                        if (snapshot.getValue() != null) {

                            SelectedManager manager = snapshot.getValue(SelectedManager.class);

                            if (manager != null) {

                                manager.id = snapshot.getKey();

                                names.add(manager.businessName);

                            }

                            managersList.add(manager);
                        }

                        if (managersList.size() == 0) {

                            showErrorAlert("Please select manager first");

                        } else {

                            startActivity(new Intent(getActivity(), ChatActivity.class));

                        }

                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError error) {

                        hideLoader();

                    }
                });

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
        showLoader();

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

                        hideLoader();

                        if (response.body() != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body());
                                String customerId = jsonObject.getString("cus_id");
                                String clientSecret = jsonObject.getString("key");

                                MyEphemeralKeyProvider.cusId = customerId;

                                String myId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

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