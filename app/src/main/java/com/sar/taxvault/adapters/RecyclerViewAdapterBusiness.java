package com.sar.taxvault.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sar.taxvault.Model.UserModel;
import com.sar.taxvault.Model.UserModel;
import com.sar.taxvault.activity.DetailedNewsActivity;
import com.sar.taxvault.databinding.ItemDesignUserBinding;
import com.sar.taxvault.databinding.ItemDesignUserBinding;
import com.sar.taxvault.interfaces.BusinessIdCallback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class RecyclerViewAdapterBusiness extends RecyclerView.Adapter<RecyclerViewAdapterBusiness.ViewHolder> {

    private List<UserModel> list;

    private Context mContext;

    BusinessIdCallback businessIdCallback;

    public RecyclerViewAdapterBusiness(Context mContext, List<UserModel> list, BusinessIdCallback callback) {

        this.list = list;

        this.mContext = mContext;

        this.businessIdCallback = callback;

    }

    @NonNull
    @Override
    public RecyclerViewAdapterBusiness.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(ItemDesignUserBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        UserModel user = list.get(i);

        viewHolder.binding.nameTV.setText(user.getFirstName() + " " + user.getLastName());

        viewHolder.binding.mainCL.setOnClickListener(v->{
            businessIdCallback.onItemClick(user.getUserId(),user.getFirstName()+" "+user.getLastName());
        });

    }


    @Override
    public int getItemCount() {

        return list.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemDesignUserBinding binding;


        public ViewHolder(@NonNull ItemDesignUserBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }

    }

}
