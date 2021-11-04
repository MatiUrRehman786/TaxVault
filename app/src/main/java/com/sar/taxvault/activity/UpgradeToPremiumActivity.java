package com.sar.taxvault.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.sar.taxvault.Model.UserModel;
import com.sar.taxvault.Stripe.MyEphemeralKeyProvider;
import com.sar.taxvault.Stripe.PaymentResultCallback;
import com.sar.taxvault.Stripe.response.StripeResponse;
import com.sar.taxvault.databinding.FragmentPremiumFeatureBinding;
import com.sar.taxvault.retrofit.Controller;
import com.sar.taxvault.utils.Constants;
import com.stripe.android.CustomerSession;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentSession;
import com.stripe.android.PaymentSessionConfig;
import com.stripe.android.PaymentSessionData;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentMethod;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpgradeToPremiumActivity extends BaseActivity {

    FragmentPremiumFeatureBinding binding;

    double amount = 0;

    Stripe stripe;

    PaymentSession paymentSession;

    Boolean readyToCharge = false;

    String clientSecret;
    String customerId;
    String orderClientSecret;

    UserModel user;

    Dialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = FragmentPremiumFeatureBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        getExtrasFromIntent();

        MyEphemeralKeyProvider.cusId = customerId;

        setListener();

    }

    private void getExtrasFromIntent() {

        customerId = getIntent().getStringExtra("cus_id");
        clientSecret = getIntent().getStringExtra("secret");

        user = getIntent().getParcelableExtra("user");

    }

    private void setListener() {

        binding.upgradeBtn.setOnClickListener(view -> choosePlan());

    }

    private void choosePlan() {

        final CharSequence[] items = {"Choose Yearly", "Choose Monthly",
                "Cancel"};

//        TextView title = new TextView(this);
//        title.setText("Add Photo!");
//        title.setBackgroundColor(Color.BLACK);
//        title.setPadding(10, 15, 15, 10);
//        title.setGravity(Gravity.CENTER);
//        title.setTextColor(Color.WHITE);
//        title.setTextSize(22);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(
                this);
//        builder.setCustomTitle(title);

        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Choose Monthly")) {

                amount = 5;

                setUpStripe();

            } else if (items[item].equals("Choose Yearly")) {

                amount = 60;

                setUpStripe();
            }
            else if (items[item].equals("Cancel")) {

                dialog.dismiss();

            }
        });
        builder.show();
    }


    private void setUpStripe() {

        PaymentConfiguration.init(
                UpgradeToPremiumActivity.this,
                Constants.stripeKey
        );

        stripe = new Stripe(UpgradeToPremiumActivity.this, Constants.stripeKey);

        CustomerSession.initCustomerSession(
                UpgradeToPremiumActivity.this,
                new MyEphemeralKeyProvider()
        );
        paymentSession = new PaymentSession(
                UpgradeToPremiumActivity.this,
                new PaymentSessionConfig.Builder()
                        .setShippingInfoRequired(false)
                        .setShippingMethodsRequired(false)
                        .setShippingInfoRequired(false)
                        .build()
        );
        paymentSession.init(
                new PaymentSession.PaymentSessionListener() {
                    @Override
                    public void onCommunicatingStateChanged(
                            boolean isCommunicating
                    ) {
                    }

                    @Override
                    public void onError(
                            int errorCode,
                            @Nullable String errorMessage
                    ) {
                        Log.d("EmpheralKey", "onError: " + errorMessage);
                        showError(errorMessage);
                    }

                    @Override
                    public void onPaymentSessionDataChanged(
                            @NonNull PaymentSessionData data
                    ) {
                        final PaymentMethod paymentMethod = data.getPaymentMethod();
                        if (data.getUseGooglePay()) {
                        } else {
                            Log.d("Order", "elsse: ");
                        }
                        readyToCharge = false;
                        if (data.isPaymentReadyToCharge()) {
                            readyToCharge = true;

                            Toast.makeText(UpgradeToPremiumActivity.this, "Ready to charge", Toast.LENGTH_SHORT).show();

                            RequestBody requestBody;
                            requestBody = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("amount", ((int) amount)*100 +"")//stripeval
                                    .addFormDataPart("pm_id", paymentMethod.id)
                                    .addFormDataPart("cus_id", customerId)
                                    .build();
                            Controller.getApi().accountPayment(requestBody)
                                    .enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(Call<String> call, Response<String> response) {

                                            if (response.body() != null) {
                                                try {
                                                    JSONObject jsonObject = new JSONObject(response.body());
                                                    orderClientSecret = jsonObject.getJSONObject("return_data").getString("key");
                                                    if (jsonObject.getJSONObject("return_data").getInt("error") == 0){

                                                        ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                                                                .create(orderClientSecret);
                                                        ;
                                                        stripe = new Stripe(
                                                                UpgradeToPremiumActivity.this,
                                                                Constants.stripeKey
                                                        );
                                                        stripe.confirmPayment((Activity) UpgradeToPremiumActivity.this, confirmParams);
                                                    }
                                                    else{
                                                        showErrorAlert("Transaction failed, please contact service provider or admin!");
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }


                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<String> call, Throwable t) {

                                        }
                                    });
                        } else {

                            if (paymentMethod != null) {

                                paymentSession.presentPaymentMethodSelection(MyEphemeralKeyProvider.cusId);
                                Log.d("Order", "Ready: ");

                            } else {
                                paymentSession.presentPaymentMethodSelection(MyEphemeralKeyProvider.cusId);
                            }
                        }

                    }
                }
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && !readyToCharge) {
            paymentSession.handlePaymentData(requestCode, resultCode, data);
        }
        if (readyToCharge)
            stripe.onPaymentResult(requestCode,
                    data,
                    new PaymentResultCallback(UpgradeToPremiumActivity.this));
    }

    public void showSuccessMessage() {

        if (amount == 5) {

            user.setCurrentPackage("monthly");

        } else if (amount == 60) {

            user.setCurrentPackage("yearly");

        }

        user.setPurchasedTSp(new Date().getTime());

        FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(user);

        finish();

        Toast.makeText(UpgradeToPremiumActivity.this, "Purchased Successfully", Toast.LENGTH_SHORT).show();

    }

    public void showError(String msg) {
        if (errorDialog != null && errorDialog.isShowing()) {
            return;
        }
        errorDialog = new AlertDialog.Builder(UpgradeToPremiumActivity.this)
                .setMessage(msg)
                .setTitle("Alert")
                .setPositiveButton("OK", (dialog, a) -> {
                    dialog.dismiss();
                })
                .create();
        errorDialog.show();
    }

}
