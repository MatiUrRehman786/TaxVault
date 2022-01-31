package com.sar.taxvault.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sar.taxvault.activity.VaultActivity;
import com.sar.taxvault.databinding.ItemCategoryBinding;

import java.util.List;


public class RecyclerViewAdapterCategories extends RecyclerView.Adapter<RecyclerViewAdapterCategories.ViewHolder> {

    private List<String> list;

    private Context mContext;

    String year;

    OnMoveHereClickedListener listener;

    Boolean showMoveHere;

    public RecyclerViewAdapterCategories(Context mContext, List<String> list, String year, Boolean showMoveHere) {
        this.list = list;
        this.mContext = mContext;
        this.year = year;
        this.showMoveHere = showMoveHere;
    }

    public void setListener(OnMoveHereClickedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerViewAdapterCategories.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemCategoryBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        String name = list.get(i);

        if (name != null) {

            viewHolder.binding.titleTV.setText(name);

            if (!showMoveHere)

                viewHolder.binding.getRoot().setOnClickListener(v -> moveToVaultsList(name));

            else {

                viewHolder.binding.moveHereBt.setVisibility(View.VISIBLE);

                viewHolder.binding.moveHereBt.setOnClickListener(v -> {

                    if(listener != null) {

                        listener.onCategorySelected(name);

                    }

                });
            }
        }
    }

    private void moveToVaultsList(String name) {

        Intent i = new Intent(mContext, VaultActivity.class);


        i.putExtra("year", year);
        i.putExtra("category", name);

        mContext.startActivity(i);
    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    public void setYear(String year) {
        this.year = year;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemCategoryBinding binding;

        public ViewHolder(@NonNull ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }

    public interface OnMoveHereClickedListener {

        public void onCategorySelected(String category);

    }
}
