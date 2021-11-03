package com.sar.taxvault.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.sar.taxvault.R;
import com.sar.taxvault.adapters.RecyclerViewAdapterNews;
import com.sar.taxvault.databinding.ActivityMainBinding;
import com.sar.taxvault.databinding.ActivityNewsBinding;

public class NewsActivity extends AppCompatActivity {

    ActivityNewsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setView();
        setListeners();
        initRecyclerViewNews();
    }

    private void setView() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding.includeView.titleTV.setText("News");
    }

    private void setListeners() {
        binding.includeView.backIV.setOnClickListener(view -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    private void initRecyclerViewNews(){
        LinearLayoutManager layoutManager5 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.newsRV.setLayoutManager(layoutManager5);

        RecyclerViewAdapterNews adapter = new RecyclerViewAdapterNews(this,null);
        binding.newsRV.setItemAnimator( new DefaultItemAnimator());
        binding.newsRV.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}