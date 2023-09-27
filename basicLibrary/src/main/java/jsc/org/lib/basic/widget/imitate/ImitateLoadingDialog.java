package jsc.org.lib.basic.widget.imitate;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import jsc.org.lib.basic.databinding.BaseDialogLoadingBinding;
import jsc.org.lib.basic.utils.ViewOutlineUtils;
import jsc.org.lib.basic.utils.ViewUtils;

public final class ImitateLoadingDialog extends BaseImitateDialog {

    private BaseDialogLoadingBinding binding = null;

    public ImitateLoadingDialog(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    public void initContentView(@NonNull LayoutInflater inflater, FrameLayout root) {
        binding = BaseDialogLoadingBinding.inflate(inflater, root, true);
        ViewUtils.disableCrossClick(binding.getRoot());
        DisplayMetrics metrics = root.getResources().getDisplayMetrics();
        int size = Math.min(metrics.widthPixels, metrics.heightPixels) / 4;
        ViewGroup.LayoutParams params = binding.getRoot().getLayoutParams();
        params.width = size;
        params.height = size;
        ViewOutlineUtils.applyRoundOutline(binding.getRoot(), 8);
    }

    @Override
    public void show() {
        super.show();
        runDefaultInAnim(binding.getRoot());
    }
}