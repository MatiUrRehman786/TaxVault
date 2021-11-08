package com.sar.taxvault.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sar.taxvault.Model.NewsModel;
import com.sar.taxvault.activity.DetailedNewsActivity;
import com.sar.taxvault.databinding.LayoutItemsNewsBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class RecyclerViewAdapterNews extends RecyclerView.Adapter<RecyclerViewAdapterNews.ViewHolder>  {

    private List<NewsModel> list;

    private Context mContext;

    public RecyclerViewAdapterNews(Context mContext, List<NewsModel> list) {

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

        NewsModel newsModel=list.get(i);

        Glide.with(mContext).load(newsModel.getImageUrl()).into(viewHolder.binding.newsIV);

        viewHolder.binding.newsTitleTV.setText(newsModel.getTitle());

        viewHolder.binding.securityCodeTV.setText(newsModel.getSecurityCode());

        viewHolder.binding.timeTV.setText(getTime(String.valueOf(newsModel.getTime())));

        viewHolder.binding.descriptionTV.setText(newsModel.getDescription());

        viewHolder.binding.mainLayout.setOnClickListener(view -> startActivity(newsModel));

    }

    public String getTime(String timeStamp){

        long time = Long.parseLong(timeStamp);

        SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd,yyyy HH:mm");

        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(time);

        return formatter.format(calendar.getTime());

    }

    public void startActivity(NewsModel newsModel){

        Intent intent = new Intent(mContext, DetailedNewsActivity.class);

        intent.putExtra("news",newsModel);

        mContext.startActivity(intent);

        ((Activity) mContext).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

    @Override
    public int getItemCount() {

        return list.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        LayoutItemsNewsBinding binding;


        public ViewHolder(@NonNull LayoutItemsNewsBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }

    }

}
