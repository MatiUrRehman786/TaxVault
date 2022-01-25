package com.sar.taxvault.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sar.taxvault.Model.Chat;
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

        viewHolder.binding.othersMessageLay.getRoot().setVisibility(View.GONE);

        viewHolder.binding.myMessageLay.getRoot().setVisibility(View.GONE);


        if (chat.messageType.equalsIgnoreCase("incoming")) {

            viewHolder.binding.othersMessageLay.messageTV.setText(chat.message);

            viewHolder.binding.othersMessageLay.getRoot().setVisibility(View.VISIBLE);

            viewHolder.binding.othersMessageLay.messageTV.setVisibility(View.VISIBLE);

            viewHolder.binding.othersMessageLay.timeTV.setText(convertFormat(chat.messageTime));

        } else {

            viewHolder.binding.myMessageLay.messageTV.setText(chat.message);

            viewHolder.binding.myMessageLay.getRoot().setVisibility(View.VISIBLE);

            viewHolder.binding.myMessageLay.messageTV.setVisibility(View.VISIBLE);

            viewHolder.binding.myMessageLay.timeTV.setText(convertFormat(chat.messageTime));

        }

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

    public static String convertFormat(String inputDate) {

        Date date = new Date(Long.parseLong(inputDate));

        SimpleDateFormat dF = new SimpleDateFormat("hh:mm a");

        return dF.format(date);
    }

}
