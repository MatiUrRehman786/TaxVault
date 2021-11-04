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

import com.sar.taxvault.Model.Document;
import com.sar.taxvault.R;
import com.sar.taxvault.activity.VaultActivity;
import com.sar.taxvault.databinding.LayoutItemsFilesBinding;
import com.sar.taxvault.databinding.LayoutItemsNotificationsBinding;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;


public class RecyclerViewAdapterFiles extends RecyclerView.Adapter<RecyclerViewAdapterFiles.ViewHolder>  {

    private static final String TAG = "RCA_Notifications";

    private List<Document> list;

    private Context mContext;

    EditFileCallback callback;

    public void setCallback(EditFileCallback callback) {
        this.callback = callback;
    }

    public RecyclerViewAdapterFiles(Context context, List<Document> documents) {
        this.list = documents;
        this.mContext = context;
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

        Document document = list.get(i);

        viewHolder.binding.creationDateTV.setText(document.getTime());
        viewHolder.binding.documentTitleTV.setText(document.getName());

        double sizeInMB = new Long(document.getSize()).doubleValue()/1048576;

        double size = round(sizeInMB, 3);

        viewHolder.binding.fileSizeTV.setText("Total "+size+ " MB");
        viewHolder.binding.moreOptionsIV.setImageResource(R.drawable.three_dots);

        viewHolder.binding.moreOptionsIV.setOnClickListener(view -> {
            if (callback != null)
                callback.onEditPressed(document);
        });
    }

    public double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        LayoutItemsFilesBinding binding;

        public ViewHolder(@NonNull LayoutItemsFilesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }

    public interface EditFileCallback {

        void onEditPressed(Document document);
    }
}
