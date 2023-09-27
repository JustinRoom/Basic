package com.jsc.basic;

import android.view.View;
import android.widget.Toast;

import com.jsc.basic.databinding.ActivityMainBinding;

import jsc.org.lib.basic.framework.ABaseActivity;
import jsc.org.lib.basic.widget.imitate.ImitateLoadingDialog;

public class MainActivity extends ABaseActivity {

    ActivityMainBinding binding = null;

    @Override
    public View initContentView() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        binding.tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog(false);
            }
        });
        binding.btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "clicked button", Toast.LENGTH_SHORT).show();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onLazyLoad() {
        showLoadingDialog(true);
    }

    private void showLoadingDialog(boolean overContent) {
        ImitateLoadingDialog dialog = new ImitateLoadingDialog(this);
        dialog.setCancelable(true);
        dialog.setCancelableTouchOutside(false);
        dialog.show(overContent);
    }

}