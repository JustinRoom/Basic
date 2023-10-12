package com.jsc.basic;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.jsc.basic.databinding.ActivitySubBinding;

import jsc.org.lib.basic.utils.ViewOutlineUtils;

public class SubActivity extends AppCompatActivity {

    ActivitySubBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewOutlineUtils.applyHorizontalEllipticOutline(binding.tvSweep);
        binding.tvSweep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cpvProgress.sweepAngle(245, 1_500);
            }
        });
    }
}