package com.sar.taxvault.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sar.taxvault.activity.DetailedNewsActivity;
import com.sar.taxvault.databinding.LayoutItemsNewsBinding;

import java.util.List;


public class RecyclerViewAdapterNews extends RecyclerView.Adapter<RecyclerViewAdapterNews.ViewHolder>  {

    private static final String TAG = "RCA_Notifications";

    private List<String> list;
    private Context mContext;

    public RecyclerViewAdapterNews(Context mContext, List<String> list) {
        this.list = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerViewAdapterNews.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutItemsNewsBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder : called.");

        viewHolder.binding.mainLayout.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, DetailedNewsActivity.class);
            mContext.startActivity(intent);
            ((Activity) mContext).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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

        LayoutItemsNewsBinding binding;


        public ViewHolder(@NonNull LayoutItemsNewsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }
}
