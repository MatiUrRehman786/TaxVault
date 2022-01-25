package com.sar.taxvault.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sar.taxvault.Model.Chat;
import com.sar.taxvault.Model.UserModel;
import com.sar.taxvault.adapters.MessagesAdapter;
import com.sar.taxvault.databinding.ActivityChatBinding;
import com.sar.taxvault.databinding.ActivitySendMessageBinding;
import com.sar.taxvault.utils.UIUpdate;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ChatActivity extends BaseActivity {

    ActivityChatBinding binding;

    UserModel user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityChatBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.titleTV.setText("Messages");

        setListeners();
    }

    @Override
    protected void onResume() {

        super.onResume();

        getMyProfile();

    }

    private void setListeners() {

        binding.backIV.setOnClickListener(v -> finish());

        binding.addBt.setOnClickListener(v -> moveToSendMessage());
    }

    private void moveToSendMessage() {

        startActivity(new Intent(ChatActivity.this, ActivitySendMessage.class));

    }

    private void getMyProfile() {

        showLoader();

        FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot snapshot) {

                        hideLoader();

                        if (snapshot.getValue() != null) {

                            user = snapshot.getValue(UserModel.class);

                            if (user != null) {

                                user.setUserId(snapshot.getKey());

                                if(user.businessId != null && !user.businessId.equalsIgnoreCase("null") && !user.businessId.isEmpty()) {

                                    getMessages();

                                }

                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError error) {

                        hideLoader();

                        UIUpdate.GetUIUpdate(ChatActivity.this).showAlertDialog("Alert", error.getMessage());
                    }
                });
    }

    private void getMessages() {

        FirebaseDatabase.getInstance().getReference("chat")
                .child(user.getUserId())
                .child(user.getBusinessId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                        List<Chat> messages = new ArrayList<Chat>();

                        if(snapshot.getValue() != null) {

                            for(DataSnapshot childSnapshot: snapshot.getChildren()) {

                                Chat chat = childSnapshot.getValue(Chat.class);

                                if(chat != null) {

                                    chat.id = childSnapshot.getKey();

                                }

                                messages.add(chat);

                            }


                        }

                        binding.rv.setLayoutManager(new LinearLayoutManager(ChatActivity.this));

                        binding.rv.setAdapter(new MessagesAdapter(ChatActivity.this, messages));

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

    }
}
