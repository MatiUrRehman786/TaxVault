package com.sar.taxvault.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sar.taxvault.databinding.LayoutItemsNotificationsBinding;

import java.util.List;


public class RecyclerViewAdapterNotifications extends RecyclerView.Adapter<RecyclerViewAdapterNotifications.ViewHolder>  {

    private static final String TAG = "RCA_Notifications";

    private List<String> list;
    private Context mContext;

    public RecyclerViewAdapterNotifications(Context mContext, List<String> list) {
        this.list = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerViewAdapterNotifications.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutItemsNotificationsBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder : called.");

    }

    @Override
    public int getItemCount() {
        int arr = 0;
        try{
            if(list.size()==0){
                arr = 0;
            }
            else{

                arr=list.size();
            }
        }catch (Exception e){
        }

        return 8;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        LayoutItemsNotificationsBinding binding;


        public ViewHolder(@NonNull LayoutItemsNotificationsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }
}
