package com.jsc.basic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.jsc.basic.databinding.ViewModuleSubPageBinding;

import jsc.org.lib.basic.framework.ABaseViewModule;

public class SubViewModule extends ABaseViewModule<MainActivity> {

    ViewModuleSubPageBinding binding = null;

    @Override
    public View bindView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        binding = ViewModuleSubPageBinding.inflate(inflater, parent, true);
        return binding.getRoot();
    }
}
