package com.jsc.basic;

import android.graphics.BitmapFactory;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.jsc.basic.databinding.ActivitySkinColorDetectBinding;
import com.jsc.basic.entities.SkinColorItem;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import jsc.org.lib.basic.framework.ABaseActivity;
import jsc.org.lib.basic.object.LocalFileManager;
import jsc.org.lib.basic.utils.YCrCbSkinColorDetectUtils;
import jsc.org.lib.basic.widget.imitate.ImitateLoadingDialogUtils;

public class SkinColorDetectActivity extends ABaseActivity {

    ActivitySkinColorDetectBinding binding = null;
    SkinColorDetectAdapter adapter = null;
    final List<SkinColorItem> list = new ArrayList<>();

    @Override
    public View initContentView() {
        binding = ActivitySkinColorDetectBinding.inflate(getLayoutInflater());
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new SkinColorDetectAdapter();
        binding.recyclerView.setAdapter(adapter);
        return binding.getRoot();
    }

    @Override
    public void onLazyLoad() {
        ImitateLoadingDialogUtils.show(this, "_loading");
        new Thread(new Runnable() {
            @Override
            public void run() {
                list.clear();
                File dir = LocalFileManager.getInstance().getExternalFilesDir("skin3");
                File[] files = dir.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return pathname.isFile();
                    }
                });
                if (files != null) {
                    for (File f : files) {
                        SkinColorItem item = new SkinColorItem();
                        item.file = f;
                        item.bitmap = YCrCbSkinColorDetectUtils.yCrCbSkinDetect(BitmapFactory.decodeFile(f.getPath()));
                        list.add(item);
                    }
                }
                getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setItems(list);
                        list.clear();
                        ImitateLoadingDialogUtils.dismiss("_loading");
                    }
                });
            }
        }).start();
    }
}
