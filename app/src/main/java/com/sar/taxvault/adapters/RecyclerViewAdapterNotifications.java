package com.sar.taxvault.adapters;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sar.taxvault.Model.Remainder;
import com.sar.taxvault.databinding.LayoutItemsNotificationsBinding;
import com.sar.taxvault.utils.UIUpdate;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class RecyclerViewAdapterNotifications extends RecyclerView.Adapter<RecyclerViewAdapterNotifications.ViewHolder> {

    private static final String TAG = "RCA_Notifications";

    private List<Remainder> list;

    private Activity mContext;

    public RecyclerViewAdapterNotifications(Activity mContext, List<Remainder> list) {

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

        final Calendar newCalendar = Calendar.getInstance();

        Date date = new Date();
        date.setTime(remainder.getTime() * 1000L);
        Log.d(TAG, "expandRemainder: +"+date.toString());

        newCalendar.setTime(date);
        Log.d(TAG, "expandRemainder: +"+newCalendar.toString());
        Log.d(TAG, "expandRemainder: +"+newCalendar.getTime().getMonth());

        final DatePickerDialog  StartTime = new DatePickerDialog(mContext, (view, year, monthOfYear, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        StartTime.getDatePicker().setMaxDate(remainder.getReminderFiringTime()*1000L);
        StartTime.getDatePicker().setMinDate(remainder.getReminderFiringTime()*1000L);
//        btn_checkin.setOnClickListener(new View.OnClickListener() {
//            @Override   public void onClick(View v) {
                StartTime.show();
//            });
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
