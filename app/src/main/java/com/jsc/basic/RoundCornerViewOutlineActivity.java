package com.jsc.basic;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.jsc.basic.databinding.ActivityRoundCornerViewOutlineBinding;

import java.io.IOException;

import jsc.org.lib.basic.inter.RoundCornerViewOutlineProvider;
import jsc.org.lib.basic.utils.ViewOutlineUtils;

public class RoundCornerViewOutlineActivity extends AppCompatActivity {

    ActivityRoundCornerViewOutlineBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRoundCornerViewOutlineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        try {
            binding.ivPic.setImageBitmap(BitmapFactory.decodeStream(getAssets().open("000.jpg")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ViewOutlineUtils.applyRoundCornerPixel(binding.ivPic,
                RoundCornerViewOutlineProvider.TOP_LEFT | RoundCornerViewOutlineProvider.BOTTOM_RIGHT,
                6);
    }
}