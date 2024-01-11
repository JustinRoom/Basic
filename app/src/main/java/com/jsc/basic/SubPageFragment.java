package com.jsc.basic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jsc.basic.databinding.FragmentSubPageBinding;

import jsc.org.lib.basic.framework.ABaseFragment;

public class SubPageFragment extends ABaseFragment {

    FragmentSubPageBinding binding = null;

    @Override
    public View initContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        binding = FragmentSubPageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onLazyLoad() {

    }
}
