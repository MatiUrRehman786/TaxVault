package com.sar.taxvault.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sar.taxvault.Model.Remainder;
import com.sar.taxvault.databinding.LayoutItemsNotificationsBinding;
import com.sar.taxvault.utils.UIUpdate;

import java.util.List;


public class RecyclerViewAdapterNotifications extends RecyclerView.Adapter<RecyclerViewAdapterNotifications.ViewHolder> {

    private static final String TAG = "RCA_Notifications";

    private List<Remainder> list;

    private Context mContext;

    public RecyclerViewAdapterNotifications(Context mContext, List<Remainder> list) {

        this.list = list;

        this.mContext = mContext;

    }

    @NonNull
    @Override
    public RecyclerViewAdapterNotifications.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(
                LayoutItemsNotificationsBinding.inflate(
                        LayoutInflater.from(
                                parent.getContext()
                        ),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        Remainder remainder = list.get(i);

        viewHolder.binding.notificationTitleTV.setText(remainder.getTitle());

        viewHolder.binding.notificationDetailTV.setText(remainder.getBody());

        viewHolder.binding.getRoot().setOnClickListener(view -> expandRemainder(remainder));

    }

    private void expandRemainder(Remainder remainder) {

        UIUpdate.GetUIUpdate(mContext).destroy();

        UIUpdate.GetUIUpdate(mContext).showAlertDialog(remainder.getTitle(), remainder.getBody());

    }

    @Override
    public int getItemCount() {

        return list.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LayoutItemsNotificationsBinding binding;

        public ViewHolder(@NonNull LayoutItemsNotificationsBinding binding) {

            super(binding.getRoot());

            this.binding = binding;

        }
    }
}
