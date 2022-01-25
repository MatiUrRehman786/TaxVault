package com.sar.taxvault.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.sar.taxvault.Model.Document;
import com.sar.taxvault.databinding.ActivityImagePreViewBinding;
import com.sar.taxvault.databinding.ActivityPdfViewBinding;
import com.sar.taxvault.utils.UIUpdate;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImagePreview extends BaseActivity  {

    ActivityImagePreViewBinding binding;

    Document document;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityImagePreViewBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();

        setContentView(view);

        setView();

        setUpPDF();
    }

    private void setUpPDF() {

        Glide.with(this).load(document.getUrl()).apply(new RequestOptions().centerCrop()).into(binding.picIV);

    }

    private void setView() {

        document = getIntent().getParcelableExtra("document");

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        binding.includeView.titleTV.setText("Preview");

        binding.includeView.backIV.setOnClickListener(v -> onBackPressed());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();

    }

}