package com.sar.taxvault.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sar.taxvault.Model.Chat;
import com.sar.taxvault.activity.ChatActivity;
import com.sar.taxvault.activity.MessageDetailsView;
import com.sar.taxvault.databinding.MessagesItemLayBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    List<Chat> list;

    Context mContext;

    public MessagesAdapter(Context mContext, List<Chat> list) {

        this.list = list;

        this.mContext = mContext;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(MessagesItemLayBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        final Chat chat = list.get(i);

        viewHolder.binding.nameTV.setText("Me");

        if (chat.messageType.equalsIgnoreCase("incoming"))

            viewHolder.binding.nameTV.setText(chat.fromUserName);

        viewHolder.binding.timeTV.setText(convertFormat(chat.messageTime));

        if (chat.message != null)

            viewHolder.binding.msgTV.setText(chat.message);

        if (!chat.type.equalsIgnoreCase("attach"))

            viewHolder.binding.attachmentIV.setVisibility(View.GONE);

        viewHolder.binding.getRoot().setOnClickListener(v -> {

            mContext.startActivity(new Intent(mContext, MessageDetailsView.class)

            .putExtra("chat",chat));

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        MessagesItemLayBinding binding;

        public ViewHolder(@NonNull MessagesItemLayBinding binding) {

            super(binding.getRoot());

            this.binding = binding;
        }
    }

    public static String convertFormat(Long ts) {

        Date date = new Date();

        date.setTime(ts);

        SimpleDateFormat dF = new SimpleDateFormat("hh:mm a");

        return dF.format(date);
    }

}
