package com.jsc.basic;

import android.view.View;

import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jsc.basic.databinding.ActivityExifInterfaceBinding;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Locale;

import jsc.org.lib.basic.framework.ABaseActivity;
import jsc.org.lib.basic.object.LocalFileManager;
import jsc.org.lib.basic.utils.MyFileUtils;
import jsc.org.lib.basic.utils.ViewOutlineUtils;

public class ExifInterfaceActivity extends ABaseActivity {

    ActivityExifInterfaceBinding binding = null;
    ExifAdapter adapter = null;

    @Override
    public View initContentView() {
        registerPickImageLauncher();
        binding = ActivityExifInterfaceBinding.inflate(getLayoutInflater());
        adapter = new ExifAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        ViewOutlineUtils.applyHorizontalEllipticOutline(binding.btnEncrypt);
        binding.btnEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onLazyLoad() {
        File dir = LocalFileManager.getInstance().getExternalFilesDir("images");
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String lowerName = pathname.getName().toLowerCase(Locale.US);
                return pathname.isFile() && (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg"));
            }
        });
        adapter.addFiles(files);
    }

    @Override
    public void onPickImageLaunchBack(int resultCode, String path) {
        super.onPickImageLaunchBack(resultCode, path);
        File file = new File(path);
        if (!file.exists()) return;
        File to = new File(LocalFileManager.getInstance().getExternalFilesDir("images"), file.getName());
        MyFileUtils.nioTransferCopy(file, to);

        try {
            ExifInterface face = new ExifInterface(to);
            //ExifInterface.TAG_IMAGE_UNIQUE_ID为可见属性，不可编辑，稳定
            face.setAttribute(ExifInterface.TAG_IMAGE_UNIQUE_ID, "gxzzzzqjyksyA");
            //ExifInterface.TAG_BODY_SERIAL_NUMBER为隐藏属性，稳定
            face.setAttribute(ExifInterface.TAG_BODY_SERIAL_NUMBER, String.valueOf(System.currentTimeMillis()));
            face.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        adapter.addFile(to);
    }
}
