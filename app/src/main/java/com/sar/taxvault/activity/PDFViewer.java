package com.sar.taxvault.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.sar.taxvault.Model.Document;
import com.sar.taxvault.databinding.ActivityPdfViewBinding;
import com.sar.taxvault.utils.UIUpdate;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PDFViewer extends BaseActivity implements OnPageChangeListener, OnLoadCompleteListener, OnPageErrorListener {

    ActivityPdfViewBinding binding;

    Document document;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPdfViewBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();

        setContentView(view);

        setView();

        setUpPDF();
    }

    private void setUpPDF() {
        UIUpdate.GetUIUpdate(this).destroy();

        showLoader();
        try {

            new RetrievePdfStream(PDFViewer.this).execute(document.getUrl());

        } catch (Exception e) {

            hideLoader();

            Toast.makeText(this, "Failed to load Url :" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setView() {

        document = getIntent().getParcelableExtra("document");

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        binding.includeView.titleTV.setText("PDF");

        binding.includeView.backIV.setOnClickListener(v -> onBackPressed());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();

    }

    public void loadPDF(InputStream inputStream) {

        binding.pdfView.fromStream(inputStream).defaultPage(0)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .onPageError(this)
                .load();
    }

    @Override
    public void onPageChanged(int page, int pageCount) {

    }

    @Override
    public void loadComplete(int nbPages) {

        hideLoader();

    }

    @Override
    public void onPageError(int page, Throwable t) {

        hideLoader();

        if (t != null)

            showErrorAlert(t.getLocalizedMessage());
    }

    class RetrievePdfStream extends AsyncTask<String, Void, InputStream> {

        PDFViewer instance;

        public RetrievePdfStream(PDFViewer pdfViewer) {

            this.instance = pdfViewer;
        }

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;

            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());

                }
            } catch (IOException e) {

                hideLoader();

                instance.showErrorAlert("Invalid PDF");

                return null;



            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {

            instance.loadPDF(inputStream);

        }

    }

}