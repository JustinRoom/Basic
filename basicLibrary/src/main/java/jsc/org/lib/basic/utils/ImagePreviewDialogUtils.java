package jsc.org.lib.basic.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;

public final class ImagePreviewDialogUtils {

    public static void showImagePreviewDialog(Context context, final String path) {
        ImageView view = new ImageView(context);
        view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        view.setBackgroundColor(Color.BLACK);
        ViewOutlineUtils.applyRoundOutline2(view, 12);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        Point size;
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            size = new Point(metrics.widthPixels / 2, metrics.heightPixels * 4 / 5);
        } else {
            size = new Point(metrics.widthPixels * 4 / 5, metrics.heightPixels * 3 / 5);
        }
        view.setLayoutParams(new ViewGroup.LayoutParams(size.x, size.y));
        Dialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .create();
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.CENTER;
            params.width = size.x;
            params.height = size.y;
            window.setAttributes(params);
        }
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setImageBitmap(BitmapFactory.decodeFile(path));
            }
        }, 50);
    }
}
