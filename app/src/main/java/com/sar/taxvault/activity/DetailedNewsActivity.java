package com.sar.taxvault.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.sar.taxvault.Model.NewsModel;
import com.sar.taxvault.adapters.RecyclerViewAdapterNews;
import com.sar.taxvault.databinding.ActivityDetailedNewsBinding;
import com.sar.taxvault.databinding.ActivityNewsBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DetailedNewsActivity extends AppCompatActivity {

    ActivityDetailedNewsBinding binding;
    
    NewsModel newsModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        binding = ActivityDetailedNewsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        
        getDataFromIntent();
        
        setView();
        
        setListeners();
    }


    private void getDataFromIntent() {

        Intent intent=getIntent();
        
        newsModel= (NewsModel) intent.getSerializableExtra("news");

        setNewsData();
        
    }

    private void setView() {
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            
        }
        
    }
    
    public  void setNewsData(){

        Glide.with(this).load(newsModel.getImageUrl()).into(binding.newsIV);

        binding.newsTitleTV.setText(newsModel.getTitle());

        binding.securityCodeTV.setText(newsModel.getSecurityCode());

        binding.timeTV.setText(getTime(String.valueOf(newsModel.getTime())));

        binding.descriptionTV.setText(newsModel.getDescription());
    }

    public String getTime(String timeStamp){

        long time = Long.parseLong(timeStamp);

        SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd,yyyy HH:mm");

        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(time);

        return formatter.format(calendar.getTime());

    }


    private void setListeners() {
        
        binding.includeView.backIV.setOnClickListener(view -> {
            
            finish();

//            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            
        });
        
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        
        finish();
        
//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        
    }
}