package com.sar.taxvault.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sar.taxvault.R;
import com.sar.taxvault.adapters.RecyclerViewAdapterFiles;
import com.sar.taxvault.adapters.RecyclerViewAdapterNotifications;
import com.sar.taxvault.databinding.FragmentFilesBinding;
import com.sar.taxvault.databinding.FragmentNotificationsBinding;

public class FilesFragment extends Fragment {

    private FragmentFilesBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFilesBinding.inflate(inflater, container, false);

        initRecyclerViewFiles();
        return binding.getRoot();
    }

    private void initRecyclerViewFiles(){
        LinearLayoutManager layoutManager5 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.filesRV.setLayoutManager(layoutManager5);

        RecyclerViewAdapterFiles adapter = new RecyclerViewAdapterFiles(getActivity(),null, false);
        binding.filesRV.setItemAnimator( new DefaultItemAnimator());
        binding.filesRV.setAdapter(adapter);
    }
}