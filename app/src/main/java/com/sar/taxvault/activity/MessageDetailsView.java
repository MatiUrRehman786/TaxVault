package com.sar.taxvault.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sar.taxvault.Model.Chat;
import com.sar.taxvault.Model.SelectedManager;
import com.sar.taxvault.Model.UserModel;
import com.sar.taxvault.databinding.ActivityMessageDetailBinding;
import com.sar.taxvault.databinding.ActivitySendMessageBinding;
import com.sar.taxvault.utils.UIUpdate;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageDetailsView extends BaseActivity {

    ActivityMessageDetailBinding binding;

    Chat msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityMessageDetailBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        getExtrasFromIntent();

        setData();

        setListeners();
    }

    private void setData() {

        if (msg != null) {

            binding.fromTV.setText(msg.fromUserName);

            binding.toTV.setText(msg.toUserName);

            if (msg.message != null)

                binding.messageET.setText(msg.message);

            if(msg.fileName != null) {

                binding.attachmentTV.setText(msg.fileName);

            } else {

                binding.attachmentTV.setVisibility(View.GONE);

                binding.attachmentIV.setVisibility(View.GONE);

            }
        }

        if (msg.messageType.equalsIgnoreCase("incoming"))

            binding.sendBt.setVisibility(View.VISIBLE);

        else

            binding.sendBt.setVisibility(View.GONE);

    }

    private void getExtrasFromIntent() {

        Bundle b = getIntent().getExtras();

        if (b != null) {

            if (b.getParcelable("chat") != null) {

                msg = b.getParcelable("chat");

            }

        }

    }

    private void setListeners() {

        binding.sendBt.setOnClickListener(v -> validateAndSendMsg());

        binding.attachmentTV.setOnClickListener(v -> openAttachment());

        binding.includeView.menuIV.setOnClickListener(v -> finish());

        binding.includeView.titleTV.setText("");

        binding.includeView.menuIV.setVisibility(View.VISIBLE);

    }

    private void openAttachment() {

        if(msg.fileUrl != null) {

            String url = msg.fileUrl;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);

        }
    }


    private void validateAndSendMsg() {

        startActivity(new Intent(MessageDetailsView.this, ActivitySendMessage.class)
        .putExtra("chat",msg));


    }

}
