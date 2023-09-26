package jsc.org.lib.basic.widget.imitate;

import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
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
    public void initContentView(@NonNull AppCompatActivity activity) {
        binding = BaseDialogLoadingBinding.inflate(activity.getLayoutInflater(), activity.findViewById(android.R.id.content), false);
        ViewUtils.disableCrossClick(binding.getRoot());
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        int size = Math.min(metrics.widthPixels, metrics.heightPixels) / 4;
        ViewGroup.LayoutParams params = binding.getRoot().getLayoutParams();
        params.width = size;
        params.height = size;
        ViewOutlineUtils.applyRoundOutline(binding.getRoot(), 8);
    }

    @Override
    public void show() {
        super.show();
        ScaleAnimation inAnim = new ScaleAnimation(.2f, 1.0f, .2f, 1.0f, .5f, .5f);
        inAnim.setDuration(250);
        binding.getRoot().setAnimation(inAnim);
        inAnim.start();
    }
}