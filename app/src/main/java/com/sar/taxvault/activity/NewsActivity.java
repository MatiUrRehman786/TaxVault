package com.sar.taxvault.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sar.taxvault.Model.NewsModel;
import com.sar.taxvault.R;
import com.sar.taxvault.adapters.RecyclerViewAdapterNews;
import com.sar.taxvault.databinding.ActivityMainBinding;
import com.sar.taxvault.databinding.ActivityNewsBinding;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends BaseActivity {

    ActivityNewsBinding binding;

    private List<NewsModel> newsList;

    private FirebaseAuth mAuth;

    public FirebaseDatabase rootNode;

    public DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNewsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        initFireBase();

        setView();

        setListeners();

        getNews();

    }

    private void setView() {

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        binding.includeView.titleTV.setText("News");

    }

    private void initFireBase() {

        rootNode = FirebaseDatabase.getInstance();

        mDatabase = rootNode.getReference("News");

        mAuth = FirebaseAuth.getInstance();

    }


    private void setListeners() {

        binding.includeView.backIV.setOnClickListener(view -> {
            finish();
//            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

    }

    private void setRecyclerViewNews() {

        LinearLayoutManager layoutManager5 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.newsRV.setLayoutManager(layoutManager5);

        RecyclerViewAdapterNews adapter = new RecyclerViewAdapterNews(this, newsList);

        binding.newsRV.setItemAnimator(new DefaultItemAnimator());

        binding.newsRV.setAdapter(adapter);

    }

    private void getNews() {

        if (isOnline()) {

            newsList = new ArrayList<>();


            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    newsList.clear();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        NewsModel newsModel = ds.getValue(NewsModel.class);

                        String newsId = ds.getKey();

                        newsModel.setId(newsId);

                        newsList.add(newsModel);

                    }

                    setRecyclerViewNews();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    System.out.println("GetNews" + databaseError.getCode());

                }
            });
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();

//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }
}