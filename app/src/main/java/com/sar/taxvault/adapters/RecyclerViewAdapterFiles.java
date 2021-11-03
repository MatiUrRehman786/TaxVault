package com.sar.taxvault.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sar.taxvault.R;
import com.sar.taxvault.databinding.LayoutItemsFilesBinding;
import com.sar.taxvault.databinding.LayoutItemsNotificationsBinding;

import java.util.List;


public class RecyclerViewAdapterFiles extends RecyclerView.Adapter<RecyclerViewAdapterFiles.ViewHolder>  {

    private static final String TAG = "RCA_Notifications";

    private List<String> list;
    private Context mContext;
    boolean isFromVaultClass;

    public RecyclerViewAdapterFiles(Context mContext, List<String> list, boolean isFromVaultClass) {
        this.list = list;
        this.mContext = mContext;
        this.isFromVaultClass = isFromVaultClass;
    }

    @NonNull
    @Override
    public RecyclerViewAdapterFiles.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutItemsFilesBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder : called.");

        if(isFromVaultClass){
            viewHolder.binding.moreOptionsIV.setImageResource(R.drawable.vault);
        }
        else{
            viewHolder.binding.moreOptionsIV.setImageResource(R.drawable.three_dots);
        }
        viewHolder.binding.moreOptionsIV.setOnClickListener(view -> {
            Dialog dialog = new Dialog(mContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            dialog.setContentView(R.layout.dialog_file_options);

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });
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

        LayoutItemsFilesBinding binding;


        public ViewHolder(@NonNull LayoutItemsFilesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }
}
