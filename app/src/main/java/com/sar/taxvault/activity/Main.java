package com.sar.taxvault.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.Constants;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sar.taxvault.Model.UserModel;
import com.sar.taxvault.R;
import com.sar.taxvault.Stripe.MyEphemeralKeyProvider;
import com.sar.taxvault.Stripe.PaymentResultCallback;
import com.sar.taxvault.databinding.ActivityMainBinding;
import com.sar.taxvault.fragments.PremiumFeatureFragment;
import com.sar.taxvault.fragments.TaxVaultFragment;
import com.sar.taxvault.retrofit.Controller;
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

import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Main extends BaseActivity {

    private ActivityMainBinding binding;

    private FirebaseAuth mAuth;

    public FirebaseDatabase rootNode;

    public DatabaseReference mDatabase;

    Stripe stripe;

    PaymentSession paymentSession;

    Boolean readyToCharge = false;

    String clientSecret;
    String customerId;
    String orderClientSecret;

    Dialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        initFireBase();

        updateFireBaseToken();

        setView();

        loadFragment(new TaxVaultFragment());

        setDrawer();

        setListeners();

//        Todo: Create customer

        createCustomer();
    }

    private void createCustomer() {

//        MyEphemeralKeyProvider.cusId = "1";
//
//        RequestBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("name", Constants.currentUser.getName())
//                .addFormDataPart("email", Constants.currentUser.getEmail())
//                .addFormDataPart("cus_id", "")
//                .build();
//        Controller.getApi().createCustomer(requestBody)
//                .enqueue(new Callback<String>() {
//                    @Override
//                    public void onResponse(Call<String> call, Response<String> response) {
//                        if (response.body() != null) {
//                            try {
//                                try {
//                                    JSONObject jsonObject = new JSONObject(response.body());
//                                    customerId = jsonObject.getString("cus_id");
//                                    clientSecret = jsonObject.getString("key");
//                                    EmpeheralKeyProvider.cusId = customerId;
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<String> call, Throwable t) {
//                    }
//                });
    }
