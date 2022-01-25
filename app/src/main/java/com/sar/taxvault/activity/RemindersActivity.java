package com.sar.taxvault.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sar.taxvault.Model.Remainder;
import com.sar.taxvault.Model.UserModel;
import com.sar.taxvault.adapters.RecyclerViewAdapterFiles;
import com.sar.taxvault.adapters.RecyclerViewAdapterNotifications;
import com.sar.taxvault.databinding.ActivityRemidersBinding;
import com.sar.taxvault.databinding.ActivityVaultBinding;
import com.sar.taxvault.utils.UIUpdate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RemindersActivity extends BaseActivity {

    ActivityRemidersBinding binding;

    ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRemidersBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();

        setContentView(view);

        setView();
        setListeners();
        getData();
    }

    String getCurrentUserId() {

        return FirebaseAuth.getInstance().getCurrentUser().getUid();

    }

    private void getData() {

        UIUpdate.GetUIUpdate(this).destroy();
        showLoader();

        valueEventListener = FirebaseDatabase.getInstance().getReference("Remainders")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        hideLoader();

                        if (snapshot.getValue() != null)

                            parseSnapshot(snapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                        hideLoader();

                        UIUpdate.GetUIUpdate(RemindersActivity.this).showAlertDialog("Alert", error.getMessage());
                    }
                });
    }

    private void parseSnapshot(DataSnapshot snapshot) {

        List<Remainder> list = new ArrayList<Remainder>();

        for (DataSnapshot remainderSnapshot : snapshot.getChildren()) {

            Remainder remainder = remainderSnapshot.getValue(Remainder.class);

            remainder.setId(snapshot.getKey());

            list.add(remainder);

        }

        setAdapter(list);

    }

    private void setView() {

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        binding.includeView.titleTV.setText("Reminders");
    }

    private void setListeners() {

        binding.includeView.backIV.setOnClickListener(view -> {

            finish();

//            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });
    }

    private void setAdapter(List<Remainder> remainders) {

        binding.notificationsRV.setLayoutManager(new LinearLayoutManager(this));

        RecyclerViewAdapterNotifications adapter = new RecyclerViewAdapterNotifications(this, remainders);

        binding.notificationsRV.setItemAnimator(new DefaultItemAnimator());

        binding.notificationsRV.setAdapter(adapter);

//        binding.openCalendar.setOnClickListener(view -> {
//            new DatePickerDialog(this, date, myCalendar
//                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
//        });
    }


    final Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, monthOfYear);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//            updateLabel();
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();

//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        valueEventListener = null;
    }

    public static void startActivity(Context context) {

        context.startActivity(new Intent(context, RemindersActivity.class));

    }
}