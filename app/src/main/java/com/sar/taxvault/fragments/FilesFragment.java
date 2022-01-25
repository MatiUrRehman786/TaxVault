package com.sar.taxvault.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sar.taxvault.databinding.FragmentFilesBinding;

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
    }
}