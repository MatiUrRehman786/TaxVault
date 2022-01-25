package com.sar.taxvault.activity;

import android.os.Bundle;

import androidx.room.Database;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sar.taxvault.Model.Chat;
import com.sar.taxvault.Model.UserModel;
import com.sar.taxvault.databinding.ActivitySendMessageBinding;
import com.sar.taxvault.utils.UIUpdate;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Objects;

public class ActivitySendMessage extends BaseActivity {

    ActivitySendMessageBinding binding;

    UserModel user;

    UserModel manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivitySendMessageBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        getMyProfile();

        setListeners();
    }

    private void setListeners() {

        binding.sendBt.setOnClickListener(v -> validateAndSendMsg());
        binding.includeView.menuIV.setOnClickListener(v -> finish());
        binding.includeView.titleTV.setText("Send Message");

    }

    private void validateAndSendMsg() {

        if (validate()) {

            Chat chat = new Chat();

            chat.isRead = false;
            chat.message = binding.messageET.getText().toString();
            chat.messageTime = String.valueOf(new Date().getTime());
            chat.messageType = "outgoing";
            chat.type = "text";

            DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chat");

            String key = chatRef.push().getKey();

            chatRef.child(user.getUserId()).child(manager.getUserId()).child(key).setValue(chat);
            chatRef.child(manager.getUserId()).child(user.getUserId()).child(key).setValue(chat);

            finish();

        }

    }

    private boolean validate() {

        if (manager == null) {

            showErrorAlert("Please select manager first");

            return false;
        }

        if (user == null) {

            showErrorAlert("Please check your internet connection and try again");

            return false;
        }

        if (user.businessId == null || user.businessId.equalsIgnoreCase("null") || user.businessId.isEmpty()) {

            showErrorAlert("Please select manager first");

            return false;

        }

        if (user.businessStatus == null || user.businessStatus.equalsIgnoreCase("pending") || user.businessStatus.isEmpty()) {

            showErrorAlert("Waiting for business manager approval. Once manager approves you will be able to send messages to him.");

            return false;
        }

        if (binding.messageET.getText().toString().isEmpty()) {


            showErrorAlert("Message Required");

            return false;
        }

        return true;

    }

    private void getMyProfile() {

        showLoader();

        FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot snapshot) {

                        hideLoader();

                        if (snapshot.getValue() != null) {

                            user = snapshot.getValue(UserModel.class);

                            if (user != null) {

                                user.setUserId(snapshot.getKey());

                            }

                            setDetails();

                            getManager();
                        }
                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError error) {

                        hideLoader();

                        UIUpdate.GetUIUpdate(ActivitySendMessage.this).showAlertDialog("Alert", error.getMessage());
                    }
                });
    }

    private void getManager() {

        showLoader();

        FirebaseDatabase.getInstance().getReference("User").child(Objects.requireNonNull(user.businessId))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot snapshot) {

                        hideLoader();

                        if (snapshot.getValue() != null) {

                            manager = snapshot.getValue(UserModel.class);

                            if (manager != null) {

                                manager.setUserId(snapshot.getKey());

                            }

                            setDetails();
                        }
                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError error) {

                        hideLoader();

                        UIUpdate.GetUIUpdate(ActivitySendMessage.this).showAlertDialog("Alert", error.getMessage());
                    }
                });

    }

    private void setDetails() {

        if (user != null) {

            binding.fromTV.setText(user.getFirstName() + " " + user.getLastName());

        }

        if (manager != null) {

            binding.toTV.setText(manager.getFirstName() + " " + manager.getLastName());

        }
    }
}
