package com.sar.taxvault.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sar.taxvault.R;
import com.sar.taxvault.activity.Login;
import com.sar.taxvault.adapters.RecyclerViewAdapterNotifications;
import com.sar.taxvault.databinding.FragmentNotificationsBinding;
import com.sar.taxvault.databinding.FragmentProfileBinding;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);

        initRecyclerViewNotifications();
        return binding.getRoot();
    }

    private void initRecyclerViewNotifications(){
        LinearLayoutManager layoutManager5 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.notificationsRV.setLayoutManager(layoutManager5);

        RecyclerViewAdapterNotifications adapter = new RecyclerViewAdapterNotifications(getActivity(),null);
        binding.notificationsRV.setItemAnimator( new DefaultItemAnimator());
        binding.notificationsRV.setAdapter(adapter);
    }

}