//
//    private void setUpStripe() {
//
//        PaymentConfiguration.init(
//                Main.this,
//                Constants.stripePublishableKey
//        );
//
//        stripe = new Stripe(Main.this, Constants.stripePublishableKey);
//        CustomerSession.initCustomerSession(
//                Main.this,
//                new MyEphemeralKeyProvider()
//        );
//        paymentSession = new PaymentSession(
//                Main.this,
//                new PaymentSessionConfig.Builder()
//                        .setShippingInfoRequired(false)
//                        .setShippingMethodsRequired(false)
//                        .setShippingInfoRequired(false)
//                        .build()
//        );
//        paymentSession.init(
//                new PaymentSession.PaymentSessionListener() {
//                    @Override
//                    public void onCommunicatingStateChanged(
//                            boolean isCommunicating
//                    ) {
//                    }
//
//                    @Override
//                    public void onError(
//                            int errorCode,
//                            @Nullable String errorMessage
//                    ) {
//                        Log.d("EmpheralKey", "onError: " + errorMessage);
//                        showError(errorMessage);
//                    }
//
//                    @Override
//                    public void onPaymentSessionDataChanged(
//                            @NonNull PaymentSessionData data
//                    ) {
//                        final PaymentMethod paymentMethod = data.getPaymentMethod();
//                        if (data.getUseGooglePay()) {
//                        } else {
//                            Log.d("Order", "elsse: ");
//                        }
//                        readyToCharge = false;
//                        if (data.isPaymentReadyToCharge()) {
//                            readyToCharge = true;
//
//                            Stripe stripe;
//
//
////                    PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
////                    if (params != null) {
//
//
//                            Toast.makeText(Main.this, "Ready to charge", Toast.LENGTH_SHORT).show();
//
//                            RequestBody requestBody = new MultipartBody.Builder()
//                                    .setType(MultipartBody.FORM)
////                                    .addFormDataPart("amount", ((int) amount)*100 +"")//stripeval
//                                    .addFormDataPart("pm_id", paymentMethod.id)
//                                    .addFormDataPart("cus_id", customerId)
//                                    .build();
//                            Controller.getApi().accountPayment(requestBody)
//                                    .enqueue(new Callback<String>() {
//                                        @Override
//                                        public void onResponse(Call<String> call, Response<String> response) {
//                                            if (response.body() != null) {
//                                                try {
//                                                    JSONObject jsonObject = new JSONObject(response.body());
//                                                    orderClientSecret = jsonObject.getJSONObject("return_data").getString("key");
//                                                    if (jsonObject.getJSONObject("return_data").getInt("error") == 0){
//
//                                                        ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
//                                                                .create(list.get(i).getOrderClientSecret());
//                                                        final Context context = mContext;
//                                                        stripe = new Stripe(
//                                                                context,
//                                                                Constants.stripePublishableKey
//                                                        );
//                                                        stripe.confirmPayment((Activity) Main.this, confirmParams);
//                                                    }
//                                                    else{
//                                                        showErrorAlert("Transaction failed, please contact service provider or admin!");
//                                                    }
//                                                } catch (JSONException e) {
//                                                    e.printStackTrace();
//                                                }
////                                                StripeResponse stripeResponse = new Gson().fromJson(response.body(), StripeResponse.class);
////                                                stripe.confirmPayment(Booking.this,
////                                                        ConfirmPaymentIntentParams.createWithPaymentMethodId(
////                                                                paymentMethod.id,
////                                                                stripeResponse.getPaymentintentId().getClientSecret(),
////                                                                "https://api.stripe.com" + stripeResponse.getPaymentintentId().getCharges()
////                                                                        .getUrl()
////                                                        )
////                                                );
//
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onFailure(Call<String> call, Throwable t) {
//                                        }
//                                    });
//                        } else {
//                            if (paymentMethod != null) {
//
//                                paymentSession.presentPaymentMethodSelection(MyEphemeralKeyProvider.cusId);
//                                Log.d("Order", "Ready: ");
//
//                            } else {
//                                paymentSession.presentPaymentMethodSelection(MyEphemeralKeyProvider.cusId);
//                            }
//                        }
//
//                    }
//                }
//        );
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && !readyToCharge) {
            paymentSession.handlePaymentData(requestCode, resultCode, data);
        }
        if (readyToCharge)
            stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(Main.this));
    }

    public void showSuccessMessage() {
//        PaymentFinished paymentFinished = new PaymentFinished();
        Toast.makeText(Main.this, "Escrow Successfully", Toast.LENGTH_SHORT).show();
    }

    public void showError(String msg) {
        if(errorDialog!=null && errorDialog.isShowing()){
            return;
        }
        errorDialog =  new AlertDialog.Builder(Main.this)
                .setMessage(msg)
                .setTitle("Alert")
                .setPositiveButton("OK",(dialog,a)->{
                    dialog.dismiss();
                })
                .create();
        errorDialog.show();
    }

    private void initFireBase() {

        rootNode = FirebaseDatabase.getInstance();

        mDatabase = rootNode.getReference("User");

        mAuth = FirebaseAuth.getInstance();

    }


    private void setView() {

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        binding.includeView.titleTV.setText("Tax Vault");

        getUserData();
    }

    private void setDrawer() {

        DuoDrawerToggle drawerToggle = new DuoDrawerToggle(this, binding.drawer,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        binding.drawer.setDrawerListener(drawerToggle);

        drawerToggle.syncState();

    }

    private void setListeners() {

        binding.includeView.menuIV.setOnClickListener(v -> {

            checkOpenOrCloseDrawer();

        });

        binding.navView.drawerMenuIV.setOnClickListener(v -> {

            checkOpenOrCloseDrawer();

        });

        binding.navView.homeTV.setOnClickListener(v -> {

            binding.includeView.titleTV.setText("Tax Vault");

            checkOpenOrCloseDrawer();

            startActivity(new Intent(this, VaultTypeActivity.class));

            binding.includeView.yearSpinner.setVisibility(View.INVISIBLE);

        });

        binding.navView.filesTV.setOnClickListener(v -> {

//            binding.includeView.titleTV.setText("");

            checkOpenOrCloseDrawer();

            Intent intent = new Intent(Main.this, VaultTypeActivity.class);

            startActivity(intent);

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


            binding.includeView.yearSpinner.setVisibility(View.VISIBLE);

        });

        binding.navView.notificationTV.setOnClickListener(v -> {

            checkOpenOrCloseDrawer();

            RemindersActivity.startActivity(this);

            binding.includeView.yearSpinner.setVisibility(View.INVISIBLE);

        });


        binding.navView.upgradeTV.setOnClickListener(v -> {

            binding.includeView.titleTV.setText("Premium Feature");

            checkOpenOrCloseDrawer();

            loadFragment(new PremiumFeatureFragment());

            binding.includeView.yearSpinner.setVisibility(View.INVISIBLE);

        });

        binding.navView.settingsTV.setOnClickListener(v -> {

            checkOpenOrCloseDrawer();

            Intent intent = new Intent(this, SettingsActivity.class);

            startActivity(intent);

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });

    }

    private void checkOpenOrCloseDrawer() {

        if (binding.drawer.isDrawerOpen()) {

            binding.drawer.closeDrawer(Gravity.LEFT);

        } else {

            binding.drawer.openDrawer(Gravity.LEFT);

        }
    }

    private void loadFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(binding.mainFragmentContainer.getId(), fragment);

        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();

    }

    private void getUserData() {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        DatabaseReference usersRef = rootRef.child("User").child(mAuth.getCurrentUser().getUid());

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserModel user = dataSnapshot.getValue(UserModel.class);

                binding.navView.userNameTV.setText(user.getFirstName() + " " + user.getLastName());

                binding.navView.userEmailTV.setText(user.getEmail());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("userDataResponse", databaseError.getMessage());
            }

        };

        usersRef.addListenerForSingleValueEvent(valueEventListener);

    }

    private void updateFireBaseToken() {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Log.w("FireBase Token", "Fetching FCM registration token failed", task.getException());

                        return;

                    } else {

                        String token = task.getResult();

                        addTokenToDB(token);

                        Log.d("FireBase Token", token);
                    }

                });
    }

    private void addTokenToDB(String token) {

        if (isOnline()) {

            mDatabase.child(mAuth.getCurrentUser().getUid()).child("token").setValue(token);

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (getSupportFragmentManager().getBackStackEntryCount() < 1) {

            finish();

        } else {

            super.onBackPressed();

        }
    }
}