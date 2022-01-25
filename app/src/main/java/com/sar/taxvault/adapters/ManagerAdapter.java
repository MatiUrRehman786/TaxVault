package com.sar.taxvault.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.sar.taxvault.Model.SelectedManager;
import com.sar.taxvault.databinding.ItemManagerSelectedBinding;
import com.sar.taxvault.interfaces.BusinessIdCallback;

import java.util.List;


public class ManagerAdapter extends RecyclerView.Adapter<ManagerAdapter.ViewHolder> {

    private List<SelectedManager> list;

    private Context mContext;

    public ManagerAdapter(Context mContext, List<SelectedManager> list) {

        this.list = list;

        this.mContext = mContext;


    }

    @NonNull
    @Override
    public ManagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(ItemManagerSelectedBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        SelectedManager user = list.get(i);

        viewHolder.binding.managerNameTV.setText(user.businessName);

        viewHolder.binding.managerCL.setOnClickListener(v -> {

            String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            String managerId = user.id;

            FirebaseDatabase.getInstance().getReference("userBusiness")
                    .child(managerId).child(myId)
                    .removeValue();

            FirebaseDatabase.getInstance().getReference("userBusiness")
                    .child(myId).child(managerId)
                    .removeValue();

        });

    }


    @Override
    public int getItemCount() {

        return list.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemManagerSelectedBinding binding;


        public ViewHolder(@NonNull ItemManagerSelectedBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }

    }

}
